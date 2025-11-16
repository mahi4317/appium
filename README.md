# Appium Java Android Test Framework

A minimal, ready-to-run Appium + TestNG framework for Android testing.

## Prerequisites
- Java 16 (or compatible JDK)
- Maven 3.6+
- Node.js + Appium Server v2
- Android SDK Platform-Tools (adb) installed and on PATH
- Real device or emulator with Developer Mode enabled

## Install Appium
```powershell
npm install -g appium
appium driver install uiautomator2
```

## Install Android Platform-Tools (adb) on Windows
1. Download the latest "SDK Platform-Tools for Windows" from Google:
	https://developer.android.com/tools/releases/platform-tools
2. Extract the zip (e.g., to `C:\Android\platform-tools`).
3. Add that folder to your PATH (Windows Settings → System → About → Advanced system settings → Environment Variables → Path → New).
4. Verify in a new PowerShell:
```powershell
adb version
```
You should also enable Developer Options + USB debugging on a real device, or start an emulator via Android Studio.

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
Start Appium server in a terminal:
```powershell
appium
```
Run tests from the project root:
```powershell
mvn clean test
```
Or run a single class:
```powershell
mvn test -Dtest=LaunchSessionTest
mvn test -Dtest=CalculatorTest
```

## What the sample test does
`LaunchSessionTest` starts an Android session and asserts a non-null session id. You can expand from here by adding page objects and flows.

`CalculatorTest` opens the built-in Calculator app (tries Google and AOSP variants), performs 2 + 3, and asserts the result is 5. This test doesn't require setting `appPackage`/`appActivity` in properties because it activates the app at runtime.

## Troubleshooting
- ECONNREFUSED: Ensure Appium server is running at `appium.server.url`.
- No device found: Check `adb devices` shows your device/emulator.
- Chromedriver issues (web tests): install matching drivers or use `appium --use-plugins=relaxed-security` and set `chromedriverExecutable`.
- `adb` or `appium` not recognized: Make sure they are installed and added to PATH. Open a NEW PowerShell after updating PATH.
