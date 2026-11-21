package org.example.Model;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class Conexao {
    private static final String URL_PADRAO = "jdbc:postgresql://localhost:5432/atividade2011";

    private Conexao() {
    }

    public static Connection conectar() throws SQLException {
        String url = System.getenv().getOrDefault("DB_URL", URL_PADRAO);
        String usuario = System.getenv().getOrDefault("DB_USER", "postgres");
        String senha = System.getenv().getOrDefault("DB_PASSWORD", "postgres");
        try {
            Class.forName("org.postgresql.Driver");
        } catch (ClassNotFoundException e) {
            throw new SQLException("Driver PostgreSQL não está disponível na aplicação.", e);
        }
        return DriverManager.getConnection(url, usuario, senha);
    }
}