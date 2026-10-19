package com.securevault.api.service;

import com.securevault.api.model.SecretEntity;
import com.securevault.api.repository.SecretRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class VaultService {

    private final SecretRepository secretRepository;
    private final EncryptionService encryptionService;

    // Spring inietta automaticamente il Repository e l'EncryptionService grazie al costruttore
    public VaultService(SecretRepository secretRepository, EncryptionService encryptionService) {
        this.secretRepository = secretRepository;
        this.encryptionService = encryptionService;
    }

    // Salva un nuovo segreto (cifrandolo prima)
    public SecretEntity saveSecret(String name, String plainValue) {
        if (secretRepository.existsByName(name)) {
            throw new IllegalArgumentException("Esiste già un segreto con il nome: " + name);
        }

        // Cifriamo il valore in chiaro
        String encryptedValue = encryptionService.encrypt(plainValue);

        SecretEntity secret = new SecretEntity(name, encryptedValue);
        return secretRepository.save(secret);
    }

    // Recupera un segreto per nome e lo decifra
    public Optional<String> getDecryptedSecret(String name) {
        Optional<SecretEntity> optionalSecret = secretRepository.findByName(name);

        if (optionalSecret.isEmpty()) {
            return Optional.empty();
        }

        // Decifriamo il valore salvato nel DB
        String decryptedValue = encryptionService.decrypt(optionalSecret.get().getEncryptedValue());
        return Optional.of(decryptedValue);
    }

    // Restituisce la lista di tutti i nomi dei segreti salvati (senza rivelare i valori)
    public List<String> getAllSecretNames() {
        return secretRepository.findAll().stream()
                .map(SecretEntity::getName)
                .collect(Collectors.toList());
    }

    // Elimina un segreto per nome
    public boolean deleteSecret(String name) {
        Optional<SecretEntity> secret = secretRepository.findByName(name);
        if (secret.isPresent()) {
            secretRepository.delete(secret.get());
            return true;
        }
        return false;
    }
}