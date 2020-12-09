package com.bllk.Apka;

import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;

@Entity
public class Money {
    @Id
    @GeneratedValue
    private Integer id;
    private Double moneyonaccount;
    private Integer accountid;

    public Money(Double moneyonaccount, Integer accountid) {
        this.moneyonaccount = moneyonaccount;
        this.accountid = accountid;
    }

    public Money(Integer id, Double moneyonaccount, Integer accountid) {
        this.id = id;
        this.moneyonaccount = moneyonaccount;
        this.accountid = accountid;
    }

    public Money() {
    }

    @Id
    @GeneratedValue(generator = "incrementator-inator")
    @GenericGenerator(name = "incrementator-inator", strategy = "increment")
    public Integer getID() {
        return id;
    }
    public Double getMoneyonaccount() {
        return moneyonaccount;
    }
    public Integer getAccountid() {
        return accountid;
    }

    public void setID(Integer id) {
        this.id = id;
    }
    public void setMoneyonaccount(Double moneyonaccount) {
        this.moneyonaccount = moneyonaccount;
    }
    public void setAccountid(Integer accountid) {
        this.accountid = accountid;
    }
}
