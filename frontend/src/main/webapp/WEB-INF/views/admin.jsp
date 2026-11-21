<%@ page pageEncoding="UTF-8" %>
<%@ include file="includes/header.jsp" %>

<section class="secao">
    <div class="limite admin-painel">
        <div class="admin-cabecalho">
            <div>
                <p class="olho">Área administrativa</p>
                <h1>Operação da loja</h1>
            </div>
            <a class="botao secundario" href="${ctx}/logout">Sair</a>
        </div>

        <section class="admin-secao">
            <h2>Vendas registradas <span class="admin-contagem">${vendas.size()}</span></h2>
            <c:choose>
                <c:when test="${empty vendas}">
                    <p class="admin-vazio">Ainda não há vendas registradas.</p>
                </c:when>
                <c:otherwise>
                    <div class="admin-tabela-wrap">
                        <table class="admin-tabela">
                            <thead>
                            <tr>
                                <th>Data</th>
                                <th>Cliente</th>
                                <th>Contato</th>
                                <th>Veículo vendido</th>
                                <th>Valor</th>
                                <th>Comprovante</th>
                            </tr>
                            </thead>
                            <tbody>
                            <c:forEach var="venda" items="${vendas}">
                                <tr>
                                    <td><c:out value="${venda.realizadaEmFormatada}"/></td>
                                    <td><c:out value="${venda.compradorNome}"/></td>
                                    <td>
                                        <a href="mailto:${fn:escapeXml(venda.compradorEmail)}"><c:out value="${venda.compradorEmail}"/></a>
                                        <a href="tel:${fn:escapeXml(venda.compradorTelefone)}"><c:out value="${venda.compradorTelefone}"/></a>
                                    </td>
                                    <td><c:out value="${venda.veiculoNome}"/> · <c:out value="${venda.veiculoAno}"/></td>
                                    <td><c:out value="${venda.valorFormatado}"/></td>
                                    <td>#<c:out value="${venda.id}"/></td>
                                </tr>
                            </c:forEach>
                            </tbody>
                        </table>
                    </div>
                </c:otherwise>
            </c:choose>
        </section>

        <section class="admin-secao">
            <h2>Propostas recebidas</h2>
            <c:if test="${falhaBanco}">
                <p class="admin-alerta" role="alert">Não foi possível carregar as propostas. Tente novamente mais tarde.</p>
            </c:if>
        <c:choose>
            <c:when test="${empty propostas}">
                <p class="admin-vazio">Ainda não há propostas para exibir.</p>
            </c:when>
            <c:otherwise>
                <div class="admin-tabela-wrap">
                    <table class="admin-tabela">
                        <thead>
                        <tr>
                            <th>Recebida</th>
                            <th>Cliente</th>
                            <th>Contato</th>
                            <th>Veículo</th>
                            <th>Mensagem</th>
                            <th>Status</th>
                        </tr>
                        </thead>
                        <tbody>
                        <c:forEach var="item" items="${propostas}">
                            <tr>
                                <td><c:out value="${item.criadaEm}"/></td>
                                <td><c:out value="${item.nome}"/></td>
                                <td>
                                    <a href="mailto:${fn:escapeXml(item.email)}"><c:out value="${item.email}"/></a>
                                    <a href="tel:${fn:escapeXml(item.telefone)}"><c:out value="${item.telefone}"/></a>
                                </td>
                                <td><c:out value="${item.veiculoNome}"/></td>
                                <td class="admin-mensagem"><c:out value="${item.mensagem}"/></td>
                                <td><c:out value="${item.status}"/></td>
                            </tr>
                        </c:forEach>
                        </tbody>
                    </table>
                </div>
            </c:otherwise>
        </c:choose>
        </section>
    </div>
</section>

<%@ include file="includes/footer.jsp" %>