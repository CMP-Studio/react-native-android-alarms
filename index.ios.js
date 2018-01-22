'use strict';

var fail = 'Cannot use react-native-android-alarms on IOS.';
class AndroidAlarms {

  static setAlarm() {
    console.warn(fail);
  }

  static clearAlarm() {
    console.warn(fail);
  }

}

module.exports = AndroidAlarms;