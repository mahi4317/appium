# Jenkins CI/CD Setup Guide

This guide explains how to set up Jenkins for automated Appium testing with multiple execution modes.

## 📋 Table of Contents

1. [Jenkins Installation](#jenkins-installation)
2. [Required Plugins](#required-plugins)
3. [Credentials Setup](#credentials-setup)
4. [Pipeline Configuration](#pipeline-configuration)
5. [Execution Modes](#execution-modes)
6. [Troubleshooting](#troubleshooting)

---

## 🚀 Jenkins Installation

### Option 1: Docker (Recommended)

```bash
# Pull Jenkins image
docker pull jenkins/jenkins:lts

# Run Jenkins with Docker socket access
docker run -d \
  --name jenkins \
  -p 8080:8080 \
  -p 50000:50000 \
  -v jenkins_home:/var/jenkins_home \
  -v /var/run/docker.sock:/var/run/docker.sock \
  jenkins/jenkins:lts

# Get initial admin password
docker exec jenkins cat /var/jenkins_home/secrets/initialAdminPassword
```

### Option 2: Native Installation

**macOS:**
```bash
brew install jenkins-lts
brew services start jenkins-lts
```

**Ubuntu/Debian:**
```bash
wget -q -O - https://pkg.jenkins.io/debian-stable/jenkins.io.key | sudo apt-key add -
sudo sh -c 'echo deb https://pkg.jenkins.io/debian-stable binary/ > /etc/apt/sources.list.d/jenkins.list'
sudo apt-get update
sudo apt-get install jenkins
sudo systemctl start jenkins
```

Access Jenkins: `http://localhost:8080`

---

## 🔌 Required Plugins

Install these plugins from **Manage Jenkins → Plugins → Available**:

### Essential Plugins:
- ✅ **Pipeline** - For Jenkinsfile support
- ✅ **Git** - Source code management
- ✅ **Docker Pipeline** - Docker integration
- ✅ **JUnit** - Test results publishing
- ✅ **Credentials Binding** - Secure credential management
- ✅ **Timestamper** - Add timestamps to console output
- ✅ **AnsiColor** - Color console output

### Optional Plugins:
- **Allure** - Enhanced test reporting
- **Email Extension** - Email notifications
- **Slack Notification** - Slack integration
- **Build Timeout** - Prevent stuck builds
- **Workspace Cleanup** - Clean workspace before/after builds

---

## 🔐 Credentials Setup

### 1. BrowserStack Credentials

**Navigate:** Manage Jenkins → Credentials → System → Global credentials → Add Credentials

**Add Secret Text:**
- **ID:** `browserstack-user`
- **Secret:** Your BrowserStack username
- **Description:** BrowserStack Username

**Add Secret Text:**
- **ID:** `browserstack-key`
- **Secret:** Your BrowserStack access key
- **Description:** BrowserStack Access Key

### 2. Sauce Labs Credentials (Optional)

**Add Secret Text:**
- **ID:** `sauce-username`
- **Secret:** Your Sauce Labs username

**Add Secret Text:**
- **ID:** `sauce-accesskey`
- **Secret:** Your Sauce Labs access key

### 3. GitHub Credentials (for private repos)

**Add Username with Password:**
- **ID:** `github-credentials`
- **Username:** Your GitHub username
- **Password:** Personal Access Token (PAT)

---

## ⚙️ Pipeline Configuration

### Method 1: Multibranch Pipeline (Recommended)

1. **New Item** → **Multibranch Pipeline**
2. **Branch Sources** → Add source → Git
   - **Repository URL:** `https://github.com/mahi4317/appium.git`
   - **Credentials:** Select your GitHub credentials
3. **Build Configuration:**
   - **Mode:** by Jenkinsfile
   - **Script Path:** `Jenkinsfile`
4. **Scan Multibranch Pipeline Triggers:**
   - ✅ Periodically if not otherwise run: `1 hour`
5. **Save**

Jenkins will automatically detect the `Jenkinsfile` and create jobs for each branch.

### Method 2: Pipeline Job

1. **New Item** → **Pipeline**
2. **Pipeline Definition:** Pipeline script from SCM
3. **SCM:** Git
   - **Repository URL:** `https://github.com/mahi4317/appium.git`
   - **Script Path:** `Jenkinsfile`
4. **Save**

---

## 🎯 Execution Modes

The Jenkinsfile supports 4 execution modes:

### 1. **Local Execution** (Jenkins Agent with Android SDK)

```groovy
// Build with Parameters
EXECUTION_MODE: local
TEST_CLASS: (leave empty for all tests)
SKIP_INSTALL_SDK: false
```

**Requirements:**
- Jenkins agent with:
  - JDK 17
  - Maven 3.9.5
  - Android SDK
  - Node.js + Appium

**What it does:**
- Installs Android SDK (if needed)
- Creates and starts Android emulator
- Installs calculator app
- Runs tests on local emulator

### 2. **Docker Execution** (Containerized)

```groovy
// Build with Parameters
EXECUTION_MODE: docker
```

**Requirements:**
- Docker installed on Jenkins agent
- `docker-compose` available

**What it does:**
- Builds test container
- Starts Android emulator container
- Runs tests in isolated environment
- Cleans up containers

**Best for:** Consistent environment, no SDK setup needed

### 3. **BrowserStack Execution** (Cloud)

```groovy
// Build with Parameters
EXECUTION_MODE: browserstack
TEST_CLASS: (optional)
```

**Requirements:**
- BrowserStack credentials configured
- Internet connectivity

**What it does:**
- Runs tests on real devices in BrowserStack cloud
- No local Android setup needed

**Best for:** Testing on real devices, parallel execution

### 4. **Sauce Labs Execution** (Cloud)

```groovy
// Build with Parameters
EXECUTION_MODE: saucelabs
```

**Requirements:**
- Sauce Labs credentials configured

---

## 📊 Viewing Results

### Test Reports
- **Build** → **Test Results** - JUnit test reports
- **Build** → **Workspace** → `target/surefire-reports/` - Raw reports

### Console Output
- **Build** → **Console Output** - Full execution logs

### Artifacts
- **Build** → **Build Artifacts** - Archived test reports

---

## 🔄 Automated Triggers

### Trigger on Git Push (Webhook)

**GitHub Webhook Setup:**
1. Go to your GitHub repo → **Settings** → **Webhooks**
2. **Add webhook:**
   - **Payload URL:** `http://your-jenkins-url/github-webhook/`
   - **Content type:** application/json
   - **Events:** Just the push event
3. **Save**

**Jenkins Configuration:**
1. Your Pipeline → **Configure**
2. **Build Triggers:**
   - ✅ **GitHub hook trigger for GITScm polling**
3. **Save**

### Scheduled Builds (Cron)

```groovy
// Add to Jenkinsfile
triggers {
    // Run daily at 2 AM
    cron('0 2 * * *')
    
    // Or run every 6 hours
    cron('0 */6 * * *')
}
```

---

## 🛠️ Tool Configuration

### Configure Maven

1. **Manage Jenkins** → **Tools**
2. **Maven installations:**
   - **Name:** `Maven 3.9.5`
   - ✅ Install automatically
   - **Version:** 3.9.5
3. **Save**

### Configure JDK

1. **Manage Jenkins** → **Tools**
2. **JDK installations:**
   - **Name:** `JDK 17`
   - ✅ Install automatically
   - **Version:** OpenJDK 17
3. **Save**

---

## 🐛 Troubleshooting

### Issue: "Cannot connect to Docker daemon"

**Solution:**
```bash
# Add Jenkins user to docker group
sudo usermod -aG docker jenkins
sudo systemctl restart jenkins
```

### Issue: "Emulator failed to start"

**Solution:**
- Increase timeout in Jenkinsfile
- Check KVM support: `kvm-ok`
- Use Docker mode instead

### Issue: "Tests fail in Jenkins but pass locally"

**Solution:**
- Check environment variables
- Verify credentials are configured
- Review console output for specific errors
- Try Docker mode for consistency

### Issue: "BrowserStack tests fail"

**Solution:**
- Verify credentials: `browserstack-user` and `browserstack-key`
- Check BrowserStack account status
- Review BrowserStack dashboard for errors

### Issue: "Workspace permission denied"

**Solution:**
```bash
# Fix workspace permissions
sudo chown -R jenkins:jenkins /var/lib/jenkins/workspace
```

---

## 📈 Advanced Configuration

### Parallel Test Execution

```groovy
// Modify Jenkinsfile
stage('Run Tests') {
    parallel {
        stage('Android 11') {
            steps {
                sh 'mvn test -Denv=browserstack -Dplatform.version=11'
            }
        }
        stage('Android 12') {
            steps {
                sh 'mvn test -Denv=browserstack -Dplatform.version=12'
            }
        }
    }
}
```

### Email Notifications

```groovy
// Add to post section in Jenkinsfile
post {
    success {
        emailext (
            subject: "✅ Build Success: ${env.JOB_NAME} #${env.BUILD_NUMBER}",
            body: "Test results: ${env.BUILD_URL}testReport/",
            to: 'your-email@example.com'
        )
    }
    failure {
        emailext (
            subject: "❌ Build Failed: ${env.JOB_NAME} #${env.BUILD_NUMBER}",
            body: "Console output: ${env.BUILD_URL}console",
            to: 'your-email@example.com'
        )
    }
}
```

### Slack Notifications

```groovy
// Add to post section
post {
    always {
        slackSend (
            channel: '#qa-automation',
            color: currentBuild.result == 'SUCCESS' ? 'good' : 'danger',
            message: "Test Results: ${currentBuild.result}\nBuild: ${env.BUILD_URL}"
        )
    }
}
```

---

## 📝 Jenkins File Options

We provide two Jenkinsfile variants:

1. **`Jenkinsfile`** - Full-featured pipeline
   - Supports all 4 execution modes
   - Local Android SDK setup
   - Comprehensive configuration

2. **`Jenkinsfile.docker`** - Simplified Docker-only pipeline
   - Docker and BrowserStack modes only
   - Runs in Maven Docker container
   - Minimal setup required

**To use Jenkinsfile.docker:**
```groovy
// In Pipeline configuration
Script Path: Jenkinsfile.docker
```

---

## 🎉 Quick Start Example

### Run Your First Jenkins Build:

1. **Create Pipeline job** named "Appium-Tests"
2. **Configure:**
   - SCM: Git
   - URL: `https://github.com/mahi4317/appium.git`
   - Script Path: `Jenkinsfile.docker`
3. **Add BrowserStack credentials** (if needed)
4. **Build with Parameters:**
   - EXECUTION_MODE: `docker`
5. **Click "Build"**

---

## 📚 Additional Resources

- [Jenkins Documentation](https://www.jenkins.io/doc/)
- [Pipeline Syntax](https://www.jenkins.io/doc/book/pipeline/syntax/)
- [Docker Plugin](https://plugins.jenkins.io/docker-plugin/)
- [Credentials Plugin](https://plugins.jenkins.io/credentials/)

---

## 🆘 Support

For issues or questions:
- Check Jenkins console output
- Review test reports in `target/surefire-reports/`
- Check Docker logs: `docker-compose logs`
- Verify credentials configuration
