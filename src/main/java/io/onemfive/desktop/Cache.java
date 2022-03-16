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

    public BTCWallet getActiveWallet() {
        return activeWallet;
    }

    public void setActiveWallet(BTCWallet activeWallet) {
        this.activeWallet = activeWallet;
    }

    public ObservableList<Transaction> getTransactions() {
        return transactions;
    }

    public Map<String, DID> getLocalIdentities() {
        return localIdentities;
    }

    public void setLocalIdentities(Map<String, DID> localIdentities) {
        this.localIdentities = localIdentities;
    }

    public Map<String, DID> getContacts() {
        return contacts;
    }

    public void setContacts(Map<String, DID> contacts) {
        this.contacts = contacts;
    }

    public DID getActiveCommunity() {
        return activeCommunity;
    }

    public void setActiveCommunity(DID activeCommunity) {
        this.activeCommunity = activeCommunity;
    }

    public DID getActiveCommunityDID() {
        return activeCommunityDID;
    }

    public void setActiveCommunityDID(DID activeCommunityDID) {
        this.activeCommunityDID = activeCommunityDID;
    }

    public BTCWallet getActiveCommunityWallet() {
        return activeCommunityWallet;
    }

    public void setActiveCommunityWallet(BTCWallet activeCommunityWallet) {
        this.activeCommunityWallet = activeCommunityWallet;
    }

    public ObservableList<String> getActiveCommunityTransactions() {
        return activeCommunityTransactions;
    }

    public Map<String, DID> getCommunities() {
        return communities;
    }

    public void setCommunities(Map<String, DID> communities) {
        this.communities = communities;
    }

    public BTCWallet getPublicCharityWallet() {
        return publicCharityWallet;
    }

    public void setPublicCharityWallet(BTCWallet publicCharityWallet) {
        this.publicCharityWallet = publicCharityWallet;
    }

    public ObservableList<String> getActiveCharitableTransactions() {
        return activeCharitableTransactions;
    }

    public Map<String, DID> getPublicCharities() {
        return publicCharities;
    }

    public void setPublicCharities(Map<String, DID> publicCharities) {
        this.publicCharities = publicCharities;
    }

    public DID getActivePublicDID() {
        return activePublicDID;
    }

    public void setActivePublicDID(DID activePublicDID) {
        this.activePublicDID = activePublicDID;
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
