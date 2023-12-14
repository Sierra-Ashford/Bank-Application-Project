package com.techelevator.tenmo.model;

import java.math.BigDecimal;

public class Account {

    private int id;
    private int user_id;
    private BigDecimal balance;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getUser_id() {
        return user_id;
    }

    public void setUser_id(int user_id) {
        this.user_id = user_id;
    }

    public BigDecimal getBalance() {
        return balance;
    }

    public void setBalance(BigDecimal balance) {
        this.balance = balance;
    }


    @Override
    public String toString() {
        return "\n--------------------------------------------" +
                "\n Account Detail" +
                "\n--------------------------------------------" +
                "\n Id: " + id +
                "\n User_Id: " + user_id +
                "\n Balance: " + balance;
    }
}
