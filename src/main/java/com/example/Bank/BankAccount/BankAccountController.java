package com.example.Bank.BankAccount;

import java.util.HashMap;

import java.util.List;
import java.util.Map;
import java.util.Random;

import com.example.Bank.Error.InsufficientBalanceException;
import com.example.Bank.Error.ResourceNotFoundException;
import com.example.Bank.Error.WithdrawalFailedException;
import com.example.Bank.UsedKey.UsedKey;
import com.example.Bank.UsedKey.UsedKeyRepository;
import jakarta.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;


@RestController
@RequestMapping("/api/v1")
public class BankAccountController {
    @Autowired
    private BankAccountRepository bankAccountRepository;

    @Autowired
    private UsedKeyRepository usedKeyRepository;

    private final BankAccountService bankAccountService;

    @Autowired
    public BankAccountController(BankAccountService bankAccountService) {
        this.bankAccountService = bankAccountService;
    }

    @GetMapping("/bank_accounts")
    public List <BankAccount> getAllBankAccounts() {
        return bankAccountRepository.findAll();
    }

    @GetMapping("/bank_accounts/{id}")
    public ResponseEntity < BankAccount > getBankAccountById(@PathVariable(value = "id") String bankAccountId)
            throws ResourceNotFoundException {
        BankAccount bankAccount = bankAccountRepository.findById(bankAccountId)
                .orElseThrow(() -> new ResourceNotFoundException("Bank account not found for this id :: " + bankAccountId));
        return ResponseEntity.ok().body(bankAccount);
    }

    @PostMapping("/bank_accounts")
    public BankAccount createBankAccount(@Valid @RequestBody BankAccount bankAccount) {
        String generatedId = generateAccountKey();
        String generatedPin = generateRandomInt();

        bankAccount.setId(generatedId);
        bankAccount.setPin(generatedPin);

        Double clientProvidedBalance = bankAccount.getBalance();
        bankAccount.setBalance(clientProvidedBalance);

        return bankAccountRepository.save(bankAccount);
    }

    @PutMapping("/bank_accounts/{id}")
    public ResponseEntity < BankAccount > updateBankAccount(@PathVariable(value = "id") String bankAccountId,
                                                      @Valid @RequestBody BankAccount bankAccountDetails) throws ResourceNotFoundException {
        BankAccount bankAccount = bankAccountRepository.findById(bankAccountId)
                .orElseThrow(() -> new ResourceNotFoundException("Bank account not found for this id :: " + bankAccountId));

        bankAccount.setPin(bankAccountDetails.getPin());
        bankAccount.setBalance(bankAccountDetails.getBalance());
        final BankAccount updatedbankAccount = bankAccountRepository.save(bankAccount);
        return ResponseEntity.ok(updatedbankAccount);
    }

    @DeleteMapping("/bank_accounts/{id}")
    public Map < String, Boolean > deleteBankAccount(@PathVariable(value = "id") String bankAccountId)
            throws ResourceNotFoundException {
        BankAccount bankAccount = bankAccountRepository.findById(bankAccountId)
                .orElseThrow(() -> new ResourceNotFoundException("Bank account not found for this id :: " + bankAccountId));

        bankAccountRepository.delete(bankAccount);
        Map < String, Boolean > response = new HashMap < > ();
        response.put("deleted", Boolean.TRUE);
        return response;
    }
    // generate key to be recognized for
    private String generateAccountKey() {
        int keyLength = 10;
        StringBuilder key = new StringBuilder();

        String characters = "ABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";
        Random random = new Random();

        while (true) {
            for (int i = 0; i < keyLength; i++) {
                key.append(characters.charAt(random.nextInt(characters.length())));
            }

            if (!usedKeyRepository.existsById(key.toString())) {
                usedKeyRepository.save(new UsedKey(key.toString()));
                break;
            } else {
                key.setLength(0);
            }
        }
        return key.toString();
    }
    // generate pin
    private static String generateRandomInt() {
        int min = 0;
        int max = 9999;

        Random random = new Random();

        int randomNum = random.nextInt(max - min + 1) + min;

        return String.format("%04d", randomNum);
    }

    @PostMapping("/bank_accounts/deposit/{id}")
    public ResponseEntity<?> increaseBalance(
            @PathVariable String id,
            @RequestBody Map<String, String> requestBody) {
        try {
            if (id == null || requestBody == null || !requestBody.containsKey("amount")) {
                throw new ResourceNotFoundException(id + requestBody);
            }
            String amountString = requestBody.get("amount");
            Double amount = Double.parseDouble(amountString);
            if (amount < 0) {
                throw new InsufficientBalanceException("Amount can not be negative");
            }

            BankAccount bankAccount = bankAccountService.increaseBalance(id, amount);
            return ResponseEntity.ok().body(Map.of("success", true, "data", bankAccount));
        } catch (WithdrawalFailedException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Map.of("success", false, "error", e.getMessage()));
        } catch (ResourceNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of("success", false, "error", e.getMessage()));
        } catch (NumberFormatException e){
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Map.of("success", false, "error", e.getMessage()));
        }
    }

    @PostMapping("/bank_accounts/withdraw/{id}")
    public ResponseEntity<?> decreaseBalance(
            @PathVariable String id,
            @RequestBody Map<String, String> requestBody) {
        try {
            if (id == null || requestBody == null || !requestBody.containsKey("amount")) {
                throw new ResourceNotFoundException("Input data missing");
            }
            String amountString = requestBody.get("amount");
            Double amount = Double.parseDouble(amountString);
            if (amount < 0) {
                throw new InsufficientBalanceException("Amount can not be negative");
            }
            BankAccount bankAccount = bankAccountService.decreaseBalance(id, amount);
            return ResponseEntity.ok().body(Map.of("success", true, "data", bankAccount));
        } catch (InsufficientBalanceException e) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(Map.of("success", false, "error", e.getMessage()));
        } catch (WithdrawalFailedException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Map.of("success", false, "error", e.getMessage()));
        } catch (ResourceNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of("success", false, "error", e.getMessage()));
        } catch (NumberFormatException e){
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Map.of("success", false, "error", e.getMessage()));
        }
    }

    @PostMapping("/bank_accounts/transfer/{id}")
    public ResponseEntity<?> transferBalance(
            @PathVariable String id,
            @RequestBody Map<String, String> requestBody) {
        try {
            if (id == null || requestBody == null || !requestBody.containsKey("amount") || !requestBody.containsKey("receiver")) {
                throw new ResourceNotFoundException("Input data missing");
            }
            String amountString = requestBody.get("amount");
            String receiverId = requestBody.get("receiver");
            Double amount = Double.parseDouble(amountString);
            if (amount < 0) {
                throw new InsufficientBalanceException("Amount can not be negative");
            }
            Map<String, BankAccount> bankAccountMap = bankAccountService.transferBalance(id, receiverId, amount);
            return ResponseEntity.ok().body(Map.of("success", true, "sender", bankAccountMap.get("senderAccount"), "receiver", bankAccountMap.get("receiverAccount")));
        } catch (InsufficientBalanceException e) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(Map.of("success", false, "error", e.getMessage()));
        } catch (WithdrawalFailedException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Map.of("success", false, "error", e.getMessage()));
        } catch (ResourceNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of("success", false, "error", e.getMessage()));
        } catch (NumberFormatException e){
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Map.of("success", false, "error", e.getMessage()));
        }
    }
}