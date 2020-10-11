package ui;

import ag.Ambiente;
import java.awt.Color;
import util.MP3;
import util.MersenneTwister;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.border.Border;
import util.BuscaEmLargura;
import util.Grafo;

/**
 * Classe para o modo Jogar, contém todos os metodos e atributos do modo Jogar
 *
 * @author Guilherme Lima
 */
public class Versus extends javax.swing.JFrame {

    public static Grafo grafo;
    private JButton[] vetQuadradosVisuais = new JButton[23]; // objetos visuais
    private int[] vetPosicaoQuadradosY = new int[23];
    private int[] vetPosicaoQuadradosX = new int[23];
    private int[] vetPosicaoCentralY = new int[23];
    //Sons
    private MP3 somFundo = new MP3("somfundo");
    //Imagens
    private ImageIcon cacaRED = new ImageIcon(getClass().getResource("/imagVERS/cacaR.png"));
    private ImageIcon cacaBLUE = new ImageIcon(getClass().getResource("/imagVERS/cacaB.png"));
    private ImageIcon descida = new ImageIcon(getClass().getClassLoader().getResource("imagVERS/descida.gif"));
    private ImageIcon explosao = new ImageIcon(getClass().getClassLoader().getResource("imagVERS/explo.gif"));
    private ImageIcon bandeira = new ImageIcon(getClass().getClassLoader().getResource("imagVERS/bandeira.gif"));
    private ImageIcon icon = new ImageIcon(getClass().getClassLoader().getResource("imagVERS/icon.png"));
    private ImageIcon quadRED0 = new ImageIcon(getClass().getClassLoader().getResource("imagVERS/circRED0.png"));
    private ImageIcon quadRED1 = new ImageIcon(getClass().getClassLoader().getResource("imagVERS/circRED1.png"));
    private ImageIcon quadRED2 = new ImageIcon(getClass().getClassLoader().getResource("imagVERS/circRED2.png"));
    private ImageIcon quadRED3 = new ImageIcon(getClass().getClassLoader().getResource("imagVERS/circRED3.png"));
    private ImageIcon quadBLUE0 = new ImageIcon(getClass().getClassLoader().getResource("imagVERS/circBLUE0.png"));
    private ImageIcon quadBLUE1 = new ImageIcon(getClass().getClassLoader().getResource("imagVERS/circBLUE1.png"));
    private ImageIcon quadBLUE2 = new ImageIcon(getClass().getClassLoader().getResource("imagVERS/circBLUE2.png"));
    private ImageIcon quadBLUE3 = new ImageIcon(getClass().getClassLoader().getResource("imagVERS/circBLUE3.png"));
    //
    public static MersenneTwister rand = new MersenneTwister(System.nanoTime());
    private boolean atualRED, chamouTriploAviaoRED, chamouAviaoRED, chamouTriploAviaoBLUE, chamouAviaoBLUE;
    private int numMovimentos, numAtaques, quadSelecionado, exploRED, exploBLUE, numJogos, chamouAviaoREDIdx, chamouAviaoBLUEIdx;
    private String status, modo, nome_jogador;
    private int vitoriasRED = 0, vitoriasBLUE = 0;
    //
    private DataOutputStream ostream = null;
    private DataInputStream istream = null;
    private Border bordaQuadrado = new javax.swing.border.LineBorder(new java.awt.Color(250, 250, 0), 1, true);

    //Configs
    private int custoReforços = 2;
    private int custoAereo = 3;
    private int tempoTurno = 40;
    private double chanceAtaqueMatar = 0.35;
    private double chanceRevideMatar = 0.65;
    private boolean isMusica = true;
    private boolean isEfeitos = true;


    /*
     * Gerencia a conexão entre o servidor e o cliente
     */
    private void redeGerenciarConexão() {
        new Thread() {
            public void run() {
                try {
                    String operação;
                    if (modo.equals("servidor")) {
                        ostream.writeUTF(numJogos + "");
                    } else if (modo.equals("cliente")) {
                        operação = istream.readUTF();
                        numJogos = Integer.parseInt(operação);
                        ostream.writeUTF(nome_jogador);
                    }
                    if (modo.equals("servidor")) {
                        String nomeCliente = istream.readUTF();
                        setTitle("GLIMA War Game | " + nome_jogador + "(RED) vs " + nomeCliente + "(BLUE)");
                        ostream.writeUTF(nome_jogador);
                    } else if (modo.equals("cliente")) {
                        String nomeServidor = istream.readUTF();
                        setTitle("GLIMA War Game | " + nomeServidor + "(RED) vs " + nome_jogador + "(BLUE)");
                    }
                    while (true) { // os dois ficam em espera           
                        operação = istream.readUTF();
                        if (operação.charAt(0) == 'A' || operação.charAt(0) == 'M') {
                            redeRecebeQuadrados(operação);
                        } else if (operação.charAt(0) == 'V') {
                            redeRecebeAereo(operação);
                        } else if (operação.charAt(0) == 'R') {
                            redeRecebeReforços(operação);
                        } else if (operação.charAt(0) == 'P' && operação.charAt(1) == 'R' && modo.equals("cliente")) {
                            exploRED = Integer.parseInt(operação.substring(2, 4));
                            proExplosRED.setValue(exploRED);
                            proExplosRED.setString("" + exploRED);
                            passarVez();
                        } else if (operação.charAt(0) == 'P' && operação.charAt(1) == 'B' && modo.equals("servidor")) {
                            exploBLUE = Integer.parseInt(operação.substring(2, 4));
                            proExplosBLUE.setValue(exploBLUE);
                            proExplosBLUE.setString("" + exploBLUE);
                            passarVez();
                        } else if (operação.charAt(0) == 'E') {
                            vitoriaEsquerda();
                        } else if (operação.charAt(0) == 'D') {
                            vitoriaDireita();
                        }
                    }
                } catch (IOException ex) {
                    Logger.getLogger(Versus.class.getName()).log(Level.SEVERE, null, ex);
                    txtStatus.setText("O oponente está desconectado");
                    camadas.setEnabled(false);
                }
            }
        }.start();
    }

    private void redeEnviaQuadrados(String tipo) {
        String cores = tipo, quantidades = "";
        for (int x = 0; x < vetQuadradosVisuais.length; x++) {
            if (grafo.getVertice(x).isCorRED()) {
                cores += "R";
            } else {
                cores += "B";
            }
            quantidades += grafo.getVertice(x).getNumSoldados();
        }
        cores += quantidades;
        try {
            ostream.writeUTF(cores);
            ostream.flush();
        } catch (IOException ex) {
            Logger.getLogger(Versus.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    private void redeRecebeQuadrados(String pacote) {
        //Para Ataque do oponente ativa sons
        if (pacote.charAt(0) == 'A' && isEfeitos) {
            new MP3("atq" + (rand.nextInt(5) + 1)).play();
        }
        // Atualiza todos os quadrados
        int intNumSquares = vetQuadradosVisuais.length;
        for (int x = 1; x <= intNumSquares; x++) {
            int num_solds = Integer.parseInt("" + pacote.charAt(x + intNumSquares));
            grafo.getVertice(x - 1).setNumSoldados(num_solds);
            if (pacote.charAt(x) == 'R') {
                grafo.getVertice(x - 1).setCorRED(true);
                if (num_solds == 0) {
                    vetQuadradosVisuais[x - 1].setIcon(quadRED0);
                    vetQuadradosVisuais[x - 1].setDisabledIcon(quadRED0);
                }
                if (num_solds == 1) {
                    vetQuadradosVisuais[x - 1].setIcon(quadRED1);
                    vetQuadradosVisuais[x - 1].setDisabledIcon(quadRED1);
                }
                if (num_solds == 2) {
                    vetQuadradosVisuais[x - 1].setIcon(quadRED2);
                    vetQuadradosVisuais[x - 1].setDisabledIcon(quadRED2);
                }
                if (num_solds == 3) {
                    vetQuadradosVisuais[x - 1].setIcon(quadRED3);
                    vetQuadradosVisuais[x - 1].setDisabledIcon(quadRED3);
                }
            } else {
                grafo.getVertice(x - 1).setCorRED(false);
                if (num_solds == 0) {
                    vetQuadradosVisuais[x - 1].setIcon(quadBLUE0);
                    vetQuadradosVisuais[x - 1].setDisabledIcon(quadBLUE0);
                }
                if (num_solds == 1) {
                    vetQuadradosVisuais[x - 1].setIcon(quadBLUE1);
                    vetQuadradosVisuais[x - 1].setDisabledIcon(quadBLUE1);
                }
                if (num_solds == 2) {
                    vetQuadradosVisuais[x - 1].setIcon(quadBLUE2);
                    vetQuadradosVisuais[x - 1].setDisabledIcon(quadBLUE2);
                }
                if (num_solds == 3) {
                    vetQuadradosVisuais[x - 1].setIcon(quadBLUE3);
                    vetQuadradosVisuais[x - 1].setDisabledIcon(quadBLUE3);
                }
            }
            vetQuadradosVisuais[x - 1].repaint();
        }
    }

    private void redeEnviaAereo(int quadrado) {
        String pos = "V";
        if (atualRED) {
            pos += "R";
        } else {
            pos += "B";
        }
        if (quadrado <= 9) {
            pos += "0" + quadrado;
        } else {
            pos += quadrado;
        }
        try {
            ostream.writeUTF(pos);
            ostream.flush();
        } catch (IOException ex) {
            Logger.getLogger(Versus.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    private void redeRecebeAereo(String pacote) {
        final int quadrado = Integer.parseInt(pacote.substring(2, 4));
        final boolean aux = atualRED;
        if (pacote.charAt(1) == 'R') {
            atualRED = true;
        }
        if (pacote.charAt(1) == 'B') {
            atualRED = false;
        }
        new Thread() {
            public void run() {
                ataqueAereo(quadrado);
                atualRED = aux;
            }
        }.start();
    }

    private void redeEnviaReforços(int quadrado) {
        String pos = "R";
        if (atualRED) {
            pos += "R";
        } else {
            pos += "B";
        }
        if (quadrado <= 9) {
            pos += "0" + quadrado;
        } else {
            pos += quadrado;
        }
        try {
            ostream.writeUTF(pos);
            ostream.flush();
        } catch (IOException ex) {
            Logger.getLogger(Versus.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    private void redeRecebeReforços(String pacote) {
        final int quadrado = Integer.parseInt(pacote.substring(2, 4));
        final boolean aux = atualRED;
        if (pacote.charAt(1) == 'R') {
            atualRED = true;
        }
        if (pacote.charAt(1) == 'B') {
            atualRED = false;
        }
        new Thread() {
            public void run() {
                chamarReforços(quadrado);
                atualRED = aux;
            }
        }.start();
    }

    /**
     * Construtor da classe Jogar. Inicializa todos os componentes gráficos e
     * lógicos.
     */
    public Versus(String modo, int numJogos, DataOutputStream ostream, DataInputStream istream, String nome) {
        this.modo = modo;
        this.numJogos = numJogos;
        this.ostream = ostream;
        this.istream = istream;
        this.nome_jogador = nome;
        this.initComponents();
        this.setVisible(true);
        this.setIconImage(icon.getImage());
        this.setLocationRelativeTo(null);
        this.inicializarQuadradosVisuais();
        btnPassar.setEnabled(false);
        //lado RED
        esqVitoria.setEnabled(false);
        btnReforçosRED.setVisible(false);
        btnAereoRED.setVisible(false);
        btnTriploAereoRED.setVisible(false);
        proExplosRED.setValue(0);
        proExplosRED.setString("0");
        lblAtaqueRED.setVisible(false);
        lblMoveRED.setVisible(false);
        lblHeliRED.setVisible(false);
        //lado BLUE
        dirVitoria.setEnabled(false);
        btnReforçosBLUE.setVisible(false);
        btnAereoBLUE.setVisible(false);
        btnTriploAereoBLUE.setVisible(false);
        proExplosBLUE.setValue(0);
        proExplosBLUE.setString("0");
        lblAtaqueBLUE.setVisible(false);
        lblMoveBLUE.setVisible(false);
        lblHeliBLUE.setVisible(false);
        //outros
        lblResult1.setVisible(false);
        lblResultX.setVisible(false);
        lblResult2.setVisible(false);
        //placar
        lblVitorias.setVisible(false);
        lblVitoriasX.setVisible(false);
        lblVitoriasRED.setVisible(false);
        lblVitoriasBLUE.setVisible(false);
        lblVitoriasPanRED.setVisible(false);
        lblVitoriasPanBLUE.setVisible(false);
        lblVitoriasQuadro.setVisible(false);
        lblVitoriasCont.setVisible(false);
        if (modo.equals("servidor") || modo.equals("cliente")) {
            redeGerenciarConexão();
        }
        animacaoInicial();
    }

    /**
     * Método para marcar todos os quadrados como não selecionaveis
     */
    private void limparSelecionaveis() {
        for (int x = 0; x < vetQuadradosVisuais.length; x++) {
            vetQuadradosVisuais[x].setEnabled(false);
            vetQuadradosVisuais[x].setBorder(null);
            vetQuadradosVisuais[x].repaint();
        }
    }

    /**
     * Método que realiza a animação gráfica dos blocos sendo criados. Usado
     * apenas no ínicio.
     */
    private void animacaoInicial() {
        this.inicializarGrafo();
        for (int x = 0; x < vetQuadradosVisuais.length; x++) {
            vetQuadradosVisuais[x].setVisible(false);
        }
        new Thread() {
            public void run() {
                if(isEfeitos)
                 new MP3("blocos").play();
                try {
                    Thread.sleep(500);
                } catch (InterruptedException ex) {
                    Logger.getLogger(Versus.class.getName()).log(Level.SEVERE, null, ex);
                }
                esqVitoria.setVisible(true);
                dirVitoria.setVisible(true);
                try {
                    Thread.sleep(500);
                } catch (InterruptedException ex) {
                    Logger.getLogger(Versus.class.getName()).log(Level.SEVERE, null, ex);
                }
                vetQuadradosVisuais[1].setVisible(true);
                vetQuadradosVisuais[21].setVisible(true);
                try {
                    Thread.sleep(500);
                } catch (InterruptedException ex) {
                    Logger.getLogger(Versus.class.getName()).log(Level.SEVERE, null, ex);
                }
                vetQuadradosVisuais[4].setVisible(true);
                vetQuadradosVisuais[18].setVisible(true);
                try {
                    Thread.sleep(500);
                } catch (InterruptedException ex) {
                    Logger.getLogger(Versus.class.getName()).log(Level.SEVERE, null, ex);
                }
                vetQuadradosVisuais[8].setVisible(true);
                vetQuadradosVisuais[14].setVisible(true);
                try {
                    Thread.sleep(500);
                } catch (InterruptedException ex) {
                    Logger.getLogger(Versus.class.getName()).log(Level.SEVERE, null, ex);
                }
                vetQuadradosVisuais[13].setVisible(true);
                vetQuadradosVisuais[9].setVisible(true);
                try {
                    Thread.sleep(500);
                } catch (InterruptedException ex) {
                    Logger.getLogger(Versus.class.getName()).log(Level.SEVERE, null, ex);
                }
                vetQuadradosVisuais[5].setVisible(true);
                vetQuadradosVisuais[17].setVisible(true);
                try {
                    Thread.sleep(500);
                } catch (InterruptedException ex) {
                    Logger.getLogger(Versus.class.getName()).log(Level.SEVERE, null, ex);
                }
                vetQuadradosVisuais[10].setVisible(true);
                vetQuadradosVisuais[12].setVisible(true);
                try {
                    Thread.sleep(500);
                } catch (InterruptedException ex) {
                    Logger.getLogger(Versus.class.getName()).log(Level.SEVERE, null, ex);
                }
                vetQuadradosVisuais[7].setVisible(true);
                vetQuadradosVisuais[15].setVisible(true);
                try {
                    Thread.sleep(500);
                } catch (InterruptedException ex) {
                    Logger.getLogger(Versus.class.getName()).log(Level.SEVERE, null, ex);
                }
                vetQuadradosVisuais[19].setVisible(true);
                vetQuadradosVisuais[3].setVisible(true);
                try {
                    Thread.sleep(500);
                } catch (InterruptedException ex) {
                    Logger.getLogger(Versus.class.getName()).log(Level.SEVERE, null, ex);
                }
                vetQuadradosVisuais[0].setVisible(true);
                vetQuadradosVisuais[22].setVisible(true);
                try {
                    Thread.sleep(500);
                } catch (InterruptedException ex) {
                    Logger.getLogger(Versus.class.getName()).log(Level.SEVERE, null, ex);
                }
                vetQuadradosVisuais[2].setVisible(true);
                vetQuadradosVisuais[20].setVisible(true);
                try {
                    Thread.sleep(500);
                } catch (InterruptedException ex) {
                    Logger.getLogger(Versus.class.getName()).log(Level.SEVERE, null, ex);
                }
                vetQuadradosVisuais[6].setVisible(true);
                vetQuadradosVisuais[16].setVisible(true);
                try {
                    Thread.sleep(500);
                } catch (InterruptedException ex) {
                    Logger.getLogger(Versus.class.getName()).log(Level.SEVERE, null, ex);
                }
                vetQuadradosVisuais[11].setVisible(true);
                //
                iniciarGame();
                //
                for (int x = 0; x <= 15; x++) {
                    proExplosRED.setValue(x);
                    proExplosBLUE.setValue(x);
                    proExplosRED.setString("" + x);
                    proExplosBLUE.setString("" + x);
                    try {
                        Thread.sleep(150);
                    } catch (InterruptedException ex) {
                        Logger.getLogger(Versus.class.getName()).log(Level.SEVERE, null, ex);
                    }
                }
                // RED inicia
                if (modo.equals("servidor") || modo.equals("ai")) {
                    btnReforçosRED.setVisible(true);
                    try {
                        Thread.sleep(150);
                    } catch (InterruptedException ex) {
                        Logger.getLogger(Versus.class.getName()).log(Level.SEVERE, null, ex);
                    }
                    btnAereoRED.setVisible(true);
                    try {
                        Thread.sleep(150);
                    } catch (InterruptedException ex) {
                        Logger.getLogger(Versus.class.getName()).log(Level.SEVERE, null, ex);
                    }
                    lblAtaqueRED.setVisible(true);
                    try {
                        Thread.sleep(150);
                    } catch (InterruptedException ex) {
                        Logger.getLogger(Versus.class.getName()).log(Level.SEVERE, null, ex);
                    }
                    lblMoveRED.setVisible(true);
                    try {
                        Thread.sleep(150);
                    } catch (InterruptedException ex) {
                        Logger.getLogger(Versus.class.getName()).log(Level.SEVERE, null, ex);
                    }
                    btnTriploAereoRED.setVisible(true);
                } else if (modo.equals("cliente")) {
                    btnPassar.setEnabled(false);
                }
                System.out.println("Jogador: " + nome_jogador + " cor:" + ((atualRED) ? "RED" : "BLUE"));
                lblResult1.setVisible(true);
                lblResultX.setVisible(true);
                lblResult2.setVisible(true);
            }
        }.start();
    }

    /**
     * Método que cria todos os quadrados gráficos e lógicos
     */
    private void inicializarQuadradosVisuais() {
        java.awt.event.ActionListener action = new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                for (int i = 0; i < vetQuadradosVisuais.length; i++) {
                    if (evt.getSource().equals(vetQuadradosVisuais[i])) {
                        tratarCliqueQuadrado(i);
                    }
                }
            }
        };

        vetQuadradosVisuais[0] = quadrado0;
        vetQuadradosVisuais[1] = quadrado1;
        vetQuadradosVisuais[2] = quadrado2;
        vetQuadradosVisuais[3] = quadrado3;
        vetQuadradosVisuais[4] = quadrado4;
        vetQuadradosVisuais[5] = quadrado5;
        vetQuadradosVisuais[6] = quadrado6;
        vetQuadradosVisuais[7] = quadrado7;
        vetQuadradosVisuais[8] = quadrado8;
        vetQuadradosVisuais[9] = quadrado9;
        vetQuadradosVisuais[10] = quadrado10;
        vetQuadradosVisuais[11] = quadrado11;
        vetQuadradosVisuais[12] = quadrado12;
        vetQuadradosVisuais[13] = quadrado13;
        vetQuadradosVisuais[14] = quadrado14;
        vetQuadradosVisuais[15] = quadrado15;
        vetQuadradosVisuais[16] = quadrado16;
        vetQuadradosVisuais[17] = quadrado17;
        vetQuadradosVisuais[18] = quadrado18;
        vetQuadradosVisuais[19] = quadrado19;
        vetQuadradosVisuais[20] = quadrado20;
        vetQuadradosVisuais[21] = quadrado21;
        vetQuadradosVisuais[22] = quadrado22;

        for (int i = 0; i < vetQuadradosVisuais.length; i++) {
            JButton btn = vetQuadradosVisuais[i];
            btn.addActionListener(action);
            vetPosicaoQuadradosX[i] = btn.getBounds().x;
            vetPosicaoQuadradosY[i] = btn.getBounds().y;
            vetPosicaoCentralY[i] = Math.round(btn.getBounds().y + (btn.getBounds().height / 2));
            System.out.println("Square " + i + " x: " + vetPosicaoQuadradosX[i] + " Y: " + vetPosicaoQuadradosY[i]);
        }
    }

    public void inicializarGrafo() {
        grafo = new Grafo(vetQuadradosVisuais.length);
        grafo.insereAresta(0, 1);
        grafo.insereAresta(0, 2);
        grafo.insereAresta(0, 3);
        grafo.insereAresta(1, 3);
        grafo.insereAresta(1, 4);
        grafo.insereAresta(2, 3);
        grafo.insereAresta(2, 5);
        grafo.insereAresta(2, 6);
        grafo.insereAresta(3, 4);
        grafo.insereAresta(3, 6);
        grafo.insereAresta(3, 7);
        grafo.insereAresta(4, 7);
        grafo.insereAresta(4, 8);
        grafo.insereAresta(5, 6);
        grafo.insereAresta(5, 9);
        grafo.insereAresta(5, 10);
        grafo.insereAresta(6, 7);
        grafo.insereAresta(6, 10);
        grafo.insereAresta(6, 11);
        grafo.insereAresta(7, 8);
        grafo.insereAresta(7, 11);
        grafo.insereAresta(7, 12);
        grafo.insereAresta(8, 12);
        grafo.insereAresta(8, 13);
        grafo.insereAresta(9, 10);
        grafo.insereAresta(9, 14);
        grafo.insereAresta(10, 11);
        grafo.insereAresta(10, 14);
        grafo.insereAresta(10, 15);
        grafo.insereAresta(11, 12);
        grafo.insereAresta(11, 15);
        grafo.insereAresta(11, 16);
        grafo.insereAresta(12, 13);
        grafo.insereAresta(12, 16);
        grafo.insereAresta(12, 17);
        grafo.insereAresta(13, 17);
        grafo.insereAresta(14, 15);
        grafo.insereAresta(14, 18);
        grafo.insereAresta(15, 16);
        grafo.insereAresta(15, 18);
        grafo.insereAresta(15, 19);
        grafo.insereAresta(16, 17);
        grafo.insereAresta(16, 19);
        grafo.insereAresta(16, 20);
        grafo.insereAresta(17, 20);
        grafo.insereAresta(18, 19);
        grafo.insereAresta(18, 21);
        grafo.insereAresta(19, 20);
        grafo.insereAresta(19, 21);
        grafo.insereAresta(19, 22);
        grafo.insereAresta(20, 22);
        grafo.insereAresta(21, 22);
    }

    /**
     * Método que dispoe todos os componentes na posição inicial
     */
    private void iniciarGame() {
        somFundo.play();
        for (int x = 0; x < vetQuadradosVisuais.length; x++) {
            if (x < 12) {
                if (x == 2 || x == 3 || x == 4) {
                    vetQuadradosVisuais[x].setIcon(quadRED3);
                    vetQuadradosVisuais[x].setDisabledIcon(quadRED3);
                    grafo.setVertice(x, true, 3);
                } else {
                    vetQuadradosVisuais[x].setIcon(quadRED0);
                    vetQuadradosVisuais[x].setDisabledIcon(quadRED0);
                    grafo.setVertice(x, true, 0);
                }
            } else {
                if (x == 18 || x == 19 || x == 20) {
                    vetQuadradosVisuais[x].setIcon(quadBLUE3);
                    vetQuadradosVisuais[x].setDisabledIcon(quadBLUE3);
                    grafo.setVertice(x, false, 3);
                } else {
                    vetQuadradosVisuais[x].setIcon(quadBLUE0);
                    vetQuadradosVisuais[x].setDisabledIcon(quadBLUE0);
                    grafo.setVertice(x, false, 0);
                }
            }
            vetQuadradosVisuais[x].setEnabled(false);
            vetQuadradosVisuais[x].repaint();
        }
        lblRed.setVisible(true);
        lblBlue.setVisible(true);
        bandir.setIcon(null);
        banesq.setIcon(null);
        camadas.moveToBack(bandir);
        camadas.moveToBack(banesq);
        esqVitoria.setIcon(quadRED0);
        esqVitoria.setDisabledIcon(quadRED0);
        esqVitoria.setEnabled(false);
        dirVitoria.setIcon(quadBLUE0);
        dirVitoria.setDisabledIcon(quadBLUE0);
        dirVitoria.setEnabled(false);
        vitoriasRED = 0;
        vitoriasBLUE = 0;
        chamouTriploAviaoRED = false;
        chamouAviaoRED = false;
        chamouTriploAviaoBLUE = false;
        chamouAviaoBLUE = false;
        exploRED = 15;
        exploBLUE = 15;
        passarVez();
    }

    /**
     * Método que realiza a troca da imagem de um quadrado.
     *
     * @param sold Deternina a quantidade de soldados que a imagem a ser trocada
     * de ter
     * @param quad Determina o quadrado que terá a imagem trocada
     */
    private void trocarImagemQuadrado(int sold, int quad) {
        if (atualRED) {
            if (sold == 0) {
                vetQuadradosVisuais[quad].setIcon(quadRED0);
                vetQuadradosVisuais[quad].setDisabledIcon(quadRED0);
            }
            if (sold == 1) {
                vetQuadradosVisuais[quad].setIcon(quadRED1);
                vetQuadradosVisuais[quad].setDisabledIcon(quadRED1);
            }
            if (sold == 2) {
                vetQuadradosVisuais[quad].setIcon(quadRED2);
                vetQuadradosVisuais[quad].setDisabledIcon(quadRED2);
            }
            if (sold == 3) {
                vetQuadradosVisuais[quad].setIcon(quadRED3);
                vetQuadradosVisuais[quad].setDisabledIcon(quadRED3);
            }
        } else {
            if (sold == 0) {
                vetQuadradosVisuais[quad].setIcon(quadBLUE0);
                vetQuadradosVisuais[quad].setDisabledIcon(quadBLUE0);
            }
            if (sold == 1) {
                vetQuadradosVisuais[quad].setIcon(quadBLUE1);
                vetQuadradosVisuais[quad].setDisabledIcon(quadBLUE1);
            }
            if (sold == 2) {
                vetQuadradosVisuais[quad].setIcon(quadBLUE2);
                vetQuadradosVisuais[quad].setDisabledIcon(quadBLUE2);
            }
            if (sold == 3) {
                vetQuadradosVisuais[quad].setIcon(quadBLUE3);
                vetQuadradosVisuais[quad].setDisabledIcon(quadBLUE3);
            }
        }
        vetQuadradosVisuais[quad].paint(vetQuadradosVisuais[quad].getGraphics());
    }

    /**
     * Método que realiza a troca entre os jogadores RED e BLUE
     */
    private void passarVez() {
        limparSelecionaveis();
        atualRED = !atualRED;
        numMovimentos = 3;
        numAtaques = 3;
        executarChamadasAviao();
        iniciarTempoTurno();
        if (atualRED) {
            btnPassar.setEnabled(false);
            lblBlue.setVisible(false);
            lblAtaqueBLUE.setVisible(false);
            lblMoveBLUE.setVisible(false);
            btnReforçosBLUE.setVisible(false);
            btnAereoBLUE.setVisible(false);
            btnTriploAereoBLUE.setVisible(false);
            //
            lblRed.setVisible(true);
            exploRED++;
            if (exploRED > 25) {
                exploRED = 25;
            }
            lblAtaqueRED.setText("Ataques: " + numAtaques + " ");
            lblMoveRED.setText("Movimentos: " + numMovimentos + " ");
            proExplosRED.setValue(exploRED);
            proExplosRED.setString("" + exploRED);
            if (modo.equals("ai") || modo.equals("servidor")) {
                lblAtaqueRED.setVisible(true);
                lblMoveRED.setVisible(true);
                btnReforçosRED.setVisible(true);
                btnAereoRED.setVisible(true);
                btnTriploAereoRED.setVisible(true);
                btnPassar.setEnabled(true);
                movimentar();
            }
            txtStatus.setText("Vez do jogador RED!");
        } else {
            btnPassar.setEnabled(false);
            lblRed.setVisible(false);
            lblAtaqueRED.setVisible(false);
            lblMoveRED.setVisible(false);
            btnReforçosRED.setVisible(false);
            btnAereoRED.setVisible(false);
            btnTriploAereoRED.setVisible(false);
            //
            lblBlue.setVisible(true);
            exploBLUE++;
            if (exploBLUE > 25) {
                exploBLUE = 25;
            }
            lblAtaqueBLUE.setText("Ataques: " + numAtaques + " ");
            lblMoveBLUE.setText("Movimentos: " + numMovimentos + " ");
            proExplosBLUE.setValue(exploBLUE);
            proExplosBLUE.setString("" + exploBLUE);
            if (modo.equals("cliente")) {
                lblAtaqueBLUE.setVisible(true);
                lblMoveBLUE.setVisible(true);
                btnReforçosBLUE.setVisible(true);
                btnAereoBLUE.setVisible(true);
                btnTriploAereoBLUE.setVisible(true);
                btnPassar.setEnabled(true);
                movimentar();
            } else if (modo.equals("ai")) {
                ia();
            }
            txtStatus.setText("Vez do jogador BLUE!");
        }
    }

    public void iniciarTempoTurno() {
        new Thread() {
            public void run() {
                boolean atual = atualRED;
                for (int i = tempoTurno; i >= 0; i--) {
                    if (atual) {
                        lblRed.setText(String.format("%d", (i)));
                    } else {
                        lblBlue.setText(String.format("%d", (i)));
                    }
                    if (atual != atualRED) {
                        break;
                    }
                    try {
                        Thread.sleep(1000);
                    } catch (InterruptedException ex) {
                        Logger.getLogger(Versus.class.getName()).log(Level.SEVERE, null, ex);
                    }
                }
                if (atual == atualRED) {
                    passarVez();
                }
            }
        }.start();
    }

    /**
     * Método que realiza o movimento, entre quadrados, de soldados.
     */
    private void movimentar() {
        txtStatus.setText("Clique no grupo que deseja movimentar");
        status = "move1";
        for (int x = 0; x < vetQuadradosVisuais.length; x++) {
            if (grafo.getVertice(x).isCorRED() == atualRED && grafo.getVertice(x).temSoldados()) {
                vetQuadradosVisuais[x].setEnabled(true);
                vetQuadradosVisuais[x].setBorder(bordaQuadrado);
                vetQuadradosVisuais[x].repaint();
            }
        }
        if (numMovimentos == 0) {
            txtStatus.setText("Não é mais possível movimentar!");
            if (numAtaques == 0) {
                btnPassarActionPerformed(null);
            }
        }
        if (numAtaques == 0) {
            txtStatus.setText("Não é mais possível atacar!");
            if (numMovimentos == 0) {
                btnPassarActionPerformed(null);
            }
        }
    }

    /**
     * Método para tratar o clique em um quadrado. Decidindo quando é um clique
     * de ataque ou movimento.
     */
    private void tratarCliqueQuadrado(final int num) {
        List<Grafo.Vertice> adjs = grafo.verticesAdjacentesDe(num);
        if (status.equals("move1")) {
            lblResult1.setText(" ");
            lblResult2.setText(" ");
            status = "move2";
            limparSelecionaveis();
            for (Grafo.Vertice v : adjs) {
                int idxV = grafo.indexVertice(v);
                if (numMovimentos > 0) {
                    if (v.isCorRED() == atualRED) {
                        if (v.getNumSoldados() != 3) {
                            vetQuadradosVisuais[idxV].setEnabled(true);
                            vetQuadradosVisuais[idxV].setBorder(bordaQuadrado);
                        }
                    } else {
                        if (v.getNumSoldados() == 0) {
                            vetQuadradosVisuais[idxV].setEnabled(true);
                            vetQuadradosVisuais[idxV].setBorder(bordaQuadrado);
                        }
                    }
                    //se tiver perto da bandeira do inimigo libera bandeira
                    if ((num == 0 || num == 1) && !grafo.getVertice(num).isCorRED()) {
                        esqVitoria.setEnabled(true);
                        esqVitoria.setBorder(bordaQuadrado);
                    }
                    if ((num == 21 || num == 22) && grafo.getVertice(num).isCorRED()) {
                        dirVitoria.setEnabled(true);
                        dirVitoria.setBorder(bordaQuadrado);
                    }
                }
                if (numAtaques > 0) {
                    if (v.isCorRED() != atualRED) {
                        if (v.temSoldados()) {
                            vetQuadradosVisuais[idxV].setEnabled(true);
                            vetQuadradosVisuais[idxV].setBorder(bordaQuadrado);
                        }
                    }
                }
            }
            vetQuadradosVisuais[num].setEnabled(true);
            vetQuadradosVisuais[num].setBorder(bordaQuadrado);
            txtStatus.setText("Clique em um dos campos selecionados!");
            quadSelecionado = num;
        } else if (status.equals("move2")) {
            limparSelecionaveis();
            if (quadSelecionado == num) {
                status = "move1"; // se cliquar no msm volta para move1
            } else if (modo.equals("ai") && !atualRED && numMovimentos > 1 && grafo.getVertice(quadSelecionado).getNumSoldados() > 1 && grafo.getVertice(num).getNumSoldados() < 2) {
                //movimento especial para ia
                grafo.getVertice(quadSelecionado).menosUmSoldado();
                trocarImagemQuadrado(grafo.getVertice(quadSelecionado).getNumSoldados(), quadSelecionado);
                grafo.getVertice(num).maisUmSoldado();
                trocarImagemQuadrado(grafo.getVertice(num).getNumSoldados(), num);
                grafo.getVertice(num).setCorRED(atualRED);
                numMovimentos--;
                try {
                    Thread.sleep(2000);
                } catch (InterruptedException ex) {
                    Logger.getLogger(Versus.class.getName()).log(Level.SEVERE, null, ex);
                }
                grafo.getVertice(quadSelecionado).menosUmSoldado();
                trocarImagemQuadrado(grafo.getVertice(quadSelecionado).getNumSoldados(), quadSelecionado);
                grafo.getVertice(num).maisUmSoldado();
                trocarImagemQuadrado(grafo.getVertice(num).getNumSoldados(), num);
                grafo.getVertice(num).setCorRED(atualRED);
                numMovimentos--;
            } else if (grafo.getVertice(num).isCorRED() != atualRED && grafo.getVertice(num).temSoldados()) {
                // se o quadrado clicado não é da mesma cor e tem soldados, é ataque.
                btnPassar.setEnabled(false);
                int numAtacantes = 0;
                for (Grafo.Vertice v : adjs) {
                    //int idxV = grafo.indexVertice(v);
                    if (atualRED) {
                        if (v.temSoldados() && v.isCorRED()) {
                            numAtacantes += v.getNumSoldados();
                        }
                    } else {
                        if (v.temSoldados() == true && !v.isCorRED()) {
                            numAtacantes += v.getNumSoldados();
                        }
                    }
                }
                int numDefensores = grafo.getVertice(num).getNumSoldados();
                limparSelecionaveis();
                txtStatus.setText("Ataques em ataque!");
                if(isEfeitos)
                  new MP3("atq" + (rand.nextInt(6) + 1)).play();
                try {
                    Thread.sleep(1200);
                } catch (InterruptedException ex) {
                    Logger.getLogger(Versus.class.getName()).log(Level.SEVERE, null, ex);
                }
                int atacantesMortos = 0, defensoresMortos = 0;
                //Cada defensor passa por uma chance de morre para um dos atacantes
                atualRED = !atualRED; // para deletar defensores do oponente                
                for (int i = 0; i < numDefensores; i++) {
                    for (int x = 0; x < numAtacantes; x++) {
                        if (rand.nextBoolean(chanceAtaqueMatar)) {
                            grafo.getVertice(num).menosUmSoldado();
                            trocarImagemQuadrado(grafo.getVertice(num).getNumSoldados(), num);
                            numDefensores--;
                            defensoresMortos++;
                            if (numDefensores == 0) {
                                break;
                            }
                        }
                    }
                }
                atualRED = !atualRED;
                if (numDefensores != 0) {
                    txtStatus.setText("Denfensores revidam!");
                    if(isEfeitos)
                        new MP3("atq" + (rand.nextInt(6) + 1)).play();
                    try {
                        Thread.sleep(1200);
                    } catch (InterruptedException ex) {
                        Logger.getLogger(Versus.class.getName()).log(Level.SEVERE, null, ex);
                    }
                    //Cada atacante passa por uma chance de morre para um dos defensores
                    for (int i = 0; i < numAtacantes; i++) {
                        for (int x = 0; x < numDefensores; x++) {
                            if (rand.nextBoolean(chanceRevideMatar)) {
                                Grafo.Vertice aleatorio = adjs.get(rand.nextInt(adjs.size()));
                                while (!aleatorio.temSoldados() || aleatorio.isCorRED() != atualRED) {
                                    aleatorio = adjs.get(rand.nextInt(adjs.size()));
                                }
                                aleatorio.menosUmSoldado();
                                trocarImagemQuadrado(aleatorio.getNumSoldados(), grafo.indexVertice(aleatorio));
                                numAtacantes--;
                                atacantesMortos++;
                                if (atualRED) {
                                    proExplosBLUE.setValue(++exploBLUE);
                                    proExplosBLUE.setString("" + exploBLUE);
                                } else {
                                    proExplosRED.setValue(++exploRED);
                                    proExplosRED.setString("" + exploRED);
                                }
                                if (numAtacantes == 0) {
                                    break;
                                }
                            }
                        }
                    }
                }
                numAtaques--;
                lblResult1.setText("Perdeu: " + (atualRED ? atacantesMortos : defensoresMortos));
                lblResult2.setText("Perdeu: " + (!atualRED ? atacantesMortos : defensoresMortos));
                lblAtaqueRED.setText("Ataques: " + numAtaques + " ");
                txtStatus.setText("Ataque realizado!");
                if (modo.equals("servidor") || modo.equals("cliente")) {
                    redeEnviaQuadrados("A");
                }
            } else {
                // se não é ataque é movimento
                if (grafo.getVertice(num).isCorRED() && !atualRED) {
                    proExplosBLUE.setValue(++exploBLUE);
                    proExplosBLUE.setString("" + exploBLUE);
                }
                if (!grafo.getVertice(num).isCorRED() && atualRED) {
                    proExplosRED.setValue(++exploRED);
                    proExplosRED.setString("" + exploRED);
                }
                grafo.getVertice(quadSelecionado).menosUmSoldado();
                trocarImagemQuadrado(grafo.getVertice(quadSelecionado).getNumSoldados(), quadSelecionado);
                grafo.getVertice(num).maisUmSoldado();
                trocarImagemQuadrado(grafo.getVertice(num).getNumSoldados(), num);
                grafo.getVertice(num).setCorRED(atualRED);
                numMovimentos--;
                if (atualRED) {
                    lblMoveRED.setText("Movimentos: " + numMovimentos + " ");
                } else {
                    lblMoveBLUE.setText("Movimentos: " + numMovimentos + " ");
                }
                txtStatus.setText("Movimentado!");
                if (modo.equals("servidor") || modo.equals("cliente")) {
                    redeEnviaQuadrados("M");
                }
            }
            if (!modo.equals("ai") || atualRED) {
                btnPassar.setEnabled(true);
                movimentar();
            }
        } else if (status.equals("reforçar")) {
            if (modo.equals("servidor") || modo.equals("cliente")) {
                redeEnviaReforços(num);
            }
            new Thread() {
                public void run() {
                    chamarReforços(num);
                    movimentar();
                }
            }.start();
        } else if (status.equals("aereo")) {
            limparSelecionaveis();
            if (atualRED) {
                chamouAviaoRED = true;
                chamouAviaoREDIdx = num;
                btnAereoRED.setVisible(false);
                btnTriploAereoRED.setVisible(false);
                txtStatus.setText("Jogador RED chamou um Ataque Aéreo!");
            } else {
                chamouAviaoBLUE = true;
                chamouAviaoBLUEIdx = num;
                btnAereoBLUE.setVisible(false);
                btnTriploAereoBLUE.setVisible(false);
                txtStatus.setText("Jogador BLUE chamou um Ataque Aéreo!");
            }
            movimentar();
        }
    }

    /**
     * Método que controla o BLUE. Ativado apenas se o usuário escolher a opção
     * "1 Jogador".
     */
    private void ia() {
        exploBLUE++;
        new Thread() {
            public void run() {
                // Movimentar
                boolean fim = false;
                do {
                    int somaSoldsBLUE = 0, somaSoldsRED = 0;
                    status = "move2";
                    for (int x = 0; x < vetQuadradosVisuais.length; x++) {
                        if (grafo.getVertice(x).isCorRED() == false && grafo.getVertice(x).temSoldados()) {
                            somaSoldsBLUE += grafo.getVertice(x).getNumSoldados();
                        } else if (grafo.getVertice(x).isCorRED() == true && grafo.getVertice(x).temSoldados()) {
                            somaSoldsRED += grafo.getVertice(x).getNumSoldados();
                        }
                    }
                    if (somaSoldsBLUE == 0) {// não da para movimentar
                        numMovimentos = 0;
                        break;
                    } else if (grafo.getVertice(0).isCorRED() == false && grafo.getVertice(0).temSoldados()
                            || grafo.getVertice(1).isCorRED() == false && grafo.getVertice(1).temSoldados()
                            || somaSoldsRED == 0) {
                        esqVitoriaActionPerformed(null); // pegar bandeira vermelha
                        fim = true;
                        break;
                    } else {
                        Ambiente ag = new Ambiente();
                        for (int i = 0; i < 20; i++) {
                            ag.cruzamento();
                            ag.mutacao();
                            ag.torneio();
                            //ag.imprimePopulacao(i);
                        }
                        quadSelecionado = ag.getElite().getPosAtual();
                        tratarCliqueQuadrado(ag.getElite().getPosMovido());
                        System.out.println("Mover AI de " + ag.getElite().getPosAtual() + " para " + ag.getElite().getPosMovido());
                        if (numAtaques > 0) {
                            ataqueIA();
                        }
                    }
                    try {
                        Thread.sleep(2000);
                    } catch (InterruptedException ex) {
                        Logger.getLogger(Versus.class.getName()).log(Level.SEVERE, null, ex);
                    }
                } while (numMovimentos > 0);
                //
                status = "reforçar";
                if (!fim) {
                    // Chamar reforços
                    if (custoReforços < exploBLUE) {
                        BuscaEmLargura bfs18 = new BuscaEmLargura(grafo, 18);
                        BuscaEmLargura bfs19 = new BuscaEmLargura(grafo, 19);
                        BuscaEmLargura bfs20 = new BuscaEmLargura(grafo, 20);
                        int menor18 = Integer.MAX_VALUE;
                        int menor19 = Integer.MAX_VALUE;
                        int menor20 = Integer.MAX_VALUE;
                        for (int i = 0; i < vetQuadradosVisuais.length; i++) {
                            if (grafo.getVertice(i).isCorRED() && grafo.getVertice(i).temSoldados()) {
                                if (menor18 > bfs18.tempoDescoberta(i)) {
                                    menor18 = bfs18.tempoDescoberta(i);
                                }
                                if (menor19 > bfs19.tempoDescoberta(i)) {
                                    menor19 = bfs19.tempoDescoberta(i);
                                }
                                if (menor20 > bfs20.tempoDescoberta(i)) {
                                    menor20 = bfs20.tempoDescoberta(i);
                                }
                            }
                        }
                        if (!grafo.getVertice(18).temSoldados()) {
                            if (!grafo.getVertice(19).temSoldados()) {
                                if (menor19 > menor18) {
                                    grafo.getVertice(18).setCorRED(false);
                                    chamarReforços(18);
                                } else {
                                    grafo.getVertice(19).setCorRED(false);
                                    chamarReforços(19);
                                }
                            } else if (!grafo.getVertice(20).temSoldados()) {
                                if (menor20 > menor18) {
                                    grafo.getVertice(18).setCorRED(false);
                                    chamarReforços(18);
                                } else {
                                    grafo.getVertice(20).setCorRED(false);
                                    chamarReforços(20);
                                }
                            } else {
                                grafo.getVertice(18).setCorRED(false);
                                chamarReforços(18);
                            }
                        } else if (!grafo.getVertice(19).temSoldados()) {
                            if (!grafo.getVertice(20).temSoldados()) {
                                if (menor20 > menor19) {
                                    grafo.getVertice(19).setCorRED(false);
                                    chamarReforços(19);
                                } else {
                                    grafo.getVertice(20).setCorRED(false);
                                    chamarReforços(20);
                                }
                            } else {
                                grafo.getVertice(19).setCorRED(false);
                                chamarReforços(19);
                            }
                        } else if (!grafo.getVertice(20).temSoldados()) {
                            grafo.getVertice(20).setCorRED(false);
                            chamarReforços(20);
                        }
                    }
                    // Ataque aereo - lança onde tem mais soldados inimigos
                    if (custoAereo < exploBLUE) {
                        int maxSoldsRed = 0, idx = -1;
                        for (int x = 0; x < vetQuadradosVisuais.length; x++) {
                            if (grafo.getVertice(x).isCorRED() == true && maxSoldsRed < grafo.getVertice(x).getNumSoldados()) {
                                maxSoldsRed = grafo.getVertice(x).getNumSoldados();
                                idx = x;
                            }
                        }
                        if (maxSoldsRed > 0) {
                            chamouAviaoBLUE = true;
                            chamouAviaoBLUEIdx = idx;
                        }
                    }
                    ataqueIA();
                    passarVez();
                }
            }
        }.start();
    }

    /**
     * Método para que o computador possa atacar
     */
    private void ataqueIA() {
        boolean atacou;
        status = "move2";
        do {
            atacou = false;
            for (int v = 0; v < vetQuadradosVisuais.length; v++) {
                if (grafo.getVertice(v).isCorRED() == true && grafo.getVertice(v).temSoldados()) {
                    int numSoldBLUEAoRedor = 0;
                    List<Grafo.Vertice> adjs = grafo.verticesAdjacentesDe(v);
                    for (int y = 0; y < adjs.size(); y++) {
                        Grafo.Vertice adj = adjs.get(y);
                        if (adj.isCorRED() == false && adj.temSoldados()) {
                            numSoldBLUEAoRedor += adj.getNumSoldados();
                        }
                    }
                    if (numSoldBLUEAoRedor > grafo.getVertice(v).getNumSoldados()) {
                        tratarCliqueQuadrado(v);
                        atacou = true;
                    }
                }
            }
        } while (atacou == true);
    }

    /**
     * Método que realiza a interação lógica e a animação do chamar reforços.
     * (PARA RED e BLUE) Adiciona até 3 soldados a um quadrado.
     */
    private void chamarReforços(final int num_quadrado) {
        limparSelecionaveis();
        btnPassar.setEnabled(false);
        if(isEfeitos)
            new MP3("heli").play();
        JLabel lblDescida = new JLabel();
        camadas.add(lblDescida);
        camadas.setLayer(lblDescida, javax.swing.JLayeredPane.POPUP_LAYER);
        lblDescida.setBounds(0, 0, 50, 70);
        int targetX = vetPosicaoQuadradosX[num_quadrado] - 25;
        int targetY = vetPosicaoQuadradosY[num_quadrado] - 55;
        if (atualRED) {
            btnReforçosRED.setVisible(false);
            lblHeliRED.setVisible(true);
            //move ida heli
            for (int x = 0; x < targetX; x++) {
                lblHeliRED.setLocation(x, targetY);
                try {
                    Thread.sleep(10);
                } catch (InterruptedException ex) {
                    Logger.getLogger(Versus.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
            //anima descida
            lblDescida.setLocation(vetPosicaoQuadradosX[num_quadrado] + 20, vetPosicaoQuadradosY[num_quadrado] - 20);
            camadas.validate();
            lblDescida.setVisible(true);
            lblDescida.setIcon(descida);
            //
            while (grafo.getVertice(num_quadrado).getNumSoldados() != 3) {
                try {
                    Thread.sleep(1180);
                } catch (InterruptedException ex) {
                    Logger.getLogger(Versus.class.getName()).log(Level.SEVERE, null, ex);
                }
                trocarImagemQuadrado(grafo.getVertice(num_quadrado).getNumSoldados() + 1, num_quadrado);
                grafo.getVertice(num_quadrado).maisUmSoldado();
            }
            lblDescida.setIcon(null);
            //anima volta heli
            for (int x = targetX; x > 0; x--) {
                try {
                    Thread.sleep(10);
                } catch (InterruptedException ex) {
                    Logger.getLogger(Versus.class.getName()).log(Level.SEVERE, null, ex);
                }
                lblHeliRED.setLocation(x, targetY);
            }
            lblHeliRED.setVisible(false);
            //
            exploRED = exploRED - custoReforços;
        } else {
            btnReforçosBLUE.setVisible(false);
            lblHeliBLUE.setVisible(true);
            //move ida heli
            for (int x = camadas.getWidth(); x > targetX; x--) {
                lblHeliBLUE.setLocation(x, targetY);
                try {
                    Thread.sleep(10);
                } catch (InterruptedException ex) {
                    Logger.getLogger(Versus.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
            lblDescida.setLocation(vetPosicaoQuadradosX[num_quadrado] + 20, vetPosicaoQuadradosY[num_quadrado] - 20);
            camadas.validate();
            lblDescida.setVisible(true);
            lblDescida.setIcon(descida);
            while (grafo.getVertice(num_quadrado).getNumSoldados() != 3) {
                try {
                    Thread.sleep(1180);
                } catch (InterruptedException ex) {
                    Logger.getLogger(Versus.class.getName()).log(Level.SEVERE, null, ex);
                }
                grafo.getVertice(num_quadrado).maisUmSoldado();
                trocarImagemQuadrado(grafo.getVertice(num_quadrado).getNumSoldados(), num_quadrado);
            }
            lblDescida.setIcon(null);
            //anima volta heli
            for (int x = targetX; x < camadas.getWidth(); x++) {
                try {
                    Thread.sleep(10);
                } catch (InterruptedException ex) {
                    Logger.getLogger(Versus.class.getName()).log(Level.SEVERE, null, ex);
                }
                lblHeliBLUE.setLocation(x, targetY);
            }
            lblHeliBLUE.setVisible(false);
            exploBLUE -= custoReforços;
        }
        lblDescida = null;
        txtStatus.setText("Reforços enviados!");
        btnPassar.setEnabled(true);
    }

    /**
     * Método que executa a ação ataque aereo
     */
    private void executarChamadasAviao() {
        if (atualRED) {
            if (chamouAviaoRED) {
                if (modo.equals("servidor") || modo.equals("cliente")) {
                    redeEnviaAereo(chamouAviaoREDIdx);
                }
                btnPassar.setEnabled(false);
                ataqueAereo(chamouAviaoREDIdx);
                btnPassar.setEnabled(true);
                movimentar();
                chamouAviaoRED = false;
            } else if (chamouTriploAviaoRED) {
                int soma = 0, cont = 3, num_quadrado;
                for (int x = 0; x < vetQuadradosVisuais.length; x++) {
                    if (!grafo.getVertice(x).isCorRED()) {
                        soma++;
                    }
                }
                if (soma < 3) {
                    cont = soma;
                }
                do {
                    num_quadrado = rand.nextInt(vetQuadradosVisuais.length);
                    if (grafo.getVertice(num_quadrado).isCorRED() != atualRED) {
                        if (modo.equals("servidor") || modo.equals("cliente")) {
                            redeEnviaAereo(num_quadrado);
                        }
                        ataqueAereo(num_quadrado);
                        cont--;
                        try {
                            Thread.sleep(2000);
                        } catch (InterruptedException ex) {
                            Logger.getLogger(Versus.class.getName()).log(Level.SEVERE, null, ex);
                        }
                    }
                } while (cont != 0);
                movimentar();
                chamouTriploAviaoRED = false;
            }
        } else {
            if (chamouAviaoBLUE) {
                if (modo.equals("servidor") || modo.equals("cliente")) {
                    redeEnviaAereo(chamouAviaoBLUEIdx);
                }
                btnPassar.setEnabled(false);
                ataqueAereo(chamouAviaoBLUEIdx);
                btnPassar.setEnabled(true);
                if (!modo.equals("ai")) {
                    movimentar();
                }
                chamouAviaoBLUE = false;
            } else if (chamouTriploAviaoBLUE) {
                int soma = 0, cont = 3, num_quadrado;
                for (int x = 0; x < vetQuadradosVisuais.length; x++) {
                    if (grafo.getVertice(x).isCorRED()) {
                        soma++;
                    }
                }
                if (soma < 3) {
                    cont = soma;
                }
                do {
                    num_quadrado = rand.nextInt(vetQuadradosVisuais.length);
                    if (grafo.getVertice(num_quadrado).isCorRED() != atualRED) {
                        if (modo.equals("servidor") || modo.equals("cliente")) {
                            redeEnviaAereo(num_quadrado);
                        }
                        ataqueAereo(num_quadrado);
                        cont--;
                        try {
                            Thread.sleep(2000);
                        } catch (InterruptedException ex) {
                            Logger.getLogger(Versus.class.getName()).log(Level.SEVERE, null, ex);
                        }
                    }
                } while (cont != 0);
                if (!modo.equals("ai")) {
                    movimentar();
                }
                chamouTriploAviaoBLUE = false;
            }
        }
    }

    /**
     * Método que realiza a animação gráfica e lógica do ataque aéreo. (PARA RED
     * e BLUE)
     */
    private void ataqueAereo(int num_quadrado) {
        limparSelecionaveis();
        if(isEfeitos){
            new MP3("aviao").play();
        }
        if (atualRED) {
            txtStatus.setText("Jogador RED está realizando um Ataque Aéreo!");
            animarAviaoRED(num_quadrado);
            exploRED = exploRED - custoAereo;
            proExplosRED.setValue(exploRED);
            proExplosRED.setString("" + exploRED);
        } else {
            btnAereoBLUE.setVisible(false);
            btnTriploAereoBLUE.setVisible(false);
            txtStatus.setText("Jogador BLUE está realizando um Ataque Aéreo!");
            animarAviaoBLUE(num_quadrado);
            exploBLUE = exploBLUE - custoAereo;
            proExplosBLUE.setValue(exploBLUE);
            proExplosBLUE.setString("" + exploBLUE);
        }
        grafo.getVertice(num_quadrado).setCorRED(atualRED);
        grafo.getVertice(num_quadrado).zerarSoldados();
    }

    public void animarAviaoRED(final int num_quadrado) {
        new Thread() {
            public void run() {
                JLabel lblAviaoRED = new JLabel();
                lblAviaoRED.setIcon(cacaRED); // NOI18N
                lblAviaoRED.setSize(70, 40);
                //
                JLabel lblExplosao = new JLabel();
                lblExplosao.setVisible(false);
                lblExplosao.setSize(60, 80);
                //
                camadas.add(lblAviaoRED);
                camadas.setLayer(lblAviaoRED, javax.swing.JLayeredPane.POPUP_LAYER);
                //
                camadas.add(lblExplosao);
                camadas.setLayer(lblExplosao, javax.swing.JLayeredPane.POPUP_LAYER);

                int targetX = vetPosicaoQuadradosX[num_quadrado];
                int targetY = vetPosicaoQuadradosY[num_quadrado] - 25;
                for (int x = 0; x < camadas.getWidth(); x++) {
                    try {
                        Thread.sleep(9);
                    } catch (InterruptedException ex) {
                        Logger.getLogger(Versus.class.getName()).log(Level.SEVERE, null, ex);
                    }
                    lblAviaoRED.setLocation(x, targetY); // move o avião
                    if (x == targetX) { // posição da bomba
                        if(isEfeitos){
                            new MP3("atq2").play();
                        }
                        lblExplosao.setLocation(targetX, targetY);
                        lblExplosao.setVisible(true);
                        lblExplosao.setIcon(explosao);
                        trocarImagemQuadrado(0, num_quadrado);
                    }
                    if (x == targetX + 100) {
                        lblExplosao.setVisible(false);
                        camadas.remove(lblExplosao);
                    }
                }
                camadas.remove(lblAviaoRED);
                txtStatus.setText("Ataque aéreo realizado!");
            }
        }.start();
    }

    public void animarAviaoBLUE(final int num_quadrado) {
        new Thread() {
            public void run() {
                JLabel lblAviaoBLUE = new JLabel();
                lblAviaoBLUE.setIcon(cacaBLUE); // NOI18N
                lblAviaoBLUE.setSize(70, 40);
                //
                JLabel lblExplosao = new JLabel();
                lblExplosao.setVisible(false);
                lblExplosao.setSize(60, 80);
                //
                camadas.add(lblAviaoBLUE);
                camadas.setLayer(lblAviaoBLUE, javax.swing.JLayeredPane.POPUP_LAYER);
                //
                camadas.add(lblExplosao);
                camadas.setLayer(lblExplosao, javax.swing.JLayeredPane.POPUP_LAYER);

                int targetX = vetPosicaoQuadradosX[num_quadrado];
                int targetY = vetPosicaoQuadradosY[num_quadrado] - 25;
                for (int x = camadas.getWidth(); x > 0; x--) {
                    try {
                        Thread.sleep(9);
                    } catch (InterruptedException ex) {
                        Logger.getLogger(Versus.class.getName()).log(Level.SEVERE, null, ex);
                    }
                    lblAviaoBLUE.setLocation(x, targetY);
                    if (x == targetX) {
                        if(isEfeitos){
                            new MP3("atq2").play();
                        }
                        lblExplosao.setLocation(targetX, targetY);
                        lblExplosao.setVisible(true);
                        lblExplosao.setIcon(explosao);
                        trocarImagemQuadrado(0, num_quadrado);
                    }
                    if (x == targetX - 100) {
                        lblExplosao.setVisible(false);
                        camadas.remove(lblExplosao);
                    }
                }
                lblAviaoBLUE.setVisible(false);
                camadas.remove(lblAviaoBLUE);
                txtStatus.setText("Ataque aéreo realizado!");
            }
        }.start();
    }

    /**
     * Método para mostrar o placar do jogo
     */
    private void mostrarPlacar() {
        new Thread() {
            public void run() {
                try {
                    Thread.sleep(2000);
                } catch (InterruptedException ex) {
                    Logger.getLogger(Versus.class.getName()).log(Level.SEVERE, null, ex);
                }
                lblVitoriasQuadro.setVisible(true);
                for (int x = 0; x < 300; x++) {
                    lblVitoriasQuadro.setSize(320, x);
                    try {
                        Thread.sleep(10);
                    } catch (InterruptedException ex) {
                        Logger.getLogger(Versus.class.getName()).log(Level.SEVERE, null, ex);
                    }
                }
                lblVitorias.setVisible(true);
                lblVitoriasPanRED.setVisible(true);
                lblVitoriasX.setVisible(true);
                lblVitoriasPanBLUE.setVisible(true);
                lblVitoriasCont.setVisible(true);
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException ex) {
                    Logger.getLogger(Versus.class.getName()).log(Level.SEVERE, null, ex);
                }
                lblVitoriasRED.setText(vitoriasRED + "");
                lblVitoriasRED.setVisible(true);
                lblVitoriasBLUE.setText(vitoriasBLUE + "");
                lblVitoriasBLUE.setVisible(true);
                if ((vitoriasBLUE + vitoriasRED) >= numJogos || vitoriasBLUE > numJogos / 2 || vitoriasRED > numJogos / 2) {
                    for (int x = 9; x > 0; x--) {
                        lblVitoriasCont.setText("FIM DE JOGO! saindo em " + x);
                        try {
                            Thread.sleep(1000);
                        } catch (InterruptedException ex) {
                            Logger.getLogger(Versus.class.getName()).log(Level.SEVERE, null, ex);
                        }
                    }
                    System.exit(0);
                } else {
                    for (int x = 9; x > 0; x--) {
                        lblVitoriasCont.setText("novo jogo inicia em " + x);
                        try {
                            Thread.sleep(1000);
                        } catch (InterruptedException ex) {
                            Logger.getLogger(Versus.class.getName()).log(Level.SEVERE, null, ex);
                        }
                    }
                    lblVitorias.setVisible(false);
                    lblVitoriasX.setVisible(false);
                    lblVitoriasRED.setVisible(false);
                    lblVitoriasBLUE.setVisible(false);
                    lblVitoriasPanRED.setVisible(false);
                    lblVitoriasPanBLUE.setVisible(false);
                    lblVitoriasCont.setVisible(false);
                    for (int x = 300; x >= 0; x--) {
                        lblVitoriasQuadro.setSize(320, x);
                        try {
                            Thread.sleep(10);
                        } catch (InterruptedException ex) {
                            Logger.getLogger(Versus.class.getName()).log(Level.SEVERE, null, ex);
                        }
                    }
                    lblVitoriasQuadro.setVisible(false);
                    iniciarGame();
                }
            }
        }.start();
    }

    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        camadas = new javax.swing.JLayeredPane();
        lblHeliRED = new javax.swing.JLabel();
        lblHeliBLUE = new javax.swing.JLabel();
        btnPassar = new javax.swing.JButton();
        lblBlue = new javax.swing.JLabel();
        lblRed = new javax.swing.JLabel();
        lblSound = new javax.swing.JLabel();
        quadrado9 = new javax.swing.JButton();
        quadrado10 = new javax.swing.JButton();
        quadrado11 = new javax.swing.JButton();
        quadrado12 = new javax.swing.JButton();
        quadrado13 = new javax.swing.JButton();
        quadrado5 = new javax.swing.JButton();
        quadrado6 = new javax.swing.JButton();
        quadrado7 = new javax.swing.JButton();
        quadrado8 = new javax.swing.JButton();
        quadrado14 = new javax.swing.JButton();
        quadrado15 = new javax.swing.JButton();
        quadrado16 = new javax.swing.JButton();
        quadrado17 = new javax.swing.JButton();
        quadrado2 = new javax.swing.JButton();
        quadrado3 = new javax.swing.JButton();
        quadrado4 = new javax.swing.JButton();
        quadrado18 = new javax.swing.JButton();
        quadrado19 = new javax.swing.JButton();
        quadrado20 = new javax.swing.JButton();
        quadrado1 = new javax.swing.JButton();
        quadrado0 = new javax.swing.JButton();
        quadrado22 = new javax.swing.JButton();
        quadrado21 = new javax.swing.JButton();
        esqVitoria = new javax.swing.JButton();
        dirVitoria = new javax.swing.JButton();
        lblResult1 = new javax.swing.JLabel();
        lblResultX = new javax.swing.JLabel();
        lblResult2 = new javax.swing.JLabel();
        lblAtaqueRED = new javax.swing.JLabel();
        lblMoveRED = new javax.swing.JLabel();
        bandir = new javax.swing.JLabel();
        banesq = new javax.swing.JLabel();
        proExplosRED = new javax.swing.JProgressBar();
        proExplosBLUE = new javax.swing.JProgressBar();
        lblAtaqueBLUE = new javax.swing.JLabel();
        lblMoveBLUE = new javax.swing.JLabel();
        lblVitoriasBLUE = new javax.swing.JLabel();
        lblVitoriasRED = new javax.swing.JLabel();
        lblVitorias = new javax.swing.JLabel();
        lblVitoriasPanRED = new javax.swing.JLabel();
        lblVitoriasPanBLUE = new javax.swing.JLabel();
        lblVitoriasCont = new javax.swing.JLabel();
        lblVitoriasX = new javax.swing.JLabel();
        lblVitoriasQuadro = new javax.swing.JLabel();
        btnReforçosRED = new javax.swing.JButton();
        btnAereoRED = new javax.swing.JButton();
        btnReforçosBLUE = new javax.swing.JButton();
        btnAereoBLUE = new javax.swing.JButton();
        btnTriploAereoBLUE = new javax.swing.JButton();
        btnTriploAereoRED = new javax.swing.JButton();
        txtStatus = new javax.swing.JTextField();
        jLabel1 = new javax.swing.JLabel();
        jLabel2 = new javax.swing.JLabel();
        jLabel3 = new javax.swing.JLabel();
        jLabel4 = new javax.swing.JLabel();
        fundo = new javax.swing.JLabel();
        camuflado = new javax.swing.JLabel();
        lblSound1 = new javax.swing.JLabel();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        setTitle("GLIMA War Game");
        setBounds(new java.awt.Rectangle(300, 100, 0, 0));
        setResizable(false);
        addWindowListener(new java.awt.event.WindowAdapter() {
            public void windowClosing(java.awt.event.WindowEvent evt) {
                formWindowClosing(evt);
            }
        });

        camadas.setBackground(new java.awt.Color(0, 0, 0));
        camadas.setForeground(new java.awt.Color(255, 255, 255));

        lblHeliRED.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        lblHeliRED.setIcon(new javax.swing.ImageIcon(getClass().getResource("/imagVERS/heliR.gif"))); // NOI18N
        lblHeliRED.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        camadas.setLayer(lblHeliRED, javax.swing.JLayeredPane.POPUP_LAYER);
        camadas.add(lblHeliRED);
        lblHeliRED.setBounds(130, 40, 110, 50);

        lblHeliBLUE.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        lblHeliBLUE.setIcon(new javax.swing.ImageIcon(getClass().getResource("/imagVERS/heliB.gif"))); // NOI18N
        lblHeliBLUE.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        camadas.setLayer(lblHeliBLUE, javax.swing.JLayeredPane.POPUP_LAYER);
        camadas.add(lblHeliBLUE);
        lblHeliBLUE.setBounds(440, 40, 110, 50);

        btnPassar.setBackground(new java.awt.Color(0, 153, 51));
        btnPassar.setIcon(new javax.swing.ImageIcon(getClass().getResource("/imagVERS/Passar.png"))); // NOI18N
        btnPassar.setBorderPainted(false);
        btnPassar.setContentAreaFilled(false);
        btnPassar.setCursor(new java.awt.Cursor(java.awt.Cursor.DEFAULT_CURSOR));
        btnPassar.setDisabledIcon(new javax.swing.ImageIcon(getClass().getResource("/imagVERS/PassarPress.png"))); // NOI18N
        btnPassar.setPressedIcon(new javax.swing.ImageIcon(getClass().getResource("/imagVERS/PassarPress.png"))); // NOI18N
        btnPassar.setRolloverIcon(new javax.swing.ImageIcon(getClass().getResource("/imagVERS/PassarRoll.png"))); // NOI18N
        btnPassar.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnPassarActionPerformed(evt);
            }
        });
        camadas.setLayer(btnPassar, javax.swing.JLayeredPane.PALETTE_LAYER);
        camadas.add(btnPassar);
        btnPassar.setBounds(280, 380, 120, 80);

        lblBlue.setBackground(new java.awt.Color(255, 255, 255));
        lblBlue.setFont(new java.awt.Font("Magneto", 0, 24)); // NOI18N
        lblBlue.setForeground(java.awt.Color.blue);
        lblBlue.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        lblBlue.setText("Blue");
        lblBlue.setBorder(javax.swing.BorderFactory.createCompoundBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(25, 50, 0), 4), null));
        lblBlue.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        lblBlue.setOpaque(true);
        camadas.add(lblBlue);
        lblBlue.setBounds(560, 30, 120, 60);

        lblRed.setBackground(new java.awt.Color(255, 255, 255));
        lblRed.setFont(new java.awt.Font("Magneto", 0, 24)); // NOI18N
        lblRed.setForeground(java.awt.Color.red);
        lblRed.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        lblRed.setText("Red");
        lblRed.setBorder(javax.swing.BorderFactory.createCompoundBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(25, 50, 0), 4), null));
        lblRed.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        lblRed.setOpaque(true);
        camadas.add(lblRed);
        lblRed.setBounds(0, 30, 120, 60);

        lblSound.setFont(new java.awt.Font("Magneto", 1, 18)); // NOI18N
        lblSound.setForeground(new java.awt.Color(220, 220, 220));
        lblSound.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        lblSound.setText("efeitos [x]");
        lblSound.setToolTipText("");
        lblSound.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                lblSoundMouseClicked(evt);
            }
        });
        camadas.setLayer(lblSound, javax.swing.JLayeredPane.PALETTE_LAYER);
        camadas.add(lblSound);
        lblSound.setBounds(300, 500, 90, 20);

        quadrado9.setBackground(new java.awt.Color(0, 0, 0));
        quadrado9.setIcon(new javax.swing.ImageIcon(getClass().getResource("/imagVERS/circRED0.png"))); // NOI18N
        quadrado9.setToolTipText("[5,1]");
        quadrado9.setAlignmentY(0.0F);
        quadrado9.setBorder(null);
        quadrado9.setHideActionText(true);
        quadrado9.setPreferredSize(new java.awt.Dimension(50, 50));
        camadas.setLayer(quadrado9, javax.swing.JLayeredPane.MODAL_LAYER);
        camadas.add(quadrado9);
        quadrado9.setBounds(320, 240, 50, 50);

        quadrado10.setBackground(new java.awt.Color(0, 0, 0));
        quadrado10.setIcon(new javax.swing.ImageIcon(getClass().getResource("/imagVERS/circRED0.png"))); // NOI18N
        quadrado10.setToolTipText("[5,2]");
        quadrado10.setAlignmentY(0.0F);
        quadrado10.setBorder(null);
        quadrado10.setHideActionText(true);
        quadrado10.setPreferredSize(new java.awt.Dimension(50, 50));
        camadas.setLayer(quadrado10, javax.swing.JLayeredPane.MODAL_LAYER);
        camadas.add(quadrado10);
        quadrado10.setBounds(320, 190, 50, 50);

        quadrado11.setBackground(new java.awt.Color(0, 0, 0));
        quadrado11.setIcon(new javax.swing.ImageIcon(getClass().getResource("/imagVERS/circBLUE0.png"))); // NOI18N
        quadrado11.setToolTipText("[5,3]");
        quadrado11.setAlignmentY(0.0F);
        quadrado11.setBorder(null);
        quadrado11.setHideActionText(true);
        quadrado11.setPreferredSize(new java.awt.Dimension(50, 50));
        camadas.setLayer(quadrado11, javax.swing.JLayeredPane.MODAL_LAYER);
        camadas.add(quadrado11);
        quadrado11.setBounds(320, 140, 50, 50);

        quadrado12.setBackground(new java.awt.Color(0, 0, 0));
        quadrado12.setIcon(new javax.swing.ImageIcon(getClass().getResource("/imagVERS/circBLUE0.png"))); // NOI18N
        quadrado12.setToolTipText("[5,4]");
        quadrado12.setAlignmentY(0.0F);
        quadrado12.setBorder(null);
        quadrado12.setHideActionText(true);
        quadrado12.setPreferredSize(new java.awt.Dimension(50, 50));
        camadas.setLayer(quadrado12, javax.swing.JLayeredPane.MODAL_LAYER);
        camadas.add(quadrado12);
        quadrado12.setBounds(320, 90, 50, 50);

        quadrado13.setBackground(new java.awt.Color(0, 0, 0));
        quadrado13.setIcon(new javax.swing.ImageIcon(getClass().getResource("/imagVERS/circBLUE0.png"))); // NOI18N
        quadrado13.setToolTipText("[5,5]");
        quadrado13.setAlignmentY(0.0F);
        quadrado13.setBorder(null);
        quadrado13.setHideActionText(true);
        quadrado13.setPreferredSize(new java.awt.Dimension(50, 50));
        camadas.setLayer(quadrado13, javax.swing.JLayeredPane.MODAL_LAYER);
        camadas.add(quadrado13);
        quadrado13.setBounds(320, 40, 50, 50);

        quadrado5.setBackground(new java.awt.Color(0, 0, 0));
        quadrado5.setIcon(new javax.swing.ImageIcon(getClass().getResource("/imagVERS/circRED0.png"))); // NOI18N
        quadrado5.setToolTipText("[4,1]");
        quadrado5.setAlignmentY(0.0F);
        quadrado5.setBorder(null);
        quadrado5.setHideActionText(true);
        quadrado5.setPreferredSize(new java.awt.Dimension(50, 50));
        camadas.setLayer(quadrado5, javax.swing.JLayeredPane.MODAL_LAYER);
        camadas.add(quadrado5);
        quadrado5.setBounds(270, 220, 50, 50);

        quadrado6.setBackground(new java.awt.Color(0, 0, 0));
        quadrado6.setIcon(new javax.swing.ImageIcon(getClass().getResource("/imagVERS/circRED0.png"))); // NOI18N
        quadrado6.setToolTipText("[4,2]");
        quadrado6.setAlignmentY(0.0F);
        quadrado6.setBorder(null);
        quadrado6.setHideActionText(true);
        quadrado6.setPreferredSize(new java.awt.Dimension(50, 50));
        camadas.setLayer(quadrado6, javax.swing.JLayeredPane.MODAL_LAYER);
        camadas.add(quadrado6);
        quadrado6.setBounds(270, 170, 50, 50);

        quadrado7.setBackground(new java.awt.Color(0, 0, 0));
        quadrado7.setIcon(new javax.swing.ImageIcon(getClass().getResource("/imagVERS/circRED0.png"))); // NOI18N
        quadrado7.setToolTipText("[4,3]");
        quadrado7.setAlignmentY(0.0F);
        quadrado7.setBorder(null);
        quadrado7.setHideActionText(true);
        quadrado7.setPreferredSize(new java.awt.Dimension(50, 50));
        camadas.setLayer(quadrado7, javax.swing.JLayeredPane.MODAL_LAYER);
        camadas.add(quadrado7);
        quadrado7.setBounds(270, 120, 50, 50);

        quadrado8.setBackground(new java.awt.Color(0, 0, 0));
        quadrado8.setIcon(new javax.swing.ImageIcon(getClass().getResource("/imagVERS/circRED0.png"))); // NOI18N
        quadrado8.setToolTipText("[4,4]");
        quadrado8.setAlignmentY(0.0F);
        quadrado8.setBorder(null);
        quadrado8.setHideActionText(true);
        quadrado8.setPreferredSize(new java.awt.Dimension(50, 50));
        camadas.setLayer(quadrado8, javax.swing.JLayeredPane.MODAL_LAYER);
        camadas.add(quadrado8);
        quadrado8.setBounds(270, 70, 50, 50);

        quadrado14.setBackground(new java.awt.Color(0, 0, 0));
        quadrado14.setIcon(new javax.swing.ImageIcon(getClass().getResource("/imagVERS/circBLUE0.png"))); // NOI18N
        quadrado14.setToolTipText("[6,1]");
        quadrado14.setAlignmentY(0.0F);
        quadrado14.setBorder(null);
        quadrado14.setHideActionText(true);
        quadrado14.setPreferredSize(new java.awt.Dimension(50, 50));
        camadas.setLayer(quadrado14, javax.swing.JLayeredPane.MODAL_LAYER);
        camadas.add(quadrado14);
        quadrado14.setBounds(370, 220, 50, 50);

        quadrado15.setBackground(new java.awt.Color(0, 0, 0));
        quadrado15.setIcon(new javax.swing.ImageIcon(getClass().getResource("/imagVERS/circBLUE0.png"))); // NOI18N
        quadrado15.setToolTipText("[6,2]");
        quadrado15.setAlignmentY(0.0F);
        quadrado15.setBorder(null);
        quadrado15.setHideActionText(true);
        quadrado15.setPreferredSize(new java.awt.Dimension(50, 50));
        camadas.setLayer(quadrado15, javax.swing.JLayeredPane.MODAL_LAYER);
        camadas.add(quadrado15);
        quadrado15.setBounds(370, 170, 50, 50);

        quadrado16.setBackground(new java.awt.Color(0, 0, 0));
        quadrado16.setIcon(new javax.swing.ImageIcon(getClass().getResource("/imagVERS/circBLUE0.png"))); // NOI18N
        quadrado16.setToolTipText("[6,3]");
        quadrado16.setAlignmentY(0.0F);
        quadrado16.setBorder(null);
        quadrado16.setHideActionText(true);
        quadrado16.setPreferredSize(new java.awt.Dimension(50, 50));
        camadas.setLayer(quadrado16, javax.swing.JLayeredPane.MODAL_LAYER);
        camadas.add(quadrado16);
        quadrado16.setBounds(370, 120, 50, 50);

        quadrado17.setBackground(new java.awt.Color(0, 0, 0));
        quadrado17.setIcon(new javax.swing.ImageIcon(getClass().getResource("/imagVERS/circBLUE0.png"))); // NOI18N
        quadrado17.setToolTipText("[6,4]");
        quadrado17.setAlignmentY(0.0F);
        quadrado17.setBorder(null);
        quadrado17.setHideActionText(true);
        quadrado17.setPreferredSize(new java.awt.Dimension(50, 50));
        camadas.setLayer(quadrado17, javax.swing.JLayeredPane.MODAL_LAYER);
        camadas.add(quadrado17);
        quadrado17.setBounds(370, 70, 50, 50);

        quadrado2.setBackground(new java.awt.Color(0, 0, 0));
        quadrado2.setIcon(new javax.swing.ImageIcon(getClass().getResource("/imagVERS/circRED0.png"))); // NOI18N
        quadrado2.setToolTipText("[3,1]");
        quadrado2.setAlignmentY(0.0F);
        quadrado2.setBorder(null);
        quadrado2.setHideActionText(true);
        quadrado2.setPreferredSize(new java.awt.Dimension(50, 50));
        camadas.setLayer(quadrado2, javax.swing.JLayeredPane.MODAL_LAYER);
        camadas.add(quadrado2);
        quadrado2.setBounds(220, 190, 50, 50);

        quadrado3.setBackground(new java.awt.Color(0, 0, 0));
        quadrado3.setIcon(new javax.swing.ImageIcon(getClass().getResource("/imagVERS/circRED0.png"))); // NOI18N
        quadrado3.setToolTipText("[3,2]");
        quadrado3.setAlignmentY(0.0F);
        quadrado3.setBorder(null);
        quadrado3.setHideActionText(true);
        quadrado3.setPreferredSize(new java.awt.Dimension(50, 50));
        camadas.setLayer(quadrado3, javax.swing.JLayeredPane.MODAL_LAYER);
        camadas.add(quadrado3);
        quadrado3.setBounds(220, 140, 50, 50);

        quadrado4.setBackground(new java.awt.Color(0, 0, 0));
        quadrado4.setIcon(new javax.swing.ImageIcon(getClass().getResource("/imagVERS/circRED0.png"))); // NOI18N
        quadrado4.setToolTipText("[3,3]");
        quadrado4.setAlignmentY(0.0F);
        quadrado4.setBorder(null);
        quadrado4.setHideActionText(true);
        quadrado4.setPreferredSize(new java.awt.Dimension(50, 50));
        camadas.setLayer(quadrado4, javax.swing.JLayeredPane.MODAL_LAYER);
        camadas.add(quadrado4);
        quadrado4.setBounds(220, 90, 50, 50);

        quadrado18.setBackground(new java.awt.Color(0, 0, 0));
        quadrado18.setIcon(new javax.swing.ImageIcon(getClass().getResource("/imagVERS/circBLUE0.png"))); // NOI18N
        quadrado18.setToolTipText("[7,1]");
        quadrado18.setAlignmentY(0.0F);
        quadrado18.setBorder(null);
        quadrado18.setHideActionText(true);
        quadrado18.setPreferredSize(new java.awt.Dimension(50, 50));
        camadas.setLayer(quadrado18, javax.swing.JLayeredPane.MODAL_LAYER);
        camadas.add(quadrado18);
        quadrado18.setBounds(420, 190, 50, 50);

        quadrado19.setBackground(new java.awt.Color(0, 0, 0));
        quadrado19.setIcon(new javax.swing.ImageIcon(getClass().getResource("/imagVERS/circBLUE0.png"))); // NOI18N
        quadrado19.setToolTipText("[7,2]");
        quadrado19.setAlignmentY(0.0F);
        quadrado19.setBorder(null);
        quadrado19.setHideActionText(true);
        quadrado19.setPreferredSize(new java.awt.Dimension(50, 50));
        camadas.setLayer(quadrado19, javax.swing.JLayeredPane.MODAL_LAYER);
        camadas.add(quadrado19);
        quadrado19.setBounds(420, 140, 50, 50);

        quadrado20.setBackground(new java.awt.Color(0, 0, 0));
        quadrado20.setIcon(new javax.swing.ImageIcon(getClass().getResource("/imagVERS/circBLUE0.png"))); // NOI18N
        quadrado20.setToolTipText("[7,3]");
        quadrado20.setAlignmentY(0.0F);
        quadrado20.setBorder(null);
        quadrado20.setHideActionText(true);
        quadrado20.setPreferredSize(new java.awt.Dimension(50, 50));
        camadas.setLayer(quadrado20, javax.swing.JLayeredPane.MODAL_LAYER);
        camadas.add(quadrado20);
        quadrado20.setBounds(420, 90, 50, 50);

        quadrado1.setBackground(new java.awt.Color(0, 0, 0));
        quadrado1.setIcon(new javax.swing.ImageIcon(getClass().getResource("/imagVERS/circRED0.png"))); // NOI18N
        quadrado1.setToolTipText("[2,2]");
        quadrado1.setAlignmentY(0.0F);
        quadrado1.setBorder(null);
        quadrado1.setHideActionText(true);
        quadrado1.setPreferredSize(new java.awt.Dimension(50, 50));
        camadas.setLayer(quadrado1, javax.swing.JLayeredPane.MODAL_LAYER);
        camadas.add(quadrado1);
        quadrado1.setBounds(170, 120, 50, 50);

        quadrado0.setBackground(new java.awt.Color(0, 0, 0));
        quadrado0.setIcon(new javax.swing.ImageIcon(getClass().getResource("/imagVERS/circRED0.png"))); // NOI18N
        quadrado0.setToolTipText("[2,1]");
        quadrado0.setAlignmentY(0.0F);
        quadrado0.setBorder(null);
        quadrado0.setHideActionText(true);
        quadrado0.setPreferredSize(new java.awt.Dimension(50, 50));
        camadas.setLayer(quadrado0, javax.swing.JLayeredPane.MODAL_LAYER);
        camadas.add(quadrado0);
        quadrado0.setBounds(170, 170, 50, 50);

        quadrado22.setBackground(new java.awt.Color(0, 0, 0));
        quadrado22.setIcon(new javax.swing.ImageIcon(getClass().getResource("/imagVERS/circBLUE0.png"))); // NOI18N
        quadrado22.setToolTipText("[8,2]");
        quadrado22.setAlignmentY(0.0F);
        quadrado22.setBorder(null);
        quadrado22.setHideActionText(true);
        quadrado22.setPreferredSize(new java.awt.Dimension(50, 50));
        camadas.setLayer(quadrado22, javax.swing.JLayeredPane.MODAL_LAYER);
        camadas.add(quadrado22);
        quadrado22.setBounds(470, 120, 50, 50);

        quadrado21.setBackground(new java.awt.Color(0, 0, 0));
        quadrado21.setIcon(new javax.swing.ImageIcon(getClass().getResource("/imagVERS/circBLUE0.png"))); // NOI18N
        quadrado21.setToolTipText("[8,1]");
        quadrado21.setAlignmentY(0.0F);
        quadrado21.setBorder(null);
        quadrado21.setHideActionText(true);
        quadrado21.setPreferredSize(new java.awt.Dimension(50, 50));
        camadas.setLayer(quadrado21, javax.swing.JLayeredPane.MODAL_LAYER);
        camadas.add(quadrado21);
        quadrado21.setBounds(470, 170, 50, 50);

        esqVitoria.setBackground(new java.awt.Color(0, 0, 0));
        esqVitoria.setIcon(new javax.swing.ImageIcon(getClass().getResource("/imagVERS/circBLUE0.png"))); // NOI18N
        esqVitoria.setToolTipText("Territorio para ser conquistado pelo jogador AZUL");
        esqVitoria.setAlignmentY(0.0F);
        esqVitoria.setBorder(null);
        esqVitoria.setDisabledIcon(new javax.swing.ImageIcon(getClass().getResource("/imagVERS/circRED0.png"))); // NOI18N
        esqVitoria.setHideActionText(true);
        esqVitoria.setPreferredSize(new java.awt.Dimension(50, 50));
        esqVitoria.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                esqVitoriaActionPerformed(evt);
            }
        });
        camadas.setLayer(esqVitoria, javax.swing.JLayeredPane.MODAL_LAYER);
        camadas.add(esqVitoria);
        esqVitoria.setBounds(120, 140, 50, 50);

        dirVitoria.setBackground(new java.awt.Color(0, 0, 0));
        dirVitoria.setIcon(new javax.swing.ImageIcon(getClass().getResource("/imagVERS/circRED0.png"))); // NOI18N
        dirVitoria.setToolTipText("Territorio para ser conquistado pelo jogador VERMELHO");
        dirVitoria.setAlignmentY(0.0F);
        dirVitoria.setBorder(null);
        dirVitoria.setDisabledIcon(new javax.swing.ImageIcon(getClass().getResource("/imagVERS/circBLUE0.png"))); // NOI18N
        dirVitoria.setPreferredSize(new java.awt.Dimension(50, 50));
        dirVitoria.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                dirVitoriaActionPerformed(evt);
            }
        });
        camadas.setLayer(dirVitoria, javax.swing.JLayeredPane.MODAL_LAYER);
        camadas.add(dirVitoria);
        dirVitoria.setBounds(520, 140, 50, 50);

        lblResult1.setBackground(new java.awt.Color(255, 255, 255));
        lblResult1.setFont(new java.awt.Font("Courier New", 1, 18)); // NOI18N
        lblResult1.setForeground(new java.awt.Color(255, 0, 0));
        lblResult1.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        lblResult1.setToolTipText("Número de soldados de RED mortos na última batalha");
        camadas.setLayer(lblResult1, javax.swing.JLayeredPane.MODAL_LAYER);
        camadas.add(lblResult1);
        lblResult1.setBounds(200, 320, 120, 30);

        lblResultX.setFont(new java.awt.Font("Courier New", 1, 48)); // NOI18N
        lblResultX.setForeground(new java.awt.Color(255, 255, 255));
        lblResultX.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        lblResultX.setText("X");
        camadas.setLayer(lblResultX, javax.swing.JLayeredPane.MODAL_LAYER);
        camadas.add(lblResultX);
        lblResultX.setBounds(320, 320, 50, 30);

        lblResult2.setBackground(new java.awt.Color(255, 255, 255));
        lblResult2.setFont(new java.awt.Font("Courier New", 1, 18)); // NOI18N
        lblResult2.setForeground(new java.awt.Color(0, 0, 255));
        lblResult2.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        lblResult2.setToolTipText("Número de soldados de BLUE mortos na última batalha");
        camadas.setLayer(lblResult2, javax.swing.JLayeredPane.MODAL_LAYER);
        camadas.add(lblResult2);
        lblResult2.setBounds(370, 320, 120, 30);

        lblAtaqueRED.setBackground(new java.awt.Color(255, 255, 200));
        lblAtaqueRED.setFont(new java.awt.Font("Courier New", 1, 14)); // NOI18N
        lblAtaqueRED.setForeground(new java.awt.Color(204, 51, 0));
        lblAtaqueRED.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        lblAtaqueRED.setText("Ataques: 3 ");
        lblAtaqueRED.setToolTipText("Quantidade restante de ataques corpo a corpo restantes de RED");
        lblAtaqueRED.setAlignmentY(0.0F);
        lblAtaqueRED.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(25, 50, 0), 3));
        lblAtaqueRED.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        lblAtaqueRED.setOpaque(true);
        camadas.setLayer(lblAtaqueRED, javax.swing.JLayeredPane.MODAL_LAYER);
        camadas.add(lblAtaqueRED);
        lblAtaqueRED.setBounds(0, 0, 120, 30);

        lblMoveRED.setBackground(new java.awt.Color(255, 255, 200));
        lblMoveRED.setFont(new java.awt.Font("Courier New", 1, 14)); // NOI18N
        lblMoveRED.setForeground(new java.awt.Color(0, 153, 0));
        lblMoveRED.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        lblMoveRED.setText("Movimentos: 3 ");
        lblMoveRED.setToolTipText("Quantidade restante de movimentos restantes de RED no turno");
        lblMoveRED.setAlignmentY(0.0F);
        lblMoveRED.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(25, 50, 0), 3));
        lblMoveRED.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        lblMoveRED.setOpaque(true);
        camadas.setLayer(lblMoveRED, javax.swing.JLayeredPane.MODAL_LAYER);
        camadas.add(lblMoveRED);
        lblMoveRED.setBounds(120, 0, 140, 30);

        bandir.setBackground(new java.awt.Color(204, 0, 0));
        bandir.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        bandir.setVerticalAlignment(javax.swing.SwingConstants.TOP);
        camadas.setLayer(bandir, javax.swing.JLayeredPane.MODAL_LAYER);
        camadas.add(bandir);
        bandir.setBounds(520, 100, 90, 60);

        banesq.setBackground(new java.awt.Color(0, 0, 153));
        banesq.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        banesq.setVerticalAlignment(javax.swing.SwingConstants.TOP);
        camadas.setLayer(banesq, javax.swing.JLayeredPane.MODAL_LAYER);
        camadas.add(banesq);
        banesq.setBounds(70, 100, 90, 60);

        proExplosRED.setBackground(new java.awt.Color(255, 255, 255));
        proExplosRED.setFont(new java.awt.Font("Courier New", 1, 18)); // NOI18N
        proExplosRED.setForeground(new java.awt.Color(255, 0, 0));
        proExplosRED.setMaximum(25);
        proExplosRED.setToolTipText("Explorações conquistadas por RED");
        proExplosRED.setValue(10);
        proExplosRED.setString("");
        proExplosRED.setStringPainted(true);
        camadas.setLayer(proExplosRED, javax.swing.JLayeredPane.POPUP_LAYER);
        camadas.add(proExplosRED);
        proExplosRED.setBounds(0, 405, 260, 30);

        proExplosBLUE.setBackground(new java.awt.Color(255, 255, 255));
        proExplosBLUE.setFont(new java.awt.Font("Courier New", 1, 18)); // NOI18N
        proExplosBLUE.setForeground(new java.awt.Color(0, 0, 204));
        proExplosBLUE.setMaximum(25);
        proExplosBLUE.setToolTipText("Explorações conquistadas por BLUE");
        proExplosBLUE.setValue(10);
        proExplosBLUE.setString("");
        proExplosBLUE.setStringPainted(true);
        camadas.setLayer(proExplosBLUE, javax.swing.JLayeredPane.POPUP_LAYER);
        camadas.add(proExplosBLUE);
        proExplosBLUE.setBounds(420, 405, 260, 30);

        lblAtaqueBLUE.setBackground(new java.awt.Color(255, 255, 200));
        lblAtaqueBLUE.setFont(new java.awt.Font("Courier New", 1, 14)); // NOI18N
        lblAtaqueBLUE.setForeground(new java.awt.Color(204, 51, 0));
        lblAtaqueBLUE.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        lblAtaqueBLUE.setText("Ataques: 3 ");
        lblAtaqueBLUE.setToolTipText("Quantidade restante de ataques corpo a corpo restantes de BLUE");
        lblAtaqueBLUE.setAlignmentY(0.0F);
        lblAtaqueBLUE.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(25, 50, 0), 3));
        lblAtaqueBLUE.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        lblAtaqueBLUE.setOpaque(true);
        camadas.setLayer(lblAtaqueBLUE, javax.swing.JLayeredPane.MODAL_LAYER);
        camadas.add(lblAtaqueBLUE);
        lblAtaqueBLUE.setBounds(560, 0, 120, 30);

        lblMoveBLUE.setBackground(new java.awt.Color(255, 255, 200));
        lblMoveBLUE.setFont(new java.awt.Font("Courier New", 1, 14)); // NOI18N
        lblMoveBLUE.setForeground(new java.awt.Color(0, 153, 0));
        lblMoveBLUE.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        lblMoveBLUE.setText("Movimentos 3 ");
        lblMoveBLUE.setToolTipText("Quantidade restante de movimentos restantes de BLUE");
        lblMoveBLUE.setAlignmentY(0.0F);
        lblMoveBLUE.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(25, 50, 0), 3));
        lblMoveBLUE.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        lblMoveBLUE.setOpaque(true);
        camadas.setLayer(lblMoveBLUE, javax.swing.JLayeredPane.MODAL_LAYER);
        camadas.add(lblMoveBLUE);
        lblMoveBLUE.setBounds(420, 0, 140, 30);

        lblVitoriasBLUE.setBackground(new java.awt.Color(255, 255, 255));
        lblVitoriasBLUE.setFont(new java.awt.Font("Magneto", 1, 48)); // NOI18N
        lblVitoriasBLUE.setForeground(new java.awt.Color(0, 102, 51));
        lblVitoriasBLUE.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        lblVitoriasBLUE.setText("3");
        camadas.add(lblVitoriasBLUE);
        lblVitoriasBLUE.setBounds(400, 130, 75, 60);

        lblVitoriasRED.setBackground(new java.awt.Color(255, 255, 255));
        lblVitoriasRED.setFont(new java.awt.Font("Magneto", 1, 48)); // NOI18N
        lblVitoriasRED.setForeground(new java.awt.Color(0, 102, 51));
        lblVitoriasRED.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        lblVitoriasRED.setText("3");
        camadas.add(lblVitoriasRED);
        lblVitoriasRED.setBounds(210, 130, 75, 60);

        lblVitorias.setBackground(new java.awt.Color(255, 255, 255));
        lblVitorias.setFont(new java.awt.Font("Courier New", 1, 20)); // NOI18N
        lblVitorias.setForeground(new java.awt.Color(0, 102, 51));
        lblVitorias.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        lblVitorias.setText("Vitorias");
        lblVitorias.setOpaque(true);
        camadas.add(lblVitorias);
        lblVitorias.setBounds(270, 60, 150, 30);

        lblVitoriasPanRED.setBackground(new java.awt.Color(255, 255, 255));
        lblVitoriasPanRED.setFont(new java.awt.Font("Courier New", 1, 20)); // NOI18N
        lblVitoriasPanRED.setForeground(new java.awt.Color(204, 0, 0));
        lblVitoriasPanRED.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        lblVitoriasPanRED.setText("RED");
        lblVitoriasPanRED.setVerticalAlignment(javax.swing.SwingConstants.TOP);
        lblVitoriasPanRED.setOpaque(true);
        camadas.add(lblVitoriasPanRED);
        lblVitoriasPanRED.setBounds(210, 100, 75, 100);

        lblVitoriasPanBLUE.setBackground(new java.awt.Color(255, 255, 255));
        lblVitoriasPanBLUE.setFont(new java.awt.Font("Courier New", 1, 20)); // NOI18N
        lblVitoriasPanBLUE.setForeground(new java.awt.Color(0, 0, 153));
        lblVitoriasPanBLUE.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        lblVitoriasPanBLUE.setText("BLUE");
        lblVitoriasPanBLUE.setVerticalAlignment(javax.swing.SwingConstants.TOP);
        lblVitoriasPanBLUE.setOpaque(true);
        camadas.add(lblVitoriasPanBLUE);
        lblVitoriasPanBLUE.setBounds(400, 100, 75, 100);

        lblVitoriasCont.setBackground(new java.awt.Color(255, 255, 255));
        lblVitoriasCont.setFont(new java.awt.Font("Courier New", 1, 14)); // NOI18N
        lblVitoriasCont.setForeground(new java.awt.Color(0, 102, 51));
        lblVitoriasCont.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        lblVitoriasCont.setText("novo jogo inicia em 9");
        lblVitoriasCont.setOpaque(true);
        camadas.add(lblVitoriasCont);
        lblVitoriasCont.setBounds(210, 210, 265, 30);

        lblVitoriasX.setBackground(new java.awt.Color(255, 255, 255));
        lblVitoriasX.setFont(new java.awt.Font("Courier New", 1, 55)); // NOI18N
        lblVitoriasX.setForeground(new java.awt.Color(255, 255, 255));
        lblVitoriasX.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        lblVitoriasX.setText("X");
        lblVitoriasX.setVerticalAlignment(javax.swing.SwingConstants.BOTTOM);
        camadas.add(lblVitoriasX);
        lblVitoriasX.setBounds(310, 100, 75, 100);

        lblVitoriasQuadro.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        lblVitoriasQuadro.setIcon(new javax.swing.ImageIcon(getClass().getResource("/imagVERS/fim.jpg"))); // NOI18N
        lblVitoriasQuadro.setVerticalAlignment(javax.swing.SwingConstants.TOP);
        lblVitoriasQuadro.setBorder(javax.swing.BorderFactory.createCompoundBorder(new javax.swing.border.LineBorder(new java.awt.Color(153, 153, 153), 2, true), javax.swing.BorderFactory.createEtchedBorder(new java.awt.Color(0, 51, 51), new java.awt.Color(51, 51, 51))));
        lblVitoriasQuadro.setOpaque(true);
        camadas.add(lblVitoriasQuadro);
        lblVitoriasQuadro.setBounds(190, 60, 320, 310);

        btnReforçosRED.setBackground(new java.awt.Color(255, 255, 255));
        btnReforçosRED.setFont(new java.awt.Font("Courier New", 1, 14)); // NOI18N
        btnReforçosRED.setIcon(new javax.swing.ImageIcon(getClass().getResource("/imagVERS/heliR.gif"))); // NOI18N
        btnReforçosRED.setText("Chamar reforços ");
        btnReforçosRED.setToolTipText("Completa o número de soldados de um território seu. Custo: 2");
        btnReforçosRED.setBorder(javax.swing.BorderFactory.createCompoundBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(25, 51, 0)), null));
        btnReforçosRED.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        btnReforçosRED.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        btnReforçosRED.setHorizontalTextPosition(javax.swing.SwingConstants.RIGHT);
        btnReforçosRED.setIconTextGap(0);
        btnReforçosRED.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnReforçosREDActionPerformed(evt);
            }
        });
        camadas.setLayer(btnReforçosRED, javax.swing.JLayeredPane.POPUP_LAYER);
        camadas.add(btnReforçosRED);
        btnReforçosRED.setBounds(0, 460, 260, 30);

        btnAereoRED.setBackground(new java.awt.Color(255, 255, 255));
        btnAereoRED.setFont(new java.awt.Font("Courier New", 1, 14)); // NOI18N
        btnAereoRED.setIcon(new javax.swing.ImageIcon(getClass().getResource("/imagVERS/cacaR.png"))); // NOI18N
        btnAereoRED.setText("1 ataque aéreo ");
        btnAereoRED.setToolTipText("Realiza um ataque a um único quadrado (no início do seu próximo turno). Custo 3");
        btnAereoRED.setBorder(javax.swing.BorderFactory.createCompoundBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(25, 51, 0)), null));
        btnAereoRED.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        btnAereoRED.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        btnAereoRED.setHorizontalTextPosition(javax.swing.SwingConstants.RIGHT);
        btnAereoRED.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnAereoREDActionPerformed(evt);
            }
        });
        camadas.setLayer(btnAereoRED, javax.swing.JLayeredPane.POPUP_LAYER);
        camadas.add(btnAereoRED);
        btnAereoRED.setBounds(0, 490, 250, 30);

        btnReforçosBLUE.setBackground(new java.awt.Color(255, 255, 255));
        btnReforçosBLUE.setFont(new java.awt.Font("Courier New", 1, 14)); // NOI18N
        btnReforçosBLUE.setIcon(new javax.swing.ImageIcon(getClass().getResource("/imagVERS/heliB.gif"))); // NOI18N
        btnReforçosBLUE.setText(" Chamar reforços");
        btnReforçosBLUE.setToolTipText("Completa o número de soldados de um território");
        btnReforçosBLUE.setBorder(javax.swing.BorderFactory.createCompoundBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(25, 51, 0)), null));
        btnReforçosBLUE.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        btnReforçosBLUE.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        btnReforçosBLUE.setHorizontalTextPosition(javax.swing.SwingConstants.LEFT);
        btnReforçosBLUE.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnReforçosBLUEActionPerformed(evt);
            }
        });
        camadas.setLayer(btnReforçosBLUE, javax.swing.JLayeredPane.POPUP_LAYER);
        camadas.add(btnReforçosBLUE);
        btnReforçosBLUE.setBounds(420, 460, 260, 30);

        btnAereoBLUE.setBackground(new java.awt.Color(255, 255, 255));
        btnAereoBLUE.setFont(new java.awt.Font("Courier New", 1, 14)); // NOI18N
        btnAereoBLUE.setIcon(new javax.swing.ImageIcon(getClass().getResource("/imagVERS/cacaB.png"))); // NOI18N
        btnAereoBLUE.setText(" 1 ataque aéreo");
        btnAereoBLUE.setToolTipText("Realiza um ataque a um único quadrado (no início do seu próximo turno). Custo 3");
        btnAereoBLUE.setBorder(javax.swing.BorderFactory.createCompoundBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(25, 51, 0)), null));
        btnAereoBLUE.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        btnAereoBLUE.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        btnAereoBLUE.setHorizontalTextPosition(javax.swing.SwingConstants.LEFT);
        btnAereoBLUE.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnAereoBLUEActionPerformed(evt);
            }
        });
        camadas.setLayer(btnAereoBLUE, javax.swing.JLayeredPane.POPUP_LAYER);
        camadas.add(btnAereoBLUE);
        btnAereoBLUE.setBounds(430, 490, 250, 30);

        btnTriploAereoBLUE.setBackground(new java.awt.Color(255, 255, 255));
        btnTriploAereoBLUE.setFont(new java.awt.Font("Courier New", 1, 14)); // NOI18N
        btnTriploAereoBLUE.setIcon(new javax.swing.ImageIcon(getClass().getResource("/imagVERS/cacaB.png"))); // NOI18N
        btnTriploAereoBLUE.setText(" 3 ataques aéreos");
        btnTriploAereoBLUE.setToolTipText("Realiza um ataque a 3 quadrados aleatórios (no início do seu próximo turno). Custo 9");
        btnTriploAereoBLUE.setBorder(javax.swing.BorderFactory.createCompoundBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(25, 51, 0)), null));
        btnTriploAereoBLUE.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        btnTriploAereoBLUE.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        btnTriploAereoBLUE.setHorizontalTextPosition(javax.swing.SwingConstants.LEFT);
        btnTriploAereoBLUE.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnTriploAereoBLUEActionPerformed(evt);
            }
        });
        camadas.setLayer(btnTriploAereoBLUE, javax.swing.JLayeredPane.POPUP_LAYER);
        camadas.add(btnTriploAereoBLUE);
        btnTriploAereoBLUE.setBounds(440, 520, 240, 30);

        btnTriploAereoRED.setBackground(new java.awt.Color(255, 255, 255));
        btnTriploAereoRED.setFont(new java.awt.Font("Courier New", 1, 14)); // NOI18N
        btnTriploAereoRED.setIcon(new javax.swing.ImageIcon(getClass().getResource("/imagVERS/cacaR.png"))); // NOI18N
        btnTriploAereoRED.setText("3 ataques aéreos ");
        btnTriploAereoRED.setToolTipText("Realiza um ataque a 3 quadrados aleatórios (no início do seu próximo turno). Custo 9.");
        btnTriploAereoRED.setActionCommand("3 ataques aéreos");
        btnTriploAereoRED.setBorder(javax.swing.BorderFactory.createCompoundBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(25, 51, 0)), null));
        btnTriploAereoRED.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        btnTriploAereoRED.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        btnTriploAereoRED.setHorizontalTextPosition(javax.swing.SwingConstants.RIGHT);
        btnTriploAereoRED.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnTriploAereoREDActionPerformed(evt);
            }
        });
        camadas.setLayer(btnTriploAereoRED, javax.swing.JLayeredPane.POPUP_LAYER);
        camadas.add(btnTriploAereoRED);
        btnTriploAereoRED.setBounds(0, 520, 240, 30);

        txtStatus.setEditable(false);
        txtStatus.setBackground(new java.awt.Color(255, 255, 200));
        txtStatus.setFont(new java.awt.Font("Courier New", 1, 14)); // NOI18N
        txtStatus.setHorizontalAlignment(javax.swing.JTextField.CENTER);
        txtStatus.setToolTipText("Tela De Status");
        txtStatus.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(25, 50, 0), 4));
        txtStatus.setMaximumSize(new java.awt.Dimension(600, 30));
        camadas.setLayer(txtStatus, javax.swing.JLayeredPane.POPUP_LAYER);
        camadas.add(txtStatus);
        txtStatus.setBounds(60, 350, 580, 30);

        jLabel1.setFont(new java.awt.Font("Courier New", 1, 11)); // NOI18N
        jLabel1.setForeground(new java.awt.Color(255, 255, 255));
        jLabel1.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        jLabel1.setText("Territorios conquistados");
        camadas.add(jLabel1);
        jLabel1.setBounds(420, 385, 250, 14);

        jLabel2.setFont(new java.awt.Font("Courier New", 1, 11)); // NOI18N
        jLabel2.setForeground(new java.awt.Color(255, 255, 255));
        jLabel2.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        jLabel2.setText("Territorios conquistados");
        camadas.add(jLabel2);
        jLabel2.setBounds(10, 385, 250, 14);

        jLabel3.setFont(new java.awt.Font("Courier New", 1, 11)); // NOI18N
        jLabel3.setForeground(new java.awt.Color(255, 255, 255));
        jLabel3.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel3.setText("Chamar apoio aéreo");
        camadas.add(jLabel3);
        jLabel3.setBounds(420, 440, 260, 14);

        jLabel4.setFont(new java.awt.Font("Courier New", 1, 11)); // NOI18N
        jLabel4.setForeground(new java.awt.Color(255, 255, 255));
        jLabel4.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel4.setText("Chamar apoio aéreo");
        jLabel4.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        camadas.add(jLabel4);
        jLabel4.setBounds(0, 440, 260, 14);

        fundo.setForeground(new java.awt.Color(153, 153, 255));
        fundo.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        fundo.setIcon(new javax.swing.ImageIcon(getClass().getResource("/imagVERS/Fundo.jpg"))); // NOI18N
        fundo.setAlignmentY(0.0F);
        camadas.add(fundo);
        fundo.setBounds(0, 0, 680, 340);

        camuflado.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        camuflado.setIcon(new javax.swing.ImageIcon(getClass().getResource("/imagVERS/camuflado.jpg"))); // NOI18N
        camadas.add(camuflado);
        camuflado.setBounds(0, 0, 680, 550);

        lblSound1.setFont(new java.awt.Font("Magneto", 1, 18)); // NOI18N
        lblSound1.setForeground(new java.awt.Color(220, 220, 220));
        lblSound1.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        lblSound1.setText("música [x]");
        lblSound1.setToolTipText("");
        lblSound1.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                lblSound1MouseClicked(evt);
            }
        });
        camadas.setLayer(lblSound1, javax.swing.JLayeredPane.PALETTE_LAYER);
        camadas.add(lblSound1);
        lblSound1.setBounds(299, 525, 91, 20);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(camadas, javax.swing.GroupLayout.DEFAULT_SIZE, 680, Short.MAX_VALUE)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(camadas, javax.swing.GroupLayout.DEFAULT_SIZE, 550, Short.MAX_VALUE)
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void vitoriaEsquerda() {
        limparSelecionaveis();
        lblRed.setVisible(false);
        lblBlue.setVisible(false);
        esqVitoria.setIcon(quadBLUE1);
        esqVitoria.setBorder(null);
        banesq.setIcon(bandeira);
        camadas.moveToFront(banesq);
        btnPassar.setEnabled(false);
        btnReforçosRED.setVisible(false);
        btnAereoRED.setVisible(false);
        btnTriploAereoRED.setVisible(false);
        btnReforçosBLUE.setVisible(false);
        btnAereoBLUE.setVisible(false);
        btnTriploAereoBLUE.setVisible(false);
        txtStatus.setText("JOGADOR AZUL VENCEU!!");
        somFundo.close();
        new MP3("somvitoria").play();
        vitoriasBLUE++;
        mostrarPlacar();
    }

    private void esqVitoriaActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_esqVitoriaActionPerformed
        if (modo.equals("servidor") || modo.equals("cliente")) {
            try {
                ostream.writeUTF("E");
                ostream.flush();
            } catch (IOException ex) {
                Logger.getLogger(Versus.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        vitoriaEsquerda();
    }//GEN-LAST:event_esqVitoriaActionPerformed

    private void vitoriaDireita() {
        limparSelecionaveis();
        lblRed.setVisible(false);
        lblBlue.setVisible(false);
        dirVitoria.setIcon(quadRED1);
        dirVitoria.setBorder(null);
        bandir.setIcon(bandeira);
        camadas.moveToFront(bandir);
        btnPassar.setEnabled(false);
        btnReforçosRED.setVisible(false);
        btnAereoRED.setVisible(false);
        btnTriploAereoRED.setVisible(false);
        btnReforçosBLUE.setVisible(false);
        btnAereoBLUE.setVisible(false);
        btnTriploAereoBLUE.setVisible(false);
        txtStatus.setText("JOGADOR VERMELHO VENCEU!!");
        somFundo.close();
        new MP3("somvitoria").play();
        vitoriasRED++;
        mostrarPlacar();
    }

    private void dirVitoriaActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_dirVitoriaActionPerformed
        if (modo.equals("servidor") || modo.equals("cliente")) {
            try {
                ostream.writeUTF("D");
                ostream.flush();
            } catch (IOException ex) {
                Logger.getLogger(Versus.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        vitoriaDireita();
    }//GEN-LAST:event_dirVitoriaActionPerformed

    private void btnPassarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnPassarActionPerformed
        if (modo.equals("servidor") || modo.equals("cliente")) {
            String pacote;
            if (atualRED) {
                if (exploRED < 10) {
                    pacote = "PR0" + exploRED;
                } else {
                    pacote = "PR" + exploRED;
                }
            } else {
                if (exploBLUE < 10) {
                    pacote = "PB0" + exploBLUE;
                } else {
                    pacote = "PB" + exploBLUE;
                }
            }
            try {
                ostream.writeUTF(pacote);
                ostream.flush();
            } catch (IOException ex) {
                Logger.getLogger(Versus.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        passarVez();
    }//GEN-LAST:event_btnPassarActionPerformed

    private void formWindowClosing(java.awt.event.WindowEvent evt) {//GEN-FIRST:event_formWindowClosing
        int esc = JOptionPane.showConfirmDialog(null, "Tem certeza que deseja desistir da partida?", "Fechando Jogo", 0, 3, icon);
        if (esc == 0) {
            System.exit(0);
        } else if (esc == 1) {
            if (modo.equals("cliente") || modo.equals("servidor")) {
                new Escolha().setVisible(true);
            }
            this.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
        }
    }//GEN-LAST:event_formWindowClosing

    private void lblSoundMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_lblSoundMouseClicked
        isMusica = !isMusica;
        if (isMusica) {
            lblSound.setText("música [x]");
            somFundo.play();
        } else {
            lblSound.setText("música [ ]");
            somFundo.close();
        }
    }//GEN-LAST:event_lblSoundMouseClicked

    private void btnReforçosREDActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnReforçosREDActionPerformed
        if (exploRED - custoReforços > 0) {
            limparSelecionaveis();
            status = "reforçar";
            txtStatus.setText("Jogador RED pediu reforços!");
            if (grafo.getVertice(2).isCorRED()) {
                vetQuadradosVisuais[2].setEnabled(true);
                vetQuadradosVisuais[2].setBorder(bordaQuadrado);
            }
            if (grafo.getVertice(3).isCorRED()) {
                vetQuadradosVisuais[3].setEnabled(true);
                vetQuadradosVisuais[3].setBorder(bordaQuadrado);
            }
            if (grafo.getVertice(4).isCorRED()) {
                vetQuadradosVisuais[4].setEnabled(true);
                vetQuadradosVisuais[4].setBorder(bordaQuadrado);
            }
        } else {
            this.msgExploraçõesInsuficientes();
        }
    }//GEN-LAST:event_btnReforçosREDActionPerformed

    private void btnAereoREDActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnAereoREDActionPerformed
        if (exploRED - custoAereo > 0) {
            limparSelecionaveis();
            status = "aereo";
            txtStatus.setText("Jogador RED irá realizar um ataque aéreo!");
            for (int x = 0; x < vetQuadradosVisuais.length; x++) {
                vetQuadradosVisuais[x].setEnabled(true);
                vetQuadradosVisuais[x].setBorder(bordaQuadrado);
            }
        } else {
            this.msgExploraçõesInsuficientes();
        }
    }//GEN-LAST:event_btnAereoREDActionPerformed

    private void btnAereoBLUEActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnAereoBLUEActionPerformed
        if (exploBLUE - custoAereo > 0) {
            limparSelecionaveis();
            status = "aereo";
            txtStatus.setText("Jogador BLUE irá realizar um ataque aéreo!");
            for (int x = 0; x < vetQuadradosVisuais.length; x++) {
                vetQuadradosVisuais[x].setEnabled(true);
                vetQuadradosVisuais[x].setBorder(bordaQuadrado);
            }
        } else {
            this.msgExploraçõesInsuficientes();
        }
    }//GEN-LAST:event_btnAereoBLUEActionPerformed

    private void btnReforçosBLUEActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnReforçosBLUEActionPerformed
        if (exploBLUE - custoReforços > 0) {
            limparSelecionaveis();
            status = "reforçar";
            txtStatus.setText("Jogador BLUE pediu reforços!");
            if (!grafo.getVertice(18).isCorRED()) {
                vetQuadradosVisuais[18].setEnabled(true);
                vetQuadradosVisuais[18].setBorder(bordaQuadrado);
            }
            if (!grafo.getVertice(19).isCorRED()) {
                vetQuadradosVisuais[19].setEnabled(true);
                vetQuadradosVisuais[19].setBorder(bordaQuadrado);
            }
            if (!grafo.getVertice(20).isCorRED()) {
                vetQuadradosVisuais[20].setEnabled(true);
                vetQuadradosVisuais[20].setBorder(bordaQuadrado);
            }
        } else {
            this.msgExploraçõesInsuficientes();
        }
    }//GEN-LAST:event_btnReforçosBLUEActionPerformed

    private void btnTriploAereoBLUEActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnTriploAereoBLUEActionPerformed
        if (exploBLUE - custoAereo > 0) {
            chamouTriploAviaoBLUE = true;
            btnAereoBLUE.setVisible(false);
            btnTriploAereoBLUE.setVisible(false);
            txtStatus.setText("Jogador BLUE chamou um Ataque Aéreo!");
        } else {
            this.msgExploraçõesInsuficientes();
        }
    }//GEN-LAST:event_btnTriploAereoBLUEActionPerformed

    private void btnTriploAereoREDActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnTriploAereoREDActionPerformed
        if (exploRED - custoAereo > 0) {
            chamouTriploAviaoRED = true;
            btnAereoRED.setVisible(false);
            btnTriploAereoRED.setVisible(false);
            txtStatus.setText("Jogador RED chamou um Ataque Aéreo!");
        } else {
            this.msgExploraçõesInsuficientes();
        }
    }//GEN-LAST:event_btnTriploAereoREDActionPerformed

    private void lblSound1MouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_lblSound1MouseClicked
        isEfeitos = !isEfeitos;
        if (isEfeitos) {
            lblSound1.setText("efeitos [x]");
        } else {
            lblSound1.setText("efeitos [ ]");
        }
    }//GEN-LAST:event_lblSound1MouseClicked

    public void msgExploraçõesInsuficientes() {
        new Thread() {
            public void run() {
                try {
                    Color cor = txtStatus.getForeground();
                    txtStatus.setForeground(Color.red);
                    txtStatus.setText("Número de explorações insuficientes!");
                    Thread.sleep(200);
                    txtStatus.setText("");
                    Thread.sleep(200);
                    txtStatus.setText("Número de explorações insuficientes!");
                    Thread.sleep(200);
                    txtStatus.setText("");
                    Thread.sleep(200);
                    txtStatus.setText("Número de explorações insuficientes!");
                    Thread.sleep(200);
                    txtStatus.setText("");
                    Thread.sleep(200);
                    txtStatus.setText("Número de explorações insuficientes!");
                    txtStatus.setForeground(cor);
                } catch (InterruptedException ex) {
                    Logger.getLogger(Versus.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        }.start();
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JLabel bandir;
    private javax.swing.JLabel banesq;
    private javax.swing.JButton btnAereoBLUE;
    private javax.swing.JButton btnAereoRED;
    private javax.swing.JButton btnPassar;
    private javax.swing.JButton btnReforçosBLUE;
    private javax.swing.JButton btnReforçosRED;
    private javax.swing.JButton btnTriploAereoBLUE;
    private javax.swing.JButton btnTriploAereoRED;
    private javax.swing.JLayeredPane camadas;
    private javax.swing.JLabel camuflado;
    private javax.swing.JButton dirVitoria;
    private javax.swing.JButton esqVitoria;
    private javax.swing.JLabel fundo;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel lblAtaqueBLUE;
    private javax.swing.JLabel lblAtaqueRED;
    private javax.swing.JLabel lblBlue;
    private javax.swing.JLabel lblHeliBLUE;
    private javax.swing.JLabel lblHeliRED;
    private javax.swing.JLabel lblMoveBLUE;
    private javax.swing.JLabel lblMoveRED;
    private javax.swing.JLabel lblRed;
    private javax.swing.JLabel lblResult1;
    private javax.swing.JLabel lblResult2;
    private javax.swing.JLabel lblResultX;
    private javax.swing.JLabel lblSound;
    private javax.swing.JLabel lblSound1;
    private javax.swing.JLabel lblVitorias;
    private javax.swing.JLabel lblVitoriasBLUE;
    private javax.swing.JLabel lblVitoriasCont;
    private javax.swing.JLabel lblVitoriasPanBLUE;
    private javax.swing.JLabel lblVitoriasPanRED;
    private javax.swing.JLabel lblVitoriasQuadro;
    private javax.swing.JLabel lblVitoriasRED;
    private javax.swing.JLabel lblVitoriasX;
    private javax.swing.JProgressBar proExplosBLUE;
    private javax.swing.JProgressBar proExplosRED;
    private javax.swing.JButton quadrado0;
    private javax.swing.JButton quadrado1;
    private javax.swing.JButton quadrado10;
    private javax.swing.JButton quadrado11;
    private javax.swing.JButton quadrado12;
    private javax.swing.JButton quadrado13;
    private javax.swing.JButton quadrado14;
    private javax.swing.JButton quadrado15;
    private javax.swing.JButton quadrado16;
    private javax.swing.JButton quadrado17;
    private javax.swing.JButton quadrado18;
    private javax.swing.JButton quadrado19;
    private javax.swing.JButton quadrado2;
    private javax.swing.JButton quadrado20;
    private javax.swing.JButton quadrado21;
    private javax.swing.JButton quadrado22;
    private javax.swing.JButton quadrado3;
    private javax.swing.JButton quadrado4;
    private javax.swing.JButton quadrado5;
    private javax.swing.JButton quadrado6;
    private javax.swing.JButton quadrado7;
    private javax.swing.JButton quadrado8;
    private javax.swing.JButton quadrado9;
    private javax.swing.JTextField txtStatus;
    // End of variables declaration//GEN-END:variables

}
