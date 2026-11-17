package org.example.Model;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class Conexao {
    private static final String URL_PADRAO = "jdbc:postgresql://localhost:5432/bravura";

    private Conexao() {
    }

    public static Connection conectar() throws SQLException {
        String usuario = System.getenv("DB_USER");
        if (usuario == null || usuario.isBlank()) {
            throw new SQLException("Configure a variável de ambiente DB_USER.");
        }
        String url = System.getenv().getOrDefault("DB_URL", URL_PADRAO);
        String senha = System.getenv().getOrDefault("DB_PASSWORD", "");
        return DriverManager.getConnection(url, usuario, senha);
    }
}