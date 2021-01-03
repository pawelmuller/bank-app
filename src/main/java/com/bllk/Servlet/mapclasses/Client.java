package com.bllk.Servlet.mapclasses;

import java.text.ParseException;
import java.text.SimpleDateFormat;
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
    @Column(name = "LOGIN_ID")
    Integer login_id;

    public Client(Integer id, String name, String surname, Date birth_date, Integer address_id, Integer login_id) {
        this.id = id;
        this.name = name;
        this.surname = surname;
        this.birth_date = birth_date;
        this.address_id = address_id;
        this.login_id = login_id;
    }
    public Client(Integer id, String name, String surname, String birth_date, Integer address_id, Integer login_id) {
        try {
            this.id = id;
            this.name = name;
            this.surname = surname;
            SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
            this.birth_date = formatter.parse(birth_date);
            this.address_id = address_id;
            this.login_id = login_id;
        } catch (ParseException ex) {
            System.out.println(ex.getMessage());
        }
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
    public Integer getLoginID() {
        return login_id;
    }
    public void setLoginID(Integer login_id) {
        this.login_id = login_id;
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
