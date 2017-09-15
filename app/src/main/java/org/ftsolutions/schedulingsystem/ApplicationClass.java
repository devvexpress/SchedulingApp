package org.ftsolutions.schedulingsystem;

import com.activeandroid.ActiveAndroid;

/**
 * Created by Andrew on 2017-09-15.
 */

public class ApplicationClass extends com.activeandroid.app.Application{
    @Override
    public void onCreate() {
        super.onCreate();
        ActiveAndroid.initialize(this);
    }
}