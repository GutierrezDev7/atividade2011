# Conclusão

## Estrutura

- `backend/src/main/java`: controllers, models e DAOs MVC.
- `backend/src/main/resources`: schema do PostgreSQL.
- `backend/src/test/java`: testes de integração.
- `frontend/src/main/webapp`: JSP, HTML, CSS e arquivos estáticos.
- `pom.xml` na raiz compila o backend e empacota o frontend no WAR.

## Implementação

O catálogo deixou de ser uma lista fixa em Java e passou a ser consultado no PostgreSQL por `CarroDAO`. Veículos vendidos deixam de aparecer no estoque. O carrinho guarda um veículo por sessão e o checkout valida os dados do comprador. `VendaDAO` bloqueia o veículo em transação, grava o comprador e o snapshot do carro em `vendas`, e marca o veículo como indisponível. O comprovante fica associado à sessão que concluiu a compra; o painel administrativo lista as vendas. Propostas continuam gravadas em `propostas`. O login consulta usuários ativos e verifica senhas com BCrypt.

O schema e os dez veículos iniciais estão em [backend/src/main/resources/db/schema.sql](backend/src/main/resources/db/schema.sql). A conexão usa `jdbc:postgresql://localhost:5432/atividade2011`, usuário `postgres` e senha `postgres` como padrões; `DB_URL`, `DB_USER` e `DB_PASSWORD` podem sobrescrevê-los. O driver JDBC é carregado explicitamente para funcionar pelo classloader do Tomcat. O servidor foi alinhado a Jakarta Servlet 6.0 e Tomcat 10.1 pelo Cargo.

## Comunicação HTTP

O frontend usa links e formulários HTML tradicionais; não há chamadas `fetch`/AJAX. Os endereços abaixo são relativos ao contexto `/ServletTomCat`.

| Método e origem no frontend | Backend | Acesso a dados / resultado |
| --- | --- | --- |
| `GET /` e links para `inicio` em `index.html` | `InicioServlet.doGet` | Lê `veiculos` e mostra destaques. |
| `GET /estoque?categoria=...&marca=...` pelos links e filtros | `EstoqueServlet.doGet` | Lê `veiculos`, categorias e marcas; aplica os filtros no banco. |
| `GET /veiculo?id=...` nos cartões | `VeiculoServlet.doGet` | Busca o veículo e os relacionados em `veiculos`; veículo inexistente retorna 404. |
| `POST /carrinho` com `acao=adicionar` na ficha | `CarrinhoServlet.doPost` | Confirma disponibilidade em `veiculos` e guarda o ID na sessão. |
| `GET /carrinho` pelo menu | `CarrinhoServlet.doGet` | Carrega o veículo da sessão e apresenta o checkout. |
| `POST /carrinho` com `acao=remover` | `CarrinhoServlet.doPost` | Remove o veículo da sessão, sem gravar venda. |
| `POST /carrinho` com `acao=finalizar` pelo checkout | `CarrinhoServlet.doPost` e `VendaDAO.concluirCompra` | Valida cliente, bloqueia e vende o veículo transacionalmente; grava o comprovante em `vendas`. |
| `GET /compra` após a confirmação | `CompraServlet.doGet` | Busca o comprovante pelo ID guardado na sessão; não expõe o recibo por ID público. |
| `GET /proposta?id=...` nos botões de interesse | `PropostaServlet.doGet` | Carrega o catálogo e pré-seleciona o veículo no formulário. |
| `POST /proposta` pelo formulário `proposta.jsp` | `PropostaServlet.doPost` | Valida os campos e o veículo; insere em `propostas`. |
| `GET /sobre` pela navegação | `SobreServlet.doGet` | Renderiza a página e carrega os dados comuns do catálogo para o cabeçalho/rodapé. |
| `GET /LoginServlet` | `TelaLogin.doGet` | Encaminha para `login.html`. |
| `POST /LoginServlet` pelo formulário de `login.html` | `TelaLogin.doPost` | Consulta `usuarios`, valida BCrypt e perfil; inicia sessão ou redireciona ao login. |
| `GET /admin` pelo fluxo de login | `AdministradorServlet.doGet` | Exige sessão com perfil `ADMIN` e lista `propostas` e `vendas`. |
| `GET /logout` pelo link Sair | `LogoutServlet.doGet` | Invalida a sessão e redireciona para o login. |
| `GET /home` | `HomeServlet.doGet` | Redireciona conforme a sessão existente. |

`admin.html` e `home.html` agora encaminham para as rotas correspondentes, evitando contornar a verificação de sessão. Os endpoints de demonstração `/NomeDoEndPoint` e `/NomeDoSegundoEndPoint` não são usados pelo frontend: o primeiro encaminha ao index e o segundo apenas responde/eco a mensagem, sem persistência. O checkout registra a venda no sistema; não processa pagamento eletrônico, e a loja combina pagamento/documentação com o comprador.

## Banco e execução

Crie o banco com uma conta PostgreSQL autorizada e aplique o schema com o usuário da aplicação:

```powershell
createdb -h localhost -U <administrador-postgresql> atividade2011
psql -h localhost -U postgres -d atividade2011 -f backend/src/main/resources/db/schema.sql
```

Com o PostgreSQL autenticando as credenciais configuradas, os comandos são `mvn test`, `mvn "-Ddb.integration=true" test` e `mvn cargo:run`. O endereço local do WAR é `http://localhost:8080/ServletTomCat/`; o servidor está iniciado para teste nesta sessão.

## Verificação

- `mvn test`: passou; os quatro testes de integração são ignorados por padrão.
- `mvn "-Ddb.integration=true" test`: passou com 4 testes, 0 falhas e 0 erros; cobre catálogo, propostas, usuários e venda transacional com veículo fictício.
- Deploy: o WAR iniciou em Tomcat 10.1.62. As páginas de início, estoque, detalhe, carrinho e checkout responderam 200.
- HTTP ponta a ponta: adicionei um veículo fictício, finalizei pelo navegador, conferi comprador/carro/valor no comprovante e a gravação no PostgreSQL; o veículo ficou indisponível. A proposta e o veículo fictícios foram removidos ao final.
- Painel admin: uma conta temporária confirmou a renderização das seções de vendas e propostas; a conta de teste foi removida.
- O primeiro deploy no Tomcat não carregava o driver JDBC automaticamente, embora ele estivesse no WAR; o carregamento explícito em `Conexao` resolveu o problema.

É necessário corrigir/autenticar a senha dessa role no PostgreSQL (ou definir `DB_PASSWORD` com a senha aceita) e então repetir o comando de integração. O schema não cria um usuário de aplicação `ADMIN`: o painel exige uma conta previamente provisionada com `perfil = 'ADMIN'`; não foi inventada uma credencial administrativa.