package com.elbaz.eliran.washmylaundry.controllers.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.Nullable;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.elbaz.eliran.washmylaundry.R;
import com.elbaz.eliran.washmylaundry.base.BaseFragment;
import com.elbaz.eliran.washmylaundry.controllers.fragments.bottomSheets.UserPreOrderBottomSheet;
import com.elbaz.eliran.washmylaundry.models.Provider;
import com.elbaz.eliran.washmylaundry.utils.ItemClickSupport;
import com.elbaz.eliran.washmylaundry.viewmodel.UserViewModel;
import com.elbaz.eliran.washmylaundry.views.ListViewAdapter;
import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by Eliran Elbaz on 11-Feb-20.
 */
public class ListViewFragment extends BaseFragment {
    @BindView(R.id.listView_recyclerView) RecyclerView listViewRecyclerView;
    // For Data
    private ListViewAdapter mListViewAdapter;
    private UserViewModel mUserViewModel;
    private List<Provider> mProviderList= new ArrayList<>();

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(getFragmentLayout(), container, false);
        ButterKnife.bind(this, view); //Configure Butterknife
        this.configureRecyclerView();
        this.configureOnClickRecyclerView();
        setHasOptionsMenu(true); // Prepare the correct optionsMenu items
        this.configureViewModel();

        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        // get Providers list
        mUserViewModel.getProvidersList().observe(getViewLifecycleOwner(), new Observer<List<Provider>>() {
            @Override
            public void onChanged(List<Provider> providers) {
                mProviderList.clear();
                mProviderList.addAll(providers);
                updateUI(providers);
            }
        });
    }


    //-----------------
    // CONFIGURATIONS
    //-----------------

    @Override
    protected int getFragmentLayout() {
        return R.layout.fragment_list_view;
    }

    private void configureViewModel() {
        mUserViewModel = new ViewModelProvider(this).get(UserViewModel.class);
    }

    private void configureRecyclerView(){
        // Set the recyclerView to fixed size in order to increase performances
        listViewRecyclerView.setHasFixedSize(true);
        mProviderList = new ArrayList<>();
        mListViewAdapter = new ListViewAdapter(this.mProviderList, getActivity().getApplicationContext());
        listViewRecyclerView.setAdapter(this.mListViewAdapter);
        listViewRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
    }

    private void configureOnClickRecyclerView(){
        ItemClickSupport.addTo(listViewRecyclerView, R.layout.fragment_list_view)
                .setOnItemClickListener(new ItemClickSupport.OnItemClickListener() {
                    @Override
                    public void onItemClicked(RecyclerView recyclerView, int position, View v) {
                       // Action for recyclerView item click
                        UserPreOrderBottomSheet.newInstance("providerObject", new Gson().toJson(mProviderList.get(position))).show(getActivity().getSupportFragmentManager(), "preOrder");
                    }
                });
    }

    //-----------------
    //  UI
    //-----------------
    // Update UI with providers list
    private void updateUI(List<Provider> providers){
        // Notify changes
        mListViewAdapter.notifyDataSetChanged();
    }
}
