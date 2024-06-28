package com.supportportal.grabber;

import com.supportportal.domain.CpvDb;
import com.supportportal.domain.TenderDb;
import io.github.bonigarcia.wdm.WebDriverManager;
import org.openqa.selenium.By;
import org.openqa.selenium.Keys;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

public class EisGovLvGrabber {
    public static final String URL = "https://www.eis.gov.lv/EKEIS/Supplier";
    private DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("dd.MM.yyyy");
    WebDriver driver;

    public TenderDb getTenderFromLink(String link){
        WebDriverManager.chromedriver().setup();
        ChromeOptions options = new ChromeOptions();
        options.addArguments("--headless");
        driver = new ChromeDriver(options);
        TenderDb tenderDb = null;
        try {
            tenderDb = new TenderDb();
            driver.get(link);
            driver.manage().window().maximize();

            WebDriverWait wait = new WebDriverWait(driver, 5);
            wait.until(ExpectedConditions.presenceOfAllElementsLocatedBy(By.xpath("/html/body/div[3]/div[4]/a[6]")));
            WebElement latButton = driver.findElement(By.xpath("/html/body/div[3]/div[4]/a[6]"));
            latButton.click();

            wait.until(ExpectedConditions.presenceOfAllElementsLocatedBy(By.className("panel-collapse-button")));
            List<WebElement> collapses = driver.findElements(By.className("panel-collapse-button"));
            for(int i = 1; i < collapses.size(); i++) {
                WebElement element = wait.until(ExpectedConditions.elementToBeClickable(collapses.get(i)));
                try {
                    Thread.sleep(300);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                element.click();
            }

            wait.until(ExpectedConditions.presenceOfAllElementsLocatedBy(By.className("panel-group")));
            WebElement panelGroupElement = driver.findElement(By.className("panel-group"));
            String panelGroupText = panelGroupElement.getText();
            //System.out.println(panelGroupText);

            String dateString = "";
            if(panelGroupText.contains("Izsludināts / publicēts:")) {
                dateString = panelGroupText.split("Izsludināts / publicēts:")[1].split("Identifikācijas numurs:")[0].trim();
            } else if(panelGroupText.contains("Izsludināts:")){
                dateString = panelGroupText.split("Izsludināts:")[1].trim().split("Identifikācijas numurs:")[0].trim();
            }

            String refNumber = "";
            if(panelGroupText.contains("Identifikācijas numurs:") && (panelGroupText.contains("Nosaukums:"))){
                refNumber = panelGroupText.split("Identifikācijas numurs:")[1].split("Nosaukums:")[0].trim();
            }

            String title = "";

            if(panelGroupText.contains("DIS (dinamiskā iepirkumu sistēma):")){
                title = panelGroupText.split("Nosaukums:")[1].split("DIS \\(dinamiskā iepirkumu sistēma\\):")[0].trim();
            } else {
                title = panelGroupText.split("Nosaukums:")[1].split("Regulējošais TA:")[0].trim();
            }


            String client = panelGroupText.split("Pasūtītājs:")[1].split("Kontaktpersona:")[0].trim().split(", reģ. numurs:")[0].trim();

            String deadlineString = "";
            if(panelGroupText.contains("Pieteikumu/piedāvājumu iesniegšanas termiņš:")) {
                deadlineString = panelGroupText.split("Pieteikumu/piedāvājumu iesniegšanas termiņš:")[1].split("Pieteikumu/piedāvājumu atvēršana:")[0].trim().split(" ")[0].trim();
            }

            String description = "";

            if(panelGroupText.contains("Apraksts:")) {
                description = panelGroupText.split("Apraksts:")[1].split("Piedāvājuma sagatavošanas nosacījumi")[0].trim().split(" ")[0].trim();
            }

            String mainCpvString = "";
            String addCpvsString = "";
            String field = "";

            try{
                mainCpvString = panelGroupText.split("CPV galvenais priekšmets:")[1].split("CPV papildu priekšmeti:")[0].trim();
                addCpvsString = panelGroupText.split("CPV papildu priekšmeti:")[1].split("Izpildes vieta:")[0].trim();
                if(panelGroupText.contains("Pakalpojumu kategorija:")) {
                    field = panelGroupText.split("Priekšmeta veids:")[1].split("Pakalpojumu kategorija:")[0].trim();
                } else {
                    field = panelGroupText.split("Priekšmeta veids:")[1].split("CPV galvenais priekšmets:")[0].trim();
                }

            } catch (Exception e) {
                System.out.println("<<<<<< CPV EXCEPTION >>>>>>>");
                wait.until(ExpectedConditions.presenceOfAllElementsLocatedBy(By.className("panel-collapse-button")));
                List<WebElement> collapsesAgain = driver.findElements(By.className("panel-collapse-button"));
                WebElement element = wait.until(ExpectedConditions.elementToBeClickable(collapsesAgain.get(2)));
                element.click();
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException interruptedException) {
                    interruptedException.printStackTrace();
                }
                wait.until(ExpectedConditions.presenceOfAllElementsLocatedBy(By.className("panel-group")));
                WebElement panelGroupElement2 = driver.findElement(By.className("panel-group"));
                String panelGroupText2 = panelGroupElement2.getText();
                mainCpvString = panelGroupText2.split("CPV galvenais priekšmets:")[1].split("CPV papildu priekšmeti:")[0].trim();
                addCpvsString = panelGroupText2.split("CPV papildu priekšmeti:")[1].split("Izpildes vieta:")[0].trim();
                if(panelGroupText.contains("Pakalpojumu kategorija:")) {
                    field = panelGroupText2.split("Priekšmeta veids:")[1].split("Pakalpojumu kategorija:")[0].trim();
                } else {
                    field = panelGroupText2.split("Priekšmeta veids:")[1].split("CPV galvenais priekšmets:")[0].trim();
                }
            }

            tenderDb.setLink(link);

            LocalDateTime pubDate = LocalDateTime.now(ZoneId.of("Europe/Tallinn"));
            tenderDb.setDate(pubDate);

            if(!deadlineString.isEmpty()) {
                tenderDb.setDeadline(LocalDate.parse(deadlineString, dateFormatter));
            } else {
                System.out.println(" *** Deadline string is empty ***");
            }
            tenderDb.setSourceRefNumber(refNumber);
            tenderDb.setTitle(title);
            tenderDb.setDescription(description);
            tenderDb.setField(field);
            tenderDb.setClient(client);
            List<CpvDb> cpvDbs = new ArrayList<>();
            if(mainCpvString.length() != 0) {
                cpvDbs.add(getCpvFromString(mainCpvString));
            }
            if(!addCpvsString.contains("Nav pievienots neviens ieraksts.")) {
                for(String addCpv : addCpvsString.split("\n")) {
                    cpvDbs.add(getCpvFromString(addCpv.trim()));
                }
            }
            tenderDb.setCpvDb(cpvDbs);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            driver.quit();
        }

        return tenderDb;
    }

    public List<String> getTenderLinks(int pageNumber) {
        List<String> links = new ArrayList<>();

        WebDriverManager.chromedriver().setup();
        ChromeOptions options = new ChromeOptions();
        options.addArguments("--headless");
        driver = new ChromeDriver(options);
        try {
            driver.get(URL);

            WebDriverWait wait = new WebDriverWait(driver, 5);
            wait.until(ExpectedConditions.presenceOfAllElementsLocatedBy(By.xpath("/html/body/div[4]/div[3]/div[4]/form/div[2]/div[1]/div/div[2]/div/div/div[2]/div[1]/div/div[1]/div/span/i")));

            try {
                Thread.sleep(5000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            wait.until(ExpectedConditions.presenceOfAllElementsLocatedBy(By.xpath("/html/body/div[4]/div[3]/div[4]/form/div[2]/div[2]/div[2]/div/div/div[2]/div[2]/div[2]/div/div/div/input")));
            WebElement pageInput = driver.findElement(By.xpath("/html/body/div[4]/div[3]/div[4]/form/div[2]/div[2]/div[2]/div/div/div[2]/div[2]/div[2]/div/div/div/input"));
            pageInput.clear();
            pageInput.sendKeys(String.valueOf(pageNumber));
            pageInput.sendKeys(Keys.ENTER);

            try {
                Thread.sleep(5000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            List<WebElement> elements = driver.findElements(By.tagName("a"));

            for (WebElement element : elements) {
                String attributeValue = element.getAttribute("href");
                if (attributeValue != null && attributeValue.contains("https://www.eis.gov.lv/EKEIS/Supplier/ViewProcurement/")) {
                    links.add(attributeValue);
                }
            }
        } catch (Exception e) {

        } finally {
            driver.quit();
        }

        return links;
    }

    private CpvDb getCpvFromString(String cpvString) {
        CpvDb cpvDb = new CpvDb();
        String code = cpvString.split(" ")[0];
        String description = cpvString.split(code)[1];
        cpvDb.setCode(code.trim());
        cpvDb.setDescription(description.trim());
        return cpvDb;
    }
}