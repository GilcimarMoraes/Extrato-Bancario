package model;

import java.time.LocalDateTime;
import java.util.Objects;

public class Conta {

    private String agencia;
    private String conta;
    private String banco;
    private String Titular;
    private String TipoOperacao;
    private LocalDateTime dataHora;

    public Conta( String agencia, String conta, String banco,
                  String Titular, String TipoOperacao, LocalDateTime dataHora) {
        this.agencia = agencia;
        this.conta = conta;
        this.banco = banco;
        this.Titular = Titular;
        this.TipoOperacao = TipoOperacao;
        this.dataHora = dataHora;;
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
        return TipoOperacao;
    }

    public LocalDateTime getDataHora() {
        return dataHora;
    }

    public String getTitular() {
        return Titular;
    }


    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        Conta conta1 = (Conta) o;
        return Objects.equals(
                agencia, conta1.agencia) &&
                Objects.equals(conta, conta1.conta) &&
                Objects.equals(banco, conta1.banco) &&
                Objects.equals(Titular, conta1.Titular);
    }

    @Override
    public int hashCode() {
        return Objects.hash(agencia, conta, banco, Titular);
    }
}
