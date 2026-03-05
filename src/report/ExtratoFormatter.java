package report;

import model.SaldoConta;
import model.Transacao;

import java.math.BigDecimal;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Responsabilidade única: formatar e exibir extratos bancários.
 * Não calcula saldos, apenas apresenta os dados já processados.
 *
 * @author Gilcimar Matias
 * @version 3.0
 */
public class ExtratoFormatter {

    private static final DateTimeFormatter FMT = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss");

    /**
     * Exibe extrato completo com histórico de operações e saldos parciais.
     *
     * @param saldos Mapa com os saldos calculados
     */
    public void exibirCompleto(Map<String, SaldoConta> saldos) {
        System.out.println("\n" + "=".repeat(80));
        System.out.println("EXTRATO BANCÁRIO - SALDO FINAL DA CONTA");
        System.out.println("=".repeat(80));

        // Agrupar por titular
        Map<String, List<SaldoConta>> porTitular = saldos.values().stream()
                .sorted(Comparator.comparing(SaldoConta::getTitular))
                .collect(Collectors.groupingBy(SaldoConta::getTitular,
                        LinkedHashMap::new, Collectors.toList()));

        BigDecimal saldoTotalGeral = BigDecimal.ZERO;
        int totalRejeitadas = 0;

        for (Map.Entry<String, List<SaldoConta>> entry : porTitular.entrySet()) {
            for (SaldoConta sc : entry.getValue()) {
                System.out.println("\n" + "=".repeat(80));
                System.out.printf("Titular: %s%n", sc.getTitular());
                System.out.printf("Agência: %s | Conta: %s | Banco: %s%n",
                        sc.getAgencia(), sc.getConta(), sc.getBanco());
                System.out.println("-".repeat(80));

                List<Transacao> ops = new ArrayList<>(sc.getOperacoes());
                ops.sort(Comparator.comparing(Transacao::getDataHora));

                System.out.println("Histórico de Operações:");
                BigDecimal saldoParcial = BigDecimal.ZERO;

                for (Transacao t : ops) {
                    String sinal;
                    if (t.getTipoOperacao().equals("DEPOSITO")) {
                        saldoParcial = saldoParcial.add(t.getValor());
                        sinal = "(+)";
                    } else {
                        saldoParcial = saldoParcial.subtract(t.getValor());
                        sinal = "(-)";
                    }
                    System.out.printf("  %s | %-8s | R$ %-10.2f | Saldo: R$ %-10.2f | %s%n",
                            t.getDataHora().format(FMT),
                            t.getTipoOperacao(),
                            t.getValor(),
                            saldoParcial,
                            sinal);
                }

                if (sc.hasOperacoesRejeitadas()) {
                    System.out.println("\n  OPERAÇÕES REJEITADAS (Saldo Insuficiente):");
                    for (String rejeicao : sc.getOperacoesRejeitadas()) {
                        System.out.println("  - " + rejeicao);
                        totalRejeitadas++;
                    }
                }

                System.out.printf("%nSALDO FINAL: R$ %.2f%n", sc.getSaldo());
                saldoTotalGeral = saldoTotalGeral.add(sc.getSaldo());
            }
        }

        exibirResumoFinal(saldoTotalGeral, totalRejeitadas);
    }

    /**
     * Exibe apenas os saldos finais de cada conta (modo resumido).
     *
     * @param saldos Mapa com os saldos calculados
     */
    public void exibirResumido(Map<String, SaldoConta> saldos) {
        System.out.println("\n" + "=".repeat(80));
        System.out.println("SALDOS FINAIS POR CONTA");
        System.out.println("=".repeat(80));

        List<SaldoConta> lista = new ArrayList<>(saldos.values());
        lista.sort(Comparator.comparing(SaldoConta::getTitular));

        BigDecimal saldoTotalGeral = BigDecimal.ZERO;
        int contasComRejeicao = 0;

        for (SaldoConta sc : lista) {
            String marcador = sc.hasOperacoesRejeitadas() ? " [!]" : "";
            if (sc.hasOperacoesRejeitadas()) contasComRejeicao++;

            System.out.printf("%-10s | Ag: %-4s | Conta: %-4s | Banco: %-9s | Saldo: R$ %10.2f%s%n",
                    sc.getTitular(),
                    sc.getAgencia(),
                    sc.getConta(),
                    sc.getBanco(),
                    sc.getSaldo(),
                    marcador);

            saldoTotalGeral = saldoTotalGeral.add(sc.getSaldo());
        }

        System.out.println("=".repeat(80));
        System.out.printf("SALDO TOTAL GERAL: R$ %.2f%n", saldoTotalGeral);
        if (contasComRejeicao > 0) {
            System.out.printf("! %d conta(s) tiveram saques rejeitados por saldo insuficiente%n",
                    contasComRejeicao);
        }
    }

    /**
     * Exibe o resumo final do extrato completo.
     */
    private void exibirResumoFinal(BigDecimal saldoTotal, int totalRejeitadas) {
        System.out.println("\n" + "=".repeat(80));
        System.out.println("RESUMO FINAL:");
        System.out.printf("  Saldo total geral: R$ %.2f%n", saldoTotal);
        System.out.printf("  Total de operações rejeitadas: %d%n", totalRejeitadas);
        System.out.println("=".repeat(80));
    }
}