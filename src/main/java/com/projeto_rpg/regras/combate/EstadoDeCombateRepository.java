package com.projeto_rpg.regras.combate;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface EstadoDeCombateRepository extends JpaRepository<EstadoDeCombate, Long> {

    Optional<EstadoDeCombate> findByPersonagemId(Long personagemId);

    boolean existsByPersonagemId(Long personagemId);
}
