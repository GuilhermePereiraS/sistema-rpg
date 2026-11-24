package com.projeto_rpg.inventario.modelo;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface HabilidadeRepository extends JpaRepository<Habilidade, Long> {

    boolean existsByNomeIgnoreCase(String nome);

    boolean existsByNomeIgnoreCaseAndIdNot(String nome, Long id);

    List<Habilidade> findByNivel(int nivel);
}
