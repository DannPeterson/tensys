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

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class CpvvGrabber {
    private WebDriver driver;
    private DateTimeFormatter formatterWithTime = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
    private DateTimeFormatter formatterNoTime = DateTimeFormatter.ofPattern("yyyy-MM-dd");

    public List<String> getTenderLinks(int pageNumber) {
        String URL = "https://cvpp.eviesiejipirkimai.lt/?pageNumber=" + pageNumber + "&pageSize=100";
        List<String> links = new ArrayList<>();

        WebDriverManager.chromedriver().setup();
        ChromeOptions options = new ChromeOptions();
        options.addArguments("--headless");
        driver = new ChromeDriver(options);
        try {
            driver.get(URL);

            WebDriverWait wait = new WebDriverWait(driver, 5);
            wait.until(ExpectedConditions.presenceOfAllElementsLocatedBy(By.className("notice-search-result")));

            List<WebElement> items = driver.findElements(By.className("notice-search-item"));
            for (WebElement item : items) {
                List<WebElement> elements = item.findElements(By.tagName("a"));
                for (WebElement a : elements) {
                    if (a.getAttribute("href").contains("/Notice/Details/")) {
                        links.add(a.getAttribute("href"));
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            driver.quit();
        }
        driver.quit();
        return links;
    }

    public TenderDb getTenderFromLink(String link) {
        WebDriverManager.chromedriver().setup();
        ChromeOptions options = new ChromeOptions();
        options.addArguments("--headless");
        driver = new ChromeDriver(options);
        TenderDb tender = null;
        try {

            tender = new TenderDb();
            driver.get(link);
            Thread.sleep(2000);

            WebDriverWait wait = new WebDriverWait(driver, 5);
            wait.until(ExpectedConditions.presenceOfAllElementsLocatedBy(By.className("notice-container")));

            WebElement noticeContainer = driver.findElement(By.className("notice-container"));
            String container = noticeContainer.getText();

            if (container.contains("Skelbimas apie sutarties skyrimą") ||
                    container.contains("Skelbimas dėl savanoriško Ex Ante skaidrumo") ||
                    container.contains("Skelbimas apie sutarties sudarymą") ||
                    container.contains("Reguliarus orientacinis skelbimas") ||
                    container.contains("Projekto konkurso rezultatai") ||
                    container.contains("Išankstinis informacinis skelbimas")) {
                driver.quit();
                return null;
            }

            String client = "";

            if(container.contains("Pavadinimas ir adresai")){
                client = container.split("Pavadinimas ir adresai")[1].split("\n")[1];
            } else {
                client = container.split("Pavadinimas, adresas ir kontaktinis punktas\\(-ai\\)")[1].split("\n")[1];
            }

            String publicationDate = "";

            if(container.contains("Šio skelbimo išsiuntimo data:")) {
                publicationDate = container.split("Šio skelbimo išsiuntimo data:")[1].split("\n")[1];
            } else {
                publicationDate = container.split("Paskelbimo data")[1].split("\n")[1];
            }

            String deadlineString = "";
            String deadlineDate = "";
            String deadlineTime = "";
            String deadlineFull = "";

            if (container.contains("Vokų su pasiūlymais atplėšimo sąlygos")) {
                deadlineString = container.split("Vokų su pasiūlymais atplėšimo sąlygos")[1];
                deadlineDate = deadlineString.split("Data:  ")[1].split("\n")[0];
                deadlineTime = deadlineString.split("Vietos laikas:  ")[1].split("\n")[0];
                deadlineFull = deadlineDate + " " + deadlineTime;
            } else if (container.contains("Preliminari išankstinės konsultacijos trukmė")) {
                deadlineString = container.split("Preliminari išankstinės konsultacijos trukmė")[1];
                deadlineDate = deadlineString.split("Data:  ")[1].split("\n")[0];
                deadlineTime = deadlineString.split("Vietos laikas:  ")[1].split("\n")[0];
                deadlineFull = deadlineDate + " " + deadlineTime;
            } else if (container.contains("Terminas pastaboms ir pasiūlymams teikti")) {
                deadlineString = container.split("Terminas pastaboms ir pasiūlymams teikti")[1];
                deadlineDate = deadlineString.split("Data:  ")[1].split("\n")[0];
                deadlineTime = deadlineString.split("Vietos laikas:  ")[1].split("\n")[0];
                deadlineFull = deadlineDate + " " + deadlineTime;
            } else if (container.contains("Pasiūlymų ar prašymų dalyvauti priėmimo terminas")) {
                deadlineString = container.split("Pasiūlymų ar prašymų dalyvauti priėmimo terminas")[1];
                deadlineDate = deadlineString.split("Data:  ")[1].split("\n")[0];
                deadlineTime = deadlineString.split("Vietos laikas:  ")[1].split("\n")[0];
                deadlineFull = deadlineDate + " " + deadlineTime;
            } else if (container.contains("Projektų ar prašymų dalyvauti priėmimo terminas")) {
                deadlineString = container.split("Projektų ar prašymų dalyvauti priėmimo terminas")[1];
                deadlineDate = deadlineString.split("Data:  ")[1].split("\n")[0];
                deadlineTime = deadlineString.split("Vietos laikas:  ")[1].split("\n")[0];
                deadlineFull = deadlineDate + " " + deadlineTime;
            } else if(container.contains("Paraiškų dalyvauti pirkimo procedūroje priėmimo terminas")) {
                deadlineString = container.split("Paraiškų dalyvauti pirkimo procedūroje priėmimo terminas")[1].trim().split("\n")[0];
                deadlineDate = deadlineString.split(" ")[0];
                deadlineTime = deadlineString.split(" ")[2];
                deadlineFull = deadlineDate + " " + deadlineTime;
            }

            if(deadlineTime.split(":")[0].length() == 1){
                deadlineTime = "0" + deadlineTime;
                deadlineFull = deadlineDate + " " + deadlineTime;
            }

            String title = "";

            if (container.contains("II dalis: Objektas")) {
                String objectSection = container.split("II dalis: Objektas")[1].split("III dalis:")[0];
                if (container.contains("Pirkimo pavadinimas")) {
                    title = objectSection.split("Pirkimo pavadinimas")[1].split("\n")[1];
                } else if (container.contains("Išankstinės konsultacijos objektas. Informacija, kokio indėlio tikimasi iš konsultacijai kviečiamų subjektų")) {
                    title = objectSection.split("Išankstinės konsultacijos objektas. Informacija, kokio indėlio tikimasi iš konsultacijai kviečiamų subjektų")[1].split("\n")[1];
                } else if (container.contains("Pavadinimas\n")) {
                    title = objectSection.split("Pavadinimas\n")[1].split("\n")[0];
                }
            } else if (container.contains("II dalis: Pirkimo objektas ir kita informacija")) {
                String objectSection = container.split("II dalis: Pirkimo objektas ir kita informacija")[1].split("III dalis:")[0];
                title = objectSection.split("Pavadinimas")[1].split("\n")[1];
            }

            String field = "";

            if (container.contains("Sutarties tipas")) {
                field = container.split("Sutarties tipas")[1].split("\n")[1];
            } else {
                try {
                    WebElement headerElement = driver.findElement(By.xpath("/html/body/div[3]/div/div/div/div/div[2]/div/div/div[1]"));
                    String[] headerText = headerElement.getText().split("\n");
                    field = headerText[headerText.length-1];
                } catch (Exception e) {
                }

            }

            driver.quit();

            List<String> cpvLines = new ArrayList<>();
            List<CpvDb> cpvDbs = new ArrayList<>();
            Matcher m = Pattern.compile("\\b([0-9]{8})\\b.*").matcher(container);
            while (m.find()) {
                String line = m.group();
                if(line.contains("   ") || line.contains("  -  ")){
                    cpvLines.add(line);
                }
            }
            List<String> cpvLinesNoDuplicates = new ArrayList<>(new HashSet<>(cpvLines));
            for (String cpvLine : cpvLinesNoDuplicates) {
                CpvDb cpvDb = new CpvDb();
                if(cpvLine.contains("   ")){
                    cpvDb.setCode(cpvLine.split("   ")[0].trim());
                    cpvDb.setDescription(cpvLine.split("   ")[1].trim());
                } else if(cpvLine.contains("  -  ")){
                    cpvDb.setCode(cpvLine.split("  -  ")[0].trim());
                    cpvDb.setDescription(cpvLine.split("  -  ")[1].trim());
                }

                cpvDbs.add(cpvDb);
            }


            tender.setLink(link);
            tender.setSourceRefNumber(link.split("https://cvpp.eviesiejipirkimai.lt/Notice/Details/")[1]);
            tender.setClient(client);
            tender.setTitle(title);
            tender.setField(field);

            LocalDateTime pubDate = LocalDateTime.now(ZoneId.of("Europe/Tallinn"));
            tender.setDate(pubDate);
            tender.setDeadline(LocalDateTime.parse(deadlineFull, formatterWithTime).toLocalDate());
            tender.setCpvDb(cpvDbs);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            driver.quit();
        }

        return tender;
    }
}