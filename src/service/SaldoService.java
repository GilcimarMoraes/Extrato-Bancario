package service;

import model.Transacao;

import java.math.BigDecimal;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Classe responsável por processar e calcular os saldos das contas bancárias
 * a partir de uma lista de transações. Gerencia o histórico de operações,
 * valida saques com base no saldo disponível e gera extratos detalhados.
 *
 * @author Gilcimar Matias
 * @version 2.0
 */
public class SaldoService {

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
        private List<Transacao> operacoes;
        private List<String> operacoesRejeitadas;

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
         * Adiciona uma operação à conta, atualizando o saldo se for um depósito
         * ou validando o saldo disponível se for um saque.
         *
         * @param transacao Transação a ser processada
         * @return true se a operação foi aceita, false se foi rejeitada (saque sem saldo)
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
                            transacao.getDataHora().format(
                                    DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss")
                            ),
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

    /**
     * Processa uma lista de transações e calcula os saldos finais de cada conta.
     * As transações são processadas em ordem cronológica para garantir a
     * correta validação dos saques.
     *
     * @param transacoes Lista de transações a serem processadas
     * @return Mapa contendo o saldo final de cada conta (chave = agencia-conta-banco)
     */
    public Map<String, SaldoConta> calcularSaldos(List<Transacao> transacoes) {
        List<Transacao> ordenadas = new ArrayList<>(transacoes);
        ordenadas.sort(Comparator.comparing(Transacao::getDataHora));

        Map<String, SaldoConta> saldos = new LinkedHashMap<>();

        for (Transacao t : ordenadas) {
            String chave = gerarChaveConta(t);

            saldos.computeIfAbsent(chave, k ->
                    new SaldoConta(t.getAgencia(), t.getConta(), t.getBanco(), t.getTitular())
            ).adicionarOperacao(t);
        }

        return saldos;
    }

    /**
     * Gera uma chave única para identificar uma conta.
     *
     * @param transacao Objeto Transacao
     * @return String no formato "agencia-conta-banco"
     */
    private String gerarChaveConta(Transacao transacao) {
        return transacao.getAgencia() + "-" + transacao.getConta() + "-" + transacao.getBanco();
    }

    /**
     * Exibe um extrato completo com todas as operações e saldos parciais
     * de cada conta, incluindo operações rejeitadas.
     *
     * @param saldos Mapa com os saldos calculados
     */
    public void exibirExtratoCompleto(Map<String, SaldoConta> saldos) {
        System.out.println("\n" + "=".repeat(80));
        System.out.println("EXTRATO BANCÁRIO - SALDO FINAL DA CONTA");
        System.out.println("=".repeat(80));

        Map<String, List<SaldoConta>> porTitular = saldos.values().stream()
                .sorted(Comparator.comparing(SaldoConta::getTitular))
                .collect(Collectors.groupingBy(SaldoConta::getTitular, LinkedHashMap::new, Collectors.toList()));

        BigDecimal saldoTotalGeral = BigDecimal.ZERO;
        int totalOperacoesRejeitadas = 0;

        for (Map.Entry<String, List<SaldoConta>> entry : porTitular.entrySet()) {
            for (SaldoConta sc : entry.getValue()) {
                System.out.println("\n" + "=".repeat(80));
                System.out.printf("Titular: %s%n", sc.getTitular());
                System.out.printf("Agência: %s | Conta: %s | Banco: %s%n",
                        sc.getAgencia(), sc.getConta(), sc.getBanco());
                System.out.println("-".repeat(80));

                List<Transacao> operacoesOrdenadas = new ArrayList<>(sc.getOperacoes());
                operacoesOrdenadas.sort(Comparator.comparing(Transacao::getDataHora));

                System.out.println("Histórico de Operações:");
                BigDecimal saldoParcial = BigDecimal.ZERO;

                for (Transacao t : operacoesOrdenadas) {
                    if (t.getTipoOperacao().equals("DEPOSITO")) {
                        saldoParcial = saldoParcial.add(t.getValor());
                        System.out.printf("  %s | %-8s | R$ %-10.2f | Saldo: R$ %-10.2f | (+)%n",
                                t.getDataHora().format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss")),
                                t.getTipoOperacao(),
                                t.getValor(),
                                saldoParcial);
                    } else {
                        saldoParcial = saldoParcial.subtract(t.getValor());
                        System.out.printf("  %s | %-8s | R$ %-10.2f | Saldo: R$ %-10.2f | (-)%n",
                                t.getDataHora().format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss")),
                                t.getTipoOperacao(),
                                t.getValor(),
                                saldoParcial);
                    }
                }

                if (sc.hasOperacoesRejeitadas()) {
                    System.out.println("\n  OPERAÇÕES REJEITADAS (Saldo Insuficiente):");
                    for (String rejeicao : sc.getOperacoesRejeitadas()) {
                        System.out.println("  - " + rejeicao);
                        totalOperacoesRejeitadas++;
                    }
                }

                System.out.printf("%nSALDO FINAL: R$ %.2f%n", sc.getSaldo());
                saldoTotalGeral = saldoTotalGeral.add(sc.getSaldo());
            }
        }

        System.out.println("\n" + "=".repeat(80));
        System.out.println("RESUMO FINAL:");
        System.out.printf("  Saldo total geral: R$ %.2f%n", saldoTotalGeral);
        System.out.printf("  Total de operações rejeitadas: %d%n", totalOperacoesRejeitadas);
        System.out.println("=".repeat(80));
    }

    /**
     * Exibe um resumo com apenas os saldos finais de cada conta.
     *
     * @param saldos Mapa com os saldos calculados
     */
    public void exibirSaldoResumido(Map<String, SaldoConta> saldos) {
        System.out.println("\n" + "=".repeat(80));
        System.out.println("SALDOS FINAIS POR CONTA");
        System.out.println("=".repeat(80));

        List<SaldoConta> listaSaldos = new ArrayList<>(saldos.values());
        listaSaldos.sort(Comparator.comparing(SaldoConta::getTitular));

        BigDecimal saldoTotalGeral = BigDecimal.ZERO;
        int totalContasComRejeicao = 0;

        for (SaldoConta sc : listaSaldos) {
            String marcadorRejeicao = sc.hasOperacoesRejeitadas() ? " [!]" : "";
            if (sc.hasOperacoesRejeitadas()) {
                totalContasComRejeicao++;
            }
            System.out.printf("%-10s | Ag: %-4s | Conta: %-4s | Banco: %-9s | Saldo: R$ %10.2f%s%n",
                    sc.getTitular(),
                    sc.getAgencia(),
                    sc.getConta(),
                    sc.getBanco(),
                    sc.getSaldo(),
                    marcadorRejeicao);

            saldoTotalGeral = saldoTotalGeral.add(sc.getSaldo());
        }

        System.out.println("=".repeat(80));
        System.out.printf("SALDO TOTAL GERAL: R$ %.2f%n", saldoTotalGeral);
        if (totalContasComRejeicao > 0) {
            System.out.printf("! %d conta(s) tiveram saques rejeitados por saldo insuficiente%n",
                    totalContasComRejeicao);
        }
    }
}