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

---

## 🔄 Jenkins Docker CI/CD Flow (Cloud Execution)

**Complete Step-by-Step Flow when `Jenkinsfile.docker` runs in cloud:**

### **1. Developer Pushes Code**
```
Local Machine → Git Push → GitHub Repository
```

### **2. GitHub Webhook Triggers Jenkins**
```
GitHub → HTTP POST → Cloud Jenkins Master
Payload: {
  "repository": "appium",
  "branch": "main",
  "commit": "abc123"
}
```

### **3. Jenkins Master Schedules Build**
```
┌─────────────────────────────────────┐
│  Jenkins Master (Cloud)             │
│  • Receives webhook                 │
│  • Reads Jenkinsfile.docker         │
│  • Finds available agent            │
│  • Schedules build job              │
└──────────────┬──────────────────────┘
               ↓
         Assigns to Agent
```

### **4. Cloud Agent Starts (Kubernetes Pod Example)**
```
┌─────────────────────────────────────────────┐
│  Kubernetes Cluster (AWS EKS/GKE/AKS)       │
│                                             │
│  Jenkins Master creates Pod dynamically:    │
│                                             │
│  apiVersion: v1                             │
│  kind: Pod                                  │
│  metadata:                                  │
│    name: jenkins-agent-xyz123               │
│  spec:                                      │
│    containers:                              │
│    - name: jnlp                             │
│      image: jenkins/inbound-agent           │
│                                             │
│  Status: Pod starting... ⏳                 │
└─────────────────────────────────────────────┘
```

### **5. Agent Pulls Docker Image (Maven Container)**
```
┌─────────────────────────────────────────────┐
│  Agent Pod (jenkins-agent-xyz123)           │
│                                             │
│  $ docker pull maven:3.9.5-eclipse-temurin-17
│                                             │
│  ⬇️  Downloading from Docker Hub...         │
│  ✓ Layer 1: eclipse-temurin:17             │
│  ✓ Layer 2: Maven 3.9.5                    │
│  ✓ Complete: 500MB                         │
└─────────────────────────────────────────────┘
```

### **6. Maven Container Starts (Pipeline Execution Environment)**
```
┌──────────────────────────────────────────────────────┐
│  Agent Pod                                           │
│  ┌────────────────────────────────────────────────┐  │
│  │  Maven Container                               │  │
│  │  (This is where your pipeline runs!)           │  │
│  │                                                │  │
│  │  Environment:                                  │  │
│  │  • Java 17 ✓                                   │  │
│  │  • Maven 3.9.5 ✓                               │  │
│  │  • /var/run/docker.sock mounted ✓              │  │
│  │  • $HOME/.m2 volume mounted ✓                  │  │
│  │                                                │  │
│  │  Working directory:                            │  │
│  │  /home/jenkins/workspace/appium/               │  │
│  └────────────────────────────────────────────────┘  │
└──────────────────────────────────────────────────────┘
```

### **7. Checkout Stage Executes**
```
Inside Maven Container:

[Pipeline] stage('Checkout')
$ git clone https://github.com/mahi4317/appium.git
Cloning into 'appium'...
✓ Cloned repository
✓ Switched to branch 'main'
✓ Commit: abc123

Files now in container:
/home/jenkins/workspace/appium/
├── Jenkinsfile.docker
├── pom.xml
├── Dockerfile
├── docker-compose.yml
├── run-docker-tests.sh
└── src/...
```

### **8. Install Dependencies Stage**
```
[Pipeline] stage('Install Dependencies')

Inside Maven Container:
$ apt-get update
$ apt-get install -y docker-compose curl

✓ docker-compose installed
✓ curl installed

Verification:
$ docker --version
Docker version 24.0.7 ✓ (via host socket)

$ docker-compose --version
docker-compose version 1.29.2 ✓
```

### **9. Run Tests Stage - Docker Mode**
```
[Pipeline] stage('Run Tests - Docker')

Inside Maven Container:
$ chmod +x ./run-docker-tests.sh
$ ./run-docker-tests.sh

┌─────────────────────────────────────────────┐
│  run-docker-tests.sh Execution:             │
│                                             │
│  1. Check Docker status                     │
│     ✓ Docker daemon accessible              │
│                                             │
│  2. Build test image                        │
│     $ docker build -t appium-tests .        │
│     ⬇️  Building from Dockerfile...         │
│     ✓ Image built: appium-tests:latest     │
│                                             │
│  3. Start emulator container                │
│     $ docker-compose up -d android-emulator │
│     ⬇️  Pulling budtmo/docker-android...    │
│     ✓ Container: android-emulator (running) │
│                                             │
│  4. Wait for emulator boot                  │
│     Checking: adb devices...                │
│     ⏳ Waiting... (0s)                       │
│     ⏳ Waiting... (15s)                      │
│     ⏳ Waiting... (30s)                      │
│     ✓ Emulator ready! (45s)                 │
│                                             │
│  5. Run tests in container                  │
│     $ docker-compose run --rm appium-tests  │
└─────────────────────────────────────────────┘
```

### **10. Test Container Execution**
```
┌─────────────────────────────────────────────────────┐
│  Network: appium-network (bridge)                   │
│                                                     │
│  ┌───────────────────────────┐                      │
│  │  android-emulator         │                      │
│  │  IP: 172.20.0.2           │                      │
│  │  Port 5555: ADB           │                      │
│  │  Status: Device ready     │                      │
│  └───────────────────────────┘                      │
│              ↑                                      │
│              │ Test commands                        │
│              │                                      │
│  ┌───────────────────────────┐                      │
│  │  appium-tests             │                      │
│  │  IP: 172.20.0.3           │                      │
│  │                           │                      │
│  │  $ mvn clean test \       │                      │
│  │    -Denv=docker           │                      │
│  │                           │                      │
│  │  Execution:               │                      │
│  │  • Start Appium server    │                      │
│  │  • Connect to emulator    │                      │
│  │  • Run LaunchSessionTest  │                      │
│  │  • Run CalculatorTest     │                      │
│  │                           │                      │
│  │  Results:                 │                      │
│  │  ✓ Tests run: 2           │                      │
│  │  ✓ Failures: 0            │                      │
│  │  ✓ Skipped: 0             │                      │
│  │  ✓ Time: 45s              │                      │
│  └───────────────────────────┘                      │
└─────────────────────────────────────────────────────┘
```

### **11. Cleanup Stage**
```
[Pipeline] stage('Cleanup')

Inside Maven Container:
$ docker-compose down

Stopping containers:
✓ appium-tests: stopped & removed
✓ android-emulator: stopped & removed

Removing network:
✓ appium-network: removed

Volumes preserved (for next build)
```

### **12. Publish Results**
```
[Pipeline] stage('Publish Results')

Inside Maven Container:
$ junit 'target/surefire-reports/*.xml'

Reading test results:
✓ Found 2 test cases
✓ LaunchSessionTest.testLaunchApp: PASSED
✓ CalculatorTest.testBasicAddition: PASSED

Uploading to Jenkins Master:
⬆️  target/surefire-reports/TEST-*.xml
✓ Results published
```

### **13. Archive Artifacts**
```
[Pipeline] stage('Archive')

Inside Maven Container:
$ archiveArtifacts 'target/surefire-reports/**'

Uploading artifacts to Jenkins Master:
⬆️  target/surefire-reports/
    ├── TEST-LaunchSessionTest.xml
    ├── TEST-CalculatorTest.xml
    └── index.html

✓ Artifacts archived (accessible via Jenkins UI)
```

### **14. Pipeline Completion**
```
[Pipeline] End of Pipeline

Maven Container: Stopped & Removed ✓
Agent Pod (jenkins-agent-xyz123): Terminated ✓

Final Status: SUCCESS ✓
Duration: 3m 45s
```

### **15. Notification (Optional)**
```
Post-Build Actions:

Email:
  To: team@company.com
  Subject: ✓ Appium Tests PASSED (Build #42)
  
Slack:
  Channel: #ci-notifications
  Message: "✓ Appium build #42 succeeded in 3m 45s"
  
GitHub:
  Commit Status: ✓ (green checkmark on commit)
```

---

### **Visual: Complete Cloud Flow Diagram**

```
┌─────────────────────────────────────────────────────────────────────────┐
│                          DEVELOPER                                       │
│                              ↓                                           │
│                      git push origin main                                │
└────────────────────────────────┬────────────────────────────────────────┘
                                 ↓
┌─────────────────────────────────────────────────────────────────────────┐
│                          GITHUB                                          │
│  • Receives commit abc123                                               │
│  • Triggers webhook                                                     │
└────────────────────────────────┬────────────────────────────────────────┘
                                 ↓
┌─────────────────────────────────────────────────────────────────────────┐
│              CLOUD PROVIDER (AWS/Azure/GCP)                              │
│  ┌────────────────────────────────────────────────────────────────────┐ │
│  │                   KUBERNETES CLUSTER                               │ │
│  │                                                                    │ │
│  │  ┌──────────────────────────────────────────────────────────────┐ │ │
│  │  │  JENKINS MASTER POD                                          │ │ │
│  │  │  • Receives webhook from GitHub                              │ │ │
│  │  │  • Reads Jenkinsfile.docker from repo                        │ │ │
│  │  │  • Creates dynamic agent Pod                                 │ │ │
│  │  └────────────────────┬─────────────────────────────────────────┘ │ │
│  │                       ↓                                            │ │
│  │  ┌──────────────────────────────────────────────────────────────┐ │ │
│  │  │  JENKINS AGENT POD (jenkins-agent-xyz123)                    │ │ │
│  │  │  ┌────────────────────────────────────────────────────────┐  │ │ │
│  │  │  │  MAVEN CONTAINER (Pipeline runs here!)                 │  │ │ │
│  │  │  │  ┌──────────────────────────────────────────────────┐  │  │ │ │
│  │  │  │  │  STAGE 1: Checkout                               │  │  │ │ │
│  │  │  │  │  $ git clone github.com/mahi4317/appium          │  │  │ │ │
│  │  │  │  └──────────────────────────────────────────────────┘  │  │ │ │
│  │  │  │  ┌──────────────────────────────────────────────────┐  │  │ │ │
│  │  │  │  │  STAGE 2: Install Dependencies                   │  │  │ │ │
│  │  │  │  │  $ apt-get install docker-compose                │  │  │ │ │
│  │  │  │  └──────────────────────────────────────────────────┘  │  │ │ │
│  │  │  │  ┌──────────────────────────────────────────────────┐  │  │ │ │
│  │  │  │  │  STAGE 3: Run Tests                              │  │  │ │ │
│  │  │  │  │  $ ./run-docker-tests.sh                         │  │  │ │ │
│  │  │  │  │    ↓                                             │  │  │ │ │
│  │  │  │  │  Launches containers:                            │  │  │ │ │
│  │  │  │  └──────────────┬───────────────────────────────────┘  │  │ │ │
│  │  │  │                 ↓                                      │  │ │ │
│  │  │  │  ┌──────────────────────────────────────────────────┐  │  │ │ │
│  │  │  │  │  DOCKER DAEMON (Host)                            │  │  │ │ │
│  │  │  │  │  (Accessed via /var/run/docker.sock)             │  │  │ │ │
│  │  │  │  │                                                  │  │  │ │ │
│  │  │  │  │  Creates network: appium-network                 │  │  │ │ │
│  │  │  │  │                                                  │  │  │ │ │
│  │  │  │  │  ┌───────────────────────────────────────────┐  │  │  │ │ │
│  │  │  │  │  │  Container: android-emulator              │  │  │  │ │ │
│  │  │  │  │  │  Image: budtmo/docker-android             │  │  │  │ │ │
│  │  │  │  │  │  • Android 11 boots                       │  │  │  │ │ │
│  │  │  │  │  │  • ADB listening on 5555                  │  │  │  │ │ │
│  │  │  │  │  │  • Calculator app ready                   │  │  │  │ │ │
│  │  │  │  │  └───────────────────────────────────────────┘  │  │  │ │ │
│  │  │  │  │                  ↑                               │  │  │ │ │
│  │  │  │  │                  │ adb connect                   │  │  │ │ │
│  │  │  │  │                  │                               │  │  │ │ │
│  │  │  │  │  ┌───────────────────────────────────────────┐  │  │  │ │ │
│  │  │  │  │  │  Container: appium-tests                  │  │  │  │ │ │
│  │  │  │  │  │  Image: appium-tests:latest               │  │  │  │ │ │
│  │  │  │  │  │  • Appium server starts                   │  │  │  │ │ │
│  │  │  │  │  │  • Connects to emulator                   │  │  │  │ │ │
│  │  │  │  │  │  • Runs LaunchSessionTest ✓               │  │  │  │ │ │
│  │  │  │  │  │  • Runs CalculatorTest ✓                  │  │  │  │ │ │
│  │  │  │  │  │  • Generates reports                      │  │  │  │ │ │
│  │  │  │  │  └───────────────────────────────────────────┘  │  │  │ │ │
│  │  │  │  └──────────────────────────────────────────────────┘  │  │ │ │
│  │  │  │  ┌──────────────────────────────────────────────────┐  │  │ │ │
│  │  │  │  │  STAGE 4: Cleanup                                │  │  │ │ │
│  │  │  │  │  $ docker-compose down                           │  │  │ │ │
│  │  │  │  └──────────────────────────────────────────────────┘  │  │ │ │
│  │  │  │  ┌──────────────────────────────────────────────────┐  │  │ │ │
│  │  │  │  │  STAGE 5: Publish Results                        │  │  │ │ │
│  │  │  │  │  $ junit 'target/surefire-reports/*.xml'         │  │  │ │ │
│  │  │  │  │  ⬆️ Upload to Jenkins Master                      │  │  │ │ │
│  │  │  │  └──────────────────────────────────────────────────┘  │  │ │ │
│  │  │  └────────────────────────────────────────────────────────┘  │ │ │
│  │  │  Maven Container: Stopped & Removed                         │ │ │
│  │  └──────────────────────────────────────────────────────────────┘ │ │
│  │  Agent Pod: Terminated                                            │ │
│  └────────────────────────────────────────────────────────────────────┘ │
│                                                                         │
│  ┌────────────────────────────────────────────────────────────────────┐ │
│  │  JENKINS MASTER POD                                                │ │
│  │  • Receives test results                                           │ │
│  │  • Publishes to UI                                                 │ │
│  │  • Updates commit status on GitHub                                 │ │
│  │  • Sends notifications                                             │ │
│  └────────────────────────────────────────────────────────────────────┘ │
└─────────────────────────────────────────────────────────────────────────┘
                                 ↓
┌─────────────────────────────────────────────────────────────────────────┐
│                       NOTIFICATIONS                                      │
│  • Email: team@company.com ✓                                            │
│  • Slack: #ci-notifications ✓                                           │
│  • GitHub: Commit status updated ✓                                      │
└─────────────────────────────────────────────────────────────────────────┘
```

---

### **Key Points About Cloud Execution:**

**1. Container Nesting (3 Levels):**
```
Kubernetes Pod (Agent)
  └── Maven Container (Pipeline)
      └── Test Containers (Emulator + Tests)
```

**2. Dynamic Resource Allocation:**
- Agent Pod created **only when build starts**
- Pod destroyed **after build completes**
- Cost: Only pay for ~4 minutes of compute time

**3. Network Isolation:**
- Each build gets isolated network (`appium-network`)
- Containers communicate via bridge network
- No conflicts between parallel builds

**4. Data Flow:**
```
Code:     GitHub → Agent Pod → Maven Container
Results:  Maven Container → Agent Pod → Jenkins Master → GitHub
Logs:     All stages stream to Jenkins Master UI
```

**5. Resource Usage (Example AWS EKS):**
```
Jenkins Master: 1 Pod (always running) = t3.small ($15/month)
Agent Pod:      Created on-demand     = t3.medium ($0.05/hour)
Build Duration: 4 minutes             = $0.003 per build
Daily Builds:   20 builds             = $0.06/day = $1.80/month

Total: ~$17/month (vs $300/month for 24/7 VMs)
```

**6. Parallel Execution:**
- Can run 10 builds simultaneously
- Each gets isolated Agent Pod
- Test containers don't interfere
- Perfect for large teams

---

### **What Makes This Work:**

✅ **Docker Socket Mounting:**
```groovy
args '-v /var/run/docker.sock:/var/run/docker.sock'
```
Allows Maven container to control host Docker daemon

✅ **Stateless Builds:**
- Each build starts fresh
- No leftover state
- Reproducible results

✅ **Automatic Cleanup:**
- Containers removed after tests
- Agent Pod terminated
- No manual intervention

✅ **Cloud-Native:**
- Works on any Kubernetes cluster
- AWS EKS, Azure AKS, Google GKE
- Same code, different clouds

---

## 🌐 Cloud Jenkins Execution

**Common Question:** "If Jenkins runs in the cloud (AWS/Azure/GCP/Kubernetes), how does it execute tests?"

### **Answer: Using Jenkins Agents (Workers)**

Jenkins uses a **master-agent architecture** where:
- **Jenkins Master** (Controller) - Orchestrates builds, manages UI, stores configurations
- **Jenkins Agents** (Workers) - Execute the actual pipeline jobs

**Cloud Execution Flow:**

```
┌──────────────────────────────────────────────────────────┐
│                    CLOUD PROVIDER                         │
│                  (AWS / Azure / GCP / K8s)                │
├──────────────────────────────────────────────────────────┤
│                                                           │
│  ┌─────────────────────────┐                             │
│  │   Jenkins Master        │   (Manages & Schedules)     │
│  │   (Controller)          │                             │
│  │   • Web UI              │                             │
│  │   • Job Scheduling      │                             │
│  │   • Plugin Management   │                             │
│  └───────────┬─────────────┘                             │
│              │                                            │
│              │ Assigns jobs to:                           │
│              ↓                                            │
│  ┌───────────────────────────────────────────────────┐   │
│  │          Jenkins Agents (Workers)                 │   │
│  ├───────────────────────────────────────────────────┤   │
│  │                                                   │   │
│  │  Agent 1 (VM/Container)   Agent 2   Agent 3      │   │
│  │  ┌──────────────────┐    ┌───────┐  ┌───────┐   │   │
│  │  │ Running your     │    │ Idle  │  │ Busy  │   │   │
│  │  │ Appium tests     │    │       │  │       │   │   │
│  │  │ ┌──────────────┐ │    └───────┘  └───────┘   │   │
│  │  │ │ Docker       │ │                            │   │
│  │  │ │ ├─ Maven    │ │                            │   │
│  │  │ │ ├─ Emulator │ │                            │   │
│  │  │ │ └─ Tests    │ │                            │   │
│  │  │ └──────────────┘ │                            │   │
│  │  └──────────────────┘                            │   │
│  └───────────────────────────────────────────────────┘   │
└──────────────────────────────────────────────────────────┘
```

### **Three Cloud Execution Approaches:**

---

#### **Approach 1: Static Cloud VMs as Agents**

**How it works:**
- Provision VMs in cloud (EC2, Azure VMs, GCE instances)
- Install Docker on each VM
- Configure as Jenkins agents
- Agents stay running 24/7 or start on-demand

```yaml
# Example AWS EC2 Setup
1. Launch EC2 instance (Ubuntu 22.04)
2. Install Docker: apt-get install docker.io
3. Add Jenkins user to docker group
4. Configure in Jenkins:
   Manage Jenkins → Nodes → New Node
   - Name: aws-agent-1
   - Remote root directory: /home/jenkins
   - Launch method: SSH
   - Host: ec2-xx-xxx-xxx-xx.compute.amazonaws.com
```

**Test Execution:**
```
Jenkins Master (Cloud) 
    → Connects to EC2 Agent via SSH
    → Agent pulls your code
    → Runs Jenkinsfile.docker
    → Docker containers launch on EC2
    → Tests execute
    → Results sent back to Master
```

**Cost:** VMs run 24/7 (expensive) or on-demand (cheaper)

---

#### **Approach 2: Kubernetes Dynamic Agents** ⭐ **Recommended for Cloud**

**How it works:**
- Jenkins runs in Kubernetes cluster
- Agents created as **Pods** on-demand
- Each build gets fresh Pod
- Pod destroyed after build completes

```yaml
# Jenkins Kubernetes Plugin Configuration
apiVersion: v1
kind: Pod
metadata:
  labels:
    jenkins: agent
spec:
  containers:
  - name: maven-docker
    image: maven:3.9.5-eclipse-temurin-17
    command: ['cat']
    tty: true
    volumeMounts:
    - name: docker-sock
      mountPath: /var/run/docker.sock
  volumes:
  - name: docker-sock
    hostPath:
      path: /var/run/docker.sock
```

**Test Execution Flow:**
```
1. Developer pushes code to GitHub
2. Jenkins Master (K8s Pod) receives webhook
3. Master creates new Agent Pod dynamically
4. Agent Pod pulls code
5. Runs Jenkinsfile.docker
   → Launches test containers (emulator, appium-tests)
6. Tests complete
7. Results uploaded to Master
8. Agent Pod deleted automatically
```

**Kubernetes Architecture:**
```
┌─────────────────────────────────────────────────┐
│         Kubernetes Cluster (EKS/GKE/AKS)        │
├─────────────────────────────────────────────────┤
│                                                 │
│  ┌─────────────────────────────────┐            │
│  │  Namespace: jenkins              │            │
│  │                                  │            │
│  │  ┌────────────────────────────┐  │            │
│  │  │  Pod: jenkins-master       │  │            │
│  │  │  (Controller)              │  │            │
│  │  └────────────┬───────────────┘  │            │
│  │               │                  │            │
│  │               ↓ Creates Pods     │            │
│  │                                  │            │
│  │  ┌────────────────────────────┐  │            │
│  │  │  Pod: jenkins-agent-abc12  │  │ ← Build 1  │
│  │  │  ┌──────────────────────┐  │  │            │
│  │  │  │ Container: maven     │  │  │            │
│  │  │  │ • Runs pipeline      │  │  │            │
│  │  │  │ • Launches Docker    │  │  │            │
│  │  │  └──────────────────────┘  │  │            │
│  │  └────────────────────────────┘  │            │
│  │                                  │            │
│  │  ┌────────────────────────────┐  │            │
│  │  │  Pod: jenkins-agent-def34  │  │ ← Build 2  │
│  │  │  (Running different build) │  │            │
│  │  └────────────────────────────┘  │            │
│  │                                  │            │
│  │  ┌────────────────────────────┐  │            │
│  │  │  Pod: android-emulator     │  │ ← Test     │
│  │  │  (Launched by agent)       │  │   Container│
│  │  └────────────────────────────┘  │            │
│  │                                  │            │
│  └──────────────────────────────────┘            │
└─────────────────────────────────────────────────┘
```

**Benefits:**
- ✅ **Elastic scaling** - Agents created on-demand
- ✅ **Cost efficient** - Only pay when building
- ✅ **Isolated builds** - Each build in separate Pod
- ✅ **No maintenance** - Kubernetes manages lifecycle
- ✅ **Perfect for `Jenkinsfile.docker`** - Containers within containers!

**Real Example:**
```groovy
// Jenkinsfile.docker in Kubernetes
pipeline {
    agent {
        kubernetes {
            yaml '''
apiVersion: v1
kind: Pod
spec:
  containers:
  - name: maven
    image: maven:3.9.5-eclipse-temurin-17
    command: ['sleep', '99999']
    volumeMounts:
    - name: docker-sock
      mountPath: /var/run/docker.sock
  volumes:
  - name: docker-sock
    hostPath:
      path: /var/run/docker.sock
'''
        }
    }
    
    stages {
        stage('Run Tests') {
            steps {
                container('maven') {
                    sh './run-docker-tests.sh'
                }
            }
        }
    }
}
```

**What Happens:**
1. K8s creates Pod with Maven container
2. Maven container has Docker access (via socket)
3. `./run-docker-tests.sh` runs inside Maven container
4. Script launches `android-emulator` and `appium-tests` containers
5. Tests execute
6. Pod cleaned up automatically

---

#### **Approach 3: Serverless/Fargate Agents**

**AWS ECS Fargate Example:**
- Jenkins Master runs in ECS
- Agents launched as Fargate tasks
- No server management needed

```yaml
# AWS ECS Task Definition
{
  "family": "jenkins-agent",
  "networkMode": "awsvpc",
  "containerDefinitions": [{
    "name": "maven",
    "image": "maven:3.9.5-eclipse-temurin-17",
    "memory": 4096,
    "cpu": 2048
  }]
}
```

---

### **Which Approach for Your Framework?**

| Scenario | Best Approach | Why |
|----------|--------------|-----|
| **Running in AWS EKS** | K8s Dynamic Agents | Built-in, cost-effective |
| **Running in GKE/AKS** | K8s Dynamic Agents | Native integration |
| **AWS EC2 only** | Static VMs + Docker | No K8s needed |
| **Azure VMs** | Static VMs + Docker | Simple setup |
| **Mixed cloud** | Docker agents | Portable |

---

### **Your Framework: Cloud-Ready!**

Both your Jenkinsfiles work perfectly in cloud:

**`Jenkinsfile.docker` (Best for Cloud):**
```groovy
agent {
    docker {
        image 'maven:3.9.5-eclipse-temurin-17'
        // This works in:
        // ✅ K8s Pods
        // ✅ EC2 instances
        // ✅ Azure VMs
        // ✅ Any cloud with Docker
    }
}
```

**Why it's cloud-friendly:**
- No local dependencies (everything in containers)
- Works same on any cloud provider
- Automatic cleanup (no leftover resources)
- Scales horizontally (run 100 builds in parallel)

---

### **Example: Jenkins in AWS EKS**

**Setup Steps:**
```bash
# 1. Create EKS cluster
eksctl create cluster --name jenkins-cluster

# 2. Install Jenkins via Helm
helm repo add jenkins https://charts.jenkins.io
helm install jenkins jenkins/jenkins

# 3. Configure Kubernetes plugin
# Jenkins UI → Manage Jenkins → Configure System
# → Cloud → Add Kubernetes
#   - Kubernetes URL: https://kubernetes.default
#   - Pod Template: (use yaml above)

# 4. Push code → Jenkins creates Pod → Runs tests → Pod deleted
```

**Cost Optimization:**
```yaml
# Only pay for:
- Jenkins Master Pod: ~$30/month (t3.medium)
- Agent Pods: Only when building (~$0.10/hour when active)
- Storage: ~$10/month (for workspace)

# vs Traditional:
- 3 VMs running 24/7: ~$300/month
```

---

### **Network Architecture for Cloud Jenkins**

```
Internet
    ↓
GitHub Webhook
    ↓
┌───────────────────────────────────────┐
│  Cloud VPC (AWS/Azure/GCP)            │
│                                       │
│  ┌─────────────────────────────────┐  │
│  │  Public Subnet                  │  │
│  │  ┌───────────────────────────┐  │  │
│  │  │  Load Balancer            │  │  │
│  │  │  jenkins.yourcompany.com  │  │  │
│  │  └────────────┬──────────────┘  │  │
│  └───────────────┼──────────────────┘  │
│                  ↓                     │
│  ┌─────────────────────────────────┐  │
│  │  Private Subnet                 │  │
│  │  ┌───────────────────────────┐  │  │
│  │  │  Jenkins Master           │  │  │
│  │  └────────────┬──────────────┘  │  │
│  │               ↓                 │  │
│  │  ┌───────────────────────────┐  │  │
│  │  │  Jenkins Agents (Pods)    │  │  │
│  │  │  • Execute tests          │  │  │
│  │  │  • Launch containers      │  │  │
│  │  └───────────────────────────┘  │  │
│  └─────────────────────────────────┘  │
│                  ↓                     │
│         Can access:                    │
│         • Docker Hub (images)          │
│         • GitHub (code)                │
│         • BrowserStack (if configured) │
└───────────────────────────────────────┘
```

---

### **Key Takeaway**

**Your framework works in cloud Jenkins by:**
1. **Jenkins Master** (cloud) schedules the build
2. **Jenkins Agent** (cloud VM/Pod) executes `Jenkinsfile.docker`
3. **Maven container** launches inside the agent
4. **Test containers** (emulator, appium-tests) launch from Maven container
5. **Results** uploaded to Jenkins Master
6. **Cleanup** happens automatically

**No code changes needed!** Your existing Jenkinsfiles work as-is in cloud environments. The cloud provider just runs the containers instead of your local machine. 🚀
