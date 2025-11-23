# GitHub Actions CI/CD

This project uses GitHub Actions for continuous integration and testing.

## Workflows

### 1. Android Appium Tests (`android-tests.yml`)
Runs tests on macOS with Android emulator.

**Triggers:**
- Push to `main` or `develop` branches
- Pull requests to `main`
- Daily at 9 AM UTC
- Manual dispatch

**What it does:**
- Sets up Java 17, Node.js, Appium
- Installs Android SDK and creates emulator
- Installs calculator app
- Runs all tests
- Publishes test reports

### 2. BrowserStack Cloud Tests (`browserstack-tests.yml`)
Runs tests on BrowserStack cloud devices.

**Triggers:**
- Push to `main` branch
- Pull requests
- Daily at 10 AM UTC
- Manual dispatch

**What it does:**
- Runs tests on real BrowserStack devices
- Uploads test results
- Publishes test reports

## Setup Instructions

### Required Secrets
Add these secrets in GitHub repo settings (Settings → Secrets and variables → Actions):

- `BROWSERSTACK_USER` - Your BrowserStack username
- `BROWSERSTACK_KEY` - Your BrowserStack access key

### Manual Workflow Trigger
1. Go to **Actions** tab in your GitHub repo
2. Select the workflow you want to run
3. Click **Run workflow** button
4. Select branch and click **Run workflow**

## Viewing Test Results

- **Test Reports:** Available in the Actions run summary
- **Artifacts:** Downloaded from the Actions run page
- **Test Reporter:** Detailed test results in the "Checks" tab of PRs

## Local Testing

To test the same configuration locally:

```bash
# Disable emulator auto-start (CI handles this)
sed -i 's/emulator.auto.start=true/emulator.auto.start=false/' src/test/resources/config/android.properties

# Run tests
mvn clean test
```

## Workflow Badges

Add to your README.md:

```markdown
![Android Tests](https://github.com/mahi4317/appium/actions/workflows/android-tests.yml/badge.svg)
![BrowserStack Tests](https://github.com/mahi4317/appium/actions/workflows/browserstack-tests.yml/badge.svg)
```
