package com.testtask.currencyf;

import android.app.Fragment;
import android.content.Context;
import android.content.res.Configuration;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.testtask.currencyf.domain.Currency;
import com.testtask.currencyf.service.CurrencyJSONParser;
import com.testtask.currencyf.service.HttpManager;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by 41X on 13.11.2015.
 */


public class Fragment1_CurrentCurrencyRate extends Fragment
        implements View.OnClickListener {

    public static final String LOG_DEBUG ="Debug" ;
    private ProgressBar pb;
    private List<MyTask> tasks;
    private List<Currency> list;


    private static final String PB_API = "https://api.privatbank.ua/p24api/pubinfo?json&exchange&coursid=5";

    View rootview;

    public Fragment1_CurrentCurrencyRate(){
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        rootview=inflater.inflate(R.layout.fragment1_current_currency_rate, container, false);

        pb = (ProgressBar) rootview.findViewById(R.id.progressBar);
        pb.setVisibility(View.INVISIBLE);
        tasks = new ArrayList<>();

        Button b = (Button) rootview.findViewById(R.id.button);
        b.setOnClickListener(this);

        getCurrencyRate();

        return rootview;
    }

    private void requestData(String uri) {
        Log.d(LOG_DEBUG, "button clicked3");
        MyTask task = new MyTask();
        task.execute(uri);
    }

    protected void updateDisplay() {

        TextView resSaleUSD = (TextView) getView().findViewById(R.id.saleUSD);
        TextView resBuyUSD = (TextView) getView().findViewById(R.id.buyUSD);
        TextView resSaleEUR = (TextView) getView().findViewById(R.id.saleEUR);
        TextView resBuyEUR = (TextView) getView().findViewById(R.id.buyEUR);
        TextView resSaleRUR = (TextView) getView().findViewById(R.id.saleRUR);
        TextView resBuyRUR = (TextView) getView().findViewById(R.id.buyRUR);

        if (list != null){
            for (Currency cur:list) {
                if (cur.getName().equals("USD")){
                    resSaleUSD.append((String.valueOf(cur.getSaleCoef())));
                    resBuyUSD.append((String.valueOf(cur.getBuyCoef())));
                }
                if (cur.getName().equals("EUR")){
                    resSaleEUR.append((String.valueOf(cur.getSaleCoef())));
                    resBuyEUR.append((String.valueOf(cur.getBuyCoef())));
                }
                if (cur.getName().equals("RUR")){
                    resSaleRUR.append((String.valueOf(cur.getSaleCoef())));
                    resBuyRUR.append((String.valueOf(cur.getBuyCoef())));
                }
            }
        }
    }


    public boolean isOnline() {
        Log.d(LOG_DEBUG, "check online");
        ConnectivityManager cm = (ConnectivityManager) getActivity()
                .getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo netInfo = cm.getActiveNetworkInfo();
        if (netInfo != null && netInfo.isConnectedOrConnecting()) {
            return true;
        } else {
            return false;
        }
    }

    public void getCurrencyRate() {
        if (isOnline()) {
            Log.d(LOG_DEBUG, "button clicked2");
            requestData(PB_API);
        } else {
            Toast.makeText(getActivity(), "Network isn't available", Toast.LENGTH_LONG).show();
        }
    }

    @Override
    public void onClick(View v) {

        switch (v.getId()) {
            case R.id.button:
                getCurrencyRate();
                break;
        }

    }


    private class MyTask extends AsyncTask<String, String, String> {

        @Override
        protected void onPreExecute() {
            Log.d(LOG_DEBUG, "button clicked4");
            if (tasks.size()==0){
                pb.setVisibility(View.VISIBLE);
            }
            tasks.add(this);
        }

        @Override
        protected String doInBackground(String... params) {

            String content = HttpManager.getData(params[0]);
            return content;
        }

        @Override
        protected void onPostExecute(String result) {

            list = CurrencyJSONParser.parseFeed(result);

            updateDisplay();

            tasks.remove(this);
            if (tasks.size()==0){
                pb.setVisibility(View.INVISIBLE);
            }
        }
    }
}
