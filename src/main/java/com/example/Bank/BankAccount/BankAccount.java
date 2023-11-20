package com.example.Bank.BankAccount;

import jakarta.persistence.*;

@Entity
@Table(name = "bankaccounts")
public class BankAccount {

    private String id;
    private String pin;
    private Double balance = 0.0;

    public BankAccount() {

    }

    public BankAccount(String pin, Double balance) {
        this.pin = pin;
        this.balance = balance;
    }

    @Id
    public String getId() {
        return id;
    }
    public void setId(String id) {
        this.id = id;
    }

    @Column(name = "pin", nullable = false)
    public String getPin() {
        return pin;
    }
    public void setPin(String pin) {
        this.pin = pin;
    }

    @Column(name = "balance", nullable = false)
    public Double getBalance() {
        return balance;
    }
    public void setBalance(Double balance) {
        this.balance = balance;
    }

    @Override
    public String toString() {
        return "{" +
                "\"id\": \"" + id + "\"," +
                "\"pin\": \"" + pin + "\"," +
                "\"balance\": " + balance +
                "}";
    }
}