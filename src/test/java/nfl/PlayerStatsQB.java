package nfl;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVPrinter;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.testng.annotations.Test;
import testbase.TestBase;

import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import static org.testng.Assert.assertEquals;

public class PlayerStatsQB extends TestBase {

    @Test
    public void getPlayerNames() throws IOException {

        driver.get("https://www.nfl.com/stats/player-stats");
        String pageTitle = driver.getTitle();
        System.out.println("Page Title is: " + pageTitle);
        assertEquals(pageTitle, "NFL 2024 Player Stats | passing Stats | NFL.com");
        rejectCookiesPolicy(driver);

        List<String> rowData;
        ArrayList<List<String>> playerData = new ArrayList<>();

        do {

            // Each page, grab new data in the table, or we'll hit 'state element' error
            List<WebElement> playerRows = driver.findElements(By.xpath("//table/tbody/tr"));

            rowData = playerRows.stream()
                    .map(row -> row.getText().replace("\n", " "))
                    .collect(Collectors.toList());

            playerData.add(rowData);

            if (rowData.size() == 25) {
                // If multiple pages of players to search through, this will tab through each page until a less that full page is found
                driver.findElement(By.className("nfl-o-table-pagination__next")).click();
            }
        } while (rowData.size() == 25);

        List<String> allData = playerData.stream()
                .flatMap(s -> s.stream())
                .collect(Collectors.toList());

        System.out.println(allData);

        writeCSVFile(allData, "PlayerStats-QB", "\n");

    }

    private void writeCSVFile(List<String> data, String filename, String recordSeparator) throws IOException {
        //String NEW_LINE_SEPARATOR = lineSeparator;
        String CSV_File_Path = "./target/csv/" + filename + ".csv";
        // Write the file
        BufferedWriter writer = Files.newBufferedWriter(Paths.get(CSV_File_Path));
        CSVPrinter csvPrinter = new CSVPrinter(writer, CSVFormat.DEFAULT.builder()
                .setDelimiter(",")
                .build()
                .withRecordSeparator(recordSeparator));
                //.withHeader("Player Name"));

        // Push the values into the file
        // csvPrinter.printRecord("TEST");
        csvPrinter.printRecords(data);
        csvPrinter.flush();
        csvPrinter.close();
    }
}
