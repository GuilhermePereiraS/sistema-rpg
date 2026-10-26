package com.projeto_rpg.regras.combate;

import com.projeto_rpg.regras.excecao.TesteContraMorteNaoPermitidoException;

/**
 * Regra de morte com testes contra a morte e morte instantânea (estilo D&D 5e).
 *
 * @param cd                     valor mínimo no dado para o teste ser um sucesso
 * @param sucessosParaEstabilizar sucessos que estabilizam o personagem
 * @param falhasParaMorrer       falhas que matam o personagem
 * @param valorQueReanima        valor natural que devolve 1 PV e a consciência
 * @param valorDeFalhaDupla      valor natural que conta como duas falhas
 */
public record TestesContraMorte(int cd, int sucessosParaEstabilizar, int falhasParaMorrer,
                                int valorQueReanima, int valorDeFalhaDupla) implements RegraDeMorte {

    public TestesContraMorte {
        if (cd <= 0 || sucessosParaEstabilizar <= 0 || falhasParaMorrer <= 0) {
            throw new IllegalArgumentException("CD, sucessos e falhas necessários devem ser positivos");
        }
    }

    public static TestesContraMorte dnd5e() {
        return new TestesContraMorte(10, 3, 3, 20, 1);
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
        registrarFalhas(estado, critico ? 2 : 1);
    }

    @Override
    public ResultadoTesteContraMorte realizarTesteContraMorte(EstadoDeCombate estado, int dadoNatural) {
        if (estado.getCondicao() != Condicao.INCONSCIENTE) {
            throw new TesteContraMorteNaoPermitidoException(
                    "personagem está " + estado.getCondicao().name().toLowerCase());
        }
        boolean sucesso = dadoNatural >= cd;
        if (dadoNatural == valorQueReanima) {
            estado.despertarCom(1);
        } else if (dadoNatural == valorDeFalhaDupla) {
            registrarFalhas(estado, 2);
        } else if (sucesso) {
            estado.registrarSucesso();
            if (estado.getSucessos() >= sucessosParaEstabilizar) {
                estado.estabilizar();
            }
        } else {
            registrarFalhas(estado, 1);
        }
        return new ResultadoTesteContraMorte(dadoNatural, sucesso, estado.getSucessos(), estado.getFalhas(),
                estado.getCondicao());
    }

    private void registrarFalhas(EstadoDeCombate estado, int quantidade) {
        estado.registrarFalhas(quantidade);
        if (estado.getFalhas() >= falhasParaMorrer) {
            estado.morrer();
        }
    }
}
