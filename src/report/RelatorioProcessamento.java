package report;

import model.ResultadoValidacao;

/**
 * Responsabilidade única: exibir estatísticas do processamento do CSV.
 * Não valida, não calcula, apenas apresenta.
 *
 * @author Gilcimar Matias
 * @version 3.0
 */
public class RelatorioProcessamento {

    /**
     * Exibe relatório de leitura e validação do CSV.
     *
     * @param resultado Resultado da validação
     * @param duplicatasRemovidas Quantidade de duplicatas removidas
     */
    public void exibir(ResultadoValidacao resultado, int duplicatasRemovidas) {
        System.out.println("\n=== RELATÓRIO DE PROCESSAMENTO ===");
        System.out.println("Linhas processadas   : " + resultado.getLinhasProcessadas());
        System.out.println("Linhas com erro      : " + resultado.getTotalErros());
        System.out.println("Duplicatas removidas : " + duplicatasRemovidas);
        System.out.println("Operações válidas    : " +
                (resultado.getValidas().size() - duplicatasRemovidas));

        if (resultado.hasErros()) {
            System.out.println("\nErros encontrados:");
            for (String erro : resultado.getErros()) {
                System.out.println("  " + erro);
            }
        }
    }
}