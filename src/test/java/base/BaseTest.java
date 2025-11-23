package base;

import com.appium.config.ConfigManager;
import io.appium.java_client.android.AndroidDriver;
import io.appium.java_client.android.options.UiAutomator2Options;
import io.appium.java_client.service.local.AppiumDriverLocalService;
import io.appium.java_client.service.local.AppiumServiceBuilder;
import org.testng.annotations.AfterSuite;
import org.testng.annotations.BeforeSuite;

import java.net.MalformedURLException;
import java.net.URL;
import java.time.Duration;
import java.util.logging.Logger;

public class BaseTest {
    protected static final Logger logger = Logger.getLogger(BaseTest.class.getName());
    protected static AndroidDriver driver;
    protected static AppiumDriverLocalService service;

    @BeforeSuite(alwaysRun = true)
    public void globalSetup() throws MalformedURLException {
        // Load config
        ConfigManager.load();
        
        String serverUrl = ConfigManager.get("appium.server.url", "http://127.0.0.1:4723/");
        boolean isLocal = Boolean.parseBoolean(ConfigManager.get("appium.server.local", "true"));
        
        // Start Appium server automatically only if running locally
        if (isLocal) {
            logger.info("Starting local Appium server...");
            AppiumServiceBuilder builder = new AppiumServiceBuilder()
                    .withIPAddress("127.0.0.1")
                    .usingPort(4723)
                    .withTimeout(Duration.ofSeconds(30));
            
            service = AppiumDriverLocalService.buildService(builder);
            service.start();
            serverUrl = service.getUrl().toString();
            logger.info("Appium server started at: " + serverUrl);
        } else {
            logger.info("Using remote Appium server at: " + serverUrl);
        }
        String deviceName = ConfigManager.get("deviceName", "Android Emulator");
        String platformVersion = ConfigManager.get("platformVersion", "");
        String udid = ConfigManager.get("udid", "");
        String automationName = ConfigManager.get("automationName", "UiAutomator2");
        boolean noReset = Boolean.parseBoolean(ConfigManager.get("noReset", "true"));
        String appPackage = ConfigManager.get("appPackage", "");
        String appActivity = ConfigManager.get("appActivity", "");

        UiAutomator2Options options = new UiAutomator2Options()
                .setPlatformName("Android")
                .setDeviceName(deviceName)
                .setAutomationName(automationName)
                .setNoReset(noReset)
                .setNewCommandTimeout(Duration.ofSeconds(120));
        if (!platformVersion.isEmpty()) options.setPlatformVersion(platformVersion);
        if (!udid.isEmpty()) options.setUdid(udid);
        if (!appPackage.isEmpty()) options.setAppPackage(appPackage);
        if (!appActivity.isEmpty()) options.setAppActivity(appActivity);
        
        // Add cloud provider credentials if available
        String bsUser = System.getProperty("browserstack.user");
        String bsKey = System.getProperty("browserstack.key");
        if (bsUser != null && bsKey != null) {
            options.setCapability("browserstack.user", bsUser);
            options.setCapability("browserstack.key", bsKey);
            logger.info("BrowserStack credentials configured");
        }
        
        String sauceUser = System.getProperty("sauce.username");
        String sauceKey = System.getProperty("sauce.accessKey");
        if (sauceUser != null && sauceKey != null) {
            options.setCapability("sauce:options", new java.util.HashMap<String, Object>() {{
                put("username", sauceUser);
                put("accessKey", sauceKey);
            }});
            logger.info("Sauce Labs credentials configured");
        }

        logger.info("Starting AndroidDriver session at: " + serverUrl);
        driver = new AndroidDriver(new URL(serverUrl), options);
        // Small implicit wait to make demo tests less flaky
        driver.manage().timeouts().implicitlyWait(Duration.ofSeconds(5));
    }

    @AfterSuite(alwaysRun = true)
    public void globalTeardown() {
        if (driver != null) {
            logger.info("Quitting AndroidDriver session");
            driver.quit();
        }
        
        // Stop Appium server (only if running locally)
        if (service != null && service.isRunning()) {
            logger.info("Stopping local Appium server");
            service.stop();
        }
    }
}
