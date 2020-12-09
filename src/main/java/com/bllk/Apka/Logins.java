package com.bllk.Apka;

import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;

@Entity
public class Logins {
    @Id
    @GeneratedValue
    private Integer id;
    private String login;
    private String password;
    private Integer accountid;

    public Logins(String login, String password, Integer accountid) {
        this.login = login;
        this.password = password;
        this.accountid = accountid;
    }
    public Logins(Integer id, String login, String password, Integer accountid) {
        this.id = id;
        this.login = login;
        this.password = password;
        this.accountid = accountid;
    }
    public Logins() {
    }

    @Id
    @GeneratedValue(generator = "incrementator-inator")
    @GenericGenerator(name = "incrementator-inator", strategy = "increment")
    public Integer getID() {
        return id;
    }
    public void setID(Integer id) {
        this.id = id;
    }
    public String getLogin() {
        return login;
    }
    public void setLogin(String login) {
        this.login = login;
    }
    public String getPassword() {
        return password;
    }
    public void setPassword(String password) {
        this.password = password;
    }
    public Integer getAccountid() {
        return accountid;
    }
    public void setAccountid(Integer accountid) {
        this.accountid = accountid;
    }
}
