package one.moveo.simpleexample;

import android.app.Application;


import one.moveo.androidlib.MoveoOne;

public class SimpleAppApplication extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        // Initialize MoveoOne SDK
        MoveoOne.getInstance().initialize("YOUR_SDK_TOKEN");
        // (Optional) Identify user
        // MoveoOne.getInstance().identify("USER_ID");
        // Start tracking session (before first tick event)
        MoveoOne.getInstance().start("main_activity_semantic");
    }
}
