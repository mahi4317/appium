pipeline {
    agent any
    
    tools {
        maven 'Maven 3.9.5'
        jdk 'JDK 17'
    }
    
    environment {
        ANDROID_HOME = "${env.WORKSPACE}/android-sdk"
        PATH = "${ANDROID_HOME}/cmdline-tools/latest/bin:${ANDROID_HOME}/platform-tools:${ANDROID_HOME}/emulator:${PATH}"
        BROWSERSTACK_USER = credentials('browserstack-user')
        BROWSERSTACK_KEY = credentials('browserstack-key')
    }
    
    parameters {
        choice(name: 'EXECUTION_MODE', choices: ['local', 'docker', 'browserstack', 'saucelabs'], description: 'Select execution mode')
        string(name: 'TEST_CLASS', defaultValue: '', description: 'Specific test class to run (optional)')
        booleanParam(name: 'SKIP_INSTALL_SDK', defaultValue: false, description: 'Skip Android SDK installation')
    }
    
    stages {
        stage('Checkout') {
            steps {
                checkout scm
                sh 'chmod +x run-docker-tests.sh'
            }
        }
        
        stage('Setup Android SDK') {
            when {
                allOf {
                    expression { params.EXECUTION_MODE == 'local' }
                    expression { params.SKIP_INSTALL_SDK == false }
                }
            }
            steps {
                script {
                    sh '''
                        # Install Android SDK if not exists
                        if [ ! -d "${ANDROID_HOME}" ]; then
                            mkdir -p ${ANDROID_HOME}/cmdline-tools
                            wget -q https://dl.google.com/android/repository/commandlinetools-linux-9477386_latest.zip
                            unzip -q commandlinetools-linux-9477386_latest.zip -d ${ANDROID_HOME}/cmdline-tools
                            mv ${ANDROID_HOME}/cmdline-tools/cmdline-tools ${ANDROID_HOME}/cmdline-tools/latest
                            rm commandlinetools-linux-9477386_latest.zip
                            
                            # Install SDK components
                            yes | sdkmanager --licenses || true
                            sdkmanager "platform-tools" "platforms;android-29" "build-tools;29.0.3" \
                                "emulator" "system-images;android-29;default;x86_64"
                        fi
                    '''
                }
            }
        }
        
        stage('Install Node.js & Appium') {
            when {
                expression { params.EXECUTION_MODE == 'local' }
            }
            steps {
                script {
                    sh '''
                        # Install Node.js if not exists
                        if ! command -v node &> /dev/null; then
                            curl -fsSL https://deb.nodesource.com/setup_20.x | bash -
                            apt-get install -y nodejs
                        fi
                        
                        # Install Appium
                        npm install -g appium@next || true
                        appium driver install uiautomator2 || true
                    '''
                }
            }
        }
        
        stage('Start Android Emulator') {
            when {
                expression { params.EXECUTION_MODE == 'local' }
            }
            steps {
                script {
                    sh '''
                        # Create AVD if not exists
                        if ! avdmanager list avd | grep -q "jenkins-emulator"; then
                            echo "no" | avdmanager create avd -n jenkins-emulator \
                                -k "system-images;android-29;default;x86_64" \
                                -d "Nexus 6" --force
                        fi
                        
                        # Start emulator in background
                        ${ANDROID_HOME}/emulator/emulator -avd jenkins-emulator \
                            -no-window -no-audio -no-boot-anim -gpu swiftshader_indirect \
                            -memory 2048 -partition-size 4096 > /dev/null 2>&1 &
                        
                        # Wait for emulator to boot
                        timeout=300
                        elapsed=0
                        while [ $elapsed -lt $timeout ]; do
                            if adb shell getprop sys.boot_completed 2>/dev/null | grep -q "1"; then
                                echo "Emulator is ready!"
                                sleep 5
                                break
                            fi
                            sleep 5
                            elapsed=$((elapsed + 5))
                            echo "Waiting for emulator... ($elapsed seconds)"
                        done
                        
                        if [ $elapsed -ge $timeout ]; then
                            echo "Emulator failed to start within timeout"
                            exit 1
                        fi
                        
                        adb devices
                    '''
                }
            }
        }
        
        stage('Install Calculator App') {
            when {
                expression { params.EXECUTION_MODE == 'local' }
            }
            steps {
                script {
                    sh '''
                        # Download and install calculator app
                        wget -q -O calculator.apk \
                            "https://f-droid.org/repo/com.simplemobiletools.calculator_38.apk" || true
                        adb install -r calculator.apk || echo "App already installed or installation failed"
                    '''
                }
            }
        }
        
        stage('Run Tests - Local') {
            when {
                expression { params.EXECUTION_MODE == 'local' }
            }
            steps {
                script {
                    def testCmd = params.TEST_CLASS ? "-Dtest=${params.TEST_CLASS}" : ""
                    sh """
                        mvn clean test ${testCmd} -Demulator.auto.start=false
                    """
                }
            }
        }
        
        stage('Run Tests - Docker') {
            when {
                expression { params.EXECUTION_MODE == 'docker' }
            }
            steps {
                script {
                    sh '''
                        ./run-docker-tests.sh
                    '''
                }
            }
        }
        
        stage('Run Tests - BrowserStack') {
            when {
                expression { params.EXECUTION_MODE == 'browserstack' }
            }
            steps {
                script {
                    def testCmd = params.TEST_CLASS ? "-Dtest=${params.TEST_CLASS}" : ""
                    sh """
                        mvn clean test -Denv=browserstack ${testCmd} \
                            -Dbrowserstack.user=${BROWSERSTACK_USER} \
                            -Dbrowserstack.key=${BROWSERSTACK_KEY}
                    """
                }
            }
        }
        
        stage('Run Tests - Sauce Labs') {
            when {
                expression { params.EXECUTION_MODE == 'saucelabs' }
            }
            steps {
                script {
                    def testCmd = params.TEST_CLASS ? "-Dtest=${params.TEST_CLASS}" : ""
                    withCredentials([
                        string(credentialsId: 'sauce-username', variable: 'SAUCE_USER'),
                        string(credentialsId: 'sauce-accesskey', variable: 'SAUCE_KEY')
                    ]) {
                        sh """
                            mvn clean test -Denv=saucelabs ${testCmd} \
                                -Dsauce.username=${SAUCE_USER} \
                                -Dsauce.accessKey=${SAUCE_KEY}
                        """
                    }
                }
            }
        }
    }
    
    post {
        always {
            // Publish test results
            junit allowEmptyResults: true, testResults: '**/target/surefire-reports/*.xml'
            
            // Archive test reports
            archiveArtifacts artifacts: '**/target/surefire-reports/**/*', allowEmptyArchive: true
            
            // Clean up emulator
            script {
                if (params.EXECUTION_MODE == 'local') {
                    sh 'adb emu kill || true'
                }
                if (params.EXECUTION_MODE == 'docker') {
                    sh 'docker-compose down || true'
                }
            }
            
            // Clean workspace
            cleanWs()
        }
        
        success {
            echo 'Tests passed successfully! ✅'
            // You can add notifications here (email, Slack, etc.)
        }
        
        failure {
            echo 'Tests failed! ❌'
            // You can add notifications here
        }
    }
}
