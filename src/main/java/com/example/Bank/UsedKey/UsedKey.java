package com.example.Bank.UsedKey;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

import java.util.Random;

@Entity
@Table(name = "usedkeys")
public class UsedKey {

    private String id;

    public UsedKey() {

    }

    public UsedKey(String id) {
        this.id = id;
    }

    @Id
    public String getId() {
        return id;
    }
    public void setId(String id) {
        this.id = id;
    }
}