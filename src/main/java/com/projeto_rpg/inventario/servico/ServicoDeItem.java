package com.projeto_rpg.inventario.servico;

import com.projeto_rpg.inventario.excecao.ItemEmUsoException;
import com.projeto_rpg.inventario.excecao.ItemJaCadastradoException;
import com.projeto_rpg.inventario.excecao.ItemNaoEncontradoException;
import com.projeto_rpg.inventario.modelo.Inventario;
import com.projeto_rpg.inventario.modelo.InventarioRepository;
import com.projeto_rpg.inventario.modelo.Item;
import com.projeto_rpg.inventario.modelo.ItemRepository;
import com.projeto_rpg.inventario.modelo.TipoDeArmadura;
import com.projeto_rpg.inventario.modelo.TipoDeItem;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * CRUD do catálogo de itens.
 */
@Service
@Transactional
public class ServicoDeItem {

    private final ItemRepository repository;
    private final InventarioRepository inventarios;

    public ServicoDeItem(ItemRepository repository, InventarioRepository inventarios) {
        this.repository = repository;
        this.inventarios = inventarios;
    }

    public Item criarArma(String nome, double peso) {
        return salvar(Item.arma(nome, peso));
    }

    public Item criarArmadura(String nome, double peso, int caBase, TipoDeArmadura tipoDeArmadura) {
        return salvar(Item.armadura(nome, peso, caBase, tipoDeArmadura));
    }

    public Item criarEscudo(String nome, double peso) {
        return salvar(Item.escudo(nome, peso));
    }

    public Item criarConsumivel(String nome, double peso) {
        return salvar(Item.consumivel(nome, peso));
    }

    public Item criarGeral(String nome, double peso) {
        return salvar(Item.geral(nome, peso));
    }

    @Transactional(readOnly = true)
    public Item buscar(Long id) {
        return repository.findById(id).orElseThrow(() -> new ItemNaoEncontradoException(id));
    }

    @Transactional(readOnly = true)
    public List<Item> listar() {
        return repository.findAll();
    }

    @Transactional(readOnly = true)
    public List<Item> listarPorTipo(TipoDeItem tipo) {
        return repository.findByTipo(tipo);
    }

    public Item atualizar(Long id, String nome, TipoDeItem tipo, double peso, int caBase,
                          TipoDeArmadura tipoDeArmadura) {
        Item item = buscar(id);
        // a checagem vem antes de alterar a entidade: com a mudança pendente, o Hibernate
        // faria flush antes da consulta e a violação do índice único mascararia esta exceção
        if (nome != null && repository.existsByNomeIgnoreCaseAndIdNot(nome.trim(), id)) {
            throw new ItemJaCadastradoException(nome.trim());
        }
        TipoDeItem tipoAnterior = item.getTipo();
        item.atualizar(nome, tipo, peso, caBase, tipoDeArmadura);
        if (tipoAnterior != item.getTipo()) {
            // um item equipado que mudou de tipo não cabe mais no espaço onde estava: sem isso
            // o inventário ficaria inconsistente e o cálculo da CA passaria a falhar
            inventarios.findAll().forEach(Inventario::revalidarEquipamento);
        }
        return repository.save(item);
    }

    public void remover(Long id) {
        Item item = buscar(id);
        if (inventarios.existsByItensId(id)) {
            throw new ItemEmUsoException(id);
        }
        repository.delete(item);
    }

    private Item salvar(Item item) {
        if (repository.existsByNomeIgnoreCase(item.getNome())) {
            throw new ItemJaCadastradoException(item.getNome());
        }
        return repository.save(item);
    }
}
