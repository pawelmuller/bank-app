package com.bllk.Servlet.mapclasses;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import java.util.Date;

@Entity
@Table(name = "CREDITS")
public class Credit {
    @Id
    @Column(name = "CREDIT_ID")
    Integer id;
    @Column(name = "NAME")
    String name;
    @Column(name = "VALUE")
    Integer value;
    @Column(name = "INTEREST_RATE")
    Double interest;
    @Column(name = "RRSO")
    Double rrso;
    @Column(name = "DATE_CREATED")
    Date datecreated;
    @Column(name = "DATE_ENDS")
    Date dateend;
    @Column(name = "CURRENCY_ID")
    Integer currencyid;

    public Credit(Integer id, String name, Integer value, Double interest, Double rrso, Date datecreated, Date dateend, Integer currencyid) {
        this.id = id;
        this.name = name;
        this.value = value;
        this.interest = interest;
        this.rrso = rrso;
        this.datecreated = datecreated;
        this.dateend = dateend;
        this.currencyid = currencyid;
    }
    public Credit() {}

    public Integer getID() {
        return id;
    }
    public void setID(Integer id) {
        this.id = id;
    }
    public String getName() {
        return name;
    }
    public void setName(String name) {
        this.name = name;
    }
    public Integer getValue() {
        return value;
    }
    public void setValue(Integer value) {
        this.value = value;
    }
    public Double getInterest() {
        return interest;
    }
    public void setInterest(Double interest) {
        this.interest = interest;
    }
    public Double getRrso() {
        return rrso;
    }
    public void setRrso(Double rrso) {
        this.rrso = rrso;
    }
    public Date getDatecreated() {
        return datecreated;
    }
    public void setDatecreated(Date datecreated) {
        this.datecreated = datecreated;
    }
    public Date getDateend() {
        return dateend;
    }
    public void setDateend(Date dateend) {
        this.dateend = dateend;
    }
    public Integer getCurrencyID() {
        return currencyid;
    }
    public void setCurrencyID(Integer currencyid) {
        this.currencyid = currencyid;
    }
}
