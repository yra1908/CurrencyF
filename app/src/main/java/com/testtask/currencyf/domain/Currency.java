package com.testtask.currencyf.domain;

import android.os.Bundle;
import android.os.Parcelable;

/**
 * Created by konstr on 30.10.2015.
 */
public class Currency {

    //	constants for field references
    public static final String CURRENCY_NAME = "currncyName";
    public static final String BUY_COEF = "buyCoef";
    public static final String SALE_COEF = "saleCoef";
    public static final String BUY_COEF_NB = "buyCoefNB";
    public static final String SALE_COEF_NB = "saleCoefNB";


    private String name;
    private String shortName;
    private double buyCoef;
    private double saleCoef;
    private double saleCoefNB;
    private double buyCoefNB;

    public String getName() {
        return name;
    }

    public String getShortName() {
        return shortName;
    }

    public double getBuyCoef() {
        return buyCoef;
    }

    public double getSaleCoef() {
        return saleCoef;
    }

    public double getSaleCoefNB() {
        return saleCoefNB;
    }

    public double getBuyCoefNB() {
        return buyCoefNB;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setShortName(String shortName) {
        this.shortName = shortName;
    }

    public void setBuyCoef(double buyCoef) {
        this.buyCoef = buyCoef;
    }

    public void setSaleCoef(double saleCoef) {
        this.saleCoef = saleCoef;
    }

    public void setSaleCoefNB(double saleCoefNB) {
        this.saleCoefNB = saleCoefNB;
    }

    public void setBuyCoefNB(double buyCoefNB) {
        this.buyCoefNB = buyCoefNB;
    }

    @Override
    public String toString() {
        return "Currency{" +
                "name='" + name + '\'' +
                ", shortName='" + shortName + '\'' +
                ", buyCoef=" + buyCoef +
                ", saleCoef=" + saleCoef +
                ", saleCoefNB=" + saleCoefNB +
                ", buyCoefNB=" + buyCoefNB +
                '}';
    }

    public Currency(){}

    /**
     * Create Currency instancr from a Bundle
     * @param b
     */
    public Currency (Bundle b) {
        if (b != null) {
            this.name = b.getString(CURRENCY_NAME);
            this.buyCoef = b.getDouble(BUY_COEF);
            this.saleCoef = b.getDouble(SALE_COEF);
            this.buyCoefNB=b.getDouble(BUY_COEF_NB);
            this.saleCoefNB=b.getDouble(SALE_COEF_NB);
        }
    }

    /**
     * Pack Currency to Bundle (for transfer between activities)
     * @return Bundle with Currency object
     */
    public Bundle toBundle() {
        Bundle b = new Bundle();
        b.putString(CURRENCY_NAME, this.name);
        b.putDouble(BUY_COEF, this.buyCoef);
        b.putDouble(SALE_COEF, this.saleCoef);
        b.putDouble(BUY_COEF_NB, this.buyCoefNB);
        b.putDouble(SALE_COEF_NB, this.saleCoefNB);
        return b;
    }

}
