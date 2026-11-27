# Monitoramento de Máquinas

API REST desenvolvida em Java com Spring Boot para a CloudTech Solutions, voltada ao monitoramento de máquinas industriais. A aplicação permite cadastrar, consultar, atualizar e excluir máquinas, além de registrar e consultar dados de produção e consumo de energia.

Os dados ficam em memória (listas `ArrayList`), então são apagados quando a aplicação reinicia.

## Tecnologias

- Java 21
- Spring Boot 3.5 (spring-boot-starter-web)
- Maven
- JUnit 5 e MockMvc (spring-boot-starter-test)
- GitHub Actions (integração contínua)
- Railway (deploy em nuvem)

## Estrutura do projeto

```
monitoramento-maquinas/
├── .github/
│   └── workflows/
│       └── pipeline.yml
├── src/
│   ├── main/
│   │   ├── java/com/cloudtech/monitoramento/
│   │   │   ├── MonitoramentoMaquinasApplication.java
│   │   │   ├── controller/
│   │   │   │   └── MaquinaControllers.java
│   │   │   └── models/
│   │   │       ├── Maquinas.java
│   │   │       └── Producao.java
│   │   └── resources/
│   │       └── application.properties
│   └── test/
│       └── java/com/cloudtech/monitoramento/controller/
│           └── MaquinaControllersTest.java
├── .gitignore
├── pom.xml
└── README.md
```

- `models/Maquinas.java`: dados da máquina (id, nome, tipo, setor e status).
- `models/Producao.java`: dados de produção (maquinaId, consumoKw, horarioAcionamento e pecasProduzidas). A lista de produções forma o histórico básico de produção e consumo.
- `controller/MaquinaControllers.java`: todos os endpoints da API.
- `application.properties`: define a porta com `server.port=${PORT:8080}`. No Railway a porta vem da variável de ambiente `PORT`; localmente a aplicação usa a 8080.

**Sobre a pasta de testes:** o enunciado mostra uma pasta `tests/` na raiz. Em projetos Maven, o padrão é manter os testes em `src/test/java`, que é onde o Maven procura e executa os testes automaticamente. Por isso os testes deste projeto ficam em `src/test`.

## Endpoints

| Método | Endpoint | Descrição | Resposta |
|---|---|---|---|
| GET | `/` | Página inicial | 200 |
| GET | `/status` | Status da aplicação | 200 |
| GET | `/versao` | Versão da aplicação | 200 |
| GET | `/maquinas` | Lista as máquinas cadastradas | 200 |
| POST | `/maquinas/cadastro` | Cadastra uma máquina | 201 |
| PUT | `/maquinas/{id}` | Atualiza uma máquina | 200 ou 404 |
| DELETE | `/maquinas/{id}` | Exclui uma máquina | 204 ou 404 |
| GET | `/producao` | Lista o histórico de produção e consumo | 200 |
| POST | `/producao` | Registra dados de produção | 201 |

### GET /

```json
{
  "mensagem": "Bem-vindo ao sistema de monitoramento de máquinas industriais da CloudTech Solutions"
}
```

### GET /status

```json
{
  "status": "online",
  "mensagem": "Aplicação de monitoramento em execução"
}
```

### GET /versao

```json
{
  "versao": "1.0.0"
}
```

### POST /maquinas/cadastro

Requisição:

```json
{
  "nome": "Máquina CNC 01",
  "tipo": "CNC",
  "setor": "Usinagem",
  "status": "em operação"
}
```

Resposta (201), com o `id` gerado automaticamente:

```json
{
  "id": 1,
  "nome": "Máquina CNC 01",
  "tipo": "CNC",
  "setor": "Usinagem",
  "status": "em operação"
}
```

### GET /maquinas

```json
[
  {
    "id": 1,
    "nome": "Máquina CNC 01",
    "tipo": "CNC",
    "setor": "Usinagem",
    "status": "em operação"
  }
]
```

### PUT /maquinas/{id}

Substitui nome, tipo, setor e status da máquina e mantém o `id`. Retorna 404 se o `id` não existir.

Requisição para `PUT /maquinas/1`:

```json
{
  "nome": "Máquina CNC 01",
  "tipo": "CNC",
  "setor": "Usinagem",
  "status": "em manutenção"
}
```

Resposta (200):

```json
{
  "id": 1,
  "nome": "Máquina CNC 01",
  "tipo": "CNC",
  "setor": "Usinagem",
  "status": "em manutenção"
}
```

### DELETE /maquinas/{id}

Exclui a máquina e retorna 204 sem corpo. Retorna 404 se o `id` não existir.

### POST /producao

Requisição e resposta (201):

```json
{
  "maquinaId": 1,
  "consumoKw": 12.5,
  "horarioAcionamento": "08:30",
  "pecasProduzidas": 150
}
```

### GET /producao

```json
[
  {
    "maquinaId": 1,
    "consumoKw": 12.5,
    "horarioAcionamento": "08:30",
    "pecasProduzidas": 150
  }
]
```

## Como rodar

Pré-requisitos: Java 21 e Maven instalados.

```
mvn spring-boot:run
```

A aplicação sobe em `http://localhost:8080`.

Também é possível gerar o `.jar` e executá-lo:

```
mvn package
java -jar target/monitoramento-maquinas-1.0.0.jar
```

## Como rodar os testes

```
mvn test
```

Os testes ficam em `src/test/java/com/cloudtech/monitoramento/controller/MaquinaControllersTest.java` e usam JUnit 5 e MockMvc. São 11 testes, que verificam:

- se a página inicial, o `/status` e o `/versao` respondem corretamente;
- se é possível cadastrar, listar, atualizar e excluir máquinas;
- se atualizar ou excluir uma máquina inexistente retorna 404;
- se é possível registrar e consultar dados de produção.

Cada teste roda com o contexto da aplicação recriado (`@DirtiesContext`), então um teste não interfere no outro.

## Fluxo de branches

- `main`: versão estável da aplicação.
- `develop`: branch de desenvolvimento, onde as funcionalidades são integradas.
- `feature/*`: uma branch para cada funcionalidade, criada a partir da `develop`.

Cada funcionalidade foi desenvolvida em sua própria branch e integrada à `develop` por pull request:

1. `feature/cadastro-maquinas`: model `Maquinas` e endpoints de máquinas.
2. `feature/producao`: model `Producao` e endpoints de produção.
3. `feature/status-versao`: página inicial, status e versão.
4. `feature/testes`: testes automatizados com MockMvc.
5. `feature/pipeline`: pipeline de integração contínua.
6. `feature/documentacao`: este README.

Ao final, a `develop` é integrada à `main` por pull request, gerando a versão 1.0.0.

## Pipeline de integração contínua

O workflow `.github/workflows/pipeline.yml` ("CI - Build e Testes") roda no GitHub Actions a cada push, em qualquer branch:

1. Baixa o código do repositório (`actions/checkout`).
2. Configura o Java 21 com a distribuição temurin (`actions/setup-java`).
3. Executa `mvn -B package`, que baixa as dependências, compila a aplicação, roda os testes e gera o `.jar`. Se algum teste falhar, o pipeline é interrompido.
4. Publica o `.jar` como artefato `monitoramento-maquinas-jar` (`actions/upload-artifact`).
