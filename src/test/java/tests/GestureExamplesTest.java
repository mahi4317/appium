package tests;

import base.BaseTest;
import io.appium.java_client.AppiumBy;
import org.openqa.selenium.Dimension;
import org.openqa.selenium.Point;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.interactions.PointerInput;
import org.openqa.selenium.interactions.Sequence;
import org.testng.annotations.Test;

import java.time.Duration;
import java.util.Collections;

/**
 * Examples of swipe, scroll, and other touch gestures using Appium.
 * These methods work with Appium java-client 8.x and Selenium 4.x.
 */
public class GestureExamplesTest extends BaseTest {

    /**
     * Swipe vertically from bottom to top (scroll up).
     */
    @Test
    public void swipeUp() {
        Dimension size = driver.manage().window().getSize();
        int startX = size.width / 2;
        int startY = (int) (size.height * 0.8);  // Start near bottom
        int endY = (int) (size.height * 0.2);    // End near top

        PointerInput finger = new PointerInput(PointerInput.Kind.TOUCH, "finger");
        Sequence swipe = new Sequence(finger, 1);
        swipe.addAction(finger.createPointerMove(Duration.ZERO, PointerInput.Origin.viewport(), startX, startY));
        swipe.addAction(finger.createPointerDown(PointerInput.MouseButton.LEFT.asArg()));
        swipe.addAction(finger.createPointerMove(Duration.ofMillis(600), PointerInput.Origin.viewport(), startX, endY));
        swipe.addAction(finger.createPointerUp(PointerInput.MouseButton.LEFT.asArg()));
        driver.perform(Collections.singletonList(swipe));
    }

    /**
     * Swipe vertically from top to bottom (scroll down).
     */
    @Test
    public void swipeDown() {
        Dimension size = driver.manage().window().getSize();
        int startX = size.width / 2;
        int startY = (int) (size.height * 0.2);  // Start near top
        int endY = (int) (size.height * 0.8);    // End near bottom

        PointerInput finger = new PointerInput(PointerInput.Kind.TOUCH, "finger");
        Sequence swipe = new Sequence(finger, 1);
        swipe.addAction(finger.createPointerMove(Duration.ZERO, PointerInput.Origin.viewport(), startX, startY));
        swipe.addAction(finger.createPointerDown(PointerInput.MouseButton.LEFT.asArg()));
        swipe.addAction(finger.createPointerMove(Duration.ofMillis(600), PointerInput.Origin.viewport(), startX, endY));
        swipe.addAction(finger.createPointerUp(PointerInput.MouseButton.LEFT.asArg()));
        driver.perform(Collections.singletonList(swipe));
    }

    /**
     * Swipe horizontally from right to left.
     */
    @Test
    public void swipeLeft() {
        Dimension size = driver.manage().window().getSize();
        int startX = (int) (size.width * 0.8);   // Start near right edge
        int endX = (int) (size.width * 0.2);     // End near left edge
        int y = size.height / 2;

        PointerInput finger = new PointerInput(PointerInput.Kind.TOUCH, "finger");
        Sequence swipe = new Sequence(finger, 1);
        swipe.addAction(finger.createPointerMove(Duration.ZERO, PointerInput.Origin.viewport(), startX, y));
        swipe.addAction(finger.createPointerDown(PointerInput.MouseButton.LEFT.asArg()));
        swipe.addAction(finger.createPointerMove(Duration.ofMillis(600), PointerInput.Origin.viewport(), endX, y));
        swipe.addAction(finger.createPointerUp(PointerInput.MouseButton.LEFT.asArg()));
        driver.perform(Collections.singletonList(swipe));
    }

    /**
     * Swipe horizontally from left to right.
     */
    @Test
    public void swipeRight() {
        Dimension size = driver.manage().window().getSize();
        int startX = (int) (size.width * 0.2);   // Start near left edge
        int endX = (int) (size.width * 0.8);     // End near right edge
        int y = size.height / 2;

        PointerInput finger = new PointerInput(PointerInput.Kind.TOUCH, "finger");
        Sequence swipe = new Sequence(finger, 1);
        swipe.addAction(finger.createPointerMove(Duration.ZERO, PointerInput.Origin.viewport(), startX, y));
        swipe.addAction(finger.createPointerDown(PointerInput.MouseButton.LEFT.asArg()));
        swipe.addAction(finger.createPointerMove(Duration.ofMillis(600), PointerInput.Origin.viewport(), endX, y));
        swipe.addAction(finger.createPointerUp(PointerInput.MouseButton.LEFT.asArg()));
        driver.perform(Collections.singletonList(swipe));
    }

    /**
     * Scroll to find an element using UiAutomator2 UIScrollable.
     * Works with Android UiAutomator2 driver.
     */
    @Test
    public void scrollToElementUsingUiAutomator() {
        // Scroll to an element with text "Settings" in a scrollable view
        driver.findElement(AppiumBy.androidUIAutomator(
            "new UiScrollable(new UiSelector().scrollable(true).instance(0))" +
            ".scrollIntoView(new UiSelector().text(\"Settings\").instance(0))"
        ));
    }

    /**
     * Scroll within a specific scrollable element until a target element is visible.
     */
    @Test
    public void scrollInScrollableContainer() {
        // Find the scrollable container (e.g., a RecyclerView or ListView)
        WebElement scrollableElement = driver.findElement(AppiumBy.id("com.example.app:id/recycler_view"));
        
        // Perform swipe within the element's bounds
        Point location = scrollableElement.getLocation();
        Dimension size = scrollableElement.getSize();
        
        int startX = location.x + size.width / 2;
        int startY = location.y + (int)(size.height * 0.8);
        int endY = location.y + (int)(size.height * 0.2);

        PointerInput finger = new PointerInput(PointerInput.Kind.TOUCH, "finger");
        Sequence swipe = new Sequence(finger, 1);
        swipe.addAction(finger.createPointerMove(Duration.ZERO, PointerInput.Origin.viewport(), startX, startY));
        swipe.addAction(finger.createPointerDown(PointerInput.MouseButton.LEFT.asArg()));
        swipe.addAction(finger.createPointerMove(Duration.ofMillis(600), PointerInput.Origin.viewport(), startX, endY));
        swipe.addAction(finger.createPointerUp(PointerInput.MouseButton.LEFT.asArg()));
        driver.perform(Collections.singletonList(swipe));
    }

    /**
     * Long press on an element.
     */
    @Test
    public void longPress() {
        WebElement element = driver.findElement(AppiumBy.id("com.example.app:id/button"));
        Point location = element.getLocation();
        Dimension size = element.getSize();
        int x = location.x + size.width / 2;
        int y = location.y + size.height / 2;

        PointerInput finger = new PointerInput(PointerInput.Kind.TOUCH, "finger");
        Sequence longPress = new Sequence(finger, 1);
        longPress.addAction(finger.createPointerMove(Duration.ZERO, PointerInput.Origin.viewport(), x, y));
        longPress.addAction(finger.createPointerDown(PointerInput.MouseButton.LEFT.asArg()));
        longPress.addAction(finger.createPointerMove(Duration.ofSeconds(2), PointerInput.Origin.viewport(), x, y));
        longPress.addAction(finger.createPointerUp(PointerInput.MouseButton.LEFT.asArg()));
        driver.perform(Collections.singletonList(longPress));
    }

    /**
     * Double tap on an element.
     */
    @Test
    public void doubleTap() {
        WebElement element = driver.findElement(AppiumBy.id("com.example.app:id/image"));
        Point location = element.getLocation();
        Dimension size = element.getSize();
        int x = location.x + size.width / 2;
        int y = location.y + size.height / 2;

        PointerInput finger = new PointerInput(PointerInput.Kind.TOUCH, "finger");
        Sequence doubleTap = new Sequence(finger, 1);
        
        // First tap
        doubleTap.addAction(finger.createPointerMove(Duration.ZERO, PointerInput.Origin.viewport(), x, y));
        doubleTap.addAction(finger.createPointerDown(PointerInput.MouseButton.LEFT.asArg()));
        doubleTap.addAction(finger.createPointerUp(PointerInput.MouseButton.LEFT.asArg()));
        
        // Short pause
        doubleTap.addAction(finger.createPointerMove(Duration.ofMillis(100), PointerInput.Origin.viewport(), x, y));
        
        // Second tap
        doubleTap.addAction(finger.createPointerDown(PointerInput.MouseButton.LEFT.asArg()));
        doubleTap.addAction(finger.createPointerUp(PointerInput.MouseButton.LEFT.asArg()));
        
        driver.perform(Collections.singletonList(doubleTap));
    }

    /**
     * Drag and drop from one element to another.
     */
    @Test
    public void dragAndDrop() {
        WebElement source = driver.findElement(AppiumBy.id("com.example.app:id/drag_source"));
        WebElement target = driver.findElement(AppiumBy.id("com.example.app:id/drop_target"));
        
        Point sourceCenter = getElementCenter(source);
        Point targetCenter = getElementCenter(target);

        PointerInput finger = new PointerInput(PointerInput.Kind.TOUCH, "finger");
        Sequence dragDrop = new Sequence(finger, 1);
        dragDrop.addAction(finger.createPointerMove(Duration.ZERO, PointerInput.Origin.viewport(), sourceCenter.x, sourceCenter.y));
        dragDrop.addAction(finger.createPointerDown(PointerInput.MouseButton.LEFT.asArg()));
        dragDrop.addAction(finger.createPointerMove(Duration.ofMillis(600), PointerInput.Origin.viewport(), targetCenter.x, targetCenter.y));
        dragDrop.addAction(finger.createPointerUp(PointerInput.MouseButton.LEFT.asArg()));
        driver.perform(Collections.singletonList(dragDrop));
    }

    /**
     * Pinch to zoom in (spread two fingers apart).
     */
    @Test
    public void pinchZoomIn() {
        Dimension size = driver.manage().window().getSize();
        int centerX = size.width / 2;
        int centerY = size.height / 2;
        
        // Start positions (fingers close together)
        int finger1StartX = centerX - 50;
        int finger2StartX = centerX + 50;
        
        // End positions (fingers spread apart)
        int finger1EndX = centerX - 200;
        int finger2EndX = centerX + 200;

        PointerInput finger1 = new PointerInput(PointerInput.Kind.TOUCH, "finger1");
        PointerInput finger2 = new PointerInput(PointerInput.Kind.TOUCH, "finger2");
        
        Sequence finger1Seq = new Sequence(finger1, 1);
        finger1Seq.addAction(finger1.createPointerMove(Duration.ZERO, PointerInput.Origin.viewport(), finger1StartX, centerY));
        finger1Seq.addAction(finger1.createPointerDown(PointerInput.MouseButton.LEFT.asArg()));
        finger1Seq.addAction(finger1.createPointerMove(Duration.ofMillis(600), PointerInput.Origin.viewport(), finger1EndX, centerY));
        finger1Seq.addAction(finger1.createPointerUp(PointerInput.MouseButton.LEFT.asArg()));
        
        Sequence finger2Seq = new Sequence(finger2, 1);
        finger2Seq.addAction(finger2.createPointerMove(Duration.ZERO, PointerInput.Origin.viewport(), finger2StartX, centerY));
        finger2Seq.addAction(finger2.createPointerDown(PointerInput.MouseButton.LEFT.asArg()));
        finger2Seq.addAction(finger2.createPointerMove(Duration.ofMillis(600), PointerInput.Origin.viewport(), finger2EndX, centerY));
        finger2Seq.addAction(finger2.createPointerUp(PointerInput.MouseButton.LEFT.asArg()));
        
        driver.perform(java.util.Arrays.asList(finger1Seq, finger2Seq));
    }

    /**
     * Helper method to get the center point of an element.
     */
    private Point getElementCenter(WebElement element) {
        Point location = element.getLocation();
        Dimension size = element.getSize();
        return new Point(location.x + size.width / 2, location.y + size.height / 2);
    }
}
