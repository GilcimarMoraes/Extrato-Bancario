package model;

import java.math.BigDecimal;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

/**
 * Representa o estado atual de uma conta bancária.
 * Responsabilidade única: manter saldo, histórico de operações e rejeições.
 *
 * @author Gilcimar Matias
 * @version 3.0
 */
public class SaldoConta {

    private final String agencia;
    private final String conta;
    private final String banco;
    private final String titular;
    private BigDecimal saldo;
    private final List<Transacao> operacoes;
    private final List<String> operacoesRejeitadas;

    public SaldoConta(String agencia, String conta, String banco, String titular) {
        this.agencia = agencia;
        this.conta = conta;
        this.banco = banco;
        this.titular = titular;
        this.saldo = BigDecimal.ZERO;
        this.operacoes = new ArrayList<>();
        this.operacoesRejeitadas = new ArrayList<>();
    }

    /**
     * Adiciona uma operação à conta, atualizando o saldo.
     * Depósito sempre é aceito. Saque é rejeitado se saldo insuficiente.
     *
     * @param transacao Transação a ser processada
     * @return true se aceita, false se rejeitada
     */
    public boolean adicionarOperacao(Transacao transacao) {
        if (transacao.getTipoOperacao().equals("DEPOSITO")) {
            saldo = saldo.add(transacao.getValor());
            operacoes.add(transacao);
            return true;

        } else if (transacao.getTipoOperacao().equals("SAQUE")) {
            if (saldo.compareTo(transacao.getValor()) >= 0) {
                saldo = saldo.subtract(transacao.getValor());
                operacoes.add(transacao);
                return true;
            } else {
                String rejeicao = String.format(
                        "SAQUE REJEITADO: R$ %.2f em %s - Saldo disponível: R$ %.2f",
                        transacao.getValor(),
                        transacao.getDataHora().format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss")),
                        saldo
                );
                operacoesRejeitadas.add(rejeicao);
                return false;
            }
        }
        return false;
    }

    public String getAgencia() { return agencia; }
    public String getConta() { return conta; }
    public String getBanco() { return banco; }
    public String getTitular() { return titular; }
    public BigDecimal getSaldo() { return saldo; }
    public List<Transacao> getOperacoes() { return operacoes; }
    public List<String> getOperacoesRejeitadas() { return operacoesRejeitadas; }
    public boolean hasOperacoesRejeitadas() { return !operacoesRejeitadas.isEmpty(); }
}