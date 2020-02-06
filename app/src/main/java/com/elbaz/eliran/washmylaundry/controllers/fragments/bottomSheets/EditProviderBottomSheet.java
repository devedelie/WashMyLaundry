package com.elbaz.eliran.washmylaundry.controllers.fragments.bottomSheets;

import android.os.Bundle;

import com.elbaz.eliran.washmylaundry.R;
import com.elbaz.eliran.washmylaundry.base.BaseBottomSheet;

import butterknife.OnClick;

/**
 * Created by Eliran Elbaz on 06-Feb-20.
 */
public class EditProviderBottomSheet extends BaseBottomSheet {

    public static EditProviderBottomSheet newInstance() {
        EditProviderBottomSheet editProviderBottomSheet;
        editProviderBottomSheet = new EditProviderBottomSheet();
        Bundle bundle = new Bundle();
//        bundle.putString(MARKER_ID, markerID);
//        bundle.putInt(MARKER_INDEX, index);
        editProviderBottomSheet.setArguments(bundle);
        return editProviderBottomSheet ;
    }

    // --------------------
    // CONFIGURATIONS
    // --------------------
    @Override
    protected int getFragmentLayout() { return R.layout.bottom_sheet_edit_provider; }

    @Override
    protected int setTitle() { return R.string.edit_bs_title; }

    // --------------------
    // ACTIONS
    // --------------------

    @OnClick(R.id.bottom_sheet_back_button)
    public void onBackButtonClick(){
        dismiss();
    }

}
