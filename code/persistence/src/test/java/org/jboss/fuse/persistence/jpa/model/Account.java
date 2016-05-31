package org.jboss.fuse.persistence.jpa.model;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

public class Account {

    private int id;
    private int balance;

    public Account() {}

    public Account(int id, int balance) {
        this.id = id;
        this.balance = balance;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getBalance() {
        return balance;
    }

    public void setBalance(int balance) {
        this.balance = balance;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || !(o instanceof Account)) return false;

        Account account = (Account) o;

        if (getId() != account.getId()) return false;
        return getBalance() == account.getBalance();

    }

    @Override
    public int hashCode() {
        int result = getId();
        result = 31 * result + getBalance();
        return result;
    }
}
