package org.example.Controller;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import org.example.Model.PropostaDAO;

import java.io.IOException;
import java.sql.SQLException;
import java.util.List;

@WebServlet("/admin")
public class AdministradorServlet extends HttpServlet {
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        HttpSession session = request.getSession(false);
        if (session == null || session.getAttribute("usuario") == null) {
            response.sendRedirect(request.getContextPath() + "/login.html");
            return;
        }

        if (!"ADMIN".equals(session.getAttribute("perfil"))) {
            response.sendRedirect(request.getContextPath() + "/inicio");
            return;
        }

        Pagina.preparar(request, "admin", "Painel administrativo");
        try {
            request.setAttribute("propostas", new PropostaDAO().listarTodas());
        } catch (SQLException e) {
            response.setStatus(HttpServletResponse.SC_SERVICE_UNAVAILABLE);
            request.setAttribute("falhaBanco", true);
            request.setAttribute("propostas", List.of());
        }
        Pagina.mostrar(request, response, "admin.jsp");
    }
}