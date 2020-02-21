package com.elbaz.eliran.washmylaundry.controllers.activities;

import android.graphics.PorterDuff;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.elbaz.eliran.washmylaundry.R;
import com.elbaz.eliran.washmylaundry.base.BaseActivity;
import com.elbaz.eliran.washmylaundry.controllers.fragments.bottomSheets.OrderStateBottomSheet;
import com.elbaz.eliran.washmylaundry.models.Orders;
import com.elbaz.eliran.washmylaundry.utils.ItemClickSupport;
import com.elbaz.eliran.washmylaundry.viewmodel.ProviderViewModel;
import com.elbaz.eliran.washmylaundry.views.MyOrdersAdapter;
import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import butterknife.BindView;
import butterknife.OnClick;

/**
 * Created by Eliran Elbaz on 21-Feb-20.
 */
public class ProviderOrdersActivity extends BaseActivity {
    @BindView(R.id.activity_provider_orders_waiting_orders_btn) ImageButton waitingOrdersBtn;
    @BindView(R.id.activity_provider_orders_in_progress_orders_btn) ImageButton progressOrdersBtn;
    @BindView(R.id.activity_provider_orders_finished_orders_btn) ImageButton finishedOrdersBtn;
    @BindView(R.id.activity_provider_orders_recycler_view) RecyclerView ordersRecyclerView;
    @BindView(R.id.activity_provider_orders__text_view_recycler_view_empty) TextView emptyRecyclerViewText;
    @BindView(R.id.provider_order_activity_bar_title) TextView titleText;

    private List<Orders> allProviderOrders;
    private List<Orders> filteredOrdersList;
    private ProviderViewModel mProviderViewModel;
    private MyOrdersAdapter mMyOrdersAdapter;
    private String currentButton= "waiting"; // By default
    // STATIC DATA FOR BUTTONS (3)
    private static final String LIST_NAME_WAITING = "waiting";
    private static final String LIST_NAME_PROGRESS = "progress";
    private static final String LIST_NAME_FINISHED = "finished";

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setTitle();
        configureViewModel();
        configureRecyclerView();
        configureOnClickRecyclerView();
    }

    @Override
    protected void onResume() {
        super.onResume();
        waitingOrdersBtn.setColorFilter(getResources().getColor(R.color.colorTextIcons), PorterDuff.Mode.SRC_ATOP); // Set 'waitingBtn' white by default
        setDataObserver();
    }

    private void setDataObserver() {
        mProviderViewModel.getOrdersList().observe(this, new Observer<List<Orders>>() {
            @Override
            public void onChanged(List<Orders> orders) {
                allProviderOrders = new ArrayList<>();
                allProviderOrders.clear();
                allProviderOrders.addAll(orders);
                updateUI();
            }
        });
    }



    //-------------------
    // CONFIGURATIONS
    //-------------------

    @Override
    public int getActivityLayout() {
        return R.layout.activity_provider_orders;
    }

    private void setTitle(){titleText.setText(R.string.your_orders_title); }

    private void configureViewModel() {
        mProviderViewModel = new ViewModelProvider(this).get(ProviderViewModel.class);
    }

    private void configureRecyclerView() {
        //Configure Adapter & RecyclerView
        ordersRecyclerView.setHasFixedSize(true);
        filteredOrdersList = new ArrayList<>();
        mMyOrdersAdapter = new MyOrdersAdapter(this.filteredOrdersList, this);
        ordersRecyclerView.setAdapter(this.mMyOrdersAdapter);
        ordersRecyclerView.setLayoutManager(new LinearLayoutManager(this));
    }

    //  Configure item click on RecyclerView
    private void configureOnClickRecyclerView(){
        ItemClickSupport.addTo(ordersRecyclerView, R.layout.fragment_orders)
                .setOnItemClickListener(new ItemClickSupport.OnItemClickListener() {
                    @Override
                    public void onItemClicked(RecyclerView recyclerView, int position, View v) {
                        // RecyclerView onClick action
                        OrderStateBottomSheet.newInstance("orderObject", new Gson().toJson(filteredOrdersList.get(position))).show(getSupportFragmentManager(), "OrderInvoice");
                    }
                });
    }

    //-------------------
    // ACTIONS
    //-------------------

    @OnClick(R.id.provider_order_activity_back_button)
    public void onBackBtnClick(){
        finish();
    }

    @OnClick(R.id.activity_provider_orders_waiting_orders_btn)
    public void onWaitingBtnClick(){
        currentButton= LIST_NAME_WAITING;
        updateUI();
    }

    @OnClick(R.id.activity_provider_orders_in_progress_orders_btn)
    public void onProgressBtnClick(){
        currentButton= LIST_NAME_PROGRESS;
        updateUI();
    }

    @OnClick(R.id.activity_provider_orders_finished_orders_btn)
    public void onFinishedBtnClick(){
        currentButton= LIST_NAME_FINISHED;
        updateUI();
    }

    //-------------------
    // UI
    //-------------------

    private void updateUI() {
       // Switch Button state
        switch(currentButton) {
            case "waiting":
                // Add only waiting orders
                filteredOrdersList.clear();
                for(int i = 0 ; i < allProviderOrders.size() ; i++){
                    if(allProviderOrders.get(i).getOrderStatus() == 1){
                        filteredOrdersList.add(allProviderOrders.get(i));
                    }
                }
                // Set buttons color
                waitingOrdersBtn.setColorFilter(getResources().getColor(R.color.colorTextIcons), PorterDuff.Mode.SRC_ATOP);
                progressOrdersBtn.setColorFilter(getResources().getColor(R.color.colorAccent), PorterDuff.Mode.SRC_ATOP);
                finishedOrdersBtn.setColorFilter(getResources().getColor(R.color.colorAccent), PorterDuff.Mode.SRC_ATOP);
                break;
            case "progress":
                // Add only orders in progress
                filteredOrdersList.clear();
                for(int i = 0 ; i < allProviderOrders.size() ; i++){
                    if(allProviderOrders.get(i).getOrderStatus() > 1  && allProviderOrders.get(i).getOrderStatus() < 4){
                        filteredOrdersList.add(allProviderOrders.get(i));
                    }
                }
                // Set buttons color
                waitingOrdersBtn.setColorFilter(getResources().getColor(R.color.colorAccent), PorterDuff.Mode.SRC_ATOP);
                progressOrdersBtn.setColorFilter(getResources().getColor(R.color.colorTextIcons), PorterDuff.Mode.SRC_ATOP);
                finishedOrdersBtn.setColorFilter(getResources().getColor(R.color.colorAccent), PorterDuff.Mode.SRC_ATOP);
                break;
            case "finished":
                // Add only finished orders
                filteredOrdersList.clear();
                for(int i = 0 ; i < allProviderOrders.size() ; i++){
                    if(allProviderOrders.get(i).getOrderStatus() == 4){
                        filteredOrdersList.add(allProviderOrders.get(i));
                    }
                }
                // Set buttons color
                waitingOrdersBtn.setColorFilter(getResources().getColor(R.color.colorAccent), PorterDuff.Mode.SRC_ATOP);
                progressOrdersBtn.setColorFilter(getResources().getColor(R.color.colorAccent), PorterDuff.Mode.SRC_ATOP);
                finishedOrdersBtn.setColorFilter(getResources().getColor(R.color.colorTextIcons), PorterDuff.Mode.SRC_ATOP);
                break;
        }

        // Check if list is empty and set text
        if(filteredOrdersList.size() == 0){ emptyRecyclerViewText.setVisibility(View.VISIBLE);}
        else {emptyRecyclerViewText.setVisibility(View.GONE);}

        // Sort orders (newest first)
        if(filteredOrdersList != null && filteredOrdersList.size() > 0){
            Collections.sort(filteredOrdersList, new Comparator<Orders>() {
                @Override
                public int compare(Orders orders, Orders t1) {
                    int sort;
                    sort = orders.getReservationDate().compareTo(t1.getReservationDate());
                    return sort;
                }
            });
        }
        // Notify changes
        mMyOrdersAdapter.notifyDataSetChanged();
    }


}
