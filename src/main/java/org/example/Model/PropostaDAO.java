package org.example.Model;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class PropostaDAO {
    private static final String SQL_INSERIR = """
            INSERT INTO propostas (nome, email, telefone, veiculo_id, mensagem)
            VALUES (?, ?, ?, ?, ?)
            """;
        private static final String SQL_LISTAR = """
            SELECT id, nome, email, telefone, veiculo_id, mensagem, status, criada_em
            FROM propostas
            ORDER BY criada_em DESC, id DESC
            """;

    public void salvar(Proposta proposta) throws SQLException {
        try (Connection conexao = Conexao.conectar();
             PreparedStatement stmt = conexao.prepareStatement(SQL_INSERIR)) {
            stmt.setString(1, proposta.getNome());
            stmt.setString(2, proposta.getEmail());
            stmt.setString(3, proposta.getTelefone());
            stmt.setInt(4, proposta.getVeiculoId());
            stmt.setString(5, proposta.getMensagem());
            stmt.executeUpdate();
        }
    }

    public List<Proposta> listarTodas() throws SQLException {
        List<Proposta> propostas = new ArrayList<>();
        try (Connection conexao = Conexao.conectar();
             PreparedStatement stmt = conexao.prepareStatement(SQL_LISTAR);
             ResultSet resultado = stmt.executeQuery()) {
            while (resultado.next()) {
                Proposta proposta = new Proposta();
                proposta.setId(resultado.getLong("id"));
                proposta.setNome(resultado.getString("nome"));
                proposta.setEmail(resultado.getString("email"));
                proposta.setTelefone(resultado.getString("telefone"));
                proposta.setVeiculoId(resultado.getInt("veiculo_id"));
                proposta.setMensagem(resultado.getString("mensagem"));
                proposta.setStatus(resultado.getString("status"));
                proposta.setCriadaEm(resultado.getTimestamp("criada_em").toLocalDateTime());
                proposta.setVeiculoNome(Estoque.buscarPorId(proposta.getVeiculoId())
                        .map(carro -> carro.getMarca() + " " + carro.getNome())
                        .orElse("Veículo indisponível"));
                propostas.add(proposta);
            }
        }
        return propostas;
    }
}