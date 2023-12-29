package org.example;

import io.github.bonigarcia.wdm.WebDriverManager;
import org.example.classes.Link;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.util.List;

public class Main extends JFrame {

    public XmlHelper.Links LinksXml;
    public XmlHelper.Infos InfosXml;
    public Main() {
        LinksXml= new XmlHelper.Links();
        InfosXml= new XmlHelper.Infos();



        setTitle("Web Scraper");
        setSize(600, 400);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        refreshUi();
    }

    private void  refreshUi(){

        JButton scrapeBtn = new JButton();

        JPanel initialUrlPanel = new JPanel();
        Thread thread = new Thread(this::scrape);

        JTextField initialUrlTF = new JTextField(16);
        try{


            if (LinksXml.size()>0){
                scrapeBtn.setText("Continuar Scraping");
                initialUrlPanel.setVisible(false);
            }
            else {
                scrapeBtn.setText("Começar Scraping");
                initialUrlPanel.setVisible(true);
            }
        }catch (Exception e){
            e.printStackTrace();
        }
        JButton stopBtn = new JButton("Parar");
        stopBtn.setEnabled(false);
        stopBtn.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                stopBtn.setEnabled(false);
                scrapeBtn.setEnabled(true);
                try {
                    thread.join();
                } catch (InterruptedException ex) {
                    throw new RuntimeException(ex);
                }

            }
        });
        JButton exportBtn = new JButton("Exportar");
        JButton addUrlBtn = new JButton("Adicionar Url");
        JButton cleanDataBtn = new JButton("Limpar Todos os Dados");


        scrapeBtn.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {

                String url = initialUrlTF.getText();
                if (!url.startsWith("https://")){
                    url = "https://"+url;
                }
                if (!Extractor.extractUrls(url).isEmpty()){
                    LinksXml.save(url);
                    System.out.println("printed URL");
                };
                scrapeBtn.setEnabled(false);
                stopBtn.setEnabled(true);
                thread.start();
            }
        });

        cleanDataBtn.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                File file = new File("info.xml");
                file.delete();
                file = new File("links.xml");
                file.delete();
                LinksXml= new XmlHelper.Links();
                InfosXml= new XmlHelper.Infos();
                refreshUi();
            }
        });
        JPanel buttonsPannel = new JPanel();
        buttonsPannel.add(scrapeBtn);
        buttonsPannel.add(stopBtn);
        buttonsPannel.add(exportBtn);
        buttonsPannel.add(addUrlBtn);
        buttonsPannel.add(cleanDataBtn);



        JLabel initialUrlLb = new JLabel("Url Inicial:");


        initialUrlPanel.add(initialUrlLb);
        initialUrlPanel.add(initialUrlTF);

        setLayout(new BorderLayout());

        add(buttonsPannel, BorderLayout.NORTH);
        add(initialUrlPanel, BorderLayout.WEST);
    }



    private void scrape(){

        WebDriverManager.chromedriver().setup();
        ChromeOptions options = new ChromeOptions();
        options.addArguments("--headless");
        options.addArguments("--start-maximized");
        options.addArguments("--log-level=3");
        options.addArguments("--output=/dev/null");
        options.addArguments("--ignore-certificate-errors");
        options.addArguments("window-size=1920x1080");
        options.addArguments("--ignore-ssl-errors");
        WebDriver driver = new ChromeDriver(options);


        do{
            Link link = LinksXml.getNotVisited();

            if (link.getUrl()==null){
                System.out.println("Sem links para Scrape");
                driver.quit();
                break;
            }

            try{
                Thread.sleep(5000,0);
                System.out.println("Url: " + link.getUrl());
                driver.get(link.getUrl());
                extractData(driver.getPageSource(), link);


            }catch (Exception e){
                System.out.println("Erro ao Entrar no Site");
            }
            LinksXml.markAsVisited(link);


        }while (true);


    }

    private void extractData(String html, Link link){


        List<String> LinksExtraidos = Extractor.extractUrls(html);
        for (String url:LinksExtraidos) {
            LinksXml.save(url);
        }

        List<String> EmailsExtraidos= Extractor.extractEmails(html);
        for (String email: EmailsExtraidos ) {
            InfosXml.save("email",link.getId() ,email);
        }
        List<String> NumerosExtraidos = Extractor.extractNumbers(html);
        for (String numero : NumerosExtraidos){
            InfosXml.save("numero",link.getId() ,numero);
        }

    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                new Main().setVisible(true);
            }
        });
    }
}