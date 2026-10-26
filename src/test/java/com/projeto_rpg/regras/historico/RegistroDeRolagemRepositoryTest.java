package com.projeto_rpg.regras.historico;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.boot.jpa.test.autoconfigure.TestEntityManager;

import java.time.Instant;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

@DataJpaTest
class RegistroDeRolagemRepositoryTest {

    private static final Instant INICIO = Instant.parse("2026-01-01T10:00:00Z");

    @Autowired
    private RegistroDeRolagemRepository repository;

    @Autowired
    private TestEntityManager entityManager;

    @BeforeEach
    void popularHistorico() {
        salvar(1L, TipoRolagem.ATAQUE, "1d20+5", List.of(14), 19, 0);
        salvar(1L, TipoRolagem.DANO, "1d8+3", List.of(6), 9, 1);
        salvar(2L, TipoRolagem.TESTE, "1d20", List.of(3), 3, 2);
        salvar(1L, TipoRolagem.ATAQUE, "1d20+5", List.of(2), 7, 3);
        entityManager.flush();
        entityManager.clear();
    }

    private void salvar(Long personagemId, TipoRolagem tipo, String descricao, List<Integer> dados, int total,
                        int minutos) {
        repository.save(new RegistroDeRolagem(personagemId, tipo, descricao, dados, total,
                INICIO.plusSeconds(minutos * 60L)));
    }

    @Test
    @DisplayName("deve listar o histórico do personagem da rolagem mais recente para a mais antiga")
    void deveListarHistoricoDoMaisRecenteParaOMaisAntigo() {
        List<RegistroDeRolagem> historico = repository.findByPersonagemIdOrderByDataHoraDescIdDesc(1L);

        assertEquals(List.of(7, 9, 19), historico.stream().map(RegistroDeRolagem::getTotal).toList());
    }

    @Test
    @DisplayName("deve filtrar o histórico do personagem pelo tipo de rolagem")
    void deveFiltrarHistoricoPorTipo() {
        List<RegistroDeRolagem> ataques =
                repository.findByPersonagemIdAndTipoOrderByDataHoraDescIdDesc(1L, TipoRolagem.ATAQUE);

        assertEquals(List.of(7, 19), ataques.stream().map(RegistroDeRolagem::getTotal).toList());
    }

    @Test
    @DisplayName("deve persistir os dados individuais na ordem em que foram rolados")
    void devePersistirDadosNaOrdem() {
        repository.save(new RegistroDeRolagem(3L, TipoRolagem.LIVRE, "4d6kh3", List.of(1, 6, 3, 5), 14, INICIO));
        entityManager.flush();
        entityManager.clear();

        RegistroDeRolagem registro = repository.findByPersonagemIdOrderByDataHoraDescIdDesc(3L).getFirst();

        assertEquals(List.of(1, 6, 3, 5), registro.getDados());
    }
}
