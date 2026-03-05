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
 * Classe principal — orquestra o fluxo completo:
 * Leitura → Validação → Deduplicação → Cálculo → Apresentação.
 *
 * Cada etapa é delegada a uma classe com responsabilidade única.
 *
 * @author Gilcimar Matias
 * @version 3.0
 */
public class Main {

    public static void main(String[] args) {
        if (args.length < 1) {
            System.out.println("Uso: java Main <caminho-do-arquivo.csv>");
            System.out.println("Exemplo: java Main data/operacoes.csv");
            return;
        }

        String caminhoArquivo = args[0];
        long inicio = System.currentTimeMillis();

        // 1. LEITURA — I/O puro, retorna linhas brutas
        LeitorCsv leitor = new LeitorCsv();
        List<String[]> linhasBrutas = leitor.lerArquivo(caminhoArquivo);

        if (linhasBrutas.isEmpty()) {
            System.out.println("Arquivo vazio ou não encontrado.");
            return;
        }

        System.out.printf("Linhas lidas do CSV: %d%n", linhasBrutas.size());

        // 2. VALIDAÇÃO — converte String[] em Transacao, separando erros
        ValidadorTransacao validador = new ValidadorTransacao();
        ResultadoValidacao resultado = validador.validar(linhasBrutas, leitor.isTemColunaValor());

        if (resultado.getValidas().isEmpty()) {
            System.out.println("Nenhuma transação válida encontrada.");
            return;
        }

        // 3. DEDUPLICAÇÃO — remove transações duplicatas via HashSet
        DeduplicadorService deduplicador = new DeduplicadorService();
        List<Transacao> unicas = deduplicador.removerDuplicatas(resultado.getValidas());

        // 4. RELATÓRIO DE PROCESSAMENTO — estatísticas da leitura
        RelatorioProcessamento relatorio = new RelatorioProcessamento();
        relatorio.exibir(resultado, deduplicador.getDuplicatasRemovidas());

        System.out.printf("%nTransações únicas para processar: %d%n", unicas.size());

        // 5. CÁLCULO DE SALDOS — ordena e processa transações
        SaldoService saldoService = new SaldoService();
        Map<String, SaldoConta> saldos = saldoService.calcular(unicas);

        // 6. APRESENTAÇÃO — formata e exibe extratos
        ExtratoFormatter formatter = new ExtratoFormatter();

        if (unicas.size() > 10_000) {
            System.out.println("\n[Arquivo grande detectado - exibindo apenas resumo]");
            formatter.exibirResumido(saldos);
        } else {
            formatter.exibirCompleto(saldos);
        }

        long fim = System.currentTimeMillis();
        System.out.printf("%nTempo total de processamento: %.2f segundos%n", (fim - inicio) / 1000.0);
    }
}