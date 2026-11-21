package org.example.Model;

import java.text.NumberFormat;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Locale;

public class Venda {
    private static final DateTimeFormatter DATA = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");

    private final long id;
    private final String compradorNome;
    private final String compradorEmail;
    private final String compradorTelefone;
    private final int veiculoId;
    private final String veiculoMarca;
    private final String veiculoModelo;
    private final String veiculoVersao;
    private final int veiculoAno;
    private final double valor;
    private final LocalDateTime realizadaEm;

    public Venda(long id, String compradorNome, String compradorEmail, String compradorTelefone,
                 int veiculoId, String veiculoMarca, String veiculoModelo, String veiculoVersao,
                 int veiculoAno, double valor, LocalDateTime realizadaEm) {
        this.id = id;
        this.compradorNome = compradorNome;
        this.compradorEmail = compradorEmail;
        this.compradorTelefone = compradorTelefone;
        this.veiculoId = veiculoId;
        this.veiculoMarca = veiculoMarca;
        this.veiculoModelo = veiculoModelo;
        this.veiculoVersao = veiculoVersao;
        this.veiculoAno = veiculoAno;
        this.valor = valor;
        this.realizadaEm = realizadaEm;
    }

    public long getId() {
        return id;
    }

    public String getCompradorNome() {
        return compradorNome;
    }

    public String getCompradorEmail() {
        return compradorEmail;
    }

    public String getCompradorTelefone() {
        return compradorTelefone;
    }

    public int getVeiculoId() {
        return veiculoId;
    }

    public String getVeiculoMarca() {
        return veiculoMarca;
    }

    public String getVeiculoModelo() {
        return veiculoModelo;
    }

    public String getVeiculoVersao() {
        return veiculoVersao;
    }

    public String getVeiculoNome() {
        return veiculoMarca + " " + veiculoModelo + " " + veiculoVersao;
    }

    public int getVeiculoAno() {
        return veiculoAno;
    }

    public double getValor() {
        return valor;
    }

    public String getValorFormatado() {
        return NumberFormat.getCurrencyInstance(Locale.forLanguageTag("pt-BR")).format(valor);
    }

    public LocalDateTime getRealizadaEm() {
        return realizadaEm;
    }

    public String getRealizadaEmFormatada() {
        return realizadaEm == null ? "" : DATA.format(realizadaEm);
    }
}