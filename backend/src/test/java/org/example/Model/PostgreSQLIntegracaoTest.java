package org.example.Model;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.condition.EnabledIfSystemProperty;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.sql.Connection;
import java.sql.Statement;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

@EnabledIfSystemProperty(named = "db.integration", matches = "true")
class PostgreSQLIntegracaoTest {
    @BeforeAll
    static void prepararSchema() throws IOException, java.sql.SQLException {
        try (InputStream recurso = PostgreSQLIntegracaoTest.class.getResourceAsStream("/db/schema.sql")) {
            assertNotNull(recurso, "schema.sql não foi encontrado no classpath");
            String schema = new String(recurso.readAllBytes(), StandardCharsets.UTF_8);
            try (Connection conexao = Conexao.conectar(); Statement stmt = conexao.createStatement()) {
                for (String comando : schema.split(";")) {
                    if (!comando.isBlank()) {
                        stmt.execute(comando);
                    }
                }
            }
        }
    }

    @Test
    void consultaCatalogoEFiltros() throws java.sql.SQLException {
        CarroDAO dao = new CarroDAO();
        assertTrue(dao.listarTodos().size() >= 10);
        assertTrue(dao.listarCategorias().contains("SUV"));
        assertTrue(dao.listarMarcas().contains("Toyota"));
        assertEquals(1, dao.filtrar("SUV", "Toyota").size());
        assertEquals("Corolla Cross XRX", dao.buscarPorId(1).orElseThrow().getNome());
        assertFalse(dao.listarDestaques().isEmpty());
        assertFalse(dao.relacionados(dao.buscarPorId(1).orElseThrow(), 3).isEmpty());
    }

    @Test
    void persisteProposta() throws java.sql.SQLException {
        String email = "integracao-" + UUID.randomUUID() + "@example.test";
        try {
            Proposta proposta = new Proposta();
            proposta.setNome("Teste de integração");
            proposta.setEmail(email);
            proposta.setTelefone("11999990000");
            proposta.setVeiculoId(1);
            proposta.setMensagem("Solicito um horário para conhecer o veículo.");

            PropostaDAO dao = new PropostaDAO();
            dao.salvar(proposta);
            assertTrue(dao.listarTodas().stream().anyMatch(item -> email.equals(item.getEmail())));
        } finally {
            executar("DELETE FROM propostas WHERE email = ?", email);
        }
    }

    @Test
    void concluiVendaArmazenaRelatorioEBloqueiaVeiculo() throws java.sql.SQLException {
        int veiculoId = 900001;
        String email = "venda-integracao-" + UUID.randomUUID() + "@example.test";
        prepararVeiculoDeTeste(veiculoId);
        try {
            Compra compra = new Compra();
            compra.setNome("Cliente fictício");
            compra.setEmail(email);
            compra.setTelefone("11988887777");

            VendaDAO dao = new VendaDAO();
            Venda venda = dao.concluirCompra(compra, veiculoId).orElseThrow();
            assertEquals("Cliente fictício", venda.getCompradorNome());
            assertEquals(email, venda.getCompradorEmail());
            assertEquals("Teste Carro Integração E2E", venda.getVeiculoNome());
            assertEquals(80000, venda.getValor(), 0.01);
            assertEquals(venda.getId(), dao.buscarPorId(venda.getId()).orElseThrow().getId());
            assertTrue(new CarroDAO().buscarPorId(veiculoId).isEmpty());
            assertTrue(dao.concluirCompra(compra, veiculoId).isEmpty());
        } finally {
            executar("DELETE FROM vendas WHERE comprador_email = ?", email);
            executar("DELETE FROM veiculos WHERE id = ?", veiculoId);
        }
    }

    @Test
    void cadastraAutenticaEInativaUsuario() throws java.sql.SQLException {
        String login = "integracao-" + UUID.randomUUID();
        UsuarioDAO dao = new UsuarioDAO();
        try {
            assertTrue(dao.cadastrar(new Usuario(0, login, "Senha-Integracao-123", true)));
            assertTrue(UsuarioDAO.autenticar(login, "Senha-Integracao-123"));
            Usuario usuario = dao.consultarUsuario(login);
            assertNotNull(usuario);
            assertEquals("CLIENTE", dao.consultarPerfil(login));
            assertTrue(dao.inativarUsuario(usuario));
            assertFalse(UsuarioDAO.autenticar(login, "Senha-Integracao-123"));
        } finally {
            executar("DELETE FROM usuarios WHERE login = ?", login);
        }
    }

    private static void prepararVeiculoDeTeste(int id) throws java.sql.SQLException {
        String sql = "INSERT INTO veiculos (id, marca, modelo, versao, ano, preco, quilometragem, cambio, "
                + "combustivel, cor, categoria, potencia, portas, descricao, destaque, tom, disponivel) "
                + "VALUES (?, 'Teste', 'Carro Integração', 'E2E', 2025, 80000, 100, 'Manual', 'Flex', "
                + "'Prata', 'Hatch', 100, 4, 'Veículo fictício para teste de compra.', FALSE, '#317c88', TRUE) "
                + "ON CONFLICT (id) DO UPDATE SET disponivel = TRUE";
        try (Connection conexao = Conexao.conectar();
             var stmt = conexao.prepareStatement(sql)) {
            stmt.setInt(1, id);
            stmt.executeUpdate();
        }
    }

    private static void executar(String sql, Object valor) throws java.sql.SQLException {
        try (Connection conexao = Conexao.conectar();
             var stmt = conexao.prepareStatement(sql)) {
            stmt.setObject(1, valor);
            stmt.executeUpdate();
        }
    }
}