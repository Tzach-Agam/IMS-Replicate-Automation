package browsers;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.edge.EdgeDriver;
import org.openqa.selenium.edge.EdgeOptions;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.firefox.FirefoxOptions;


public class WebDriverFactory  {
    /**
     * Creates a Chrome WebDriver instance with custom configurations.
     * The instance uses incognito mode, accepts insecure certificates,
     * runs in headless mode, and disables automation detection.
     * @return WebDriver instance for Chrome
     */
    public static WebDriver chromeDriver() {
        ChromeOptions options = new ChromeOptions();
        options.addArguments("--incognito");
        options.setAcceptInsecureCerts(true);
        //options.addArguments("--headless=new");
        options.setExperimentalOption("excludeSwitches", new String[] {"enable-automation"});
        WebDriver driver = new ChromeDriver(options);
        return driver;
    }

    /**
     * Creates an Edge WebDriver instance with custom configurations.
     * The instance uses InPrivate mode, accepts insecure certificates,
     * runs in headless mode, and disables automation detection.
     * @return WebDriver instance for Edge
     */
    public static WebDriver edgeDriver() {
        EdgeOptions options = new EdgeOptions();
        options.addArguments("--inprivate");
        options.setAcceptInsecureCerts(true);
        options.addArguments("--headless=new");
        options.setExperimentalOption("excludeSwitches", new String[] {"enable-automation"});
        EdgeDriver driver = new EdgeDriver(options);
        return driver;
    }

    /**
     * Creates a Firefox WebDriver instance with custom configurations.
     * The instance uses private browsing mode, accepts insecure certificates,
     * and runs in headless mode.
     * @return WebDriver instance for Firefox
     */
    public static WebDriver firefoxDriver() {
        FirefoxOptions options = new FirefoxOptions();
        options.addArguments("--private");
        options.setAcceptInsecureCerts(true);
        options.addArguments("--headless");
        // Note: Firefox doesn't have a direct equivalent for "excludeSwitches" used in Chrome and Edge
        FirefoxDriver driver = new FirefoxDriver(options);
        return driver;
    }

}
