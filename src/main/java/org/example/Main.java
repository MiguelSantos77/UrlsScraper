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
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class Main extends JFrame {

    public List<String> Emails = new ArrayList<>();
    public List<String> Numeros = new ArrayList<>();
    public XmlHelper.Links LinksXml;
    public Main() {
        LinksXml= new XmlHelper.Links();


        setTitle("Web Scraper");
        setSize(600, 400);

        JTextField textField = new JTextField(16);
        textField.setText("http://jaxen.codehaus.org");
        JButton scrapeButton = new JButton("Scrape");
        scrapeButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String initialUrl = textField.getText();
                LinksXml.save(initialUrl);
                scrape();
            }
        });

        String[] columnNames = {"URL", "Emails", "Telefones"};
        Object[][] data = new Object[0][];
        JTable table = new JTable(data, columnNames);

        JScrollPane scrollPane = new JScrollPane(table);

        setLayout(new BorderLayout());
        add(textField, BorderLayout.SOUTH);
        add(scrapeButton, BorderLayout.NORTH);
        add(scrollPane, BorderLayout.CENTER);
    }



    private void scrape(){
        WebDriverManager.chromedriver().setup();
        ChromeOptions options = new ChromeOptions();
        //options.addArguments("--headless");
        options.addArguments("--start-maximized");
        options.addArguments("--log-level=3");
        options.addArguments("--output=/dev/null");
        options.addArguments("--ignore-certificate-errors");
        options.addArguments("window-size=1920x1080");
        options.addArguments("--ignore-ssl-errors");
        WebDriver driver = new ChromeDriver(options);


        do{

            Link link = LinksXml.getNotVisitedUrl();
            if (link.getUrl().equals("") || link.getUrl()==null){
                System.out.println("Sem links para Scrape");
                break;
            }

            if (link.getUrl() != null || !link.getUrl().isEmpty())
            {

                try{
                    Thread.sleep(5000,0);
                    System.out.println("Url: " + link.getUrl());
                    driver.get(link.getUrl());
                    extractData(driver.getPageSource(), link);


                }catch (Exception e){
                    System.out.println("Erro ao Entrar no Site");
                }
                LinksXml.markAsVisited(link);
            }

        }while (true);


    }








    private void extractData(String html, Link link) throws IOException {


        List<String> LinksExtraidos = Extractor.extractUrls(html);
        for (String url:LinksExtraidos) {
            LinksXml.save(url);

        }

        List<String> EmailsExtraidos= Extractor.extractEmails(html);
        for (String email: EmailsExtraidos ) {
            if (Emails.contains(email))
                Emails.add(email);
        }
        List<String> NumerosExtraidos = Extractor.extractNumbers(html);
        for (String numero : NumerosExtraidos){
            if (Numeros.contains(numero))
                Numeros.add(numero);
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
