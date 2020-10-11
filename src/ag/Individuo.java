package ag;

import util.BuscaEmLargura;
import util.Grafo;
import ui.Versus;

/**
 *
 * @author Guilherme
 */
public class Individuo implements Comparable {

    private int posAtual = -1;
    private int posMover = -1;
    private int posInimigo = -1;
    private double fitness = -1;
    private Boolean mod;

    private static int[][] camadas = {{0, 1}, {2, 3, 4}, {5, 6, 7, 8}, {9, 10, 11, 12, 13}, {14, 15, 16, 17}, {18, 19, 20}, {21, 22}};
    private int camadaMedia = 3;

    public Individuo() {
        this.novoPosAtualValido();
        this.novoPosMoverValido();
        this.novoPosInimigoValido();
        this.calcularFitness();
        //System.out.println("Add: " + posAtual + " " + posMovido + " " + posInimigo + " " + fitness);
    }

    public Individuo(Individuo ind) {
        this.posAtual = ind.getPosAtual();
        this.posMover = ind.getPosMovido();
        this.posInimigo = ind.getPosInimigo();
        this.fitness = ind.getFitness();
        this.mod = ind.isMod().booleanValue();
    }

    public void setPosAtual(int atual) {
        Grafo.Vertice vAtualNovo = Versus.grafo.getVertice(atual);
        if (vAtualNovo.isCorBLUE()) {
            this.posAtual = atual;
            this.mod = Boolean.TRUE;
            Grafo.Vertice vMovido = Versus.grafo.getVertice(posMover);
            if (!vAtualNovo.contemAdj(vMovido)) {
                this.novoPosMoverValido();
            }
        }
    }

    public boolean setPosMovido(int movido) {
        Grafo.Vertice vAtual = Versus.grafo.getVertice(posAtual);
        Grafo.Vertice vMovido = Versus.grafo.getVertice(movido);
        if (vAtual.contemAdj(vMovido)) {
            if (vMovido.temSoldados()) {
                if (vMovido.isCorBLUE() && !vMovido.cheio()) {
                    this.posMover = movido;
                    this.mod = Boolean.TRUE;
                }
            } else {
                this.posMover = movido;
                this.mod = Boolean.TRUE;
            }
            return true;
        }
        return false;
    }

    public void setPosInimigo(int inimigo) {
        Grafo.Vertice vInimigo = Versus.grafo.getVertice(inimigo);
        if (vInimigo.isCorRED() && vInimigo.temSoldados()) {
            this.posInimigo = inimigo;
            this.mod = Boolean.TRUE;
        }
    }

    public void novoPosAtualValido() {
        int atual = Versus.rand.nextInt(Versus.grafo.numeroVertices());
        Grafo.Vertice vAtual = Versus.grafo.getVertice(atual);
        while (!vAtual.temSoldados() || vAtual.isCorRED()) {
            atual = Versus.rand.nextInt(Versus.grafo.numeroVertices());
            vAtual = Versus.grafo.getVertice(atual);
        }
        this.posAtual = atual;
    }

    public void novoPosMoverValido() {
        Grafo.Vertice vAtual = Versus.grafo.getVertice(posAtual);
        int movido = Versus.rand.nextInt(Versus.grafo.numeroVertices());
        Grafo.Vertice vMovidoNovo = Versus.grafo.getVertice(movido);
        while (!vAtual.contemAdj(vMovidoNovo) || vMovidoNovo.temSoldados() && (vMovidoNovo.isCorRED() || (vMovidoNovo.isCorBLUE() && vMovidoNovo.cheio()))) {
            movido = Versus.rand.nextInt(Versus.grafo.numeroVertices());
            vMovidoNovo = Versus.grafo.getVertice(movido);
        }
        this.posMover = movido;
    }

    public void novoPosInimigoValido() {
        int inimigo = Versus.rand.nextInt(Versus.grafo.numeroVertices());
        Grafo.Vertice vInimigo = Versus.grafo.getVertice(inimigo);
        while (!vInimigo.temSoldados() || vInimigo.isCorBLUE()) {
            inimigo = Versus.rand.nextInt(Versus.grafo.numeroVertices());
            vInimigo = Versus.grafo.getVertice(inimigo);
        }
        this.posInimigo = inimigo;
    }

    private int getCamada(int v) {
        for (int x = 0; x < camadas.length; x++) {
            for (int y = 0; y < camadas[x].length; y++) {
                if (camadas[x][y] == v) {
                    return x;
                }
            }
        }
        return -1;
    }

    public void calcularFitness() {
        int redsAtual = 0, bluesAtual = 0;
        Grafo.Vertice vAtual = Versus.grafo.getVertice(posAtual);
        bluesAtual += vAtual.getNumSoldados();
        for (int i = 0; i < vAtual.getAdjs().size(); i++) {
            if (vAtual.getAdjs().get(i).isCorRED()) {
                redsAtual += vAtual.getAdjs().get(i).getNumSoldados();
            } else {
                bluesAtual += vAtual.getAdjs().get(i).getNumSoldados();
            }
        }
        //
        int redsMovido = 0, bluesMovido = 0;
        Grafo.Vertice vMovido = Versus.grafo.getVertice(posMover);
        bluesMovido += vMovido.getNumSoldados() + 0;//+1 movido e -1 atualAdj
        for (int i = 0; i < vMovido.getAdjs().size(); i++) {
            if (vMovido.getAdjs().get(i).isCorRED()) {
                redsMovido += vMovido.getAdjs().get(i).getNumSoldados();
            } else {
                bluesMovido += vMovido.getAdjs().get(i).getNumSoldados();
            }
        }
        BuscaEmLargura bfs = new BuscaEmLargura(Versus.grafo, posInimigo);
        //
        //Variável avanço:
        int varAvanco = camadas.length + 1 - this.getCamada(posMover);
        fitness = varAvanco + (bluesAtual - redsAtual);
        int varAvancoInimigo = this.getCamada(posInimigo) + 1;
        if (varAvanco != 0) {
            fitness += Math.pow(varAvanco, 1.5);
            fitness += Math.pow(varAvancoInimigo, 1.5);
        }
        //Variável distância: Quanto mais avancado o inimigo menor tem que ser a distancia
        int varDistancia = bfs.getMenorCaminho(posAtual).size() - bfs.getMenorCaminho(posMover).size();
        if (varDistancia != 0) {// varDistancia -1 0 1, se 1 aproxima e -1 afasta
            fitness += 10 * (varAvancoInimigo / varDistancia);
        }
        //Variável ultrapassagem: Se nivelInimigo positivo e varAvanco menor melhor
        int nivelInimigo = this.getCamada(posInimigo) / camadaMedia;
        fitness += 10 * (nivelInimigo / varAvanco);
        //Variável agrupamento
        double varAgrupamento = bluesMovido / ((double) redsMovido + 1) - bluesAtual / ((double) redsAtual + 1);
        fitness += varAgrupamento;
        //
        mod = Boolean.FALSE;
    }

    public Boolean isMod() {
        return mod;
    }

    public int getPosAtual() {
        return posAtual;
    }

    public int getPosMovido() {
        return posMover;
    }

    public int getPosInimigo() {
        return posInimigo;
    }

    public double getFitness() {
        if (mod) {
            this.calcularFitness();
        }
        return fitness;
    }

    @Override
    public int compareTo(Object o) {
        Double fit1 = this.getFitness();
        Double fit2 = ((Individuo) o).getFitness();
        return fit1.compareTo(fit2);
    }

}
