package org.example.Controller;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import org.example.Model.Venda;
import org.example.Model.VendaDAO;

import java.io.IOException;
import java.sql.SQLException;
import java.util.Optional;

@WebServlet("/compra")
public class CompraServlet extends HttpServlet {
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        HttpSession sessao = request.getSession(false);
        Object idSessao = sessao == null ? null : sessao.getAttribute(CarrinhoServlet.CHAVE_ULTIMA_VENDA);
        if (!(idSessao instanceof Number)) {
            response.sendRedirect(request.getContextPath() + "/estoque");
            return;
        }

        try {
            Optional<Venda> venda = new VendaDAO().buscarPorId(((Number) idSessao).longValue());
            if (venda.isEmpty()) {
                response.sendError(HttpServletResponse.SC_NOT_FOUND);
                return;
            }
            Pagina.preparar(request, "compra", "Compra registrada");
            request.setAttribute("venda", venda.get());
            Pagina.mostrar(request, response, "compra.jsp");
        } catch (SQLException e) {
            Pagina.bancoIndisponivel(request, response, e);
        }
    }
}