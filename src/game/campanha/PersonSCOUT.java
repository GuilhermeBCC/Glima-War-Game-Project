package game.campanha;

import javax.swing.ImageIcon;


/**
 * Classe filha da classe Personagem, representação do persoangem com a Arma SCOUT
 * @author Guilherme
 */
public class PersonSCOUT extends Personagem{    
    
    public PersonSCOUT(){
        this.setDefesaFull(25);
        this.setTempDeAtq(1500);
        this.setAlcance(80);
        this.setAtaque(80);
        this.setDefesa(25);
        this.setVelocidade(2);         

        //andar para esquerda
        andandoI[0] = new ImageIcon(getClass().getClassLoader().getResource("imagCAMP/snipaW1I.png"));
        andandoI[1] = new ImageIcon(getClass().getClassLoader().getResource("imagCAMP/snipaW2I.png"));
        andandoI[2] = new ImageIcon(getClass().getClassLoader().getResource("imagCAMP/snipaW3I.png"));
        andandoI[3] = new ImageIcon(getClass().getClassLoader().getResource("imagCAMP/snipaW4I.png"));
        paradoI = new ImageIcon(getClass().getClassLoader().getResource("imagCAMP/snipaPI.png"));
        //andar para direita
        andando[0] = new ImageIcon(getClass().getClassLoader().getResource("imagCAMP/snipaW1.png"));
        andando[1] = new ImageIcon(getClass().getClassLoader().getResource("imagCAMP/snipaW2.png"));
        andando[2] = new ImageIcon(getClass().getClassLoader().getResource("imagCAMP/snipaW3.png"));
        andando[3] = new ImageIcon(getClass().getClassLoader().getResource("imagCAMP/snipaW4.png"));
        parado = new ImageIcon(getClass().getClassLoader().getResource("imagCAMP/snipaP.png"));
        //andar para frente
        andandoF[0] = new ImageIcon(getClass().getClassLoader().getResource("imagCAMP/snipaF1.png"));
        andandoF[1] = new ImageIcon(getClass().getClassLoader().getResource("imagCAMP/snipaF2.png"));
        andandoF[2] = new ImageIcon(getClass().getClassLoader().getResource("imagCAMP/snipaF3.png"));
        andandoF[3] = new ImageIcon(getClass().getClassLoader().getResource("imagCAMP/snipaF4.png"));
        paradoF = new ImageIcon(getClass().getClassLoader().getResource("imagCAMP/snipaFP.png"));
        //andar para tras
        andandoC[0] = new ImageIcon(getClass().getClassLoader().getResource("imagCAMP/snipaC1.png"));
        andandoC[1] = new ImageIcon(getClass().getClassLoader().getResource("imagCAMP/snipaC2.png"));
        andandoC[2] = new ImageIcon(getClass().getClassLoader().getResource("imagCAMP/snipaC3.png"));
        andandoC[3] = new ImageIcon(getClass().getClassLoader().getResource("imagCAMP/snipaC4.png"));
        paradoC = new ImageIcon(getClass().getClassLoader().getResource("imagCAMP/snipaCP.png"));        
        
    }    
  
 
}
