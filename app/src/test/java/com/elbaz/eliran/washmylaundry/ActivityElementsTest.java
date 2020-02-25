package com.elbaz.eliran.washmylaundry;

import android.content.Context;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import com.elbaz.eliran.washmylaundry.adapters.PageAdapter;
import com.elbaz.eliran.washmylaundry.controllers.fragments.ListViewFragment;
import com.elbaz.eliran.washmylaundry.controllers.fragments.MapViewFragment;
import com.elbaz.eliran.washmylaundry.controllers.fragments.OrdersFragment;

import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

/**
 * Created by Eliran Elbaz on 24-Feb-20.
 */
public class ActivityElementsTest {
    Context mContext;
    FragmentManager mFragmentManager;

    @Test
    public void PageAdapter_TestNumberOfFragments_assertCorrectNumber () throws Exception{
        // Create a new PageAdapter instance
        PageAdapter pageAdapter = new PageAdapter(mContext, mFragmentManager );
        // Check that the method returns 3 as the number of fragments to show
        assertEquals(3, pageAdapter.getCount());
    }

    //Test to verify that the fragments are not returning null
    @Test
    public void Fragment1ShouldNotBeNull() throws Exception {
        Fragment mapViewFragment = new MapViewFragment();
        assertNotNull(mapViewFragment);
    }
    @Test
    public void Fragment2ShouldNotBeNull() throws Exception {
        Fragment listViewFragment = new ListViewFragment();
        assertNotNull(listViewFragment);
    }
    @Test
    public void Fragment3ShouldNotBeNull() throws Exception {
        Fragment ordersFragment = new OrdersFragment();
        assertNotNull(ordersFragment);
    }
}
