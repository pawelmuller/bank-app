package com.bllk.Servlet.mapclasses;

import java.util.Date;
import javax.persistence.*;
import java.util.Objects;

@Entity
@Table(name ="CLIENTS")
public class Client {
    @Id
    @Column(name = "CLIENT_ID")
    Integer id;
    @Column(name = "NAME")
    String name;
    @Column(name = "SURNAME")
    String surname;
    @Column(name = "BIRTH_DATE")
    Date birth_date;
    @Column(name = "ADDRESS_ID")
    Integer address_id;

    public Client(Integer id, String name, String surname) {
        this.id = id;
        this.name = name;
        this.surname = surname;
    }
    public Client(Integer id, String name, String surname, Date birth_date, Integer address_id) {
        this.id = id;
        this.name = name;
        this.surname = surname;
        this.birth_date = birth_date;
        this.address_id = address_id;
    }
    public Client() {
    }

    public Integer getID() {
        return id;
    }
    public String getName() {
        return name;
    }
    public String getSurname() {
        return surname;
    }
    public Date getBirth_date() {
        return birth_date;
    }
    public int getAddress_id() {
        return address_id;
    }

    public void setID(Integer id) {
        this.id = id;
    }
    public void setName(String name) {
        this.name = name;
    }
    public void setSurname(String surname) {
        this.surname = surname;
    }
    public void setBirth_date(Date birth_date) {
        this.birth_date = birth_date;
    }
    public void setAddress_id(int address_id) {
        this.address_id = address_id;
    }

    @Override
    public String toString() {
        return "BankClients{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", surname='" + surname + '\'' +
                '}';
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, name, surname);
    }
}
