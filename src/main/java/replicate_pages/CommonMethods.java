package replicate_pages;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.openqa.selenium.TimeoutException;
import java.time.Duration;
import static utilities.UtilitiesMethods.safeClick;


public class CommonMethods {
    private final WebDriver driver;

    public CommonMethods(WebDriver driver) {
        this.driver = driver;
    }

    /**
     * Wait for the loader icon to complete when opening Qlik Replicate.
     * This is used for waiting until Replicate has finished loading.
     */
    public void loaderIconOpeningReplicate() {
        try {
            WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(10));
            wait.until(ExpectedConditions.visibilityOfElementLocated(By.cssSelector("[class='connectedLoader loader'][loader-type='ball-spin-fade-loader']")));
            wait.until(ExpectedConditions.invisibilityOfElementLocated(By.cssSelector("[class='connectedLoader loader'][loader-type='ball-spin-fade-loader']")));
        } catch (TimeoutException e) {
            System.out.println("Timeout while waiting for loader icon: " + e.getMessage());
        }
    }

    /**
     * Wait until the task data loader disappears.
     * Used to wait for task-related data loading to complete.
     */
    public void taskDataLoader() {
        try {
            WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(10));
            wait.until(ExpectedConditions.visibilityOfElementLocated(By.cssSelector("[class='loader'][loader-type='ball-clip-rotate-multiple']")));
            wait.until(ExpectedConditions.invisibilityOfElementLocated(By.cssSelector("[class='loader'][loader-type='ball-clip-rotate-multiple']")));
        } catch (TimeoutException e) {
            System.out.println("Timeout while waiting for task data loader: " + e.getMessage());
        }
    }

    /**
     * Navigate to the main page in Qlik Replicate.
     * This method opens the dropdown menu and selects the target page based on the provided 'target_page'.
     *
     * @param targetPage The name of the target page to navigate to (e.g., "tasks" or "server").
     */
    public void navigateToMainPage(String targetPage) {
        WebElement dropdown = driver.findElement(By.cssSelector(".dropdown-toggle.hiddenActionButton.right"));
        safeClick(dropdown);

        String pageLinkText = "";
        if ("tasks".equals(targetPage)) {
            pageLinkText = "Tasks";
        } else if ("server".equals(targetPage)) {
            pageLinkText = "Server";
        } else {
            throw new IllegalArgumentException("Invalid target page name");
        }

        WebElement pageLink = driver.findElement(By.xpath("//a[text()='" + pageLinkText + "']"));
        safeClick(pageLink);
    }
}


