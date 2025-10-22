# MyTracker

A minimal habit-tracking Android app built with Kotlin and Jetpack Compose. Track one goal at a time with time-window based progress tracking and humorous notifications.

## Features

- ✅ **Single Active Goal**: Focus on one goal at a time
- ⏰ **Time Windows**: Define specific time windows for each day
- 📊 **Progress Tracking**: Visual progress bar showing completion percentage
- 🔔 **Smart Notifications**: Humorous reminders for missed windows and praise for completed ones
- 🚫 **No Editing**: Simple, straightforward goal creation without complexity
- 🗑️ **Easy Cancellation**: Delete goal and all history with one action
- 🔄 **Boot Persistence**: Notifications reschedule after device reboot

## Architecture

- **UI Layer**: Jetpack Compose with Material 3
- **Architecture Pattern**: MVVM (ViewModel + Repository)
- **Database**: Room for local persistence
- **Background Work**: WorkManager for notification scheduling
- **Dependency Injection**: Hilt
- **Concurrency**: Kotlin Coroutines + Flow

## Tech Stack

- **Language**: Kotlin
- **UI**: Jetpack Compose
- **Database**: Room
- **DI**: Hilt
- **Background**: WorkManager
- **Min SDK**: 21
- **Target SDK**: 34
- **Build**: Gradle Kotlin DSL

## Project Structure

```
app/src/main/java/com/mytracker/app/
├── data/
│   ├── dao/              # Room DAOs
│   ├── database/         # Room Database
│   ├── entity/           # Room Entities
│   └── repository/       # Repository layer
├── domain/
│   ├── model/            # Domain models
│   └── validation/       # Validation logic
├── ui/
│   ├── activegoal/       # Active Goal screen
│   ├── creategoal/       # Create Goal screen
│   ├── navigation/       # Navigation setup
│   └── theme/            # Material 3 theme
├── utils/                # Utilities (TimeProvider, NotificationHelper, etc.)
├── worker/               # WorkManager workers
├── receiver/             # BroadcastReceivers
├── di/                   # Hilt modules
├── MainActivity.kt
└── MyTrackerApplication.kt
```

## How to Build & Run

### Prerequisites

- Android Studio (latest stable version recommended: Hedgehog or newer)
- JDK 17
- Android SDK with API level 34
- Git

### Setup Instructions

1. **Clone or open the project**
   ```bash
   cd MyTracker
   ```

2. **Open in Android Studio**
   - Launch Android Studio
   - Select "Open an Existing Project"
   - Navigate to the MyTracker folder
   - Wait for Gradle sync to complete

3. **Build the project**
   ```bash
   ./gradlew build
   ```
   On Windows:
   ```bash
   gradlew.bat build
   ```

4. **Run on emulator or device**
   - Connect an Android device or start an emulator
   - Click the "Run" button (green triangle) in Android Studio
   - Or use command line:
   ```bash
   ./gradlew installDebug
   ```

5. **Build APK**
   ```bash
   ./gradlew assembleDebug
   ```
   The APK will be located at: `app/build/outputs/apk/debug/app-debug.apk`

### Running Tests

**Unit Tests**
```bash
./gradlew test
```

**Instrumentation Tests**
```bash
./gradlew connectedAndroidTest
```

**All Tests**
```bash
./gradlew testDebugUnitTest connectedAndroidTest
```

## How It Works

### Goal Creation

1. App opens to **Create Goal** screen if no active goal exists
2. User enters:
   - Goal title (e.g., "Exercise Daily")
   - Number of days (e.g., 30)
   - Time windows (e.g., 08:00-10:00, 18:00-20:00)
3. Validation ensures:
   - No overlapping windows
   - Windows don't cross midnight
   - At least one window defined
4. On creation, notifications are scheduled for each window

### Active Goal

1. Shows current progress as percentage and progress bar
2. Displays today's time windows with completion status
3. **Big button** to record progress:
   - Only works inside a defined time window
   - One press per window per day
   - Shows confirmation message on success
4. **Cancel Goal** button deletes everything

### Notifications & Scheduling

- **Implementation**: WorkManager with OneTimeWorkRequest for each window
- **Missed Window Check**: At the end of each time window, a worker checks if the user pressed the button
- **Notifications**:
  - If missed → humorous scold message (random from array)
  - If pressed → optional praise notification
- **Boot Persistence**: BootReceiver reschedules all notifications after device reboot

#### WorkManager Strategy

Each time window gets a OneTimeWorkRequest scheduled for its end time:
- Goal with 2 windows/day for 7 days = 14 scheduled work requests
- Work is tagged by goal ID for easy cancellation
- On boot, all work is rescheduled via BootReceiver

### Data Model

**GoalEntity**
- `id`, `title`, `startDate`, `durationDays`, `timeWindows[]`, `isActive`

**TimeWindow** (embedded in Goal)
- `index`, `startHour`, `startMinute`, `endHour`, `endMinute`

**PressRecordEntity**
- `id`, `goalId`, `windowIndex`, `date`, `timestamp`
- Unique constraint: (goalId, windowIndex, date)

### Progress Calculation

```
Progress % = (Total Presses / (Duration Days × Windows Per Day)) × 100
```

Example: 7-day goal with 2 windows/day
- Total required presses: 7 × 2 = 14
- After 5 presses: 5/14 = 35.7%

## Sample Messages

### Praise (when you press the button)
- "Nice! You did it — tiny legend! 🌟"
- "Goal ticked — high five! ✋"
- "You pressed the button and the universe approved. ✨"

### Scold (when you miss a window)
- "Hey! You missed your slot — lazybones 😜"
- "No press detected — the chair wins today. 🪑"
- "You forgot! Don't worry, tomorrow's another chance. 📅"

All messages are defined in `app/src/main/res/values/strings.xml`

## Permissions

Required permissions (defined in AndroidManifest.xml):
- `POST_NOTIFICATIONS` - For sending notifications
- `RECEIVE_BOOT_COMPLETED` - For rescheduling after reboot
- `SCHEDULE_EXACT_ALARM` - For precise notification timing
- `USE_EXACT_ALARM` - Android 14+ exact alarm permission
- `WAKE_LOCK` - For WorkManager reliability

## Limitations & Known Issues

1. **Time Windows**: Must be within same calendar day (no midnight crossing)
2. **Single Goal Only**: Only one active goal at a time (by design)
3. **No Editing**: Goals cannot be modified after creation (by design)
4. **No History**: Cancelling a goal deletes all data (by design)
5. **DST Handling**: Time windows follow local wall-clock time; DST transitions may cause minor timing shifts
6. **Notification Limits**: On some devices with aggressive battery optimization, notifications may be delayed

## Testing Strategy

### Unit Tests (app/src/test/)
- `GoalValidatorTest` - Input validation logic
- `GoalTest` - Progress calculation formulas
- `TimeWindowTest` - Window validation and overlap detection
- `WindowCheckerTest` - Current window detection logic

### Instrumentation Tests (app/src/androidTest/)
- `CreateGoalFlowTest` - Full goal creation flow
- `ActiveGoalScreenTest` - Active goal screen interactions

Run tests to verify:
- Progress calculation is correct
- Time window validation works (no overlap, no midnight crossing)
- Press acceptance logic (one per window per day)
- UI navigation flows

## Development

### Adding New Messages

Edit `app/src/main/res/values/strings.xml`:
```xml
<string-array name="praise_messages">
    <item>Your new message here! 🎉</item>
</string-array>
```

### Modifying Time Provider (for testing)

The `TimeProvider` interface allows mocking system time:
```kotlin
// Production
class SystemTimeProvider : TimeProvider { ... }

// Testing
class FakeTimeProvider : TimeProvider {
    var currentTime: LocalTime = ...
}
```

### Dependencies

Key dependencies (see `app/build.gradle.kts` for versions):
- Jetpack Compose BOM
- Room (runtime, ktx, compiler)
- Hilt (android, compiler, navigation-compose, work)
- WorkManager
- Coroutines
- Navigation Compose
- JUnit, Mockk (testing)

## Troubleshooting

**Gradle sync fails**
- Check internet connection
- Invalidate caches: File → Invalidate Caches / Restart

**Notifications not working**
- Check notification permissions in app settings
- Disable battery optimization for the app
- On Android 13+, ensure POST_NOTIFICATIONS permission is granted

**App crashes on startup**
- Check logcat for Room database errors
- Clear app data and reinstall

**WorkManager not scheduling**
- Check battery optimization settings
- Verify SCHEDULE_EXACT_ALARM permission (Android 12+)

## Future Enhancements (Not Implemented)

- Goal history/archiving
- Multiple concurrent goals
- Editable goals
- Statistics and charts
- Export/import data
- Goal templates
- Customizable notifications

## License

This project is for demonstration purposes.

## Author

Built with Kotlin, Jetpack Compose, and ☕

