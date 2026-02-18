import model.Conta;
import service.LeitorCsv;

import java.util.ArrayList;
import java.util.List;

public class LeitorCSVTest {

    public static void main() {

        LeitorCsv leitor =  new LeitorCsv();

        String caminho = "data/operacoes.csv";

        List<Conta> operacoes = leitor.lerArquivo( caminho );

        System.out.println( "Operações lidas sem ordenação.");
        exibirOperacoes(operacoes);

        List<Conta> operacoesOrdenadas = leitor.ordenarPorDataHora( operacoes );
        System.out.println( "Operações ordenadas." );
        exibirOperacoes(operacoesOrdenadas);


    }

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
}
