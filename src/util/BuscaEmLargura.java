package util;

import java.util.ArrayList;
import java.util.List;

public class BuscaEmLargura {

    public static final byte branco = 0;
    public static final byte cinza = 1;
    public static final byte preto = 2;
    private int descoberta[], antecessor[], origem;
    private Grafo grafo;

    public BuscaEmLargura(Grafo grafo, int origem) {
        this.grafo = grafo;
        this.origem = origem;
        int n = this.grafo.numeroVertices();
        this.descoberta = new int[n];
        this.antecessor = new int[n];
        this.buscaEmLargura();
    }

    private void visitaBFS(int u, int cor[]) {
        cor[u] = cinza;
        this.descoberta[u] = 0;
        List<Integer> fila = new ArrayList();
        fila.add(new Integer(u));
        while (!fila.isEmpty()) {
            u = fila.remove(0);
            List<Grafo.Vertice> adjs = this.grafo.verticesAdjacentesDe(u);
            for (int i = 0; i < adjs.size(); i++) {
                int v = grafo.indexVertice(adjs.get(i));
                if (cor[v] == branco) {
                    cor[v] = cinza;
                    this.descoberta[v] = this.descoberta[u] + 1;
                    this.antecessor[v] = u;
                    fila.add(new Integer(v));
                }
            }
            cor[u] = preto;
        }
    }

    public void buscaEmLargura() {
        int cor[] = new int[this.grafo.numeroVertices()];
        for (int u = 0; u < grafo.numeroVertices(); u++) {
            cor[u] = branco;
            this.descoberta[u] = Integer.MAX_VALUE;
            this.antecessor[u] = -1;
        }
        for (int u = 0; u < grafo.numeroVertices(); u++) {
            int v = (origem + u) % grafo.numeroVertices();
            if (cor[v] == branco) {
                this.visitaBFS(v, cor);
            }
        }
    }

    public void imprime() {
        System.out.println("");
        System.out.println("------------ BUSCA EM LARGURA --------------");
        for (int v = 0; v < grafo.numeroVertices(); v++) {
            System.out.printf("Vértice %2s: [%2s]; Antecessor de %2s: %2s", v, this.tempoDescoberta(v), v, this.antecessor(v));
            System.out.println();
        }
        System.out.println("");
    }

    public int tempoDescoberta(int v) {
        return this.descoberta[v];
    }

    public int antecessor(int v) {
        return this.antecessor[v];
    }

    public List<Integer> getMenorCaminho(int destino) {
        List<Integer> caminho = new ArrayList();
        this.getCaminhoPara(destino, caminho);
        return (caminho.contains(null) ? null : caminho);
    }

    private void getCaminhoPara(int destino, List<Integer> caminho) {
        if (origem == destino) {
            caminho.add(origem);
        } else if (this.antecessor[destino] == -1) {
            caminho.add(null);
        } else {
            this.getCaminhoPara(this.antecessor[destino], caminho);
            caminho.add(destino);
        }
    }

    public void imprimeMenorCaminhoPara(int destino) {
        System.out.println("Menor caminho entre " + origem + " e " + destino + ": ");
        this.imprimeCaminhoPara(destino);
        System.out.println("");
    }

    private void imprimeCaminhoPara(int destino) {
        if (origem == destino) {
            System.out.print(origem);
        } else if (this.antecessor[destino] == -1) {
            System.out.print("Nao existe caminho de " + origem + " ate " + destino);
        } else {
            this.imprimeCaminhoPara(this.antecessor[destino]);
            System.out.print((grafo.isOrientado() ? " -> " : " - ") + destino);
        }
    }

}
