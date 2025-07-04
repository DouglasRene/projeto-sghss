// src/main/java/com/sghss/production/repository/TipoExameRepository.java
package com.sghss.production.repository;

import com.sghss.production.model.TipoExame;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface TipoExameRepository extends JpaRepository<TipoExame, Long> {

    // Método para buscar um TipoExame pelo nome (útil para evitar duplicatas ou buscar existentes)
    Optional<TipoExame> findByNome(String nome);
}