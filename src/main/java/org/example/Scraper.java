package org.example;

import io.github.bonigarcia.wdm.WebDriverManager;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.firefox.FirefoxOptions;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.w3c.dom.Element;
import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Dictionary;
import java.util.Hashtable;
import java.util.List;
import java.util.concurrent.TimeUnit;

public class Scraper {

    protected String browserDriverPath = Paths.get("").toAbsolutePath().toString();
    protected Dictionary<String, String> xpaths = new Hashtable<>();
    protected float waitAfterAction = 0.2f;
    public boolean isFirefoxDriver = false;
    public String userAgent;
    public WebDriver driver;

    public Scraper(boolean isHeadless , String userAgent) {
        this.userAgent = userAgent;
        WebDriverManager.chromedriver().setup();
        ChromeOptions chromeOptions = getOptions(userAgent, isHeadless);
        driver = new ChromeDriver(chromeOptions);

    }

    public ChromeOptions getOptions(String userAgent, boolean isHeadless) {
        ChromeOptions options = new ChromeOptions();
        if (isHeadless) {
            options.addArguments("--headless");
        }
        if (userAgent != null && !userAgent.isEmpty()) {
            options.addArguments("user-agent=" + userAgent);
        }
        options.addArguments("--start-maximized");
        options.addArguments("--log-level=3");
        options.addArguments("--output=/dev/null");
        options.addArguments("--ignore-certificate-errors");
        options.addArguments("--headless");
        options.addArguments("window-size=1920x1080");
        options.addArguments("--ignore-ssl-errors");
        return options;
    }

    public WebElement waitTillLoaded(By locator, int loadSeconds) {
        WebDriverWait wait = new WebDriverWait(driver, loadSeconds);
        return wait.until(ExpectedConditions.visibilityOfElementLocated(locator));
    }

    public WebElement waitForElementVisible(By locator) {
        WebDriverWait wait = new WebDriverWait(driver, 10);
        return wait.until(ExpectedConditions.visibilityOfElementLocated(locator));
    }

    public boolean clickElement(By locator) {
        try {
            waitForElementVisible(locator).click();
            return true;
        } catch (Exception e) {
            System.out.println("Unknown error " + e.getMessage() + " occurred on page " + driver.getTitle());
            return false;
        }
    }

    public boolean isElementVisible(By locator) {
        try {
            return waitForElementVisible(locator).isDisplayed();
        } catch (Exception e) {
            System.out.println("Unknown error " + e.getMessage() + " occurred on page " + driver.getTitle());
            return false;
        }
    }

    public boolean isElementPresent(By locator) {
        try {
            driver.findElement(locator);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    public WebElement customFindElement(By by) {
        try {
            return driver.findElement(by);
        } catch (Exception e) {
            return null;
        }
    }


    public String getTextNode(WebElement e) {
        String text = e.getAttribute("textContent").trim();
        List<WebElement> children = e.findElements(By.xpath("./*"));
        for (WebElement child : children) {
            text = text.replaceFirst(child.getAttribute("textContent"), "").trim();
        }
        return text;
    }

    public static byte[] makeBytesSmaller(byte[] bytes) {
        ByteArrayInputStream bis = new ByteArrayInputStream(bytes);
        BufferedImage bufferedImage;
        try {
            bufferedImage = ImageIO.read(bis);
        } catch (IOException e) {
            e.printStackTrace();
            return bytes;
        }
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        try {
            ImageIO.write(bufferedImage, "jpeg", bos);
        } catch (IOException e) {
            e.printStackTrace();
            return bytes;
        }
        return bos.toByteArray();
    }

    public static Image byteArrayToImage(byte[] data) {
        ByteArrayInputStream bis = new ByteArrayInputStream(data);
        try {
            return ImageIO.read(bis);
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }
}
