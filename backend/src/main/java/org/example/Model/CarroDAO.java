package org.example.Model;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class CarroDAO {
    private static final String COLUNAS = "id, marca, modelo, versao, ano, preco, quilometragem, "
            + "cambio, combustivel, cor, categoria, potencia, portas, descricao, destaque, tom";

    public List<Carro> listarTodos() throws SQLException {
        String sql = "SELECT " + COLUNAS + " FROM veiculos WHERE disponivel = TRUE ORDER BY id";
        try (Connection conexao = Conexao.conectar();
             PreparedStatement stmt = conexao.prepareStatement(sql);
             ResultSet resultado = stmt.executeQuery()) {
            return mapearLista(resultado);
        }
    }

    public List<Carro> listarDestaques() throws SQLException {
        String sql = "SELECT " + COLUNAS + " FROM veiculos WHERE destaque = TRUE AND disponivel = TRUE ORDER BY id";
        try (Connection conexao = Conexao.conectar();
             PreparedStatement stmt = conexao.prepareStatement(sql);
             ResultSet resultado = stmt.executeQuery()) {
            return mapearLista(resultado);
        }
    }

    public List<String> listarCategorias() throws SQLException {
        return listarValores("categoria");
    }

    public List<String> listarMarcas() throws SQLException {
        return listarValores("marca");
    }

    public Optional<Carro> buscarPorId(int id) throws SQLException {
        String sql = "SELECT " + COLUNAS + " FROM veiculos WHERE id = ? AND disponivel = TRUE";
        try (Connection conexao = Conexao.conectar();
             PreparedStatement stmt = conexao.prepareStatement(sql)) {
            stmt.setInt(1, id);
            try (ResultSet resultado = stmt.executeQuery()) {
                return resultado.next() ? Optional.of(mapear(resultado)) : Optional.empty();
            }
        }
    }

    public List<Carro> filtrar(String categoria, String marca) throws SQLException {
        StringBuilder sql = new StringBuilder("SELECT ").append(COLUNAS)
            .append(" FROM veiculos WHERE disponivel = TRUE");
        if (!"todos".equals(categoria)) {
            sql.append(" AND LOWER(categoria) = LOWER(?)");
        }
        if (!"todas".equals(marca)) {
            sql.append(" AND LOWER(marca) = LOWER(?)");
        }
        sql.append(" ORDER BY id");

        try (Connection conexao = Conexao.conectar();
             PreparedStatement stmt = conexao.prepareStatement(sql.toString())) {
            int parametro = 1;
            if (!"todos".equals(categoria)) {
                stmt.setString(parametro++, categoria);
            }
            if (!"todas".equals(marca)) {
                stmt.setString(parametro, marca);
            }
            try (ResultSet resultado = stmt.executeQuery()) {
                return mapearLista(resultado);
            }
        }
    }

    public List<Carro> relacionados(Carro carro, int limite) throws SQLException {
        String sql = "SELECT " + COLUNAS + " FROM veiculos WHERE disponivel = TRUE AND id <> ? "
            + "AND categoria = ? ORDER BY id LIMIT ?";
        try (Connection conexao = Conexao.conectar();
             PreparedStatement stmt = conexao.prepareStatement(sql)) {
            stmt.setInt(1, carro.getId());
            stmt.setString(2, carro.getCategoria());
            stmt.setInt(3, limite);
            try (ResultSet resultado = stmt.executeQuery()) {
                List<Carro> encontrados = mapearLista(resultado);
                if (!encontrados.isEmpty()) {
                    return encontrados;
                }
            }
        }

        String sqlFallback = "SELECT " + COLUNAS + " FROM veiculos WHERE disponivel = TRUE "
            + "AND id <> ? ORDER BY id LIMIT ?";
        try (Connection conexao = Conexao.conectar();
             PreparedStatement stmt = conexao.prepareStatement(sqlFallback)) {
            stmt.setInt(1, carro.getId());
            stmt.setInt(2, limite);
            try (ResultSet resultado = stmt.executeQuery()) {
                return mapearLista(resultado);
            }
        }
    }

    private List<String> listarValores(String coluna) throws SQLException {
        if (!"categoria".equals(coluna) && !"marca".equals(coluna)) {
            throw new IllegalArgumentException("Coluna de catálogo inválida.");
        }
        String sql = "SELECT DISTINCT " + coluna + " FROM veiculos WHERE disponivel = TRUE ORDER BY " + coluna;
        List<String> valores = new ArrayList<>();
        try (Connection conexao = Conexao.conectar();
             PreparedStatement stmt = conexao.prepareStatement(sql);
             ResultSet resultado = stmt.executeQuery()) {
            while (resultado.next()) {
                valores.add(resultado.getString(1));
            }
        }
        return valores;
    }

    private List<Carro> mapearLista(ResultSet resultado) throws SQLException {
        List<Carro> carros = new ArrayList<>();
        while (resultado.next()) {
            carros.add(mapear(resultado));
        }
        return carros;
    }

    private Carro mapear(ResultSet resultado) throws SQLException {
        return new Carro(
                resultado.getInt("id"), resultado.getString("marca"), resultado.getString("modelo"),
                resultado.getString("versao"), resultado.getInt("ano"), resultado.getDouble("preco"),
                resultado.getInt("quilometragem"), resultado.getString("cambio"),
                resultado.getString("combustivel"), resultado.getString("cor"),
                resultado.getString("categoria"), resultado.getInt("potencia"),
                resultado.getInt("portas"), resultado.getString("descricao"),
                resultado.getBoolean("destaque"), resultado.getString("tom"));
    }
}