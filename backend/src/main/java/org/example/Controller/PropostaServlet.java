package org.example.Controller;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.example.Model.Estoque;
import org.example.Model.Proposta;
import org.example.Model.PropostaDAO;

import java.io.IOException;
import java.sql.SQLException;
import java.util.Map;

@WebServlet("/proposta")
public class PropostaServlet extends HttpServlet {
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        try {
            Proposta proposta = new Proposta();
            proposta.setVeiculoId(lerId(request.getParameter("id")));
            preparar(request, proposta, false);
            Pagina.mostrar(request, response, "proposta.jsp");
        } catch (SQLException e) {
            Pagina.bancoIndisponivel(request, response, e);
        }
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        try {
            Proposta proposta = new Proposta();
            proposta.setNome(request.getParameter("nome"));
            proposta.setEmail(request.getParameter("email"));
            proposta.setTelefone(request.getParameter("telefone"));
            proposta.setVeiculoId(lerId(request.getParameter("veiculo")));
            proposta.setMensagem(request.getParameter("mensagem"));

            Map<String, String> erros = proposta.validar();
            if (proposta.getVeiculoId() > 0 && Estoque.buscarPorId(proposta.getVeiculoId()).isEmpty()) {
                erros.put("veiculo", "Escolha um veículo do estoque.");
            }
            if (!erros.isEmpty()) {
                response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                request.setAttribute("erros", erros);
                preparar(request, proposta, false);
            } else {
                new PropostaDAO().salvar(proposta);
                preparar(request, proposta, true);
            }
            Pagina.mostrar(request, response, "proposta.jsp");
        } catch (SQLException e) {
            Pagina.bancoIndisponivel(request, response, e);
        }
    }

    private void preparar(HttpServletRequest request, Proposta proposta, boolean enviada) throws SQLException {
        Pagina.preparar(request, "proposta", "Proposta");
        request.setAttribute("proposta", proposta);
        request.setAttribute("carros", Estoque.listarTodos());
        request.setAttribute("carroInteresse", Estoque.buscarPorId(proposta.getVeiculoId()).orElse(null));
        request.setAttribute("enviada", enviada);
    }

    private int lerId(String valor) {
        if (valor == null || valor.isBlank()) {
            return 0;
        }
        try {
            return Integer.parseInt(valor);
        } catch (NumberFormatException ex) {
            return 0;
        }
    }
}
