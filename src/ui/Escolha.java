package ui;

import java.awt.Component;
import util.MP3;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.*;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.ImageIcon;
import javax.swing.JOptionPane;

/**
 * Classe principal do programa, permite que o usuário escolha o modo de jogo
 *
 * @author Guilherme
 */
public class Escolha extends javax.swing.JFrame {

    private ImageIcon iconSoldado = new ImageIcon(getClass().getClassLoader().getResource("imagINI/sol.gif"));
    private ImageIcon iconGWG = new ImageIcon(getClass().getClassLoader().getResource("imagVERS/icon.png"));
    private MP3 somFundo = new MP3("somfundoini");
    private List servers = new ArrayList();
    private List<DatagramPacket> pacotes = new ArrayList();
    private int numJogos;
    private String modo;
    private boolean esperar = true;
    private Socket socket;
    private ServerSocket server_socket;

    /**
     * Construtor da classe, inicializa todos os componentes da classe Escolha
     */
    public Escolha() {
        initComponents();
        somFundo.play();
        this.setIconImage(iconGWG.getImage());
        lblSoldadoGif.setIcon(iconSoldado);
        //Em ordem de amostragem
        visivelEmLayPrincipal(lblFundoEscolha);
        visivelEmLayPrincipal(lblSoldadoGif);
        visivelEmLayPrincipal(btnVersus);
        visivelEmLayPrincipal(btnCampanha);
        visivelEmLayPrincipal(lblFechar);
        visivelEmLayPrincipal(lblGlima);
        //
        invisivelEmLayPrincipal(btnCriar);
        invisivelEmLayPrincipal(btnJogador);
        invisivelEmLayPrincipal(btnJogadores);
        invisivelEmLayPrincipal(btnMelhorDe1);
        invisivelEmLayPrincipal(btnMelhorDe3);
        invisivelEmLayPrincipal(btnMelhorDe5);
        invisivelEmLayPrincipal(btnPerson1);
        invisivelEmLayPrincipal(btnPerson2);
        invisivelEmLayPrincipal(btnPerson3);
        invisivelEmLayPrincipal(btnProcurar);
        invisivelEmLayPrincipal(btnJogarNormal);
        invisivelEmLayPrincipal(btnJogarCliente);
        invisivelEmLayPrincipal(lblEsperar);
        invisivelEmLayPrincipal(lblTop);
        invisivelEmLayPrincipal(listServidores);
        invisivelEmLayPrincipal(txtNickJogador);
        //
        iniciar();
    }

    private void iniciar() {
        new Thread() {
            public void run() {
                btnVersus.setSize(150, 0);
                btnCampanha.setSize(150, 0);
                visivelEmLayPrincipal(btnVersus);
                visivelEmLayPrincipal(btnCampanha);
                for (int x = 1; x < 40; x++) {
                    btnVersus.setSize(150, x);
                    btnCampanha.setSize(150, x);
                    try {
                        Thread.sleep(20);
                    } catch (InterruptedException ex) {
                        Logger.getLogger(Escolha.class.getName()).log(Level.SEVERE, null, ex);
                    }
                }
            }
        }.start();
    }

    private void numeroDeJogos() {
        invisivelEmLayPrincipal(btnJogarCliente);
        invisivelEmLayPrincipal(btnJogarNormal);
        invisivelEmLayPrincipal(btnProcurar);        
        invisivelEmLayPrincipal(btnCriar);                
        invisivelEmLayPrincipal(scrServidores);        
        invisivelEmLayPrincipal(txtNickJogador);        
        lblTop.setText("Melhor de:");
        visivelEmLayPrincipal(btnMelhorDe1);
        visivelEmLayPrincipal(btnMelhorDe3);
        visivelEmLayPrincipal(btnMelhorDe5);
        layPrincipal.revalidate();
    }

    private void selecionouNumDeJogos() {
        invisivelEmLayPrincipal(btnMelhorDe1);
        invisivelEmLayPrincipal(btnMelhorDe3);
        invisivelEmLayPrincipal(btnMelhorDe5);
        somFundo.close();
        if (modo.equals("ai")) {
            setVisible(false);
            new Versus(modo, numJogos, null, null, null);
        } else if (modo.equals("servidor")) {
            invisivelEmLayPrincipal(lblTop);
            visivelEmLayPrincipal(lblEsperar);
            repaint();
            iniciarServidorUDP();
            iniciarServidorTCP();
        }
    }

    // cliente multicast, quando ativado procura os servidores que estão mandando pacotes UDP
    private void atualizarListaServidoresDoCliente() {
        new Thread() {

            public void run() {
                btnProcurar.setEnabled(false);
                MulticastSocket socket = null;
                InetAddress grupo = null;
                DatagramPacket pacote;
                pacotes.clear();
                servers.clear();
                //desnecessario
                try {
                    InetAddress host = InetAddress.getLocalHost();
                    System.out.println("Nome PC: " + host.getHostName() + " IP: " + host.getHostAddress());
                } catch (UnknownHostException ex) {
                    ex.printStackTrace();
                }
                //      
                try {
                    grupo = InetAddress.getByName("239.100.100.100");
                    socket = new MulticastSocket(8282);//escutar a porta 8282
                    socket.joinGroup(grupo);
                    byte rec[] = new byte[256];
                    pacote = new DatagramPacket(rec, rec.length);
                    socket.receive(pacote);
                    String server = new String(pacote.getData(), pacote.getOffset(), pacote.getLength(), "ISO-8859-1");
                    servers.add(server);
                    pacotes.add(pacote);
                } catch (Exception e) {
                    System.out.println("Erro ao tentar receptar pacote do servidor: " + e.getMessage());
                    e.printStackTrace();
                } finally {
                    socket.close();
                }

                if (servers.isEmpty()) {
                    servers.add("sem servidores");
                }
                listServidores.setListData(servers.toArray());
                btnProcurar.setEnabled(true);
            }
        }.start();
    }

    // servidor multicast, envia pacotes para rede multicast com o endereço do servidor
    public void iniciarServidorUDP() {
        new Thread() {

            public void run() {
                try {
                    byte[] nomeHost = txtNickJogador.getText().getBytes("ISO-8859-1");
                    DatagramSocket socket = new DatagramSocket(8882); // enviando da porta 8882
                    try {
                        InetAddress grupo = InetAddress.getByName("239.100.100.100"); //endereço multicast
                        DatagramPacket pacote = new DatagramPacket(nomeHost, nomeHost.length, grupo, 8282);
                        while (esperar) { // envia pacotes ao endereço multicast até que alguem conecte                                                                
                            socket.send(pacote);
                            String pac = new String(pacote.getData(), pacote.getOffset(), pacote.getLength(), "ISO-8859-1");
                            System.out.println("Servidor enviando pacote: " + pac);
                            Thread.sleep(1500);
                        }
                    } catch (InterruptedException e) {
                        Logger.getLogger(Escolha.class.getName()).log(Level.SEVERE, null, e);
                        e.printStackTrace();
                    } finally {
                        System.out.println("O servidor parou de enviar pacotes");
                        socket.close();
                    }
                } catch (IOException ex) {
                    ex.printStackTrace();
                }
            }
        }.start();
    }

    // servidor TCP espera algum cliente se conectar, pelo endereço enviado pelo UDP    
    public void iniciarServidorTCP() {
        new Thread() {
            public void run() {
                DataOutputStream ostream = null;
                DataInputStream istream = null;
                try {
                    server_socket = new ServerSocket(8880);
                    socket = server_socket.accept(); // espera conexão..
                    esperar = false;
                    ostream = new DataOutputStream(socket.getOutputStream());
                    istream = new DataInputStream(socket.getInputStream());
                    somFundo.close();
                    setVisible(false);
                    new Versus(modo, numJogos, ostream, istream, txtNickJogador.getText());
                } catch (Exception e) {
                    System.err.println("Erro! Fechando conexão servidor");
                    if (socket != null) {
                        try {
                            ostream.close();
                            istream.close();
                            socket.close();
                        } catch (IOException ex) {
                            Logger.getLogger(Escolha.class.getName()).log(Level.SEVERE, null, ex);
                        }
                    }
                }
            }
        }.start();
    }

    // cliente TCP recebe o endereço do servidor para se conectar
    public void iniciarClienteTCP(InetAddress host) {
        DataOutputStream ostream = null;
        DataInputStream istream = null;
        try {
            socket = new Socket(host, 8880); // se conecta ao host
            ostream = new DataOutputStream(socket.getOutputStream());
            istream = new DataInputStream(socket.getInputStream());
            somFundo.close();
            setVisible(false);
            new Versus(modo, numJogos, ostream, istream, txtNickJogador.getText());
        } catch (Exception e) {
            e.printStackTrace();
            System.err.println("Erro! Fechando conexão servidor");
            JOptionPane.showMessageDialog(this, "Impossivel conectar", "Host inexistente!", 2);
            if (socket != null) {
                try {
                    ostream.close();
                    istream.close();
                    socket.close();
                } catch (IOException ex) {
                    Logger.getLogger(Escolha.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        }
    }

    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        layPrincipal = new javax.swing.JLayeredPane();
        lblFechar = new javax.swing.JLabel();
        lblEsperar = new javax.swing.JLabel();
        btnMelhorDe1 = new javax.swing.JButton();
        btnMelhorDe3 = new javax.swing.JButton();
        btnMelhorDe5 = new javax.swing.JButton();
        lblTop = new javax.swing.JLabel();
        scrServidores = new javax.swing.JScrollPane();
        listServidores = new javax.swing.JList();
        btnProcurar = new javax.swing.JButton();
        txtNickJogador = new javax.swing.JTextField();
        btnJogarNormal = new javax.swing.JButton();
        btnJogarCliente = new javax.swing.JButton();
        btnCriar = new javax.swing.JButton();
        btnVersus = new javax.swing.JButton();
        btnCampanha = new javax.swing.JButton();
        btnJogador = new javax.swing.JButton();
        btnJogadores = new javax.swing.JButton();
        lblGlima = new javax.swing.JLabel();
        lblSoldadoGif = new javax.swing.JLabel();
        lblFundoEscolha = new javax.swing.JLabel();
        btnPerson1 = new javax.swing.JButton();
        btnPerson2 = new javax.swing.JButton();
        btnPerson3 = new javax.swing.JButton();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        setTitle("Glima War Game");
        setBounds(new java.awt.Rectangle(425, 225, 440, 200));
        setUndecorated(true);
        setPreferredSize(new java.awt.Dimension(440, 200));
        setResizable(false);

        layPrincipal.setDoubleBuffered(true);
        layPrincipal.setPreferredSize(new java.awt.Dimension(440, 200));
        layPrincipal.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        lblFechar.setFont(new java.awt.Font("Tahoma", 1, 16)); // NOI18N
        lblFechar.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        lblFechar.setText("X");
        lblFechar.setAlignmentY(0.0F);
        lblFechar.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        lblFechar.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                lblFecharMouseClicked(evt);
            }
        });
        layPrincipal.add(lblFechar, new org.netbeans.lib.awtextra.AbsoluteConstraints(415, 5, 20, -1));

        lblEsperar.setBackground(new java.awt.Color(204, 204, 255));
        lblEsperar.setFont(new java.awt.Font("MingLiU", 1, 18)); // NOI18N
        lblEsperar.setForeground(new java.awt.Color(0, 102, 102));
        lblEsperar.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        lblEsperar.setText("ESPERANDO ADVERSÁRIO SE CONECTAR");
        lblEsperar.setBorder(new javax.swing.border.LineBorder(new java.awt.Color(0, 0, 0), 1, true));
        lblEsperar.setOpaque(true);
        layPrincipal.add(lblEsperar, new org.netbeans.lib.awtextra.AbsoluteConstraints(20, 80, 400, 50));

        btnMelhorDe1.setFont(new java.awt.Font("Arial", 1, 36)); // NOI18N
        btnMelhorDe1.setForeground(new java.awt.Color(135, 245, 145));
        btnMelhorDe1.setIcon(new javax.swing.ImageIcon(getClass().getResource("/imagINI/btn.png"))); // NOI18N
        btnMelhorDe1.setText("1");
        btnMelhorDe1.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(102, 102, 102)));
        btnMelhorDe1.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        btnMelhorDe1.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        btnMelhorDe1.setMargin(new java.awt.Insets(1, 1, 1, 1));
        btnMelhorDe1.setPreferredSize(new java.awt.Dimension(50, 50));
        btnMelhorDe1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnMelhorDe1ActionPerformed(evt);
            }
        });
        layPrincipal.add(btnMelhorDe1, new org.netbeans.lib.awtextra.AbsoluteConstraints(80, 100, 50, 50));

        btnMelhorDe3.setFont(new java.awt.Font("Arial", 1, 36)); // NOI18N
        btnMelhorDe3.setForeground(new java.awt.Color(135, 245, 145));
        btnMelhorDe3.setIcon(new javax.swing.ImageIcon(getClass().getResource("/imagINI/btn.png"))); // NOI18N
        btnMelhorDe3.setText("3");
        btnMelhorDe3.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(102, 102, 102)));
        btnMelhorDe3.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        btnMelhorDe3.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        btnMelhorDe3.setMargin(new java.awt.Insets(1, 1, 1, 1));
        btnMelhorDe3.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnMelhorDe3ActionPerformed(evt);
            }
        });
        layPrincipal.add(btnMelhorDe3, new org.netbeans.lib.awtextra.AbsoluteConstraints(200, 100, 50, 50));

        btnMelhorDe5.setFont(new java.awt.Font("Arial", 1, 36)); // NOI18N
        btnMelhorDe5.setForeground(new java.awt.Color(135, 245, 145));
        btnMelhorDe5.setIcon(new javax.swing.ImageIcon(getClass().getResource("/imagINI/btn.png"))); // NOI18N
        btnMelhorDe5.setText("5");
        btnMelhorDe5.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(102, 102, 102)));
        btnMelhorDe5.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        btnMelhorDe5.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        btnMelhorDe5.setMargin(new java.awt.Insets(1, 1, 1, 1));
        btnMelhorDe5.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnMelhorDe5ActionPerformed(evt);
            }
        });
        layPrincipal.add(btnMelhorDe5, new org.netbeans.lib.awtextra.AbsoluteConstraints(320, 100, 50, 50));

        lblTop.setBackground(new java.awt.Color(204, 204, 204));
        lblTop.setFont(new java.awt.Font("Courier New", 1, 14)); // NOI18N
        lblTop.setText("Selecione um jogo ou crie um:");
        lblTop.setAlignmentY(0.0F);
        lblTop.setHorizontalTextPosition(javax.swing.SwingConstants.LEFT);
        lblTop.setIconTextGap(1);
        lblTop.setOpaque(true);
        layPrincipal.add(lblTop, new org.netbeans.lib.awtextra.AbsoluteConstraints(20, 20, 240, 15));

        scrServidores.setBackground(new java.awt.Color(204, 204, 255));
        scrServidores.setBorder(javax.swing.BorderFactory.createEmptyBorder(1, 1, 1, 1));

        listServidores.setBackground(new java.awt.Color(204, 204, 255));
        listServidores.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0), 2));
        listServidores.setFont(new java.awt.Font("Magneto", 1, 12)); // NOI18N
        listServidores.setSelectionMode(javax.swing.ListSelectionModel.SINGLE_SELECTION);
        listServidores.setValueIsAdjusting(true);
        listServidores.setVisibleRowCount(6);
        scrServidores.setViewportView(listServidores);

        layPrincipal.add(scrServidores, new org.netbeans.lib.awtextra.AbsoluteConstraints(160, 80, 260, 112));

        btnProcurar.setIcon(new javax.swing.ImageIcon(getClass().getResource("/imagINI/procurar.png"))); // NOI18N
        btnProcurar.setToolTipText("Atualiza lista de jogos");
        btnProcurar.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(102, 102, 102)));
        btnProcurar.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        btnProcurar.setDisabledIcon(new javax.swing.ImageIcon(getClass().getResource("/imagINI/procurar.png"))); // NOI18N
        btnProcurar.setHideActionText(true);
        btnProcurar.setRolloverIcon(new javax.swing.ImageIcon(getClass().getResource("/imagINI/procurarRoll.png"))); // NOI18N
        btnProcurar.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnProcurarActionPerformed(evt);
            }
        });
        layPrincipal.add(btnProcurar, new org.netbeans.lib.awtextra.AbsoluteConstraints(370, 30, 46, 42));

        txtNickJogador.setFont(new java.awt.Font("Courier New", 3, 14)); // NOI18N
        txtNickJogador.setHorizontalAlignment(javax.swing.JTextField.LEFT);
        txtNickJogador.setText("Jogador");
        txtNickJogador.setMargin(new java.awt.Insets(0, 5, 0, 0));
        layPrincipal.add(txtNickJogador, new org.netbeans.lib.awtextra.AbsoluteConstraints(160, 45, 150, 26));

        btnJogarNormal.setIcon(new javax.swing.ImageIcon(getClass().getResource("/imagINI/lado.png"))); // NOI18N
        btnJogarNormal.setToolTipText("Conectar diretamente um host");
        btnJogarNormal.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(102, 102, 102)));
        btnJogarNormal.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        btnJogarNormal.setDisabledIcon(new javax.swing.ImageIcon(getClass().getResource("/imagINI/lado.png"))); // NOI18N
        btnJogarNormal.setHideActionText(true);
        btnJogarNormal.setRolloverIcon(new javax.swing.ImageIcon(getClass().getResource("/imagINI/ladoRoll.png"))); // NOI18N
        btnJogarNormal.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnJogarNormalActionPerformed(evt);
            }
        });
        layPrincipal.add(btnJogarNormal, new org.netbeans.lib.awtextra.AbsoluteConstraints(320, 30, 46, 42));

        btnJogarCliente.setIcon(new javax.swing.ImageIcon(getClass().getResource("/imagINI/jogar.png"))); // NOI18N
        btnJogarCliente.setToolTipText("Jogar no servidor (jogo) selecionado na lista");
        btnJogarCliente.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(102, 102, 102)));
        btnJogarCliente.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        btnJogarCliente.setDisabledIcon(new javax.swing.ImageIcon(getClass().getResource("/imagINI/jogar.png"))); // NOI18N
        btnJogarCliente.setHideActionText(true);
        btnJogarCliente.setRolloverIcon(new javax.swing.ImageIcon(getClass().getResource("/imagINI/jogarRoll.png"))); // NOI18N
        btnJogarCliente.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnJogarClienteActionPerformed(evt);
            }
        });
        layPrincipal.add(btnJogarCliente, new org.netbeans.lib.awtextra.AbsoluteConstraints(20, 80, 130, 40));

        btnCriar.setIcon(new javax.swing.ImageIcon(getClass().getResource("/imagINI/criar.png"))); // NOI18N
        btnCriar.setToolTipText("Criar um servidor (jogo) na rede");
        btnCriar.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(102, 102, 102)));
        btnCriar.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        btnCriar.setDisabledIcon(new javax.swing.ImageIcon(getClass().getResource("/imagINI/criar.png"))); // NOI18N
        btnCriar.setHideActionText(true);
        btnCriar.setRolloverIcon(new javax.swing.ImageIcon(getClass().getResource("/imagINI/criarRoll.png"))); // NOI18N
        btnCriar.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnCriarActionPerformed(evt);
            }
        });
        layPrincipal.add(btnCriar, new org.netbeans.lib.awtextra.AbsoluteConstraints(20, 120, 130, 40));

        btnVersus.setIcon(new javax.swing.ImageIcon(getClass().getResource("/imagINI/versus.gif"))); // NOI18N
        btnVersus.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(102, 102, 102)));
        btnVersus.setDisabledIcon(new javax.swing.ImageIcon(getClass().getResource("/imagINI/versus.gif"))); // NOI18N
        btnVersus.setHideActionText(true);
        btnVersus.setIconTextGap(2);
        btnVersus.setMargin(new java.awt.Insets(0, 14, 2, 14));
        btnVersus.setPreferredSize(new java.awt.Dimension(150, 40));
        btnVersus.setRolloverIcon(new javax.swing.ImageIcon(getClass().getResource("/imagINI/versusRoll.gif"))); // NOI18N
        btnVersus.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnVersusActionPerformed(evt);
            }
        });
        layPrincipal.add(btnVersus, new org.netbeans.lib.awtextra.AbsoluteConstraints(140, 100, 150, 40));

        btnCampanha.setIcon(new javax.swing.ImageIcon(getClass().getResource("/imagINI/campanha.gif"))); // NOI18N
        btnCampanha.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(102, 102, 102)));
        btnCampanha.setDisabledIcon(new javax.swing.ImageIcon(getClass().getResource("/imagINI/campanha.gif"))); // NOI18N
        btnCampanha.setHideActionText(true);
        btnCampanha.setIconTextGap(2);
        btnCampanha.setMargin(new java.awt.Insets(0, 14, 2, 14));
        btnCampanha.setPreferredSize(new java.awt.Dimension(150, 40));
        btnCampanha.setRolloverIcon(new javax.swing.ImageIcon(getClass().getResource("/imagINI/campanhaRoll.gif"))); // NOI18N
        btnCampanha.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnCampanhaActionPerformed(evt);
            }
        });
        layPrincipal.add(btnCampanha, new org.netbeans.lib.awtextra.AbsoluteConstraints(140, 142, 150, 40));

        btnJogador.setIcon(new javax.swing.ImageIcon(getClass().getResource("/imagINI/jogador.png"))); // NOI18N
        btnJogador.setToolTipText("Você contra o computador");
        btnJogador.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(102, 102, 102)));
        btnJogador.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        btnJogador.setDisabledIcon(new javax.swing.ImageIcon(getClass().getResource("/imagINI/jogador.png"))); // NOI18N
        btnJogador.setHideActionText(true);
        btnJogador.setRolloverIcon(new javax.swing.ImageIcon(getClass().getResource("/imagINI/jogadorRoll.png"))); // NOI18N
        btnJogador.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnJogadorActionPerformed(evt);
            }
        });
        layPrincipal.add(btnJogador, new org.netbeans.lib.awtextra.AbsoluteConstraints(140, 100, 150, 40));

        btnJogadores.setBackground(new java.awt.Color(255, 255, 255));
        btnJogadores.setIcon(new javax.swing.ImageIcon(getClass().getResource("/imagINI/jogadores.png"))); // NOI18N
        btnJogadores.setToolTipText("Você contra um amigo");
        btnJogadores.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(102, 102, 102)));
        btnJogadores.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        btnJogadores.setDisabledIcon(new javax.swing.ImageIcon(getClass().getResource("/imagINI/jogadores.png"))); // NOI18N
        btnJogadores.setHideActionText(true);
        btnJogadores.setRolloverIcon(new javax.swing.ImageIcon(getClass().getResource("/imagINI/jogadoresRoll.png"))); // NOI18N
        btnJogadores.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnJogadoresActionPerformed(evt);
            }
        });
        layPrincipal.add(btnJogadores, new org.netbeans.lib.awtextra.AbsoluteConstraints(140, 142, 150, 40));

        lblGlima.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        lblGlima.setIcon(new javax.swing.ImageIcon(getClass().getResource("/imagINI/Glima.png"))); // NOI18N
        layPrincipal.add(lblGlima, new org.netbeans.lib.awtextra.AbsoluteConstraints(140, 0, 150, 100));

        lblSoldadoGif.setBackground(new java.awt.Color(255, 255, 255));
        lblSoldadoGif.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        lblSoldadoGif.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 51, 51), 4));
        lblSoldadoGif.setIconTextGap(2);
        layPrincipal.add(lblSoldadoGif, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 0, 440, 200));

        lblFundoEscolha.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        lblFundoEscolha.setIcon(new javax.swing.ImageIcon(getClass().getResource("/imagINI/fundoEsc.jpg"))); // NOI18N
        lblFundoEscolha.setOpaque(true);
        lblFundoEscolha.setPreferredSize(new java.awt.Dimension(440, 205));
        layPrincipal.add(lblFundoEscolha, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 0, -1, -1));

        btnPerson1.setFont(new java.awt.Font("Tempus Sans ITC", 2, 12)); // NOI18N
        btnPerson1.setForeground(new java.awt.Color(255, 255, 255));
        btnPerson1.setIcon(new javax.swing.ImageIcon(getClass().getResource("/imagINI/personGLOCK.png"))); // NOI18N
        btnPerson1.setText("BLOQUEADO");
        btnPerson1.setToolTipText("Arma: GLOCK Tiro semi-rápido, dano baixo, alcance curto");
        btnPerson1.setAlignmentY(0.0F);
        btnPerson1.setBorder(null);
        btnPerson1.setBorderPainted(false);
        btnPerson1.setContentAreaFilled(false);
        btnPerson1.setCursor(new java.awt.Cursor(java.awt.Cursor.DEFAULT_CURSOR));
        btnPerson1.setFocusPainted(false);
        btnPerson1.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        btnPerson1.setMargin(new java.awt.Insets(2, 2, 2, 2));
        btnPerson1.setRolloverIcon(new javax.swing.ImageIcon(getClass().getResource("/imagINI/SELpersonGLOCK.png"))); // NOI18N
        btnPerson1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnPerson1ActionPerformed(evt);
            }
        });
        layPrincipal.add(btnPerson1, new org.netbeans.lib.awtextra.AbsoluteConstraints(50, 25, -1, -1));

        btnPerson2.setFont(new java.awt.Font("Tempus Sans ITC", 2, 12)); // NOI18N
        btnPerson2.setForeground(new java.awt.Color(255, 255, 255));
        btnPerson2.setIcon(new javax.swing.ImageIcon(getClass().getResource("/imagINI/personCOLT.png"))); // NOI18N
        btnPerson2.setText("BLOQUEADO");
        btnPerson2.setToolTipText("Arma: COLT Tiro rápido, dano médio, alcance curto");
        btnPerson2.setAlignmentY(0.0F);
        btnPerson2.setBorder(null);
        btnPerson2.setBorderPainted(false);
        btnPerson2.setContentAreaFilled(false);
        btnPerson2.setCursor(new java.awt.Cursor(java.awt.Cursor.DEFAULT_CURSOR));
        btnPerson2.setFocusPainted(false);
        btnPerson2.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        btnPerson2.setMargin(new java.awt.Insets(2, 2, 2, 2));
        btnPerson2.setRolloverIcon(new javax.swing.ImageIcon(getClass().getResource("/imagINI/SELpersonCOLT.png"))); // NOI18N
        btnPerson2.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnPerson2ActionPerformed(evt);
            }
        });
        layPrincipal.add(btnPerson2, new org.netbeans.lib.awtextra.AbsoluteConstraints(170, 25, -1, -1));

        btnPerson3.setFont(new java.awt.Font("Tempus Sans ITC", 2, 12)); // NOI18N
        btnPerson3.setForeground(new java.awt.Color(255, 255, 255));
        btnPerson3.setIcon(new javax.swing.ImageIcon(getClass().getResource("/imagINI/personSCOUT.png"))); // NOI18N
        btnPerson3.setText("Arma: SCOUT");
        btnPerson3.setToolTipText("Tiro lento, dano alto, alcance longo");
        btnPerson3.setAlignmentY(0.0F);
        btnPerson3.setBorder(null);
        btnPerson3.setBorderPainted(false);
        btnPerson3.setContentAreaFilled(false);
        btnPerson3.setCursor(new java.awt.Cursor(java.awt.Cursor.DEFAULT_CURSOR));
        btnPerson3.setFocusPainted(false);
        btnPerson3.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        btnPerson3.setMargin(new java.awt.Insets(2, 2, 2, 2));
        btnPerson3.setRolloverIcon(new javax.swing.ImageIcon(getClass().getResource("/imagINI/SELpersonSCOUT.png"))); // NOI18N
        btnPerson3.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnPerson3ActionPerformed(evt);
            }
        });
        layPrincipal.add(btnPerson3, new org.netbeans.lib.awtextra.AbsoluteConstraints(290, 25, -1, -1));

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(layPrincipal, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(layPrincipal, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void btnJogadorActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnJogadorActionPerformed
        new Thread() {
            public void run() {
                for (int x = 40; x > 0; x--) {
                    btnJogador.setSize(150, x);
                    btnJogadores.setSize(150, x);
                    try {
                        Thread.sleep(20);
                    } catch (InterruptedException ex) {
                        Logger.getLogger(Escolha.class.getName()).log(Level.SEVERE, null, ex);
                    }
                }
                invisivelEmLayPrincipal(btnJogador);
                invisivelEmLayPrincipal(btnJogadores);
                //
                btnMelhorDe1.setSize(50, 0);
                btnMelhorDe3.setSize(50, 0);
                btnMelhorDe5.setSize(50, 0);
                visivelEmLayPrincipal(btnMelhorDe1);
                visivelEmLayPrincipal(btnMelhorDe3);
                visivelEmLayPrincipal(btnMelhorDe5);
                for (int x = 1; x < 40; x++) {
                    btnMelhorDe1.setSize(50, x);
                    btnMelhorDe3.setSize(50, x);
                    btnMelhorDe5.setSize(50, x);
                    try {
                        Thread.sleep(20);
                    } catch (InterruptedException ex) {
                        Logger.getLogger(Escolha.class.getName()).log(Level.SEVERE, null, ex);
                    }
                }
                visivelEmLayPrincipal(lblTop);
                lblTop.setText("Melhor de:");
                modo = "ai";
            }
        }.start();
    }//GEN-LAST:event_btnJogadorActionPerformed

    private void btnJogadoresActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnJogadoresActionPerformed
        new Thread() {
            public void run() {
                for (int x = 40; x >= 0; x--) {
                    btnJogador.setSize(150, x);
                    btnJogadores.setSize(150, x);
                    try {
                        Thread.sleep(20);
                    } catch (InterruptedException ex) {
                        Logger.getLogger(Escolha.class.getName()).log(Level.SEVERE, null, ex);
                    }
                }
                invisivelEmLayPrincipal(btnJogador);
                invisivelEmLayPrincipal(btnJogadores);
                // 
                btnJogarCliente.setSize(130, 0);
                btnCriar.setSize(130, 0);
                visivelEmLayPrincipal(btnJogarCliente);
                visivelEmLayPrincipal(btnCriar);
                for (int x = 1; x < 40; x++) {
                    btnJogarCliente.setSize(130, x);
                    btnCriar.setSize(130, x);
                    try {
                        Thread.sleep(20);
                    } catch (InterruptedException ex) {
                        Logger.getLogger(Escolha.class.getName()).log(Level.SEVERE, null, ex);
                    }
                }
                try {
                    Thread.sleep(500);
                } catch (InterruptedException ex) {
                    Logger.getLogger(Escolha.class.getName()).log(Level.SEVERE, null, ex);
                }
                visivelEmLayPrincipal(lblTop);
                visivelEmLayPrincipal(scrServidores);
                visivelEmLayPrincipal(btnProcurar);
                visivelEmLayPrincipal(btnJogarNormal);
                visivelEmLayPrincipal(txtNickJogador);
            }
        }.start();
    }//GEN-LAST:event_btnJogadoresActionPerformed

    private void lblFecharMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_lblFecharMouseClicked
        System.exit(0);
    }//GEN-LAST:event_lblFecharMouseClicked

    private void btnJogarClienteActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnJogarClienteActionPerformed
        if (listServidores.getSelectedValue() != null && !listServidores.getSelectedValue().equals("sem servidores")) {
            modo = "cliente";
            iniciarClienteTCP((InetAddress) pacotes.get(listServidores.getSelectedIndex()).getAddress());
        }
    }//GEN-LAST:event_btnJogarClienteActionPerformed

    private void btnCriarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnCriarActionPerformed
        modo = "servidor";
        numeroDeJogos();
    }//GEN-LAST:event_btnCriarActionPerformed

    private void btnProcurarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnProcurarActionPerformed
        atualizarListaServidoresDoCliente();
    }//GEN-LAST:event_btnProcurarActionPerformed

    private void btnJogarNormalActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnJogarNormalActionPerformed
        modo = "cliente";
        String ip = JOptionPane.showInputDialog(this, "Conectar ao host", "Digite o endereço IP do servidor:", 2).trim();
        if (!ip.equals("")) {
            try {
                iniciarClienteTCP(InetAddress.getByName(ip));
            } catch (UnknownHostException ex) {
                Logger.getLogger(Escolha.class.getName()).log(Level.SEVERE, null, ex);
                JOptionPane.showMessageDialog(this, "Impossivel conectar", "O endereço IP mal formatado ou inexistente!", 2);
            }
        }
    }//GEN-LAST:event_btnJogarNormalActionPerformed

    private void btnMelhorDe1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnMelhorDe1ActionPerformed
        numJogos = 1;
        selecionouNumDeJogos();
    }//GEN-LAST:event_btnMelhorDe1ActionPerformed

    private void btnMelhorDe3ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnMelhorDe3ActionPerformed
        numJogos = 3;
        selecionouNumDeJogos();
    }//GEN-LAST:event_btnMelhorDe3ActionPerformed

    private void btnMelhorDe5ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnMelhorDe5ActionPerformed
        numJogos = 5;
        selecionouNumDeJogos();
    }//GEN-LAST:event_btnMelhorDe5ActionPerformed

    private void btnPerson1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnPerson1ActionPerformed
        //this.setVisible(false);
        //new Campanha(1).setVisible(true);
    }//GEN-LAST:event_btnPerson1ActionPerformed

    private void btnPerson2ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnPerson2ActionPerformed
        //this.setVisible(false);
        //new Campanha(2).setVisible(true);
    }//GEN-LAST:event_btnPerson2ActionPerformed

    private void btnPerson3ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnPerson3ActionPerformed
        somFundo.close();
        this.setVisible(false);
        new Campanha(3).setVisible(true);
    }//GEN-LAST:event_btnPerson3ActionPerformed

    private void btnVersusActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnVersusActionPerformed
        iniciarVersus();
    }//GEN-LAST:event_btnVersusActionPerformed

    private void btnCampanhaActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnCampanhaActionPerformed
        iniciarCampanha();
    }//GEN-LAST:event_btnCampanhaActionPerformed

    private void iniciarVersus() {
        new Thread() {
            public void run() {
                for (int x = 40; x > 0; x--) {
                    btnVersus.setSize(150, x);
                    btnCampanha.setSize(150, x);
                    try {
                        Thread.sleep(20);
                    } catch (InterruptedException ex) {
                        Logger.getLogger(Escolha.class.getName()).log(Level.SEVERE, null, ex);
                    }
                }
                invisivelEmLayPrincipal(btnVersus);
                invisivelEmLayPrincipal(btnCampanha);
                //
                btnJogador.setSize(150, 0);
                btnJogadores.setSize(150, 0);
                visivelEmLayPrincipal(btnJogador);
                visivelEmLayPrincipal(btnJogadores);
                for (int x = 0; x < 40; x++) {
                    btnJogador.setSize(150, x);
                    btnJogadores.setSize(150, x);
                    try {
                        Thread.sleep(25);
                    } catch (InterruptedException ex) {
                        Logger.getLogger(Escolha.class.getName()).log(Level.SEVERE, null, ex);
                    }
                }
            }
        }.start();
    }

    private void iniciarCampanha() {
        new Thread() {
            public void run() {
                //animação
                for (int x = 40; x > 1; x--) {
                    btnVersus.setSize(150, x);
                    btnCampanha.setSize(150, x);
                    try {
                        Thread.sleep(20);
                    } catch (InterruptedException ex) {
                        Logger.getLogger(Escolha.class.getName()).log(Level.SEVERE, null, ex);
                    }
                }
                invisivelEmLayPrincipal(btnVersus);
                invisivelEmLayPrincipal(btnCampanha);
                //
                btnPerson1.setSize(100, 0);
                btnPerson2.setSize(100, 0);
                btnPerson3.setSize(100, 0);
                visivelEmLayPrincipal(btnPerson1);
                visivelEmLayPrincipal(btnPerson2);
                visivelEmLayPrincipal(btnPerson3);
                for (int x = 1; x < 180; x++) {
                    btnPerson1.setSize(100, x);
                    btnPerson2.setSize(100, x);
                    btnPerson3.setSize(100, x);
                    try {
                        Thread.sleep(8);
                    } catch (InterruptedException ex) {
                        Logger.getLogger(Escolha.class.getName()).log(Level.SEVERE, null, ex);
                    }
                }
                lblTop.setText("Selecione um personagem");
                visivelEmLayPrincipal(lblTop);
            }
        }.start();
    }

    private void visivelEmLayPrincipal(Component comp) {
        layPrincipal.moveToFront(comp);
        comp.setVisible(true);
    }

    private void invisivelEmLayPrincipal(Component comp) {
        layPrincipal.moveToBack(comp);
        comp.setVisible(false);
    }

    public static void main(String args[]) {
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                new Escolha().setVisible(true);
            }
        });
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btnCampanha;
    private javax.swing.JButton btnCriar;
    private javax.swing.JButton btnJogador;
    private javax.swing.JButton btnJogadores;
    private javax.swing.JButton btnJogarCliente;
    private javax.swing.JButton btnJogarNormal;
    private javax.swing.JButton btnMelhorDe1;
    private javax.swing.JButton btnMelhorDe3;
    private javax.swing.JButton btnMelhorDe5;
    private javax.swing.JButton btnPerson1;
    private javax.swing.JButton btnPerson2;
    private javax.swing.JButton btnPerson3;
    private javax.swing.JButton btnProcurar;
    private javax.swing.JButton btnVersus;
    private javax.swing.JLayeredPane layPrincipal;
    private javax.swing.JLabel lblEsperar;
    private javax.swing.JLabel lblFechar;
    private javax.swing.JLabel lblFundoEscolha;
    private javax.swing.JLabel lblGlima;
    private javax.swing.JLabel lblSoldadoGif;
    private javax.swing.JLabel lblTop;
    private javax.swing.JList listServidores;
    private javax.swing.JScrollPane scrServidores;
    private javax.swing.JTextField txtNickJogador;
    // End of variables declaration//GEN-END:variables
}
