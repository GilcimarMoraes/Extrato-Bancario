package service;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Responsabilidade única: leitura do arquivo CSV.
 * Não valida, não converte, não deduplica.
 * Apenas lê as linhas brutas e detecta se há coluna VALOR.
 *
 * @author Gilcimar Matias
 * @version 3.0
 */
public class LeitorCsv {

    private boolean temColunaValor = false;

    /**
     * Lê um arquivo CSV e retorna as linhas brutas (sem o header).
     *
     * @param caminho Caminho completo para o arquivo CSV
     * @return Lista de arrays de String, cada um representando os campos de uma linha
     */
    public List<String[]> lerArquivo(String caminho) {
        List<String[]> linhas = new ArrayList<>();

        try (BufferedReader br = new BufferedReader(new FileReader(caminho), 1024 * 1024)) {

            String header = br.readLine();
            if (header != null) {
                temColunaValor = header.toUpperCase().contains("VALOR");
            }

            String linha;
            while ((linha = br.readLine()) != null) {
                if (!linha.isBlank()) {
                    linhas.add(linha.split(","));
                }
            }

        } catch (IOException e) {
            System.err.println("Erro ao ler arquivo: " + e.getMessage());
            e.printStackTrace();
        }

        return linhas;
    }

    /**
     * Indica se o CSV lido possui a coluna VALOR.
     */
    public boolean isTemColunaValor() {
        return temColunaValor;
    }
}