package br.com.fiapbank.model;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * Classe abstrata que representa uma conta bancária.
 * Define o Template Method para saque e depósito.
 * O saldo é protegido — nenhuma classe externa pode alterá-lo diretamente.
 */
public abstract class Conta {

    protected Cliente cliente;
    protected Dinheiro saldo;
    protected Double taxa;
    protected StatusConta status;
    protected LocalDate dataAbertura;
    protected ContaAcesso contaAcesso;
    protected List<Movimentacao> movimentacoes;

    protected Conta(Cliente cliente, ContaAcesso contaAcesso, Dinheiro saldo, Double taxaMensal) {
        this.cliente = cliente;
        this.contaAcesso = contaAcesso;
        this.saldo = saldo;
        this.taxa = taxaMensal;
        this.status = StatusConta.ATIVA;
        this.dataAbertura = LocalDate.now();
        this.movimentacoes = new ArrayList<>();
    }

    // ==================== TEMPLATE METHOD: SAQUE ====================

    /**
     * Template Method que dita o algoritmo do saque:
     * 1. Verificar saldo
     * 2. Debitar valor
     * 3. Registrar movimentação de SAQUE
     * 4. Aplicar taxa específica da subclasse
     */
    public void realizarSaque(Dinheiro valor) {
        verificarContaAtiva();

        if (valor == null || valor.menorQue(Dinheiro.zero()) || valor.igualA(Dinheiro.zero())) {
            throw new IllegalArgumentException("O valor do saque deve ser maior que zero.");
        }

        if (valor.maiorQue(saldo)) {
            throw new IllegalStateException("Saldo insuficiente para realizar o saque.");
        }

        sacar(valor);
        registrarMovimentacao(TipoMovimentacao.SAQUE, valor);
        aplicarRegraDeTaxa();
    }

    // ==================== TEMPLATE METHOD: DEPÓSITO ====================

    /**
     * Realiza um depósito na conta e registra no histórico.
     */
    public void realizarDeposito(Dinheiro valor) {
        verificarContaAtiva();

        if (valor == null || valor.menorQue(Dinheiro.zero()) || valor.igualA(Dinheiro.zero())) {
            throw new IllegalArgumentException("O valor do depósito deve ser maior que zero.");
        }

        depositar(valor);
        registrarMovimentacao(TipoMovimentacao.DEPOSITO, valor);
    }

    // ==================== MÉTODOS INTERNOS ====================

    private void depositar(Dinheiro valor) {
        this.saldo = this.saldo.somar(valor);
    }

    private void sacar(Dinheiro valor) {
        this.saldo = this.saldo.subtrair(valor);
    }

    protected void registrarMovimentacao(TipoMovimentacao tipo, Dinheiro valor) {
        Movimentacao movimentacao = new Movimentacao(LocalDateTime.now(), valor, tipo);
        movimentacoes.add(movimentacao);
    }

    private void verificarContaAtiva() {
        if (status != StatusConta.ATIVA) {
            throw new IllegalStateException("A conta não está ativa.");
        }
    }

    // ==================== MÉTODO ABSTRATO ====================

    /**
     * Cada subclasse implementa sua própria regra de taxa/rendimento.
     * Chamado ao final do Template Method de saque.
     */
    protected abstract void aplicarRegraDeTaxa();

    // ==================== GETTERS ====================

    public Dinheiro getSaldo() {
        return saldo;
    }

    public Cliente getCliente() {
        return cliente;
    }

    public LocalDate getDataAbertura() {
        return dataAbertura;
    }

    public StatusConta getStatus() {
        return status;
    }

    public ContaAcesso getContaAcesso() {
        return contaAcesso;
    }

    public List<Movimentacao> getMovimentacoes() {
        return new ArrayList<>(movimentacoes);
    }
}
