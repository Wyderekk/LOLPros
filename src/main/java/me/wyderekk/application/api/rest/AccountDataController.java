package me.wyderekk.application.api.rest;

import com.fasterxml.jackson.databind.ObjectMapper;
import me.wyderekk.application.data.datatypes.AccountData;
import me.wyderekk.application.data.database.SQLite;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;

@RestController
@RequestMapping("/api")
public class AccountDataController {

    @GetMapping("/accounts/{name}")
    public ResponseEntity<String> getAccounts(@PathVariable String name) {

        ArrayList<AccountData> accountData = SQLite.getAccountData(name);

        if (!accountData.isEmpty()) {
            try {
                ObjectMapper objectMapper = new ObjectMapper();
                String jsonData = objectMapper.writeValueAsString(accountData);
                return ResponseEntity.ok(jsonData);
            } catch (Exception e) {
                e.printStackTrace();
                return ResponseEntity.status(500).body("Internal Server Error");
            }
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    @GetMapping("/account/{name}/{index}")
    public ResponseEntity<String> getAccountData(@PathVariable String name, @PathVariable int index) {

        ArrayList<AccountData> accountData = SQLite.getAccountData(name);
        System.out.println(accountData.size());
        if (!accountData.isEmpty()) {
            if(index >= accountData.size()) {
                return ResponseEntity.notFound().build();
            } else {
                try {
                    ObjectMapper objectMapper = new ObjectMapper();
                    String jsonData = objectMapper.writeValueAsString(accountData.get(index));
                    return ResponseEntity.ok(jsonData);
                } catch (Exception e) {
                    return ResponseEntity.status(500).body("Internal Server Error");
                }
            }
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    @GetMapping("/account/{name}/{index}/rank")
    public ResponseEntity<String> getAccountRank(@PathVariable String name, @PathVariable int index) {

        ArrayList<AccountData> accountData = SQLite.getAccountData(name);

        if (!accountData.isEmpty()) {
            if(index > accountData.size()) {
                return ResponseEntity.notFound().build();
            } else {
                try {
                    ObjectMapper objectMapper = new ObjectMapper();
                    String jsonData = objectMapper.writeValueAsString(accountData.get(index).rank());
                    return ResponseEntity.ok(jsonData);
                } catch (Exception e) {
                    return ResponseEntity.status(500).body("Internal Server Error");
                }
            }
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    @GetMapping("/account/{name}/{index}/peak")
    public ResponseEntity<String> getAccountPeak(@PathVariable String name, @PathVariable int index) {

        ArrayList<AccountData> accountData = SQLite.getAccountData(name);

        if (!accountData.isEmpty()) {
            if(index > accountData.size()) {
                return ResponseEntity.notFound().build();
            } else {
                try {
                    ObjectMapper objectMapper = new ObjectMapper();
                    String jsonData = objectMapper.writeValueAsString(accountData.get(index).peak());
                    return ResponseEntity.ok(jsonData);
                } catch (Exception e) {
                    return ResponseEntity.status(500).body("Internal Server Error");
                }
            }
        } else {
            return ResponseEntity.notFound().build();
        }
    }
}