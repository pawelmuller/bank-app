package com.bllk.Servlet.mapclasses;

import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;

@Entity
@Table(name = "ACCOUNTS")
public class Account {
    @Id
    @Column(name = "ACCOUNT_ID")
    private Integer id;
    @Column(name = "VALUE")
    private Integer value;
    @Column(name = "CURRENCY_ID")
    private Integer currency_id;
    @Column(name = "OWNER_ID")
    private Integer owner_id;

    public Integer getID() {
        return id;
    }
    public void setID(Integer id) {
        this.id = id;
    }
    public Integer getValue() {
        return value;
    }
    public Integer getOwnerID() {
        return owner_id;
    }
    public void setValue(Integer value) {
        this.value = value;
    }
    public Integer getCurrencyID() {
        return currency_id;
    }
    public void setCurrencyID(Integer currency_id) {
        this.currency_id = currency_id;
    }
    public void setOwnerID(Integer owner_id) {
        this.owner_id = owner_id;
    }

    public Account() {
    }
    public Account(Integer id, Integer value, Integer currency_id, Integer owner_id) {
        this.id = id;
        this.value = value;
        this.currency_id = currency_id;
        this.owner_id = owner_id;
    }
    public Account(Integer id, Integer currency_id) {
        this.id = id;
        this.currency_id = currency_id;
    }
}
