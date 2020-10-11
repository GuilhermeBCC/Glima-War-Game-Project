package game.campanha;

import ui.Campanha;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.ImageIcon;
import util.MP3;

/**
 * Classe que implementa todos os códigos e arquivos necessários para se jogar a
 * fase 1 do modo Campanha
 *
 * @author Guilherme
 */
public class Fase1 implements Fase {

    private PersonInimigo inimigo1 = new PersonInimigo();
    private PersonInimigo inimigo2 = new PersonInimigo();
    private PersonInimigo inimigo3 = new PersonInimigo();
    private Campanha jogo;
    
    private int wt = 680, ht = 420;
    private MP3 somHeli = new MP3("heli");
    private MP3 somIni = new MP3("atqak47");
    private ImageIcon imgIni1M = new ImageIcon(getClass().getClassLoader().getResource("imagCAMP/inimigo1M.png"));

    /**
     * Método construtor, posiciona os inimigos no campo
     *
     * @param camp Recebe o endereço da classe campanha que executa o jogo
     */
    public Fase1(Campanha camp) {
        jogo = camp;
        inimigo1.setPosX(200);
        inimigo1.setPosY(45);
        jogo.getlblInimigo1().setLocation(160, 20);
        inimigo2.setPosX(460);
        inimigo2.setPosY(225);
        jogo.getlblInimigo2().setLocation(420, 200);
        inimigo3.setPosX(460);
        inimigo3.setPosY(275);
        jogo.getlblInimigo3().setLocation(420, 260);
    }

    public PersonInimigo getInimigo1() {
        return inimigo1;
    }

    public PersonInimigo getInimigo2() {
        return inimigo2;
    }

    public PersonInimigo getInimigo3() {
        return inimigo3;
    }

    public void setInimigo1(PersonInimigo inimigo1) {
        this.inimigo1 = inimigo1;
    }

    public void setInimigo2(PersonInimigo inimigo2) {
        this.inimigo2 = inimigo2;
    }

    public void setInimigo3(PersonInimigo inimigo3) {
        this.inimigo3 = inimigo3;
    }

    /**
     * Método que controla todos os oponentes da fase1
     */
    public void inimigos() {
        new Thread() {
            public void run() {
                int r = inimigo1.getAlcance() + 30;
                while (10 > 9) {
                    if (110 < jogo.getXis() && jogo.getXis() < 280) {
                        if (inimigo1.getVida() > 0) {
                            for (int w = 0; w < wt; w++) {
                                for (int h = 0; h < ht; h++) {
                                    if (((w - inimigo1.getPosX()) * (w - inimigo1.getPosX()) + (h - inimigo1.getPosY()) * (h - inimigo1.getPosY())) < r * r) {
                                        if (jogo.getXis() == w && jogo.getYpi() == h) {
                                            somIni.play();
                                            inimigo1.atacar(jogo.getPerson());
                                            jogo.animaTiro(inimigo1.getPosX(), inimigo1.getPosY(), jogo.getXis(), jogo.getXis());
                                            jogo.getlblVida().setText(jogo.getPerson().getVida() + "");
                                            jogo.getlblDefesa().setText(jogo.getPerson().getDefesa() + "");
                                        }
                                    }
                                }
                            }
                        } else {
                            jogo.getlblInimigo1().setIcon(imgIni1M);
                        }
                    } else if (310 <= jogo.getXis() && jogo.getXis() < 510) {
                        for (int w = 0; w < wt; w++) {
                            for (int h = 0; h < ht; h++) {
                                if (inimigo3.getVida() > 0) {
                                    if (((w - inimigo3.getPosX()) * (w - inimigo3.getPosX()) + (h - inimigo3.getPosY()) * (h - inimigo3.getPosY())) < r * r) {
                                        if (jogo.getXis() == w && jogo.getYpi() == h) {
                                            somIni.play();
                                            inimigo3.atacar(jogo.getPerson());
                                            jogo.animaTiro(inimigo3.getPosX(), inimigo3.getPosY(), jogo.getXis(), jogo.getYpi());
                                            jogo.getlblVida().setText(jogo.getPerson().getVida() + "");
                                            jogo.getlblDefesa().setText(jogo.getPerson().getDefesa() + "");
                                            break;
                                        }
                                    }
                                } else {
                                    jogo.getlblInimigo3().setIcon(imgIni1M);
                                }
                                if (inimigo2.getVida() > 0) {
                                    if (((w - inimigo2.getPosX()) * (w - inimigo2.getPosX()) + (h - inimigo2.getPosY()) * (h - inimigo2.getPosY())) < r * r) {
                                        if (jogo.getXis() == w && jogo.getYpi() == h) {
                                            inimigo2.atacar(jogo.getPerson());
                                            jogo.animaTiro(inimigo2.getPosX(), inimigo2.getPosY(), jogo.getXis(), jogo.getYpi());
                                            jogo.getlblVida().setText(jogo.getPerson().getVida() + "");
                                            jogo.getlblDefesa().setText(jogo.getPerson().getDefesa() + "");
                                            break;
                                        }
                                    }
                                } else {
                                    jogo.getlblInimigo2().setIcon(imgIni1M);
                                }
                            }
                        }
                    }
                    try {
                        Thread.sleep(2000);
                    } catch (InterruptedException ex) {
                        Logger.getLogger(Campanha.class.getName()).log(Level.SEVERE, null, ex);
                    }
                }
            }
        }.start();
    }

    /**
     * Método que executa a animação quando o jogador conclui a fase1
     */
    public void fimFase() {
        jogo.getheliRED().setVisible(true);
        somHeli.play();
        jogo.clarear();
        for (int w = 90; w > 60; w--) {
            jogo.getlblPerson().setLocation(jogo.getXis(), w);
            try {
                Thread.sleep(50);
            } catch (InterruptedException ex) {
                Logger.getLogger(Campanha.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        jogo.getlblPerson().setVisible(false);
        for (int w = 50; w > 0; w--) {
            jogo.getheliRED().setLocation(530, w);
            try {
                Thread.sleep(50);
            } catch (InterruptedException ex) {
                Logger.getLogger(Campanha.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        for (int w = 530; w < 680; w++) {
            jogo.getheliRED().setLocation(w, 0);
            try {
                Thread.sleep(40);
            } catch (InterruptedException ex) {
                Logger.getLogger(Campanha.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }

}
