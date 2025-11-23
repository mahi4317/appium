# Appium Java Android Test Framework

A minimal, ready-to-run Appium + TestNG framework for Android testing.

## Prerequisites
- Java 17 (or compatible JDK)
- Maven 3.6+
- Node.js + Appium Server v3
- Android SDK Platform-Tools (adb) installed and on PATH
- Real device or emulator with Developer Mode enabled

## Install Appium (macOS)
```bash
npm install -g appium
appium driver install uiautomator2
```

## Install Android SDK on macOS
```bash
# Install via Homebrew
brew install --cask android-commandlinetools

# Accept licenses
sdkmanager --licenses

# Install required components
sdkmanager "platform-tools" "platforms;android-34" "build-tools;34.0.0" "emulator" "system-images;android-34;google_apis;arm64-v8a"
```

## Create and Start Android Emulator
```bash
# Create AVD (Android Virtual Device)
avdmanager create avd -n Pixel_6_API_34 -k "system-images;android-34;google_apis;arm64-v8a" -d pixel_6

# List available AVDs
emulator -list-avds

# Start emulator
emulator -avd Pixel_6_API_34 &

# Verify device is connected
adb devices
```

## Install Calculator App (for CalculatorTest)
```bash
# Download Simple Mobile Tools Calculator
curl -L -o /tmp/calculator.apk "https://f-droid.org/repo/com.simplemobiletools.calculator_38.apk"

# Install on emulator
adb install /tmp/calculator.apk

# Verify installation
adb shell pm list packages | grep calculator
```

## Project Structure
```
appium-java/
├─ pom.xml
├─ src/
│  ├─ main/java/com/appium/config/ConfigManager.java
│  ├─ test/java/
│  │  ├─ base/BaseTest.java
│  │  └─ tests/
│  │     ├─ LaunchSessionTest.java
│  │     └─ CalculatorTest.java
│  └─ test/resources/
│     ├─ config/android.properties
│     └─ testng.xml
└─ README.md
```

## Configure
Edit `src/test/resources/config/android.properties`:
```properties
appium.server.url=http://127.0.0.1:4723/
deviceName=Android Emulator
platformVersion=
udid=
noReset=true
appPackage=
appActivity=
```
Optionally pass `-Denv=android` to switch environments.

## Run

**Note:** The framework automatically starts and stops the Appium server using `AppiumServiceBuilder`. No manual server startup is required.

1. Ensure the emulator is running:
   ```bash
   adb devices
   ```

2. Run all tests:
   ```bash
   mvn clean test
   ```

3. Run a specific test:
   ```bash
   mvn test -Dtest=LaunchSessionTest
   mvn test -Dtest=CalculatorTest
   ```

## What the sample test does
`LaunchSessionTest` starts an Android session and asserts a non-null session id. You can expand from here by adding page objects and flows.

`CalculatorTest` opens the built-in Calculator app (tries Google and AOSP variants), performs 2 + 3, and asserts the result is 5. This test doesn't require setting `appPackage`/`appActivity` in properties because it activates the app at runtime.

## Troubleshooting

### Emulator Management
- **Stop emulator:**
  ```bash
  adb -s emulator-5554 emu kill
  ```
- **Check if emulator is running:**
  ```bash
  adb devices
  ```
- **List available AVDs:**
  ```bash
  emulator -list-avds
  ```

### Common Issues
- **No device found:** Check `adb devices` shows your device/emulator
- **Chromedriver issues (web tests):** Install matching drivers or use `appium --use-plugins=relaxed-security` and set `chromedriverExecutable`
- **Calculator app not found:** Install a calculator app on your emulator:
  ```bash
  adb install <path-to-calculator.apk>
  ```
- `adb` or `appium` not recognized: Make sure they are installed and added to PATH. Open a NEW PowerShell after updating PATH.
