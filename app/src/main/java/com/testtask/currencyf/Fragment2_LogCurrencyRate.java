package com.testtask.currencyf;

import android.app.Fragment;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

/**
 * Created by 41X on 13.11.2015.
 */


public class Fragment2_LogCurrencyRate extends Fragment {

    View rootview;

    public Fragment2_LogCurrencyRate(){

    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        rootview=inflater.inflate(R.layout.fragment2_log_currency_rate, container, false);
        return rootview;
    }
}
