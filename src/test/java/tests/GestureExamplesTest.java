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
 * GestureExamplesTest - Comprehensive examples of touch gestures using Appium
 * 
 * This test class demonstrates various mobile touch interactions using Appium's W3C Actions API
 * with PointerInput and Sequence classes. All gestures work with Appium java-client 8.x and Selenium 4.x.
 * 
 * Gesture Categories Covered:
 * 1. Swipe Gestures - Vertical and horizontal swipes for navigation
 * 2. Scroll Gestures - Scrolling to find elements in lists/containers
 * 3. Press Gestures - Long press and double tap interactions
 * 4. Drag & Drop - Moving elements from source to target
 * 5. Multi-Touch - Pinch to zoom using two fingers
 * 
 * Key Concepts:
 * - PointerInput: Represents a finger/touch input device
 * - Sequence: Chain of actions performed by a pointer
 * - Duration: Controls speed of gestures (ZERO = instant, ofMillis/ofSeconds = animated)
 * - Origin.viewport(): Uses screen coordinates (0,0 = top-left)
 * 
 * Action Flow Pattern:
 * 1. createPointerMove() - Move finger to position
 * 2. createPointerDown() - Touch down (press)
 * 3. createPointerMove() - Move while pressed (drag/swipe)
 * 4. createPointerUp() - Release touch
 */
public class GestureExamplesTest extends BaseTest {

    /**
     * Swipe vertically from bottom to top (scroll up).
     * 
     * Use Case: Scroll up in lists, feeds, or pages to see content above
     * 
     * Calculation:
     * - startX: Center of screen (horizontal middle)
     * - startY: 80% down screen (near bottom)
     * - endY: 20% down screen (near top)
     * 
     * Flow:
     * 1. Get screen dimensions
     * 2. Calculate start point (middle-bottom)
     * 3. Calculate end point (middle-top)
     * 4. Create finger pointer
     * 5. Build sequence: move → press → drag up → release
     * 6. Execute gesture with 600ms duration
     */
    @Test
    public void swipeUp() {
        // Get screen dimensions to calculate swipe coordinates
        Dimension size = driver.manage().window().getSize();
        int startX = size.width / 2;               // Center horizontally
        int startY = (int) (size.height * 0.8);    // Start near bottom (80%)
        int endY = (int) (size.height * 0.2);      // End near top (20%)

        // Create touch pointer (finger)
        PointerInput finger = new PointerInput(PointerInput.Kind.TOUCH, "finger");
        
        // Build swipe sequence
        Sequence swipe = new Sequence(finger, 1);
        swipe.addAction(finger.createPointerMove(Duration.ZERO, PointerInput.Origin.viewport(), startX, startY)); // Move to start
        swipe.addAction(finger.createPointerDown(PointerInput.MouseButton.LEFT.asArg()));                         // Touch down
        swipe.addAction(finger.createPointerMove(Duration.ofMillis(600), PointerInput.Origin.viewport(), startX, endY)); // Drag up
        swipe.addAction(finger.createPointerUp(PointerInput.MouseButton.LEFT.asArg()));                           // Release
        
        // Execute swipe
        driver.perform(Collections.singletonList(swipe));
    }

    /**
     * Swipe vertically from top to bottom (scroll down).
     * 
     * Use Case: Pull down to refresh, scroll down in content
     * 
     * Calculation:
     * - startY: 20% down screen (near top)
     * - endY: 80% down screen (near bottom)
     * - Reverse of swipeUp()
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
     * 
     * Use Case: Navigate to next screen/page, swipe carousel items, dismiss notifications
     * 
     * Calculation:
     * - startX: 80% across screen (near right edge)
     * - endX: 20% across screen (near left edge)
     * - y: Middle of screen vertically
     */
    @Test
    public void swipeLeft() {
        Dimension size = driver.manage().window().getSize();
        int startX = (int) (size.width * 0.8);   // Start near right edge
        int endX = (int) (size.width * 0.2);     // End near left edge
        int y = size.height / 2;                 // Center vertically

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
     * 
     * Use Case: Navigate to previous screen, open side menu, swipe back in carousels
     * 
     * Calculation:
     * - Reverse of swipeLeft()
     * - startX: 20% across (near left)
     * - endX: 80% across (near right)
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
     * 
     * Platform: Android only (UiAutomator2 driver)
     * 
     * Use Case: Find and scroll to specific element by text in scrollable containers
     * 
     * UIScrollable Parameters:
     * - scrollable(true): Find scrollable containers
     * - instance(0): Use first matching container
     * - scrollIntoView(): Scroll until target element is visible
     * 
     * Advantages:
     * - No coordinate calculations needed
     * - Automatically scrolls until element found
     * - Handles different screen sizes
     * 
     * Limitations:
     * - Android only (not available for iOS)
     * - Requires UiAutomator2 driver
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
     * 
     * Use Case: Scroll in specific RecyclerView/ListView, not entire screen
     * 
     * Flow:
     * 1. Find the scrollable container element (RecyclerView, ListView, ScrollView)
     * 2. Get container's location and size
     * 3. Calculate swipe coordinates within container bounds
     * 4. Perform swipe gesture inside the container
     * 
     * Benefits:
     * - More precise - only scrolls specific container
     * - Avoids accidentally scrolling other areas
     * - Better for complex layouts with multiple scrollable areas
     */
    @Test
    public void scrollInScrollableContainer() {
        // Find the scrollable container (e.g., a RecyclerView or ListView)
        WebElement scrollableElement = driver.findElement(AppiumBy.id("com.example.app:id/recycler_view"));
        
        // Get container's position and size on screen
        Point location = scrollableElement.getLocation();  // Top-left corner
        Dimension size = scrollableElement.getSize();      // Width and height
        
        // Calculate swipe coordinates relative to container
        int startX = location.x + size.width / 2;          // Center horizontally
        int startY = location.y + (int)(size.height * 0.8); // Near bottom of container
        int endY = location.y + (int)(size.height * 0.2);   // Near top of container

        // Create and execute swipe gesture within container bounds
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
     * 
     * Use Case: Context menus, delete mode, reorder lists, show tooltips
     * 
     * Flow:
     * 1. Find target element
     * 2. Calculate element's center point
     * 3. Move to center, press down, hold for 2 seconds, release
     * 
     * Key Difference from Regular Click:
     * - Regular click: down → immediate up
     * - Long press: down → pause (2 seconds) → up
     * 
     * Duration.ofSeconds(2) = Hold time before release
     */
    @Test
    public void longPress() {
        // Find element to long press
        WebElement element = driver.findElement(AppiumBy.id("com.example.app:id/button"));
        
        // Calculate center point of element
        Point location = element.getLocation();
        Dimension size = element.getSize();
        int x = location.x + size.width / 2;
        int y = location.y + size.height / 2;

        // Create long press sequence
        PointerInput finger = new PointerInput(PointerInput.Kind.TOUCH, "finger");
        Sequence longPress = new Sequence(finger, 1);
        longPress.addAction(finger.createPointerMove(Duration.ZERO, PointerInput.Origin.viewport(), x, y)); // Move to element
        longPress.addAction(finger.createPointerDown(PointerInput.MouseButton.LEFT.asArg()));              // Press down
        longPress.addAction(finger.createPointerMove(Duration.ofSeconds(2), PointerInput.Origin.viewport(), x, y)); // Hold for 2 seconds
        longPress.addAction(finger.createPointerUp(PointerInput.MouseButton.LEFT.asArg()));
        driver.perform(Collections.singletonList(longPress));
    }

    /**
     * Double tap on an element.
     * 
     * Use Case: Zoom in/out on images, like posts, select text
     * 
     * Flow:
     * 1. Find target element
     * 2. Calculate center point
     * 3. Perform two quick taps with 100ms pause between
     * 
     * Tap Sequence:
     * - First tap: move → down → up
     * - Pause: 100ms (short enough to register as double tap)
     * - Second tap: down → up
     * 
     * Important: 100ms pause is critical
     * - Too long: Registers as two separate taps
     * - Too short: May not register second tap
     */
    @Test
    public void doubleTap() {
        // Find element to double tap
        WebElement element = driver.findElement(AppiumBy.id("com.example.app:id/image"));
        
        // Calculate center point
        Point location = element.getLocation();
        Dimension size = element.getSize();
        int x = location.x + size.width / 2;
        int y = location.y + size.height / 2;

        // Create double tap sequence
        PointerInput finger = new PointerInput(PointerInput.Kind.TOUCH, "finger");
        Sequence doubleTap = new Sequence(finger, 1);
        
        // First tap
        doubleTap.addAction(finger.createPointerMove(Duration.ZERO, PointerInput.Origin.viewport(), x, y));
        doubleTap.addAction(finger.createPointerDown(PointerInput.MouseButton.LEFT.asArg()));
        doubleTap.addAction(finger.createPointerUp(PointerInput.MouseButton.LEFT.asArg()));
        
        // Short pause between taps (100ms)
        doubleTap.addAction(finger.createPointerMove(Duration.ofMillis(100), PointerInput.Origin.viewport(), x, y));
        
        // Second tap
        doubleTap.addAction(finger.createPointerDown(PointerInput.MouseButton.LEFT.asArg()));
        doubleTap.addAction(finger.createPointerUp(PointerInput.MouseButton.LEFT.asArg()));
        
        driver.perform(Collections.singletonList(doubleTap));
    }

    /**
     * Drag and drop from one element to another.
     * 
     * Use Case: Reorder lists, move items between containers, file management
     * 
     * Flow:
     * 1. Find source element (item to drag)
     * 2. Find target element (drop location)
     * 3. Get center points of both elements
     * 4. Press on source → drag to target → release
     * 
     * Key Points:
     * - Uses getElementCenter() helper to find exact center of elements
     * - 600ms drag duration makes movement visible and smooth
     * - Keeps finger down during entire drag motion
     */
    @Test
    public void dragAndDrop() {
        // Find source and target elements
        WebElement source = driver.findElement(AppiumBy.id("com.example.app:id/drag_source"));
        WebElement target = driver.findElement(AppiumBy.id("com.example.app:id/drop_target"));
        
        // Calculate center points
        Point sourceCenter = getElementCenter(source);
        Point targetCenter = getElementCenter(target);

        // Create drag and drop sequence
        PointerInput finger = new PointerInput(PointerInput.Kind.TOUCH, "finger");
        Sequence dragDrop = new Sequence(finger, 1);
        dragDrop.addAction(finger.createPointerMove(Duration.ZERO, PointerInput.Origin.viewport(), sourceCenter.x, sourceCenter.y)); // Move to source
        dragDrop.addAction(finger.createPointerDown(PointerInput.MouseButton.LEFT.asArg()));                                         // Press down on source
        dragDrop.addAction(finger.createPointerMove(Duration.ofMillis(600), PointerInput.Origin.viewport(), targetCenter.x, targetCenter.y)); // Drag to target
        dragDrop.addAction(finger.createPointerUp(PointerInput.MouseButton.LEFT.asArg()));                                           // Release on target
        driver.perform(Collections.singletonList(dragDrop));
    }

    /**
     * Pinch to zoom in (spread two fingers apart).
     * 
     * Use Case: Zoom in on maps, images, documents
     * 
     * Multi-Touch Concept:
     * - Simulates two fingers touching screen simultaneously
     * - Both fingers start close together
     * - Both fingers move apart (spread) at same time
     * - Creates zoom-in effect
     * 
     * Coordinate Calculation:
     * - Center point: Middle of screen
     * - Finger 1 starts: Center - 50px (left of center)
     * - Finger 2 starts: Center + 50px (right of center)
     * - Finger 1 ends: Center - 200px (far left)
     * - Finger 2 ends: Center + 200px (far right)
     * - Spread distance: 100px → 400px (4x spread = zoom in)
     * 
     * Flow:
     * 1. Calculate screen center
     * 2. Position two fingers close together (100px apart)
     * 3. Move both fingers outward simultaneously (400px apart)
     * 4. Execute both finger sequences together with driver.perform()
     * 
     * Note: For zoom OUT (pinch), reverse the coordinates:
     * - Start: fingers apart (200px from center)
     * - End: fingers together (50px from center)
     */
    @Test
    public void pinchZoomIn() {
        // Get screen dimensions
        Dimension size = driver.manage().window().getSize();
        int centerX = size.width / 2;
        int centerY = size.height / 2;
        
        // Start positions (fingers close together - 100px apart)
        int finger1StartX = centerX - 50;  // 50px left of center
        int finger2StartX = centerX + 50;  // 50px right of center
        
        // End positions (fingers spread apart - 400px apart)
        int finger1EndX = centerX - 200;   // 200px left of center
        int finger2EndX = centerX + 200;   // 200px right of center

        // Create two finger pointers
        PointerInput finger1 = new PointerInput(PointerInput.Kind.TOUCH, "finger1");
        PointerInput finger2 = new PointerInput(PointerInput.Kind.TOUCH, "finger2");
        
        // Finger 1 sequence: Start close → spread left
        Sequence finger1Seq = new Sequence(finger1, 1);
        finger1Seq.addAction(finger1.createPointerMove(Duration.ZERO, PointerInput.Origin.viewport(), finger1StartX, centerY));
        finger1Seq.addAction(finger1.createPointerDown(PointerInput.MouseButton.LEFT.asArg()));
        finger1Seq.addAction(finger1.createPointerMove(Duration.ofMillis(600), PointerInput.Origin.viewport(), finger1EndX, centerY));
        finger1Seq.addAction(finger1.createPointerUp(PointerInput.MouseButton.LEFT.asArg()));
        
        // Finger 2 sequence: Start close → spread right
        Sequence finger2Seq = new Sequence(finger2, 1);
        finger2Seq.addAction(finger2.createPointerMove(Duration.ZERO, PointerInput.Origin.viewport(), finger2StartX, centerY));
        finger2Seq.addAction(finger2.createPointerDown(PointerInput.MouseButton.LEFT.asArg()));
        finger2Seq.addAction(finger2.createPointerMove(Duration.ofMillis(600), PointerInput.Origin.viewport(), finger2EndX, centerY));
        finger2Seq.addAction(finger2.createPointerUp(PointerInput.MouseButton.LEFT.asArg()));
        
        // Execute both fingers simultaneously
        driver.perform(java.util.Arrays.asList(finger1Seq, finger2Seq));
    }

    /**
     * Helper method to get the center point of an element.
     * 
     * Purpose: Calculate exact center coordinates for precise touch interactions
     * 
     * Calculation:
     * - X coordinate: element.x + (element.width / 2)
     * - Y coordinate: element.y + (element.height / 2)
     * 
     * Why Center Point?
     * - Most reliable tap location
     * - Avoids edge cases where tap might miss element
     * - Works consistently across different element sizes
     * 
     * Used by: longPress(), doubleTap(), dragAndDrop()
     * 
     * @param element WebElement to find center of
     * @return Point object with x,y coordinates of center
     */
    private Point getElementCenter(WebElement element) {
        Point location = element.getLocation();  // Top-left corner (x, y)
        Dimension size = element.getSize();      // Width and height
        return new Point(
            location.x + size.width / 2,   // Center X
            location.y + size.height / 2   // Center Y
        );
    }
}
