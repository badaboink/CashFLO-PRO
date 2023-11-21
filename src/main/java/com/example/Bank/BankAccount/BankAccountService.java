package com.example.Bank.BankAccount;

import com.example.Bank.Error.InsufficientBalanceException;
import com.example.Bank.Error.ResourceNotFoundException;
import com.example.Bank.Error.WithdrawalFailedException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.Map;

@Service
public class BankAccountService {
    private final BankAccountRepository bankAccountRepository;

    @Autowired
    public BankAccountService(BankAccountRepository bankAccountRepository) {
        this.bankAccountRepository = bankAccountRepository;
    }


    public BankAccount increaseBalance(String bankAccountId, Double amount) throws ResourceNotFoundException {
        try {
            BankAccount bankAccount = bankAccountRepository.findById(bankAccountId)
                    .orElseThrow(() -> new ResourceNotFoundException("Bank account not found for this id :: " + bankAccountId));

            Double currentBalance = bankAccount.getBalance();
            Double newBalance = currentBalance + amount;
            bankAccount.setBalance(newBalance);
            return bankAccountRepository.save(bankAccount);
        } catch (DataIntegrityViolationException e) {
            throw new WithdrawalFailedException("Failed to update bank account balance", e);
        }
    }
    @Transactional
    public BankAccount decreaseBalance(String bankAccountId, Double amount) throws ResourceNotFoundException {
        try {
            BankAccount bankAccount = bankAccountRepository.findById(bankAccountId)
                    .orElseThrow(() -> new ResourceNotFoundException("Bank account not found for this id :: " + bankAccountId));

            Double currentBalance = bankAccount.getBalance();
            if (currentBalance - amount >= 0) {
                Double newBalance = currentBalance - amount;
                bankAccount.setBalance(newBalance);
                return bankAccountRepository.save(bankAccount);
            } else {
                throw new InsufficientBalanceException("Insufficient balance for withdrawal");
            }
        } catch (DataIntegrityViolationException e) {
            throw new WithdrawalFailedException("Failed to update bank account balance", e);
        }
    }

    @Transactional
    public Map<String, BankAccount> transferBalance(String bankAccountId, String receiverId, Double amount) throws ResourceNotFoundException {
        try {
            BankAccount senderAccount = bankAccountRepository.findById(bankAccountId)
                    .orElseThrow(() -> new ResourceNotFoundException("Bank account not found for this id :: " + bankAccountId));
            BankAccount receiverAccount = bankAccountRepository.findById(receiverId)
                    .orElseThrow(() -> new ResourceNotFoundException("Bank account not found for this id :: " + receiverId));

            Double currentBalance = senderAccount.getBalance();
            if (currentBalance - amount >= 0) {
                Double newBalance = currentBalance - amount;
                senderAccount.setBalance(newBalance);
                Double receiveNewBalance = receiverAccount.getBalance() + amount;
                receiverAccount.setBalance(receiveNewBalance);
                Map<String, BankAccount> result = new HashMap<>();
                result.put("senderAccount", bankAccountRepository.save(receiverAccount));
                result.put("receiverAccount", bankAccountRepository.save(senderAccount));
                return result;
            } else {
                throw new InsufficientBalanceException("Insufficient balance for transfer");
            }
        } catch (DataIntegrityViolationException e) {
            throw new WithdrawalFailedException("Failed to update bank account balance", e);
        }
    }
}
