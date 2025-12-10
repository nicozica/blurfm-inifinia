#!/bin/bash
# Blur FM - Verification Script
# Run this to check if everything is correctly implemented

echo "======================================"
echo "🎵 Blur FM - Implementation Check 🎵"
echo "======================================"
echo ""

# Colors for output
RED='\033[0;31m'
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
BLUE='\033[0;34m'
NC='\033[0m' # No Color

# Function to check if file exists and has content
check_file() {
    local file=$1
    local name=$2

    if [ -f "$file" ]; then
        local lines=$(wc -l < "$file")
        if [ "$lines" -gt 5 ]; then
            echo -e "${GREEN}✓${NC} $name ($lines lines)"
            return 0
        else
            echo -e "${RED}✗${NC} $name (too short: $lines lines)"
            return 1
        fi
    else
        echo -e "${RED}✗${NC} $name (missing)"
        return 1
    fi
}

echo "1. Checking Core Files..."
echo "─────────────────────────────────"

check_file "app/src/main/java/com/argensonix/blurfm/MainActivity.kt" "MainActivity"
check_file "app/src/main/java/com/argensonix/blurfm/StreamConfig.kt" "StreamConfig"

echo ""
echo "2. Checking Player Implementation..."
echo "─────────────────────────────────"

check_file "app/src/main/java/com/argensonix/blurfm/player/AudioPlayerManager.kt" "AudioPlayerManager"
check_file "app/src/main/java/com/argensonix/blurfm/ui/viewmodel/PlayerViewModel.kt" "PlayerViewModel"

echo ""
echo "3. Checking Data Layer..."
echo "─────────────────────────────────"

check_file "app/src/main/java/com/argensonix/blurfm/data/model/NowPlaying.kt" "NowPlaying Model"
check_file "app/src/main/java/com/argensonix/blurfm/data/repository/NowPlayingRepository.kt" "Repository"

echo ""
echo "4. Checking API Layer..."
echo "─────────────────────────────────"

check_file "app/src/main/java/com/argensonix/blurfm/data/api/ApiConfig.kt" "API Config"
check_file "app/src/main/java/com/argensonix/blurfm/data/api/ITunesApiService.kt" "iTunes API"
check_file "app/src/main/java/com/argensonix/blurfm/data/api/ITunesModels.kt" "iTunes Models"
check_file "app/src/main/java/com/argensonix/blurfm/data/api/NowPlayingApiService.kt" "Now Playing API"
check_file "app/src/main/java/com/argensonix/blurfm/data/api/NowPlayingModels.kt" "Now Playing Models"

echo ""
echo "5. Checking Dependencies..."
echo "─────────────────────────────────"

if grep -q "retrofit" gradle/libs.versions.toml; then
    echo -e "${GREEN}✓${NC} Retrofit dependency"
else
    echo -e "${RED}✗${NC} Retrofit dependency missing"
fi

if grep -q "coil" gradle/libs.versions.toml; then
    echo -e "${GREEN}✓${NC} Coil dependency"
else
    echo -e "${RED}✗${NC} Coil dependency missing"
fi

if grep -q "lifecycle-viewmodel-compose" gradle/libs.versions.toml; then
    echo -e "${GREEN}✓${NC} ViewModel Compose dependency"
else
    echo -e "${RED}✗${NC} ViewModel Compose dependency missing"
fi

echo ""
echo "6. Checking Key Features in Code..."
echo "─────────────────────────────────"

# Check for fade-in implementation
if grep -q "FADE_IN_DURATION_MS" app/src/main/java/com/argensonix/blurfm/player/AudioPlayerManager.kt; then
    echo -e "${GREEN}✓${NC} Fade-in implementation found"
else
    echo -e "${RED}✗${NC} Fade-in implementation missing"
fi

# Check for StateFlow usage
if grep -q "StateFlow" app/src/main/java/com/argensonix/blurfm/player/AudioPlayerManager.kt; then
    echo -e "${GREEN}✓${NC} StateFlow for reactive state"
else
    echo -e "${RED}✗${NC} StateFlow missing"
fi

# Check for ViewModel integration
if grep -q "viewModel()" app/src/main/java/com/argensonix/blurfm/MainActivity.kt; then
    echo -e "${GREEN}✓${NC} ViewModel integration in UI"
else
    echo -e "${RED}✗${NC} ViewModel not integrated"
fi

# Check for Coil AsyncImage
if grep -q "AsyncImage" app/src/main/java/com/argensonix/blurfm/MainActivity.kt; then
    echo -e "${GREEN}✓${NC} Coil AsyncImage for artwork"
else
    echo -e "${RED}✗${NC} AsyncImage not used"
fi

# Check for dynamic now playing
if grep -q "nowPlaying.title" app/src/main/java/com/argensonix/blurfm/MainActivity.kt; then
    echo -e "${GREEN}✓${NC} Dynamic now playing text"
else
    echo -e "${RED}✗${NC} Hardcoded text still present"
fi

echo ""
echo "7. Code Quality Checks..."
echo "─────────────────────────────────"

# Check for English comments
spanish_comments=$(grep -r "\/\/ .*[áéíóúñ¿¡]" app/src/main/java/com/argensonix/blurfm/ 2>/dev/null | wc -l)
if [ "$spanish_comments" -eq 0 ]; then
    echo -e "${GREEN}✓${NC} All comments in English"
else
    echo -e "${YELLOW}!${NC} Found $spanish_comments Spanish comments (check manually)"
fi

# Check for proper package structure
if [ -d "app/src/main/java/com/argensonix/blurfm/data" ] && \
   [ -d "app/src/main/java/com/argensonix/blurfm/player" ] && \
   [ -d "app/src/main/java/com/argensonix/blurfm/ui" ]; then
    echo -e "${GREEN}✓${NC} Clean package structure"
else
    echo -e "${RED}✗${NC} Package structure incomplete"
fi

echo ""
echo "8. Configuration Status..."
echo "─────────────────────────────────"

if grep -q "api.blurfm.com" app/src/main/java/com/argensonix/blurfm/data/api/ApiConfig.kt; then
    echo -e "${YELLOW}!${NC} Using placeholder API URL (needs configuration)"
else
    echo -e "${GREEN}✓${NC} Custom API URL configured"
fi

echo ""
echo "======================================"
echo "📊 Summary"
echo "======================================"

total_files=$(find app/src/main/java/com/argensonix/blurfm -name "*.kt" -type f | wc -l)
echo "Total Kotlin files: $total_files"

total_lines=$(find app/src/main/java/com/argensonix/blurfm -name "*.kt" -type f -exec wc -l {} + | tail -1 | awk '{print $1}')
echo "Total lines of code: $total_lines"

echo ""
echo "======================================"
echo "📝 Next Steps"
echo "======================================"
echo ""
echo "1. Configure your Now Playing API URL:"
echo "   Edit: app/src/main/java/com/argensonix/blurfm/data/api/ApiConfig.kt"
echo "   Change: NOW_PLAYING_BASE_URL"
echo ""
echo "2. Build the project:"
echo "   ./gradlew clean assembleDebug"
echo ""
echo "3. Install on device:"
echo "   ./gradlew installDebug"
echo ""
echo "4. Check logs for API calls:"
echo "   adb logcat | grep -E '(AudioPlayerManager|PlayerViewModel|NowPlayingRepository)'"
echo ""
echo "📚 Documentation:"
echo "   - IMPLEMENTATION_COMPLETE.md (full details)"
echo "   - CONFIGURATION_GUIDE.md (setup instructions)"
echo ""

