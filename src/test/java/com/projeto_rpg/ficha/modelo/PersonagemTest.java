package com.projeto_rpg.ficha.modelo;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class PersonagemTest {

    private Classe guerreiro;
    private Atributos atributos;

    @BeforeEach
    void prepararDados() {
        guerreiro = new Classe("Guerreiro", 10);
        atributos = new Atributos(15, 14, 13, 12, 10, 8);
    }

    @Test
    @DisplayName("deve criar personagem no nível 1 com 0 XP")
    void deveCriarPersonagemNoNivelUmQuandoDadosForemValidos() {
        Personagem personagem = new Personagem("Aragorn", guerreiro, 7L, atributos);

        assertAll(
                () -> assertEquals("Aragorn", personagem.getNome()),
                () -> assertEquals(guerreiro, personagem.getClasse()),
                () -> assertEquals(7L, personagem.getRacaId()),
                () -> assertEquals(atributos, personagem.getAtributos()),
                () -> assertEquals(0, personagem.getXp()),
                () -> assertEquals(1, personagem.getNivel()));
    }

    @Test
    @DisplayName("deve lançar exceção quando o nome for vazio ou nulo")
    void deveLancarExcecaoQuandoNomeDoPersonagemForVazio() {
        assertThrows(IllegalArgumentException.class, () -> new Personagem(" ", guerreiro, 7L, atributos));
        assertThrows(IllegalArgumentException.class, () -> new Personagem(null, guerreiro, 7L, atributos));
    }

    @Test
    @DisplayName("deve lançar exceção quando classe, raça ou atributos forem nulos")
    void deveLancarExcecaoQuandoDadosObrigatoriosForemNulos() {
        assertAll(
                () -> assertThrows(IllegalArgumentException.class,
                        () -> new Personagem("Aragorn", null, 7L, atributos)),
                () -> assertThrows(IllegalArgumentException.class,
                        () -> new Personagem("Aragorn", guerreiro, null, atributos)),
                () -> assertThrows(IllegalArgumentException.class,
                        () -> new Personagem("Aragorn", guerreiro, 7L, null)));
    }

    @Test
    @DisplayName("deve expor o modificador de cada atributo do personagem")
    void deveExporModificadorQuandoConsultarAtributoDoPersonagem() {
        Personagem personagem = new Personagem("Aragorn", guerreiro, 7L, atributos);

        assertAll(
                () -> assertEquals(2, personagem.modificador(Atributo.FORCA)),
                () -> assertEquals(2, personagem.modificador(Atributo.DESTREZA)),
                () -> assertEquals(-1, personagem.modificador(Atributo.CARISMA)));
    }

    @Test
    @DisplayName("deve acumular XP a cada ganho")
    void deveAcumularXpQuandoGanharXpVariasVezes() {
        Personagem personagem = new Personagem("Aragorn", guerreiro, 7L, atributos);

        personagem.ganharXp(100);
        personagem.ganharXp(150);

        assertEquals(250, personagem.getXp());
    }

    @Test
    @DisplayName("deve subir de nível quando o XP atingir o mínimo do próximo nível")
    void deveSubirDeNivelQuandoXpAtingirOMinimo() {
        Personagem personagem = new Personagem("Aragorn", guerreiro, 7L, atributos);

        personagem.ganharXp(299);
        assertEquals(1, personagem.getNivel());

        personagem.ganharXp(1);
        assertEquals(2, personagem.getNivel());
    }

    @Test
    @DisplayName("deve poder subir vários níveis de uma vez")
    void deveSubirVariosNiveisQuandoGanharMuitoXp() {
        Personagem personagem = new Personagem("Aragorn", guerreiro, 7L, atributos);

        personagem.ganharXp(6500);

        assertEquals(5, personagem.getNivel());
    }

    @Test
    @DisplayName("deve lançar exceção quando o XP ganho não for positivo")
    void deveLancarExcecaoQuandoXpGanhoNaoForPositivo() {
        Personagem personagem = new Personagem("Aragorn", guerreiro, 7L, atributos);

        assertThrows(IllegalArgumentException.class, () -> personagem.ganharXp(0));
        assertThrows(IllegalArgumentException.class, () -> personagem.ganharXp(-10));
        assertEquals(0, personagem.getXp());
    }

    @Test
    @DisplayName("deve ter bônus de proficiência +2 no nível 1")
    void deveTerBonusDeProficienciaDoisQuandoForNivelUm() {
        Personagem personagem = new Personagem("Aragorn", guerreiro, 7L, atributos);

        assertEquals(2, personagem.getBonusDeProficiencia());
    }

    @Test
    @DisplayName("deve aumentar o bônus de proficiência ao subir de nível")
    void deveAumentarBonusDeProficienciaQuandoSubirDeFaixa() {
        Personagem personagem = new Personagem("Aragorn", guerreiro, 7L, atributos);

        personagem.ganharXp(6500);

        assertEquals(3, personagem.getBonusDeProficiencia());
    }
}
