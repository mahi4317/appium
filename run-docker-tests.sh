#!/bin/bash

echo "🐳 Starting Docker-based Appium tests..."

# Check if Docker is running
if ! docker info > /dev/null 2>&1; then
    echo "❌ Docker is not running. Please start Docker Desktop."
    exit 1
fi

# Build the test image
echo "📦 Building test image..."
docker-compose build appium-tests

# Start emulator in background
echo "🚀 Starting Android emulator..."
docker-compose up -d android-emulator

# Wait for emulator to be ready
echo "⏳ Waiting for Android emulator to boot (this may take 2-3 minutes)..."
timeout=300
elapsed=0
while [ $elapsed -lt $timeout ]; do
    if docker-compose exec -T android-emulator adb wait-for-device shell getprop sys.boot_completed 2>/dev/null | grep -q "1"; then
        echo "✅ Emulator is ready!"
        sleep 5  # Extra time for full boot
        break
    fi
    sleep 10
    elapsed=$((elapsed + 10))
    echo "   Still waiting... ($elapsed seconds)"
done

if [ $elapsed -ge $timeout ]; then
    echo "❌ Emulator failed to start within $timeout seconds"
    docker-compose logs android-emulator | tail -50
    docker-compose down
    exit 1
fi

# Connect to emulator from test container
echo "🔗 Connecting to emulator..."
docker-compose run --rm appium-tests adb connect android-emulator:5555 || true
sleep 2

# Install calculator app (optional, if not pre-installed)
echo "📱 Installing calculator app..."
docker-compose run --rm appium-tests adb -s android-emulator:5555 install -r /tmp/calculator.apk 2>/dev/null || \
    echo "⚠️  Calculator app installation skipped (may already be installed)"

# Run tests
echo "🧪 Running tests..."
docker-compose run --rm appium-tests

# Capture exit code
TEST_EXIT_CODE=$?

# Stop services
echo "🛑 Stopping services..."
docker-compose down

if [ $TEST_EXIT_CODE -eq 0 ]; then
    echo "✅ Tests completed successfully!"
else
    echo "❌ Tests failed with exit code $TEST_EXIT_CODE"
fi

exit $TEST_EXIT_CODE
