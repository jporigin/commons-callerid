
# OG-Caller-ID
[![](https://jitpack.io/v/jporigin/commons-callerid.svg)](https://jitpack.io/#jporigin/commons-callerid) | [Release notes](https://sites.google.com/view/og-caller-id-release-notes)

Caller-ID SDK seamlessly integrates with your app to deliver rich call-related experiences. By showing detailed call information and context-aware features during, after, or even for missed calls, your app becomes more useful and engaging to users.
This smart enhancement delivers real value, resulting in happier users, higher retention, and more frequent engagement. Increased user interaction leads to more impressions, which directly translates into stronger and more sustainable revenue growth.

## Download

```gradle

dependencyResolutionManagement {
 repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
 repositories {
	// CallerID	
 	mavenCentral()
	maven { url = uri("https://jitpack.io") }
 }
}

dependencies {
   ...
   // CallerID
   implementation("com.github.jporigin:commons-callerid:Tag")
}

```

## How do I use OG-Caller-ID?

Check out the latest release version from our [Release notes](https://sites.google.com/view/og-caller-id-release-notes)

Simply extend the `CallerIdSDKApplication` class in your main application like this:
```kotlin
import com.origin.commons.callerid.CallerIdSDKApplication

class MyApplication : CallerIdSDKApplication() {
    override fun onCreate() {
        super.onCreate()
        initCallerSDK()
    }

    // initialize caller id sdk
    private fun initCallerSDK() {
        setUpAdsUIDs()
        setUp(R.drawable.app_logo_, R.drawable.app_logo_icon)
        openClass1 = { MainActivity::class.java }
        openClass2High = { SplashActivity::class.java }
        customHomeFragment = { CIHomeScreenFragment() }
    }	
    private fun setUpAdsUIDs() {
       setUpAdsIDs(
          nativeBigIds = listOf(Utils.nativeBigId1, Utils.nativeBigId2, Utils.nativeBigId3),
          nativeSmallIds = listOf(Utils.nativeSmallId1, Utils.nativeSmallId2),
          bannerIds = listOf(Utils.bannerId1, Utils.bannerId2)
       )
    }
}
```
> [!NOTE]
>* please provide appropriate ads IDs `nativeBigIds`, `nativeSmallIds`, `bannerIds`.
>* If you want to open the Caller ID settings activity directly, set `openSettingClass`.
>* To open the app from the Caller ID screen, define `openClass1` and `openClass2High`:
   >   - If the app is already open, it will launch `openClass1`.
>   - If the app is closed, it will launch `openClass2High`.
>* To use a custom Caller ID home screen, set your fragment to `customHomeFragment`.

### 🔧 Caller Screen Feature Toggles

You can customize the caller screen behavior by enabling or disabling specific features through the following flags:

```kotlin
isMissedCallFeatureEnable = true  // Show caller screen on missed calls
isCompleteCallFeatureEnable = true  // Show caller screen when a call ends
isNoAnswerFeatureEnable = true  // Show caller screen if the call is not answered
```
| Flag                          | Description                                     | Behavior When `true`                   | Behavior When `false`                         |
| ----------------------------- | ----------------------------------------------- | -------------------------------------- | --------------------------------------------- |
| `isMissedCallFeatureEnable`   | Control screen display on missed calls          | Shows caller screen on missed calls    | Caller screen will not appear on missed calls |
| `isCompleteCallFeatureEnable` | Control screen display when a call is completed | Shows caller screen when a call ends   | No screen shown after call ends               |
| `isNoAnswerFeatureEnable`     | Control screen display on unanswered calls      | Shows caller screen on no-answer calls | No screen shown if the call isn’t answered    |
```kotlin
(application as? CallerIdSDKApplication)?.let { callerIdSDKApplication ->

    _binding.switchMissCall.isChecked = callerIdSDKApplication.getMissedCallFeatureEnable() == true

    _binding.switchMissCall.setOnCheckedChangeListener { _, isChecked ->
        callerIdSDKApplication.setMissedCallFeatureEnable(isChecked)
    }
}
```
