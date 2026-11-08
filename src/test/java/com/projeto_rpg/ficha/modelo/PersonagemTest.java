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
}
