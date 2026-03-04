package model;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Objects;

/**
 * Representa uma transação (operação) bancária.
 * Cada instância corresponde a uma operação específica
 * (saque ou depósito) realizada em uma conta.
 *
 * @author Gilcimar Matias
 * @version 2.0
 */
public class Transacao {

    private String agencia;
    private String conta;
    private String banco;
    private String titular;
    private String tipoOperacao;
    private LocalDateTime dataHora;
    private BigDecimal valor;

    public Transacao(String agencia, String conta, String banco,
                     String titular, String tipoOperacao, LocalDateTime dataHora, BigDecimal valor) {
        this.agencia = agencia;
        this.conta = conta;
        this.banco = banco;
        this.titular = titular;
        this.tipoOperacao = tipoOperacao;
        this.dataHora = dataHora;
        this.valor = valor;
    }

    public String getAgencia() {
        return agencia;
    }

    public String getConta() {
        return conta;
    }

    public String getBanco() {
        return banco;
    }

    public String getTipoOperacao() {
        return tipoOperacao;
    }

    public LocalDateTime getDataHora() {
        return dataHora;
    }

    public String getTitular() {
        return titular;
    }

    public BigDecimal getValor() {
        return valor;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Transacao t = (Transacao) o;
        return Objects.equals(agencia, t.agencia) &&
                Objects.equals(conta, t.conta) &&
                Objects.equals(banco, t.banco) &&
                Objects.equals(titular, t.titular) &&
                Objects.equals(tipoOperacao, t.tipoOperacao) &&
                Objects.equals(dataHora, t.dataHora) &&
                Objects.equals(valor, t.valor);
    }

    @Override
    public int hashCode() {
        return Objects.hash(agencia, conta, banco, titular, tipoOperacao, dataHora, valor);
    }

    @Override
    public String toString() {
        return String.format("Transacao{titular='%s', agencia='%s', conta='%s', banco='%s', " +
                        "operacao='%s', valor=%s, dataHora=%s}",
                titular, agencia, conta, banco, tipoOperacao, valor, dataHora);
    }
}