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

**What is "Jenkins Docker"?**  
This Jenkinsfile runs the **entire pipeline inside a Docker container**. Instead of installing tools on the Jenkins agent, the pipeline executes inside a pre-built Maven container that already has Java 17 and Maven installed.

**How it Works:**
```groovy
agent {
    docker {
        image 'maven:3.9.5-eclipse-temurin-17'  // Runs inside this container
        args '-v /var/run/docker.sock:/var/run/docker.sock'  // Mount Docker socket
    }
}
```

**The Magic:**
1. Jenkins pulls `maven:3.9.5-eclipse-temurin-17` image
2. Your pipeline code runs **inside** this container
3. Container has access to host's Docker (via mounted socket)
4. Test containers can be launched from inside the Maven container
5. Everything cleans up automatically when pipeline completes

**Visual Flow:**
```
Jenkins Server
    └── Jenkins Agent (any OS)
        └── Docker Engine
            └── Maven Container (Pipeline runs here!)
                ├── Your test code
                ├── Maven commands
                └── Can launch Docker containers:
                    ├── android-emulator container
                    └── appium-tests container
```

**Execution Modes:**
- ✅ **Docker** - Containerized execution only
- ✅ **BrowserStack** - Cloud real devices

**Features:**
- **Minimal setup** - Just Docker required on Jenkins agent
- **Runs inside Maven Docker container** - No Maven/Java installation needed
- **No local SDK installation needed** - Everything containerized
- **Faster setup time** - Skip SDK downloads (~10GB saved)
- **Smaller resource footprint** - Only containers running during tests
- **Portable** - Same everywhere Docker runs

**Agent:**
```groovy
agent {
    docker {
        image 'maven:3.9.5-eclipse-temurin-17'
        args '-v /var/run/docker.sock:/var/run/docker.sock -v $HOME/.m2:/root/.m2'
        //    └─ Access host Docker                      └─ Cache Maven dependencies
    }
}
// No SDK needed - everything in containers!
```

**Why Mount Docker Socket?**  
`-v /var/run/docker.sock:/var/run/docker.sock` allows the Maven container to control the host's Docker daemon. This enables "Docker-in-Docker" - launching test containers from inside the pipeline container.

**Why Mount .m2 Directory?**  
`-v $HOME/.m2:/root/.m2` caches Maven dependencies between builds, so you don't re-download them every time.

**Pipeline Flow:**
```
1. Jenkins pulls maven:3.9.5-eclipse-temurin-17 image
2. Starts Maven container with your code mounted
3. Checkout code (inside Maven container)
4. Install docker-compose (in container)
5. Launch test execution:
   OPTION A (Docker mode):
     → ./run-docker-tests.sh
     → Launches android-emulator + appium-tests containers
     → Tests run in containers
   OPTION B (BrowserStack mode):
     → mvn test -Denv=browserstack
     → Tests run on BrowserStack cloud
6. Publish JUnit results
7. Cleanup: Stop containers, remove Maven container
```

**Real-World Example:**

**Traditional Jenkinsfile (Heavy):**
```groovy
// Requires on Jenkins agent:
- Java 17 installed
- Maven installed  
- Android SDK installed (~10GB)
- Node.js installed
- Appium installed
- Emulator setup
Total setup: 30+ minutes, 15GB+ disk
```

**Jenkinsfile.docker (Light):**
```groovy
// Requires on Jenkins agent:
- Docker only
Total setup: 2 minutes, minimal disk

// Everything else runs in containers!
```

**When to Use `Jenkinsfile.docker`:**

✅ **Use it if:**
- Your Jenkins runs in Kubernetes/cloud
- You want minimal Jenkins agent setup
- Your team uses Docker for local development
- You prefer consistency (same env everywhere)
- Multiple projects share same Jenkins agents
- You don't need local emulator mode

❌ **Don't use it if:**
- You need local emulator testing on Jenkins agent
- Docker isn't available on your Jenkins agents
- You need Sauce Labs support (use full `Jenkinsfile`)
- Your network blocks Docker Hub access

**Comparison: Full vs Docker Jenkinsfile**

| Aspect | `Jenkinsfile` | `Jenkinsfile.docker` |
|--------|--------------|---------------------|
| **Pipeline Runs** | On Jenkins agent | Inside Docker container |
| **Java/Maven** | Installed on agent | In container (pre-installed) |
| **Android SDK** | Installed on agent (~10GB) | Not needed |
| **Setup Time** | 10-30 min | 2-5 min |
| **Disk Space** | 15GB+ | 2GB (containers) |
| **Execution Modes** | 4 (local/Docker/BS/SL) | 2 (Docker/BS) |
| **Best For** | Full control | Quick setup |

**Example Build Output:**

```groovy
[Pipeline] Start of Pipeline
[Pipeline] node
Running on Jenkins-Agent
[Pipeline] {
[Pipeline] docker.image('maven:3.9.5-eclipse-temurin-17').inside
  Pulling maven:3.9.5-eclipse-temurin-17 ✓
  Starting container 8a3f2b1... ✓
  
  [Pipeline] Inside container: 8a3f2b1
  [Pipeline] stage('Checkout')
  [Pipeline] checkout scm ✓
  
  [Pipeline] stage('Run Tests - Docker')
  $ docker-compose up -d android-emulator
  $ docker-compose run --rm appium-tests
    Running: mvn clean test -Denv=docker
    Tests run: 2, Failures: 0, Skipped: 0 ✓
  
  [Pipeline] junit
  Test Results: 2 passed ✓
  
  [Pipeline] Stopping container 8a3f2b1 ✓
[Pipeline] End of Pipeline
```

**Architecture Diagram:**

```
┌─────────────────────────────────────────┐
│         Jenkins Server                  │
├─────────────────────────────────────────┤
│  ┌──────────────────────────────────┐   │
│  │   Jenkins Agent (Host)           │   │
│  │   ┌──────────────────────────┐   │   │
│  │   │  Docker Engine           │   │   │
│  │   │  ┌────────────────────┐  │   │   │
│  │   │  │ Maven Container    │  │   │   │
│  │   │  │ (Pipeline runs)    │  │   │   │
│  │   │  │ ┌────────────────┐ │  │   │   │
│  │   │  │ │ Your Code      │ │  │   │   │
│  │   │  │ │ + mvn test     │ │  │   │   │
│  │   │  │ └────────────────┘ │  │   │   │
│  │   │  │         ↓          │  │   │   │
│  │   │  │    Launches:       │  │   │   │
│  │   │  └─────────┬──────────┘  │   │   │
│  │   │            ↓             │   │   │
│  │   │  ┌─────────────────┐    │   │   │
│  │   │  │ android-emulator│    │   │   │
│  │   │  │ container       │    │   │   │
│  │   │  └─────────────────┘    │   │   │
│  │   │  ┌─────────────────┐    │   │   │
│  │   │  │ appium-tests    │    │   │   │
│  │   │  │ container       │    │   │   │
│  │   │  └─────────────────┘    │   │   │
│  │   └──────────────────────────┘   │   │
│  └──────────────────────────────────┘   │
└─────────────────────────────────────────┘
```

**Key Advantages:**

1. **Clean Jenkins Agents** - No tool pollution across projects
2. **Version Control** - Pipeline defines exact container version
3. **Reproducibility** - Same container = same results everywhere
4. **Easy Rollback** - Just change image version in Jenkinsfile
5. **Parallel Builds** - Multiple builds don't conflict (isolated containers)
6. **Multi-Project** - Different projects use different containers

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
