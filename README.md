# Appium Java Android Test Framework

![Android Tests](https://github.com/mahi4317/appium/actions/workflows/android-tests.yml/badge.svg)
![Docker Tests](https://github.com/mahi4317/appium/actions/workflows/docker-tests.yml/badge.svg)
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

## Docker Execution

Run tests in isolated Docker containers with managed emulator:

### Prerequisites
- Docker Desktop installed and running

### Quick Start
```bash
# Run tests with one command
./run-docker-tests.sh
```

This will:
1. Build the test container
2. Start Android emulator in Docker
3. Start Appium server
4. Run all tests
5. Cleanup containers

### Manual Docker Commands

**Build test image:**
```bash
docker build -t appium-tests:latest .
```

**Start all services:**
```bash
docker-compose up -d
```

**Run tests:**
```bash
docker-compose run --rm appium-tests
```

**View emulator via web browser:**
```
http://localhost:6080
```

**Stop all services:**
```bash
docker-compose down
```

### Docker Configuration

Use `docker.properties` for Docker-specific settings:
```bash
mvn test -Denv=docker
```

### Benefits of Docker Execution
- ✅ Consistent environment across all machines
- ✅ No local Android SDK installation needed
- ✅ Isolated test runs
- ✅ Easy CI/CD integration
- ✅ Visual debugging via noVNC web interface

## CI/CD

This project supports multiple CI/CD platforms with comprehensive automation:

---

### 🚀 GitHub Actions (Cloud-Based CI/CD)

**What is GitHub Actions?**  
Automated CI/CD pipelines that run on GitHub's servers when you push code. No setup required - just push and tests run automatically!

**Three Workflow Files:**

#### 1. **`android-tests.yml`** - Native Android Emulator Testing
**Location:** `.github/workflows/android-tests.yml`

**What it does:**
- Runs tests on GitHub-hosted Ubuntu runner
- Uses real Android emulator (API 29, x86_64, Nexus 6)
- Hardware acceleration (KVM) for faster execution
- Automatic on every push/PR to main/develop

**Key Features:**
- ✅ No setup required - works out of the box
- ✅ Uses `reactivecircus/android-emulator-runner`
- ✅ Auto-installs JDK 17, Node.js 20, Appium
- ✅ Emulator boots in ~2-3 minutes
- ✅ Free for public repositories

**Execution Time:** ~5 minutes

```yaml
# Triggers automatically on:
- push to main/develop
- pull requests
- manual trigger (workflow_dispatch)
```

#### 2. **`docker-tests.yml`** - Containerized Testing
**Location:** `.github/workflows/docker-tests.yml`

**What it does:**
- Runs tests in Docker containers
- Uses `budtmo/docker-android` emulator image
- Exact same environment as local Docker execution
- Includes noVNC for visual debugging

**Key Features:**
- ✅ Fully containerized & isolated
- ✅ Consistent with local development
- ✅ Docker Buildx for efficient builds
- ✅ Automated container cleanup

**Execution Time:** ~7 minutes

```yaml
# Process:
1. Build test container (Dockerfile)
2. Start Android emulator container
3. Run tests: docker-compose run appium-tests
4. Upload results & cleanup
```

#### 3. **`browserstack-tests.yml`** - Cloud Device Testing
**Location:** `.github/workflows/browserstack-tests.yml`

**What it does:**
- Runs tests on real devices in BrowserStack cloud
- Fastest execution - no emulator startup needed
- Supports multiple device/OS combinations

**Key Features:**
- ✅ Tests on real physical devices
- ✅ Parallel execution possible
- ✅ No emulator management
- ❌ Requires BrowserStack subscription

**Execution Time:** ~2-3 minutes

**Setup Required:**
Go to GitHub repo → Settings → Secrets → Actions and add:
- `BROWSERSTACK_USER` - Your BrowserStack username
- `BROWSERSTACK_KEY` - Your BrowserStack access key

**Comparison:**

| Workflow | Environment | Speed | Cost | Best For |
|----------|-------------|-------|------|----------|
| `android-tests.yml` | Native emulator | ⚡⚡⚡ 5 min | Free | Quick validation |
| `docker-tests.yml` | Docker containers | ⚡⚡ 7 min | Free | Consistent env |
| `browserstack-tests.yml` | Real devices | ⚡⚡⚡⚡ 3 min | Paid | Production testing |

**View Results:**  
GitHub Repository → **Actions** tab → Click any workflow run

**Status Badges:**  
The badges at the top of this README show real-time status:
- ![Android Tests](https://github.com/mahi4317/appium/actions/workflows/android-tests.yml/badge.svg) ← Click to see workflow runs
- ![Docker Tests](https://github.com/mahi4317/appium/actions/workflows/docker-tests.yml/badge.svg)
- ![BrowserStack Tests](https://github.com/mahi4317/appium/actions/workflows/browserstack-tests.yml/badge.svg)

📖 **Detailed Guide:** [.github/CICD.md](.github/CICD.md)

---

### 🏗️ Jenkins (Self-Hosted CI/CD)

**What is Jenkins?**  
Open-source automation server that runs on your own infrastructure. Provides more control and flexibility than cloud CI/CD.

**Two Jenkinsfile Options:**

#### 1. **`Jenkinsfile`** - Full-Featured Pipeline

**Best for:** Organizations with dedicated Jenkins infrastructure

**Execution Modes:**
- ✅ **Local** - Runs on Jenkins agent with Android SDK & emulator
- ✅ **Docker** - Containerized execution
- ✅ **BrowserStack** - Cloud real devices
- ✅ **Sauce Labs** - Cloud emulators/devices

**Features:**
- Parameterized builds (choose mode via dropdown)
- Automatic Android SDK installation
- Emulator creation and management
- App installation automation
- Specific test class selection
- Comprehensive test reporting

**Agent Requirements:**
```groovy
tools {
    maven 'Maven 3.9.5'
    jdk 'JDK 17'
}
// Plus: Android SDK, Node.js, Appium (auto-installed)
```

**Pipeline Flow:**
```
1. Checkout code
2. Install Android SDK (if needed)
3. Install Node.js + Appium
4. Create & start emulator
5. Install calculator app
6. Run tests (local/docker/cloud)
7. Publish JUnit reports
8. Archive artifacts
9. Cleanup
```

#### 2. **`Jenkinsfile.docker`** - Simplified Pipeline

**Best for:** Quick setup, cloud Jenkins, containerized environments

**Execution Modes:**
- ✅ **Docker** - Containerized execution only
- ✅ **BrowserStack** - Cloud real devices

**Features:**
- Minimal setup (just Docker required)
- Runs inside Maven Docker container
- No local SDK installation needed
- Faster setup time
- Smaller resource footprint

**Agent:**
```groovy
agent {
    docker {
        image 'maven:3.9.5-eclipse-temurin-17'
    }
}
// No SDK needed - everything in containers!
```

**Pipeline Flow:**
```
1. Checkout code (inside Maven container)
2. Install docker-compose
3. Run: ./run-docker-tests.sh OR mvn test -Denv=browserstack
4. Publish results
5. Cleanup
```

**Which Jenkinsfile to Use?**

| Choose `Jenkinsfile` if: | Choose `Jenkinsfile.docker` if: |
|-------------------------|-------------------------------|
| Need all 4 execution modes | Docker/BrowserStack is enough |
| Have dedicated Jenkins server | Quick setup preferred |
| Want local emulator testing | Jenkins runs in containers/K8s |
| Need Sauce Labs support | Minimal infrastructure |
| Enterprise environment | Startup/small team |

**Setup Instructions:**

1. **Install Jenkins:**
   ```bash
   # Docker (recommended)
   docker run -d -p 8080:8080 -v jenkins_home:/var/jenkins_home jenkins/jenkins:lts
   
   # macOS
   brew install jenkins-lts && brew services start jenkins-lts
   ```

2. **Create Pipeline Job:**
   - New Item → Multibranch Pipeline
   - Add Git source: `https://github.com/mahi4317/appium.git`
   - Script Path: `Jenkinsfile` or `Jenkinsfile.docker`
   - Save (Jenkins auto-detects branches)

3. **Configure Credentials:**
   - Manage Jenkins → Credentials → Add
   - Add `browserstack-user` (Secret Text)
   - Add `browserstack-key` (Secret Text)
   - Add `sauce-username` & `sauce-accesskey` (if using Sauce Labs)

4. **Run Build:**
   - Click "Build with Parameters"
   - Select EXECUTION_MODE (local/docker/browserstack/saucelabs)
   - Optional: Specify TEST_CLASS
   - Click "Build"

**Jenkins Features:**
- ✅ Parameterized builds with dropdown selection
- ✅ Automated environment setup
- ✅ JUnit test report publishing
- ✅ Artifact archiving (test reports)
- ✅ Automatic cleanup (emulator, containers)
- ✅ Email/Slack notifications (configurable)
- ✅ Cron scheduling for nightly builds
- ✅ Webhook triggers on git push

**View Results:**
- Build → Test Results (JUnit reports)
- Build → Console Output (full logs)
- Build → Build Artifacts (test reports)

📖 **Complete Setup Guide:** [.jenkins/README.md](.jenkins/README.md)

---

### 📊 CI/CD Platform Comparison

| Feature | GitHub Actions | Jenkins |
|---------|---------------|---------|
| **Hosting** | GitHub's servers (cloud) | Your own server/agent |
| **Setup** | Zero - just push code | Manual installation |
| **Cost** | Free (public repos) | Free (self-hosted) |
| **Triggers** | Auto on push/PR | Webhook, cron, manual |
| **Configuration** | YAML files | Groovy Jenkinsfile |
| **Execution** | 3 separate workflows | 1 parameterized pipeline |
| **Best For** | Open source, quick start | Enterprise, full control |
| **Customization** | Limited | Extensive |

**Why Both?**
- **GitHub Actions** → Automatic validation on every push (no maintenance)
- **Jenkins** → Advanced scheduling, private network testing, custom workflows

Both platforms use the **same test code** - just different execution environments! 🎯
