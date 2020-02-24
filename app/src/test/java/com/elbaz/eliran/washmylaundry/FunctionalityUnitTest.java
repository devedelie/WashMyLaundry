package com.elbaz.eliran.washmylaundry;

import android.content.Context;

import androidx.fragment.app.FragmentManager;

import com.elbaz.eliran.washmylaundry.adapters.PageAdapter;
import com.elbaz.eliran.washmylaundry.models.Constants;
import com.elbaz.eliran.washmylaundry.utils.Utils;

import org.junit.Test;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import static org.junit.Assert.assertEquals;

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */
public class FunctionalityUnitTest {
    Context mContext;
    FragmentManager mFragmentManager;

    @Test
    public void todayDate_verifyFormat_isCorrect() throws Exception {
        DateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
        assertEquals(dateFormat.format(new Date()), Utils.getTodayDateFormat());
    }

    @Test
    public void todayDateAndTime_verifyFormat_isCorrect() throws Exception {
        DateFormat dateFormat = new SimpleDateFormat("ddMMyyyy_HHmmss", Locale.getDefault());
        assertEquals(dateFormat.format(new Date()), Utils.getDateForOrderId());
    }

    @Test
    public void orderStatus_verifyStatus_returnCorrectStatus() {
        assertEquals( "Waiting for approval", Utils.getOrderStatus(1));
        assertEquals( "Approved and in process", Utils.getOrderStatus(2));
        assertEquals( "Order is Ready for delivery", Utils.getOrderStatus(3));
        assertEquals( "Order delivered", Utils.getOrderStatus(4));
    }

    @Test
    public void orderStatusColor_verifyStatusColor_returnCorrectStatusColor() {
        assertEquals( R.color.order_waiting, Utils.getOrderStatusColor(1));
        assertEquals( R.color.order_approved, Utils.getOrderStatusColor(2));
        assertEquals( R.color.order_ready, Utils.getOrderStatusColor(3));
        assertEquals( R.color.order_delivered, Utils.getOrderStatusColor(4));
    }


    @Test
    public void ConstantsURL_verifyStaticMapURL_returnCorrectURL() {
        assertEquals("https://maps.googleapis.com/maps/api/staticmap?center=", Constants.IMAGE_URL_PART1);
        assertEquals("&zoom=15&size=120x120&maptype=roadmap&markers=color:blue%7Clabel:S%7C", Constants.IMAGE_URL_PART2);
        assertEquals("&key=", Constants.IMAGE_URL_PART3);
    }
    @Test
    public void ConstantsChatChannel_verifyChannelString_returnCorrectChannel() {
        assertEquals("chat" , Constants.CHANNEL_ID);
    }

    @Test
    public void Rating_verifyStarRatingCalculator_returnCorrectStarRating() {
        double expectedStars = 5;
        assertEquals(Utils.rating(95, 20), expectedStars, 0.5 ); // 4.75
        expectedStars--; // 4
        assertEquals(Utils.rating(95, 22), expectedStars, 0.5 ); // 4.31
        assertEquals(Utils.rating(95, 25), expectedStars, 0.5 ); // 3.8
        expectedStars--; // 3
        assertEquals(Utils.rating(95, 28), expectedStars, 0.5 ); // 3.39
        assertEquals(Utils.rating(95, 32), expectedStars, 0.5 ); // 2.96
        expectedStars--; // 2
        assertEquals(Utils.rating(95, 40), expectedStars, 0.5 ); // 2.375
        assertEquals(Utils.rating(95, 50), expectedStars, 0.5 ); // 1.9
        expectedStars--; // 1
        assertEquals(Utils.rating(95, 65), expectedStars, 0.5 ); // 1.46
        assertEquals(Utils.rating(95, 80), expectedStars, 0.5 ); // 1.11
    }


    @Test
    public void PageAdapter_TestNumberOfFragments_assertCorrectNumber () throws Exception{
        // Create a new PageAdapter instance
        PageAdapter pageAdapter = new PageAdapter(mContext, mFragmentManager );
        // Check that the method returns 3 as the number of fragments to show
        assertEquals(3, pageAdapter.getCount());
    }
}