package io.onemfive.desktop;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import ra.btc.BTCWallet;
import ra.btc.Transaction;
import ra.common.JSONParser;
import ra.common.JSONPretty;
import ra.common.JSONSerializable;
import ra.common.identity.DID;

import java.util.HashMap;
import java.util.Map;

public class Cache implements JSONSerializable {

    // Personal
    private DID activePersonalDID;
    private BTCWallet activeWallet;
    private final ObservableList<Transaction> transactions = FXCollections.observableArrayList();
    private final Map<String,DID> localIdentities = new HashMap<>();
    private final Map<String,DID> contacts = new HashMap<>();

    // Community
    private DID activeCommunityDID;
    private BTCWallet activeCommunityWallet;
    private final ObservableList<String> communityTransactions = FXCollections.observableArrayList();
    private final Map<String,DID> communityIdentities = new HashMap<>();
    private DID activeCommunityIdentity;

    // Public
    private BTCWallet publicCharityWallet;
    private final ObservableList<String> charitableTransactions = FXCollections.observableArrayList();
    private final Map<String,DID> publicIdentities = new HashMap<>();
    private DID activePublicDID;

    public void setPersonalActiveWallet(BTCWallet personalActiveWallet) {
        this.activeWallet = personalActiveWallet;
    }

    public BTCWallet getPersonalActiveWallet() {
        return this.activeWallet;
    }

    public void setActivePersonalDID(DID did) {
        this.activePersonalDID = did;
    }

    public DID getActivePersonalDID() {
        return activePersonalDID;
    }

    @Override
    public Map<String, Object> toMap() {
        return null;
    }

    @Override
    public void fromMap(Map<String, Object> map) {

    }

    public String toJSON() {
        return JSONPretty.toPretty(JSONParser.toString(this.toMap()), 4);
    }

    public void fromJSON(String json) {
        this.fromMap((Map)JSONParser.parse(json));
    }
}
