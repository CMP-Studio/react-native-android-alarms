'use strict'

import { NativeModules } from 'react-native'

export default class AndroidAlarms {
    /**
     * Sets or updates an alarm through Android's AlarmManager system with the specified `id` to fire at the specified
     * UNIX timestamp `timestamp`. If parameter `inexact` is set to true, the battery-saving version of
     * AlarmManager is used that can fire at inexact times. Default value for `inexact` is false.
     */
    static setAlarm(id, timestamp, inexact){
        if(!(typeof id === 'string' || typeof id === 'number'))
            throw new Error('AndroidAlarms.setAlarm: `id` must be a number or a string.')
        if(typeof timestamp !== 'number')
            throw new Error('AndroidAlarms.setAlarm: `timestamp` must be an integer.')
        id = id.toString()  // parse to string
        timestamp = Math.trunc(timestamp)   // parse to int
        inexact = !!inexact // parse to bool
        NativeModules.AndroidAlarms.setAlarm(id, timestamp, inexact)
    }

    /**
     * Clears the alarm specified by `id`
     */
    static clearAlarm(id) {
        if(!(typeof id === 'string' || typeof id === 'number'))
            throw new Error('AndroidAlarms.clearAlarm: `id` must be a number or a string.')
        id = id.toString()  // parse to string
        NativeModules.AndroidAlarms.clearAlarm(id)
    }
}
