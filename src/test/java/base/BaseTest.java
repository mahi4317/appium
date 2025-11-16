package base;

import com.appium.config.ConfigManager;
import io.appium.java_client.android.AndroidDriver;
import io.appium.java_client.android.options.UiAutomator2Options;
import org.testng.annotations.AfterSuite;
import org.testng.annotations.BeforeSuite;

import java.net.MalformedURLException;
import java.net.URL;
import java.time.Duration;
import java.util.logging.Logger;

public class BaseTest {
    protected static final Logger logger = Logger.getLogger(BaseTest.class.getName());
    protected static AndroidDriver driver;

    @BeforeSuite(alwaysRun = true)
    public void globalSetup() throws MalformedURLException {
        // Load config
        ConfigManager.load();
        String serverUrl = ConfigManager.get("appium.server.url", "http://127.0.0.1:4723/");
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
    }
}
