package dmit2015.faces;

import org.junit.jupiter.api.*;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.openqa.selenium.*;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.time.Duration;
import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class StudentCrudPageSeleniumIT {

    private static WebDriver driver;

    private static JavascriptExecutor js;

    static List<String> sharedEditIds = new ArrayList<>();

    private static final String primeFacesDataTableId = "form:dt-Students";

    @BeforeAll
    static void beforeAllTests() {
        // You can download the latest version of Selenium Manager from https://github.com/titusfortner/selenium_manager_debug/releases
        // and then use the command `chmod u+x ~/Downloads/selenium-manager-linux` to make the file executable.
        // Use the command `~/Downloads/selenium-manager-linux --browser chrome` to download the webdriver for Chrome browser
        // Use the command `~/Downloads/selenium-manager-linux --browser firefox` to download the webdriver for Firefox browser
        // Uncomment statement below to specify the location of the webdriver file.
        // System.setProperty("webdriver.chrome.driver", "/home/user2015/.cache/selenium/chromedriver/linux64/132.0.6834.159/chromedriver");
        System.setProperty("webdriver.gecko.driver", "/snap/bin/geckodriver");

        // WebDriverManager
        //     .chromedriver()
        //     .driverVersion("132.0.6834.159")
        //     .setup();

        // var chromeOptions = new ChromeOptions();
        // chromeOptions.addArguments("--remote-allow-origins=*");
        // driver = new ChromeDriver(chromeOptions);

        driver = new FirefoxDriver();

        js = (JavascriptExecutor) driver;
    }

    @AfterAll
    static void afterAllTests() {
        driver.quit();
    }

    @BeforeEach
    void beforeEachTestMethod() {

    }

    @AfterEach
    void afterEachTestMethod() throws InterruptedException {
//        driver.manage().timeouts().implicitlyWait(Duration.ofSeconds(5));
        Thread.sleep(1000);
    }

    private void setTextValue(String fieldId, String fieldValue) throws InterruptedException {
        WebElement element = driver.findElement(By.id(fieldId));
        element.clear();
        element.sendKeys(fieldValue);
        Thread.sleep(1000);
    }

    private void setPrimeFacesDatePickerValue(String fieldId, String fieldValue) {
        // The text field for the p:datePicker component has a suffix of "_input" appended to the end of the field id.
        final String datePickerTextFieldId = String.format("%s_input", fieldId);
        WebElement element = driver.findElement(By.id(datePickerTextFieldId));

        element.sendKeys(Keys.chord(Keys.ESCAPE));

        element.sendKeys(fieldValue);
        element.sendKeys(Keys.chord(Keys.TAB));
    }

    private void setPrimeFacesSelectOneMenuValue(String fieldId, String fieldValue) {
        // The id of the element to click for p:selectOneMenu has a suffix of "_label" appended to the id of the p:selectOneMenu
        String selectOneMenuId = String.format("%s_label", fieldId);
        driver.findElement(By.id(selectOneMenuId)).click();
        // Wait until 3 seconds for select items to pop up
        var waitSelectOneMenu = new WebDriverWait(driver, Duration.ofSeconds(3));
        // The id of the items for p:selectOneMenu has a suffix of "_items" appended to the id of the p:selectOneMenu
        String selectOneMenuItemsId = String.format("%s_items", fieldId);
        var selectOneMenuItems = waitSelectOneMenu.until(ExpectedConditions.visibilityOfElementLocated(By.id(selectOneMenuItemsId)));
        // The value for each item is stored a attribute named "data-label"
        String selectItemXPath = String.format("//*[@data-label=\"%s\"]", fieldValue);
        var selectItem = selectOneMenuItems.findElement(By.xpath(selectItemXPath));
        selectItem.click();
    }

    /**
     * Find and returns the table row element with the "data-rk" attribute value that matches idValue.
     *
     * @param idValue The unique identifier value for the row.
     * @return The row element that contains the idValue valur or null if not found.
     */
    private WebElement findRowIndex(String idValue) {
        // Check each page in the dataTable for a row that contains the idValue until found or the last page is checked.
        // Find the total number of pages in the table paginator
        int totalPages = driver.findElements(By.className("ui-paginator-page")).size();
        // Set the current page to 1 of the paginator
        int currentPage = 1;
        // Check each page of the dataTable
        while (currentPage <= totalPages) {
            // find the table element
            var tableElement = driver.findElement(By.id(primeFacesDataTableId));
            // Get all the rows from the table
            var tableRows = tableElement.findElements(By.tagName("tr"));
            // Track which row index the idValue is located
            int currentRowIndex = 0;
            // Check each row in the dataTable
            for (WebElement currentRow : tableRows) {
                // The idValue is stored in the "data-rk" attribute of the <tr> element
                String rowIdValue = currentRow.getDomAttribute("data-rk");
                // If the row contains a id value and it matches idValue then return the current row
                if (rowIdValue != null && rowIdValue.equalsIgnoreCase(idValue)) {
                    return tableRows.get(currentRowIndex);
                }
                // Check the next row
                currentRowIndex += 1;
            }
            // Check the next page
            currentPage += 1;
            // Click on the next page link
            var pageNextLink = driver.findElement(By.className("ui-paginator-next"));
            if (pageNextLink.isEnabled()) {
                pageNextLink.click();
            }
        }

        return null;
    }

    @Order(1)
    @ParameterizedTest
    @CsvSource(value = {
            "firstName, Kara, lastName, Learn, courseSection, DMIT2015-1243-OE01",
            "firstName, Alexander, lastName, Knibb, courseSection, DMIT2015-1243-A01",
            "firstName, Andrew, lastName, Cummings, courseSection, DMIT2015-1243-A02",
    })
    void shouldCreate(
            String field1Id, String field1Value,
            String field2Id, String field2Value,
            String field3Id, String field3Value
    ) throws InterruptedException {

        driver.get("http://localhost:8080/student/crud.xhtml");
        // Maximize the browser window to see the data being inputted
        driver.manage().window().maximize();
        Thread.sleep(1000);

        assertThat(driver.getTitle())
                .isEqualToIgnoringCase("Student - CRUD");

        // Find the New button by id then click on it
        var newButtonElement = driver.findElement(By.id("form:newButton"));
        assertThat(newButtonElement).isNotNull();
        newButtonElement.click();

        // Set the value for each form field.
        // Add suffix `_input` for p:inputNumber
        setTextValue("dialogs:" + field1Id, field1Value);
        // setPrimeFacesSelectOneMenuValue(field2Id, field2Value);
        // setPrimeFacesDatePickerValue(field1Id, field1Value);
        setTextValue("dialogs:" + field2Id, field2Value);
        setTextValue("dialogs:" + field3Id, field3Value);

        Thread.sleep(1000);

        // Find the Save button then click on it
        var saveButtonElement = driver.findElement(By.id("dialogs:saveButton"));
        assertThat(saveButtonElement).isNotNull();
        saveButtonElement.click();

        // Wait for 3 seconds and verify navigate has been redirected to the listing page
        var wait = new WebDriverWait(driver, Duration.ofSeconds(3));
        var growlMessage = wait.until(ExpectedConditions.visibilityOfElementLocated(By.className("ui-growl-message")));
        // Verify the feedback message is displayed in the page
        String feedbackMessage = growlMessage.getText();
        assertThat(feedbackMessage)
                .containsIgnoringCase("Create was successful");
        // The primary key of the entity is at the end of the feedback message after the last space character.
        final int indexOfPrimaryKeyValue = feedbackMessage.lastIndexOf(" ") + 1;
        // Extract the primary key and remove any comma separating numbers greater 999
        String idValue = feedbackMessage.substring(indexOfPrimaryKeyValue).replaceAll(",", "");
        // Add the primary key to the list for usage in other test methods
        sharedEditIds.add(idValue);

        Thread.sleep(1000);
    }

    @Order(2)
    @ParameterizedTest
    @CsvSource({
            "0, Kara, Learn, DMIT2015-1243-OE01",
            "1, Alexander, Knibb, DMIT2015-1243-A01",
            "2, Andrew, Cummings, DMIT2015-1243-A02",
    })
    void shouldList(
            int idIndex,
            String expectedColumn1Value, String expectedColumn2Value, String expectedColumn3Value
    ) throws InterruptedException {
        String expectedIdValue = sharedEditIds.get(idIndex);
        // Open a browser and navigate to the index page
        driver.get("http://localhost:8080/student/crud.xhtml");
        // Maximize the browser window so we can see the data being inputted
        driver.manage().window().maximize();
        Thread.sleep(1000);

        assertThat(driver.getTitle())
                .isEqualToIgnoringCase("Student - CRUD");

        WebElement rowElement = findRowIndex(expectedIdValue);
        assertThat(rowElement).isNotNull();

        var rowColumns = rowElement.findElements(By.xpath("td"));
        final String column1Value = rowColumns.get(0).getText();
        final String column2Value = rowColumns.get(1).getText();
        final String column3Value = rowColumns.get(2).getText();

        assertThat(column1Value)
                .isEqualToIgnoringCase(expectedColumn1Value);
        assertThat(column2Value)
                .isEqualToIgnoringCase(expectedColumn2Value);
        assertThat(column3Value)
                .isEqualToIgnoringCase(expectedColumn3Value);

    }

    @Order(3)
    @Test
    void shouldDelete() throws InterruptedException {
        // Open a browser and navigate to the target page
        driver.get("http://localhost:8080/student/crud.xhtml");
        // Maximize the browser window so we can see the data being inputted
        driver.manage().window().maximize();
        Thread.sleep(1000);

        assertThat(driver.getTitle())
                .isEqualToIgnoringCase("Student - CRUD");

        // Find and delete all the rows added from the shouldCreate() method
        for (String idValue : sharedEditIds) {

            WebElement rowElement = findRowIndex(idValue);
            assertThat(rowElement).isNotNull();

            var rowColumns = rowElement.findElements(By.xpath("td"));
            var lastColumnIndex = rowColumns.size() - 1;
            var actionColumnButtons = rowColumns.get(lastColumnIndex).findElements(By.tagName("button"));
            // Delete button is the 2nd button in the column
            var deleteButton = actionColumnButtons.get(1);
            deleteButton.click();

            var wait = new WebDriverWait(driver, Duration.ofSeconds(1));

            var confirmationDialog = wait.until(ExpectedConditions.visibilityOfElementLocated(By.className("ui-dialog-buttonpane")));
            Thread.sleep(1000);
            var confirmButtons = confirmationDialog.findElements(By.tagName("button"));
            // Yes button is the 1std button in the diaog
            var yesButton = confirmButtons.getFirst();
            yesButton.click();

            wait = new WebDriverWait(driver, Duration.ofSeconds(3));
            var growlMessage = wait.until(ExpectedConditions.visibilityOfElementLocated(By.className("ui-growl-message")));
            assertThat(driver.getTitle())
                    .isEqualToIgnoringCase("Student - CRUD");
            assertThat(growlMessage.getText())
                    .containsIgnoringCase("Delete was successful");

        }

    }

}