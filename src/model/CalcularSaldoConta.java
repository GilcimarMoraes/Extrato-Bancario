    package model;

import java.math.BigDecimal;
import java.time.format.DateTimeFormatter;
import java.util.*;


/**
* Classe responsável por processar e calcular os saldos das contas bancárias
 * a partir de uma lista de operações. Gerencia o histórico de transações,
 * valida saques com base no saldo disponível e gera extratos detalhados.
 *
 * @author Gilcimar Matias
 * @version 1.0
 */

public class CalcularSaldoConta {

    /**
     * Classe interna que representa o estado atual de uma conta bancária,
     * incluindo saldo, histórico de operações e operações rejeitadas.
     */

    public static class SaldoConta {
        private String agencia;
        private String conta;
        private String banco;
        private String titular;
        private BigDecimal saldo;
        private List<Conta> operacoes;
        private List<String> operacoesRejeitadas;

        public SaldoConta( String agencia, String conta, String banco, String titular ) {
            this.agencia = agencia;
            this.conta = conta;
            this.banco = banco;
            this.titular = titular;
            this.saldo = BigDecimal.ZERO;
            this.operacoes = new ArrayList<>();
            this.operacoesRejeitadas = new ArrayList<>();
        }

        /**
         * Adiciona uma operação à conta, atualizando o saldo se for um depósito
         * ou validando o saldo disponível se for um saque.
         *
         * @param operacao Operação a ser processada
         * @return true se a operação foi aceita, false se foi rejeitada (saque sem saldo)
         */

        public boolean adicionarOperacao( Conta operacao ) {
            if( operacao.getTipoOperacao().equals( "DEPOSITO" ) ) {
                //Deposito sempre é permitido
                saldo = saldo.add(operacao.getValor());
                operacoes.add( operacao );
                return true;

            } else if(operacao.getTipoOperacao().equals( "SAQUE" ) ) {
                if( saldo.compareTo(operacao.getValor()) >= 0 ) {
                    saldo = saldo.subtract(operacao.getValor());
                    operacoes.add( operacao );
                    return true;

                } else {
                    String rejeicao = String.format( "SAQUE REJEITADO: R$ %.2f em %s - Saldo " +
                                    "disponível: R$ %.2f",
                            operacao.getValor(),
                            operacao.getDataHora().format(DateTimeFormatter.ofPattern(
                                    "dd/MM/yyyy HH:mm:ss"
                            )),
                            saldo );

                    operacoesRejeitadas.add( rejeicao );

                    return false;
                }
            }

            return false;
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

        public List<String>  getOperacoesRejeitadas() { return operacoesRejeitadas; }

        public boolean hasOperacoesRejeitadas() { return !operacoesRejeitadas.isEmpty(); }
    }

    /**
     * Processa uma lista de operações e calcula os saldos finais de cada conta.
     * As operações são processadas em ordem cronológica para garantir a
     * correta validação dos saques.
     *
     * @param operacoes Lista de operações a serem processadas
     * @return Mapa contendo o saldo final de cada conta (chave = agencia-conta-banco)
     */

    public Map<String, SaldoConta> calcularSaldos( List<Conta> operacoes ) {
        List<Conta> operacoesOrdenadas = new ArrayList<>( operacoes);
        operacoesOrdenadas.sort( Comparator.comparing(Conta::getDataHora) );

        Map<String, SaldoConta> saldos = new HashMap<>();
        Map<String, List<String>> errosPorConta = new HashMap<>();

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

            boolean operacaoAceita = saldoConta.adicionarOperacao( op );

            if( !operacaoAceita ) {
                errosPorConta.computeIfAbsent( chave, k -> new ArrayList<>() )
                        .add( String.format( "Linha com SAQUE de R$ %.2f em %s rejeitado - " +
                                "saldo insuficiente",
                                op.getValor(),
                                op.getDataHora() ) );
            }
        }

        // Alerta de operações rejeitadas
        if( !errosPorConta.isEmpty() ) {
            System.out.println( "\n" + "!".repeat( 80 ) );
            System.out.println( "ALERTAS DE OPERAÇÕES REJEITADAS" );
            System.out.println( "!".repeat( 80 ) );

            for( Map.Entry<String, List<String>> entry : errosPorConta.entrySet() ) {
                System.out.println( "Conta: " + entry.getKey());
                for( String erro : entry.getValue() ) {
                    System.out.println( "  - " + erro );
                }
            }
        }

        return saldos;
    }

    /**
     * Gera uma chave única para identificar uma conta.
     *
     * @param conta Objeto Conta
     * @return String no formato "agencia-conta-banco"
     */

    private String gerarChaveConta( Conta conta ) {
        return conta.getAgencia() + "-" + conta.getConta() + "-" + conta.getBanco();
    }

    /**
     * Exibe um extrato completo com todas as operações e saldos parciais
     * de cada conta, incluindo operações rejeitadas.
     *
     * @param saldos Mapa com os saldos calculados
     */

    public void exibirExtratoCompleto( Map<String, SaldoConta> saldos ) {
        System.out.println( "\n" + "=".repeat( 80 ) );
        System.out.println( "EXTRATO BANCÁRIO - SALDO FINAL DA CONTA");
        System.out.println( "=".repeat( 80 ) );

        // Extrato por titular
        List<SaldoConta> listaSaldos = new ArrayList<>( saldos.values() );
        listaSaldos.sort( Comparator.comparing( SaldoConta::getTitular ) );

        BigDecimal saldoTotalGeral = BigDecimal.ZERO;
        int totalOperacoesComRejeicao = 0;

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
                    System.out.printf( " %s | %-8s | R$ %-10.2f | Saldo: R$ %-10.2f | %s\n",
                            op.getDataHora().format( DateTimeFormatter.ofPattern(
                                    "dd/MM/yyyy HH:mm:ss"
                            )),
                            op.getTipoOperacao(),
                            op.getValor(),
                            saldoParcial,
                            "(+)");
                } else {
                    saldoParcial = saldoParcial.subtract(op.getValor());

                    System.out.printf( " %s | %-8s | R$ %10.2f | %s\n",
                            op.getDataHora().format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss" ) ),
                            op.getTipoOperacao(),
                            op.getValor(),
                            saldoParcial,
                            "(-)" );

                }
            }

            if( sc.hasOperacoesRejeitadas() ) {
                System.out.println( "\n OPERAÇÕES REJEITADAS (Saldo Insuficiente): " );
                for( String rejeicao : sc.getOperacoesRejeitadas()) {
                    System.out.println( "- " + rejeicao );
                    totalOperacoesComRejeicao++;
                }
            }

            System.out.printf( "\nSALDO FINAL: R$ %.2f\n", sc.getSaldo() );
            saldoTotalGeral = saldoTotalGeral.add( sc.getSaldo() );
        }

        System.out.println( "\n" + "=".repeat( 80 ) );
        System.out.printf("RESUMO FINAL:\n");
        System.out.printf("  Saldo total geral: R$ %.2f\n", saldoTotalGeral);
        System.out.printf("  Total de operações rejeitadas: %d\n", totalOperacoesComRejeicao);
        System.out.println("=".repeat(100));
    }

    /**
     * Exibe um resumo com apenas os saldos finais de cada conta.
     *
     * @param saldos Mapa com os saldos calculados
     */

    public void exibirSaldoResumido( Map<String, SaldoConta> saldos ) {
        System.out.println( "\n".repeat( 80 ) );
        System.out.println( "SALDOS FINAIS POR CONTA");
        System.out.println( "=".repeat( 80 ) );

        List<SaldoConta> listaSaldos = new ArrayList<>( saldos.values() );
        listaSaldos.sort( Comparator.comparing( SaldoConta::getTitular ) );

        BigDecimal saldoTotalGeral = BigDecimal.ZERO;
        int totalOperacoesComRejeicao = 0;

        for( SaldoConta sc : listaSaldos ) {
            String rejeicao = sc.hasOperacoesRejeitadas() ? " - " : "";
            if( sc.hasOperacoesRejeitadas() ) {
                totalOperacoesComRejeicao++;
            }
            System.out.printf( "%-10s | Ag: %-4s | Conta: %-4s | Banco: %-9s | Saldo: R$ %10.2f\n",
                    sc.getTitular(),
                    sc.getAgencia(),
                    sc.getConta(),
                    sc.getBanco(),
                    sc.getSaldo(),
                    rejeicao );

            saldoTotalGeral = saldoTotalGeral.add( sc.getSaldo() );
        }

        System.out.println( "=".repeat( 80 ) );
        System.out.printf("SALDO TOTAL GERAL: R$ %.2f\n", saldoTotalGeral);
        if (totalOperacoesComRejeicao > 0) {
            System.out.printf("! %d conta(s) tiveram saques rejeitados por saldo insuficiente\n",
                    totalOperacoesComRejeicao);
        }
    }
}
