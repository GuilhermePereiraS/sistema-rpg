package com.projeto_rpg.ficha.servico;

import com.projeto_rpg.ficha.excecao.PersonagemNaoEncontradoException;
import com.projeto_rpg.ficha.modelo.Atributos;
import com.projeto_rpg.ficha.modelo.Classe;
import com.projeto_rpg.ficha.modelo.Personagem;
import com.projeto_rpg.ficha.modelo.PersonagemRepository;
import com.projeto_rpg.ficha.regra.CompraDePontos;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * Casos de uso da ficha: criação por compra de pontos, consulta e ganho de XP.
 *
 * <p>Os outros módulos consomem a ficha por aqui: o PV máximo alimenta o estado de combate,
 * e a Força e o modificador de Destreza alimentam o inventário.
 */
@Service
@Transactional
public class ServicoDePersonagem {

    private final PersonagemRepository repository;
    private final ServicoDeClasse servicoDeClasse;
    private final ServicoDeRaca servicoDeRaca;

    public ServicoDePersonagem(PersonagemRepository repository, ServicoDeClasse servicoDeClasse,
                               ServicoDeRaca servicoDeRaca) {
        this.repository = repository;
        this.servicoDeClasse = servicoDeClasse;
        this.servicoDeRaca = servicoDeRaca;
    }

    /**
     * Cria um personagem de nível 1; os atributos precisam respeitar a compra de pontos.
     */
    public Personagem criar(String nome, Long classeId, Long racaId, Atributos atributos) {
        CompraDePontos.validar(atributos);
        Classe classe = servicoDeClasse.buscar(classeId);
        servicoDeRaca.buscar(racaId);
        return repository.save(new Personagem(nome, classe, racaId, atributos));
    }

    @Transactional(readOnly = true)
    public Personagem buscar(Long id) {
        return repository.findById(id).orElseThrow(() -> new PersonagemNaoEncontradoException(id));
    }

    @Transactional(readOnly = true)
    public List<Personagem> listar() {
        return repository.findAll();
    }

    public Personagem ganharXp(Long id, int xp) {
        Personagem personagem = buscar(id);
        personagem.ganharXp(xp);
        return repository.save(personagem);
    }
}
