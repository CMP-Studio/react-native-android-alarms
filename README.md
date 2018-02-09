This React Native library will allow you to schedule and show alarms on Android (tested on >= API 21). To see a working example of this module, see [Dawn Chorus](https://github.com/CMP-Studio/DawnChorus). The code for this module was modified from [Christoph Michel's App Launcher](https://github.com/MrToph/react-native-app-launcher).

## Features
* Schedules Alarms using AlarmManager
* Alarm reciever that will launch application at alarm time, even if the application is closed
* Reschedules alarms after phone boots back up
* Notifies users of alarms they may have missed when their phone was off

## Installation
* Run `npm install --save git+https://github.com/CMP-Studio/react-native-android-alarms.git`
* Add the following to `android/settings.gradle`:
    ```
    include ':react-native-android-alarms'
    project(':react-native-android-alarms').projectDir = new File(settingsDir, '../node_modules/react-native-android-alarms/android')
    ```

* Add the following to `android/app/build.gradle`:
    ```xml
    ...

    dependencies {
        ...
        compile project(':react-native-android-alarms') 
    }
    ```
* Add the following to `android/app/src/main/AndroidManifest.xml`:
    ```xml
    <manifest 
        ...
    >   
        ...

        <uses-permission android:name="android.permission.RECEIVE_BOOT_COMPLETED"/>

        ... 

        <application
            ...
        >
            <receiver android:name="com.dawnchorus.alarms.AlarmReceiver" />
            <receiver android:name="com.dawnchorus.alarms.RebootReceiver">
                <intent-filter>
                    <action android:name="android.intent.action.BOOT_COMPLETED" />
                    <action android:name="android.intent.action.QUICKBOOT_POWERON" />

                    <category android:name="android.intent.category.DEFAULT" />
                </intent-filter>
            </receiver>
            <service
              android:name="com.dawnchorus.alarms.RebootService"
              android:exported="false"/>

          ...

        </application>
    </manifest>
    ```
* Add the following to `android/app/src/main/java/**/MainApplication.java`:
    ```java
    import com.dawnchorus.alarms.AlarmPackage;  // add this for react-native-android-alarms

    public class MainApplication extends Application implements ReactApplication {

        @Override
        protected List<ReactPackage> getPackages() {
            return Arrays.<ReactPackage>asList(
                new AlarmPackage()     // add this for react-native-android-alarms
            );
        }
    }
    ```
    
    
    * In `MainActivity.java`:
    
    ```
     @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // add
        final Window win = getWindow();
        win.addFlags(WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED |
                WindowManager.LayoutParams.FLAG_DISMISS_KEYGUARD |
                WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON |
                WindowManager.LayoutParams.FLAG_TURN_SCREEN_ON |
                WindowManager.LayoutParams.FLAG_ALLOW_LOCK_WHILE_SCREEN_ON);
    }
    ```
    
 ## Usage
 
 # Receiving An Alarm
 
In Android 8.0 and above, clicking the alarm icon in the notification manager will deliver the intent as if it is an alarm. To avoid this, double check that it is the alarm time before sounding your alarm. Otherwise, handle it how you'd like.
 
 # Handling Missed Alarms
 
 

