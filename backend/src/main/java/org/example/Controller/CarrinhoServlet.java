package org.example.Controller;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import org.example.Model.Carro;
import org.example.Model.CarroDAO;
import org.example.Model.Compra;
import org.example.Model.Venda;
import org.example.Model.VendaDAO;

import java.io.IOException;
import java.sql.SQLException;
import java.util.Map;
import java.util.Optional;

@WebServlet("/carrinho")
public class CarrinhoServlet extends HttpServlet {
    static final String CHAVE_VEICULO = "carrinhoVeiculoId";
    static final String CHAVE_ULTIMA_VENDA = "ultimaVendaId";

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        try {
            preparar(request, new Compra(), Map.of(), null);
            Pagina.mostrar(request, response, "carrinho.jsp");
        } catch (SQLException e) {
            Pagina.bancoIndisponivel(request, response, e);
        }
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        String acao = request.getParameter("acao");
        if ("adicionar".equals(acao)) {
            adicionar(request, response);
        } else if ("remover".equals(acao)) {
            HttpSession sessao = request.getSession(false);
            if (sessao != null) {
                sessao.removeAttribute(CHAVE_VEICULO);
            }
            response.sendRedirect(request.getContextPath() + "/carrinho");
        } else if ("finalizar".equals(acao)) {
            finalizar(request, response);
        } else {
            response.sendError(HttpServletResponse.SC_BAD_REQUEST);
        }
    }

    private void adicionar(HttpServletRequest request, HttpServletResponse response)
            throws IOException {
        int veiculoId = lerId(request.getParameter("veiculoId"));
        try {
            if (veiculoId <= 0 || new CarroDAO().buscarPorId(veiculoId).isEmpty()) {
                response.sendError(HttpServletResponse.SC_CONFLICT, "Veículo indisponível.");
                return;
            }
            HttpSession sessao = request.getSession(true);
            sessao.setAttribute(CHAVE_VEICULO, veiculoId);
            sessao.removeAttribute(CHAVE_ULTIMA_VENDA);
            response.sendRedirect(request.getContextPath() + "/carrinho");
        } catch (SQLException e) {
            Pagina.bancoIndisponivel(request, response, e);
        }
    }

    private void finalizar(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        HttpSession sessao = request.getSession(false);
        Integer veiculoId = lerIdSessao(sessao);
        if (veiculoId == null) {
            response.sendRedirect(request.getContextPath() + "/carrinho");
            return;
        }

        Compra compra = new Compra();
        compra.setNome(request.getParameter("nome"));
        compra.setEmail(request.getParameter("email"));
        compra.setTelefone(request.getParameter("telefone"));
        Map<String, String> erros = compra.validar();
        try {
            if (!erros.isEmpty()) {
                response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                preparar(request, compra, erros, null);
                Pagina.mostrar(request, response, "carrinho.jsp");
                return;
            }

            Optional<Venda> venda = new VendaDAO().concluirCompra(compra, veiculoId);
            if (venda.isEmpty()) {
                sessao.removeAttribute(CHAVE_VEICULO);
                response.setStatus(HttpServletResponse.SC_CONFLICT);
                preparar(request, compra, Map.of(), "Esse veículo acabou de ser vendido. Escolha outro do estoque.");
                Pagina.mostrar(request, response, "carrinho.jsp");
                return;
            }

            sessao.removeAttribute(CHAVE_VEICULO);
            sessao.setAttribute(CHAVE_ULTIMA_VENDA, venda.get().getId());
            response.sendRedirect(request.getContextPath() + "/compra");
        } catch (SQLException e) {
            Pagina.bancoIndisponivel(request, response, e);
        }
    }

    private void preparar(HttpServletRequest request, Compra compra, Map<String, String> erros,
                          String mensagem) throws SQLException {
        Pagina.preparar(request, "carrinho", "Carrinho");
        request.setAttribute("compra", compra);
        request.setAttribute("erros", erros);
        request.setAttribute("mensagemCarrinho", mensagem);

        Integer veiculoId = lerIdSessao(request.getSession(false));
        if (veiculoId != null) {
            Carro carro = new CarroDAO().buscarPorId(veiculoId).orElse(null);
            if (carro == null) {
                HttpSession sessao = request.getSession(false);
                if (sessao != null) {
                    sessao.removeAttribute(CHAVE_VEICULO);
                }
                request.setAttribute("mensagemCarrinho", "Esse veículo não está mais disponível.");
            }
            request.setAttribute("carro", carro);
        }
    }

    private Integer lerIdSessao(HttpSession sessao) {
        if (sessao == null) {
            return null;
        }
        Object valor = sessao.getAttribute(CHAVE_VEICULO);
        return valor instanceof Integer ? (Integer) valor : null;
    }

    private int lerId(String valor) {
        if (valor == null) {
            return 0;
        }
        try {
            return Integer.parseInt(valor);
        } catch (NumberFormatException e) {
            return 0;
        }
    }
}