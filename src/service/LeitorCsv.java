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
import java.util.Comparator;
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

    private int linhasProcessadas = 0;
    private int linhasComErro = 0;
    private int duplicatasRemovidas = 0;
    private List<String> errosDetalhados = new ArrayList<>();

    /**
     * Lê um arquivo CSV e converte suas linhas em objetos Conta.
     * Detecta automaticamente se o CSV possui coluna VALOR.
     * Remove duplicatas durante a leitura via LinkedHashSet.
     *
     * @param caminho Caminho completo para o arquivo CSV
     * @return Lista de operações válidas e sem duplicatas
     */
    public List<Conta> lerArquivo(String caminho) {
        // Buffer de 1MB para melhor performance com arquivos grandes
        try (BufferedReader br = new BufferedReader(new FileReader(caminho), 1024 * 1024)) {

            String header = br.readLine();
            boolean temColunaValor = header != null && header.toUpperCase().contains("VALOR");

            System.out.println("Lendo arquivo...");
            System.out.println("Coluna VALOR detectada: " + temColunaValor);

            // LinkedHashSet já deduplica na leitura, sem lista intermediária
            LinkedHashSet<Conta> conjunto = new LinkedHashSet<>();
            String linha;
            int numeroLinha = 1;

            while ((linha = br.readLine()) != null) {
                numeroLinha++;
                linhasProcessadas++;
                try {
                    Conta op = processarLinha(linha, numeroLinha, temColunaValor);
                    if (op != null) {
                        boolean nova = conjunto.add(op);
                        if (!nova) duplicatasRemovidas++;
                    }
                } catch (Exception e) {
                    linhasComErro++;
                    errosDetalhados.add("Linha " + numeroLinha + ": " + e.getMessage());
                }
            }

            exibirRelatorio();
            return new ArrayList<>(conjunto);

        } catch (IOException e) {
            System.err.println("Erro ao ler arquivo: " + e.getMessage());
            e.printStackTrace();
            return new ArrayList<>();
        }
    }

    /**
     * Processa uma linha individual do CSV, validando e convertendo
     * seus campos em um objeto Conta.
     *
     * @param linha          Linha do arquivo CSV
     * @param numeroLinha    Número da linha para referência em erros
     * @param temColunaValor Se o CSV possui a coluna VALOR
     * @return Objeto Conta criado a partir da linha
     * @throws IllegalArgumentException Se a linha tiver dados inválidos
     */
    private Conta processarLinha(String linha, int numeroLinha, boolean temColunaValor) {
        if (linha == null || linha.isBlank()) {
            throw new IllegalArgumentException("Linha vazia.");
        }

        String[] campos = linha.split(",");
        int minCampos = temColunaValor ? 7 : 6;

        if (campos.length < minCampos) {
            throw new IllegalArgumentException(
                    "Campos insuficientes. Esperado: " + minCampos + ", encontrado: " + campos.length
            );
        }

        String agencia      = campos[0].trim();
        String conta        = campos[1].trim();
        String banco        = campos[2].trim();
        String titular      = campos[3].trim();
        String operacao     = campos[4].trim();
        String dataHoraStr  = campos[5].trim();

        // Validação de campos obrigatórios
        if (agencia.isEmpty() || conta.isEmpty() || banco.isEmpty() ||
                titular.isEmpty() || operacao.isEmpty() || dataHoraStr.isEmpty()) {
            throw new IllegalArgumentException("Campo(s) obrigatório(s) vazio(s).");
        }

        if (!operacao.equals("SAQUE") && !operacao.equals("DEPOSITO")) {
            throw new IllegalArgumentException(
                    "Operação inválida: " + operacao + ". Deve ser SAQUE ou DEPOSITO."
            );
        }

        if (dataHoraStr.length() != TAMANHO_CORRETO_DATA) {
            throw new IllegalArgumentException(
                    "Data/Hora com tamanho incorreto. Esperado: " + TAMANHO_CORRETO_DATA +
                            ", encontrado: " + dataHoraStr.length() + ". Valor: " + dataHoraStr
            );
        }

        LocalDateTime dataHora;
        try {
            dataHora = LocalDateTime.parse(dataHoraStr, FORMATADOR_DATA);
        } catch (DateTimeParseException e) {
            throw new IllegalArgumentException(
                    "Data/Hora em formato inválido: " + dataHoraStr +
                            ". Formato esperado: yyyy-MM-ddTHH:mm:ss"
            );
        }

        // Valor: lê do CSV se tiver coluna, senão usa BigDecimal.ONE como padrão
        BigDecimal valor;
        if (temColunaValor) {
            String valorStr = campos[6].trim();
            if (valorStr.isEmpty()) {
                throw new IllegalArgumentException("Campo VALOR está vazio.");
            }
            try {
                valor = new BigDecimal(valorStr);
                if (valor.compareTo(BigDecimal.ZERO) <= 0) {
                    throw new IllegalArgumentException("Valor deve ser positivo: " + valorStr);
                }
            } catch (NumberFormatException e) {
                throw new IllegalArgumentException("Valor inválido: " + valorStr);
            }
        } else {
            valor = BigDecimal.ONE; // valor padrão quando coluna não existe
        }

        return new Conta(agencia, conta, banco, titular, operacao, dataHora, valor);
    }

    /**
     * Exibe relatório com estatísticas do processamento.
     */
    private void exibirRelatorio() {
        System.out.println("\n === RELATÓRIO DE PROCESSAMENTO ===");
        System.out.println("Linhas processadas : " + linhasProcessadas);
        System.out.println("Linhas com erro    : " + linhasComErro);
        System.out.println("Duplicatas removidas: " + duplicatasRemovidas);
        System.out.println("Operações válidas  : " + (linhasProcessadas - linhasComErro - duplicatasRemovidas));

        if (!errosDetalhados.isEmpty()) {
            System.out.println("\nErros encontrados:");
            for (String erro : errosDetalhados) {
                System.out.println("  " + erro);
            }
        }
    }

    /**
     * Ordena uma lista de operações por data e hora.
     */
    public List<Conta> ordenarPorDataHora(List<Conta> operacoes) {
        List<Conta> ordenadas = new ArrayList<>(operacoes);
        ordenadas.sort(Comparator.comparing(Conta::getDataHora));
        return ordenadas;
    }

    /**
     * Remove duplicatas de uma lista (alternativa ao LinkedHashSet na leitura).
     */
    public List<Conta> removerDuplicatas(List<Conta> operacoes) {
        return new ArrayList<>(new LinkedHashSet<>(operacoes));
    }
}