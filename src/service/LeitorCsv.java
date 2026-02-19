package service;

import model.Conta;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;

/**
 * Classe responsável pela leitura e processamento de arquivos CSV
 * contendo operações bancárias. Realiza validações de formato,
 * conversão de tipos e remove operações duplicadas.
 *
 * @author Gilcimar Matias
 * @version 1.0
 */


public class LeitorCsv {

    private static final DateTimeFormatter FORMATADOR_DATA = DateTimeFormatter.ISO_LOCAL_DATE_TIME;

    private static final int TAMANHO_CORRETO_DATA = 19;

    private String caminho = "data/operacoes.csv";

    private int linhasProcessadas = 0;
    private int linhasComErro = 0;
    private List<String> errosDetalhados = new ArrayList<>();

    /**
     * Lê um arquivo CSV e converte suas linhas em objetos Conta.
     * Realiza validações de formato e conteúdo, gerando relatório
     * de erros ao final do processamento.
     *
     * @param caminho Caminho completo para o arquivo CSV
     * @return Lista de operações válidas lidas do arquivo
     */

    public List<Conta>  lerArquivo( String caminho) {
        List<Conta> operacoes = new ArrayList<>();
        String linha;
        boolean primeiraLinha = true;
        int numeroLinha = 0;

        try(BufferedReader br = new BufferedReader(new FileReader(caminho))) {
            System.out.println( "Lendo arquivo ...\n");

            while( (linha = br.readLine() ) != null ) {
                numeroLinha ++;

                if( primeiraLinha ) {
                    primeiraLinha = false;
                    continue;
                }

                linhasProcessadas++;

                try{
                    Conta operacao = processarLinha( linha, numeroLinha );
                    if( operacao != null ) {
                        operacoes.add( operacao );
                    }
                } catch ( Exception e) {
                    linhasComErro++;
                    String erro = String.format( "Linha %d: %s - Erro: %s",
                            numeroLinha, caminho, e.getMessage() );
                    errosDetalhados.add( erro );
                    System.err.println( erro );
                }
            }

            exibirRelatorio();

        } catch( IOException e) {
            System.err.println( "Erro ao ler arquivo: " + e.getMessage() );
            e.printStackTrace();
        }

        return operacoes;

    }

    /**
     * Processa uma linha individual do CSV, validando e convertendo
     * seus campos em um objeto Conta.
     *
     * @param linha Linha do arquivo CSV
     * @param numeroLinha Número da linha para referência em erros
     * @return Objeto Conta criado a partir da linha
     * @throws IllegalArgumentException Se a linha tiver dados inválidos
     */

    private Conta processarLinha( String linha, int numeroLinha) throws IllegalArgumentException {
        if( linha == null ) {
            throw new IllegalArgumentException( "Linha vazia." );
        }

        String[] campos = linha.split(",");
        if( campos.length < 7 ) {
            throw new IllegalArgumentException( "Número insuficiente de campos. Esperado: 7, " +
                    "encontrado: " + campos.length );
        }

        String agencia =  campos[0].trim();
        String conta =  campos[1].trim();
        String banco =  campos[2].trim();
        String titular =   campos[3].trim();
        String operacao =  campos[4].trim();
        String dataHoraStr =  campos[5].trim();
        String valorStr =  campos[6].trim();

        if( agencia.isEmpty() || conta.isEmpty() || banco.isEmpty() ||
                titular.isEmpty() || operacao.isEmpty() ||
                dataHoraStr.isEmpty() || valorStr.isEmpty() ) {
            throw new IllegalArgumentException( "Campo(s) vazio(s) ou nao encontrado(s).");
        }

        if( !operacao.equals( "SAQUE" ) && !operacao.equals( "DEPOSITO" ) ) {
            throw new IllegalArgumentException( "Operação inválida: " + operacao +". " +
                    "Deve ser SAQUE ou DEPOSITO." );
        }

        if( dataHoraStr.length() != TAMANHO_CORRETO_DATA ) {
            throw new IllegalArgumentException( "Data/Hora com formato incorreto. Tamanho" +
                    "esperado: " + TAMANHO_CORRETO_DATA + ". Número de caracteres, encontrados: " +
                    dataHoraStr.length() + ". Valor: " + dataHoraStr );
        }

        LocalDateTime dataHora;
        try {
            dataHora = LocalDateTime.parse( dataHoraStr, FORMATADOR_DATA );
        } catch( DateTimeParseException e ) {
            throw new IllegalArgumentException( "Data/Hora em formato inválido: " +  dataHoraStr +
                    ". Formato esperado: yyyy-MM-ddTHH:mm:ss" );
        }

        BigDecimal valor;
        try {
            valor = new BigDecimal( valorStr );
            if( valor.compareTo( BigDecimal.ZERO ) <= 0 ) {
                throw new IllegalArgumentException( "Valor deve ser positivo: " + valorStr );
            }
        } catch ( NumberFormatException e ) {
            throw new IllegalArgumentException( "Valor inválido: " + valorStr );
        }

        return new Conta( agencia, conta, banco, titular, operacao, dataHora, valor );
    }

    /**
     * Exibe um relatório com estatísticas do processamento do arquivo.
     */

    private void exibirRelatorio() {
        System.out.println( "\n === RELATÓRIO DE PROCESSAMENTO ===" );
        System.out.println( "Linhas Processadas: " + linhasProcessadas );
        System.out.println( "Linhas Com Erro: " + linhasComErro );
        System.out.println( "Operações válidas: " + (linhasProcessadas - linhasComErro) );

        if( !errosDetalhados.isEmpty() ) {
            System.out.println( "\nErros encontrados: ");
            for( String erro : errosDetalhados ) {
                System.out.println( " " + erro );
            }
        }
    }

    /**
     * Ordena uma lista de operações por data e hora.
     *
     * @param operacoes Lista de operações a ser ordenada
     * @return Nova lista contendo as operações em ordem cronológica
     */

    public List<Conta> ordenarPorDataHora( List<Conta> operacoes ) {
        List<Conta> operacoesOrdenadas = new ArrayList<>( operacoes );
        operacoesOrdenadas.sort( ( c1, c2) -> c1.getDataHora().
                compareTo( c2.getDataHora() ) );

        return operacoesOrdenadas;
    }

    /**
     * Remove operações duplicadas de uma lista, mantendo a ordem original.
     * Duas operações são consideradas duplicadas se possuem os mesmos
     * dados de conta, tipo, data/hora e valor.
     *
     * @param operacoes Lista de operações
     * @return Nova lista contendo apenas operações únicas
     */

    public List<Conta> removerDuplicata(  List<Conta> operacoes ) {
        return new ArrayList<>( new LinkedHashSet<>( operacoes ) );
    }
}
