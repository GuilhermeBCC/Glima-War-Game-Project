
package game.campanha;


import java.util.Random;
import javax.swing.ImageIcon;

/**
 * Classe pai de todos os personagens do jogo
 * @author Guilherme
 */
public class Personagem {
    
    public ImageIcon [] andando = new ImageIcon[4];
    public ImageIcon [] andandoI = new ImageIcon[4];
    public ImageIcon [] andandoF = new ImageIcon[4];
    public ImageIcon [] andandoC = new ImageIcon[4];    
    public ImageIcon parado,paradoI,paradoF,paradoC;
    protected Item [] inventario = new Item[3];
    protected int tempDeAtq,velocidade,alcance,ataque,defesa,defesaFull,vida=100,quantVida=3;
    protected boolean matar=false;
    protected Random rad = new Random(System.currentTimeMillis());
    
    /**
     * Método para atacar outros personagens
     * @param p Recebe uma variável do tipo Personagem, que representa o personagem atacado
     */
    public void atacar(Personagem p){
        int dano;
        if(matar){
            p.setDefesa(0);
            p.setVida(0);          
            matar = false;
        }else{
            dano = this.ataque + (rad.nextInt(ataque/5) - ataque/10);
            p.setDefesa(p.getDefesa()-(int)dano/6);
            if(p.getDefesa()>0){
                dano = dano-(int)dano/8;
            }
            p.setVida(p.getVida()-dano);            
        }            
    }  

    /**
     * Método para atualizar o máximo que o personagem pode ter de defesa
     * @param defesaFull Recebe o valor máximo atribuido ao personagem de uma classe filha, e atualiza a variável defesaFull
     */
    public void setDefesaFull(int defesaFull) {
        this.defesaFull = defesaFull;
    }

    /**
     * Retorna o máximo de defesa que o personagem pode ter
     * @return Retorna a variável defesaFull, que representa o máximo que o personagem pode ter de defesa
     */
    public int getDefesaFull() {
        return defesaFull;
    }    
    
    /**
     * Método para matar o próximo personagem a ser atacado pelo objeto
     */
    public void setMatar(){
        matar = true;
    }
    /**
     * Retorna a quantidade de vidas que o personagem tem
     * @return Retorna a variável quantVida, que guarda a quantidade de vidas do personagem
     */
    public int getQuantVida() {
        return quantVida;
    }

    /**
     * Método para decrementar a quantidade de vidas do personagem em uma vida, até o limite de 0 (zero)
     */
    public void menosQuantVida() {
        this.quantVida--;
    }

    /**
     * Retorna um item do inventario do personagem
     * @param x Posição do item a ser retornado
     * @return Item a ser retornado
     */
    public Item getInventario(int x) {
        return inventario[x];
    }

    /**
     * Atualiza o inventário, se ainda haver vaga
     * @param intem Item a ser introduzido no inventário
     * @return Retorna a posição onde foi inserido o item, caso o inventario cheio retorna -1
     */
    public int setInventario(Item intem) {
        for(int i=0;i<3;i++){
            if(inventario[i]==null){
                this.inventario[i] = intem;
                return i;
            }
        }    
        return -1;
    }
    
    /**
     * Ativa o item no inventario, e depois o deleta
     * @param x Posição do inventário em que o item irá ser ativado 
     */
    public void ativarItem(int x){
        inventario[x].ativar(this);
        inventario[x] = null;
    }
    
    /**
     * Método para atualizar a defesa com o máximo de defesa do personagem
     */
    public void restaurarDefesa(){
            this.defesa = this.defesaFull;
    }
    
    /**
     * Método para atualizar a vida com o máximo de vida
     */
    public void completarVida(){
            this.vida = 100;
    }
        
    /**
     * Retorna oue o personagem deve esperar para atacar novamente
     * @return Retonra a variavél tempDeAtq, que contém o tempo de esperar entre ataques
     */
    public int getTempDeAtq() {
        return tempDeAtq;
    }

    /**
     * Atualiza o tempo de espera entre ataques do personagem
     * @param tempDeAtq Variável que atualizará o tempo de ataque do objeto personagem
     */
    public void setTempDeAtq(int tempDeAtq) {
        this.tempDeAtq = tempDeAtq;
    }
            
    /**
     * Retorna o alcance de tiro do personagem
     * @return Retorna a variável alcance, que é o alcance de tiro do objeto personagem
     */
    public int getAlcance() {
        return alcance;
    }

    /**
     * Atualiza o alcance de tiro do personagem
     * @param alcance Variável que atualizará o alcance do objeto personagem
     */
    public void setAlcance(int alcance) {
        this.alcance = alcance;
    }

    /**
     * Retorna o ataque do persoangem
     * @return Retorna a variável ataque do objeto personagem, que é o ataque do personagem
     */
    public int getAtaque() {
        return ataque;
    }

    /**
     * Atualiza o ataque do personagem
     * @param ataque Variavel que atualizará o ataque do objeto personagem
     */
    public void setAtaque(int ataque) {
        this.ataque = ataque;
    }

    /**
     * Retorna a defesa do personagem
     * @return Retorna a variável defesa, que é a defesa do objeto personagem
     */
    public int getDefesa() {
        return defesa;
    }

    /**
     * Atualiza a defesa do personagem
     * @param defesa Atualiza a variável defesa, que é a defesa do personagem
     */
    public void setDefesa(int defesa) {
        this.defesa = defesa;
    }

    /**
     * Retorna a velocidade do persongem
     * @return Retorna a variável velocidade, que é velocidade que o personagem anda
     */
    public int getVelocidade() {
        return velocidade;
    }

    /**
     * Atualiza a velocidade do personagem
     * @param velocidade Atualiza a variável velocidade, que é a velocidade do objeto personagem
     */
    public void setVelocidade(int velocidade) {
        this.velocidade = velocidade;
    }

    /**
     * Retorna a veida do persoangem
     * @return Retorna a variável vida, que é a vida do objeto personagem
     */
    public int getVida() {
        return vida;
    }

    /**
     * Atualiza a vida do personagem     
     * @param vida Atualiza a variável vida, que é a vida que o objeto personagem tem
     */
    public void setVida(int vida) {
        this.vida = vida;
    }           
    
}

