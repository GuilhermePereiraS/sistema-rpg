package com.projeto_rpg.ficha.servico;

import com.projeto_rpg.ficha.excecao.ClasseEmUsoException;
import com.projeto_rpg.ficha.excecao.ClasseNaoEncontradaException;
import com.projeto_rpg.ficha.excecao.NomeJaCadastradoException;
import com.projeto_rpg.ficha.modelo.Classe;
import com.projeto_rpg.ficha.modelo.ClasseRepository;
import com.projeto_rpg.ficha.modelo.PersonagemRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * CRUD de classes de personagem.
 */
@Service
@Transactional
public class ServicoDeClasse {

    private final ClasseRepository repository;
    private final PersonagemRepository personagens;

    public ServicoDeClasse(ClasseRepository repository, PersonagemRepository personagens) {
        this.repository = repository;
        this.personagens = personagens;
    }

    public Classe criar(String nome, int dadoDeVida) {
        Classe classe = new Classe(nome, dadoDeVida);
        if (repository.existsByNomeIgnoreCase(classe.getNome())) {
            throw new NomeJaCadastradoException("Classe", classe.getNome());
        }
        return repository.save(classe);
    }

    @Transactional(readOnly = true)
    public Classe buscar(Long id) {
        return repository.findById(id).orElseThrow(() -> new ClasseNaoEncontradaException(id));
    }

    @Transactional(readOnly = true)
    public List<Classe> listar() {
        return repository.findAll();
    }

    public Classe atualizar(Long id, String nome, int dadoDeVida) {
        Classe classe = buscar(id);
        // a checagem vem antes de alterar a entidade: com a mudança pendente, o Hibernate
        // faria flush antes da consulta e a violação do índice único mascararia esta exceção
        if (nome != null && repository.existsByNomeIgnoreCaseAndIdNot(nome.trim(), id)) {
            throw new NomeJaCadastradoException("Classe", nome.trim());
        }
        classe.atualizar(nome, dadoDeVida);
        return repository.save(classe);
    }

    public void remover(Long id) {
        Classe classe = buscar(id);
        if (personagens.existsByClasseId(id)) {
            throw new ClasseEmUsoException(id);
        }
        repository.delete(classe);
    }
}
