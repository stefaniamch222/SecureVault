package com.securevault.api.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.crypto.Cipher;
import javax.crypto.spec.GCMParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.security.SecureRandom;
import java.util.Base64;

@Service
public class EncryptionService {

    private static final String ALGORITHM = "AES/GCM/NoPadding";
    private static final int TAG_LENGTH_BIT = 128; // Lunghezza del tag di autenticazione in bit
    private static final int IV_LENGTH_BYTE = 12;  // Initialization Vector (IV) consigliato per GCM

    private final String masterKey;

    // Legge la chiave master dal file di configurazione (application.yml)
    public EncryptionService(@Value("${vault.encryption.key:ChiaveSegretaDiTestMoltoLunga123456789}") String masterKey) {
        this.masterKey = masterKey;
    }

    // Cifratura: da testo in chiaro a stringa cifrata
    public String encrypt(String plainText) {
        try {
            byte[] iv = new byte[IV_LENGTH_BYTE];
            SecureRandom.getInstanceStrong().nextBytes(iv); // Genera un vettore casuale unico per ogni cifratura

            Cipher cipher = Cipher.getInstance(ALGORITHM);
            SecretKeySpec keySpec = new SecretKeySpec(getKeyBytes(masterKey), "AES");
            GCMParameterSpec gcmParameterSpec = new GCMParameterSpec(TAG_LENGTH_BIT, iv);

            cipher.init(Cipher.ENCRYPT_MODE, keySpec, gcmParameterSpec);
            byte[] encryptedBytes = cipher.doFinal(plainText.getBytes(StandardCharsets.UTF_8));

            // Uniamo l'IV e i dati cifrati in un unico array e li convertiamo in Base64
            byte[] combined = new byte[iv.length + encryptedBytes.length];
            System.arraycopy(iv, 0, combined, 0, iv.length);
            System.arraycopy(encryptedBytes, 0, combined, iv.length, encryptedBytes.length);

            return Base64.getEncoder().encodeToString(combined);
        } catch (Exception e) {
            throw new RuntimeException("Errore durante la cifratura del segreto", e);
        }
    }

    // Decifratura: da stringa cifrata a testo in chiaro
    public String decrypt(String encryptedText) {
        try {
            byte[] combined = Base64.getDecoder().decode(encryptedText);

            // Estraiamo l'IV iniziale dai primi 12 byte
            byte[] iv = new byte[IV_LENGTH_BYTE];
            System.arraycopy(combined, 0, iv, 0, iv.length);

            // Estraiamo il resto del testo cifrato
            int encryptedSize = combined.length - iv.length;
            byte[] encryptedBytes = new byte[encryptedSize];
            System.arraycopy(combined, iv.length, encryptedBytes, 0, encryptedSize);

            Cipher cipher = Cipher.getInstance(ALGORITHM);
            SecretKeySpec keySpec = new SecretKeySpec(getKeyBytes(masterKey), "AES");
            GCMParameterSpec gcmParameterSpec = new GCMParameterSpec(TAG_LENGTH_BIT, iv);

            cipher.init(Cipher.DECRYPT_MODE, keySpec, gcmParameterSpec);
            byte[] decryptedBytes = cipher.doFinal(encryptedBytes);

            return new String(decryptedBytes, StandardCharsets.UTF_8);
        } catch (Exception e) {
            throw new RuntimeException("Errore durante la decifratura del segreto", e);
        }
    }

    // Converte la stringa della chiave master in un formato valido a 32 byte per AES-256
    private byte[] getKeyBytes(String key) {
        byte[] keyBytes = key.getBytes(StandardCharsets.UTF_8);
        byte[] aesKey = new byte[32];
        System.arraycopy(keyBytes, 0, aesKey, 0, Math.min(keyBytes.length, 32));
        return aesKey;
    }
}