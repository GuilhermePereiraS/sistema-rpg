package com.projeto_rpg.regras.combate;

import com.projeto_rpg.regras.excecao.PersonagemMortoException;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

/**
 * Estado de combate de um personagem: PV, condição e testes contra a morte.
 *
 * <p>O personagem é referenciado apenas pelo id (a ficha pertence a outro módulo).
 * As mudanças de estado acontecem só pelos métodos de domínio. O que ocorre ao chegar
 * a 0 PV fica a cargo da {@link RegraDeMorte} do sistema em uso.
 */
@Entity
@Table(name = "estado_de_combate")
public class EstadoDeCombate {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private Long personagemId;

    @Column(nullable = false)
    private int pvMaximo;

    @Column(nullable = false)
    private int pvAtual;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Condicao condicao;

    @Column(nullable = false)
    private int sucessos;

    @Column(nullable = false)
    private int falhas;

    protected EstadoDeCombate() {
        // exigido pelo JPA
    }

    public EstadoDeCombate(Long personagemId, int pvMaximo) {
        if (personagemId == null) {
            throw new IllegalArgumentException("Personagem não pode ser nulo");
        }
        if (pvMaximo <= 0) {
            throw new IllegalArgumentException("PV máximo deve ser positivo");
        }
        this.personagemId = personagemId;
        this.pvMaximo = pvMaximo;
        this.pvAtual = pvMaximo;
        this.condicao = Condicao.CONSCIENTE;
    }

    public void sofrerDano(int dano, boolean critico, RegraDeMorte regra) {
        if (dano < 0) {
            throw new IllegalArgumentException("Dano não pode ser negativo");
        }
        garantirVivo();
        if (dano == 0) {
            return;
        }
        if (pvAtual == 0) {
            regra.aoSofrerDanoSemPv(this, dano, critico);
            return;
        }
        int excedente = dano - pvAtual;
        pvAtual = Math.max(0, pvAtual - dano);
        if (pvAtual == 0) {
            regra.aoZerarPv(this, excedente);
        }
    }

    public void receberCura(int cura) {
        if (cura <= 0) {
            throw new IllegalArgumentException("Cura deve ser positiva");
        }
        garantirVivo();
        pvAtual = Math.min(pvMaximo, pvAtual + cura);
        recuperarConsciencia();
    }

    public void recuperarTotalmente() {
        garantirVivo();
        pvAtual = pvMaximo;
        recuperarConsciencia();
    }

    void ficarInconsciente() {
        condicao = Condicao.INCONSCIENTE;
    }

    void morrer() {
        pvAtual = 0;
        condicao = Condicao.MORTO;
    }

    void registrarFalhas(int quantidade) {
        falhas += quantidade;
    }

    private void recuperarConsciencia() {
        condicao = Condicao.CONSCIENTE;
        sucessos = 0;
        falhas = 0;
    }

    private void garantirVivo() {
        if (condicao == Condicao.MORTO) {
            throw new PersonagemMortoException(personagemId);
        }
    }

    public Long getId() {
        return id;
    }

    public Long getPersonagemId() {
        return personagemId;
    }

    public int getPvMaximo() {
        return pvMaximo;
    }

    public int getPvAtual() {
        return pvAtual;
    }

    public Condicao getCondicao() {
        return condicao;
    }

    public int getSucessos() {
        return sucessos;
    }

    public int getFalhas() {
        return falhas;
    }
}
