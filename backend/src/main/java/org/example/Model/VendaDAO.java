package org.example.Model;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class VendaDAO {
    private static final String COLUNAS = "id, comprador_nome, comprador_email, comprador_telefone, "
            + "veiculo_id, veiculo_marca, veiculo_modelo, veiculo_versao, veiculo_ano, valor, realizada_em";

    public Optional<Venda> concluirCompra(Compra compra, int veiculoId) throws SQLException {
        try (Connection conexao = Conexao.conectar()) {
            conexao.setAutoCommit(false);
            try {
                String sqlVeiculo = "SELECT marca, modelo, versao, ano, preco FROM veiculos "
                        + "WHERE id = ? AND disponivel = TRUE FOR UPDATE";
                String marca;
                String modelo;
                String versao;
                int ano;
                double valor;
                try (PreparedStatement stmt = conexao.prepareStatement(sqlVeiculo)) {
                    stmt.setInt(1, veiculoId);
                    try (ResultSet resultado = stmt.executeQuery()) {
                        if (!resultado.next()) {
                            conexao.rollback();
                            return Optional.empty();
                        }
                        marca = resultado.getString("marca");
                        modelo = resultado.getString("modelo");
                        versao = resultado.getString("versao");
                        ano = resultado.getInt("ano");
                        valor = resultado.getDouble("preco");
                    }
                }

                String sqlVenda = "INSERT INTO vendas (comprador_nome, comprador_email, comprador_telefone, "
                        + "veiculo_id, veiculo_marca, veiculo_modelo, veiculo_versao, veiculo_ano, valor) "
                        + "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?) RETURNING id, realizada_em";
                Venda venda;
                try (PreparedStatement stmt = conexao.prepareStatement(sqlVenda)) {
                    stmt.setString(1, compra.getNome());
                    stmt.setString(2, compra.getEmail());
                    stmt.setString(3, compra.getTelefone());
                    stmt.setInt(4, veiculoId);
                    stmt.setString(5, marca);
                    stmt.setString(6, modelo);
                    stmt.setString(7, versao);
                    stmt.setInt(8, ano);
                    stmt.setDouble(9, valor);
                    try (ResultSet resultado = stmt.executeQuery()) {
                        resultado.next();
                        Timestamp realizadaEm = resultado.getTimestamp("realizada_em");
                        venda = new Venda(resultado.getLong("id"), compra.getNome(), compra.getEmail(),
                                compra.getTelefone(), veiculoId, marca, modelo, versao, ano, valor,
                                realizadaEm.toLocalDateTime());
                    }
                }

                try (PreparedStatement stmt = conexao.prepareStatement(
                        "UPDATE veiculos SET disponivel = FALSE, destaque = FALSE WHERE id = ? AND disponivel = TRUE")) {
                    stmt.setInt(1, veiculoId);
                    if (stmt.executeUpdate() != 1) {
                        throw new SQLException("O veículo deixou de estar disponível durante a compra.");
                    }
                }
                conexao.commit();
                return Optional.of(venda);
            } catch (SQLException e) {
                try {
                    conexao.rollback();
                } catch (SQLException rollbackError) {
                    e.addSuppressed(rollbackError);
                }
                throw e;
            }
        }
    }

    public Optional<Venda> buscarPorId(long id) throws SQLException {
        String sql = "SELECT " + COLUNAS + " FROM vendas WHERE id = ?";
        try (Connection conexao = Conexao.conectar();
             PreparedStatement stmt = conexao.prepareStatement(sql)) {
            stmt.setLong(1, id);
            try (ResultSet resultado = stmt.executeQuery()) {
                return resultado.next() ? Optional.of(mapear(resultado)) : Optional.empty();
            }
        }
    }

    public List<Venda> listarTodas() throws SQLException {
        String sql = "SELECT " + COLUNAS + " FROM vendas ORDER BY realizada_em DESC, id DESC";
        List<Venda> vendas = new ArrayList<>();
        try (Connection conexao = Conexao.conectar();
             PreparedStatement stmt = conexao.prepareStatement(sql);
             ResultSet resultado = stmt.executeQuery()) {
            while (resultado.next()) {
                vendas.add(mapear(resultado));
            }
        }
        return vendas;
    }

    private Venda mapear(ResultSet resultado) throws SQLException {
        Timestamp realizadaEm = resultado.getTimestamp("realizada_em");
        return new Venda(resultado.getLong("id"), resultado.getString("comprador_nome"),
                resultado.getString("comprador_email"), resultado.getString("comprador_telefone"),
                resultado.getInt("veiculo_id"), resultado.getString("veiculo_marca"),
                resultado.getString("veiculo_modelo"), resultado.getString("veiculo_versao"),
                resultado.getInt("veiculo_ano"), resultado.getDouble("valor"),
                realizadaEm.toLocalDateTime());
    }
}