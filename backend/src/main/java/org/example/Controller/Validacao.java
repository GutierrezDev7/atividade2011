package org.example.Controller;

import org.example.Model.UsuarioDAO;
import java.sql.SQLException;

public class Validacao {
    public static boolean validarLogin(String usuario, String senha) throws SQLException {
        if(usuario==null||usuario.isBlank()){
            return false;
        }
        if(senha == null || senha.isBlank()){
            return false;
        }
        return UsuarioDAO.autenticar(usuario,senha);
    }

    public static String getPerfil(String usuario) throws SQLException {
        if (usuario == null || usuario.isBlank()) {
            return null;
        }
        return new UsuarioDAO().consultarPerfil(usuario.trim());
    }
}