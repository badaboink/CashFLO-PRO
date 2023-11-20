package com.example.Bank.UsedKey;

import com.example.Bank.BankAccount.BankAccount;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UsedKeyRepository extends JpaRepository<UsedKey, String>{

}