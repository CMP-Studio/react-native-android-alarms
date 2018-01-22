This React Native library will allow you to schedule and show alarms. 

## Features
* Schedules Alarms using AlarmManager
* Alarm reciever that will launch application upon alarm even if the application is closed. You will need to write the code to handle what your application does upon launch. The code for this feature was modified from [Christoph Michel's App Launcher](https://github.com/MrToph/react-native-app-launcher)
* Reschedules alarms after phone boots back up


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
 

