package org.example;
import com.opencsv.CSVWriter;
import io.github.bonigarcia.wdm.WebDriverManager;
import org.example.classes.Info;
import org.example.classes.Link;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.List;

public class Main extends JFrame {

    boolean runScraping= false;
    public static XmlHelper.Links LinksXml;
    public static XmlHelper.Infos InfosXml;
    public static XmlHelper.Options OptionsXml;
    private JButton scrapeBtn,stopBtn,cleanDataBtn,exportBtn,addUrlBtn, optionsBtn;
    public Main() {
        LinksXml= new XmlHelper.Links();
        InfosXml= new XmlHelper.Infos();
        OptionsXml = new XmlHelper.Options();
        setTitle("Web Scraper");
        setSize(800, 400);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        loadUi();
        changeButtonsState(false);
    }

    private Thread thread;

    private void  loadUi(){

        scrapeBtn = new JButton();

        JPanel initialUrlPanel = new JPanel();

        JTextField initialUrlTF = new JTextField(16);
        try{

            if (LinksXml.getNotVisited().getUrl()==null|| LinksXml.getNotVisited().getUrl().isEmpty()){
                scrapeBtn.setText("Começar Scraping");
                initialUrlPanel.setVisible(true);
            }
            else {
                scrapeBtn.setText("Continuar Scraping");
                initialUrlPanel.setVisible(false);
            }
        }catch (Exception e){
            e.printStackTrace();
        }
        stopBtn = new JButton("Parar");
        cleanDataBtn = new JButton("Limpar Todos os Dados");

        stopBtn.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (thread != null) {
                    runScraping=false;
                    thread.interrupt();
                    System.out.println("Thread End");
                }
                changeButtonsState(false);
            }
        });
        exportBtn = new JButton("Exportar");

        exportBtn.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                List<Info> infoList = InfosXml.getAllInfos();
                String filePath = "export.csv";
                try (CSVWriter csvWriter = new CSVWriter(new FileWriter(filePath))) {
                    csvWriter.writeNext(new String[]{"Tipo", "Valor", "Links"});
                    for (Info info : infoList) {
                        String urls= "";
                        for (int linkId: info.getLinksIds()) {
                            if (!urls.equals(""))
                                urls= urls+"," + LinksXml.getUrl(linkId);
                            else
                                urls=LinksXml.getUrl(linkId);
                        }

                        String[] row = {
                                info.getType(),
                                info.getValue(),
                                urls
                        };
                        csvWriter.writeNext(row);
                    }

                    System.out.println("CSV file created successfully at: " + filePath);
                } catch (IOException ex) {
                    ex.printStackTrace();
                }
            }
        });

        addUrlBtn = new JButton("Adicionar Url");

        addUrlBtn.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {

                showUrlInputDialog();
            }
            });



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
                runScraping= true;
                thread = new Thread(() -> {

                    scrape();

                });
                thread.start();
                changeButtonsState(true);
                scrapeBtn.setText("Continuar Scraping");
                initialUrlPanel.setVisible(false);
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

                scrapeBtn.setText("Começar Scraping");
                initialUrlPanel.setVisible(true);

            }
        });

        optionsBtn = new JButton("Opções");

        optionsBtn.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                showOptionsDialog();
            }
        });

        JPanel buttonsPannel = new JPanel();
        buttonsPannel.add(scrapeBtn);
        buttonsPannel.add(stopBtn);
        buttonsPannel.add(exportBtn);
        buttonsPannel.add(addUrlBtn);
        buttonsPannel.add(cleanDataBtn);
        buttonsPannel.add(optionsBtn);

        JLabel initialUrlLb = new JLabel("Url Inicial:");


        initialUrlPanel.add(initialUrlLb);
        initialUrlPanel.add(initialUrlTF);

        setLayout(new BorderLayout());

        add(buttonsPannel, BorderLayout.NORTH);
        add(initialUrlPanel, BorderLayout.WEST);


    }
    private void changeButtonsState(boolean isRuning){
        scrapeBtn.setEnabled(!isRuning);
        stopBtn.setEnabled(isRuning);
        cleanDataBtn.setEnabled(!isRuning);
        exportBtn.setEnabled(!isRuning);
        addUrlBtn.setEnabled(!isRuning);
        optionsBtn.setEnabled(!isRuning);
    }

    private static void showUrlInputDialog() {
        JTextField urlField = new JTextField();

        Object[] message = {
                "URL:", urlField
        };

        int option = JOptionPane.showOptionDialog(
                null,
                message,
                "Adicionar Url",
                JOptionPane.OK_CANCEL_OPTION,
                JOptionPane.QUESTION_MESSAGE,
                null,
                null,
                null
        );

        if (option == JOptionPane.OK_OPTION) {
            String urltext = urlField.getText();
            if(!urltext.isEmpty()){
                if (!urltext.startsWith("https://"))
                    urltext = "https://"+urltext;
            }
            if (!Extractor.extractUrls(urltext).get(0).isEmpty()){
                LinksXml.save(urltext);
                System.out.println("Added "+ urltext);
            }
        }
    }

    private static  void showOptionsDialog() { {
        // OPTIONS
        JPanel optionsPanel = new JPanel();

        JLabel label = new JLabel("Limite de Urls Precorridos:");
        int limit = OptionsXml.getLimit();
        JTextField limitTextField = new JTextField(String.valueOf(limit),16);
        JPanel limitPanel = new JPanel();
        limitPanel.add(label);
        limitPanel.add(limitTextField);

        JLabel label1 = new JLabel("Tempo de Espera /s:");
        int sleepTime = OptionsXml.getSleepTime();
        sleepTime = sleepTime/1000;
        JTextField sleepTimeTextField = new JTextField(String.valueOf(sleepTime),16);
        JPanel sleepPanel = new JPanel();
        sleepPanel.add(label1);
        sleepPanel.add(sleepTimeTextField);

        optionsPanel.add(limitPanel);
        optionsPanel.add(sleepPanel);

        Object[] message = {
                optionsPanel
        };

        int option = JOptionPane.showOptionDialog(
                null,
                message,
                "Opções",
                JOptionPane.DEFAULT_OPTION,
                JOptionPane.INFORMATION_MESSAGE,
                null,
                new Object[]{"Salvar"},
                "Salvar");

        if (option == 0) {
            limit = Integer.parseInt(limitTextField.getText());
            OptionsXml.setLimit(limit);
            sleepTime = Integer.parseInt(sleepTimeTextField.getText());
            sleepTime= sleepTime*1000;
            OptionsXml.setSleepTime(sleepTime);
        }
    }}

    private void scrape() {
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
        int limit = OptionsXml.getLimit();
        int cont = 0;
        int sleepTime = OptionsXml.getSleepTime();
        while (runScraping) {
            if (cont>=limit && limit!=0)
            {
                System.out.println("Chegou ao limite");
                break;
            }
            Link link = LinksXml.getNotVisited();

            if (link.getUrl() == null) {
                System.out.println("Sem links para Scrape");
                break;
            }

            try {
                Thread.sleep(sleepTime);
                System.out.println("Url: " + link.getUrl());
                driver.get(link.getUrl());
                extractData(driver.getPageSource(), link);

            } catch (InterruptedException e) {
                System.out.println("Scraping interrupted.");
                break;
            } catch (Exception e) {
                System.out.println("Erro ao Entrar no Site");
            }
            LinksXml.markAsVisited(link);
            cont++;
        }
        driver.quit();


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