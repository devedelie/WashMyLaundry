package com.elbaz.eliran.washmylaundry;

import android.content.Context;

import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.platform.app.InstrumentationRegistry;

import com.elbaz.eliran.washmylaundry.utils.Utils;
import com.google.android.gms.maps.model.LatLng;

import org.junit.Test;
import org.junit.runner.RunWith;

import static org.junit.Assert.assertEquals;

/**
 * Instrumented test, which will execute on an Android device.
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */
@RunWith(AndroidJUnit4.class)
public class InstrumentedTest {
    @Test
    public void useAppContext() {
        // Context of the app under test.
        Context appContext = InstrumentationRegistry.getInstrumentation().getTargetContext();
        assertEquals("com.elbaz.eliran.washmylaundry", appContext.getPackageName());
    }

    @Test
    public void calculateDistance_returnCorrectDistance() {
        assertEquals( 202, Utils.calculateDistance(new LatLng(48.82735, 2.3298833), new LatLng(48.8278228, 2.3325422)));
    }
}
