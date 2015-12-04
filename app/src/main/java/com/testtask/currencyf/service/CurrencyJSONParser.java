package com.testtask.currencyf.service;

import android.util.Log;

import com.testtask.currencyf.MainActivity;
import com.testtask.currencyf.domain.Currency;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.TreeMap;

/**
 * Utility class for parsing results of REST requests
 */
public class CurrencyJSONParser {

    /**
     * Parsing JSON with current Currency Exchange Rate
     * @param content - JSON with current Currency Exchange Rate
     * @return List<Currency> object
     */
    public static List<Currency> parseFeed(String content){

        try {
            JSONArray ar = new JSONArray(content);
            List<Currency> list = new ArrayList<>();

            for (int i=0; i<ar.length(); i++){

                JSONObject obj = ar.getJSONObject(i);
                Currency currency = new Currency();

                currency.setName(Currency.Type.valueOf(obj.getString("ccy").toUpperCase()));
                currency.setSaleCoef(obj.getDouble("sale"));
                currency.setBuyCoef(obj.getDouble("buy"));

                list.add(currency);
            }

            return list;

        } catch (JSONException e){
            e.printStackTrace();
            return null;
        }
    }

    /**
     * Parsing JSON with Currency Exchange Rate for specified Date
     * @param content - JSON with Currency Exchange Rate
     * @return List<Currency> object
     */
    public static List<Currency> parseLogFeed(String content) {

        try {

            JSONObject jsonObject = new JSONObject(content);
            JSONArray ar = jsonObject.getJSONArray("exchangeRate");
            Log.d(MainActivity.LOG_DEBUG, ar.toString());
            List<Currency> list = new ArrayList<>();

            for (int i=0; i<ar.length(); i++){

                JSONObject obj = ar.getJSONObject(i);

                if(obj.getString("currency").equals("EUR") ||
                        obj.getString("currency").equals("USD") ||
                        obj.getString("currency").equals("GBP") ||
                        obj.getString("currency").equals("PLZ") ||
                        obj.getString("currency").equals("RUB") ||
                        obj.getString("currency").equals("CAD") ||
                        obj.getString("currency").equals("CHF") ){

                    Currency currency = new Currency();

                    currency.setName(Currency.Type.valueOf(obj.getString("currency").toUpperCase()));
                    currency.setShortName(obj.getString("currency"));
                    currency.setSaleCoef(obj.getDouble("saleRate"));
                    currency.setBuyCoef(obj.getDouble("purchaseRate"));
                    currency.setSaleCoefNB(obj.getDouble("saleRateNB"));
                    currency.setBuyCoefNB(obj.getDouble("purchaseRateNB"));

                    list.add(currency);
                }
            }

            return list;

        } catch (JSONException e){
            e.printStackTrace();
            return null;
        }
    }

    /**
     * Parsing JSON with Currency Exchange Rate by Date
     * @param content - JSON with Currency Exchange Rate by Date
     * @return TreeMap<Date, Currency> object
     */
    public static TreeMap<Date, Currency> parseMinfinFeed(String content) {

        try {
            JSONArray ar = new JSONArray(content);
            TreeMap<Date, Currency> mapData= new TreeMap<>();

            for (int i=0; i<ar.length(); i++){

                JSONObject obj = ar.getJSONObject(i);
                Currency currency = new Currency();

                currency.setName(Currency.Type.USD);
                currency.setSaleCoef(obj.getDouble("bid"));
                currency.setBuyCoef(obj.getDouble("ask"));

                DateFormat df = new SimpleDateFormat("yyyy-M-dd", Locale.getDefault());
                Date date =  df.parse(obj.getString("date"));

                mapData.put(date, currency);
            }

            return mapData;

        } catch (JSONException e){
            e.printStackTrace();
            return null;
        } catch (ParseException e) {
            e.printStackTrace();
            return null;
        }
    }
}
