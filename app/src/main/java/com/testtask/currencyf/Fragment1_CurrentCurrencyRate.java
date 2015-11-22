package com.testtask.currencyf;

import android.app.Activity;
import android.app.Fragment;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
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

    private ProgressBar pb;
    private List<MyTask> tasks;
    private List<Currency> list;
    private Callbacks2 activity;


    private static final String PB_API = "https://api.privatbank.ua/p24api/pubinfo?json&exchange&coursid=5";
    private static final String NETWORK_NOT_AVAILABLE = "Network isn't available";

    View rootview;

    public Fragment1_CurrentCurrencyRate(){
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        rootview=inflater.inflate(R.layout.fragment1_current_currency_rate, container, false);

        pb = (ProgressBar) rootview.findViewById(R.id.progressBar);
        pb.setVisibility(View.INVISIBLE);
        tasks = new ArrayList<>();


        Button b = (Button) rootview.findViewById(R.id.button);
        b.setOnClickListener(this);

        //setting onclick listener to layout
        RelativeLayout usdRL=(RelativeLayout)rootview.findViewById(R.id.usd_linear_layout);
        RelativeLayout eurRL=(RelativeLayout)rootview.findViewById(R.id.eur_linear_layout);
        RelativeLayout rubRL=(RelativeLayout)rootview.findViewById(R.id.rub_linear_layout);
        usdRL.setOnClickListener(this);
        eurRL.setOnClickListener(this);
        rubRL.setOnClickListener(this);

        getCurrencyRate();


        return rootview;
    }

    private void requestData(String uri) {
        Log.d(MainActivity.LOG_DEBUG, "button clicked3");
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
            requestData(PB_API);
        } else {
            Toast.makeText(getActivity(), NETWORK_NOT_AVAILABLE, Toast.LENGTH_LONG).show();
        }
    }

    @Override
    public void onClick(View v) {

        switch (v.getId()) {
            case R.id.button:
                getCurrencyRate();
                break;
            case R.id.usd_linear_layout:
                if(isOnline()){
                    Currency cur = list.get(2);
                    activity.onItemSelected2(cur);
                } else {
                    Toast.makeText(getActivity(), NETWORK_NOT_AVAILABLE, Toast.LENGTH_LONG).show();
                }
                break;
            case R.id.eur_linear_layout:
                if(isOnline()){
                    Currency cur2 = list.get(1);
                    activity.onItemSelected2(cur2);
                } else {
                    Toast.makeText(getActivity(), NETWORK_NOT_AVAILABLE, Toast.LENGTH_LONG).show();
                }
                break;
            case R.id.rub_linear_layout:
                if(isOnline()){
                    Currency cur3 = list.get(0);
                    activity.onItemSelected2(cur3);
                } else {
                    Toast.makeText(getActivity(), NETWORK_NOT_AVAILABLE, Toast.LENGTH_LONG).show();
                }
                break;
        }

    }


    private class MyTask extends AsyncTask<String, String, String> {

        @Override
        protected void onPreExecute() {
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

    //interface for sending data (currency Bundle) to main activity
    public interface Callbacks2{
        public void onItemSelected2(Currency currency);
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        this.activity= (Callbacks2) activity;
    }
}
