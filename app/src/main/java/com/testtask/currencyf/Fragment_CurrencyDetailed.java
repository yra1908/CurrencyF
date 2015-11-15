package com.testtask.currencyf;

import android.app.Fragment;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.testtask.currencyf.domain.Currency;

/**
 * Created by 41X on 14.11.2015.
 */
public class Fragment_CurrencyDetailed extends Fragment {

    Currency currency;

    View rootview;

    public Fragment_CurrencyDetailed(){}

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Bundle b = getArguments();
        if(b != null && b.containsKey(Currency.CURRENCY_NAME)){
            currency = new Currency(b);
            Log.d(MainActivity.LOG_DEBUG, "recieved bundle data");
            Log.d(MainActivity.LOG_DEBUG, currency.getName());
        }
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        rootview=inflater.inflate(R.layout.fragment_detailed_currency_rate, container, false);

        if(currency != null){
            TextView tv = (TextView) rootview.findViewById(R.id.textView);
            tv.setText(currency.getName());

        }
        return rootview;
    }
}
