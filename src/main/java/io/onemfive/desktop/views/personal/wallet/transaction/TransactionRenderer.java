package io.onemfive.desktop.views.personal.wallet.transaction;

import ra.btc.Transaction;

import java.text.DecimalFormat;

import static java.util.Objects.nonNull;

public class TransactionRenderer extends Transaction {

    public String getFeeString() {
        String feeString = "0.00000000";
        if(nonNull(fee)) {
            DecimalFormat df = new DecimalFormat("#");
            df.setMaximumFractionDigits(8);
            feeString = df.format(fee);
        }
        return feeString;
    }

}
