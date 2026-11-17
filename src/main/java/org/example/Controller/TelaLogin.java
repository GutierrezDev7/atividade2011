package org.example.Controller;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import java.io.IOException;

@WebServlet("/LoginServlet")
public class TelaLogin extends HttpServlet {
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        request.getRequestDispatcher("/login.html").forward(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        String usuario = request.getParameter("usuario");
        String senha = request.getParameter("senha");

        if (!Validacao.validarLogin(usuario, senha)) {
            response.sendRedirect(request.getContextPath() + "/login.html?erro=1");
            return;
        }

        String perfil = Validacao.getPerfil(usuario);
        HttpSession sessaoAnterior = request.getSession(false);
        if (sessaoAnterior != null) {
            sessaoAnterior.invalidate();
        }
        HttpSession session = request.getSession(true);
        session.setAttribute("usuario", usuario.trim());
        session.setAttribute("perfil", perfil);

        String destino = "ADMIN".equals(perfil) ? "/admin" : "/inicio";
        response.sendRedirect(request.getContextPath() + destino);
    }
}