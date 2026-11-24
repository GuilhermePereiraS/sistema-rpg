package com.projeto_rpg.inventario.modelo;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ReservaDeEspacosRepository extends JpaRepository<ReservaDeEspacos, Long> {

    Optional<ReservaDeEspacos> findByPersonagemId(Long personagemId);

    boolean existsByPersonagemId(Long personagemId);
}
