package com.securevault.api.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "secrets")
public class SecretEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // Il nome del segreto (es. "STRIPE_API_KEY"), deve essere unico
    @Column(nullable = false, unique = true)
    private String name;

    // Il valore del segreto cifrato (lungo abbastanza per contenere il testo cifrato)
    @Column(nullable = false, length = 1024)
    private String encryptedValue;

    // Data di creazione automatica
    @Column(nullable = false)
    private LocalDateTime createdAt;

    @PrePersist
    protected void onCreate() {
        this.createdAt = LocalDateTime.now();
    }

    // Costruttore vuoto (richiesto da JPA)
    public SecretEntity() {}

    // Costruttore con parametri
    public SecretEntity(String name, String encryptedValue) {
        this.name = name;
        this.encryptedValue = encryptedValue;
    }

    // --- Getter e Setter ---
    public Long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEncryptedValue() {
        return encryptedValue;
    }

    public void setEncryptedValue(String encryptedValue) {
        this.encryptedValue = encryptedValue;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }
}