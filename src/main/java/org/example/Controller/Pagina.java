package org.example.Controller;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.example.Model.Estoque;

import java.io.IOException;
import java.sql.SQLException;
import java.util.List;

public final class Pagina {
    private Pagina() {
    }

    public static void preparar(HttpServletRequest request, String pagina, String titulo) throws SQLException {
        request.setAttribute("pagina", pagina);
        request.setAttribute("titulo", titulo);
        List<org.example.Model.Carro> carros = Estoque.listarTodos();
        List<String> marcas = carros.stream().map(org.example.Model.Carro::getMarca).distinct().toList();
        request.setAttribute("totalEstoque", carros.size());
        request.setAttribute("totalMarcas", marcas.size());
        request.setAttribute("marcas", marcas);
        request.setAttribute("destaqueId", Estoque.destaques().stream()
                .findFirst().or(() -> carros.stream().findFirst())
                .map(org.example.Model.Carro::getId).orElse(0));
    }

    public static void mostrar(HttpServletRequest request, HttpServletResponse response, String view)
            throws ServletException, IOException {
        request.getRequestDispatcher("/WEB-INF/views/" + view).forward(request, response);
    }

    public static void bancoIndisponivel(HttpServletResponse response) throws IOException {
        response.setStatus(HttpServletResponse.SC_SERVICE_UNAVAILABLE);
        response.setContentType("text/html;charset=UTF-8");
        response.getWriter().write("<!doctype html><html lang=\"pt-BR\"><meta charset=\"UTF-8\"><title>Serviço indisponível</title>"
                + "<body><h1>Serviço temporariamente indisponível</h1>"
                + "<p>Não foi possível acessar o banco de dados. Tente novamente mais tarde.</p></body></html>");
    }
}
