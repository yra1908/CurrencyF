package com.testtask.currencyf;

import android.app.DatePickerDialog;
import android.app.Fragment;
import android.content.Context;
import android.graphics.Color;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.jjoe64.graphview.GraphView;
import com.jjoe64.graphview.helper.DateAsXAxisLabelFormatter;
import com.jjoe64.graphview.series.DataPoint;
import com.jjoe64.graphview.series.LineGraphSeries;
import com.testtask.currencyf.domain.Currency;
import com.testtask.currencyf.service.CurrencyJSONParser;
import com.testtask.currencyf.service.HttpManager;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.SortedMap;
import java.util.TreeMap;

/**
 * Fragment for building Graph for specified period
 */
public class Fragment3_CurrencyRateGraph extends Fragment
        implements View.OnClickListener{

    private static final String MINFIN_USD_API = "http://minfin.com.ua/data/currency/ib/usd.ib.stock.json";
    private static final String MINFIN_EUR_API = "http://minfin.com.ua/data/currency/ib/eur.ib.stock.json";
    private static final String MINFIN_RUB_API = "http://minfin.com.ua/data/currency/ib/rub.ib.stock.json";
    private static final String NETWORK_NOT_AVAILABLE = "Network isn't available";

    private int startYear;
    private int startMonth;
    private int startDay;
    private int endYear;
    private int endMonth;
    private int endDay;
    private Currency.Type currencyType;
    private TreeMap<Date, Currency> mapData;

    private GraphView graph;
    private LineGraphSeries<DataPoint> series;
    private TextView tvDisplayStartDate;
    private TextView tvDisplayEndDate;
    private ProgressBar pb;
    private List<MyTask> tasks;
    View rootview;

    public Fragment3_CurrencyRateGraph(){

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        rootview=inflater.inflate(R.layout.fragment3_currency_rate_graph, container, false);

        pb = (ProgressBar) rootview.findViewById(R.id.progressBar);
        pb.setVisibility(View.INVISIBLE);

        Button b1 = (Button) rootview.findViewById(R.id.button1); //start Day
        b1.setOnClickListener(this);
        Button b2 = (Button) rootview.findViewById(R.id.button2); //End Day
        b2.setOnClickListener(this);
        Button b3 = (Button) rootview.findViewById(R.id.button3); //Build Graph
        b3.setOnClickListener(this);
        Button b4 = (Button) rootview.findViewById(R.id.button4); //Clear Graph
        b4.setOnClickListener(this);

        tvDisplayStartDate = (TextView) rootview.findViewById(R.id.startDateRes);
        tvDisplayEndDate = (TextView) rootview.findViewById(R.id.endDateRes);
        setCurrentDateOnView();

        graph = (GraphView) rootview.findViewById(R.id.graph);

        tasks = new ArrayList<>();

        //Spinner (dropdown)
        Spinner dropdown = (Spinner) rootview.findViewById(R.id.spinner);
        Currency.Type[] items = new Currency.Type[]{Currency.Type.USD,
                Currency.Type.EUR, Currency.Type.RUB};
        ArrayAdapter<Currency.Type> adapter = new ArrayAdapter<>(getActivity(),
                android.R.layout.simple_spinner_dropdown_item, items);
        dropdown.setAdapter(adapter);

        dropdown.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view,
                                       int position, long id) {
                currencyType = (Currency.Type) parent.getItemAtPosition(position);
                getMinfinData();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                currencyType = Currency.Type.USD;
                getMinfinData();
            }
        });

        return rootview;
    }

    /**
     * Listener for buttons on page clicked
     * @param v id of selected item
     */
    @Override
    public void onClick(View v) {

        switch (v.getId()) {
            case R.id.button1:
                setStartDate();
                break;
            case R.id.button2:
                setEndDate();
                break;
            case R.id.button3:
                buildGraph();
                break;
            case R.id.button4:
                clearGraph();
                break;
        }

    }

    /**
     * Display Current Date
     */
    public void setCurrentDateOnView() {

        final Calendar c = Calendar.getInstance();
        endYear = c.get(Calendar.YEAR);
        endMonth = c.get(Calendar.MONTH)+1;
        endDay = c.get(Calendar.DAY_OF_MONTH);
        startDay = endDay;
        startMonth = endMonth;
        startYear = endYear - 1;

        tvDisplayEndDate.setText(new StringBuilder()
                .append(endMonth).append("-").append(endDay).append("-")
                .append(endYear).append(" "));

        tvDisplayStartDate.setText(new StringBuilder()
                .append(startMonth).append("-").append(startDay).append("-")
                .append(startYear).append(" "));

    }

    /**
     * Setting start Date
     */
    public void setStartDate() {
        int inputMonth=startMonth-1;
        DatePickerDialog d = new DatePickerDialog(getActivity(),
                datePickerListener, startYear, inputMonth, startDay);
        d.show();
    }

    /**
     * Setting end Date
     */
    public void setEndDate() {
        int inputMonth=endMonth-1;
        DatePickerDialog d = new DatePickerDialog(getActivity(),
                datePickerListener2, endYear, inputMonth, endDay);
        d.show();

    }

    /**
     * Dialog - setting start Date
     */
    private DatePickerDialog.OnDateSetListener datePickerListener =
            new DatePickerDialog.OnDateSetListener() {

                public void onDateSet(DatePicker view, int selectedYear,
                                      int selectedMonth, int selectedDay) {
                    startYear = selectedYear;
                    startMonth = selectedMonth+1;
                    startDay = selectedDay;

                    tvDisplayStartDate.setText(new StringBuilder().append(startMonth)
                            .append("-").append(startDay).append("-").append(startYear)
                            .append(" "));

                }
            };

    /**
     * Dialog - setting end Date
     */
    private DatePickerDialog.OnDateSetListener datePickerListener2 =
            new DatePickerDialog.OnDateSetListener() {

                public void onDateSet(DatePicker view, int selectedYear,
                                      int selectedMonth, int selectedDay) {
                    endYear = selectedYear;
                    endMonth = selectedMonth+1;
                    endDay = selectedDay;

                    tvDisplayEndDate.setText(new StringBuilder().append(endMonth)
                            .append("-").append(endDay).append("-").append(endYear)
                            .append(" "));

                }
            };

    /**
     * Clear graph
     */
    public void clearGraph() {
        graph.removeAllSeries();
    }

    /**
     * Get Data sorted TreeMap<Date, Currency> (Currency Exchange Rate with Date)
     * from MINFIN API
     */
    private void getMinfinData() {
        if (isOnline()) {
            if (currencyType.equals(Currency.Type.USD)) {
                requestData(MINFIN_USD_API);
            }
            if (currencyType.equals(Currency.Type.EUR)) {
                requestData(MINFIN_EUR_API);
            }
            if (currencyType.equals(Currency.Type.RUB)) {
                requestData(MINFIN_RUB_API);
            }
        } else {
            Toast.makeText(getActivity(), NETWORK_NOT_AVAILABLE, Toast.LENGTH_LONG).show();
        }
    }

    /**
     * Building Graph
     */
    public void buildGraph() {

        if (!isOnline()) {
            Toast.makeText(getActivity(), NETWORK_NOT_AVAILABLE, Toast.LENGTH_LONG).show();
            return;
        }

        if(mapData.isEmpty()){
            Toast.makeText(getActivity(), "Error. No Data for building graph.",
                    Toast.LENGTH_LONG).show();
        }

        Date startDate = null;
        Date endDate = null;
        try {
            DateFormat df = new SimpleDateFormat("yyyy-M-dd", Locale.getDefault());
            startDate = df.parse(startYear + "-" + startMonth + "-" + startDay);
            endDate = df.parse(endYear + "-" + endMonth + "-" + endDay);
        } catch (ParseException e) {
            e.printStackTrace();
        }

        if(startDate.after(endDate)){
            Toast.makeText(getActivity(), "Date input Error. Start Date can't be after End Date.",
                    Toast.LENGTH_LONG).show();
            return;
        }

        SortedMap<Date, Currency> mapDataToShow = mapData.subMap(startDate, endDate);

        int count = mapDataToShow.size();

        DataPoint[] values = new DataPoint[count];
        int i = 0;

        for (Map.Entry<Date, Currency> entry : mapDataToShow.entrySet()) {
            Date d = entry.getKey();
            double v = entry.getValue().getBuyCoef();
            DataPoint temp = new DataPoint(d, v);
            values[i] = temp;
            i++;
        }

        series = new LineGraphSeries<DataPoint>(values);

        // set manual x bounds to have nice steps
        graph.getViewport().setMinX(mapDataToShow.firstKey().getTime());
        graph.getViewport().setMaxX(mapDataToShow.lastKey().getTime());
        graph.getViewport().setXAxisBoundsManual(true);


        // set date label formatter
        graph.getGridLabelRenderer().setLabelFormatter(new DateAsXAxisLabelFormatter(getActivity()));
        graph.getGridLabelRenderer().setNumHorizontalLabels(3);

        if (currencyType.equals(Currency.Type.EUR)) {
            series.setColor(Color.RED);
        }
        if (currencyType.equals(Currency.Type.RUB)) {
            series.setColor(Color.BLACK);
        }
        graph.addSeries(series);


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
     * Start AsyncTask for getting TreeMap<Date, Currency> from MINFIN API
     * @param uri
     */
    private void requestData(String uri) {
        MyTask task = new MyTask();
        task.execute(uri);
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

            mapData = CurrencyJSONParser.parseMinfinFeed(result);

            tasks.remove(this);
            if (tasks.size() == 0) {
                pb.setVisibility(View.INVISIBLE);
            }

        }

    }

}
