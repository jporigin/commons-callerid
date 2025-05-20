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
import dagger.hilt.android.HiltAndroidApp

@HiltAndroidApp
class MyApplication : CallerIdSDKApplication() {
    override fun onCreate() {
        super.onCreate()
    }
}
```

Call the function below to initialize OG-Caller-ID in your MainActivity:
```kotlin
class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        ...

        // initialize CallerID SDK
        this@MainActivity.initCallerSDK()
    }

    // CallerID
    private fun Activity.initCallerSDK() {
        (application as? CallerIdSDKApplication)?.let { callerIdSDKApplication ->
            val isInitialized = callerIdSDKApplication.initSDK(this)
            if (isInitialized) {
                callerIdSDKApplication.initSDKAds(adFormat = AdFormat.NATIVE_BIG, adUnitId = nativeAds)
            }
            callerIdSDKApplication.setUpClassToOpenApp(MainActivity::class.java, SplashActivity::class.java)
	    callerIdSDKApplication.setUpCustomView()
        }
    }

    private fun CallerIdSDKApplication.setUpCustomView() {
        val customViewBinding = ItemCustomViewBinding.inflate(layoutInflater)
        this.mCustomView = customViewBinding.root
    }
}
```

> [!NOTE]
>* To change the Caller ID ad format, use the `AdFormat` class with one of the following options: `NONE`, `BANNER`, `NATIVE_SMALL`, or `NATIVE_BIG`.
>* Please provide the correct `adUnitId` based on the selected `AdFormat`.
>* If you want users to open the app from the Caller ID screen, add two classes in the `setUpClassToOpenApp` function: `classA` and `classBHigh`.
>   - If the app is already open, it will launch `classA`.
>   - If the app is closed, it will launch `classBHigh`.
>* To use a custom caller screen, set your view to `mCustomView`.

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
