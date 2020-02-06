package com.elbaz.eliran.washmylaundry.controllers.activities;

import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.elbaz.eliran.washmylaundry.R;
import com.elbaz.eliran.washmylaundry.base.BaseActivity;
import com.google.android.material.navigation.NavigationView;

import butterknife.BindView;

import static android.content.ContentValues.TAG;

public class MainUserActivity extends BaseActivity implements NavigationView.OnNavigationItemSelectedListener {
    @BindView(R.id.toolbar) Toolbar toolbar;
    @BindView(R.id.main_user_activity_drawerLayout) DrawerLayout drawerLayout;
    @BindView(R.id.drawer_main_user_activity) NavigationView navigationView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        configureDrawerLayoutAndNavigationView();
    }

    @Override
    public int getFragmentLayout() {
        return R.layout.activity_main_user;
    }


    //-------------------
    // CONFIGURATIONS
    //-------------------

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
                break;
            case 1:
                // settings action
                break;
            case 2:
                signOutUserFromFirebase(); // logout action
                break;
        }
        this.drawerLayout.closeDrawer(GravityCompat.START);
        return true;
    }

}
