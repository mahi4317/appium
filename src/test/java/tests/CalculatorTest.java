package tests;

import base.BaseTest;
import io.appium.java_client.AppiumBy;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.testng.Assert;
import org.testng.SkipException;
import org.testng.annotations.Test;

public class CalculatorTest extends BaseTest {

    private boolean activateCalculator() {
        // Try common calculator packages
        String[] pkgs = new String[]{
                "com.google.android.calculator",
                "com.android.calculator2"
        };
        for (String pkg : pkgs) {
            try {
                driver.activateApp(pkg);
                return true;
            } catch (Exception ignored) { }
        }
        return false;
    }

    private WebElement findAny(By... locators) {
        for (By by : locators) {
            try {
                WebElement el = driver.findElement(by);
                if (el != null) return el;
            } catch (Exception ignored) { }
        }
        throw new RuntimeException("Element not found using any provided locator");
    }

    @Test
    public void addTwoNumbers() {
        if (!activateCalculator()) {
            throw new SkipException("No Calculator app found (tried Google and AOSP). Install one to run this test.");
        }

    // Build candidate locators using both package names
        By twoGoogle = AppiumBy.id("com.google.android.calculator:id/digit_2");
        By twoAosp = AppiumBy.id("com.android.calculator2:id/digit_2");
        By threeGoogle = AppiumBy.id("com.google.android.calculator:id/digit_3");
        By threeAosp = AppiumBy.id("com.android.calculator2:id/digit_3");
        By plusGoogle = AppiumBy.id("com.google.android.calculator:id/op_add");
        By plusAosp = AppiumBy.id("com.android.calculator2:id/op_add");
        By equalsGoogle = AppiumBy.id("com.google.android.calculator:id/eq");
        By equalsAosp = AppiumBy.id("com.android.calculator2:id/eq");
        By resultGoogle = AppiumBy.id("com.google.android.calculator:id/result_final");
        By resultGoogleAlt = AppiumBy.id("com.google.android.calculator:id/result");
        By resultAosp = AppiumBy.id("com.android.calculator2:id/result");

        // 2 + 3 =
        findAny(twoGoogle, twoAosp).click();
        findAny(plusGoogle, plusAosp).click();
        findAny(threeGoogle, threeAosp).click();
        findAny(equalsGoogle, equalsAosp).click();

        String resultText = findAny(resultGoogle, resultGoogleAlt, resultAosp).getText();
        // Extract digits only
        String digits = resultText.replaceAll("[^0-9]", "");
        Assert.assertEquals(digits, "5", "Expected result of 2+3 to be 5 but was: " + resultText);
    }
}
