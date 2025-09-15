
# Caller-ID
![JitPack Version](https://img.shields.io/badge/JitPack-1.0.1-green)  |  [Release notes](https://sites.google.com/view/og-caller-id-release-notes)

Caller-ID SDK seamlessly integrates with your app to deliver rich call-related experiences. By showing detailed call information and context-aware features during, after, or even for missed calls, your app becomes more useful and engaging to users.
This smart enhancement delivers real value, resulting in happier users, higher retention, and more frequent engagement. Increased user interaction leads to more impressions, which directly translates into stronger and more sustainable revenue growth.

## Download

```gradle

dependencyResolutionManagement {
 repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
 repositories {
	// CallerID	
 	mavenCentral()
	maven {
            url = uri("https://jitpack.io")
            credentials {
                username = "-----Get the key from your TL or PM-----"
                password = "-----Get the key from your TL or PM-----"
            }
    }
 }
}

dependencies {
   ...
   // CallerID
   implementation 'com.github.ogmediaapps18:callerid:Tag'
}

```

## How do I use OG-Caller-ID?

Check out the latest release version from our [Release notes](https://sites.google.com/view/og-caller-id-release-notes)

Simply extend the `CallerIdSDKApplication` class in your main application like this:

`Kotlin`
```kotlin
import com.ogmediaapps.callerid.CallerIdSDKApplication

class MyApplication : CallerIdSDKApplication() {
    override fun onCreate() {
        super.onCreate()
        initCallerSDK()
    }

    // initialize caller id sdk
    private fun initCallerSDK() {
        CallerIdSDK.init(this) // Call this if you have some default app to check (e.g message)
        openClassForDefaultApp = ActivityClassProvider { MainActivity::class.java } // ActivityClassProvider to open when user click on set as default app
        setUpAdsUIDs()  // set up this to show ad on CallerIdScreen
        setUp(R.drawable.app_logo_, R.drawable.app_logo_icon) // set up logo
        notifyOverlayDenied(true) // notify if overlay permission is denied
        setUpNotificationConfig(NotificationConfig(pendingClass = PermissionActivity::class.java)) // set up notification config for notify overlay permission is denied provide pendingClass which you want to open on click (default takes openClass1 ActivityClassProvider or openClass2High if provided)
        openClass1 = ActivityClassProvider { MainActivity::class.java } // ActivityClassProvider to open when user click on notification or any click where host app interacts on first priority
        openClass2High = ActivityClassProvider { SplashActivity::class.java } // ActivityClassProvider to open when user click on notification or any click where host app interacts on second priority
        customHomeFragment = FragmentClassProvider { CIHomeScreenFragment() } // FragmentClassProvider to set up custom first fragment of caller screen
        customCallerSetting = ActivityClassProvider { CallerSettingActivity::class.java } // ActivityClassProvider to set up custom caller setting screen
    }

    private fun setUpAdsUIDs() {
       setUpAdsIDs(
          nativeBigIds = listOf(Utils.nativeBigId1, Utils.nativeBigId2, Utils.nativeBigId3),
          nativeSmallIds = listOf(Utils.nativeSmallId1, Utils.nativeSmallId2),
          bannerIds = listOf(Utils.bannerId1, Utils.bannerId2)
       )
    }

    override fun onCallerThemeChanged(themeConfig: ThemeConfig) {}
}
```
`Java`
```Java
import com.ogmediaapps.callerid.CallerIdSDKApplication;

public class MyApplication extends CallerIdSDKApplication {

    @Override
    public void onCreate() {
        super.onCreate();
        initCallerSDK();
    }

  	// initialize caller id sdk
    private void initCallerSDK() {
        setUpAdsUIDs();
        setUp(R.drawable.app_logo_, R.drawable.app_logo_icon);
        setOpenClass1(() -> MainActivity.class);
        setOpenClass2High(() -> SplashActivity.class);
        setCustomHomeFragment(CIHomeScreenFragment::new);
    }
    
    private void setUpAdsUIDs() {
        setUpAdsIDs(
                Arrays.asList(Utils.INSTANCE.getNativeBigId1(), Utils.INSTANCE.getNativeBigId2(), Utils.INSTANCE.getNativeBigId3()),
                Arrays.asList(Utils.INSTANCE.getNativeSmallId1(), Utils.INSTANCE.getNativeSmallId2()),
                Arrays.asList(Utils.INSTANCE.getBannerId1(), Utils.INSTANCE.getBannerId2())
        );
    }

    @Override
    public void onCallerThemeChanged(@NotNull ThemeConfig themeConfig) {}
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
