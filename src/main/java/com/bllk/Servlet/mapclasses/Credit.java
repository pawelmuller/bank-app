package com.bllk.Servlet.mapclasses;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import java.text.SimpleDateFormat;
import java.util.Date;

@Entity
@Table(name = "CREDITS")
public class Credit {
    @Id
    @Column(name = "CREDIT_ID")
    Integer id;
    @Column(name = "OWNER_ID")
    Integer ownerid;
    @Column(name = "NAME")
    String name;
    @Column(name = "VALUE")
    Integer value;
    @Column(name = "CURRENCY_ID")
    Integer currencyid;
    @Column(name = "INTEREST_RATE")
    Double interest;
    @Column(name = "COMMISSION")
    Double commission;
    @Column(name = "RRSO")
    Double rrso;
    @Column(name = "DATE_CREATED")
    Date datecreated;
    @Column(name = "DATE_ENDED")
    Date dateended;
    @Column(name = "MONTHLY_PAYMENT")
    Integer monthly;
    @Column(name = "REMAINING_TO_PAY")
    Integer remaining;



    public Credit(Integer id, Integer ownerid, String name, Integer value, Integer currencyid, Double interest, Double commission, int months) {
        this.id = id;
        this.ownerid = ownerid;
        this.name = name;
        this.value = value;
        this.currencyid = currencyid;
        this.interest = interest;
        this.commission = commission;
        this.monthly = months;
        /*
        this.rrso = null;
        this.datecreated = null;
        this.dateended = null;
        this.remaining = null;
        */
    }
    public Credit(Integer id, Integer ownerid, String name, Integer value, Integer currencyid, Double interest,
                  Double commission, Double rrso, Date datecreated, Date dateended, Integer monthly, Integer remaining) {
        this.id = id;
        this.ownerid = ownerid;
        this.name = name;
        this.value = value;
        this.currencyid = currencyid;
        this.interest = interest;
        this.commission = commission;
        this.rrso = rrso;
        this.datecreated = datecreated;
        this.dateended = dateended;
        this.monthly = monthly;
        this.remaining = remaining;
    }
    public Credit() {}

    public Integer getID() { return id; }
    public void setID(Integer id) { this.id = id; }
    public Integer getOwnerid() { return ownerid; }
    public void setOwnerid(Integer ownerid) { this.ownerid = ownerid; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public Integer getValue() { return value; }
    public void setValue(Integer value) { this.value = value; }
    public Integer getCurrencyID() { return currencyid; }
    public void setCurrencyID(Integer currencyid) { this.currencyid = currencyid; }
    public Double getInterest() { return interest; }
    public void setInterest(Double interest) { this.interest = interest; }
    public Double getCommission() { return commission; }
    public void setCommission(Double commission) { this.commission = commission; }
    public Double getRrso() { return rrso; }
    public void setRrso(Double rrso) { this.rrso = rrso; }
    public Date getDatecreated() { return datecreated; }
    public String getDateCreatedFormatted() {
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH-mm-ss");
        return formatter.format(datecreated);
    }
    public void setDatecreated(Date datecreated) { this.datecreated = datecreated; }
    public Date getDateended() { return dateended; }
    public void setDateended(Date dateended) { this.dateended = dateended; }
    public String getDateEndedFormatted() {
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH-mm-ss");
        return formatter.format(dateended);
    }
    public Integer getMonthly() { return monthly; }
    public void setMonthly(Integer monthly) { this.monthly = monthly; }
    public Integer getRemaining() { return remaining; }
    public void setRemaining(Integer remaining) { this.remaining = remaining; }
}
