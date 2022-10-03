## ![icon](https://i.imgur.com/hVQ0Pfe.png)&nbsp;&nbsp; SyncNote

### SyncNote

- Sync (simple text) Android notes with your Desktop PC, and vice-versa, over local WiFi!

### &nbsp;&nbsp;&nbsp;Screenshots:

| Desktop(Linux) | Android |
| ![](https://i.imgur.com/frWHVnD.png)| ![](https://i.imgur.com/ma57ccg.png) |
 
- Released for Android, with Windows & Linux. Currently in development.

- Does not require cloud sign-in, sync your notes locally  after simply scanning a QR code.

- Notes sync automatically when paired devices are connected to the same network.

- Developed using Kotlin & Jetpack Compose (multi-platform), and the ktor library for networking.


### Building

## Desktop
 From the root directory, from the terminal, run:
 `./gradlew :desktop:run`
 For Linux packaging, simply run:
 `./gradlew :desktop:packageDeb`
 If you require a Windows package build, run:
 `./gradlew.bat :desktop:packageMsi`

