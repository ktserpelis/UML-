package com.bank.transfer;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

public class SwiftProtocol implements TransferProtocol {
    @Override
    public boolean processTransfer(double amount, String iban, String swiftCode, String charges) {
        System.out.println("[BRIDGE] Initiating SWIFT International Transfer...");

        // JSON Schema βασισμένο στο screenshot της SWIFT
        // Προσοχή: Το SWIFT API ζητάει currency και beneficiary structure
        String jsonPayload = String.format(
                "{ \"currency\": \"EUR\", \"amount\": %.2f, \"beneficiary\": { \"account\": \"%s\" }, " +
                        "\"beneficiaryBank\": { \"swiftCode\": \"%s\" }, \"fees\": { \"chargingModel\": \"%s\" } }",
                amount, iban, swiftCode, charges
        );

        try {
            HttpClient client = HttpClient.newHttpClient();
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create("http://147.27.70.44:3020/transfer/swift"))
                    .header("Content-Type", "application/json")
                    .POST(HttpRequest.BodyPublishers.ofString(jsonPayload))
                    .build();

            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

            // Το API επιστρέφει "status": "success" ή "failed"
            if (response.statusCode() == 200 && response.body().contains("success")) {
                System.out.println("[BRIDGE] SWIFT Success: " + response.body());
                return true;
            } else {
                System.out.println("[BRIDGE] SWIFT Failed: " + response.body());
                return false;
            }
        } catch (Exception e) {
            System.err.println("[BRIDGE] SWIFT Connection Error: " + e.getMessage());
            return false;
        }
    }
}