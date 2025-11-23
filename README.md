# Appium Java Android Test Framework

![Android Tests](https://github.com/mahi4317/appium/actions/workflows/android-tests.yml/badge.svg)
![BrowserStack Tests](https://github.com/mahi4317/appium/actions/workflows/browserstack-tests.yml/badge.svg)

A minimal, ready-to-run Appium + TestNG framework for Android testing with full CI/CD automation.

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

**Note:** The framework can automatically start the emulator (enabled by default). Manual setup is only needed once.

```bash
# Create AVD (Android Virtual Device) - one time setup
avdmanager create avd -n Pixel_6_API_34 -k "system-images;android-34;google_apis;arm64-v8a" -d pixel_6

# List available AVDs
emulator -list-avds

# Manual start (optional - framework auto-starts by default)
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

### Local Execution (Default)
Edit `src/test/resources/config/android.properties`:
```properties
appium.server.local=true
appium.server.url=http://127.0.0.1:4723/

# Emulator auto-start (enabled by default)
emulator.auto.start=true
emulator.avd.name=Pixel_6_API_34

deviceName=Android Emulator
platformVersion=
udid=
noReset=true
```

### Remote Execution (Cloud or Remote Server)
The framework supports multiple remote execution options:

**BrowserStack** (`src/test/resources/config/browserstack.properties`):
```properties
appium.server.local=false
appium.server.url=https://hub-cloud.browserstack.com/wd/hub
deviceName=Samsung Galaxy S23
platformVersion=13.0
```

**Sauce Labs** (`src/test/resources/config/saucelabs.properties`):
```properties
appium.server.local=false
appium.server.url=https://ondemand.us-west-1.saucelabs.com/wd/hub
deviceName=Google Pixel 6 GoogleAPI Emulator
platformVersion=12.0
```

**Custom Remote Server** (`src/test/resources/config/remote.properties`):
```properties
appium.server.local=false
appium.server.url=http://192.168.1.100:4723/
deviceName=Remote Android Device
```

Switch environments using `-Denv=<config_name>`

## Run

**Note:** The framework includes full automation for local execution:
- ✅ **Automatic emulator startup** - Starts emulator if not running (configurable)
- ✅ **Automatic Appium server management** - Starts and stops server automatically
- ✅ **Remote execution support** - Works with BrowserStack, Sauce Labs, and custom servers

### Local Execution (Default)

**Fully automated** - Just run the tests:
```bash
mvn clean test
```

The framework will automatically:
1. Check if emulator is running, start it if needed
2. Wait for emulator to boot completely
3. Start Appium server
4. Run your tests
5. Stop Appium server after tests complete

**Run a specific test:**
```bash
mvn test -Dtest=CalculatorTest
```

**Disable emulator auto-start** (if you prefer manual control):
Set `emulator.auto.start=false` in `android.properties`

### Remote Execution

**BrowserStack:**
```bash
mvn test -Denv=browserstack \
  -Dbrowserstack.user=YOUR_USERNAME \
  -Dbrowserstack.key=YOUR_ACCESS_KEY
```

**Sauce Labs:**
```bash
mvn test -Denv=saucelabs \
  -Dsauce.username=YOUR_USERNAME \
  -Dsauce.accessKey=YOUR_ACCESS_KEY
```

**Custom Remote Server:**
```bash
mvn test -Denv=remote
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

## CI/CD

This project includes GitHub Actions workflows for automated testing:

- **Android Tests** - Runs on macOS with Android emulator (daily + on push/PR)
- **BrowserStack Tests** - Runs on real devices via BrowserStack cloud

See [CI/CD Documentation](.github/CICD.md) for setup instructions.

**Add BrowserStack credentials:**
Go to Settings → Secrets → Actions and add:
- `BROWSERSTACK_USER`
- `BROWSERSTACK_KEY`
