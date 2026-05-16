package br.com.fiapbank.application;

import br.com.fiapbank.infrastructure.ContaRepository;
import br.com.fiapbank.model.*;
import br.com.fiapbank.presentation.TerminalBancarioController;

/**
 * Ponto de entrada da aplicação.
 * Orquestra a inicialização das camadas: presentation → application → model.
 */
public class Main {

    public static void main(String[] args) throws InterruptedException {

        // ========== APRESENTAÇÃO: Cadastro do usuário ==========

        // Controller temporário apenas para exibir telas de cadastro (sem conta ainda)
        TerminalBancarioController telaInicial =
                new TerminalBancarioController(null, null);

        // Coleta nome completo
        String nomeCompleto = telaInicial.exibirTelaCadastroNome();
        Cliente cliente = new Cliente(nomeCompleto);

        // Coleta e valida senha
        String senhaValida = telaInicial.exibirTelaCadastroSenha(cliente.obterPrimeiroNome());

        // Escolha do tipo de conta
        String tipoConta;
        while (true) {
            tipoConta = telaInicial.exibirTelaEscolhaTipoConta();
            if (tipoConta.equals("1") || tipoConta.equals("2")) break;
            System.out.println("Opcao invalida! Escolha 1 ou 2.");
        }

        // ========== MODEL: Criação das entidades via Factory ==========

        ContaAcesso contaAcesso = new ContaAcesso(senhaValida);
        ContaFactory factory = ContaFactory.getInstance();
        Conta conta;

        if (tipoConta.equals("1")) {
            conta = factory.criarContaCorrente(cliente, contaAcesso, Dinheiro.zero());
        } else {
            conta = factory.criarContaPoupanca(cliente, contaAcesso, Dinheiro.zero());
        }

        // ========== INFRA: Persiste a conta em memória ==========

        ContaRepository.getInstance().salvar(conta);

        // ========== APPLICATION: Serviços de orquestração ==========

        ContaService contaService = new ContaService(conta);
        AutorizacaoService autorizacaoService = new AutorizacaoService(conta);

        // ========== APRESENTAÇÃO: Login e menu principal ==========

        TerminalBancarioController terminal =
                new TerminalBancarioController(contaService, autorizacaoService);

        Boolean autenticado = terminal.exibirTelaLogin();

        if (!autenticado) {
            return;
        }

        terminal.exibirMenuPrincipal(cliente.obterPrimeiroNome());
    }
}
