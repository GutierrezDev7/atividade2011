package org.example.Model;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.regex.Pattern;

public class Compra {
    private static final Pattern EMAIL = Pattern.compile("^[^@\\s]+@[^@\\s]+\\.[^@\\s]+$");

    private String nome = "";
    private String email = "";
    private String telefone = "";

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome == null ? "" : nome.trim();
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email == null ? "" : email.trim();
    }

    public String getTelefone() {
        return telefone;
    }

    public void setTelefone(String telefone) {
        this.telefone = telefone == null ? "" : telefone.trim();
    }

    public Map<String, String> validar() {
        Map<String, String> erros = new LinkedHashMap<>();
        if (nome.length() < 3 || nome.length() > 120) {
            erros.put("nome", "Informe seu nome completo.");
        }
        if (email.length() > 120 || !EMAIL.matcher(email).matches()) {
            erros.put("email", "Informe um e-mail válido.");
        }
        String digitos = telefone.replaceAll("\\D", "");
        if (digitos.length() < 10 || digitos.length() > 11 || telefone.length() > 20) {
            erros.put("telefone", "Informe um telefone com DDD.");
        }
        return erros;
    }
}