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
        Proposta proposta = new Proposta();
        proposta.setVeiculoId(lerId(request.getParameter("id")));
        preparar(request, proposta, false);
        Pagina.mostrar(request, response, "proposta.jsp");
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        Proposta proposta = new Proposta();
        proposta.setNome(request.getParameter("nome"));
        proposta.setEmail(request.getParameter("email"));
        proposta.setTelefone(request.getParameter("telefone"));
        proposta.setVeiculoId(lerId(request.getParameter("veiculo")));
        proposta.setMensagem(request.getParameter("mensagem"));

        Map<String, String> erros = proposta.validar();
        if (!erros.isEmpty()) {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            request.setAttribute("erros", erros);
            preparar(request, proposta, false);
        } else {
            try {
                new PropostaDAO().salvar(proposta);
                preparar(request, proposta, true);
            } catch (SQLException e) {
                response.setStatus(HttpServletResponse.SC_SERVICE_UNAVAILABLE);
                request.setAttribute("falhaEnvio", true);
                preparar(request, proposta, false);
            }
        }
        Pagina.mostrar(request, response, "proposta.jsp");
    }

    private void preparar(HttpServletRequest request, Proposta proposta, boolean enviada) {
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
