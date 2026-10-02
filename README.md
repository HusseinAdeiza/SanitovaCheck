# SanitovaCheck

**WASH risk assessment and education for Android.**

SanitovaCheck lets someone photograph a water source or sanitation facility, add a location and what they observe, and receive an AI-generated risk assessment — plus WASH education content covering handwashing, cholera prevention, safe water storage and sanitation.

Built and published by [Sanitova Environmental Health Services Ltd](https://play.google.com/store/apps/developer?id=Sanitova+Environmental+Health+Services).

**Live on [Google Play](https://play.google.com/store/apps/details?id=com.sanitova.sanitovacheck).**

## What it does

- **Scan** — photograph a water source or sanitation facility, submit with a location and observations, and get a risk assessment with observed indicators, a risk level, and recommended action.
- **Learn** — WASH articles with references, plus educational video links.
- **Ask AI** — general WASH questions answered in plain language.
- **History** — every assessment saved and revisitable on-device.
- **Pro** — PDF report export and extended history. Free tier includes the full assessment flow.

## Limits, stated plainly

A photograph cannot confirm microbial contamination and cannot establish that water is safe to drink. The app flags **visible indicators** — discolouration, leaks, proximity to a contamination source — so a user knows whether to act and whether to escalate to a professional. It is a first check, not a laboratory result, and not a replacement for an official inspection by a licensed Environmental Health Officer.

The app is independent and is not affiliated with or endorsed by WHO, UNICEF, or CDC. Educational materials from those organisations are cited as references, not partnerships.

## Stack

| Layer | Choice |
|---|---|
| Language / UI | Kotlin, Jetpack Compose, Material 3 |
| Camera | CameraX (photo + video capture) |
| Auth | Firebase Authentication (email/password + Google via Credential Manager) |
| Backend | Python (Flask), Alibaba Cloud Function Compute |
| AI | Qwen |
| Billing | RevenueCat SDK + Google Play Billing |
| Build | Gradle KTS, AGP with R8/ProGuard shrinking |

## Project layout

```
app/
  src/main/java/com/sanitova/sanitovacheck/
    AuthRepository.kt          Firebase auth wrapper + friendly error mapping
    AuthScreen.kt              Sign-in / sign-up / reset UI
    CameraCapture.kt           CameraX photo capture
    VideoCapture.kt            CameraX video capture (max 12s, silent)
    ScanScreen.kt              Assessment flow and submission
    ReportScreen.kt            Risk report, photo verification, PDF export
    HistoryScreen.kt           Saved assessments
    LearnScreen.kt             Articles, videos, AI chat
    HomeScreen.kt              Dashboard
    SettingsScreen.kt          Account, subscription, restore
    PaywallScreen.kt           RevenueCat paywall
    SubscriptionRepository.kt  Entitlement state
    MainApplication.kt         RevenueCat configuration
    WashApiClient.kt           Retrofit client for the AI backend
    WashApiModels.kt           API request/response models
    ImageUtils.kt              Image downsampling, video frame extraction
    ScanHistoryStore.kt        Local scan persistence
    PdfReportGenerator.kt      PDF compliance report
    FreeScanTracker.kt         Free-tier scan limits
    PdfExportTracker.kt        Free-tier export limits
    Navigation.kt              NavHost routes
```

## Building

Requires `keystore.properties` at the repo root (see `keystore.properties.example`) with your upload keystore for release builds, and `app/google-services.json` from the Firebase console. Neither is committed.

```bash
./gradlew assembleDebug      # debug APK
./gradlew bundleRelease      # signed AAB for Play upload
```

## Security notes

Signing keys, `keystore.properties`, and `google-services.json` are git-ignored and must never be committed. The RevenueCat Android SDK key in `MainApplication.kt` is a public client key intended for shipping in apps; the secret API key is server-side only.