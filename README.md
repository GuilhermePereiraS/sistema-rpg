# Sistema RPG

Back-end em Java/Spring Boot para apoiar mesas de RPG: ficha de personagem, motor de regras
(dados, testes, ataque, combate e testes contra a morte) e inventário (itens, carga, CA e
espaços de habilidade).

O projeto foi desenvolvido com **TDD (red → green → refactor)**: cada regra nasce de um teste
que falha, recebe a implementação mínima para passar e, quando preciso, é refatorada. O
histórico de commits registra cada uma dessas etapas (veja [Padrão de commits](#padrão-de-commits)).

---

## Sumário

- [Tecnologias](#tecnologias)
- [Como rodar](#como-rodar)
- [Estrutura do projeto](#estrutura-do-projeto)
- [Módulos](#módulos)
- [Arquitetura e convenções de código](#arquitetura-e-convenções-de-código)
- [Testes](#testes)
- [Padrão de commits](#padrão-de-commits)
- [Branches e pull requests](#branches-e-pull-requests)
- [Documentação complementar](#documentação-complementar)

---

## Tecnologias

| Item | Versão / uso |
|---|---|
| Java | 21 |
| Spring Boot | 4.1.1 (`starter-webmvc`, `starter-data-jpa`) |
| Banco de dados | H2 em memória (criado a cada execução) |
| Build | Maven, pelo wrapper `./mvnw` (não precisa instalar o Maven) |
| Testes | JUnit 5 (`spring-boot-starter-*-test`) |
| Cobertura | JaCoCo 0.8.14 |

## Como rodar

```bash
# compilar e rodar todos os testes (gera também o relatório de cobertura)
./mvnw test

# rodar os testes de um módulo
./mvnw test -Dtest='com.projeto_rpg.ficha.**'
./mvnw test -Dtest='com.projeto_rpg.regras.**'
./mvnw test -Dtest='com.projeto_rpg.inventario.**'

# rodar uma classe de teste específica
./mvnw test -Dtest=CompraDePontosTest

# subir a aplicação
./mvnw spring-boot:run
```

No Windows, use `mvnw.cmd` no lugar de `./mvnw`.

O relatório de cobertura do JaCoCo fica em `target/site/jacoco/index.html` depois do `test`.

### Configuração

`src/main/resources/application.properties`:

| Propriedade | Padrão | Descrição |
|---|---|---|
| `spring.application.name` | `projeto-rpg` | nome da aplicação |
| `rpg.regras.perfil` | `dnd5e` | sistema de regras usado pelo motor: `dnd5e` ou `percentual` |

---

## Estrutura do projeto

```
sistema-rpg/
├── docs/                          documentação de cada módulo
│   ├── ficha.md
│   └── inventario.md
├── src/
│   ├── main/
│   │   ├── java/com/projeto_rpg/
│   │   │   ├── ProjetoRpgApplication.java   ponto de entrada do Spring Boot
│   │   │   ├── modelo/            classes base (SistemaRpg, Regra, Categoria, Campanha, Biblioteca)
│   │   │   ├── ficha/             Módulo 1 — personagem, atributos, classe e raça
│   │   │   │   ├── modelo/        entidades JPA e repositórios
│   │   │   │   ├── regra/         regras puras (CompraDePontos, TabelaDeNivel, CalculadoraDePv)
│   │   │   │   ├── servico/       casos de uso / CRUD
│   │   │   │   └── excecao/       exceções de domínio
│   │   │   ├── regras/            Módulo 2 — motor de regras e combate
│   │   │   │   ├── dados/         notação de dados (2d6+1, 4d6kh3), parser e rolador
│   │   │   │   ├── perfil/        perfis de sistema (D&D 5e, percentual) e suas peças
│   │   │   │   ├── mecanica/      testes com vantagem/desvantagem e resolução de ataque
│   │   │   │   ├── combate/       estado de combate, PV, morte e testes contra a morte
│   │   │   │   ├── historico/     registro persistido das rolagens
│   │   │   │   ├── servico/       casos de uso (rolagem, combate, histórico)
│   │   │   │   ├── config/        montagem do motor no Spring
│   │   │   │   └── excecao/       exceções de domínio
│   │   │   └── inventario/        Módulo 3 — itens, habilidades, carga, CA e espaços
│   │   │       ├── modelo/
│   │   │       ├── regra/         regras puras (CalculadoraDeCa, CalculadoraDeCarga)
│   │   │       ├── servico/
│   │   │       └── excecao/
│   │   └── resources/
│   │       └── application.properties
│   └── test/java/com/projeto_rpg/  espelha a estrutura de main (um *Test por classe)
├── pom.xml
├── mvnw / mvnw.cmd                Maven wrapper
└── README.md
```

---

## Módulos

O sistema é dividido em três módulos, cada um sob responsabilidade de uma pessoa do grupo.
Os módulos se comunicam só por **ids e valores simples** (por exemplo, o inventário recebe o
`personagemId` e o valor de Força, e não a entidade `Personagem`), para que cada um possa ser
desenvolvido e testado de forma independente.

### 1. Ficha (`com.projeto_rpg.ficha`) — Pessoa 1

Personagem, atributos, classes e raças.

- Atributos de 1 a 30; modificador = `floor((valor - 10) / 2)`.
- Compra de pontos: 27 pontos, valores de 8 a 15 (custo 1 por ponto até 13; 14 custa 7 e 15 custa 9).
- Nível pela tabela de XP (1 a 20) e bônus de proficiência `2 + (nível - 1) / 4`.
- PV máximo por dado de vida da classe, nível e modificador de Constituição.
- CRUD de classe e raça com nome único; não remove classe/raça em uso.

Detalhes em [`docs/ficha.md`](docs/ficha.md).

### 2. Regras (`com.projeto_rpg.regras`) — Pessoa 2

Motor de regras **agnóstico de sistema**: as mecânicas são montadas a partir de um
`PerfilDeRegras`, e trocar de sistema é só trocar o perfil.

- **Dados:** `ParserDeExpressao` interpreta `NdF`, `d8`, `4d6kh3`, `2d20kl1` e somas de termos e
  constantes (`2d6+1d4-1`), com limite de quantidade e faces. `RoladorDeDados` rola usando um
  `GeradorAleatorio` (nos testes, `GeradorFixo` deixa o resultado previsível).
- **Perfis:**

  | Perfil | Dado | Sucesso | Crítico / falha crítica | Dano crítico | Ao zerar PV |
  |---|---|---|---|---|---|
  | `dnd5e` | d20 | rolar acima ou igual | 20 / 1 | dobra os dados | testes contra a morte |
  | `percentual` | d100 | rolar abaixo ou igual | 1–5 / 96–100 | maximiza os dados | morte imediata |

- **Mecânica:** `RoladorDeTeste` (vantagem e desvantagem) e `ResolvedorDeAtaque` (acerto, erro,
  crítico, falha crítica, dano mínimo zero).
- **Combate:** `EstadoDeCombate` controla PV, dano, cura, recuperação, morte instantânea e testes
  contra a morte, persistidos entre chamadas.
- **Serviços:** `ServicoDeRolagem` (rolagens livres e testes), `ServicoDeCombate` (iniciar, atacar,
  dano, cura, teste contra a morte) e `ServicoDeHistorico` (toda rolagem fica registrada por
  personagem e tipo).

### 3. Inventário (`com.projeto_rpg.inventario`) — Pessoa 3

Catálogo de itens e habilidades, inventário do personagem, carga, CA final e espaços de habilidade.

- Item com tipo (`ARMA`, `ARMADURA`, `ESCUDO`, `CONSUMIVEL`, `GERAL`), peso e, para armaduras, CA
  base e categoria (`LEVE`, `MEDIA`, `PESADA`).
- Capacidade de carga = `Força × 15` libras.
- CA final: sem armadura `10 + Des`; leve `base + Des`; média `base + Des (máx. +2)`; pesada só a
  base; escudo `+2`.
- Habilidades de nível 0 (truque) a 9; usar uma de nível 1 a 9 gasta um espaço do mesmo nível;
  o descanso longo recupera todos.

Detalhes em [`docs/inventario.md`](docs/inventario.md).

---

## Arquitetura e convenções de código

Cada módulo segue a mesma divisão em pacotes:

| Pacote | Responsabilidade | Depende do Spring? |
|---|---|---|
| `modelo` | entidades (`@Entity`/`@Embeddable`) com suas validações e repositórios Spring Data | só JPA |
| `regra` | cálculos puros (sem estado, sem banco) | não |
| `servico` | casos de uso: busca, valida, chama o modelo/regra e salva | sim (`@Service`) |
| `excecao` | exceções de domínio com nome que explica o erro | não |
| `config` | montagem de beans (apenas em `regras`) | sim |

Convenções:

- **Código em português**: classes, métodos, variáveis e mensagens (`ServicoDePersonagem`,
  `ganharXp`, `ItemNaoEncontradoException`).
- Nomes de classes no padrão `ServicoDe<Coisa>`, `Calculadora<De><Coisa>`, `<Coisa>Repository`,
  `<Situação>Exception`.
- As **regras de negócio ficam nas entidades e nas classes de `regra`**; os serviços só orquestram.
- A entidade **valida a si mesma** no construtor e nos métodos de alteração e lança exceção quando
  o estado seria inválido.
- Erros de domínio usam **exceções específicas** do módulo, não `RuntimeException` genérica.
- Um módulo **não acessa a entidade de outro**: guarda só o id e recebe os valores de que precisa.

---

## Testes

- Os testes espelham os pacotes de `main` (`ficha/regra/CompraDePontos` → `ficha/regra/CompraDePontosTest`).
- **Testes de unidade** (entidades e regras) não sobem o Spring e rodam em milissegundos.
- **Testes de serviço e de repositório** usam `@SpringBootTest` / `@DataJpaTest` com o H2 em memória.
- No motor de regras, `GeradorFixo` substitui o gerador aleatório para que as rolagens sejam
  determinísticas, e `ServicoComPerfilPercentualTest` comprova que os mesmos serviços funcionam
  com outro sistema de regras.
- Situação atual: **338 testes, todos passando** (`./mvnw test`).

---

## Padrão de commits

Os commits seguem o [Conventional Commits](https://www.conventionalcommits.org/pt-br/), com a
descrição **em português**, em letra minúscula e sem ponto final:

```
<tipo>(<escopo>): <descrição curta do que muda>
```

### Tipos

| Tipo | Quando usar |
|---|---|
| `test` | adiciona ou altera testes |
| `feat` | nova funcionalidade / código de produção |
| `fix` | corrige um bug (no código ou num teste) |
| `refactor` | muda a estrutura sem mudar o comportamento |
| `docs` | documentação (`README.md`, `docs/`) |
| `build` | `pom.xml`, dependências, plugins |

### Escopo: a fase do TDD

O escopo indica **em que etapa do ciclo red → green → refactor** o commit está:

| Commit | Fase | Estado dos testes |
|---|---|---|
| `test(red): ...` | **Red**: escreve o teste da regra nova | o teste novo **falha** (ou nem compila) |
| `feat(green): ...` | **Green**: implementa o mínimo para passar | **todos passam** |
| `fix(green): ...` | **Green**: corrige um comportamento coberto por um teste red | **todos passam** |
| `refactor: ...` | **Refactor**: melhora o código já coberto | **continuam passando** |
| `test: ...` | teste extra sobre código que já existe (não é red) | passam |

Todo `test(red)` é seguido por um `feat(green)` (ou `fix(green)`) que o faz passar. Assim, o
`git log` mostra o ciclo do TDD na prática:

```
8f1b6dd test(red): reserva de espaços gasta, recupera e ajusta por nível
58ce153 feat(green): entidade ReservaDeEspacos com espaços por nível e descanso longo

44690ce test(red): XP não deve estourar o limite do inteiro
fa6221c fix(green): satura o XP em Integer.MAX_VALUE

6f6db30 feat(green): PV máximo do Personagem
4431d3c refactor: extrai CalculadoraDePv de Personagem (SRP)
6fc316f test: cobre CalculadoraDePv diretamente
```

> **Observação:** os commits do módulo `regras` foram feitos antes deste padrão e usam o nome do
> módulo como escopo, com um espaço antes dos parênteses: `test (regras): ...`,
> `feat (regras): ...`, `refactor (regras): ...`. O ciclo é o mesmo (todo `test` é seguido do
> `feat` correspondente). Para commits novos, use o padrão `tipo(fase)` descrito acima.

### Regras práticas

- Um commit por etapa do ciclo: não junte o teste red com a implementação green.
- A descrição diz **o que** a regra faz, não "adiciona arquivo X": prefira
  `test(red): custo de cada valor na compra de pontos` a `test(red): cria CompraDePontosTest`.
- Antes de um commit `feat(green)`, `fix(green)` ou `refactor`, rode `./mvnw test` e confira que
  tudo passa.

Para ver o ciclo no histórico:

```bash
git log --oneline
git log --oneline --grep='(red)'      # só os testes red
```

---

## Branches e pull requests

- `main` é a branch principal e deve estar sempre com os testes passando.
- Cada pessoa trabalha numa branch `feature/<nome>-<módulo>` (ex.: `feature/bianca-ficha`).
- A integração com a `main` é feita por **pull request** no GitHub, depois de rodar `./mvnw test`.
- Ao terminar um módulo, documente-o em `docs/<módulo>.md` com um commit `docs:`.

---

## Documentação complementar

- [`docs/ficha.md`](docs/ficha.md): módulo ficha, regras e contrato com os outros módulos.
- [`docs/inventario.md`](docs/inventario.md): módulo inventário, entidades, regras e independência.
