package org.example.Model;

import java.sql.SQLException;
import java.util.List;
import java.util.Optional;

public final class Estoque {
    private Estoque() {
    }

    public static List<Carro> listarTodos() throws SQLException {
        return new CarroDAO().listarTodos();
    }

    public static List<String> categorias() throws SQLException {
        return new CarroDAO().listarCategorias();
    }

    public static List<String> marcas() throws SQLException {
        return new CarroDAO().listarMarcas();
    }

    public static List<Carro> destaques() throws SQLException {
        return new CarroDAO().listarDestaques();
    }

    public static Optional<Carro> buscarPorId(int id) throws SQLException {
        return new CarroDAO().buscarPorId(id);
    }

    public static List<Carro> filtrar(String categoria, String marca) throws SQLException {
        String categoriaNormalizada = normalizarCategoria(categoria);
        String marcaNormalizada = normalizarMarca(marca);
        return new CarroDAO().filtrar(categoriaNormalizada, marcaNormalizada);
    }

    public static List<Carro> relacionados(Carro carro, int limite) throws SQLException {
        return new CarroDAO().relacionados(carro, limite);
    }

    public static String normalizarCategoria(String categoria) throws SQLException {
        if (categoria == null || categoria.isBlank() || categoria.equalsIgnoreCase("todos")) {
            return "todos";
        }
        for (String item : categorias()) {
            if (item.equalsIgnoreCase(categoria)) {
                return item;
            }
        }
        return "todos";
    }

    public static String normalizarMarca(String marca) throws SQLException {
        if (marca == null || marca.isBlank() || marca.equalsIgnoreCase("todas")) {
            return "todas";
        }
        for (String item : marcas()) {
            if (item.equalsIgnoreCase(marca)) {
                return item;
            }
        }
        return "todas";
    }
}
