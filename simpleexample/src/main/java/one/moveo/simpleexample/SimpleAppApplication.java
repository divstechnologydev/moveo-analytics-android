package one.moveo.simpleexample;

import android.app.Application;

import one.moveo.androidlib.MoveoOne;

public class SimpleAppApplication extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        MoveoOne moveoOne = MoveoOne.getInstance();
        moveoOne.setLogging(true);
        moveoOne.initialize("YOUR_TOKEN_GOES_HERE");
        moveoOne.start("context");
    }
}
