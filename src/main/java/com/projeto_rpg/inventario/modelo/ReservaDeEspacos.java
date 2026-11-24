package com.projeto_rpg.inventario.modelo;

import jakarta.persistence.CollectionTable;
import jakarta.persistence.Column;
import jakarta.persistence.ElementCollection;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OrderBy;
import jakarta.persistence.Table;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * Reserva de espaços de habilidade de um personagem (níveis 1 a 9).
 *
 * <p>Os espaços são gastos ao usar uma habilidade e todos voltam após um descanso longo.
 * O personagem é referenciado apenas pelo id; o módulo não depende de nenhum outro.
 */
@Entity
@Table(name = "reserva_de_espacos")
public class ReservaDeEspacos {

    public static final int NIVEL_MINIMO = 1;
    public static final int NIVEL_MAXIMO = 9;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private Long personagemId;

    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(name = "reserva_espacos_por_nivel", joinColumns = @JoinColumn(name = "reserva_id"))
    @OrderBy("nivel ASC")
    private List<EspacosDeNivel> espacos = new ArrayList<>();

    protected ReservaDeEspacos() {
        // exigido pelo JPA
    }

    /**
     * Cria a reserva informando o máximo de espaços de cada nível, do 1 ao 9.
     */
    public ReservaDeEspacos(Long personagemId, List<Integer> maximos) {
        if (personagemId == null) {
            throw new IllegalArgumentException("Personagem não pode ser nulo");
        }
        if (maximos == null || maximos.size() != NIVEL_MAXIMO) {
            throw new IllegalArgumentException("É preciso informar o máximo dos " + NIVEL_MAXIMO + " níveis (1 a 9)");
        }
        this.personagemId = personagemId;
        for (int nivel = NIVEL_MINIMO; nivel <= NIVEL_MAXIMO; nivel++) {
            espacos.add(new EspacosDeNivel(nivel, maximos.get(nivel - 1)));
        }
    }

    /**
     * Cria a reserva já com a progressão padrão de um conjurador: 2 espaços por nível.
     */
    public static ReservaDeEspacos comProgressaoPadrao(Long personagemId) {
        return new ReservaDeEspacos(personagemId, List.of(2, 2, 2, 2, 2, 2, 2, 2, 2));
    }

    public int maximo(int nivel) {
        return espaco(nivel).getMaximo();
    }

    public int gastos(int nivel) {
        return espaco(nivel).getGastos();
    }

    public int disponiveis(int nivel) {
        return espaco(nivel).disponiveis();
    }

    public boolean temEspaco(int nivel) {
        return disponiveis(nivel) > 0;
    }

    /**
     * Gasta um espaço do nível informado.
     *
     * @throws com.projeto_rpg.inventario.excecao.EspacoDeHabilidadeInsuficienteException se não houver
     *         espaço disponível
     */
    public void gastar(int nivel) {
        espaco(nivel).gastar();
    }

    public void recuperar(int nivel, int quantidade) {
        espaco(nivel).recuperar(quantidade);
    }

    /**
     * Descanso longo: recupera todos os espaços gastos.
     */
    public void descansoLongo() {
        espacos.forEach(EspacosDeNivel::recuperarTudo);
    }

    /**
     * Ajusta o máximo de espaços de um nível (por exemplo, ao subir de nível).
     */
    public void ajustarMaximo(int nivel, int maximo) {
        espaco(nivel).ajustarMaximo(maximo);
    }

    private EspacosDeNivel espaco(int nivel) {
        if (nivel < NIVEL_MINIMO || nivel > NIVEL_MAXIMO) {
            throw new IllegalArgumentException("Nível de espaço deve estar entre " + NIVEL_MINIMO
                    + " e " + NIVEL_MAXIMO + ", mas foi " + nivel);
        }
        return espacos.stream()
                .filter(espaco -> espaco.getNivel() == nivel)
                .findFirst()
                .orElseThrow(() -> new IllegalStateException("Espaços do nível " + nivel + " não configurados"));
    }

    public Long getId() {
        return id;
    }

    public Long getPersonagemId() {
        return personagemId;
    }

    public List<EspacosDeNivel> getEspacos() {
        return List.copyOf(espacos);
    }

    @Override
    public boolean equals(Object outro) {
        if (this == outro) {
            return true;
        }
        if (!(outro instanceof ReservaDeEspacos reserva)) {
            return false;
        }
        return id != null && id.equals(reserva.id);
    }

    @Override
    public int hashCode() {
        return id == null ? System.identityHashCode(this) : Objects.hash(id);
    }
}
