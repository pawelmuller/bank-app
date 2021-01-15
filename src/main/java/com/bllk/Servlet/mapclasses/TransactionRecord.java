package com.bllk.Servlet.mapclasses;

import org.hibernate.annotations.Generated;
import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;
import java.text.SimpleDateFormat;
import java.util.Date;

@Entity
@Table(name="TRANSACTIONS")
public class TransactionRecord {
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
    private Long value;
    @Column(name = "CURRENCY_ID")
    private Integer currencyid;
    @Column(name = "TRANSACTION_DATE")
    private Date date;

    public TransactionRecord(Integer senderid, Integer receiverid, String title, Long value, Integer currencyid) {
        this.senderid = senderid;
        this.receiverid = receiverid;
        this.title = title;
        this.value = value;
        this.currencyid = currencyid;
    }
    public TransactionRecord(Integer id, Integer senderid, Integer receiverid, String title, Long value, Integer currencyid) {
        this.id = id;
        this.senderid = senderid;
        this.receiverid = receiverid;
        this.title = title;
        this.value = value;
        this.currencyid = currencyid;
    }
    public TransactionRecord() {
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
    public Long getValue() {
        return value;
    }
    public void setValue(Long value) {
        this.value = value;
    }
    public Integer getCurrencyID() {
        return currencyid;
    }
    public void setCurrencyID(Integer currencyid) {
        this.currencyid = currencyid;
    }
    public Date getDate() {
        return date;
    }
    public String getDateFormatted() {
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH-mm-ss");
        return formatter.format(date);
    }
    public void setDate(Date date) {
        this.date = date;
    }
}