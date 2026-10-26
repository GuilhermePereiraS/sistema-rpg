package com.projeto_rpg.regras.servico;

import com.projeto_rpg.regras.historico.RegistroDeRolagem;
import com.projeto_rpg.regras.historico.TipoRolagem;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;

import java.time.Clock;
import java.time.Instant;
import java.time.ZoneOffset;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

@DataJpaTest
@Import({ServicoDeHistorico.class, ServicoDeHistoricoTest.Relogio.class})
class ServicoDeHistoricoTest {

    private static final Instant AGORA = Instant.parse("2026-03-01T12:00:00Z");

    @TestConfiguration
    static class Relogio {
        @Bean
        Clock clock() {
            return Clock.fixed(AGORA, ZoneOffset.UTC);
        }
    }

    @Autowired
    private ServicoDeHistorico servico;

    @Test
    @DisplayName("deve registrar a rolagem com a data e hora do relógio")
    void deveRegistrarRolagemComDataHoraDoRelogio() {
        RegistroDeRolagem registro = servico.registrar(1L, TipoRolagem.LIVRE, "2d6", List.of(3, 4), 7);

        assertNotNull(registro.getId());
        assertEquals(AGORA, registro.getDataHora());
    }

    @Test
    @DisplayName("deve consultar o histórico do personagem da rolagem mais recente para a mais antiga")
    void deveConsultarHistoricoDoMaisRecenteParaOMaisAntigo() {
        servico.registrar(1L, TipoRolagem.ATAQUE, "1d20+5", List.of(10), 15);
        servico.registrar(2L, TipoRolagem.TESTE, "1d20", List.of(8), 8);
        servico.registrar(1L, TipoRolagem.DANO, "1d8+3", List.of(5), 8);

        List<RegistroDeRolagem> historico = servico.consultarPorPersonagem(1L);

        assertEquals(List.of(TipoRolagem.DANO, TipoRolagem.ATAQUE),
                historico.stream().map(RegistroDeRolagem::getTipo).toList());
    }

    @Test
    @DisplayName("deve consultar o histórico do personagem filtrado por tipo")
    void deveConsultarHistoricoFiltradoPorTipo() {
        servico.registrar(1L, TipoRolagem.ATAQUE, "1d20+5", List.of(10), 15);
        servico.registrar(1L, TipoRolagem.DANO, "1d8+3", List.of(5), 8);

        List<RegistroDeRolagem> danos = servico.consultarPorPersonagemETipo(1L, TipoRolagem.DANO);

        assertEquals(List.of(8), danos.stream().map(RegistroDeRolagem::getTotal).toList());
    }

    @Test
    @DisplayName("deve lançar exceção ao consultar sem informar o personagem ou o tipo")
    void deveLancarExcecaoQuandoParametrosDaConsultaForemNulos() {
        assertThrows(IllegalArgumentException.class, () -> servico.consultarPorPersonagem(null));
        assertThrows(IllegalArgumentException.class, () -> servico.consultarPorPersonagemETipo(1L, null));
    }
}
