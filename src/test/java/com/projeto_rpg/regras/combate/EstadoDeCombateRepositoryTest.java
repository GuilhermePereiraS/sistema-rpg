package com.projeto_rpg.regras.combate;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.boot.jpa.test.autoconfigure.TestEntityManager;
import org.springframework.dao.DataIntegrityViolationException;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

@DataJpaTest
class EstadoDeCombateRepositoryTest {

    @Autowired
    private EstadoDeCombateRepository repository;

    @Autowired
    private TestEntityManager entityManager;

    @Test
    @DisplayName("deve persistir PV, condição e falhas e recuperar pelo id do personagem")
    void devePersistirEstadoQuandoSalvar() {
        EstadoDeCombate estado = new EstadoDeCombate(1L, 20);
        estado.sofrerDano(20, false, TestesContraMorte.dnd5e());
        estado.sofrerDano(1, true, TestesContraMorte.dnd5e());
        repository.save(estado);
        entityManager.flush();
        entityManager.clear();

        EstadoDeCombate recuperado = repository.findByPersonagemId(1L).orElseThrow();

        assertAll(
                () -> assertEquals(20, recuperado.getPvMaximo()),
                () -> assertEquals(0, recuperado.getPvAtual()),
                () -> assertEquals(Condicao.INCONSCIENTE, recuperado.getCondicao()),
                () -> assertEquals(2, recuperado.getFalhas()),
                () -> assertEquals(0, recuperado.getSucessos()));
    }

    @Test
    @DisplayName("deve retornar vazio quando o personagem não tiver estado de combate")
    void deveRetornarVazioQuandoPersonagemNaoTiverEstado() {
        Optional<EstadoDeCombate> estado = repository.findByPersonagemId(99L);

        assertTrue(estado.isEmpty());
    }

    @Test
    @DisplayName("deve informar se existe estado de combate para o personagem")
    void deveInformarExistenciaQuandoConsultarPorPersonagem() {
        repository.save(new EstadoDeCombate(1L, 10));

        assertTrue(repository.existsByPersonagemId(1L));
        assertFalse(repository.existsByPersonagemId(2L));
    }

    @Test
    @DisplayName("deve impedir dois estados de combate para o mesmo personagem")
    void deveImpedirEstadoDuplicadoQuandoPersonagemForOMesmo() {
        repository.saveAndFlush(new EstadoDeCombate(1L, 10));

        assertThrows(DataIntegrityViolationException.class,
                () -> repository.saveAndFlush(new EstadoDeCombate(1L, 12)));
    }
}
