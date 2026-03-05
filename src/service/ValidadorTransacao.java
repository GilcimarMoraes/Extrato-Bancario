package service;

import model.ResultadoValidacao;
import model.Transacao;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.List;

/**
 * Responsabilidade única: validar campos e converter String[] em Transacao.
 * Não lê arquivos, não deduplica, não calcula saldos.
 *
 * @author Gilcimar Matias
 * @version 3.0
 */
public class ValidadorTransacao {

    private static final DateTimeFormatter FORMATADOR_DATA = DateTimeFormatter.ISO_LOCAL_DATE_TIME;
    private static final int TAMANHO_CORRETO_DATA = 19;

    /**
     * Valida uma lista de linhas brutas do CSV e retorna o resultado.
     *
     * @param linhasBrutas Lista de arrays de campos
     * @param temColunaValor Se o CSV possui coluna VALOR
     * @return ResultadoValidacao com transações válidas e erros
     */
    public ResultadoValidacao validar(List<String[]> linhasBrutas, boolean temColunaValor) {
        ResultadoValidacao resultado = new ResultadoValidacao();

        for (int i = 0; i < linhasBrutas.size(); i++) {
            resultado.incrementarLinhasProcessadas();
            int numeroLinha = i + 2; // +2 porque linha 1 é header e índice começa em 0

            try {
                Transacao transacao = converter(linhasBrutas.get(i), numeroLinha, temColunaValor);
                resultado.adicionarValida(transacao);
            } catch (IllegalArgumentException e) {
                resultado.adicionarErro("Linha " + numeroLinha + ": " + e.getMessage());
            }
        }

        return resultado;
    }

    /**
     * Converte um array de campos em um objeto Transacao, validando cada campo.
     *
     * @param campos Array de campos da linha CSV
     * @param numeroLinha Número da linha para referência em erros
     * @param temColunaValor Se o CSV possui coluna VALOR
     * @return Transacao válida
     * @throws IllegalArgumentException Se algum campo for inválido
     */
    private Transacao converter(String[] campos, int numeroLinha, boolean temColunaValor) {
        int minCampos = temColunaValor ? 7 : 6;

        if (campos.length < minCampos) {
            throw new IllegalArgumentException(
                    "Campos insuficientes. Esperado: " + minCampos + ", encontrado: " + campos.length
            );
        }

        String agencia     = campos[0].trim();
        String conta       = campos[1].trim();
        String banco       = campos[2].trim();
        String titular     = campos[3].trim();
        String operacao    = campos[4].trim().toUpperCase();
        String dataHoraStr = campos[5].trim();

        // Validação de campos obrigatórios
        if (agencia.isEmpty() || conta.isEmpty() || banco.isEmpty() ||
                titular.isEmpty() || operacao.isEmpty() || dataHoraStr.isEmpty()) {
            throw new IllegalArgumentException("Campo(s) obrigatório(s) vazio(s).");
        }

        // Validação do tipo de operação
        if (!operacao.equals("SAQUE") && !operacao.equals("DEPOSITO")) {
            throw new IllegalArgumentException(
                    "Operação inválida: " + operacao + ". Deve ser SAQUE ou DEPOSITO."
            );
        }

        // Validação do tamanho da data/hora
        if (dataHoraStr.length() != TAMANHO_CORRETO_DATA) {
            throw new IllegalArgumentException(
                    "Data/Hora com tamanho incorreto. Esperado: " + TAMANHO_CORRETO_DATA +
                            ", encontrado: " + dataHoraStr.length() + ". Valor: " + dataHoraStr
            );
        }

        // Parse da data/hora
        LocalDateTime dataHora;
        try {
            dataHora = LocalDateTime.parse(dataHoraStr, FORMATADOR_DATA);
        } catch (DateTimeParseException e) {
            throw new IllegalArgumentException(
                    "Data/Hora em formato inválido: " + dataHoraStr +
                            ". Formato esperado: yyyy-MM-ddTHH:mm:ss"
            );
        }

        // Valor
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
            valor = BigDecimal.ONE;
        }

        return new Transacao(agencia, conta, banco, titular, operacao, dataHora, valor);
    }
}