package com.yellowdesks.android;

import android.os.Build;

public class Developerdevices {
    public static String[] devices = {
            "043059138291dd6f" /*armin*/,

    };

    public static Boolean isDeveloperDevice() {
        for (String s : Developerdevices.devices)
            if (Build.SERIAL.equals( s ))
                return true;

        return false;
    }
}
