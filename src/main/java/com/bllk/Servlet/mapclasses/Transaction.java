package com.bllk.Servlet.mapclasses;

import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;

@Entity
@Table(name="TRANSACTIONS")
public class Transaction {
    @Id
    @Column(name = "TRANSACTION_ID")
    private Integer id;
    @Column(name = "SENDER_ID")
    private Integer senderid;
    @Column(name = "RECEIVER_ID")
    private Integer receiverid;
    @Column(name = "TITLE")
    private String title;
    @Column(name = "VALUE")
    private Integer value;
    @Column(name = "CURRENCY_ID")
    private Integer currencyid;

    public Transaction(Integer senderid, Integer receiverid, String title, Integer value, Integer currencyid) {
        this.senderid = senderid;
        this.receiverid = receiverid;
        this.title = title;
        this.value = value;
        this.currencyid = currencyid;
    }
    public Transaction(Integer id, Integer senderid, Integer receiverid, String title, Integer value, Integer currencyid) {
        this.id = id;
        this.senderid = senderid;
        this.receiverid = receiverid;
        this.title = title;
        this.value = value;
        this.currencyid = currencyid;
    }
    public Transaction() {
    }

    public Integer getID() {
        return id;
    }
    public void setID(Integer id) {
        this.id = id;
    }
    public Integer getSenderID() {
        return senderid;
    }
    public void setSenderID(Integer senderid) {
        this.senderid = senderid;
    }
    public Integer getReceiverID() {
        return receiverid;
    }
    public void setReceiverID(Integer receiverid) {
        this.receiverid = receiverid;
    }
    public String getTitle() {
        return title;
    }
    public void setTitle(String title) {
        this.title = title;
    }
    public Integer getValue() {
        return value;
    }
    public void setValue(Integer value) {
        this.value = value;
    }
    public Integer getCurrencyID() {
        return currencyid;
    }
    public void setCurrencyID(Integer currencyid) {
        this.currencyid = currencyid;
    }
}