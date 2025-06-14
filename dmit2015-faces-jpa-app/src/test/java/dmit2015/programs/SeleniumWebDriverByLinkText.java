package dmit2015.programs;

import io.github.bonigarcia.wdm.WebDriverManager;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;

public class SeleniumWebDriverByLinkText {

    public static void main(String[] args) {
        WebDriverManager.chromedriver().setup();
        WebDriver driver = new ChromeDriver();

        driver.manage().window().maximize();

        // Navigate to the specific URL
        driver.get("http://localhost:8080/index.xhtml");
        WebElement ideaLink = driver.findElement(By.linkText("IntelliJ IDEA"));
        ideaLink.click();
        WebElement facesLink = driver.findElement(By.partialLinkText("Faces"));
        facesLink.click();
    }
}
