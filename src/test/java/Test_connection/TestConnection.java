package Test_connection;

import general.General;
import org.openqa.selenium.By;
import org.testng.Assert;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;

import org.testng.annotations.*;
import java.io.IOException;


public class TestConnection extends General {
    String endpointName;

    @BeforeClass
    void OpenBrowser() throws IOException {
        initializeWebObjects();
        initializeWebDriver();
    }

    @BeforeMethod
    void EnterManageEndpoints(){
        endpointName = manageEndpoints.randomEndpointName("IMS_Source");
        tasksGeneralPage.enterManageEndpoints();
        manageEndpoints.newEndpointConnection();
    }

    @AfterMethod
    void CloseManageEndpoints() throws InterruptedException {
        manageEndpoints.save();
        //manageEndpoints.deleteEndpointByName(endpointName);
        manageEndpoints.close();
    }

    @AfterClass
    void CloseBrowser(){
        driver.quit();
    }

    @Test
    void validConnection() throws InterruptedException {
        manageEndpoints.createIMSsource2(endpointName, "IMS endpoint for tests",
                "IMS", "zos5.qliktech.com", "5461", "VICTORK", "VICTORK","HOSP62_BULK", "HOSP62", "HOSP62_ag", "HOSP62_ag");
        manageEndpoints.testConnection();
        WebElement connectionMessage = wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//span[@class='ng-binding ng-scope']")));
        String messageText = connectionMessage.getText();
        Assert.assertTrue(messageText.contains("Test connection succeeded"), "Expected success message is not displayed.");
        System.out.println("Test passed: Connection passed as expected with message - " + messageText);
    }

    @Test
    void emptyServer() throws InterruptedException {
        manageEndpoints.createIMSsource2(endpointName, "IMS endpoint for tests",
                "IMS", "", "5461", "VICTORK", "VICTORK","HOSP62_BULK", "HOSP62", "HOSP62_ag", "HOSP62_ag");
        manageEndpoints.testConnection();
        WebElement connectionMessage = wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//span[@class='ng-binding ng-scope']")));
        String messageText = connectionMessage.getText();
        Assert.assertTrue(messageText.contains("SYS-E-HTTPFAIL, java.sql.SQLException: Connection refused: connect."), "Expected failure message is not displayed.");
        System.out.println("Test passed: Connection failed as expected with message - " + messageText);
    }

    @Test
    void invalidServer() throws InterruptedException {
        manageEndpoints.createIMSsource2(endpointName, "IMS endpoint for tests",
                "IMS", "asdasdas", "5461", "VICTORK", "VICTORK", "HOSP62_BULK", "HOSP62", "HOSP62_ag", "HOSP62_ag");
        manageEndpoints.testConnection();
        WebElement connectionMessage = wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//span[@class='ng-binding ng-scope']")));
        String messageText = connectionMessage.getText();
        Assert.assertTrue(messageText.contains("SYS-E-HTTPFAIL, java.sql.SQLException: No such host is known"), "Expected failure message is not displayed.");
        System.out.println("Test passed: Connection failed as expected with message - " + messageText);
    }


}

