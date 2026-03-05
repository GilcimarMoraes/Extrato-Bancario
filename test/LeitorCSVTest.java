import model.ResultadoValidacao;
import model.SaldoConta;
import model.Transacao;
import report.ExtratoFormatter;
import report.RelatorioProcessamento;
import service.DeduplicadorService;
import service.LeitorCsv;
import service.SaldoService;
import service.ValidadorTransacao;

import java.util.List;
import java.util.Map;

/**
 * Classe de teste para validar o fluxo completo com a nova arquitetura.
 *
 * @author Gilcimar Matias
 * @version 3.0
 */
public class LeitorCSVTest {

    public static void main(String[] args) {
        String caminho = args.length > 0 ? args[0] : "data/operacoes.csv";
        long inicio = System.currentTimeMillis();

        // 1. Leitura
        LeitorCsv leitor = new LeitorCsv();
        List<String[]> linhasBrutas = leitor.lerArquivo(caminho);
        System.out.println("Linhas lidas: " + linhasBrutas.size());

        // 2. Validação
        ValidadorTransacao validador = new ValidadorTransacao();
        ResultadoValidacao resultado = validador.validar(linhasBrutas, leitor.isTemColunaValor());
        System.out.println("Transações válidas: " + resultado.getValidas().size());

        // 3. Deduplicação
        DeduplicadorService deduplicador = new DeduplicadorService();
        List<Transacao> unicas = deduplicador.removerDuplicatas(resultado.getValidas());
        System.out.println("Transações únicas: " + unicas.size());

        // 4. Relatório
        RelatorioProcessamento relatorio = new RelatorioProcessamento();
        relatorio.exibir(resultado, deduplicador.getDuplicatasRemovidas());

        // 5. Cálculo de saldos
        SaldoService saldoService = new SaldoService();
        Map<String, SaldoConta> saldos = saldoService.calcular(unicas);

        // 6. Exibição
        ExtratoFormatter formatter = new ExtratoFormatter();
        formatter.exibirCompleto(saldos);

        long fim = System.currentTimeMillis();
        System.out.printf("%nTempo de execução: %.2f segundos%n", (fim - inicio) / 1000.0);
    }
}