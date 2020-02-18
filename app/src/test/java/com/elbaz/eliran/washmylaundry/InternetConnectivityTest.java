package com.elbaz.eliran.washmylaundry;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

import com.elbaz.eliran.washmylaundry.utils.Utils;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.Shadows;
import org.robolectric.annotation.Config;
import org.robolectric.shadows.ShadowConnectivityManager;
import org.robolectric.shadows.ShadowNetworkInfo;

import static androidx.test.core.app.ApplicationProvider.getApplicationContext;
import static com.google.common.truth.Truth.assertThat;
import static org.junit.Assert.assertFalse;

/**
 * Created by Eliran Elbaz on 18-Feb-20.
 */
@RunWith(RobolectricTestRunner.class)
@Config(sdk = 19)
public class InternetConnectivityTest {
    private ConnectivityManager connectivityManager;
    private ShadowConnectivityManager shadowConnectivityManager;
    private ShadowNetworkInfo shadowOfActiveNetworkInfo;

    @Before
    public void setUp() throws Exception {
        connectivityManager =
                (ConnectivityManager)
                        getApplicationContext().getSystemService(Context.CONNECTIVITY_SERVICE);
        shadowOfActiveNetworkInfo = Shadows.shadowOf(connectivityManager.getActiveNetworkInfo());
    }

    @Test
    public void getActiveNetworkInfo_shouldInitializeItself() {
        assertThat(connectivityManager.getActiveNetworkInfo()).isNotNull();
    }


    @Test
    public void getActiveNetworkInfo_shouldReturnTrueCorrectly() {
        shadowOfActiveNetworkInfo.setConnectionStatus(NetworkInfo.State.CONNECTED);
        Assert.assertTrue(Utils.isInternetAvailable(getApplicationContext()));
        assertThat(connectivityManager.getActiveNetworkInfo().isConnectedOrConnecting()).isTrue();
        assertThat(connectivityManager.getActiveNetworkInfo().isConnected()).isTrue();

        shadowOfActiveNetworkInfo.setConnectionStatus(NetworkInfo.State.CONNECTING);
        assertFalse(Utils.isInternetAvailable(getApplicationContext()));
        assertThat(connectivityManager.getActiveNetworkInfo().isConnectedOrConnecting()).isTrue();
        assertThat(connectivityManager.getActiveNetworkInfo().isConnected()).isFalse();

        shadowOfActiveNetworkInfo.setConnectionStatus(NetworkInfo.State.DISCONNECTED);
        assertFalse(Utils.isInternetAvailable(getApplicationContext()));
        assertThat(connectivityManager.getActiveNetworkInfo().isConnectedOrConnecting()).isFalse();
        assertThat(connectivityManager.getActiveNetworkInfo().isConnected()).isFalse();
    }



}
