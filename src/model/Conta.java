package model;

import java.math.BigDecimal;
import java.util.Objects;

public class Conta {

    private String agencia;
    private String conta;
    private String banco;
    private String Titular;
    private String tipoOperacao;
    private BigDecimal saldo;

    public Conta( String agencia, String conta, String banco,
                  String Titular, String tipoOperacao, BigDecimal saldo) {
        this.agencia = agencia;
        this.conta = conta;
        this.banco = banco;
        this.Titular = Titular;
        this.tipoOperacao = tipoOperacao;
        this.saldo = BigDecimal.ZERO;
    }

    public void depositar( BigDecimal valor ) {
        this.saldo = this.saldo.add(valor);
    }

    public void sacar( BigDecimal valor ) {
        this.saldo = this.saldo.subtract(valor);
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

    public String getTitular() {
        return Titular;
    }

    public String getTipoOperacao() {
        return tipoOperacao;
    }

    public BigDecimal getSaldo() {
        return saldo;
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        Conta conta1 = (Conta) o;
        return Objects.equals(
                agencia, conta1.agencia) &&
                Objects.equals(conta, conta1.conta) &&
                Objects.equals(banco, conta1.banco) &&
                Objects.equals(Titular, conta1.Titular) &&
                Objects.equals(tipoOperacao, conta1.tipoOperacao) &&
                Objects.equals(saldo, conta1.saldo);
    }

    @Override
    public int hashCode() {
        return Objects.hash(agencia, conta, banco, Titular, tipoOperacao, saldo);
    }
}
