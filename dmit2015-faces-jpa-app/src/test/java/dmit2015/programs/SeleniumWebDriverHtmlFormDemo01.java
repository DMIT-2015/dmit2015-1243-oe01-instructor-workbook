package dmit2015.programs;

import io.github.bonigarcia.wdm.WebDriverManager;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.firefox.FirefoxDriver;

public class SeleniumWebDriverHtmlFormDemo01 {

    public static void main(String[] args) throws InterruptedException {
        // Set up the WebDriver for Chrome
//        WebDriverManager.chromedriver().setup();
//        WebDriver driver = new ChromeDriver();
        WebDriverManager.firefoxdriver().setup();
        WebDriver driver = new FirefoxDriver();

        driver.manage().window().maximize();

        // Navigate to the specific URL
        driver.get("https://www.w3schools.com/html/html_forms.asp");

        // Find the text field elements for first name and last name
        WebElement firstNameField = driver.findElement(By.id("fname"));
        // Clear the text in the First Name field
        firstNameField.clear();
        // Send keys to the First name field
        firstNameField.sendKeys("Bruce");
        // Wait for 1 second
        Thread.sleep(1000);

        WebElement lastNameField = driver.findElement(By.id("lname"));
        lastNameField.clear();
        lastNameField.sendKeys("Less");
        Thread.sleep(1000);

        // Find the submit button then click on it
        WebElement submitButton = driver.findElement(By.cssSelector("[type='submit']"));
        submitButton.click();
        Thread.sleep(3000);

        // Close the browser
        driver.quit();
    }
}
