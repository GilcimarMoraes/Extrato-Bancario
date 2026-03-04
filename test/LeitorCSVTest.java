import model.Transacao;
import service.SaldoService;
import service.LeitorCsv;

import java.util.List;
import java.util.Map;

/**
 * Classe de teste para validar o fluxo completo:
 * leitura do CSV → deduplicação → cálculo de saldos → exibição.
 *
 * @author Gilcimar Matias
 * @version 2.0
 */
public class LeitorCSVTest {

    public static void main(String[] args) {

        String caminho = args.length > 0 ? args[0] : "data/operacoes.csv";

        long inicio = System.currentTimeMillis();

        // 1. Leitura do CSV (já deduplica internamente via HashSet)
        LeitorCsv leitor = new LeitorCsv();
        List<Transacao> transacoes = leitor.lerArquivo(caminho);

        if (transacoes.isEmpty()) {
            System.out.println("Nenhuma operação válida encontrada.");
            return;
        }

        System.out.println("Operações válidas carregadas: " + transacoes.size());

        // 2. Calcular saldos (já ordena internamente por data/hora)
        SaldoService calc = new SaldoService();
        Map<String, SaldoService.SaldoConta> saldos = calc.calcularSaldos(transacoes);

        // 3. Exibir extrato
        calc.exibirExtratoCompleto(saldos);

        long fim = System.currentTimeMillis();
        System.out.printf("%nTempo de execução: %.2f segundos%n", (fim - inicio) / 1000.0);
    }
}