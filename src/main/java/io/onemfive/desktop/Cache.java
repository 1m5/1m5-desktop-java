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
    private Map<String,DID> localIdentities = new HashMap<>();
    private Map<String,DID> contacts = new HashMap<>();

    // Community
    private DID activeCommunity;
    private DID activeCommunityDID; // User's DID within active Community
    private BTCWallet activeCommunityWallet;
    private final ObservableList<String> activeCommunityTransactions = FXCollections.observableArrayList();
    private Map<String,DID> communities = new HashMap<>();

    // Public
    private BTCWallet publicCharityWallet;
    private final ObservableList<String> activeCharitableTransactions = FXCollections.observableArrayList();
    private Map<String,DID> publicCharities = new HashMap<>();
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
        Map<String,Object> m = new HashMap<>();
        if(activePersonalDID!=null) m.put("activePersonalDID", activePersonalDID.toMap());
        if(activeWallet!=null) m.put("activeWallet", activeWallet.toMap());
        if(activeCommunity!=null) m.put("activeCommunity", activeCommunity.toMap());
        if(activeCommunityDID!=null) m.put("activeCommunityDID", activeCommunityDID.toMap());
        if(activeCommunityWallet!=null) m.put("activeCommunityWallet", activeCommunityWallet.toMap());
        if(publicCharityWallet!=null) m.put("publicCharityWallet", publicCharityWallet.toMap());
        if(activePublicDID!=null) m.put("activePublicDID", activePublicDID.toMap());
        return m;
    }

    @Override
    public void fromMap(Map<String, Object> m) {
        if(m.get("activePersonalDID")!=null) {
            activePersonalDID = new DID();
            activePersonalDID.fromMap((Map<String,Object>)m.get("activePersonalDID"));
        }
        if(m.get("activeWallet")!=null) {
            activeWallet = new BTCWallet();
            activeWallet.fromMap((Map<String,Object>)m.get("activeWallet"));
        }
        if(m.get("activeCommunity")!=null) {
            activeCommunity = new DID();
            activeCommunity.fromMap((Map<String,Object>)m.get("activeCommunity"));
        }
        if(m.get("activeCommunityDID")!=null) {
            activeCommunityDID = new DID();
            activeCommunityDID.fromMap((Map<String,Object>)m.get("activeCommunityDID"));
        }
        if(m.get("activeCommunityWallet")!=null) {
            activeCommunityWallet = new BTCWallet();
            activeCommunityWallet.fromMap((Map<String,Object>)m.get("activeCommunityWallet"));
        }
        if(m.get("publicCharityWallet")!=null) {
            publicCharityWallet = new BTCWallet();
            publicCharityWallet.fromMap((Map<String,Object>)m.get("publicCharityWallet"));
        }
        if(m.get("activePublicDID")!=null) {
            activePublicDID = new DID();
            activePublicDID.fromMap((Map<String,Object>)m.get("activePublicDID"));
        }
    }

    public String toJSON() {
        return JSONPretty.toPretty(JSONParser.toString(this.toMap()), 4);
    }

    public void fromJSON(String json) {
        this.fromMap((Map)JSONParser.parse(json));
    }
}
