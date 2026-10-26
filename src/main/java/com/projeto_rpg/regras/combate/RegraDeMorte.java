package com.projeto_rpg.regras.combate;

/**
 * Define o que acontece quando o PV chega a zero e quando um personagem com 0 PV sofre dano.
 * Cada sistema de RPG pode ter sua própria implementação.
 */
public interface RegraDeMorte {

    /**
     * Chamado quando um dano leva o PV a zero.
     *
     * @param danoExcedente quanto do dano passou do PV que o personagem tinha
     */
    void aoZerarPv(EstadoDeCombate estado, int danoExcedente);

    /**
     * Chamado quando um personagem que já está com 0 PV sofre dano.
     */
    void aoSofrerDanoSemPv(EstadoDeCombate estado, int dano, boolean critico);

    /**
     * Aplica um teste contra a morte com o valor natural rolado.
     *
     * @throws com.projeto_rpg.regras.excecao.TesteContraMorteNaoPermitidoException
     *         se a situação ou o sistema não permitirem o teste
     */
    ResultadoTesteContraMorte realizarTesteContraMorte(EstadoDeCombate estado, int dadoNatural);
}
