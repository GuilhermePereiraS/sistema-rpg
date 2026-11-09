package com.projeto_rpg.ficha.regra;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class TabelaDeNivelTest {

    @Test
    @DisplayName("deve começar no nível 1 com 0 XP")
    void deveRetornarNivelUmQuandoXpForZero() {
        assertEquals(1, TabelaDeNivel.nivelPara(0));
    }

    @Test
    @DisplayName("deve subir de nível exatamente ao atingir o XP mínimo do nível")
    void deveSubirDeNivelQuandoAtingirXpMinimo() {
        assertAll(
                () -> assertEquals(1, TabelaDeNivel.nivelPara(299)),
                () -> assertEquals(2, TabelaDeNivel.nivelPara(300)),
                () -> assertEquals(2, TabelaDeNivel.nivelPara(899)),
                () -> assertEquals(3, TabelaDeNivel.nivelPara(900)),
                () -> assertEquals(4, TabelaDeNivel.nivelPara(6499)),
                () -> assertEquals(5, TabelaDeNivel.nivelPara(6500)),
                () -> assertEquals(19, TabelaDeNivel.nivelPara(354999)),
                () -> assertEquals(20, TabelaDeNivel.nivelPara(355000)));
    }

    @Test
    @DisplayName("deve limitar o nível em 20 mesmo com XP acima do necessário")
    void deveLimitarNivelQuandoXpUltrapassarOMaximo() {
        assertEquals(20, TabelaDeNivel.nivelPara(1_000_000));
    }

    @Test
    @DisplayName("deve lançar exceção quando o XP for negativo")
    void deveLancarExcecaoQuandoXpForNegativo() {
        assertThrows(IllegalArgumentException.class, () -> TabelaDeNivel.nivelPara(-1));
    }

    @Test
    @DisplayName("deve informar o XP mínimo de cada nível")
    void deveInformarXpMinimoQuandoConsultarNivel() {
        assertAll(
                () -> assertEquals(0, TabelaDeNivel.xpMinimoDoNivel(1)),
                () -> assertEquals(300, TabelaDeNivel.xpMinimoDoNivel(2)),
                () -> assertEquals(355000, TabelaDeNivel.xpMinimoDoNivel(20)));
    }

    @Test
    @DisplayName("deve lançar exceção ao consultar nível fora de 1 a 20")
    void deveLancarExcecaoQuandoNivelForInvalido() {
        assertThrows(IllegalArgumentException.class, () -> TabelaDeNivel.xpMinimoDoNivel(0));
        assertThrows(IllegalArgumentException.class, () -> TabelaDeNivel.xpMinimoDoNivel(21));
    }

    @Test
    @DisplayName("deve dar bônus de proficiência +2 nos níveis 1 a 4")
    void deveRetornarBonusDoisQuandoNivelForAteQuatro() {
        assertAll(
                () -> assertEquals(2, TabelaDeNivel.bonusDeProficiencia(1)),
                () -> assertEquals(2, TabelaDeNivel.bonusDeProficiencia(4)));
    }

    @Test
    @DisplayName("deve aumentar o bônus de proficiência a cada 4 níveis")
    void deveAumentarBonusQuandoNivelCruzarFaixa() {
        assertAll(
                () -> assertEquals(3, TabelaDeNivel.bonusDeProficiencia(5)),
                () -> assertEquals(3, TabelaDeNivel.bonusDeProficiencia(8)),
                () -> assertEquals(4, TabelaDeNivel.bonusDeProficiencia(9)),
                () -> assertEquals(4, TabelaDeNivel.bonusDeProficiencia(12)),
                () -> assertEquals(5, TabelaDeNivel.bonusDeProficiencia(13)),
                () -> assertEquals(5, TabelaDeNivel.bonusDeProficiencia(16)),
                () -> assertEquals(6, TabelaDeNivel.bonusDeProficiencia(17)),
                () -> assertEquals(6, TabelaDeNivel.bonusDeProficiencia(20)));
    }

    @Test
    @DisplayName("deve lançar exceção ao calcular bônus de proficiência de nível inválido")
    void deveLancarExcecaoQuandoNivelDoBonusForInvalido() {
        assertThrows(IllegalArgumentException.class, () -> TabelaDeNivel.bonusDeProficiencia(0));
        assertThrows(IllegalArgumentException.class, () -> TabelaDeNivel.bonusDeProficiencia(21));
    }
}
