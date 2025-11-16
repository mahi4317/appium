package tests;

import io.appium.java_client.android.AndroidDriver;
import org.testng.Assert;
import org.testng.annotations.Test;

import base.BaseTest;

public class LaunchSessionTest extends BaseTest {
    @Test
    public void canStartAndroidSession() {
        Assert.assertNotNull(((AndroidDriver)driver).getSessionId(), "Session id should not be null");
    }
}
