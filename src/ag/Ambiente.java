package ag;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import util.MersenneTwister;

/**
 *
 * @author Guilherme
 */
public class Ambiente {

    private MersenneTwister random;

    private Individuo elite;
    private List<Individuo> populacao;
    private int tamanho_populacao = 20;
    private int tamanho_torneio = 6;
    private double taxa_cruzamento = 0.8;// Influenciado pelo tam do cromossomo (a cada 1/tam_cro)
    private double taxa_mutacao = 0.3;// Influenciado pelo tam do cromossomo

    public Ambiente() {
        random = new MersenneTwister(System.nanoTime());
        this.gerarPopulacaoInicial();
    }

    //Inicio metodos AG
    private void gerarPopulacaoInicial() {
        this.setPopulacao(new ArrayList());
        this.completarPopulacao();
        this.elite = new Individuo(this.getMaiorFitness());
        //this.imprimePopulacao(-1);
    }

    private void addParaPopulacaoNovoIndividuo() {
        this.addParaPopulacao(new Individuo());
    }

    public void cruzamento() {
        List<Individuo> novaPopulacao = new ArrayList();
        this.embaralharPopulacao();
        for (int i = 0; i < this.getTamanhoAtualPopulacao() - 1; i += 2) {
            Individuo ind1 = this.getIndividuo(i);
            Individuo ind2 = this.getIndividuo(i + 1);
            if (random.nextBoolean(taxa_cruzamento)) {
                novaPopulacao.add(this.cruzar(ind1, ind2));
                novaPopulacao.add(this.cruzar(ind1, ind2));
            } else {
                novaPopulacao.add(ind1);
                novaPopulacao.add(ind2);
            }
        }
        this.setPopulacao(novaPopulacao);
        this.completarPopulacao();
    }

    private Individuo cruzar(Individuo pai, Individuo mae) {
        Individuo filho1 = new Individuo(pai);
        Individuo filho2 = new Individuo(mae);
        int troca = random.nextInt(3);
        if (troca == 0) {
            filho1.setPosAtual(mae.getPosAtual());
            filho2.setPosAtual(pai.getPosAtual());
        } else if (troca == 1) {
            filho1.setPosMovido(mae.getPosMovido());
            filho2.setPosMovido(pai.getPosMovido());
        } else if (troca == 2) {
            filho1.setPosInimigo(mae.getPosInimigo());
            filho2.setPosInimigo(pai.getPosInimigo());
        }
        if (filho2.getFitness() > filho1.getFitness()) {//Seleção natural do óvulo
            filho1 = filho2;
        }
        return filho1;
    }

    public void mutacao() {
        for (int i = 0; i < this.getTamanhoAtualPopulacao(); i++) {
            if (random.nextBoolean(taxa_mutacao)) {
                this.mutar(this.getIndividuo(i));
            }
        }
    }

    private void mutar(Individuo ind) {
        int troca = random.nextInt(3);
        if (troca == 0) {
            ind.novoPosAtualValido();
        } else if (troca == 1) {
            ind.novoPosMoverValido();
        } else if (troca == 2) {
            ind.novoPosInimigoValido();
        }
    }

    public void torneio() {
        Individuo idMaiorGeracao = this.getMaiorFitness();
        if (elite.getFitness() < idMaiorGeracao.getFitness()) {
            elite = new Individuo(idMaiorGeracao);
        }
        this.embaralharPopulacao();
        for (int i = 0; i < this.getTamanhoAtualPopulacao(); i += tamanho_torneio - 1) {//-1 individuo na população por torneio
            List<Individuo> torneio = this.getIndividuos(i, i + tamanho_torneio);
            Individuo re = Collections.min(torneio);
            //System.out.println("Removido: " + re.getFitness());
            this.removeIndividuo(re);
        }
    }

    private void completarPopulacao() {
        while (this.getTamanhoAtualPopulacao() < this.getTamanhoFixoPopulacao()) {
            this.addParaPopulacaoNovoIndividuo();
        }
    }

    //Fim metodos AG
    public Individuo getElite() {
        return elite;
    }

    private void embaralharPopulacao() {
        Collections.shuffle(populacao);
    }

    private void setPopulacao(List<Individuo> pop) {
        populacao = pop;
    }

    public void addParaPopulacao(Individuo ind) {
        populacao.add(ind);
    }

    public Individuo getIndividuo(int x) {
        return populacao.get(x);
    }

    public Individuo getIndividuoAleatorio() {
        return populacao.get(random.nextInt(populacao.size()));
    }

    public Individuo getIndividuoAleatorioDiferenteDe(Individuo ind) {
        Individuo ale = this.getIndividuoAleatorio();
        while (ale.equals(ind)) {
            ale = this.getIndividuoAleatorio();
        }
        return ale;
    }

    public List<Individuo> getIndividuos(int ini, int fim) {
        return populacao.subList(ini, (fim > populacao.size() ? populacao.size() : fim));
    }

    public Individuo removeIndividuo(int x) {
        return populacao.remove(x);
    }

    public boolean removeIndividuo(Individuo ind) {
        return populacao.remove(ind);
    }

    public int getTamanhoAtualPopulacao() {
        return populacao.size();
    }

    public int getTamanhoFixoPopulacao() {
        return tamanho_populacao;
    }

    public Individuo getMaiorFitness() {
        Double maior = -Double.MAX_VALUE;
        Individuo idMaior = null;
        for (int i = 0; i < this.getTamanhoAtualPopulacao(); i++) {
            if (this.getIndividuo(i).getFitness() > maior) {
                maior = this.getIndividuo(i).getFitness();
                idMaior = this.getIndividuo(i);
            }
        }
        return idMaior;
    }

    public Double getMediaFitness() {
        Double soma = 0.0;
        for (int i = 0; i < this.getTamanhoAtualPopulacao(); i++) {
            soma += this.getIndividuo(i).getFitness();
        }
        return (soma / this.getTamanhoAtualPopulacao());
    }

    public void imprimePopulacao(int interacao) {
        System.out.println("---------------------------------- Interação: " + interacao);
        System.out.println("Individuo Elite: Mover de " + elite.getPosAtual() + " para " + elite.getPosMovido() + " Fitness: " + elite.getFitness());
        for (int j = 0; j < populacao.size(); j++) {
            System.out.printf("Individuo: %-3s PosAtual: %-3s  PosMovido: %-3s PosInimigo: %-3s Fitness: %-3s", j, populacao.get(j).getPosAtual(), populacao.get(j).getPosMovido(), populacao.get(j).getPosInimigo(), populacao.get(j).getFitness());
            System.out.println("");
        }
        System.out.println("");
    }

}
