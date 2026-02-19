import model.CalcularSaldoConta;
import model.Conta;
import service.LeitorCsv;

import java.sql.SQLOutput;
import java.util.List;
import java.util.Map;

public class LeitorCSVTest {

    public static void main() {

        LeitorCsv leitor =  new LeitorCsv();
        CalcularSaldoConta calc = new CalcularSaldoConta();

        String caminho = "data/operacoes.csv";

        List<Conta> operacoes = leitor.lerArquivo( caminho );

        if( operacoes.isEmpty() ) {
            System.out.println( "Nenhuma operação válida encontrada" );
            return;
        }

        System.out.println( "Operações válidas carregadas" + operacoes.size() );

        // Ordenar por data e hora
        List<Conta> operacoesOrdenadas = leitor.ordenarPorDataHora( operacoes );

        // Eliminar duplicatas
        List<Conta> operacoesUnicas = leitor.removerDuplicata( operacoesOrdenadas );

        System.out.println( "Operações após remover duplicatas: " + operacoesUnicas.size() );

        //Calcular Saldos
        Map<String, CalcularSaldoConta.SaldoConta> saldos = calc.calcularSaldos( operacoesUnicas );

        // Formatos de Extrato
        calc.exibirExtratoCompleto( saldos );
        //calc.exibirSaldoResumido( saldos );


        /*
        // Exibir os resultados
        System.out.println( "\n" + "#".repeat( 80 ) );
        System.out.println( "PROCESSAMENTO CONCLUÍDO" );
        System.out.println( "#".repeat( 80 ) );

        System.out.println( "Operações lidas sem ordenação.");
        //exibirOperacoes(operacoes);

        List<Conta> operacoesOrdenadas = leitor.ordenarPorDataHora( operacoes );
        System.out.println( "Operações ordenadas." );
        //exibirOperacoes(operacoesOrdenadas);

        List<Conta> operacoesSemDuplicata = leitor.removerDuplicata( operacoesOrdenadas );
        System.out.println( "Operações sem duplicata." );
        exibirOperacoes( operacoesSemDuplicata );

*/
    }
/*
    private static void exibirOperacoes(List<Conta> operacoes) {
        for (Conta conta : operacoes) {
            System.out.printf("Titular: %-8s | Ag: %-6s | Conta: %-6s | Banco: %-10s | Operação: %-8s | Data/Hora: %s%n",
                    conta.getTitular(),
                    conta.getAgencia(),
                    conta.getConta(),
                    conta.getBanco(),
                    conta.getTipoOperacao(),
                    conta.getDataHora());
        }
    }

 */
}
