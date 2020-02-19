package com.elbaz.eliran.washmylaundry.controllers.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.elbaz.eliran.washmylaundry.R;
import com.elbaz.eliran.washmylaundry.base.BaseFragment;
import com.elbaz.eliran.washmylaundry.models.Orders;
import com.elbaz.eliran.washmylaundry.utils.ItemClickSupport;
import com.elbaz.eliran.washmylaundry.viewmodel.UserViewModel;
import com.elbaz.eliran.washmylaundry.views.MyOrdersAdapter;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by Eliran Elbaz on 11-Feb-20.
 */
public class OrdersFragment extends BaseFragment {
    @BindView(R.id.my_orders_recyclerView) RecyclerView ordersRecyclerView;
    @BindView(R.id.my_orders_recycler_view_empty) TextView emptyListText;
    private MyOrdersAdapter mMyOrdersAdapter;
    private UserViewModel mUserViewModel;
    private List<Orders> mOrdersList= new ArrayList<>();

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(getFragmentLayout(), container, false);
        ButterKnife.bind(this, view);
        this.configureViewModel();
        this.configureRecyclerView();
        this.configureOnClickRecyclerView();
        setHasOptionsMenu(true);

        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        // get Providers list
        mUserViewModel.getOrdersList().observe(getViewLifecycleOwner(), new Observer<List<Orders>>() {
            @Override
            public void onChanged(List<Orders> orders) {
                mOrdersList.clear();
                mOrdersList.addAll(orders);
                updateUI(orders);
            }
        });
    }



    // -----------------
    // CONFIGURATIONS
    // -----------------

    private void configureViewModel() {
        mUserViewModel = new ViewModelProvider(this).get(UserViewModel.class);
    }

    @Override
    protected int getFragmentLayout() {
        return R.layout.fragment_orders;
    }

    private void configureRecyclerView() {
        //Configure Adapter & RecyclerView
        ordersRecyclerView.setHasFixedSize(true);
        mOrdersList = new ArrayList<>();
        mMyOrdersAdapter = new MyOrdersAdapter(this.mOrdersList, getActivity().getApplicationContext());
        ordersRecyclerView.setAdapter(this.mMyOrdersAdapter);
        ordersRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));

    }

    //  Configure item click on RecyclerView
    private void configureOnClickRecyclerView(){
        ItemClickSupport.addTo(ordersRecyclerView, R.layout.fragment_orders)
                .setOnItemClickListener(new ItemClickSupport.OnItemClickListener() {
                    @Override
                    public void onItemClicked(RecyclerView recyclerView, int position, View v) {
                        // RecyclerView onClick action
                    }
                });
    }

    //-----------------
    //  UI
    //-----------------
    // Update UI with orders list
    private void updateUI(List<Orders> orders) {
        // Notify changes
        mMyOrdersAdapter.notifyDataSetChanged();
    }
}
