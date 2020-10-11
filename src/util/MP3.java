package util;

import java.io.BufferedInputStream;
import java.util.logging.Level;
import java.util.logging.Logger;
import javazoom.jl.decoder.JavaLayerException;
import javazoom.jl.player.Player;

/**
 * Classe usada para tocar mídias no formato .MP3
 *
 * @author Guilherme
 */
public class MP3 {

    private boolean isPlaying;
    private Player tocador;
    private String arquivo;
    private BufferedInputStream bis;

    /**
     * Construtor da classe
     *
     * @param file recebe o endereço do arquivo .mp3
     */
    public MP3(String file) {
        this.arquivo = "sons/" + file + ".mp3";
    }

    /**
     * Tenta parar a execução da mídia
     */
    public void close() {
        try {
            tocador.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Tenta executar um arquivo .mp3 dentro do projeto
     */
    public void play() {
        if(this.isPlaying) return;
        
        this.isPlaying=true;
        new Thread() {
            public void run() {
                try {
                    bis = new BufferedInputStream(getClass().getClassLoader().getResourceAsStream(arquivo));
                    tocador = new Player(bis);
                    tocador.play();
                } catch (JavaLayerException ex) {
                    Logger.getLogger(MP3.class.getName()).log(Level.SEVERE, null, ex);
                }
                close();
                isPlaying=false;
            }
        }.start();
    }

}
