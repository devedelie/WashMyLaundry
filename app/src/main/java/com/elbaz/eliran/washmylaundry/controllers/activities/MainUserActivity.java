package com.elbaz.eliran.washmylaundry.controllers.activities;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Switch;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.lifecycle.ViewModelProvider;
import androidx.viewpager.widget.ViewPager;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.elbaz.eliran.washmylaundry.R;
import com.elbaz.eliran.washmylaundry.adapters.PageAdapter;
import com.elbaz.eliran.washmylaundry.base.BaseActivity;
import com.elbaz.eliran.washmylaundry.controllers.fragments.bottomSheets.EditUserBottomSheet;
import com.elbaz.eliran.washmylaundry.models.User;
import com.elbaz.eliran.washmylaundry.viewmodel.UserViewModel;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationView;
import com.google.gson.Gson;

import butterknife.BindView;

import static android.content.ContentValues.TAG;

public class MainUserActivity extends BaseActivity implements NavigationView.OnNavigationItemSelectedListener, ViewPager.OnPageChangeListener {
    @BindView(R.id.toolbar) Toolbar toolbar;
    @BindView(R.id.main_user_activity_drawerLayout) DrawerLayout drawerLayout;
    @BindView(R.id.drawer_main_user_activity) NavigationView navigationView;
    @BindView(R.id.activity_main_bottom_navigation) BottomNavigationView bottomNavigationView;
    @BindView(R.id.activity_main_user_viewpager) ViewPager pager;
    @BindView(R.id.user_delivery_switch) Switch userDeliverySwitch;
    @BindView(R.id.delivery_switch_container) LinearLayout deliverySwitchContainer;
    @BindView(R.id.information_container) LinearLayout informationContainer;
    //For Data
    private Context mContext;
    private View rootView;
    public static User mUser;
    private UserViewModel mUserViewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mContext = this;
        // Get RootView for snackBarMessage
        rootView = getWindow().getDecorView().getRootView();

        configureViewModel();
        configureDrawerLayoutAndNavigationView();
        configureBottomNavigation();
        configureViewPager();
        configureSwitches();
    }

    @Override
    public int getActivityLayout() {
        return R.layout.activity_main_user;
    }


    @Override
    protected void onResume() {
        super.onResume();
        configureDataObserver();
    }

    //-------------------
    // CONFIGURATIONS
    //-------------------

    private void configureViewModel() {
        mUserViewModel = new ViewModelProvider(this).get(UserViewModel.class);
        mUserViewModel.init(); // To retrieve the data from the repository
        mUserViewModel.setCurrentUserData(); // Trigger the Document listener
        mUserViewModel.setOrderList(getCurrentUser().getUid());
    }

    private void configureDataObserver() {
        mUserViewModel.getCurrentUserData().observe(this, this::updateObjectWithData);
    }

    private void updateObjectWithData(User user) {
        mUser = new User();
        mUser = user;
    }

    private void configureSwitches() {

        // ----- Delivery Switch -------//
        userDeliverySwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                Log.d(TAG, "onCheckedChanged: Delivery Switch " + isChecked);
                if (isChecked)  {mUserViewModel.setProviderList("isDelivering");} // Trigger the listener to get ProviderList ('isDelivering'  to get all results with delivery filter)
                else { mUserViewModel.setProviderList("isProvider");} // Trigger the listener to get ProviderList ('isProvider' is always true- to get all default results without delivery filter)
            }
        });
    }

    // Navigation drawer config
    protected void configureDrawerLayoutAndNavigationView(){
        // Configure drawer layout
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, drawerLayout, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawerLayout.addDrawerListener(toggle);
        toggle.syncState();

        // Configure NavigationView & set item selection listener
        View headerView = this.navigationView.getHeaderView(0);
        TextView userName = headerView.findViewById(R.id.navigation_header_name);
        TextView userEmail = headerView.findViewById(R.id.navigation_header_email);
        ImageView userImage = headerView.findViewById(R.id.navigation_header_image);
        // set user name and email

        userName.setText(this.getCurrentUser().getDisplayName());
        userEmail.setText(this.getCurrentUser().getEmail());
        // Set Image
        if (this.getCurrentUser() != null) {
            if (this.getCurrentUser().getPhotoUrl() != null) {
                Glide.with(this)
                        .load(this.getCurrentUser().getPhotoUrl())
                        .apply(RequestOptions.circleCropTransform())
                        .into(userImage);
            }
        }
        // Set listener
        navigationView.setNavigationItemSelectedListener(this);
    }

    //ViewPager configuration
    protected void configureViewPager() {
        //Set Adapter PageAdapter and glue it together
        pager.setAdapter(new PageAdapter(mContext, getSupportFragmentManager()));
        // Set the offscreenLimit - loads 2 fragments simultaneously offScreen, to improves fluency of visual load
        pager.setOffscreenPageLimit(2);
        // ViewPager scroll listener
        pager.addOnPageChangeListener(this);
    }

    // BottomNavigation Layout
    private void configureBottomNavigation(){
        // Configure BottomNavigation Listener
        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.action_mapView:
                        pager.setCurrentItem(0);
                        informationContainer.setVisibility(View.GONE);
                        deliverySwitchContainer.setVisibility(View.VISIBLE);
                        break;
                    case R.id.action_listView:
                        pager.setCurrentItem(1);
                        informationContainer.setVisibility(View.GONE);
                        deliverySwitchContainer.setVisibility(View.VISIBLE);
                        break;
                    case R.id.action_orders:
                        pager.setCurrentItem(2);
                        deliverySwitchContainer.setVisibility(View.GONE);
                        informationContainer.setVisibility(View.VISIBLE);
                        break;
                }
                return true;
            }
        });
    }

    // Toolbar for Navigation Drawer and searchAction icon
    protected void configureToolbar(){
        setSupportActionBar(toolbar);
    }

    //-------------------
    // Actions
    //-------------------

    // Drawer item selection
    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
        int order = menuItem.getOrder();
        Log.d(TAG, "Test onNavigationItemSelected: "+ order);
        switch (order){
            case 0:
                // Your orders
                test();
                break;
            case 1:
                // Edit Profile action
                editUserProfile();
                break;
            case 2:
                signOutUserFromFirebase(); // logout action
                break;
        }
        this.drawerLayout.closeDrawer(GravityCompat.START);
        return true;
    }

    private void editUserProfile() {
        EditUserBottomSheet.newInstance("userObject", new Gson().toJson(mUser)).show(getSupportFragmentManager(), "editUser");
    }

    private void test() {
    }

    // ------------------
    // ViewPager helper
    // ------------------
    @Override
    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
        Log.d(TAG, "onPageSelected: " + position);
        // setChecked the current BottomNavigation button while scrolling with ViewPager
        bottomNavigationView.getMenu().getItem(position).setChecked(true);
        // Set the current bottom- view (Delivery Switch / information)
        switch (position) {
            case 0:
            case 1:
                informationContainer.setVisibility(View.GONE);
                deliverySwitchContainer.setVisibility(View.VISIBLE);
                break;
            case 2:
                deliverySwitchContainer.setVisibility(View.GONE);
                informationContainer.setVisibility(View.VISIBLE);
                break;
        }
    }
    @Override
    public void onPageSelected(int position) { }
    @Override
    public void onPageScrollStateChanged(int state) { }
}
