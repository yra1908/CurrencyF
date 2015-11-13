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


public class Fragment3_CurrencyRateGraph extends Fragment {

    View rootview;

    public Fragment3_CurrencyRateGraph(){

    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        rootview=inflater.inflate(R.layout.fragment3_currency_rate_graph, container, false);
        return rootview;
    }
}
