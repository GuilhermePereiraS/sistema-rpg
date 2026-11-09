package com.projeto_rpg.ficha.modelo;

import org.springframework.data.jpa.repository.JpaRepository;

public interface RacaRepository extends JpaRepository<Raca, Long> {

    boolean existsByNomeIgnoreCase(String nome);

    boolean existsByNomeIgnoreCaseAndIdNot(String nome, Long id);
}
