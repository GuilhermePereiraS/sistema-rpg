package com.projeto_rpg.inventario.modelo;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface InventarioRepository extends JpaRepository<Inventario, Long> {

    Optional<Inventario> findByPersonagemId(Long personagemId);

    boolean existsByPersonagemId(Long personagemId);

    boolean existsByItensId(Long itemId);
}
