# Goosebumps Rider App

Complete Android Rider Delivery App — Kotlin + Jetpack Compose + MVVM Clean Architecture

## Setup

### 1. Geoapify API Key
Already wired in `app/build.gradle.kts`:
```kotlin
buildConfigField("String", "GEOAPIFY_API_KEY", "\"ce18878e786a43d4b5915b8c3cc358c7\"")
```
Maps are rendered via `GeoapifyMapView` (Leaflet.js + WebView) using the **dark-matter** tile style.
Routing uses the Geoapify Routing API (`/v1/routing`) for real polylines and ETAs.

### 2. Firebase
Replace `app/google-services.json` with your real Firebase project file from the Firebase Console.

### 3. Backend URLs
In `app/build.gradle.kts`:
```kotlin
buildConfigField("String", "BASE_URL", "\"https://api.yourbackend.com/v1/\"")
buildConfigField("String", "SOCKET_URL", "\"https://socket.yourbackend.com\"")
```

## Architecture

```
goosebumpsRider app/
├── app/src/main/java/com/goosebumps/rider/
│   ├── data/
│   │   ├── local/          # Room DB, DataStore
│   │   ├── remote/         # Retrofit API, DTOs
│   │   ├── repository/     # Repository implementations
│   │   └── socket/         # Socket.IO manager
│   ├── di/                 # Hilt modules
│   ├── domain/
│   │   ├── model/          # Domain models
│   │   ├── repository/     # Repository interfaces
│   │   ├── usecase/        # Business logic
│   │   └── util/           # Result wrapper
│   ├── service/            # Foreground location + FCM
│   └── ui/
│       ├── components/     # Reusable Compose components
│       ├── navigation/     # NavGraph + Routes
│       ├── screens/        # 14 screens with ViewModels
│       └── theme/          # Material 3 dark theme
```

## Screens
1. Splash — animated logo
2. Login — phone + country code
3. OTP — 6-digit with countdown
4. Home Dashboard — online toggle, earnings, surge, fatigue alert
5. Incoming Order — 30s countdown, accept/decline
6. Pickup Navigation — Google Maps + call restaurant
7. Pickup Confirmation — item list, waiting timer
8. Delivery Navigation — Google Maps + call customer
9. Delivery OTP — 4-digit verification
10. Delivery Success — animated earnings summary
11. Earnings Dashboard — bar charts, weekly/monthly
12. Order History — search, infinite scroll, pull-to-refresh
13. Profile — stats, SOS button, logout
14. Settings — language (Kannada/Hindi/Odia/Bengali), battery saver, voice nav

## Unique Features
- Fatigue/stress indicator on dashboard
- Smart battery saver mode (adaptive GPS interval)
- Emergency SOS button
- Restaurant delay compensation tracker
- Hyperlocal language support (5 languages)
- Surge pricing indicator
- Safe zone alerts placeholder
