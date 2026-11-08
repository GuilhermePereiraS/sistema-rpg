package com.projeto_rpg.ficha.modelo;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.boot.jpa.test.autoconfigure.TestEntityManager;
import org.springframework.dao.DataIntegrityViolationException;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

@DataJpaTest
class PersonagemRepositoryTest {

    @Autowired
    private PersonagemRepository repository;

    @Autowired
    private ClasseRepository classes;

    @Autowired
    private TestEntityManager entityManager;

    @Test
    @DisplayName("deve persistir atributos, XP e referências e recuperá-los")
    void devePersistirFichaQuandoSalvar() {
        Classe guerreiro = classes.save(new Classe("Guerreiro", 10));
        Personagem personagem = new Personagem("Aragorn", guerreiro, 7L, new Atributos(15, 14, 13, 12, 10, 8));
        personagem.ganharXp(900);
        Long id = repository.save(personagem).getId();
        entityManager.flush();
        entityManager.clear();

        Personagem recuperado = repository.findById(id).orElseThrow();

        assertAll(
                () -> assertEquals("Aragorn", recuperado.getNome()),
                () -> assertEquals("Guerreiro", recuperado.getClasse().getNome()),
                () -> assertEquals(7L, recuperado.getRacaId()),
                () -> assertEquals(new Atributos(15, 14, 13, 12, 10, 8), recuperado.getAtributos()),
                () -> assertEquals(900, recuperado.getXp()),
                () -> assertEquals(3, recuperado.getNivel()));
    }

    @Test
    @DisplayName("deve informar se existem personagens usando a classe")
    void deveInformarUsoDaClasseQuandoConsultar() {
        Classe guerreiro = classes.save(new Classe("Guerreiro", 10));
        Classe mago = classes.save(new Classe("Mago", 6));
        repository.save(new Personagem("Aragorn", guerreiro, 1L, new Atributos(10, 10, 10, 10, 10, 10)));

        assertTrue(repository.existsByClasseId(guerreiro.getId()));
        assertFalse(repository.existsByClasseId(mago.getId()));
    }

    @Test
    @DisplayName("deve informar se existem personagens usando a raça")
    void deveInformarUsoDaRacaQuandoConsultar() {
        Classe guerreiro = classes.save(new Classe("Guerreiro", 10));
        repository.save(new Personagem("Aragorn", guerreiro, 1L, new Atributos(10, 10, 10, 10, 10, 10)));

        assertTrue(repository.existsByRacaId(1L));
        assertFalse(repository.existsByRacaId(2L));
    }

    @Test
    @DisplayName("deve impedir duas classes com o mesmo nome")
    void deveImpedirClasseDuplicadaQuandoNomeForOMesmo() {
        classes.saveAndFlush(new Classe("Guerreiro", 10));

        assertThrows(DataIntegrityViolationException.class,
                () -> classes.saveAndFlush(new Classe("Guerreiro", 8)));
    }
}
