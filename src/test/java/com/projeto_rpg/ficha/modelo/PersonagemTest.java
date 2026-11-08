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

    @Test
    @DisplayName("deve ter no nível 1 o dado de vida máximo mais o modificador de Constituição")
    void deveCalcularPvMaximoDoNivelUmQuandoPersonagemForCriado() {
        Personagem personagem = new Personagem("Aragorn", guerreiro, 7L, atributos);

        assertEquals(11, personagem.getPvMaximo());
    }

    @Test
    @DisplayName("deve somar a média do dado de vida mais Constituição a cada nível acima do 1")
    void deveSomarMediaDoDadoQuandoSubirDeNivel() {
        Personagem personagem = new Personagem("Aragorn", guerreiro, 7L, atributos);

        personagem.ganharXp(300);
        assertEquals(18, personagem.getPvMaximo());

        personagem.ganharXp(6200);
        assertEquals(39, personagem.getPvMaximo());
    }

    @Test
    @DisplayName("deve ganhar no mínimo 1 PV por nível mesmo com Constituição muito baixa")
    void deveGarantirUmPvPorNivelQuandoConstituicaoForMuitoBaixa() {
        Classe mago = new Classe("Mago", 6);
        Personagem personagem = new Personagem("Frágil", mago, 7L, new Atributos(10, 10, 1, 10, 10, 10));

        assertEquals(1, personagem.getPvMaximo());

        personagem.ganharXp(300);
        assertEquals(2, personagem.getPvMaximo());
    }

    @Test
    @DisplayName("deve usar o dado de vida da classe no cálculo de PV")
    void deveUsarDadoDeVidaDaClasseQuandoCalcularPv() {
        Classe mago = new Classe("Mago", 6);
        Personagem personagem = new Personagem("Gandalf", mago, 7L, atributos);

        assertEquals(7, personagem.getPvMaximo());
    }

    @Test
    @DisplayName("deve permanecer no nível 20 e sem estouro quando o XP passar do limite do inteiro")
    void deveManterNivelVinteQuandoXpExceder() {
        Personagem personagem = new Personagem("Aragorn", guerreiro, 7L, atributos);

        personagem.ganharXp(Integer.MAX_VALUE);
        personagem.ganharXp(10);

        assertAll(
                () -> assertEquals(Integer.MAX_VALUE, personagem.getXp()),
                () -> assertEquals(20, personagem.getNivel()));
    }
}
