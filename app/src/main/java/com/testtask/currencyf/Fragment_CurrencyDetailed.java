package com.testtask.currencyf;

import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
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
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        rootview=inflater.inflate(R.layout.fragment_detailed_currency_rate, container, false);

        if(currency != null){

            if(currency.getName().equalsIgnoreCase("RUR")){
                currency.setName("RUB");
            }
            TextView tv = (TextView) rootview.findViewById(R.id.textView);
            tv.append(" - " + currency.getName());

            TextView tv2 = (TextView) rootview.findViewById(R.id.textView2);
            tv2.append(" - " + String.valueOf(currency.getSaleCoef()));

            TextView tv3 = (TextView) rootview.findViewById(R.id.textView3);
            tv3.append(" - " + String.valueOf(currency.getBuyCoef()));

            TextView tv4 = (TextView) rootview.findViewById(R.id.textView4);
            tv4.append(" - " + String.valueOf(currency.getSaleCoefNB()));

            TextView tv5 = (TextView) rootview.findViewById(R.id.textView5);
            tv5.append(" - " + String.valueOf(currency.getBuyCoefNB()));

            ImageView image = (ImageView) rootview.findViewById(R.id.imageView5);
            String imageName = currency.getName().toLowerCase();


            int res = getResources().getIdentifier(imageName, "drawable", MainActivity.PACKAGE_NAME);

            image.setImageResource(res);

        }
        return rootview;
    }
}
