package com.testtask.currencyf;


import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.ListFragment;
import android.content.Context;
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
import android.widget.DatePicker;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.testtask.currencyf.domain.Currency;
import com.testtask.currencyf.service.CurrencyAdapter;
import com.testtask.currencyf.service.CurrencyJSONParser;
import com.testtask.currencyf.service.HttpManager;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

/**
 * Created by 41X on 13.11.2015.
 */


public class Fragment2_LogCurrencyRate extends ListFragment
        implements View.OnClickListener{

    View rootview;
    private TextView tvDisplayDate;

    private int year;
    private int month;
    private int day;
    private List<Currency> list;

    private ProgressBar pb;
    private List<MyTask> tasks;

    private static final int DATE_DIALOG_ID = 999;
    private static final String LOG_PB_API = "https://api.privatbank.ua/p24api/exchange_rates?json&date=";

    public Fragment2_LogCurrencyRate(){

    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        rootview=inflater.inflate(R.layout.fragment2_log_currency_rate, container, false);

        setCurrentDateOnView();

        pb = (ProgressBar) rootview.findViewById(R.id.progressBar);
        pb.setVisibility(View.INVISIBLE);

        Button b = (Button) rootview.findViewById(R.id.btnChangeDate);
        b.setOnClickListener(this);
        Button b2 = (Button) rootview.findViewById(R.id.button);
        b2.setOnClickListener(this);

        tasks = new ArrayList<>();

        return rootview;
    }

    // display current date
    public void setCurrentDateOnView() {

        tvDisplayDate = (TextView) rootview.findViewById(R.id.startDateRes);


        final Calendar c = Calendar.getInstance();
        year = c.get(Calendar.YEAR);
        month = c.get(Calendar.MONTH);
        day = 1;

        // set current date into textview
        tvDisplayDate.setText(new StringBuilder()
                .append(month + 1).append("-").append(day).append("-")
                .append(year).append(" "));

    }

    //setting date for query
    @SuppressWarnings("deprecation")
    public void setDate() {
        getActivity().showDialog(DATE_DIALOG_ID);
    }

    @SuppressWarnings("deprecation")
    protected Dialog onCreateDialog(int id) {
        switch (id) {
            case DATE_DIALOG_ID:
                /*//DatePicker theme changed
                return new DatePickerDialog(this, R.style.datePickerTheme, datePickerListener,*/
                return new DatePickerDialog(getActivity(), datePickerListener,
                        year, month, day);
        }
        return null;
    }

    private DatePickerDialog.OnDateSetListener datePickerListener =
            new DatePickerDialog.OnDateSetListener() {

                public void onDateSet(DatePicker view, int selectedYear,
                                      int selectedMonth, int selectedDay) {
                    year = selectedYear;
                    month = selectedMonth;
                    day = selectedDay;

                    tvDisplayDate.setText(new StringBuilder().append(month + 1)
                            .append("-").append(day).append("-").append(year)
                            .append(" "));
                }
            };

    public void getCurrencyLogForSetDate() {

        if (isOnline()) {
            int inputMonth=month+1;
            String API = LOG_PB_API + day + "."+ inputMonth + "." + year;
            Log.d("string API - ", API);
            requestData(API);
        } else {
            Toast.makeText(getActivity(), "Network isn't available", Toast.LENGTH_LONG).show();
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

    private void requestData(String uri) {
        MyTask task = new MyTask();
        task.execute(uri);
    }

    private void updateDisplay() {

        CurrencyAdapter adapter = new CurrencyAdapter(getActivity(), R.layout.fragment2_log_currency_rate, list);

        ListView listView = (ListView) rootview.findViewById(android.R.id.list);
        listView.setAdapter(adapter);


    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btnChangeDate:
                Log.d(MainActivity.LOG_DEBUG, "change date button clicked");
                setDate();
                break;
            case R.id.button:
                Log.d(MainActivity.LOG_DEBUG, "get log data button clicked");
                getCurrencyLogForSetDate();
                break;
        }

    }

    private class MyTask extends AsyncTask<String, String, String> {

        @Override
        protected void onPreExecute() {
            if (tasks.size() == 0) {
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

            Log.d("LogActivity", "before parse list");
            list = CurrencyJSONParser.parseLogFeed(result);
            Log.d("LogActivity", "after parse list");

            updateDisplay();

            tasks.remove(this);
            if (tasks.size() == 0) {
                pb.setVisibility(View.INVISIBLE);
            }
        }

    }


}
