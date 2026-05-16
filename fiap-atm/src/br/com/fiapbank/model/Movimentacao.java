package br.com.fiapbank.model;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * Value Object que representa uma movimentação no histórico da conta.
 * Armazena data/hora exatas, tipo da operação e valor.
 */
public class Movimentacao {

    private LocalDateTime dataHora;
    private TipoMovimentacao tipo;
    private Dinheiro valor;

    public Movimentacao(LocalDateTime dataHora, Dinheiro valor, TipoMovimentacao tipo) {
        this.dataHora = dataHora;
        this.valor = valor;
        this.tipo = tipo;
    }

    public LocalDateTime getDataHora() {
        return dataHora;
    }

    public Dinheiro getValor() {
        return valor;
    }

    public TipoMovimentacao getTipo() {
        return tipo;
    }

    public String getDataHoraFormatada() {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss");
        return dataHora.format(formatter);
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        Movimentacao that = (Movimentacao) obj;
        return dataHora != null && dataHora.equals(that.dataHora)
                && tipo == that.tipo
                && valor != null && valor.equals(that.valor);
    }

    @Override
    public int hashCode() {
        Integer result = dataHora != null ? dataHora.hashCode() : 0;
        result = 31 * result + (tipo != null ? tipo.hashCode() : 0);
        result = 31 * result + (valor != null ? valor.hashCode() : 0);
        return result;
    }

    @Override
    public String toString() {
        String sinal = (tipo == TipoMovimentacao.DEPOSITO || tipo == TipoMovimentacao.RENDIMENTO) ? "+" : "-";
        return String.format("%-12s %s %10s  %s",
                tipo.name(), sinal, valor.toString(), getDataHoraFormatada());
    }
}
