<%@ page pageEncoding="UTF-8" %>
<%@ include file="includes/header.jsp" %>

<section class="secao">
    <div class="limite">
        <p class="migalha"><a href="${ctx}/inicio">Início</a> <span>/</span> Compra registrada</p>
        <div class="confirmacao">
            <p class="olho olho-claro">Comprovante de compra · #<c:out value="${venda.id}"/></p>
            <h1>Venda registrada.</h1>
            <p>Guarde este resumo. A equipe da Bravura entrará em contato para seguir com pagamento e documentação.</p>
            <dl class="resumo">
                <div><dt>Data</dt><dd><c:out value="${venda.realizadaEmFormatada}"/></dd></div>
                <div><dt>Comprador</dt><dd><c:out value="${venda.compradorNome}"/></dd></div>
                <div><dt>E-mail</dt><dd><c:out value="${venda.compradorEmail}"/></dd></div>
                <div><dt>Telefone</dt><dd><c:out value="${venda.compradorTelefone}"/></dd></div>
                <div><dt>Veículo vendido</dt><dd><c:out value="${venda.veiculoNome}"/> · <c:out value="${venda.veiculoAno}"/></dd></div>
                <div><dt>Valor registrado</dt><dd><c:out value="${venda.valorFormatado}"/></dd></div>
            </dl>
            <div class="acoes">
                <a class="botao claro" href="${ctx}/estoque">Ver outros veículos</a>
                <a class="botao secundario claro" href="${ctx}/inicio">Voltar ao início</a>
            </div>
        </div>
    </div>
</section>

<%@ include file="includes/footer.jsp" %>