package model;

import java.math.BigDecimal;
import java.time.format.DateTimeFormatter;
import java.util.*;

public class CalcularSaldoConta {

    public static class SaldoConta {
        private String agencia;
        private String conta;
        private String banco;
        private String titular;
        private BigDecimal saldo;
        private List<Conta> operacoes;

        public SaldoConta( String agencia, String conta, String banco, String titular ) {
            this.agencia = agencia;
            this.conta = conta;
            this.banco = banco;
            this.titular = titular;
            this.saldo = BigDecimal.ZERO;
            this.operacoes = new ArrayList<>();
        }

        public void adicionarOperacoes( Conta operacao ) {
            operacoes.add( operacao );
            if( operacao.getTipoOperacao().equals( "DEPOSITO") ) {
                saldo = saldo.add(operacao.getValor());
            } else if( operacao.getTipoOperacao().equals( "SAQUE") ) {
                saldo = saldo.subtract(operacao.getValor());
            }
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
            return titular;
        }

        public BigDecimal getSaldo() {
            return saldo;
        }

        public List<Conta> getOperacoes() {
            return operacoes;
        }
    }

    public Map<String, SaldoConta> calcularSaldos( List<Conta> operacoes ) {
        Map<String, SaldoConta> saldos = new HashMap<>();

        for( Conta op : operacoes ) {
            String chave = gerarChaveConta( op );

            SaldoConta saldoConta = saldos.get( chave );
            if( saldoConta == null ) {
                saldoConta = new SaldoConta(
                        op.getAgencia(),
                        op.getConta(),
                        op.getBanco(),
                        op.getTitular()
                );
                saldos.put( chave, saldoConta );
            }

            saldoConta.adicionarOperacoes( op );
        }

        return saldos;
    }

    private String gerarChaveConta( Conta conta ) {
        return conta.getAgencia() + "-" + conta.getConta() + "-" + conta.getBanco();
    }

    // Exibir extrato completo
    public void exibirExtratoCompleto( Map<String, SaldoConta> saldos ) {
        System.out.println( "\n" + "=".repeat( 80 ) );
        System.out.println( "EXTRATO BANCÁRIO - SALDO FINAL DA CONTA");
        System.out.println( "=".repeat( 80 ) );

        // Extrato por titular
        List<SaldoConta> listaSaldos = new ArrayList<>( saldos.values() );
        listaSaldos.sort( Comparator.comparing( SaldoConta::getSaldo ) );

        BigDecimal saldoTotalGeral = BigDecimal.ZERO;

        for( SaldoConta sc : listaSaldos ) {
            System.out.println( "\n" + "=".repeat( 80 ) );
            System.out.printf( "Titular: %s\n", sc.getTitular() );
            System.out.printf( "Agencia: %s | Conta: %s | Banco: %s\n",
                    sc.getAgencia(), sc.getConta(), sc.getBanco() );
            System.out.println( "-".repeat( 80 ) );

            // Ordenar operaçoes por data e hora
            List<Conta> operacoesOrdenadas = new ArrayList<>( sc.getOperacoes() );
            operacoesOrdenadas.sort( Comparator.comparing( Conta::getDataHora ) );

            System.out.println( "Histórico de Operações: ");
            BigDecimal saldoParcial = BigDecimal.ZERO;

            // Calcular saldo após cada operação
            for( Conta op : operacoesOrdenadas ) {
                if( op.getTipoOperacao().equals( "DEPOSITO") ) {
                    saldoParcial = saldoParcial.add(op.getValor());
                } else
                    saldoParcial = saldoParcial.subtract(op.getValor());

                System.out.printf( " %s | %-8s | R$ %10.2f | %s\n",
                        op.getDataHora().format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss" ) ),
                        op.getTipoOperacao(),
                        op.getValor(),
                        saldoParcial,
                        op.getTipoOperacao().equals( "DEPOSITO" ) ? "(+)" : "(-)" );
            }

            System.out.printf( "\nSALDO FINAL: R$ %.2f\n", sc.getSaldo() );
            saldoTotalGeral = saldoTotalGeral.add( sc.getSaldo() );
        }

        System.out.println( "\n" + "=".repeat( 80 ) );
        System.out.printf( "SALDO TOTAL GERAL: R$ %.2f", saldoTotalGeral );
        System.out.println( "=".repeat( 80 ) );
    }

    // Extrato Resumido
    public void exibirSaldoResumido( Map<String, SaldoConta> saldos ) {
        System.out.println( "\n".repeat( 80 ) );
        System.out.println( "SALDOS FINAIS POR CONTA");
        System.out.println( "=".repeat( 80 ) );

        List<SaldoConta> listaSaldos = new ArrayList<>( saldos.values() );
        listaSaldos.sort( Comparator.comparing( SaldoConta::getSaldo ) );

        BigDecimal saldoTotalGeral = BigDecimal.ZERO;

        for( SaldoConta sc : listaSaldos ) {
            System.out.printf( "%-10s | Ag: %-4s | Conta: %-4s | Banco: %-9s | Saldo: R$ %10.2f\n",
                    sc.getTitular(),
                    sc.getAgencia(),
                    sc.getConta(),
                    sc.getBanco(),
                    sc.getSaldo() );

            saldoTotalGeral = saldoTotalGeral.add( sc.getSaldo() );
        }

        System.out.println( "=".repeat( 80 ) );
        System.out.printf( "SALDO TOTAL: R$ %.2f\n", saldoTotalGeral );
    }
}
