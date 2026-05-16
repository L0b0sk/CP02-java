package br.com.fiapbank.application;

import br.com.fiapbank.model.Conta;
import br.com.fiapbank.model.ContaAcesso;

/**
 * Serviço de autorização de acesso ao sistema.
 * Delega a validação de senha para a ContaAcesso dentro da Conta.
 */
public class AutorizacaoService {

    private Conta conta;

    public AutorizacaoService(Conta conta) {
        this.conta = conta;
    }

    /**
     * Tenta autorizar o acesso com a senha informada.
     *
     * @param senha Senha digitada pelo usuário
     * @return true se a senha for válida e a conta não estiver bloqueada
     */
    public Boolean autorizar(String senha) {
        return conta.getContaAcesso().validarSenha(senha);
    }

    public Boolean isBloqueado() {
        return conta.getContaAcesso().isBloqueado();
    }

    public Integer getTentativasRestantes() {
        return ContaAcesso.MAXIMO_TENTATIVAS - conta.getContaAcesso().getTentativas();
    }
}
