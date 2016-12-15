package edu.ksu.cis.waterquality;

import android.app.Instrumentation;
import android.content.Context;
import android.support.test.InstrumentationRegistry;
import android.support.test.runner.AndroidJUnit4;
import android.widget.Button;

import org.junit.Test;
import org.junit.runner.RunWith;

import static android.support.test.InstrumentationRegistry.getInstrumentation;
import static org.junit.Assert.*;

/**
 * Instrumentation test, which will execute on an Android device.
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */
@RunWith(AndroidJUnit4.class)
public class ExampleInstrumentedTest {

    public void useAppContext() throws Exception {
        // Context of the app under test.
        Context appContext = InstrumentationRegistry.getTargetContext();

        assertEquals("edu.ksu.cis.waterquality", appContext.getPackageName());
    }

//    public void opensAboutActivity() {
//        // register next activity that need to be monitored.
//        Instrumentation.ActivityMonitor activityMonitor = getInstrumentation().addMonitor(AboutActivity.class.getName(), null, false);
//
//        // open current activity.
//        MyActivity myActivity = getActivity();
//        final Button button = (Button) myActivity.findViewById(com.company.R.id.open_next_activity);
//        myActivity.runOnUiThread(new Runnable() {
//            @Override
//            public void run() {
//                // click button and open next activity.
//                button.performClick();
//            }
//        });
//
//        //Watch for the timeout
//        //example values 5000 if in ms, or 5 if it's in seconds.
//        NextActivity nextActivity = getInstrumentation().waitForMonitorWithTimeout(activityMonitor, 5000);
//        // next activity is opened and captured.
//        assertNotNull(nextActivity);
//        nextActivity .finish();
//    }
}
