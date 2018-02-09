This React Native library will allow you to schedule and show alarms on Android (tested on >= API 21). To see a working example of this module, see [Dawn Chorus](https://github.com/CMP-Studio/DawnChorus). The code for this module was modified from [Christoph Michel's App Launcher](https://github.com/MrToph/react-native-app-launcher).

## Features
* Schedules alarms using AlarmManager
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
    
    
* In `MainActivity.java`, 1) Add flags to Window that allow it to open over lockscreen and 2) Extend ReactActivityDelegate to pass data from the native module to your react native code as initial props
    
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
    
    public static class AlarmActivityDelegate extends ReactActivityDelegate {
        private static final String ALARM_ID = "alarmID";
        private static final String MISSED_ALARMS = "missedAlarms";
        private Bundle mInitialProps = null;
        private final @Nullable Activity mActivity;

        public AlarmActivityDelegate(Activity activity, String mainComponentName) {
            super(activity, mainComponentName);
            this.mActivity = activity;
        }

        @Override
        protected void onCreate(Bundle savedInstanceState) {
            mInitialProps = new Bundle();
            // bundle is where we put our alarmID with launchIntent.putExtra
            Bundle bundle = mActivity.getIntent().getExtras();
            if (bundle != null && bundle.containsKey(ALARM_ID)) {
                // put any initialProps here
                mInitialProps.putString(ALARM_ID, bundle.getString(ALARM_ID));
            }
            if (bundle != null && bundle.containsKey(MISSED_ALARMS)) {
                // put any initialProps here
                mInitialProps.putString(MISSED_ALARMS, bundle.getString(MISSED_ALARMS));
            }
            super.onCreate(savedInstanceState);
        }

        @Override
        protected Bundle getLaunchOptions() {
            return mInitialProps;
        }
    };

    @Override
    protected ReactActivityDelegate createReactActivityDelegate() {
        return new AlarmActivityDelegate(this, getMainComponentName());
    }
    ```
    
 ## Usage
 
 ### Scheduling Alarms
 ```
 import AndroidAlarms from 'react-native-android-alarms';
 import moment from 'moment';
 
 alarmTime = moment(); // Edit this moment object to your correct time...
 
 // Set the alarm and return the time 
 AndroidAlarms.setAlarm(alarmID, alarmTime.valueOf(), false);
 ```
 
 ### Clearing Alarms
 ```
 AndroidAlarms.clearAlarm(alarmID);
 ```

 ### Reading data in React Native app
 
If you extended your ReactActivityDelegate as shown above, you can grab the initial data from this module by adding to your main app component
 
 ```
 static propTypes = {
    alarmID: PropTypes.string,
    missedAlarms: PropTypes.string,
  }
 ```
 And access those props elsewhere in the component with ```this.props.alarmID``` and ```this.props.missedAlarms```
 
 ### Receiving An Alarm
 
If the app was launched by an alarm, the alarmID will hold the id of the alarm that went off. If the app was not launched from an alarm, ```alarmID = undefined```.

NOTE: In Android 8.0 and above, clicking the alarm icon in the Android notification drawer will launch the app and include the alarmID as an initial prop. To avoid this setting off the alarm, double check that it is the alarm time before sounding your alarm.
 
 ### Handling Missed Alarms
 
 If the user missed an alarm becuase their phone was off, when they turn their phone on, this module will present them with a notification telling them that they missed an alarm.
 Missed alarms is delivered as a String. For example, if you missed alarms with ids 3, 5, and 7 ```missedAlarms = "3,5,7,"```. If there are no missed alarms, ```missedAlarms = undefined```.
  

