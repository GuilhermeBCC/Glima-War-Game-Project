package ui;

import java.awt.event.KeyEvent;
import java.awt.image.BufferedImage;
import java.awt.image.WritableRaster;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.ImageIcon;
import javax.swing.JLabel;
import game.campanha.*;
import util.MP3;

/**
 * Classe para o modo Campanha, contém todos os metodos e atributos do modo
 * Campanha
 *
 * @author Guilherme Lima
 */
public class Campanha extends javax.swing.JFrame {

    private Personagem person;
    private JLabel[] invent = new JLabel[3];
    private int x = 60, y = 390, wt = 680, ht = 420;
    private boolean esq, dir, cima, baixo, fire = false, begin = false, cont = false;
    private boolean morte = false, pass = false, som = false, it1 = true, it2 = true, it3 = true;
    
    private MP3 somScout = new MP3("atq3");
    private MP3 somGranada = new MP3("atq2");
    private MP3 somFundo = new MP3("somfundocamp");
    private MP3 somPassos = new MP3("passos");
    private MP3 somRadio = new MP3("radio");
    
    private ImageIcon imgRadio = new ImageIcon(getClass().getClassLoader().getResource("imagCAMP/radio.gif"));
    private ImageIcon imgFimJogo = new ImageIcon(getClass().getClassLoader().getResource("imagCAMP/fim.jpg"));
    private ImageIcon imgCapita = new ImageIcon(getClass().getClassLoader().getResource("imagINI/personSCOUT.png"));
    private ImageIcon imgExplosao = new ImageIcon(getClass().getClassLoader().getResource("imagCAMP/explosao.gif"));
    private ImageIcon imgIcon = new ImageIcon(getClass().getClassLoader().getResource("imagVERS/icon.png"));
    
    private Fase1 fase1;

    /**
     * Método construtor.A variável inteira passada por parâmetro na classe
     * Campanha, determina o tipo do Personagem que o usuário escolheu
     *
     * @param i A variável inteira passada por parâmetro na classe Campanha,
     * determina o tipo do Personagem que o usuário escolheu
     */
    public Campanha(int i) {
        initComponents();
        this.setIconImage(imgIcon.getImage());
        this.setLocationRelativeTo(null);
        heliRED.setVisible(false);
        lblQuadro.setVisible(false);
        txtDialogo.setVisible(false);
        lblExplosao.setVisible(false);
        invent[0] = lblInventario1;
        invent[1] = lblInventario2;
        invent[2] = lblInventario3;

        if (i == 1) {
            person = new PersonGLOCK();
        } else if (i == 2) {
            person = new PersonCOLT();
        } else if (i == 3) {
            person = new PersonSCOUT();
        } else {
            System.exit(0);
        }

        lblVida.setText(person.getVida() + "");
        lblAtaque.setText((person.getAtaque() + person.getAtaque() / 10) + " - " + (person.getAtaque() - person.getAtaque() / 10));
        lblDefesa.setText(person.getDefesa() + "");
        fase1 = new Fase1(this);
        movase();
    }

    /**
     * Método que retorna o label que mostra o inimigo 1 da primeira fase
     */
    public JLabel getlblInimigo1() {
        return lblInimigo1;
    }

    /**
     * Método que retorna o label que mostra o inimigo 2 da primeira fase
     */
    public JLabel getlblInimigo2() {
        return lblInimigo2;
    }

    /**
     * Método que retorna o label que mostra o inimigo 3 da primeira fase
     */
    public JLabel getlblInimigo3() {
        return lblInimigo3;
    }

    /**
     * Método que retorna o label que mostra o personagem principal
     */
    public JLabel getlblPerson() {
        return lblPerson;
    }

    /**
     * Método que retorna o personagem pricipal
     */
    public Personagem getPerson() {
        return person;
    }

    /**
     * Método que retorna o label que mostra a defesa do personagem
     */
    public JLabel getlblDefesa() {
        return lblDefesa;
    }

    /**
     * Método que retorna o label que mostra a vida do personagem
     */
    public JLabel getlblVida() {
        return lblVida;
    }

    /**
     * Método que retorna o label que representa o helicoptero de resgate
     */
    public JLabel getheliRED() {
        return heliRED;
    }

    /**
     * Método que retorna o valor X da posição do personagem
     */
    public int getXis() {
        return x;
    }

    /**
     * Método que retorna o valor Y da posição do personagem
     */
    public int getYpi() {
        return y;
    }

    /**
     * Método que realiza a interção inicial ou introdução antes do jogo
     */
    private void intro() {
        String dia = " - Capitão RED para base.. - Alguém na escuta? ";
        String res = " - Sim, Capitão RED! - Aqui é a base!  - Pode falar, na escuta! ";
        String dia1 = " - O grupo bravo caiu em uma emboscada, sou o único sobrevivente! - Requisito regaste! ";
        String res1 = " - Ok, Capitão RED, siga para as coordenadas, para o resgate! ";
        somRadio.play();
        for (int y = 0; y < 1; y++) {
            try {
                Thread.sleep(1000);
            } catch (InterruptedException ex) {
                Logger.getLogger(Campanha.class.getName()).log(Level.SEVERE, null, ex);
            }
            lblQuadro.setVisible(true);
            txtDialogo.setVisible(true);
            lblQuadro.setIcon(imgCapita);
            if (cont) {
                break;
            }
            try {
                Thread.sleep(2000);
            } catch (InterruptedException ex) {
                Logger.getLogger(Campanha.class.getName()).log(Level.SEVERE, null, ex);
            }
            if (cont) {
                break;
            }
            for (int x = 0; x < 27; x++) {
                txtDialogo.setText(dia.substring(0, x));
                try {
                    Thread.sleep(50);
                } catch (InterruptedException ex) {
                    Logger.getLogger(Campanha.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
            if (cont) {
                break;
            }
            try {
                Thread.sleep(1000);
            } catch (InterruptedException ex) {
                Logger.getLogger(Campanha.class.getName()).log(Level.SEVERE, null, ex);
            }
            if (cont) {
                break;
            }
            for (int x = 0; x < 27; x++) {
                txtDialogo.setText(dia.substring(0, x));
                try {
                    Thread.sleep(50);
                } catch (InterruptedException ex) {
                    Logger.getLogger(Campanha.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
            if (cont) {
                break;
            }
            try {
                Thread.sleep(1000);
            } catch (InterruptedException ex) {
                Logger.getLogger(Campanha.class.getName()).log(Level.SEVERE, null, ex);
            }
            if (cont) {
                break;
            }
            for (int x = 26; x < dia.length(); x++) {
                txtDialogo.setText(dia.substring(26, x));
                try {
                    Thread.sleep(50);
                } catch (InterruptedException ex) {
                    Logger.getLogger(Campanha.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
            if (cont) {
                break;
            }
            try {
                Thread.sleep(2000);
                lblQuadro.setIcon(imgRadio);
            } catch (InterruptedException ex) {
                Logger.getLogger(Campanha.class.getName()).log(Level.SEVERE, null, ex);
            }
            for (int x = 0; x < 20; x++) {
                txtDialogo.setText(res.substring(0, x));
                try {
                    Thread.sleep(50);
                } catch (InterruptedException ex) {
                    Logger.getLogger(Campanha.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
            if (cont) {
                break;
            }
            try {
                Thread.sleep(1000);
            } catch (InterruptedException ex) {
                Logger.getLogger(Campanha.class.getName()).log(Level.SEVERE, null, ex);
            }
            for (int x = 21; x < 38; x++) {
                txtDialogo.setText(res.substring(21, x));
                try {
                    Thread.sleep(50);
                } catch (InterruptedException ex) {
                    Logger.getLogger(Campanha.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
            if (cont) {
                break;
            }
            try {
                Thread.sleep(1000);
            } catch (InterruptedException ex) {
                Logger.getLogger(Campanha.class.getName()).log(Level.SEVERE, null, ex);
            }
            for (int x = 39; x < res.length(); x++) {
                txtDialogo.setText(res.substring(39, x));
                try {
                    Thread.sleep(50);
                } catch (InterruptedException ex) {
                    Logger.getLogger(Campanha.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
            if (cont) {
                break;
            }
            try {
                Thread.sleep(2000);
            } catch (InterruptedException ex) {
                Logger.getLogger(Campanha.class.getName()).log(Level.SEVERE, null, ex);
            }
            lblQuadro.setIcon(imgCapita);
            for (int x = 0; x < 66; x++) {
                txtDialogo.setText(dia1.substring(0, x));
                try {
                    Thread.sleep(50);
                } catch (InterruptedException ex) {
                    Logger.getLogger(Campanha.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
            if (cont) {
                break;
            }
            try {
                Thread.sleep(1000);
            } catch (InterruptedException ex) {
                Logger.getLogger(Campanha.class.getName()).log(Level.SEVERE, null, ex);
            }
            for (int x = 65; x < dia1.length(); x++) {
                txtDialogo.setText(dia1.substring(65, x));
                try {
                    Thread.sleep(50);
                } catch (InterruptedException ex) {
                    Logger.getLogger(Campanha.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
            if (cont) {
                break;
            }
            try {
                Thread.sleep(2000);
            } catch (InterruptedException ex) {
                Logger.getLogger(Campanha.class.getName()).log(Level.SEVERE, null, ex);
            }
            lblQuadro.setIcon(imgRadio);
            for (int x = 0; x < res1.length(); x++) {
                txtDialogo.setText(res1.substring(0, x));
                try {
                    Thread.sleep(50);
                } catch (InterruptedException ex) {
                    Logger.getLogger(Campanha.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
            try {
                Thread.sleep(4000);
            } catch (InterruptedException ex) {
                Logger.getLogger(Campanha.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        somRadio.close();
        cont = false;
        lblQuadro.setVisible(false);
        txtDialogo.setVisible(false);
    }

    /**
     * Método que clarea a área em volta do personagem.
     */
    public void clarear() {
        int r = person.getAlcance() + 15;
        int[] imageData = new int[4];
        BufferedImage image = new BufferedImage(wt, ht, BufferedImage.TYPE_INT_ARGB);
        WritableRaster raster = image.getRaster();
        for (int w = 0; w < wt; w++) {
            for (int h = 0; h < ht; h++) {
                if (((w - x) * (w - x) + (h - y) * (h - y)) < r * r) {
                    imageData[0] = 0;       // R
                    imageData[1] = 0;       // G
                    imageData[2] = 0;       // B
                    imageData[3] = 120;        // A
                    raster.setPixel(w, h, imageData);
                    if (((w - x) * (w - x) + (h - y) * (h - y)) < person.getAlcance() * person.getAlcance()) {
                        imageData[0] = 0;       // R
                        imageData[1] = 0;       // G
                        imageData[2] = 0;       // B
                        imageData[3] = 0;        // A
                        raster.setPixel(w, h, imageData);
                    }
                } else if (begin && ((w - 600) * (w - 600) + (h - 70) * (h - 70)) < 40 * 40) {
                    imageData[0] = 0;       // R
                    imageData[1] = 0;       // G
                    imageData[2] = 0;       // B
                    imageData[3] = 100;        // A
                    raster.setPixel(w, h, imageData);
                } else {
                    imageData[0] = 0;       // R
                    imageData[1] = 0;       // G
                    imageData[2] = 0;       // B
                    imageData[3] = 245;        // A
                    raster.setPixel(w, h, imageData);
                }

            }
        }
        lblEscuro.setIcon(new ImageIcon(image));
    }

    /**
     * Método que aima o cenario e o personagem principal.
     */
    private void anima() {
        new Thread() {
            public void run() {
                int temp = 150;
                int nev = 300;
                while (begin) {
                    for (int t = 0; t < 4; t++) {
                        if (!morte) {
                            if (baixo == true) {
                                lblPerson.setIcon(person.andandoF[t]);
                                try {
                                    Thread.sleep(temp);
                                } catch (InterruptedException ex) {
                                    Logger.getLogger(Campanha.class.getName()).log(Level.SEVERE, null, ex);
                                }
                            } else if (dir == true) {
                                lblPerson.setIcon(person.andando[t]);
                                try {
                                    Thread.sleep(temp);
                                } catch (InterruptedException ex) {
                                    Logger.getLogger(Campanha.class.getName()).log(Level.SEVERE, null, ex);
                                }
                            } else if (esq == true) {
                                lblPerson.setIcon(person.andandoI[t]);
                                try {
                                    Thread.sleep(temp);
                                } catch (InterruptedException ex) {
                                    Logger.getLogger(Campanha.class.getName()).log(Level.SEVERE, null, ex);
                                }
                            } else if (cima == true) {
                                lblPerson.setIcon(person.andandoC[t]);
                                try {
                                    Thread.sleep(temp);
                                } catch (InterruptedException ex) {
                                    Logger.getLogger(Campanha.class.getName()).log(Level.SEVERE, null, ex);
                                }
                            } else {
                                try {
                                    Thread.sleep(temp);
                                } catch (InterruptedException ex) {
                                    Logger.getLogger(Campanha.class.getName()).log(Level.SEVERE, null, ex);
                                }
                            }
                        } else {
                            try {
                                Thread.sleep(temp);
                            } catch (InterruptedException ex) {
                                Logger.getLogger(Campanha.class.getName()).log(Level.SEVERE, null, ex);
                            }
                        }
                        lblNevoa.setLocation(nev, 0);
                        lblNevoa1.setLocation(nev - 100, 150);
                        if (nev < -350) {
                            nev = 300;
                        } else {
                            nev = nev - 2;
                        }
                        if (som) {
                            somScout.play();
                            som = false;
                        }
                    }
                }
            }
        }.start();
    }

    /**
     * Método que realiza a animação e a interação da mina.
     */
    private void bomba() {
        lblPerson.setVisible(false);
        lblVida.setText(0 + "");
        lblExplosao.setLocation(x - 26, y - 52);
        lblExplosao.setIcon(imgExplosao);
        lblExplosao.setVisible(true);
        somGranada.play();
        try {
            Thread.sleep(900);
        } catch (InterruptedException ex) {
            Logger.getLogger(Campanha.class.getName()).log(Level.SEVERE, null, ex);
        }
        lblExplosao.setVisible(false);
        lblExplosao.setLocation(0, 0);
        menosVida();
        somGranada.close();
    }

    /**
     * Método que renicia o jogo após a morte do jogador (caso tenha vidas
     * suficientes)
     */
    private void menosVida() {
        if (person.getQuantVida() > 0) {
            person.menosQuantVida();
            person.setVida(100);
            person.setDefesa(person.getDefesaFull());
            lblVida.setText(person.getVida() + "");
            lblDefesa.setText(person.getDefesa() + "");
            lblVida.setToolTipText("Restam " + person.getQuantVida() + " vidas!");
            lblPerson.setIcon(person.parado);
            lblPerson.setVisible(true);
            x = 60;
            y = 390;
            somFundo.close();
            somFundo.play();
        } else {
            fimJogo();
        }
    }

    /**
     * Método que desenha a bala no jogo (tando do principal quanto dos
     * inimigos)
     */
    public void animaTiro(final int OX, final int OY, final int DX, final int DY) {
        new Thread() {
            public void run() {
                int r = 2, dist = 0;
                double dirX, dirY;
                int OriX = OX, OriY = OY, DesX = DX, DesY = DY;
                if (OriX > DesX) {
                    dirX = -(OriX - DesX) / 20;
                    if (OriY > DesY) {
                        dirY = -(OriY - DesY) / 20;
                    } else {
                        dirY = (DesY - OriY) / 20;
                    }
                } else {
                    dirX = (DesX - OriX) / 20;
                    if (OriY > DesY) {
                        dirY = -(OriY - DesY) / 20;
                    } else {
                        dirY = (DesY - OriY) / 20;
                    }
                }
                int wt = 680;
                int ht = 420;
                int[] imageData = new int[4];
                BufferedImage image = new BufferedImage(wt, ht, BufferedImage.TYPE_INT_ARGB);
                WritableRaster raster = image.getRaster();
                while (dist < 26) {
                    // desenhar bola
                    for (int w = 0; w < wt; w++) {
                        for (int h = 0; h < ht; h++) {
                            if (((w - OriX) * (w - OriX) + (h - OriY) * (h - OriY)) < r * r) {
                                imageData[0] = 205;       // R
                                imageData[1] = 205;       // G
                                imageData[2] = 205;       // B
                                imageData[3] = 205;        // A
                                raster.setPixel(w, h, imageData);
                            } else {
                                imageData[0] = 0;       // R
                                imageData[1] = 0;       // G
                                imageData[2] = 0;       // B
                                imageData[3] = 0;        // A
                                raster.setPixel(w, h, imageData);
                            }
                        }
                    }
                    lblTiros.setIcon(new ImageIcon(image));
                    //fim desenhar bola
                    OriX = (int) (OriX + dirX);
                    OriY = (int) (OriY + dirY);
                    dist++;
                    try {
                        Thread.sleep(10);
                    } catch (InterruptedException ex) {
                        Logger.getLogger(Campanha.class.getName()).log(Level.SEVERE, null, ex);
                    }
                }
                for (int w = 0; w < wt; w++) {
                    for (int h = 0; h < ht; h++) {
                        imageData[0] = 0;       // R
                        imageData[1] = 0;       // G
                        imageData[2] = 0;       // B
                        imageData[3] = 0;        // A
                        raster.setPixel(w, h, imageData);
                    }
                }
            }
        }.start();
    }

    /**
     * Método que realiza a interação quando o jogo acaba
     */
    private void fimJogo() {
        begin = false;
        panControles.setVisible(false);
    }

    /**
     * Método que move o personagem lógico
     */
    private void move() {
        if (dir) {
            x = x + person.getVelocidade();
        }
        if (cima) {
            y = y - person.getVelocidade();
        }
        if (baixo) {
            y = y + person.getVelocidade();
        }
        if (esq) {
            x = x - person.getVelocidade();
        }
    }

    /**
     * Método que controla todoas as ações do personagem principal. (Movimenos,
     * itens, ataques..)
     */
    private void movase() {
        new Thread() {
            public void run() {
                clarear();
                intro();
                somFundo.play();
                begin = true;
                pass = true;
                anima();
                fase1.inimigos();
                while (begin) {
                    if (45 < x && x < 110) {
                        if (45 < x && x < 70) {
                            if (120 < y && y < 145 && it3) {
                                Item gran = new ItemGranada();
                                int espaco = person.setInventario(gran);
                                if (espaco != -1) {
                                    Item3.setVisible(false);
                                    invent[espaco].setIcon(person.getInventario(espaco).getImag());
                                    it3 = false;
                                } else {
                                    System.out.println("Inventario cheio!");
                                }
                            } else if (150 < y && y < 175 && it2) {
                                Item completarVida = new ItemCompletarVida();
                                int espaco = person.setInventario(completarVida);
                                if (espaco != -1) {
                                    Item2.setVisible(false);
                                    invent[espaco].setIcon(person.getInventario(espaco).getImag());
                                    it2 = false;
                                } else {
                                    System.out.println("Inventario cheio!");
                                }
                            } else if (180 < y && y < 205 && it1) {
                                Item restaurarDefesa = new ItemRestaurarDefesa();
                                int espaco = person.setInventario(restaurarDefesa);
                                if (espaco != -1) {
                                    Item1.setVisible(false);
                                    invent[espaco].setIcon(person.getInventario(espaco).getImag());
                                    it1 = false;
                                } else {
                                    System.out.println("Inventario cheio!");
                                }
                            }
                        } else if (80 < x && x < 105 && 120 < y && y < 140) {
                            bomba();
                        }
                        if (30 < y && y < 410) {
                            move();
                            if (x >= 110) {
                                if (30 < y && y < 90) {
                                    x++; // passa bloco
                                } else {
                                    x = x - person.getVelocidade();
                                }
                            } else if (x <= 45) {
                                x = x + person.getVelocidade();
                            }
                            if (y <= 30) {
                                y = y + person.getVelocidade();
                            } else if (y >= 410) {
                                y = y - person.getVelocidade();
                            }
                        }
                    } else if (110 < x && x < 280) {
                        if (30 < y && y < 50) {
                            camadasCAMP.moveToFront(lblInimigo1);
                        } else {
                            camadasCAMP.moveToBack(lblInimigo1);
                        }
                        if (250 < x && x < 270 && 80 < y && y < 100) {
                            bomba();
                        }
                        if (30 < y && y < 90) {
                            move();
                            if (x >= 280) {
                                x++; // passa bloco
                            } else if (x <= 110) {
                                x--; // passa bloco
                            }
                            if (y <= 30) {
                                y = y + person.getVelocidade();
                            } else if (y >= 90) {
                                y = y - person.getVelocidade();
                            }
                        }
                    } else if (280 <= x && x < 350) {
                        if (270 < x && x < 310 && 310 < y && y < 340) {
                            bomba();
                        }
                        if (30 < y && y < 350) {
                            move();
                            if (x <= 280) {
                                if (35 < y && y < 90) {
                                    x--; // passa bloco
                                } else {
                                    x = x + person.getVelocidade();
                                }
                            } else if (x >= 350) {
                                if (200 < y && y < 350) {
                                    x++; // passa bloco
                                } else {
                                    x = x - person.getVelocidade();
                                }
                            }
                            if (y <= 35) {
                                y = y + person.getVelocidade();
                            } else if (y >= 350) {
                                y = y - person.getVelocidade();
                            }
                        }
                    } else if (350 <= x && x < 520) {
                        if (200 < y && y < 350) {
                            move();
                            if (x >= 520) {
                                x++; // passa bloco
                            } else if (x <= 350) {
                                x--; // passa bloco
                            }
                            if (y <= 200) {
                                y = y + person.getVelocidade();
                            } else if (y >= 350) {
                                y = y - person.getVelocidade();
                            }
                        }
                    } else if (520 < x && x < 600) {
                        if (580 < x && x < 600 && 160 < y && y < 180) {
                            bomba();
                        }
                        if (130 < y && y < 350) {
                            move();
                            if (y <= 130) {
                                fase1.fimFase();
                                break;
                            } else if (x >= 600) {
                                x = x - person.getVelocidade();
                            } else if (x <= 520) {
                                if (200 < y && y < 350) {
                                    x--;
                                } else {
                                    x = x + person.getVelocidade();
                                }
                            }
                            if (y >= 350) {
                                y = y - person.getVelocidade();
                            }
                        }
                    }
                    if (person.getVida() <= 0) {
                        cenaMorte();
                    }
                    if (fire) {
                        for (int w = 0; w < wt; w++) {
                            for (int h = 0; h < ht; h++) {
                                if (((w - x) * (w - x) + (h - y) * (h - y)) < (person.getAlcance() + 15) * person.getAlcance()) {
                                    if (fase1.getInimigo1().getPosX() == w && fase1.getInimigo1().getPosY() == h && fase1.getInimigo1().getVida() > 0) {
                                        morte = true; //aprov. de var.   
                                        som = true;
                                        if (x > fase1.getInimigo1().getPosX()) {
                                            lblPerson.setIcon(person.paradoI);
                                        } else {
                                            lblPerson.setIcon(person.parado);
                                        }
                                        person.atacar(fase1.getInimigo1());
                                        animaTiro(x, y, fase1.getInimigo1().getPosX(), fase1.getInimigo1().getPosY());
                                        try {
                                            Thread.sleep(person.getTempDeAtq());
                                        } catch (InterruptedException ex) {
                                            Logger.getLogger(Campanha.class.getName()).log(Level.SEVERE, null, ex);
                                        }
                                        morte = false;
                                    } else if (fase1.getInimigo2().getPosX() == w && fase1.getInimigo2().getPosY() == h && fase1.getInimigo2().getVida() > 0) {
                                        morte = true; //aprov. de var.
                                        som = true;
                                        if (x > fase1.getInimigo2().getPosX()) {
                                            lblPerson.setIcon(person.paradoI);
                                        } else {
                                            lblPerson.setIcon(person.parado);
                                        }
                                        person.atacar(fase1.getInimigo2());
                                        animaTiro(x, y, fase1.getInimigo2().getPosX(), fase1.getInimigo2().getPosY());
                                        try {
                                            Thread.sleep(person.getTempDeAtq());
                                        } catch (InterruptedException ex) {
                                            Logger.getLogger(Campanha.class.getName()).log(Level.SEVERE, null, ex);
                                        }
                                        morte = false;
                                    } else if (fase1.getInimigo3().getPosX() == w && fase1.getInimigo3().getPosY() == h && fase1.getInimigo3().getVida() > 0) {
                                        morte = true; //aprov. de var.
                                        som = true;
                                        if (x > fase1.getInimigo3().getPosX()) {
                                            lblPerson.setIcon(person.paradoI);
                                        } else {
                                            lblPerson.setIcon(person.parado);
                                        }
                                        person.atacar(fase1.getInimigo3());
                                        animaTiro(x, y, fase1.getInimigo3().getPosX(), fase1.getInimigo3().getPosY());
                                        try {
                                            Thread.sleep(person.getTempDeAtq());
                                        } catch (InterruptedException ex) {
                                            Logger.getLogger(Campanha.class.getName()).log(Level.SEVERE, null, ex);
                                        }
                                        morte = false;
                                    }
                                }
                            }
                        }
                        fire = false;
                    }
                    clarear();
                    lblPerson.setLocation(x - 35, y - 25);
                    try {
                        Thread.sleep(20);
                    } catch (InterruptedException ex) {
                        Logger.getLogger(Campanha.class.getName()).log(Level.SEVERE, null, ex);
                    }
                }
                lblEscuro.setIcon(imgFimJogo);
            }
        }.start();
    }

    /**
     * Método que realiza a animação da morte de um personagem
     */
    private void cenaMorte() {
        morte = true;
        lblPerson.setIcon(new ImageIcon(getClass().getClassLoader().getResource("imagCAMP/morte1.png")));
        try {
            Thread.sleep(200);
        } catch (InterruptedException ex) {
            Logger.getLogger(Campanha.class.getName()).log(Level.SEVERE, null, ex);
        }
        lblPerson.setIcon(new ImageIcon(getClass().getClassLoader().getResource("imagCAMP/morte2.png")));
        try {
            Thread.sleep(200);
        } catch (InterruptedException ex) {
            Logger.getLogger(Campanha.class.getName()).log(Level.SEVERE, null, ex);
        }
        lblPerson.setIcon(new ImageIcon(getClass().getClassLoader().getResource("imagCAMP/morte3.png")));
        try {
            Thread.sleep(200);
        } catch (InterruptedException ex) {
            Logger.getLogger(Campanha.class.getName()).log(Level.SEVERE, null, ex);
        }
        lblPerson.setIcon(new ImageIcon(getClass().getClassLoader().getResource("imagCAMP/morte4.png")));
        try {
            Thread.sleep(200);
        } catch (InterruptedException ex) {
            Logger.getLogger(Campanha.class.getName()).log(Level.SEVERE, null, ex);
        }
        lblPerson.setIcon(new ImageIcon(getClass().getClassLoader().getResource("imagCAMP/morte5.png")));
        try {
            Thread.sleep(200);
        } catch (InterruptedException ex) {
            Logger.getLogger(Campanha.class.getName()).log(Level.SEVERE, null, ex);
        }
        lblPerson.setIcon(new ImageIcon(getClass().getClassLoader().getResource("imagCAMP/morte6.png")));
        try {
            Thread.sleep(200);
        } catch (InterruptedException ex) {
            Logger.getLogger(Campanha.class.getName()).log(Level.SEVERE, null, ex);
        }
        lblPerson.setIcon(new ImageIcon(getClass().getClassLoader().getResource("imagCAMP/morte7.png")));
        try {
            Thread.sleep(2000);
        } catch (InterruptedException ex) {
            Logger.getLogger(Campanha.class.getName()).log(Level.SEVERE, null, ex);
        }
        lblPerson.setVisible(false);
        menosVida();
        morte = false;
    }

    /**
     * Método que realiza a animação quando o personage principla passa de um
     * fase.
     */

    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        camadasCAMP = new javax.swing.JLayeredPane();
        panControles = new javax.swing.JPanel();
        lblVida = new javax.swing.JLabel();
        lblAtaque = new javax.swing.JLabel();
        lblDefesa = new javax.swing.JLabel();
        lblInventario2 = new javax.swing.JLabel();
        lblInventario3 = new javax.swing.JLabel();
        lblInventario1 = new javax.swing.JLabel();
        lblQuadro = new javax.swing.JLabel();
        txtDialogo = new javax.swing.JTextArea();
        lblEscuro = new javax.swing.JLabel();
        lblTiros = new javax.swing.JLabel();
        heliRED = new javax.swing.JLabel();
        lblNevoa = new javax.swing.JLabel();
        lblFlorestaCima = new javax.swing.JLabel();
        lblNevoa1 = new javax.swing.JLabel();
        lblExplosao = new javax.swing.JLabel();
        lblPerson = new javax.swing.JLabel();
        lblInimigo1 = new javax.swing.JLabel();
        lblInimigo2 = new javax.swing.JLabel();
        lblInimigo3 = new javax.swing.JLabel();
        Item1 = new javax.swing.JLabel();
        Item2 = new javax.swing.JLabel();
        Item3 = new javax.swing.JLabel();
        lblFlorestaBaixo = new javax.swing.JLabel();
        lblTerreno = new javax.swing.JLabel();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        setTitle("GLIMA War Game - Modo Campanha");
        setAlwaysOnTop(true);
        setBackground(new java.awt.Color(0, 0, 0));
        setBounds(new java.awt.Rectangle(325, 125, 0, 0));
        setForeground(java.awt.Color.white);
        setResizable(false);
        addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                formKeyPressed(evt);
            }
            public void keyReleased(java.awt.event.KeyEvent evt) {
                formKeyReleased(evt);
            }
        });

        camadasCAMP.setOpaque(true);

        panControles.setBackground(new java.awt.Color(0, 51, 51));
        panControles.setBorder(javax.swing.BorderFactory.createCompoundBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0), 2), javax.swing.BorderFactory.createBevelBorder(javax.swing.border.BevelBorder.LOWERED, null, new java.awt.Color(0, 51, 51), null, new java.awt.Color(0, 0, 51))));

        lblVida.setFont(new java.awt.Font("Magneto", 1, 18)); // NOI18N
        lblVida.setForeground(new java.awt.Color(204, 0, 0));
        lblVida.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        lblVida.setIcon(new javax.swing.ImageIcon(getClass().getResource("/imagCAMP/vida.jpg"))); // NOI18N
        lblVida.setToolTipText("Restam 3 vidas!");
        lblVida.setBorder(javax.swing.BorderFactory.createEtchedBorder(new java.awt.Color(153, 153, 153), new java.awt.Color(102, 102, 102)));
        lblVida.setHorizontalTextPosition(javax.swing.SwingConstants.RIGHT);
        lblVida.setMaximumSize(new java.awt.Dimension(125, 45));
        lblVida.setMinimumSize(new java.awt.Dimension(125, 40));
        lblVida.setOpaque(true);
        lblVida.setPreferredSize(new java.awt.Dimension(125, 42));

        lblAtaque.setFont(new java.awt.Font("Magneto", 1, 18)); // NOI18N
        lblAtaque.setForeground(new java.awt.Color(0, 0, 102));
        lblAtaque.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        lblAtaque.setIcon(new javax.swing.ImageIcon(getClass().getResource("/imagCAMP/ataque.jpg"))); // NOI18N
        lblAtaque.setToolTipText("ATAQUE");
        lblAtaque.setBorder(javax.swing.BorderFactory.createEtchedBorder(new java.awt.Color(153, 153, 153), new java.awt.Color(102, 102, 102)));
        lblAtaque.setHorizontalTextPosition(javax.swing.SwingConstants.RIGHT);
        lblAtaque.setMaximumSize(new java.awt.Dimension(125, 45));
        lblAtaque.setMinimumSize(new java.awt.Dimension(125, 40));
        lblAtaque.setOpaque(true);
        lblAtaque.setPreferredSize(new java.awt.Dimension(125, 45));

        lblDefesa.setFont(new java.awt.Font("Magneto", 1, 18)); // NOI18N
        lblDefesa.setForeground(new java.awt.Color(51, 51, 51));
        lblDefesa.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        lblDefesa.setIcon(new javax.swing.ImageIcon(getClass().getResource("/imagCAMP/defesa.jpg"))); // NOI18N
        lblDefesa.setToolTipText("DEFESA");
        lblDefesa.setBorder(javax.swing.BorderFactory.createEtchedBorder(new java.awt.Color(153, 153, 153), new java.awt.Color(102, 102, 102)));
        lblDefesa.setHorizontalTextPosition(javax.swing.SwingConstants.RIGHT);
        lblDefesa.setMaximumSize(new java.awt.Dimension(125, 45));
        lblDefesa.setMinimumSize(new java.awt.Dimension(125, 40));
        lblDefesa.setOpaque(true);
        lblDefesa.setPreferredSize(new java.awt.Dimension(125, 45));

        lblInventario2.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        lblInventario2.setToolTipText("Item 2");
        lblInventario2.setVerticalAlignment(javax.swing.SwingConstants.TOP);
        lblInventario2.setBorder(javax.swing.BorderFactory.createCompoundBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 102, 0)), new javax.swing.border.SoftBevelBorder(javax.swing.border.BevelBorder.RAISED, new java.awt.Color(204, 204, 204), new java.awt.Color(153, 153, 153))));
        lblInventario2.setOpaque(true);
        lblInventario2.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                lblInventario2MouseClicked(evt);
            }
        });

        lblInventario3.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        lblInventario3.setToolTipText("Item 3");
        lblInventario3.setVerticalAlignment(javax.swing.SwingConstants.TOP);
        lblInventario3.setBorder(javax.swing.BorderFactory.createCompoundBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 102, 0)), new javax.swing.border.SoftBevelBorder(javax.swing.border.BevelBorder.RAISED, new java.awt.Color(204, 204, 204), new java.awt.Color(153, 153, 153))));
        lblInventario3.setOpaque(true);
        lblInventario3.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                lblInventario3MouseClicked(evt);
            }
        });

        lblInventario1.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        lblInventario1.setToolTipText("Item 1");
        lblInventario1.setVerticalAlignment(javax.swing.SwingConstants.TOP);
        lblInventario1.setBorder(javax.swing.BorderFactory.createCompoundBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 102, 0)), new javax.swing.border.SoftBevelBorder(javax.swing.border.BevelBorder.RAISED, new java.awt.Color(204, 204, 204), new java.awt.Color(153, 153, 153))));
        lblInventario1.setOpaque(true);
        lblInventario1.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                lblInventario1MouseClicked(evt);
            }
        });

        javax.swing.GroupLayout panControlesLayout = new javax.swing.GroupLayout(panControles);
        panControles.setLayout(panControlesLayout);
        panControlesLayout.setHorizontalGroup(
            panControlesLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, panControlesLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(lblVida, javax.swing.GroupLayout.PREFERRED_SIZE, 125, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(lblAtaque, javax.swing.GroupLayout.PREFERRED_SIZE, 125, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(lblDefesa, javax.swing.GroupLayout.PREFERRED_SIZE, 125, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 55, Short.MAX_VALUE)
                .addComponent(lblInventario1, javax.swing.GroupLayout.PREFERRED_SIZE, 60, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(lblInventario2, javax.swing.GroupLayout.PREFERRED_SIZE, 60, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(lblInventario3, javax.swing.GroupLayout.PREFERRED_SIZE, 60, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(27, 27, 27))
        );
        panControlesLayout.setVerticalGroup(
            panControlesLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(panControlesLayout.createSequentialGroup()
                .addGroup(panControlesLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(lblVida, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(lblDefesa, javax.swing.GroupLayout.PREFERRED_SIZE, 0, Short.MAX_VALUE)
                    .addComponent(lblAtaque, javax.swing.GroupLayout.PREFERRED_SIZE, 0, Short.MAX_VALUE)
                    .addComponent(lblInventario1, javax.swing.GroupLayout.PREFERRED_SIZE, 43, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(lblInventario2, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(lblInventario3, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addContainerGap())
        );

        camadasCAMP.setLayer(panControles, javax.swing.JLayeredPane.DRAG_LAYER);
        camadasCAMP.add(panControles);
        panControles.setBounds(0, 410, 679, 50);

        lblQuadro.setBackground(new java.awt.Color(255, 255, 255));
        lblQuadro.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        lblQuadro.setText(" ");
        lblQuadro.setVerticalAlignment(javax.swing.SwingConstants.TOP);
        lblQuadro.setBorder(javax.swing.BorderFactory.createCompoundBorder(new javax.swing.border.LineBorder(new java.awt.Color(0, 51, 51), 2, true), javax.swing.BorderFactory.createEtchedBorder(new java.awt.Color(204, 204, 204), new java.awt.Color(153, 153, 153))));
        lblQuadro.setOpaque(true);
        camadasCAMP.setLayer(lblQuadro, javax.swing.JLayeredPane.MODAL_LAYER);
        camadasCAMP.add(lblQuadro);
        lblQuadro.setBounds(170, 320, 55, 70);

        txtDialogo.setColumns(2);
        txtDialogo.setFont(new java.awt.Font("Arial", 2, 12)); // NOI18N
        txtDialogo.setForeground(new java.awt.Color(102, 51, 0));
        txtDialogo.setLineWrap(true);
        txtDialogo.setRows(5);
        txtDialogo.setTabSize(10);
        txtDialogo.setWrapStyleWord(true);
        txtDialogo.setBorder(javax.swing.BorderFactory.createCompoundBorder(new javax.swing.border.LineBorder(new java.awt.Color(0, 51, 51), 2, true), javax.swing.BorderFactory.createEtchedBorder(new java.awt.Color(204, 204, 255), new java.awt.Color(0, 102, 51))));
        camadasCAMP.setLayer(txtDialogo, javax.swing.JLayeredPane.MODAL_LAYER);
        camadasCAMP.add(txtDialogo);
        txtDialogo.setBounds(240, 340, 270, 50);

        lblEscuro.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        lblEscuro.setAlignmentY(0.0F);
        camadasCAMP.setLayer(lblEscuro, javax.swing.JLayeredPane.MODAL_LAYER);
        camadasCAMP.add(lblEscuro);
        lblEscuro.setBounds(0, 0, 680, 420);

        lblTiros.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        lblTiros.setAlignmentY(0.0F);
        camadasCAMP.setLayer(lblTiros, javax.swing.JLayeredPane.MODAL_LAYER);
        camadasCAMP.add(lblTiros);
        lblTiros.setBounds(0, 0, 680, 420);

        heliRED.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        heliRED.setIcon(new javax.swing.ImageIcon(getClass().getResource("/imagVERS/heliR.gif"))); // NOI18N
        camadasCAMP.setLayer(heliRED, javax.swing.JLayeredPane.MODAL_LAYER);
        camadasCAMP.add(heliRED);
        heliRED.setBounds(530, 50, 110, 46);

        lblNevoa.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        lblNevoa.setIcon(new javax.swing.ImageIcon(getClass().getResource("/imagCAMP/nevoa.png"))); // NOI18N
        lblNevoa.setAlignmentY(0.0F);
        camadasCAMP.setLayer(lblNevoa, javax.swing.JLayeredPane.MODAL_LAYER);
        camadasCAMP.add(lblNevoa);
        lblNevoa.setBounds(0, 0, 680, 420);

        lblFlorestaCima.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        lblFlorestaCima.setIcon(new javax.swing.ImageIcon(getClass().getResource("/imagCAMP/florestaCima.png"))); // NOI18N
        lblFlorestaCima.setAlignmentY(0.0F);
        camadasCAMP.setLayer(lblFlorestaCima, javax.swing.JLayeredPane.MODAL_LAYER);
        camadasCAMP.add(lblFlorestaCima);
        lblFlorestaCima.setBounds(0, 0, 680, 420);

        lblNevoa1.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        lblNevoa1.setIcon(new javax.swing.ImageIcon(getClass().getResource("/imagCAMP/nevoa.png"))); // NOI18N
        lblNevoa1.setAlignmentY(0.0F);
        camadasCAMP.setLayer(lblNevoa1, javax.swing.JLayeredPane.MODAL_LAYER);
        camadasCAMP.add(lblNevoa1);
        lblNevoa1.setBounds(0, 0, 680, 420);

        lblExplosao.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        lblExplosao.setIcon(new javax.swing.ImageIcon(getClass().getResource("/imagCAMP/explosao.gif"))); // NOI18N
        lblExplosao.setPreferredSize(new java.awt.Dimension(5, 5));
        camadasCAMP.setLayer(lblExplosao, javax.swing.JLayeredPane.MODAL_LAYER);
        camadasCAMP.add(lblExplosao);
        lblExplosao.setBounds(600, 340, 50, 70);

        lblPerson.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        lblPerson.setIcon(new javax.swing.ImageIcon(getClass().getResource("/imagCAMP/snipaP.png"))); // NOI18N
        lblPerson.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        lblPerson.setMaximumSize(new java.awt.Dimension(75, 55));
        lblPerson.setMinimumSize(new java.awt.Dimension(75, 55));
        lblPerson.setPreferredSize(new java.awt.Dimension(75, 55));
        camadasCAMP.setLayer(lblPerson, javax.swing.JLayeredPane.PALETTE_LAYER);
        camadasCAMP.add(lblPerson);
        lblPerson.setBounds(30, 360, 80, 50);

        lblInimigo1.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        lblInimigo1.setIcon(new javax.swing.ImageIcon(getClass().getResource("/imagCAMP/inimigo1.png"))); // NOI18N
        lblInimigo1.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        lblInimigo1.setMaximumSize(new java.awt.Dimension(75, 55));
        lblInimigo1.setMinimumSize(new java.awt.Dimension(75, 55));
        lblInimigo1.setPreferredSize(new java.awt.Dimension(75, 55));
        camadasCAMP.setLayer(lblInimigo1, javax.swing.JLayeredPane.PALETTE_LAYER);
        camadasCAMP.add(lblInimigo1);
        lblInimigo1.setBounds(160, 20, 80, 50);

        lblInimigo2.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        lblInimigo2.setIcon(new javax.swing.ImageIcon(getClass().getResource("/imagCAMP/inimigo1.png"))); // NOI18N
        lblInimigo2.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        lblInimigo2.setMaximumSize(new java.awt.Dimension(75, 55));
        lblInimigo2.setMinimumSize(new java.awt.Dimension(75, 55));
        lblInimigo2.setPreferredSize(new java.awt.Dimension(75, 55));
        camadasCAMP.setLayer(lblInimigo2, javax.swing.JLayeredPane.PALETTE_LAYER);
        camadasCAMP.add(lblInimigo2);
        lblInimigo2.setBounds(420, 200, 80, 50);

        lblInimigo3.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        lblInimigo3.setIcon(new javax.swing.ImageIcon(getClass().getResource("/imagCAMP/inimigo1.png"))); // NOI18N
        lblInimigo3.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        lblInimigo3.setMaximumSize(new java.awt.Dimension(75, 55));
        lblInimigo3.setMinimumSize(new java.awt.Dimension(75, 55));
        lblInimigo3.setPreferredSize(new java.awt.Dimension(75, 55));
        camadasCAMP.setLayer(lblInimigo3, javax.swing.JLayeredPane.PALETTE_LAYER);
        camadasCAMP.add(lblInimigo3);
        lblInimigo3.setBounds(420, 280, 80, 50);

        Item1.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        Item1.setIcon(new javax.swing.ImageIcon(getClass().getResource("/imagCAMP/caixa.png"))); // NOI18N
        Item1.setToolTipText("restaurar defesa");
        Item1.setCursor(new java.awt.Cursor(java.awt.Cursor.DEFAULT_CURSOR));
        camadasCAMP.setLayer(Item1, javax.swing.JLayeredPane.PALETTE_LAYER);
        camadasCAMP.add(Item1);
        Item1.setBounds(45, 180, 25, 25);

        Item2.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        Item2.setIcon(new javax.swing.ImageIcon(getClass().getResource("/imagCAMP/caixa.png"))); // NOI18N
        Item2.setToolTipText("completar vida");
        Item2.setCursor(new java.awt.Cursor(java.awt.Cursor.DEFAULT_CURSOR));
        camadasCAMP.setLayer(Item2, javax.swing.JLayeredPane.PALETTE_LAYER);
        camadasCAMP.add(Item2);
        Item2.setBounds(45, 150, 25, 25);

        Item3.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        Item3.setIcon(new javax.swing.ImageIcon(getClass().getResource("/imagCAMP/caixa.png"))); // NOI18N
        Item3.setToolTipText("granada");
        Item3.setCursor(new java.awt.Cursor(java.awt.Cursor.DEFAULT_CURSOR));
        camadasCAMP.setLayer(Item3, javax.swing.JLayeredPane.PALETTE_LAYER);
        camadasCAMP.add(Item3);
        Item3.setBounds(45, 120, 25, 25);

        lblFlorestaBaixo.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        lblFlorestaBaixo.setIcon(new javax.swing.ImageIcon(getClass().getResource("/imagCAMP/florestaBaixo.png"))); // NOI18N
        lblFlorestaBaixo.setAlignmentY(0.0F);
        camadasCAMP.add(lblFlorestaBaixo);
        lblFlorestaBaixo.setBounds(0, 0, 680, 420);

        lblTerreno.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        lblTerreno.setIcon(new javax.swing.ImageIcon(getClass().getResource("/imagCAMP/terreno.jpg"))); // NOI18N
        lblTerreno.setAlignmentY(0.0F);
        camadasCAMP.add(lblTerreno);
        lblTerreno.setBounds(0, 0, 680, 420);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(camadasCAMP, javax.swing.GroupLayout.DEFAULT_SIZE, 679, Short.MAX_VALUE)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(camadasCAMP, javax.swing.GroupLayout.DEFAULT_SIZE, 458, Short.MAX_VALUE)
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void formKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_formKeyPressed
        if (evt.getID() == KeyEvent.KEY_PRESSED) {
            if (evt.getKeyCode() == KeyEvent.VK_UP) {
                cima = true;
            }
            if (evt.getKeyCode() == KeyEvent.VK_DOWN) {
                baixo = true;
            }
            if (evt.getKeyCode() == KeyEvent.VK_LEFT) {
                esq = true;
            }
            if (evt.getKeyCode() == KeyEvent.VK_RIGHT) {
                dir = true;
            }
            if (evt.getKeyCode() == KeyEvent.VK_SPACE) {
                fire = true;
            }
            if (evt.getKeyCode() == KeyEvent.VK_1) {
                lblInventario1MouseClicked(null);
            } else if (evt.getKeyCode() == KeyEvent.VK_2) {
                lblInventario2MouseClicked(null);
            } else if (evt.getKeyCode() == KeyEvent.VK_3) {
                lblInventario3MouseClicked(null);
            }
        }
        if (pass && (cima || baixo || esq || dir)) {
            somPassos.play();
            pass = false;
        }
    }//GEN-LAST:event_formKeyPressed

private void formKeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_formKeyReleased
    boolean d = dir;
    boolean b = baixo;
    boolean e = esq;
    boolean c = cima;
    if (evt.getID() == KeyEvent.KEY_RELEASED) {
        if (evt.getKeyCode() == KeyEvent.VK_UP) {
            cima = false;
        }
        if (evt.getKeyCode() == KeyEvent.VK_DOWN) {
            baixo = false;
        }
        if (evt.getKeyCode() == KeyEvent.VK_LEFT) {
            esq = false;
        }
        if (evt.getKeyCode() == KeyEvent.VK_RIGHT) {
            dir = false;
        }
        if (evt.getKeyCode() == KeyEvent.VK_ENTER) {
            cont = true;
        }
    }
    if (!cima && !baixo && !esq && !dir && !morte) {
        if (d) {
            lblPerson.setIcon(person.parado);
        } else if (e) {
            lblPerson.setIcon(person.paradoI);
        } else if (c) {
            lblPerson.setIcon(person.paradoC);
        } else if (b) {
            lblPerson.setIcon(person.paradoF);
        }
    }
    if (!pass && !cima && !baixo && !esq && !dir) {
        somPassos.close();
        pass = true;
    }
}//GEN-LAST:event_formKeyReleased

    private void lblInventario1MouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_lblInventario1MouseClicked
        if (person.getInventario(0) != null) {
            person.ativarItem(0);
        }
        lblInventario1.setIcon(null);
        lblVida.setText(person.getVida() + "");
        lblDefesa.setText(person.getDefesa() + "");
    }//GEN-LAST:event_lblInventario1MouseClicked

    private void lblInventario2MouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_lblInventario2MouseClicked
        if (person.getInventario(1) != null) {
            person.ativarItem(1);
        }
        lblInventario2.setIcon(null);
        lblVida.setText(person.getVida() + "");
        lblDefesa.setText(person.getDefesa() + "");
    }//GEN-LAST:event_lblInventario2MouseClicked

    private void lblInventario3MouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_lblInventario3MouseClicked
        if (person.getInventario(2) != null) {
            person.ativarItem(2);
        }
        lblInventario3.setIcon(null);
        lblVida.setText(person.getVida() + "");
        lblDefesa.setText(person.getDefesa() + "");
    }//GEN-LAST:event_lblInventario3MouseClicked

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JLabel Item1;
    private javax.swing.JLabel Item2;
    private javax.swing.JLabel Item3;
    private javax.swing.JLayeredPane camadasCAMP;
    private javax.swing.JLabel heliRED;
    private javax.swing.JLabel lblAtaque;
    private javax.swing.JLabel lblDefesa;
    private javax.swing.JLabel lblEscuro;
    private javax.swing.JLabel lblExplosao;
    private javax.swing.JLabel lblFlorestaBaixo;
    private javax.swing.JLabel lblFlorestaCima;
    private javax.swing.JLabel lblInimigo1;
    private javax.swing.JLabel lblInimigo2;
    private javax.swing.JLabel lblInimigo3;
    private javax.swing.JLabel lblInventario1;
    private javax.swing.JLabel lblInventario2;
    private javax.swing.JLabel lblInventario3;
    private javax.swing.JLabel lblNevoa;
    private javax.swing.JLabel lblNevoa1;
    private javax.swing.JLabel lblPerson;
    private javax.swing.JLabel lblQuadro;
    private javax.swing.JLabel lblTerreno;
    private javax.swing.JLabel lblTiros;
    private javax.swing.JLabel lblVida;
    private javax.swing.JPanel panControles;
    private javax.swing.JTextArea txtDialogo;
    // End of variables declaration//GEN-END:variables
}
