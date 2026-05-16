package br.com.fiapbank.application;

import br.com.fiapbank.model.*;
import java.util.List;

/**
 * Serviço de orquestração das operações bancárias.
 * Recebe as intenções da camada de apresentação e delega ao modelo.
 */
public class ContaService {

    private Conta conta;

    public ContaService(Conta conta) {
        this.conta = conta;
    }

    public void realizarDeposito(Dinheiro valor) {
        conta.realizarDeposito(valor);
    }

    public void realizarSaque(Dinheiro valor) {
        conta.realizarSaque(valor);
    }

    public Dinheiro obterSaldo() {
        return conta.getSaldo();
    }

    public List<Movimentacao> obterMovimentacoes() {
        return conta.getMovimentacoes();
    }

    public Conta getConta() {
        return conta;
    }
}
