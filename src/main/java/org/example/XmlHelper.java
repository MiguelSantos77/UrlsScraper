package org.example;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.List;
import org.example.classes.Link;
import org.jdom2.Attribute;
import org.jdom2.Document;
import org.jdom2.Element;
import org.jdom2.JDOMException;
import org.jdom2.input.SAXBuilder;
import org.jdom2.output.Format;
import org.jdom2.output.XMLOutputter;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;


public class XmlHelper {

    public static void main(String[] args) {
    }

    public static void createFileIfNotExists(String XML_FILE_PATH, String mainElement){
        File file = new File(XML_FILE_PATH);
        if (!file.exists()) {
            try {
                Document document = new Document(new Element(mainElement));
                XMLOutputter xmlOutputter = new XMLOutputter(Format.getPrettyFormat());
                xmlOutputter.output(document, new FileWriter(XML_FILE_PATH));
                if (file.createNewFile()) {
                    System.out.println("File: " + XML_FILE_PATH + " criado");
                } else {
                    System.out.println("Erro ao criar: " + XML_FILE_PATH);
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else
            System.out.println("File: " + XML_FILE_PATH + " já existe!");
    }
    public static  class Infos{
        private static final String XML_FILE_PATH = "info.xml";

        public Infos(){
            createFileIfNotExists(XML_FILE_PATH, "infos");
        }

        public void save(String type, int linkId, String value){
            try {
                Document document = new SAXBuilder().build(new File(XML_FILE_PATH));
                List<Element> emailElements = document.getRootElement().getChildren(type);
                Boolean exists= false;
                for (Element emailElement : emailElements) {

                    if (emailElement.getValue().equals(value)){
                        exists = true;
                        String existingLinkIds = emailElement.getAttributeValue("linkIds");

                        if (existingLinkIds == null || existingLinkIds.isEmpty()) {
                            emailElement.setAttribute("linkIds", String.valueOf(linkId));
                        } else {

                            String[] existingLinkIdsArray = existingLinkIds.split(",");

                            boolean linkIdExists = false;
                            for (String id : existingLinkIdsArray) {
                                if (id.trim().equals(String.valueOf(linkId))) {
                                    linkIdExists = true;
                                    break;
                                }
                            }
                            if (!linkIdExists) {
                                emailElement.setAttribute("linkIds", existingLinkIds + "," + linkId);
                            }
                        }
                    }

                }
                if (!exists){

                    Element element = new Element(type);
                    element.setAttribute("linkIds", String.valueOf(linkId));


                    element.addContent(value);
                    document.getRootElement().addContent(element);
                }

                XMLOutputter xmlOutputter = new XMLOutputter(Format.getPrettyFormat());
                xmlOutputter.output(document, new FileWriter(XML_FILE_PATH));
            } catch (IOException | org.jdom2.JDOMException e) {
                e.printStackTrace();
            }
        }
    }



    public static class Links {
        private static final String XML_FILE_PATH = "links.xml";
        public Links() {
            createFileIfNotExists(XML_FILE_PATH, "links");
        }

        public int size() throws IOException, JDOMException {
            SAXBuilder saxBuilder = new SAXBuilder();
            Document document = saxBuilder.build(new File(XML_FILE_PATH));

            Element rootElement = document.getRootElement();
            List<Element> linkElements = rootElement.getChildren("link");
            return linkElements.size();

        }
        public int getNextId() {
            int nextId = 1;

            try {
                SAXBuilder saxBuilder = new SAXBuilder();
                Document document = saxBuilder.build(new File(XML_FILE_PATH));

                Element rootElement = document.getRootElement();
                List<Element> linkElements = rootElement.getChildren("link");

                if (!linkElements.isEmpty()) {
                    Element lastLinkElement = linkElements.get(linkElements.size() - 1);
                    Attribute idAttribute = lastLinkElement.getAttribute("id");

                    if (idAttribute != null) {
                        nextId = Integer.parseInt(idAttribute.getValue()) + 1;
                    }
                }
            } catch (IOException | JDOMException e) {
                e.printStackTrace();
            }

            return nextId;
        }
        public int getUrlId(String url){
            try {
                SAXBuilder saxBuilder = new SAXBuilder();
                File file = new File(XML_FILE_PATH);
                Document document = saxBuilder.build(file);

                Element rootElement = document.getRootElement();
                List<Element> linkElements = rootElement.getChildren("link");

                for (Element linkElement : linkElements) {
                    Element urlElement = linkElement.getChild("url");
                    if (urlElement != null && url.equals(urlElement.getText())) {
                        return  linkElement.getAttribute("id").getIntValue();
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            throw new RuntimeException();
        }
        public boolean urlExists(String url) {
            try {
                SAXBuilder saxBuilder = new SAXBuilder();
                File file = new File(XML_FILE_PATH);
                Document document = saxBuilder.build(file);

                Element rootElement = document.getRootElement();
                List<Element> linkElements = rootElement.getChildren("link");

                for (Element linkElement : linkElements) {
                    Element urlElement = linkElement.getChild("url");
                    if (urlElement != null && url.equals(urlElement.getText())) {
                        return true;
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            return false;
        }
        public Link getNotVisited(){
            Link link = new Link();
            try{
                SAXBuilder saxBuilder = new SAXBuilder();
                File file = new File(XML_FILE_PATH);

                Document document = saxBuilder.build(file);

                Element rootElement = document.getRootElement();
                List<Element> linkElements = rootElement.getChildren("link");

                for(Element element : linkElements){
                    String visited = element.getChildText("visited");
                    if(visited.equals("false")){
                        link.setUrl(element.getChildText("url"));
                        link.setId(Integer.parseInt(element.getAttributeValue("id")));
                        break;
                    }
                }

            }catch (Exception e){

                e.printStackTrace();
            }

            return  link;
        }

        public void markAsVisited(Link link) {
            try {
                SAXBuilder saxBuilder = new SAXBuilder();
                File file = new File(XML_FILE_PATH);
                Document document = saxBuilder.build(file);

                Element rootElement = document.getRootElement();
                List<Element> linkElements = rootElement.getChildren("link");

                for (Element element : linkElements) {
                    Attribute idAttribute = element.getAttribute("id");
                    if (idAttribute != null) {
                        int existingId = Integer.parseInt(idAttribute.getValue());
                        if (link.getId() == existingId) {

                            Element visitedElement = element.getChild("visited");
                            if (visitedElement != null) {
                                visitedElement.setText("true");
                            }
                        }
                    }
                }
                XMLOutputter xmlOutputter = new XMLOutputter(Format.getPrettyFormat());
                xmlOutputter.output(document, new FileWriter(XML_FILE_PATH));

            } catch (Exception e) {
                e.printStackTrace();
            }
        }




        public void save(String url) {
            try {
                if (!urlExists(url)){

                    SAXBuilder saxBuilder = new SAXBuilder();

                    File file = new File(XML_FILE_PATH);
                    Document document = saxBuilder.build(file);


                    Element linkElement = new Element("link");
                    linkElement.setAttribute("id", String.valueOf(getNextId()));

                    Element urlElement = new Element("url");
                    urlElement.setText(url);

                    Element visitedElement = new Element("visited");
                    visitedElement.setText(String.valueOf(false));

                    linkElement.addContent(urlElement);
                    linkElement.addContent(visitedElement);

                    document.getRootElement().addContent(linkElement);

                    XMLOutputter xmlOutputter = new XMLOutputter(Format.getPrettyFormat());
                    xmlOutputter.output(document, new FileWriter(XML_FILE_PATH));
                }



            } catch (IOException | JDOMException e) {
                e.printStackTrace();
            }
        }

    }
}
