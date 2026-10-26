package com.projeto_rpg.regras.historico;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface RegistroDeRolagemRepository extends JpaRepository<RegistroDeRolagem, Long> {

    List<RegistroDeRolagem> findByPersonagemIdOrderByDataHoraDescIdDesc(Long personagemId);

    List<RegistroDeRolagem> findByPersonagemIdAndTipoOrderByDataHoraDescIdDesc(Long personagemId, TipoRolagem tipo);
}
