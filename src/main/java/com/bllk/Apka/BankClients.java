package com.bllk.Apka;

import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;

@Entity
@Table
public class BankClients {
    private Integer id;
    private String name;
    private String surname;

    public BankClients(String name, String surname) {
        this.name = name;
        this.surname = surname;
    }

    public BankClients(Integer id, String name, String surname) {
        this.id = id;
        this.name = name;
        this.surname = surname;
    }
    public BankClients() {
    }

    @Id
    @GeneratedValue(generator = "incrementator-inator")
    @GenericGenerator(name = "incrementator-inator", strategy = "increment")
    public Integer getID() {
        return id;
    }
    public String getName() {
        return name;
    }
    public String getSurname() {
        return surname;
    }

    public void setID(int id) {
        this.id = id;
    }
    public void setName(String name) {
        this.name = name;
    }
    public void setSurname(String surname) {
        this.surname = surname;
    }
}
