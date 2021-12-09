package io.onemfive.desktop.views.personal.wallet.transaction;

import ra.btc.Transaction;
import ra.common.currency.crypto.BTC;

import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

import static java.util.Objects.nonNull;

public class TransactionRenderer extends Transaction {

    public String getAmountString() {
        String amountString = "0";
        if(nonNull(amount)) {
            amountString = new BTC(amount).valueWithCommas();
        }
        return amountString;
    }

    public String getFeeString() {
        String feeString = "0";
        if(nonNull(fee)) {
            DecimalFormat df = new DecimalFormat("#");
            df.setMaximumFractionDigits(8);
            feeString = df.format(fee);
            feeString = new BTC(feeString).value().toString();
        }
        return feeString;
    }

    public String getTimeString() {
        String timeString = "";
        if(nonNull(time)) {
            SimpleDateFormat f = new SimpleDateFormat("hh.mm.ss a");
            timeString = f.format(new Date((long)time));
        }
        return timeString;
    }

}
