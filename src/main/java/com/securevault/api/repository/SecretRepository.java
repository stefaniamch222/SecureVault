package com.securevault.api.repository;

import com.securevault.api.model.SecretEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface SecretRepository extends JpaRepository<SecretEntity, Long> {

    // Questo metodo ci permette di cercare un segreto nel DB usando il suo nome (es. "STRIPE_KEY")
    Optional<SecretEntity> findByName(String name);

    // Controlla se esiste già un segreto con questo nome
    boolean existsByName(String name);
}