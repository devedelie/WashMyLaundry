package com.elbaz.eliran.washmylaundry.controllers.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.elbaz.eliran.washmylaundry.R;
import com.elbaz.eliran.washmylaundry.base.BaseFragment;

/**
 * Created by Eliran Elbaz on 11-Feb-20.
 */
public class ListViewFragment extends BaseFragment {

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(getFragmentLayout(), container, false);


        return view;
    }
    @Override
    protected int getFragmentLayout() {
        return R.layout.fragment_list_view;
    }
}
