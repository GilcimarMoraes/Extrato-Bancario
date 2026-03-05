package service;

import model.SaldoConta;
import model.Transacao;

import java.util.*;

/**
 * Responsabilidade única: calcular saldos das contas.
 * Ordena transações cronologicamente e processa cada uma.
 * Não lê arquivos, não formata saída.
 *
 * @author Gilcimar Matias
 * @version 3.0
 */
public class SaldoService {

    /**
     * Processa transações e calcula o saldo final de cada conta.
     * Transações são ordenadas por data/hora antes do processamento
     * para garantir validação correta dos saques.
     *
     * @param transacoes Lista de transações únicas
     * @return Mapa com saldo de cada conta (chave = agencia-conta-banco)
     */
    public Map<String, SaldoConta> calcular(List<Transacao> transacoes) {
        List<Transacao> ordenadas = new ArrayList<>(transacoes);
        ordenadas.sort(Comparator.comparing(Transacao::getDataHora));

        Map<String, SaldoConta> saldos = new LinkedHashMap<>();

        for (Transacao t : ordenadas) {
            String chave = gerarChaveConta(t);

            saldos.computeIfAbsent(chave, k ->
                    new SaldoConta(t.getAgencia(), t.getConta(), t.getBanco(), t.getTitular())
            ).adicionarOperacao(t);
        }

        return saldos;
    }

    /**
     * Gera chave única para identificar uma conta.
     */
    private String gerarChaveConta(Transacao t) {
        return t.getAgencia() + "-" + t.getConta() + "-" + t.getBanco();
    }
}