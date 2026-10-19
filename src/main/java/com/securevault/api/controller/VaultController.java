package com.securevault.api.controller;

import com.securevault.api.service.VaultService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/vault")
public class VaultController {

    private final VaultService vaultService;

    public VaultController(VaultService vaultService) {
        this.vaultService = vaultService;
    }

    // 1. Endpoint per salvare un segreto
    // POST /api/vault/secrets
    // Body JSON atteso: { "name": "STRIPE_KEY", "value": "sk_test_12345" }
    @PostMapping("/secrets")
    public ResponseEntity<String> saveSecret(@RequestBody Map<String, String> request) {
        String name = request.get("name");
        String value = request.get("value");

        if (name == null || name.isBlank() || value == null || value.isBlank()) {
            return ResponseEntity.badRequest().body("Nome e valore del segreto sono obbligatori.");
        }

        try {
            vaultService.saveSecret(name, value);
            return ResponseEntity.status(HttpStatus.CREATED).body("Segreto salvato e cifrato con successo.");
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Errore interno del server.");
        }
    }

    // 2. Endpoint per leggere e decifrare un segreto tramite il nome
    // GET /api/vault/secrets/STRIPE_KEY
    @GetMapping("/secrets/{name}")
    public ResponseEntity<Map<String, String>> getSecret(@PathVariable String name) {
        return vaultService.getDecryptedSecret(name)
                .map(decryptedValue -> ResponseEntity.ok(Map.of("name", name, "value", decryptedValue)))
                .orElseGet(() -> ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of("error", "Segreto non trovato")));
    }

    // 3. Endpoint per elencare tutti i nomi dei segreti salvati
    // GET /api/vault/secrets
    @GetMapping("/secrets")
    public ResponseEntity<List<String>> listSecrets() {
        List<String> secretNames = vaultService.getAllSecretNames();
        return ResponseEntity.ok(secretNames);
    }

    // 4. Endpoint per eliminare un segreto
    // DELETE /api/vault/secrets/STRIPE_KEY
    @DeleteMapping("/secrets/{name}")
    public ResponseEntity<String> deleteSecret(@PathVariable String name) {
        boolean deleted = vaultService.deleteSecret(name);
        if (deleted) {
            return ResponseEntity.ok("Segreto eliminato con successo.");
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Segreto non trovato.");
        }
    }
}