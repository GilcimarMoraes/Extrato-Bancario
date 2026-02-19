package model;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Objects;

public class Conta {

    private String agencia;
    private String conta;
    private String banco;
    private String titular;
    private String tipoOperacao;
    private LocalDateTime dataHora;
    private BigDecimal valor;

    public Conta( String agencia, String conta, String banco,
                  String titular, String tipoOperacao, LocalDateTime dataHora, BigDecimal valor ) {
        this.agencia = agencia;
        this.conta = conta;
        this.banco = banco;
        this.titular = titular;
        this.tipoOperacao = tipoOperacao;
        this.dataHora = dataHora;;
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

    public BigDecimal getValor() { return valor; }


    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        Conta conta1 = (Conta) o;
        return Objects.equals(
                agencia, conta1.agencia) &&
                Objects.equals(conta, conta1.conta) &&
                Objects.equals(banco, conta1.banco) &&
                Objects.equals(titular, conta1.titular) &&
                Objects.equals(tipoOperacao, conta1.tipoOperacao) &&
                Objects.equals(dataHora, conta1.dataHora);
    }

    @Override
    public int hashCode() {
        return Objects.hash(agencia, conta, banco, titular, tipoOperacao, dataHora);
    }
}
