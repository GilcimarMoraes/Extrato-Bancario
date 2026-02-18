package service;

import model.Conta;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.List;

public class LeitorCsv {

    private static final DateTimeFormatter FORMATADOR_DATA = DateTimeFormatter.ISO_LOCAL_DATE_TIME;

    private String caminho = "data/operacoes.csv";


    /**
     *
     * @param caminho
     * @return
     */

    public List<Conta>  lerArquivo( String caminho) {
        List<Conta> operacoes = new ArrayList<>();
        String linha;
        boolean primeiraLinha = true;

        try(BufferedReader br = new BufferedReader(new FileReader(caminho))) {
            System.out.println( "Lendo arquivo ...\n");

            while( (linha = br.readLine() ) != null ) {
                if( primeiraLinha ) {
                    primeiraLinha = false;
                    continue;
                }

                try{
                    Conta operacao = processarLinha( linha );
                    if( operacao != null ) {
                        operacoes.add( operacao );
                    }
                } catch ( Exception e) {
                    System.err.println( "Erro ao processar linha." + linha );
                    System.err.println( "Erro: " + e.getMessage() );
                }
            }
        } catch( IOException e) {
            System.err.println( "Erro ao ler arquivo: " + e.getMessage() );
            e.printStackTrace();
        }

        return operacoes;

    }

    private Conta processarLinha( String linha) {
        String[] campos = linha.split(",");
        if( campos.length < 6 ) {
            System.err.println( "Linha com número insuficiente de campos." + linha );
            return null;
        }

        String agencia =  campos[0].trim();
        String conta =  campos[1].trim();
        String banco =  campos[2].trim();
        String titular =   campos[3].trim();
        String operacao =  campos[4].trim();
        String dataHoraStr =  campos[5].trim();

        dataHoraStr = corrigirFormatatoDataHora( dataHoraStr );

        try {
            LocalDateTime dataHora = LocalDateTime.parse( dataHoraStr, FORMATADOR_DATA );
            return new Conta( agencia, conta, banco, titular, operacao, dataHora);
        } catch( DateTimeParseException e ) {
            System.err.println( "Erro ao parsear dat: " + dataHoraStr );
            System.err.println( "Linha original: " + linha );
            return null;
        }
    }

    private String corrigirFormatatoDataHora( String dataHoraStr ) {
        if( dataHoraStr.contains("T0") && dataHoraStr.length() > 19 ) {
            String[] partes = dataHoraStr.split("T");
            if( partes.length == 2 ) {
                String horaParte = partes[1];
                if( horaParte.length() > 8 && horaParte.charAt(0) == '0' &&
                horaParte.charAt(1) == '1' && horaParte.charAt(2) == '1' ) {
                    dataHoraStr = partes[0] + "T" + horaParte.substring( 1 );
                }
            }
        }

        return dataHoraStr;
    }

    public List<Conta> ordenarPorDataHora( List<Conta> operacoes ) {
        List<Conta> operacoesOrdenadas = new ArrayList<>( operacoes );
        operacoesOrdenadas.sort( ( c1, c2) -> c1.getDataHora().
                compareTo( c2.getDataHora() ) );

        return operacoesOrdenadas;
    }
}
