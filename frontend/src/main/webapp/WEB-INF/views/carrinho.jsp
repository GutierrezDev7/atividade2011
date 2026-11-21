<%@ page pageEncoding="UTF-8" %>
<%@ include file="includes/header.jsp" %>

<section class="secao">
    <div class="limite">
        <p class="migalha"><a href="${ctx}/inicio">Início</a> <span>/</span> Carrinho</p>
        <p class="olho">Compra de seminovo</p>
        <h1>Carrinho</h1>
        <c:if test="${not empty mensagemCarrinho}">
            <p class="mensagem-erro" role="alert"><c:out value="${mensagemCarrinho}"/></p>
        </c:if>

        <c:choose>
            <c:when test="${empty carro}">
                <div class="vazio">
                    <h2>Seu carrinho está vazio.</h2>
                    <p>Escolha um veículo disponível no estoque para continuar.</p>
                    <a class="botao" href="${ctx}/estoque">Ver estoque</a>
                </div>
            </c:when>
            <c:otherwise>
                <div class="carrinho-grade">
                    <section class="carrinho-veiculo" aria-label="Veículo selecionado">
                        <p class="olho">Veículo selecionado</p>
                        <h2><c:out value="${carro.marca}"/> <c:out value="${carro.nome}"/></h2>
                        <p class="meta"><c:out value="${carro.ano}"/> · <c:out value="${carro.kmFormatado}"/> · <c:out value="${carro.cambio}"/></p>
                        <dl class="resumo carrinho-resumo">
                            <div><dt>Veículo</dt><dd><c:out value="${carro.marca}"/> <c:out value="${carro.nome}"/></dd></div>
                            <div><dt>Ano</dt><dd><c:out value="${carro.ano}"/></dd></div>
                            <div><dt>Total</dt><dd><c:out value="${carro.precoFormatado}"/></dd></div>
                        </dl>
                        <form class="carrinho-remover" action="${ctx}/carrinho" method="post">
                            <input type="hidden" name="acao" value="remover">
                            <button class="botao secundario" type="submit">Remover veículo</button>
                        </form>
                    </section>

                    <section aria-label="Dados do comprador">
                        <h2>Dados do comprador</h2>
                        <form class="formulario" action="${ctx}/carrinho" method="post">
                            <input type="hidden" name="acao" value="finalizar">
                            <label>
                                Nome completo
                                <input type="text" name="nome" maxlength="120" autocomplete="name" required value="${fn:escapeXml(compra.nome)}">
                                <c:if test="${not empty erros.nome}"><small><c:out value="${erros.nome}"/></small></c:if>
                            </label>
                            <label>
                                E-mail
                                <input type="email" name="email" maxlength="120" autocomplete="email" required value="${fn:escapeXml(compra.email)}">
                                <c:if test="${not empty erros.email}"><small><c:out value="${erros.email}"/></small></c:if>
                            </label>
                            <label>
                                Telefone
                                <input type="tel" name="telefone" maxlength="20" autocomplete="tel" required placeholder="(11) 90000-0000" value="${fn:escapeXml(compra.telefone)}">
                                <c:if test="${not empty erros.telefone}"><small><c:out value="${erros.telefone}"/></small></c:if>
                            </label>
                            <p class="compra-nota">O registro confirma a venda do veículo. Pagamento e documentação são tratados com a equipe da loja.</p>
                            <button class="botao" type="submit">Confirmar compra · <c:out value="${carro.precoFormatado}"/></button>
                        </form>
                    </section>
                </div>
            </c:otherwise>
        </c:choose>
    </div>
</section>

<%@ include file="includes/footer.jsp" %>