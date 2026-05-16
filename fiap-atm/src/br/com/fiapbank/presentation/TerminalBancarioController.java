package br.com.fiapbank.presentation;

import br.com.fiapbank.application.AutorizacaoService;
import br.com.fiapbank.application.ContaService;
import br.com.fiapbank.model.Dinheiro;
import br.com.fiapbank.model.Movimentacao;
import br.com.fiapbank.model.TipoMovimentacao;

import java.math.BigDecimal;
import java.util.List;
import java.util.Scanner;

/**
 * Camada de Apresentação — Controller do Terminal Bancário.
 * Responsável por toda a interface com o usuário: menus, inputs e outputs.
 * Nenhuma regra de negócio reside aqui.
 */
public class TerminalBancarioController {

    private ContaService contaService;
    private AutorizacaoService autorizacaoService;
    private Scanner scanner;

    public TerminalBancarioController(ContaService contaService, AutorizacaoService autorizacaoService) {
        this.contaService = contaService;
        this.autorizacaoService = autorizacaoService;
        this.scanner = new Scanner(System.in);
    }

    // ==================== MÉTODOS AUXILIARES DE UI ====================

    public void digitar(String texto, Integer delay) throws InterruptedException {
        for (char c : texto.toCharArray()) {
            System.out.print(c);
            Thread.sleep(delay);
        }
        System.out.println();
    }

    public void limparTerminal() {
        try {
            if (System.getProperty("os.name").contains("Windows")) {
                new ProcessBuilder("cmd", "/c", "cls").inheritIO().start().waitFor();
            } else {
                System.out.print("\033[H\033[2J");
                System.out.flush();
            }
        } catch (Exception e) {
            for (Integer i = 0; i < 50; i++) {
                System.out.println();
            }
        }
    }

    public void exibirLogo() throws InterruptedException {
        String[] logo = {
            "",
            "    █████╗ ████████╗███╗   ███╗    ███████╗██╗ █████╗ ██████╗ ",
            "   ██╔══██╗╚══██╔══╝████╗ ████║    ██╔════╝██║██╔══██╗██╔══██╗",
            "   ███████║   ██║   ██╔████╔██║    █████╗  ██║███████║██████╔╝",
            "   ██╔══██║   ██║   ██║╚██╔╝██║    ██╔══╝  ██║██╔══██║██╔═══╝ ",
            "   ██║  ██║   ██║   ██║ ╚═╝ ██║    ██║     ██║██║  ██║██║     ",
            "   ╚═╝  ╚═╝   ╚═╝   ╚═╝     ╚═╝    ╚═╝     ╚═╝╚═╝  ╚═╝╚═╝     ",
            "",
            "   =======================================================",
            "   ============ SISTEMA DE AUTOMACAO BANCARIA ============",
            "   =======================================================",
            ""
        };

        for (String linha : logo) {
            digitar(linha, 3);
            Thread.sleep(30);
        }
        Thread.sleep(800);
    }

    // ==================== CADASTRO E LOGIN ====================

    /**
     * Exibe tela de boas-vindas e retorna o nome completo digitado.
     */
    public String exibirTelaCadastroNome() throws InterruptedException {
        limparTerminal();

        digitar("=============================================================", 20);
        digitar("                   BANCO FIAP DIGITAL                      ", 20);
        digitar("=============================================================", 20);

        Thread.sleep(500);
        System.out.println();

        digitar("Inicializando sistema", 40);
        System.out.print(".");
        Thread.sleep(200);
        System.out.print(".");
        Thread.sleep(200);
        System.out.print(".");
        Thread.sleep(200);
        System.out.println();

        Thread.sleep(500);
        exibirLogo();
        Thread.sleep(500);

        System.out.print("\nDigite seu nome completo: ");
        return scanner.nextLine().trim();
    }

    /**
     * Solicita e valida uma senha forte. Retorna a senha válida cadastrada.
     */
    public String exibirTelaCadastroSenha(String primeiroNome) throws InterruptedException {
        System.out.println();
        digitar("Bem-Vindo " + primeiroNome + "!", 50);
        Thread.sleep(500);

        // Regex: mínimo 8 chars, 1 número, 1 maiúscula, 1 especial
        String regexSenha = "^(?=.*[0-9])(?=.*[A-Z])(?=.*[!@#$%¨&*()\\-_+=?><]).{8,}$";

        System.out.println("\n-------------------------------------------------------------");
        System.out.println("           REQUISITOS PARA SENHA FORTE:               ");
        System.out.println("  - Minimo de 8 caracteres                            ");
        System.out.println("  - Pelo menos 1 numero                               ");
        System.out.println("  - Pelo menos 1 letra MAIUSCULA                      ");
        System.out.println("  - Pelo menos 1 caractere especial (!@#$%¨&* etc)   ");
        System.out.println("-------------------------------------------------------------");

        String senha;
        while (true) {
            System.out.print("\nCadastre uma senha forte: ");
            senha = scanner.nextLine();

            if (senha.matches(regexSenha)) {
                digitar("Senha cadastrada com sucesso!", 40);
                break;
            } else {
                System.out.println("Senha invalida! Tente novamente.");
                System.out.println("   Lembre-se: 8+ caracteres, 1 numero, 1 maiuscula, 1 caractere especial\n");
            }
        }

        return senha;
    }

    /**
     * Exibe o tipo de conta e solicita escolha. Retorna "1" para Corrente ou "2" para Poupanca.
     */
    public String exibirTelaEscolhaTipoConta() {
        System.out.println("\n-------------------------------------------------------------");
        System.out.println("               ESCOLHA O TIPO DE CONTA:                    ");
        System.out.println("  [1]  CONTA CORRENTE  (taxa de R$ 25,00 por saque)         ");
        System.out.println("  [2]  CONTA POUPANCA  (rendimento de 1,1% apos saques)     ");
        System.out.println("-------------------------------------------------------------");
        System.out.print("Escolha uma opcao: ");
        return scanner.nextLine().trim();
    }

    /**
     * Realiza o fluxo de login. Retorna true se autenticado com sucesso.
     */
    public Boolean exibirTelaLogin() throws InterruptedException {
        Thread.sleep(1000);
        System.out.println("\nREALIZE O LOGIN PARA ACESSAR O SISTEMA");

        while (!autorizacaoService.isBloqueado()) {
            System.out.print("Digite sua senha para login: ");
            String tentativa = scanner.nextLine();

            if (autorizacaoService.autorizar(tentativa)) {
                return true;
            } else {
                if (autorizacaoService.isBloqueado()) {
                    break;
                }
                System.out.println("Senha incorreta! Tentativas restantes: "
                        + autorizacaoService.getTentativasRestantes());
            }
        }

        System.out.println("\n=============================================================");
        System.out.println("                    ACESSO BLOQUEADO!                     ");
        System.out.println("          Numero maximo de tentativas excedido.          ");
        System.out.println("=============================================================");
        return false;
    }

    // ==================== MENU PRINCIPAL ====================

    public void exibirMenuPrincipal(String primeiroNome) throws InterruptedException {
        limparTerminal();

        System.out.println();
        digitar("=============================================================", 30);
        digitar("                     LOGIN REALIZADO!                     ", 30);
        digitar("              BEM-VINDO AO SEU BANCO DIGITAL              ", 30);
        digitar("=============================================================", 30);

        Thread.sleep(1000);
        exibirLogo();

        System.out.println();
        digitar("Ola " + primeiroNome + "! Seu acesso foi liberado com sucesso.", 45);
        digitar("Como podemos ajudar voce hoje?", 45);
        Thread.sleep(1000);

        Integer opcao;

        do {
            System.out.println("\n-------------------------------------------------------------");
            System.out.println("                      MENU PRINCIPAL                      ");
            System.out.println("-------------------------------------------------------------");
            System.out.println("  [1]  CONSULTAR SALDO                                     ");
            System.out.println("  [2]  FAZER DEPOSITO                                      ");
            System.out.println("  [3]  FAZER SAQUE                                         ");
            System.out.println("  [4]  HISTORICO DE MOVIMENTACOES                          ");
            System.out.println("  [5]  SAIR                                                ");
            System.out.println("-------------------------------------------------------------");
            System.out.print("Escolha uma opcao: ");

            opcao = lerInteiroSeguro();

            switch (opcao) {
                case 1:
                    exibirSaldo();
                    break;
                case 2:
                    realizarDeposito();
                    break;
                case 3:
                    realizarSaque();
                    break;
                case 4:
                    exibirMovimentacoes();
                    break;
                case 5:
                    System.out.println("\n-------------------------------------------------------------");
                    System.out.println("    O Banco FIAP agradece sua preferencia!                ");
                    System.out.println("              Volte sempre!                                ");
                    System.out.println("-------------------------------------------------------------");
                    break;
                default:
                    System.out.println("Opcao invalida! Por favor, escolha uma opcao entre 1 e 5.");
            }

        } while (opcao != 5);

        System.out.println("\nSistema encerrado. Ate mais!");
        scanner.close();
    }

    // ==================== OPERAÇÕES DO MENU ====================

    public void exibirSaldo() {
        System.out.println("\n=============================================================");
        System.out.println("                    CONSULTA DE SALDO                        ");
        System.out.println("=============================================================");
        System.out.printf("%n SALDO ATUAL: %s%n", contaService.obterSaldo().toString());
        System.out.println("\n=============================================================");
    }

    public void realizarDeposito() {
        System.out.print("\nDigite o valor do deposito: R$ ");
        Double valorDigitado = lerDoubleSeguro();

        if (valorDigitado == null) {
            System.out.println("Valor invalido!");
            return;
        }

        try {
            Dinheiro valor = new Dinheiro(BigDecimal.valueOf(valorDigitado));
            contaService.realizarDeposito(valor);
            System.out.printf("Deposito de R$ %.2f realizado com sucesso!%n", valorDigitado);
            System.out.printf("Novo saldo: %s%n", contaService.obterSaldo().toString());
        } catch (IllegalArgumentException e) {
            System.out.println("Erro: " + e.getMessage());
        }
    }

    public void realizarSaque() {
        System.out.print("\nDigite o valor para saque: R$ ");
        Double valorDigitado = lerDoubleSeguro();

        if (valorDigitado == null) {
            System.out.println("Valor invalido!");
            return;
        }

        try {
            Dinheiro valor = new Dinheiro(BigDecimal.valueOf(valorDigitado));
            contaService.realizarSaque(valor);
            System.out.printf("Saque de R$ %.2f realizado com sucesso!%n", valorDigitado);
            System.out.printf("Novo saldo: %s%n", contaService.obterSaldo().toString());
        } catch (IllegalArgumentException | IllegalStateException e) {
            System.out.println("Erro: " + e.getMessage());
        }
    }

    public void exibirMovimentacoes() {
        List<Movimentacao> movimentacoes = contaService.obterMovimentacoes();

        System.out.println("\n=============================================================");
        System.out.println("                HISTORICO DE MOVIMENTACOES                   ");
        System.out.println("=============================================================");

        if (movimentacoes.isEmpty()) {
            System.out.println("\n  Nenhuma movimentacao realizada ainda.");
            System.out.println("  Realize depositos ou saques para ver o historico.");
        } else {
            System.out.println("\n  TIPO           VALOR          DATA E HORA");
            System.out.println("  --------------------------------------------------");

            for (Movimentacao m : movimentacoes) {
                System.out.println("  " + m.toString());
            }

            System.out.println("\n  Total de movimentacoes: " + movimentacoes.size());
        }

        System.out.println("\n=============================================================");
    }

    // ==================== LEITURA SEGURA DE INPUTS ====================

    private Integer lerInteiroSeguro() {
        try {
            String linha = scanner.nextLine().trim();
            return Integer.parseInt(linha);
        } catch (NumberFormatException e) {
            return -1;
        }
    }

    private Double lerDoubleSeguro() {
        try {
            String linha = scanner.nextLine().trim().replace(",", ".");
            return Double.parseDouble(linha);
        } catch (NumberFormatException e) {
            return null;
        }
    }
}
