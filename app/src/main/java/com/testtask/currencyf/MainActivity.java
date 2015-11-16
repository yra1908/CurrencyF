package com.testtask.currencyf;

import android.app.Fragment;
import android.app.FragmentManager;
import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.util.Log;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;

import com.testtask.currencyf.domain.Currency;

import java.util.List;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener,
        Fragment2_LogCurrencyRate.Callbacks,
        Fragment1_CurrentCurrencyRate.Callbacks2{

    public static final String LOG_DEBUG ="Debug";
    public static final String CURRENCY_BUNDLE ="CurrencyBundle";
    public static final int REQUEST_CODE =1001;
    public static String PACKAGE_NAME;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        PACKAGE_NAME = getApplicationContext().getPackageName();

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        Fragment1_CurrentCurrencyRate frag1=new Fragment1_CurrentCurrencyRate();
        getFragmentManager().beginTransaction()
                .add(R.id.myContainer, frag1)
                .commit();
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        Fragment selectedFrag = null;

        if (id == R.id.current_rate) {
            selectedFrag= new Fragment1_CurrentCurrencyRate();
        } else if (id == R.id.currency_log) {
            selectedFrag= new Fragment2_LogCurrencyRate();
        } else if (id == R.id.currency_graph) {
            selectedFrag= new Fragment3_CurrencyRateGraph();
        }

        FragmentManager fragmentManager = getFragmentManager();
        fragmentManager.beginTransaction()
                .replace(R.id.myContainer, selectedFrag)
                .commit();

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
    }

    public boolean isOnline() {
        Log.d(LOG_DEBUG, "check online");
        ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo netInfo = cm.getActiveNetworkInfo();
        if (netInfo != null && netInfo.isConnectedOrConnecting()) {
            return true;
        } else {
            return false;
        }
    }


    @Override
    public void onItemSelected(Currency currency) {
        Bundle b = currency.toBundle();
        Intent intent = new Intent(this, CurrencyDetailedActivity.class);
        intent.putExtra(CURRENCY_BUNDLE, b);
        startActivityForResult(intent, REQUEST_CODE);

    }

    //overriding method of callback interface for recieving data
    // from fragment and sending them to new frag
    //Using it when replacing fragments in one activity & sending data
    @Override
    public void onItemSelected2(Currency currency) {
        Bundle b = currency.toBundle();
        Fragment_CurrencyDetailed frag = new Fragment_CurrencyDetailed();
        frag.setArguments(b);

        getFragmentManager().beginTransaction()
                .replace(R.id.myContainer, frag)
                .commit();
    }


}
