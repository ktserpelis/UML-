package com.bank.transfer;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

public class SepaProtocol implements TransferProtocol {
    @Override
    public boolean processTransfer(double amount, String iban, String bic, String charges) {
        System.out.println("[BRIDGE] Initiating SEPA Transfer via API...");

        // Το JSON Schema ακριβώς όπως το ζητάει το screenshot σας
        String jsonPayload = String.format(
                "{ \"amount\": %.2f, \"creditor\": { \"iban\": \"%s\" }, \"creditorBank\": { \"bic\": \"%s\" }, \"execution\": { \"charges\": \"%s\" } }",
                amount, iban, bic, charges
        );

        try {
            HttpClient client = HttpClient.newHttpClient();
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create("http://147.27.70.44:3020/transfer/sepa"))
                    .header("Content-Type", "application/json")
                    .POST(HttpRequest.BodyPublishers.ofString(jsonPayload))
                    .build();

            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

            if (response.statusCode() == 200 && response.body().contains("success")) {
                System.out.println("[BRIDGE] SEPA Success: " + response.body());
                return true;
            } else {
                System.out.println("[BRIDGE] SEPA Failed: " + response.body());
                return false;
            }
        } catch (Exception e) {
            System.err.println("[BRIDGE] Connection Error: " + e.getMessage());
            return false;
        }
    }
}