package com.projeto_rpg.regras.historico;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullAndEmptySource;
import org.junit.jupiter.params.provider.ValueSource;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class RegistroDeRolagemTest {

    private static final Instant AGORA = Instant.parse("2026-01-01T10:00:00Z");

    @Test
    @DisplayName("deve criar o registro com todos os dados da rolagem")
    void deveCriarRegistroQuandoDadosForemValidos() {
        RegistroDeRolagem registro = new RegistroDeRolagem(7L, TipoRolagem.DANO, "2d6+3", List.of(4, 5), 12, AGORA);

        assertAll(
                () -> assertEquals(7L, registro.getPersonagemId()),
                () -> assertEquals(TipoRolagem.DANO, registro.getTipo()),
                () -> assertEquals("2d6+3", registro.getDescricao()),
                () -> assertEquals(List.of(4, 5), registro.getDados()),
                () -> assertEquals(12, registro.getTotal()),
                () -> assertEquals(AGORA, registro.getDataHora()));
    }

    @Test
    @DisplayName("deve proteger a lista de dados contra alterações externas")
    void deveProtegerListaDeDadosQuandoOriginalForAlterada() {
        List<Integer> dados = new ArrayList<>(List.of(4, 5));
        RegistroDeRolagem registro = new RegistroDeRolagem(7L, TipoRolagem.LIVRE, "2d6", dados, 9, AGORA);

        dados.add(6);

        assertEquals(List.of(4, 5), registro.getDados());
        assertThrows(UnsupportedOperationException.class, () -> registro.getDados().add(1));
    }

    @Test
    @DisplayName("deve lançar exceção quando o personagem for nulo")
    void deveLancarExcecaoQuandoPersonagemForNulo() {
        assertThrows(IllegalArgumentException.class,
                () -> new RegistroDeRolagem(null, TipoRolagem.LIVRE, "1d20", List.of(10), 10, AGORA));
    }

    @Test
    @DisplayName("deve lançar exceção quando o tipo for nulo")
    void deveLancarExcecaoQuandoTipoForNulo() {
        assertThrows(IllegalArgumentException.class,
                () -> new RegistroDeRolagem(7L, null, "1d20", List.of(10), 10, AGORA));
    }

    @ParameterizedTest
    @NullAndEmptySource
    @ValueSource(strings = {"  "})
    @DisplayName("deve lançar exceção quando a descrição for vazia")
    void deveLancarExcecaoQuandoDescricaoForVazia(String descricao) {
        assertThrows(IllegalArgumentException.class,
                () -> new RegistroDeRolagem(7L, TipoRolagem.LIVRE, descricao, List.of(10), 10, AGORA));
    }

    @Test
    @DisplayName("deve lançar exceção quando os dados ou a data forem nulos")
    void deveLancarExcecaoQuandoDadosOuDataForemNulos() {
        assertThrows(IllegalArgumentException.class,
                () -> new RegistroDeRolagem(7L, TipoRolagem.LIVRE, "1d20", null, 10, AGORA));
        assertThrows(IllegalArgumentException.class,
                () -> new RegistroDeRolagem(7L, TipoRolagem.LIVRE, "1d20", List.of(10), 10, null));
    }
}
