package service;

import model.Transacao;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

/**
 * Responsabilidade única: remover transações duplicadas.
 * Usa HashSet para deduplicação eficiente em O(n).
 *
 * @author Gilcimar Matias
 * @version 3.0
 */
public class DeduplicadorService {

    private int duplicatasRemovidas = 0;

    /**
     * Remove transações duplicadas da lista.
     * Duas transações são consideradas duplicatas quando possuem
     * mesma agência, conta, banco, titular, operação, data/hora e valor.
     *
     * @param transacoes Lista de transações (pode conter duplicatas)
     * @return Nova lista sem duplicatas
     */
    public List<Transacao> removerDuplicatas(List<Transacao> transacoes) {
        HashSet<Transacao> conjunto = new HashSet<>();
        duplicatasRemovidas = 0;

        for (Transacao t : transacoes) {
            boolean nova = conjunto.add(t);
            if (!nova) {
                duplicatasRemovidas++;
            }
        }

        return new ArrayList<>(conjunto);
    }

    /**
     * Retorna a quantidade de duplicatas removidas na última execução.
     */
    public int getDuplicatasRemovidas() {
        return duplicatasRemovidas;
    }
}