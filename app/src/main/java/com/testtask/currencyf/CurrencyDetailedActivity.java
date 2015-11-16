package com.testtask.currencyf;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;

public class CurrencyDetailedActivity extends AppCompatActivity  {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_currency_detailed);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);


        if(savedInstanceState ==null){
            Fragment_CurrencyDetailedLog frag = new Fragment_CurrencyDetailedLog();

            Bundle b = getIntent().getBundleExtra(MainActivity.CURRENCY_BUNDLE);
            frag.setArguments(b);

            getFragmentManager().beginTransaction()
                    .add(R.id.detailedCurrency, frag)
                    .commit();
        }

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == android.R.id.home) {
           finish();
        }

        return super.onOptionsItemSelected(item);
    }

}
