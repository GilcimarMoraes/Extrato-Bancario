package model;

import java.util.ArrayList;
import java.util.List;

/**
 * Encapsula o resultado da validação das linhas do CSV.
 * Separa transações válidas dos erros encontrados.
 *
 * @author Gilcimar Matias
 * @version 3.0
 */
public class ResultadoValidacao {

    private final List<Transacao> validas;
    private final List<String> erros;
    private int linhasProcessadas;

    public ResultadoValidacao() {
        this.validas = new ArrayList<>();
        this.erros = new ArrayList<>();
        this.linhasProcessadas = 0;
    }

    public void adicionarValida(Transacao transacao) {
        validas.add(transacao);
    }

    public void adicionarErro(String erro) {
        erros.add(erro);
    }

    public void incrementarLinhasProcessadas() {
        linhasProcessadas++;
    }

    public List<Transacao> getValidas() { return validas; }
    public List<String> getErros() { return erros; }
    public int getLinhasProcessadas() { return linhasProcessadas; }
    public int getTotalErros() { return erros.size(); }
    public boolean hasErros() { return !erros.isEmpty(); }
}