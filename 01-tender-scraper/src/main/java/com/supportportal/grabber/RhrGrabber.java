package com.supportportal.grabber;

import com.supportportal.domain.CpvDb;
import com.supportportal.domain.TenderDb;
import io.github.bonigarcia.wdm.WebDriverManager;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class RhrGrabber {
    public static final String URL_RSS = "https://riigihanked.riik.ee/rhr/api/public/v1/rss";
    private DateTimeFormatter estFormatter = DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm");
    private DateTimeFormatter enFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
    WebDriver driver;

    public List<TenderDb> getRssTenders() {
        WebDriverManager.chromedriver().setup();
        String pageSource = getPageSource();
        return getTendersFromString(pageSource);
    }

    public List<CpvDb> getTenderCpvs(String link) {
        link = link.replaceAll("/notices", "/general-info");
        List<CpvDb> cpvDbs = new ArrayList<>();

        WebDriverManager.chromedriver().setup();
        ChromeOptions options = new ChromeOptions();
        options.addArguments("--headless");
        driver = new ChromeDriver(options);
        try {
            driver.get(link);

            Thread.sleep(2000);

            WebDriverWait wait = new WebDriverWait(driver, 5);
            wait.until(ExpectedConditions.presenceOfAllElementsLocatedBy(By.className("content")));

            WebElement generalInfo = driver.findElement(By.className("content"));
            String generalInfoText = generalInfo.getText();
            List<String> cpvLines = new ArrayList<>();
            Matcher m = Pattern.compile("\\b([0-9]{8})\\-\\d\\b").matcher(generalInfoText);
            while (m.find()) {
                String line = m.group();
                cpvLines.add(line);
            }
            List<String> cpvLinesNoDuplicates = new ArrayList<>(new HashSet<>(cpvLines));
            for (String cpvLine : cpvLinesNoDuplicates) {
                CpvDb cpvDb = new CpvDb();
                cpvDb.setCode(cpvLine.trim());
                cpvDbs.add(cpvDb);
            }
        } catch (Exception e) {
        } finally {
            if (driver != null) {
                driver.quit();
            }
        }
        return cpvDbs;
    }

    private String getPageSource() {
        WebDriverManager.chromedriver().setup();
        ChromeOptions options = new ChromeOptions();
        options.addArguments("--headless");
        driver = new ChromeDriver(options);
        driver.get(URL_RSS);
        String pageSource = driver.findElement(By.tagName("body")).getText();
        driver.quit();
        return pageSource;
    }

    private List<TenderDb> getTendersFromString(String pageSource) {
        List<TenderDb> tenderDbs = new ArrayList<>();
        String[] tendersArray = pageSource.split("<item>");
        List<String> tendersAsString = new ArrayList<>(Arrays.asList(tendersArray));
        tendersAsString.remove(0);

        for (String t : tendersAsString) {
            String titleAndRef = t.split("</title>")[0].split("<title>")[1].trim();
            String ref = titleAndRef.substring(0, 6);
            String title = titleAndRef.substring(9);
            String link = t.split("</link>")[0].split("<link>")[1].trim();
            String descriptionString = t.split("</description>")[0].split("<description>")[1].trim();
            String field = descriptionString.split(";")[0];
            String description = descriptionString.split(";")[2];
            String[] deadlineArr = descriptionString.split("Tähtaeg: ");
            String deadlineString = deadlineArr[deadlineArr.length - 1].trim();
            LocalDateTime deadline = LocalDateTime.parse(deadlineString, estFormatter);
            String dateString = t.split("</dc:date>")[0].split("<dc:date>")[1].replace("Z", " ")
                    .replace('T', ' ').trim();
            LocalDateTime date = LocalDateTime.parse(dateString, enFormatter);
            String client = t.split("</dc:creator>")[0].split("<dc:creator>")[1].trim();

            TenderDb tenderDb = new TenderDb();
            tenderDb.setTitle(title);
            tenderDb.setSourceRefNumber(ref);
            tenderDb.setClient(client);
            tenderDb.setField(field);
            tenderDb.setLink(link);
            tenderDb.setDescription(description);
            tenderDb.setDeadline(deadline.toLocalDate());
            tenderDb.setDate(date);

            tenderDbs.add(tenderDb);

        }
        return tenderDbs;
    }
}