package util;

import java.util.ArrayList;
import java.util.List;

public class Grafo {

    public class Vertice {

        private boolean corRED;
        private int numSoldados;
        private List<Vertice> adjs;

        public Vertice() {            
            this.corRED = true;
            this.numSoldados = 0;
            this.adjs = new ArrayList();
        }        
        
        public Vertice(boolean corRED, int numSoldados) {            
            this.corRED = corRED;
            this.numSoldados = numSoldados;
            this.adjs = new ArrayList();
        }

        public boolean cheio() {
            return numSoldados == 3;
        }        
        
        public int getNumSoldados() {
            return numSoldados;
        }

        public boolean temSoldados() {
            return (numSoldados > 0);
        }

        public void menosUmSoldado() {
            if (temSoldados()) {
                numSoldados--;
            }
        }

        public void maisUmSoldado() {
            if (numSoldados < 3) {
                numSoldados++;
            }
        }

        public void zerarSoldados() {
            numSoldados = 0;
        }

        public void setNumSoldados(int numSoldados) {
            this.numSoldados = numSoldados;
        }

        public boolean isCorRED() {
            return corRED;
        }
        
        public boolean isCorBLUE() {
            return !corRED;
        }        

        public void setCorRED(boolean corRED) {
            this.corRED = corRED;
        }

        public void addAdj(Vertice adj) {
            adjs.add(adj);
        }

        public void removerAdj(Vertice adj) {
            adjs.remove(adj);
        }

        public boolean contemAdj(Vertice adj) {
            return adjs.contains(adj);
        }

        public List<Vertice> getAdjs() {
            return adjs;
        }

    }

    private List<Vertice> listaAdj;
    private Integer numVertices;
    private Integer numArestas;
    private Boolean orientado;

    public Grafo(int numVertices) {
        this.orientado = false;
        this.listaAdj = new ArrayList();
        this.numVertices = numVertices;
        this.numArestas = 0;
        for (int i = 0; i < this.numVertices; i++) {
            listaAdj.add(new Vertice());
        }
    }

    public Grafo(int numVertices, boolean orientado) {
        this(numVertices);
        this.orientado = orientado;
    }

    public void novoVertice(boolean cor, int soldados) {
        listaAdj.add(new Vertice(cor, soldados));
        numVertices++;
    }

    public void setVertice(int v, boolean cor, int soldados) {
        listaAdj.get(v).setCorRED(cor);
        listaAdj.get(v).setNumSoldados(soldados);
    }    
    
    public boolean removeVertice(int v) {
        if (v >= listaAdj.size() || v < 0) {
            return false;
        }
        listaAdj.remove(v);
        numVertices--;
        return true;
    }

    public boolean insereAresta(int v1, int v2) {
        if (v1 == v2 && listaAdj.size() > v1 && listaAdj.size() > v2) {
            return false;
        }
        if (!listaAdj.get(v1).contemAdj(listaAdj.get(v2))) {
            listaAdj.get(v1).addAdj(listaAdj.get(v2));
            if (!orientado) {
                listaAdj.get(v2).addAdj(listaAdj.get(v1));
            }
            this.numArestas++;
            return true;
        } else {
            return false;
        }
    }

    public boolean retiraAresta(int v1, int v2) {
        if (v1 == v2 && listaAdj.size() > v1 && listaAdj.size() > v2) {
            return false;
        }
        if (!listaAdj.get(v1).contemAdj(listaAdj.get(v2))) {
            listaAdj.get(v1).removerAdj(listaAdj.get(v2));
            if (!orientado) {
                listaAdj.get(v2).removerAdj(listaAdj.get(v1));
            }
            this.numArestas--;
            return true;
        } else {
            return false;
        }
    }

    public boolean existeAresta(Integer v1, Integer v2) {
        return listaAdj.get(v1).contemAdj(listaAdj.get(v2));
    }

    public List<Vertice> verticesAdjacentesDe(Integer v) {
        return this.verticesSucessoresDe(v);
    }

    public List<Vertice> verticesSucessoresDe(Integer v) {
        return listaAdj.get(v).getAdjs();
    }

    public void imprime() {
        System.out.print("Lista de adjacencias:\n");
        for (int v1 = 0; v1 < this.numVertices; v1++) {
            System.out.print("Vértice " + v1 + ": Quant: " + listaAdj.get(v1).getNumSoldados()+" Adjs: ");
            List<Vertice> adjs = this.verticesSucessoresDe(v1);
            for (int j = 0; j < adjs.size(); j++) {
                System.out.print(listaAdj.indexOf(adjs.get(j))+", ");
            }
            System.out.println("");
        }
    }

    public int numeroVertices() {
        return this.numVertices;
    }

    public int numeroArestas() {
        return numArestas;
    }

    public boolean isOrientado() {
        return orientado;
    }

    public Vertice getVertice(Integer v) {
        if (v >= listaAdj.size() || v < 0) {
            return null;
        } else {
            return listaAdj.get(v);
        }
    }

    public int indexVertice(Vertice v) {
        return listaAdj.indexOf(v);
    }

}
