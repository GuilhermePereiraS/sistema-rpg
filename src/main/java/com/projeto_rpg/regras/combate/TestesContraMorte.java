package com.projeto_rpg.regras.combate;

/**
 * Regra de morte com testes contra a morte e morte instantânea (estilo D&D 5e).
 *
 * @param falhasParaMorrer quantidade de falhas que mata o personagem
 */
public record TestesContraMorte(int falhasParaMorrer) implements RegraDeMorte {

    public static TestesContraMorte dnd5e() {
        return new TestesContraMorte(3);
    }

    @Override
    public void aoZerarPv(EstadoDeCombate estado, int danoExcedente) {
        if (danoExcedente >= estado.getPvMaximo()) {
            estado.morrer();
        } else {
            estado.ficarInconsciente();
        }
    }

    @Override
    public void aoSofrerDanoSemPv(EstadoDeCombate estado, int dano, boolean critico) {
        if (dano >= estado.getPvMaximo()) {
            estado.morrer();
            return;
        }
        estado.ficarInconsciente();
        estado.registrarFalhas(critico ? 2 : 1);
        if (estado.getFalhas() >= falhasParaMorrer) {
            estado.morrer();
        }
    }
}
