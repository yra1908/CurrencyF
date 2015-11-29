package com.testtask.currencyf;


import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.ListFragment;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
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
 * Log Fragment. Show Currency Exchange Rate for specified day
 */
public class Fragment2_LogCurrencyRate extends ListFragment
        implements View.OnClickListener{

    View rootview;
    private TextView tvDisplayDate;

    private int year;
    private int month;
    private int day;
    private List<Currency> list;
    private Callbacks activity;

    private ProgressBar pb;
    private List<MyTask> tasks;

    private static final String LOG_PB_API = "https://api.privatbank.ua/p24api/exchange_rates?json&date=";
    private static final String NETWORK_NOT_AVAILABLE = "Network isn't available";

    /**
     * Constructor without params
     * Need it for ArrayAdapter(Items showed as List)
     */
    public Fragment2_LogCurrencyRate(){

    }

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

    /**
     * Display current date
     */
    public void setCurrentDateOnView() {

        tvDisplayDate = (TextView) rootview.findViewById(R.id.startDateRes);


        final Calendar c = Calendar.getInstance();
        year = c.get(Calendar.YEAR);
        month = c.get(Calendar.MONTH);
        day = c.get(Calendar.DAY_OF_MONTH);;

        // set current date into textview
        tvDisplayDate.setText(new StringBuilder()
                .append(month + 1).append("-").append(day).append("-")
                .append(year).append(" "));

    }

    /**
     * Setting date for query
     */
    public void setDate() {
        DatePickerDialog d = new DatePickerDialog(getActivity(),
                datePickerListener, year, month, day);
        d.show();
    }

    /**
     * Dialog - setting Date
     */
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

    /**
     * Get Currency Exchange Rate for specified Date
     */
    public void getCurrencyLogForSetDate() {

        if (isOnline()) {
            int inputMonth=month+1;
            String API = LOG_PB_API + day + "."+ inputMonth + "." + year;
            requestData(API);
        } else {
            Toast.makeText(getActivity(), NETWORK_NOT_AVAILABLE, Toast.LENGTH_LONG).show();
        }
    }

    /**
     * Start AsyncTask for getting List<Currency> from PB API
     * @param uri
     */
    private void requestData(String uri) {
        MyTask task = new MyTask();
        task.execute(uri);
    }

    /**
     * Checking if Network available
     * @return boolean
     */
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

    /**
     * Updating View with Exchange Rate Data
     */
    private void updateDisplay() {

        CurrencyAdapter adapter = new CurrencyAdapter(getActivity(), R.layout.fragment2_log_currency_rate, list);

        ListView listView = (ListView) rootview.findViewById(android.R.id.list);
        listView.setAdapter(adapter);


    }

    /**
     * Listener for buttons on page clicked
     * @param v id of selected item
     */
    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btnChangeDate:
                setDate();
                break;
            case R.id.button:
                getCurrencyLogForSetDate();
                break;
        }

    }

    /**
     * Creating new Thread for making request
     * API for request - PrivatBank LOG exchange Rate
     * Receiving List<Currency>
     */
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

            list = CurrencyJSONParser.parseLogFeed(result);

            updateDisplay();

            tasks.remove(this);
            if (tasks.size() == 0) {
                pb.setVisibility(View.INVISIBLE);
            }
        }

    }

    /**
     * Interface for sending data (currency Bundle) to main activity
     */
    public interface Callbacks{
        public void onItemSelected(Currency currency);
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        this.activity= (Callbacks) activity;
    }

    /**
     * OnClick listener for List items
     * @param l
     * @param v
     * @param position
     * @param id
     */
    @Override
    public void onListItemClick(ListView l, View v, int position, long id) {
        Currency currency = list.get(position);
        activity.onItemSelected(currency);
    }
}
