package nfl;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVPrinter;
import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.testng.annotations.Test;
import testbase.TestBase;

import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.time.Duration;
import java.util.ArrayList;
import java.util.List;

import static org.testng.Assert.assertEquals;

/**
 * Script to scrape data from NFL.com player stat categories
 * and put data into a CSV
 */
public class PlayerStatsDataFetcher extends TestBase {

    // Define stat category and year to scrape
    private static final String STAT_CATEGORY = "Passing";
    private static final Integer YEAR = 2024;

    // Site URL
    private static final String URL = "https://www.nfl.com/stats/player-stats";

    // Xpath
    private static final String PLAYER_ROWS_XPATH = "//table/tbody/tr";
    private static final String NEXT_BUTTON_XPATH = "//a[@title='Next Page' and not(contains(@class,'nfl-o-table-pagination__disabled'))]";

    @Test
    public void getPlayerNames() throws IOException, InterruptedException {

        // Navigate to URL
        driver.get(URL);
        String pageTitle = driver.getTitle();
        System.out.println("Page Title is: " + pageTitle);
        assertEquals(pageTitle, "NFL 2025 Player Stats | passing Stats | NFL.com");
        rejectCookiesPolicy(driver);

        // Select stat category
        By statCategoryXpath = By.xpath("//a[text()='" + STAT_CATEGORY + "']");
        driver.findElement(statCategoryXpath).click();
        assertEquals(driver.findElement(statCategoryXpath).getText(), STAT_CATEGORY);

        // Select year
        driver.findElement(By.xpath("//label[text()='Year']/following-sibling::select")).click();
        driver.findElement(By.xpath("//option[text()='" + YEAR + "']")).click();

        // Wait for DOM ready state
        waitUntilPageLoaded();

        // Confirm selected year
        pageTitle = driver.getTitle();
        assertEquals(pageTitle, YEAR + " NFL " + STAT_CATEGORY.toLowerCase() + " stats - Players | NFL.com");

        List<String> playerData = new ArrayList<>();

        do {
            // Each page, grab new data in the table, or we'll hit 'stale element' error
            List<WebElement> playerRows = driver.findElements(By.xpath(PLAYER_ROWS_XPATH));

            List<String> rowData = playerRows.stream()
                    .map(row -> row.getText().replace("\n", " "))
                    .toList();

            playerData.addAll(rowData);

            // Check if "Next Page" button is present & enabled
            List<WebElement> nextButtons = driver.findElements(By.xpath(NEXT_BUTTON_XPATH));
            if (nextButtons.isEmpty()) {
                break; // No more pages
            }

            nextButtons.getFirst().click();

            // Wait for DOM ready state
            waitUntilPageLoaded();

            // NFL page will reload the same data repeatedly unless we wait a bit
            Thread.sleep(5000);

        } while (true);

        System.out.println(playerData);

        writeCSVFile(playerData, "PlayerStats-" + STAT_CATEGORY + "-" + YEAR, "\n");
    }

    private void writeCSVFile(List<String> data, String filename, String recordSeparator) throws IOException {
        String csvFilePath = "./target/csv/" + filename + ".csv";

        try (BufferedWriter writer = Files.newBufferedWriter(Paths.get(csvFilePath));
             CSVPrinter csvPrinter = new CSVPrinter(writer, CSVFormat.DEFAULT.builder()
                     .setDelimiter(',')
                     .setRecordSeparator(recordSeparator)
                     .build())) {

            csvPrinter.printRecords(data);
            csvPrinter.flush();
        }
    }

    private void waitUntilPageLoaded() {
        new WebDriverWait(driver, Duration.ofSeconds(10)).until(
                webDriver -> ((JavascriptExecutor) webDriver)
                        .executeScript("return document.readyState")
                        .equals("complete")
        );
    }

}
