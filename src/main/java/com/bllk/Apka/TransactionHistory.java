package com.bllk.Apka;

import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;

@Entity
@Table(name="TRANSACTIONHISTORY")
public class TransactionHistory {
    @Id
    @GeneratedValue
    private Integer id;
    private Integer payerid;
    private Integer targetid;
    private Double money;
    private String currency;

    public TransactionHistory(Integer payerid, Integer targetid, Double money, String currency) {
        this.payerid = payerid;
        this.targetid = targetid;
        this.money = money;
        this.currency = currency;
    }
    public TransactionHistory(Integer id, Integer payerid, Integer targetid, Double money, String currency) {
        this.id = id;
        this.payerid = payerid;
        this.targetid = targetid;
        this.money = money;
        this.currency = currency;
    }
    public TransactionHistory() {
    }

    @Id
    @GeneratedValue(generator = "incrementator-inator")
    @GenericGenerator(name = "incrementator-inator", strategy = "increment")
    public Integer getID() {
        return id;
    }
    public Integer getPayerid() {
        return payerid;
    }
    public Integer getTargetid() {
        return targetid;
    }
    public Double getMoney() {
        return money;
    }
    public String getCurrency() {
        return currency;
    }

    public void setID(Integer id) {
        this.id = id;
    }
    public void setPayerid(Integer payerid) {
        this.payerid = payerid;
    }
    public void setTargetid(Integer targetid) {
        this.targetid = targetid;
    }
    public void setMoney(Double money) {
        this.money = money;
    }
    public void setCurrency(String currency) {
        this.currency = currency;
    }
}