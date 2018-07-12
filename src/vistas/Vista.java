package vistas;

import Objetos.Semaforo;
import java.util.ArrayList;
import javax.swing.JFrame;
import javax.swing.JLabel;
import reglas.ReglaSemaforo;

public class Vista extends javax.swing.JFrame {

    public static boolean NOCTURNO = false;
    public static boolean INICIO = false;
    private Thread hilo;
    private int delay = 1000;

    int posicion = 0;
    Semaforo sVia_1, sVia_2, sVia_3, sVia_4;
    ArrayList<Semaforo> semaforos = new ArrayList<>();

    public Vista() {
        initComponents();
        this.setExtendedState(JFrame.MAXIMIZED_BOTH);
        sVia_1 = new Semaforo();
        sVia_2 = new Semaforo();
        sVia_3 = new Semaforo();
        sVia_4 = new Semaforo();
        semaforos.add(sVia_1);
        semaforos.add(sVia_2);
        semaforos.add(sVia_3);
        semaforos.add(sVia_4);
    }

    public void iniciar() {
        lblEstado.setForeground(new java.awt.Color(0, 255, 0));
        lblEstado.setText("    Activado");
        hilo = new Thread() {
            @Override
            public void run() {
                while (INICIO) {
                    if (ReglaSemaforo.vehiculoEmergencia(semaforos)) {
                        if (sVia_1.getSensorSonido().isActivado()) {
                            preferenciaSemaforo(sVia_1, via1_imgSemaforo, via1_tiempo);
                            posicion = 0;
                        }
                        if (sVia_2.getSensorSonido().isActivado()) {
                            preferenciaSemaforo(sVia_2, via2_imgSemaforo, via2_tiempo);
                            posicion = 1;
                        }
                        if (sVia_3.getSensorSonido().isActivado()) {
                            preferenciaSemaforo(sVia_3, via3_imgSemaforo, via3_tiempo);
                            posicion = 2;
                        }
                        if (sVia_4.getSensorSonido().isActivado()) {
                            preferenciaSemaforo(sVia_4, via4_imgSemaforo, via4_tiempo);
                            posicion = 3;
                        }
                    } else {
                        if (asignarTiempo(semaforos.get(posicion)) != 0) {
                            switch (posicion) {
                                case 0: {
                                    gestionarTiempoSemaforo(sVia_1, via1_imgSemaforo, via1_tiempo);
                                    break;
                                }
                                case 1: {
                                    gestionarTiempoSemaforo(sVia_2, via2_imgSemaforo, via2_tiempo);
                                    break;
                                }
                                case 2: {
                                    gestionarTiempoSemaforo(sVia_3, via3_imgSemaforo, via3_tiempo);
                                    break;
                                }
                                case 3: {
                                    gestionarTiempoSemaforo(sVia_4, via4_imgSemaforo, via4_tiempo);
                                    break;
                                }
                            }
                        }
                        if (posicion == 3) {
                            posicion = 0;
                        } else {
                            posicion++;
                        }
                    }
                    System.out.println(" ");
                }
            }
        };
        if (hilo.isAlive()) {
            hilo.resume();
        } else {
            hilo.start();
        }
    }

    public void gestionarTiempoSemaforo(Semaforo semaforo, JLabel imgSemaforo, JLabel lblCrono) {
        imgSemaforo.setIcon(new javax.swing.ImageIcon(getClass().getResource("/imagenes/luzVerde.png")));
        lblCrono.setForeground(new java.awt.Color(0, 255, 0));
        int cronometro = asignarTiempo(semaforo) + 1;
        while (cronometro > 0) {
            try {
                cronometro--;
                lblCrono.setText(cronometro + "");
                if (ReglaSemaforo.vehiculoEmergencia(semaforos)) {
                    if (semaforo.getSensorSonido().isActivado()) {
                        preferenciaSemaforo(semaforo, imgSemaforo, lblCrono);
                        return;
                    } else {
                        cronometro = 0;
                    }
                }
                if (cronometro == 0) {
                    if (darMasTiempo()) {
                        gestionarTiempoSemaforo(semaforo, imgSemaforo, lblCrono);
                        return;
                    }
                    lblCrono.setForeground(new java.awt.Color(255, 255, 51));
                    imgSemaforo.setIcon(new javax.swing.ImageIcon(getClass().getResource("/imagenes/luzAmarilla.png")));
                    int t = Semaforo.TIEMPO_AMARILLO;
                    while (t > 0) {
                        lblCrono.setText(t + "");
                        t--;
                        Thread.sleep(delay);
                    }
                    imgSemaforo.setIcon(new javax.swing.ImageIcon(getClass().getResource("/imagenes/luzRoja.png")));
                    lblCrono.setText("0");
                    if (NOCTURNO) {
                        imgSemaforo.setIcon(new javax.swing.ImageIcon(getClass().getResource("/imagenes/luzAmarilla.png")));
                    }
                    lblCrono.setForeground(new java.awt.Color(255, 0, 0));
                }
                Thread.sleep(delay);
            } catch (InterruptedException ex) {
                System.err.println("ERROR EN LOS TIEMPOS");
            }
        }
    }
    
    private void preferenciaSemaforo(Semaforo semaforo, JLabel imgSemaforo, JLabel lblCrono) {
        imgSemaforo.setIcon(new javax.swing.ImageIcon(getClass().getResource("/imagenes/luzVerde.png")));
        lblCrono.setForeground(new java.awt.Color(0, 255, 0));
        while (semaforo.getSensorSonido().isActivado()) {
            lblCrono.setText("--");
        }
        gestionarTiempoSemaforo(semaforo, imgSemaforo, lblCrono);
    }

    public void detener() {
        lblEstado.setForeground(new java.awt.Color(255, 0, 0));
        lblEstado.setText("    Activado");
        sVia_1.detener();
        sVia_2.detener();
        sVia_3.detener();
        sVia_4.detener();
        mostrarSemaforos();
        hilo.suspend();
        via1_tiempo.setForeground(new java.awt.Color(255, 0, 0));
        via1_tiempo.setText("0");
        via2_tiempo.setForeground(new java.awt.Color(255, 0, 0));
        via2_tiempo.setText("0");
        via3_tiempo.setForeground(new java.awt.Color(255, 0, 0));
        via3_tiempo.setText("0");
        via4_tiempo.setForeground(new java.awt.Color(255, 0, 0));
        via4_tiempo.setText("0");
    }

    public boolean darMasTiempo() {
        boolean r = true;
        for (int i = 0; i < semaforos.size(); i++) {
            if (i != posicion) {
                if (asignarTiempo(semaforos.get(i)) != 0 || semaforos.get(i).getSensorSonido().isActivado()) {
                    r = false;
                }
            }
        }
        if (r && asignarTiempo(semaforos.get(posicion)) == 0) {
            r = false;
        }
        return r;
    }

    public int asignarTiempo(Semaforo semaforo) {
        if (!semaforo.getReglas().sensorAnticipadoLleno()) {
            if (semaforo.getReglas().colaLarga()) {
                return Semaforo.TIEMPO_LARGO;
            } else if (semaforo.getReglas().colaMedia()) {
                return Semaforo.TIEMPO_MEDIO;
            } else if (semaforo.getReglas().colaCorta()) {
                return Semaforo.TIEMPO_CORTO;
            } else {
                return 0;
            }
        } else {
            return 0;
        }
    }

    public void encenderLucesSemaforo(JLabel imSemaforo, String color) {
        switch (color) {
            case "rojo": {
                imSemaforo.setIcon(new javax.swing.ImageIcon(getClass().getResource("/imagenes/luzRoja.png")));
            }
            case "amarillo": {
                imSemaforo.setIcon(new javax.swing.ImageIcon(getClass().getResource("/imagenes/luzAmarilla.png")));
            }
            case "verde": {
                imSemaforo.setIcon(new javax.swing.ImageIcon(getClass().getResource("/imagenes/luzVerde.png")));
            }
            default:
                imSemaforo.setIcon(new javax.swing.ImageIcon(getClass().getResource("/imagenes/luzRoja.png")));
        }
    }

    private void mostrarSemaforos() {
        if (sVia_1.isVerde()) {
            via1_imgSemaforo.setIcon(new javax.swing.ImageIcon(getClass().getResource("/imagenes/luzVerde.png")));
            via2_imgSemaforo.setIcon(new javax.swing.ImageIcon(getClass().getResource("/imagenes/luzRoja.png")));
            via3_imgSemaforo.setIcon(new javax.swing.ImageIcon(getClass().getResource("/imagenes/luzRoja.png")));
            via4_imgSemaforo.setIcon(new javax.swing.ImageIcon(getClass().getResource("/imagenes/luzRoja.png")));
        } else if (sVia_2.isVerde()) {
            via1_imgSemaforo.setIcon(new javax.swing.ImageIcon(getClass().getResource("/imagenes/luzRoja.png")));
            via2_imgSemaforo.setIcon(new javax.swing.ImageIcon(getClass().getResource("/imagenes/luzVerde.png")));
            via3_imgSemaforo.setIcon(new javax.swing.ImageIcon(getClass().getResource("/imagenes/luzRoja.png")));
            via4_imgSemaforo.setIcon(new javax.swing.ImageIcon(getClass().getResource("/imagenes/luzRoja.png")));
        } else if (sVia_3.isVerde()) {
            via1_imgSemaforo.setIcon(new javax.swing.ImageIcon(getClass().getResource("/imagenes/luzRoja.png")));
            via2_imgSemaforo.setIcon(new javax.swing.ImageIcon(getClass().getResource("/imagenes/luzRoja.png")));
            via3_imgSemaforo.setIcon(new javax.swing.ImageIcon(getClass().getResource("/imagenes/luzVerde.png")));
            via4_imgSemaforo.setIcon(new javax.swing.ImageIcon(getClass().getResource("/imagenes/luzRoja.png")));
        } else if (sVia_4.isVerde()) {
            via1_imgSemaforo.setIcon(new javax.swing.ImageIcon(getClass().getResource("/imagenes/luzRoja.png")));
            via2_imgSemaforo.setIcon(new javax.swing.ImageIcon(getClass().getResource("/imagenes/luzRoja.png")));
            via3_imgSemaforo.setIcon(new javax.swing.ImageIcon(getClass().getResource("/imagenes/luzRoja.png")));
            via4_imgSemaforo.setIcon(new javax.swing.ImageIcon(getClass().getResource("/imagenes/luzVerde.png")));
        } else {
            via1_imgSemaforo.setIcon(new javax.swing.ImageIcon(getClass().getResource("/imagenes/luzRoja.png")));
            via2_imgSemaforo.setIcon(new javax.swing.ImageIcon(getClass().getResource("/imagenes/luzRoja.png")));
            via3_imgSemaforo.setIcon(new javax.swing.ImageIcon(getClass().getResource("/imagenes/luzRoja.png")));
            via4_imgSemaforo.setIcon(new javax.swing.ImageIcon(getClass().getResource("/imagenes/luzRoja.png")));
        }
    }

    private void horarioNocturno() {
        if (NOCTURNO) {
            lblDiurno.setForeground(new java.awt.Color(255, 0, 0));
            lblDiurno.setText("    Desactivado");
            lblNocturno.setForeground(new java.awt.Color(0, 255, 0));
            lblNocturno.setText("    Activado");
            via1_imgSemaforo.setIcon(new javax.swing.ImageIcon(getClass().getResource("/imagenes/luzAmarilla.png")));
            via2_imgSemaforo.setIcon(new javax.swing.ImageIcon(getClass().getResource("/imagenes/luzAmarilla.png")));
            via3_imgSemaforo.setIcon(new javax.swing.ImageIcon(getClass().getResource("/imagenes/luzAmarilla.png")));
            via4_imgSemaforo.setIcon(new javax.swing.ImageIcon(getClass().getResource("/imagenes/luzAmarilla.png")));
        } else {
            lblDiurno.setForeground(new java.awt.Color(0, 255, 0));
            lblDiurno.setText("    Activado");
            lblNocturno.setForeground(new java.awt.Color(255, 0, 0));
            lblNocturno.setText("    Desactivado");
            mostrarSemaforos();
        }
    }
    
    private void encenderSemaforos() {
        via1_imgSemaforo.setIcon(new javax.swing.ImageIcon(getClass().getResource("/imagenes/luzRoja.png")));
        via2_imgSemaforo.setIcon(new javax.swing.ImageIcon(getClass().getResource("/imagenes/luzRoja.png")));
        via3_imgSemaforo.setIcon(new javax.swing.ImageIcon(getClass().getResource("/imagenes/luzRoja.png")));
        via4_imgSemaforo.setIcon(new javax.swing.ImageIcon(getClass().getResource("/imagenes/luzRoja.png")));
    }
    
    private void apagarSemaforos() {
        via1_imgSemaforo.setIcon(new javax.swing.ImageIcon(getClass().getResource("/imagenes/semaforoOff.png")));
        via2_imgSemaforo.setIcon(new javax.swing.ImageIcon(getClass().getResource("/imagenes/semaforoOff.png")));
        via3_imgSemaforo.setIcon(new javax.swing.ImageIcon(getClass().getResource("/imagenes/semaforoOff.png")));
        via4_imgSemaforo.setIcon(new javax.swing.ImageIcon(getClass().getResource("/imagenes/semaforoOff.png")));
    }

    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        pnlFondo1 = new vistas.pnlFondo();
        pnlContenedor = new javax.swing.JPanel();
        pnlVia1 = new javax.swing.JPanel();
        jLabel1 = new javax.swing.JLabel();
        jPanel2 = new javax.swing.JPanel();
        via1_tiempo = new javax.swing.JLabel();
        jLabel3 = new javax.swing.JLabel();
        jPanel4 = new javax.swing.JPanel();
        via1_sensorAnt = new javax.swing.JCheckBox();
        via1_sensorCerca = new javax.swing.JCheckBox();
        via1_sensorMedio = new javax.swing.JCheckBox();
        via1_sensorLejos = new javax.swing.JCheckBox();
        via1_sensorSonido = new javax.swing.JCheckBox();
        via1_imgSemaforo = new javax.swing.JLabel();
        pnlContenedor2 = new javax.swing.JPanel();
        pnlVia3 = new javax.swing.JPanel();
        jLabel8 = new javax.swing.JLabel();
        jPanel6 = new javax.swing.JPanel();
        via2_tiempo = new javax.swing.JLabel();
        jLabel9 = new javax.swing.JLabel();
        jPanel7 = new javax.swing.JPanel();
        via2_sensorAnt = new javax.swing.JCheckBox();
        via2_sensorCerca = new javax.swing.JCheckBox();
        via2_sensorMedio = new javax.swing.JCheckBox();
        via2_sensorLejos = new javax.swing.JCheckBox();
        via2_sensorSonido = new javax.swing.JCheckBox();
        via2_imgSemaforo = new javax.swing.JLabel();
        pnlContenedor3 = new javax.swing.JPanel();
        pnlVia4 = new javax.swing.JPanel();
        jLabel7 = new javax.swing.JLabel();
        jPanel8 = new javax.swing.JPanel();
        via3_tiempo = new javax.swing.JLabel();
        jLabel10 = new javax.swing.JLabel();
        jPanel9 = new javax.swing.JPanel();
        via3_sensorAnt = new javax.swing.JCheckBox();
        via3_sensorCerca = new javax.swing.JCheckBox();
        via3_sensorMedio = new javax.swing.JCheckBox();
        via3_sensorLejos = new javax.swing.JCheckBox();
        via3_sensorSonido = new javax.swing.JCheckBox();
        via3_imgSemaforo = new javax.swing.JLabel();
        pnlContenedor4 = new javax.swing.JPanel();
        pnlVia2 = new javax.swing.JPanel();
        jLabel2 = new javax.swing.JLabel();
        jPanel3 = new javax.swing.JPanel();
        via4_tiempo = new javax.swing.JLabel();
        jLabel5 = new javax.swing.JLabel();
        jPanel5 = new javax.swing.JPanel();
        via4_sensorAnt = new javax.swing.JCheckBox();
        via4_sensorCerca = new javax.swing.JCheckBox();
        via4_sensorMedio = new javax.swing.JCheckBox();
        via4_sensorLejos = new javax.swing.JCheckBox();
        via4_sensorSonido = new javax.swing.JCheckBox();
        via4_imgSemaforo = new javax.swing.JLabel();
        pnlINF = new javax.swing.JPanel();
        jLabel4 = new javax.swing.JLabel();
        lblDiurno = new javax.swing.JLabel();
        jLabel6 = new javax.swing.JLabel();
        lblNocturno = new javax.swing.JLabel();
        jLabel11 = new javax.swing.JLabel();
        lblEstado = new javax.swing.JLabel();
        jMenuBar1 = new javax.swing.JMenuBar();
        jMenu1 = new javax.swing.JMenu();
        jMenuItem1 = new javax.swing.JMenuItem();
        jMenuItem2 = new javax.swing.JMenuItem();
        jSeparator1 = new javax.swing.JPopupMenu.Separator();
        jMenuItem3 = new javax.swing.JMenuItem();
        jMenu2 = new javax.swing.JMenu();
        jMenuItem5 = new javax.swing.JMenuItem();
        jMenuItem4 = new javax.swing.JMenuItem();
        jSeparator2 = new javax.swing.JPopupMenu.Separator();
        jMenu3 = new javax.swing.JMenu();
        jMenuItem6 = new javax.swing.JMenuItem();
        jMenuItem7 = new javax.swing.JMenuItem();
        jMenuItem8 = new javax.swing.JMenuItem();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        setUndecorated(true);

        pnlContenedor.setBorder(javax.swing.BorderFactory.createEtchedBorder());
        pnlContenedor.setOpaque(false);

        pnlVia1.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));

        jLabel1.setFont(new java.awt.Font("Tahoma", 1, 12)); // NOI18N
        jLabel1.setText("Semaforo Via 1");

        jPanel2.setBorder(new javax.swing.border.SoftBevelBorder(javax.swing.border.BevelBorder.RAISED));

        via1_tiempo.setFont(new java.awt.Font("Tahoma", 1, 48)); // NOI18N
        via1_tiempo.setForeground(new java.awt.Color(255, 0, 0));
        via1_tiempo.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        via1_tiempo.setText("0");

        javax.swing.GroupLayout jPanel2Layout = new javax.swing.GroupLayout(jPanel2);
        jPanel2.setLayout(jPanel2Layout);
        jPanel2Layout.setHorizontalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(via1_tiempo, javax.swing.GroupLayout.DEFAULT_SIZE, 99, Short.MAX_VALUE)
                .addContainerGap())
        );
        jPanel2Layout.setVerticalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(via1_tiempo, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addContainerGap())
        );

        jLabel3.setFont(new java.awt.Font("Tahoma", 1, 12)); // NOI18N
        jLabel3.setText("Tiempo");

        jPanel4.setLayout(new javax.swing.BoxLayout(jPanel4, javax.swing.BoxLayout.Y_AXIS));

        via1_sensorAnt.setText("Sensor Anticipado");
        via1_sensorAnt.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        via1_sensorAnt.addItemListener(new java.awt.event.ItemListener() {
            public void itemStateChanged(java.awt.event.ItemEvent evt) {
                via1_sensorAntItemStateChanged(evt);
            }
        });
        jPanel4.add(via1_sensorAnt);

        via1_sensorCerca.setText("Sensor Cerca Distancia");
        via1_sensorCerca.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        via1_sensorCerca.addChangeListener(new javax.swing.event.ChangeListener() {
            public void stateChanged(javax.swing.event.ChangeEvent evt) {
                via1_sensorCercaStateChanged(evt);
            }
        });
        jPanel4.add(via1_sensorCerca);

        via1_sensorMedio.setText("Sensor Media Distancia");
        via1_sensorMedio.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        via1_sensorMedio.addChangeListener(new javax.swing.event.ChangeListener() {
            public void stateChanged(javax.swing.event.ChangeEvent evt) {
                via1_sensorMedioStateChanged(evt);
            }
        });
        jPanel4.add(via1_sensorMedio);

        via1_sensorLejos.setText("Sensor Larga Distrancia");
        via1_sensorLejos.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        via1_sensorLejos.addChangeListener(new javax.swing.event.ChangeListener() {
            public void stateChanged(javax.swing.event.ChangeEvent evt) {
                via1_sensorLejosStateChanged(evt);
            }
        });
        jPanel4.add(via1_sensorLejos);

        via1_sensorSonido.setText("Sensor de Sonido (Emergencia)");
        via1_sensorSonido.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        via1_sensorSonido.addChangeListener(new javax.swing.event.ChangeListener() {
            public void stateChanged(javax.swing.event.ChangeEvent evt) {
                via1_sensorSonidoStateChanged(evt);
            }
        });
        jPanel4.add(via1_sensorSonido);

        javax.swing.GroupLayout pnlVia1Layout = new javax.swing.GroupLayout(pnlVia1);
        pnlVia1.setLayout(pnlVia1Layout);
        pnlVia1Layout.setHorizontalGroup(
            pnlVia1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pnlVia1Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(pnlVia1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(pnlVia1Layout.createSequentialGroup()
                        .addComponent(jLabel1)
                        .addGap(88, 88, 88))
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, pnlVia1Layout.createSequentialGroup()
                        .addComponent(jPanel4, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)))
                .addGroup(pnlVia1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jPanel2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel3))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        pnlVia1Layout.setVerticalGroup(
            pnlVia1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pnlVia1Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(pnlVia1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel1)
                    .addComponent(jLabel3))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(pnlVia1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(jPanel4, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jPanel2, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        via1_imgSemaforo.setIcon(new javax.swing.ImageIcon(getClass().getResource("/imagenes/semaforoOff.png"))); // NOI18N

        javax.swing.GroupLayout pnlContenedorLayout = new javax.swing.GroupLayout(pnlContenedor);
        pnlContenedor.setLayout(pnlContenedorLayout);
        pnlContenedorLayout.setHorizontalGroup(
            pnlContenedorLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pnlContenedorLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(pnlVia1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(via1_imgSemaforo)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        pnlContenedorLayout.setVerticalGroup(
            pnlContenedorLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pnlContenedorLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(pnlContenedorLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(via1_imgSemaforo, javax.swing.GroupLayout.PREFERRED_SIZE, 165, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(pnlVia1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(24, Short.MAX_VALUE))
        );

        pnlContenedor2.setBorder(javax.swing.BorderFactory.createEtchedBorder());
        pnlContenedor2.setOpaque(false);

        pnlVia3.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));

        jLabel8.setFont(new java.awt.Font("Tahoma", 1, 12)); // NOI18N
        jLabel8.setText("Semaforo Via 2");

        jPanel6.setBorder(new javax.swing.border.SoftBevelBorder(javax.swing.border.BevelBorder.RAISED));

        via2_tiempo.setFont(new java.awt.Font("Tahoma", 1, 48)); // NOI18N
        via2_tiempo.setForeground(new java.awt.Color(255, 0, 0));
        via2_tiempo.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        via2_tiempo.setText("0");

        javax.swing.GroupLayout jPanel6Layout = new javax.swing.GroupLayout(jPanel6);
        jPanel6.setLayout(jPanel6Layout);
        jPanel6Layout.setHorizontalGroup(
            jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel6Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(via2_tiempo, javax.swing.GroupLayout.DEFAULT_SIZE, 99, Short.MAX_VALUE)
                .addContainerGap())
        );
        jPanel6Layout.setVerticalGroup(
            jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel6Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(via2_tiempo, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addContainerGap())
        );

        jLabel9.setFont(new java.awt.Font("Tahoma", 1, 12)); // NOI18N
        jLabel9.setText("Tiempo");

        jPanel7.setLayout(new javax.swing.BoxLayout(jPanel7, javax.swing.BoxLayout.Y_AXIS));

        via2_sensorAnt.setText("Sensor Anticipado");
        via2_sensorAnt.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        via2_sensorAnt.addItemListener(new java.awt.event.ItemListener() {
            public void itemStateChanged(java.awt.event.ItemEvent evt) {
                via2_sensorAntItemStateChanged(evt);
            }
        });
        jPanel7.add(via2_sensorAnt);

        via2_sensorCerca.setText("Sensor Cerca Distancia");
        via2_sensorCerca.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        via2_sensorCerca.addChangeListener(new javax.swing.event.ChangeListener() {
            public void stateChanged(javax.swing.event.ChangeEvent evt) {
                via2_sensorCercaStateChanged(evt);
            }
        });
        jPanel7.add(via2_sensorCerca);

        via2_sensorMedio.setText("Sensor Media Distancia");
        via2_sensorMedio.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        via2_sensorMedio.addChangeListener(new javax.swing.event.ChangeListener() {
            public void stateChanged(javax.swing.event.ChangeEvent evt) {
                via2_sensorMedioStateChanged(evt);
            }
        });
        jPanel7.add(via2_sensorMedio);

        via2_sensorLejos.setText("Sensor Larga Distrancia");
        via2_sensorLejos.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        via2_sensorLejos.addChangeListener(new javax.swing.event.ChangeListener() {
            public void stateChanged(javax.swing.event.ChangeEvent evt) {
                via2_sensorLejosStateChanged(evt);
            }
        });
        jPanel7.add(via2_sensorLejos);

        via2_sensorSonido.setText("Sensor de Sonido (Emergencia)");
        via2_sensorSonido.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        via2_sensorSonido.addChangeListener(new javax.swing.event.ChangeListener() {
            public void stateChanged(javax.swing.event.ChangeEvent evt) {
                via2_sensorSonidoStateChanged(evt);
            }
        });
        jPanel7.add(via2_sensorSonido);

        javax.swing.GroupLayout pnlVia3Layout = new javax.swing.GroupLayout(pnlVia3);
        pnlVia3.setLayout(pnlVia3Layout);
        pnlVia3Layout.setHorizontalGroup(
            pnlVia3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pnlVia3Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(pnlVia3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(pnlVia3Layout.createSequentialGroup()
                        .addComponent(jLabel8)
                        .addGap(88, 88, 88))
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, pnlVia3Layout.createSequentialGroup()
                        .addComponent(jPanel7, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)))
                .addGroup(pnlVia3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jPanel6, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel9))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        pnlVia3Layout.setVerticalGroup(
            pnlVia3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pnlVia3Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(pnlVia3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel8)
                    .addComponent(jLabel9))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(pnlVia3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(jPanel7, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jPanel6, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        via2_imgSemaforo.setIcon(new javax.swing.ImageIcon(getClass().getResource("/imagenes/semaforoOff.png"))); // NOI18N

        javax.swing.GroupLayout pnlContenedor2Layout = new javax.swing.GroupLayout(pnlContenedor2);
        pnlContenedor2.setLayout(pnlContenedor2Layout);
        pnlContenedor2Layout.setHorizontalGroup(
            pnlContenedor2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pnlContenedor2Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(pnlContenedor2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(pnlVia3, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(via2_imgSemaforo))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        pnlContenedor2Layout.setVerticalGroup(
            pnlContenedor2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pnlContenedor2Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(pnlVia3, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(via2_imgSemaforo, javax.swing.GroupLayout.PREFERRED_SIZE, 165, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
        );

        pnlContenedor3.setBorder(javax.swing.BorderFactory.createEtchedBorder());
        pnlContenedor3.setOpaque(false);

        pnlVia4.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));

        jLabel7.setFont(new java.awt.Font("Tahoma", 1, 12)); // NOI18N
        jLabel7.setText("Semaforo Via 3");

        jPanel8.setBorder(new javax.swing.border.SoftBevelBorder(javax.swing.border.BevelBorder.RAISED));

        via3_tiempo.setFont(new java.awt.Font("Tahoma", 1, 48)); // NOI18N
        via3_tiempo.setForeground(new java.awt.Color(255, 0, 0));
        via3_tiempo.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        via3_tiempo.setText("0");

        javax.swing.GroupLayout jPanel8Layout = new javax.swing.GroupLayout(jPanel8);
        jPanel8.setLayout(jPanel8Layout);
        jPanel8Layout.setHorizontalGroup(
            jPanel8Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel8Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(via3_tiempo, javax.swing.GroupLayout.DEFAULT_SIZE, 99, Short.MAX_VALUE)
                .addContainerGap())
        );
        jPanel8Layout.setVerticalGroup(
            jPanel8Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel8Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(via3_tiempo, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addContainerGap())
        );

        jLabel10.setFont(new java.awt.Font("Tahoma", 1, 12)); // NOI18N
        jLabel10.setText("Tiempo");

        jPanel9.setLayout(new javax.swing.BoxLayout(jPanel9, javax.swing.BoxLayout.Y_AXIS));

        via3_sensorAnt.setText("Sensor Anticipado");
        via3_sensorAnt.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        via3_sensorAnt.addItemListener(new java.awt.event.ItemListener() {
            public void itemStateChanged(java.awt.event.ItemEvent evt) {
                via3_sensorAntItemStateChanged(evt);
            }
        });
        jPanel9.add(via3_sensorAnt);

        via3_sensorCerca.setText("Sensor Cerca Distancia");
        via3_sensorCerca.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        via3_sensorCerca.addChangeListener(new javax.swing.event.ChangeListener() {
            public void stateChanged(javax.swing.event.ChangeEvent evt) {
                via3_sensorCercaStateChanged(evt);
            }
        });
        jPanel9.add(via3_sensorCerca);

        via3_sensorMedio.setText("Sensor Media Distancia");
        via3_sensorMedio.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        via3_sensorMedio.addChangeListener(new javax.swing.event.ChangeListener() {
            public void stateChanged(javax.swing.event.ChangeEvent evt) {
                via3_sensorMedioStateChanged(evt);
            }
        });
        jPanel9.add(via3_sensorMedio);

        via3_sensorLejos.setText("Sensor Larga Distrancia");
        via3_sensorLejos.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        via3_sensorLejos.addChangeListener(new javax.swing.event.ChangeListener() {
            public void stateChanged(javax.swing.event.ChangeEvent evt) {
                via3_sensorLejosStateChanged(evt);
            }
        });
        jPanel9.add(via3_sensorLejos);

        via3_sensorSonido.setText("Sensor de Sonido (Emergencia)");
        via3_sensorSonido.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        via3_sensorSonido.addChangeListener(new javax.swing.event.ChangeListener() {
            public void stateChanged(javax.swing.event.ChangeEvent evt) {
                via3_sensorSonidoStateChanged(evt);
            }
        });
        jPanel9.add(via3_sensorSonido);

        javax.swing.GroupLayout pnlVia4Layout = new javax.swing.GroupLayout(pnlVia4);
        pnlVia4.setLayout(pnlVia4Layout);
        pnlVia4Layout.setHorizontalGroup(
            pnlVia4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pnlVia4Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(pnlVia4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(pnlVia4Layout.createSequentialGroup()
                        .addComponent(jLabel7)
                        .addGap(88, 88, 88))
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, pnlVia4Layout.createSequentialGroup()
                        .addComponent(jPanel9, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)))
                .addGroup(pnlVia4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jPanel8, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel10))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        pnlVia4Layout.setVerticalGroup(
            pnlVia4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pnlVia4Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(pnlVia4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel7)
                    .addComponent(jLabel10))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(pnlVia4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(jPanel9, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jPanel8, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        via3_imgSemaforo.setIcon(new javax.swing.ImageIcon(getClass().getResource("/imagenes/semaforoOff.png"))); // NOI18N

        javax.swing.GroupLayout pnlContenedor3Layout = new javax.swing.GroupLayout(pnlContenedor3);
        pnlContenedor3.setLayout(pnlContenedor3Layout);
        pnlContenedor3Layout.setHorizontalGroup(
            pnlContenedor3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pnlContenedor3Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(via3_imgSemaforo)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(pnlVia4, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        pnlContenedor3Layout.setVerticalGroup(
            pnlContenedor3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pnlContenedor3Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(pnlContenedor3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(via3_imgSemaforo, javax.swing.GroupLayout.PREFERRED_SIZE, 165, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(pnlVia4, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        pnlContenedor4.setBorder(javax.swing.BorderFactory.createEtchedBorder());
        pnlContenedor4.setOpaque(false);

        pnlVia2.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));

        jLabel2.setFont(new java.awt.Font("Tahoma", 1, 12)); // NOI18N
        jLabel2.setText("Semaforo Via 4");

        jPanel3.setBorder(new javax.swing.border.SoftBevelBorder(javax.swing.border.BevelBorder.RAISED));

        via4_tiempo.setFont(new java.awt.Font("Tahoma", 1, 48)); // NOI18N
        via4_tiempo.setForeground(new java.awt.Color(255, 0, 0));
        via4_tiempo.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        via4_tiempo.setText("0");

        javax.swing.GroupLayout jPanel3Layout = new javax.swing.GroupLayout(jPanel3);
        jPanel3.setLayout(jPanel3Layout);
        jPanel3Layout.setHorizontalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel3Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(via4_tiempo, javax.swing.GroupLayout.DEFAULT_SIZE, 99, Short.MAX_VALUE)
                .addContainerGap())
        );
        jPanel3Layout.setVerticalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel3Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(via4_tiempo, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addContainerGap())
        );

        jLabel5.setFont(new java.awt.Font("Tahoma", 1, 12)); // NOI18N
        jLabel5.setText("Tiempo");

        jPanel5.setLayout(new javax.swing.BoxLayout(jPanel5, javax.swing.BoxLayout.Y_AXIS));

        via4_sensorAnt.setText("Sensor Anticipado");
        via4_sensorAnt.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        via4_sensorAnt.addItemListener(new java.awt.event.ItemListener() {
            public void itemStateChanged(java.awt.event.ItemEvent evt) {
                via4_sensorAntItemStateChanged(evt);
            }
        });
        jPanel5.add(via4_sensorAnt);

        via4_sensorCerca.setText("Sensor Cerca Distancia");
        via4_sensorCerca.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        via4_sensorCerca.addChangeListener(new javax.swing.event.ChangeListener() {
            public void stateChanged(javax.swing.event.ChangeEvent evt) {
                via4_sensorCercaStateChanged(evt);
            }
        });
        jPanel5.add(via4_sensorCerca);

        via4_sensorMedio.setText("Sensor Media Distancia");
        via4_sensorMedio.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        via4_sensorMedio.addChangeListener(new javax.swing.event.ChangeListener() {
            public void stateChanged(javax.swing.event.ChangeEvent evt) {
                via4_sensorMedioStateChanged(evt);
            }
        });
        jPanel5.add(via4_sensorMedio);

        via4_sensorLejos.setText("Sensor Larga Distrancia");
        via4_sensorLejos.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        via4_sensorLejos.addChangeListener(new javax.swing.event.ChangeListener() {
            public void stateChanged(javax.swing.event.ChangeEvent evt) {
                via4_sensorLejosStateChanged(evt);
            }
        });
        via4_sensorLejos.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                via4_sensorLejosActionPerformed(evt);
            }
        });
        jPanel5.add(via4_sensorLejos);

        via4_sensorSonido.setText("Sensor de Sonido (Emergencia)");
        via4_sensorSonido.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        via4_sensorSonido.addChangeListener(new javax.swing.event.ChangeListener() {
            public void stateChanged(javax.swing.event.ChangeEvent evt) {
                via4_sensorSonidoStateChanged(evt);
            }
        });
        jPanel5.add(via4_sensorSonido);

        javax.swing.GroupLayout pnlVia2Layout = new javax.swing.GroupLayout(pnlVia2);
        pnlVia2.setLayout(pnlVia2Layout);
        pnlVia2Layout.setHorizontalGroup(
            pnlVia2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pnlVia2Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(pnlVia2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(pnlVia2Layout.createSequentialGroup()
                        .addComponent(jLabel2)
                        .addGap(88, 88, 88))
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, pnlVia2Layout.createSequentialGroup()
                        .addComponent(jPanel5, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)))
                .addGroup(pnlVia2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jPanel3, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel5))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        pnlVia2Layout.setVerticalGroup(
            pnlVia2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pnlVia2Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(pnlVia2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel2)
                    .addComponent(jLabel5))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(pnlVia2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(jPanel5, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jPanel3, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        via4_imgSemaforo.setIcon(new javax.swing.ImageIcon(getClass().getResource("/imagenes/semaforoOff.png"))); // NOI18N

        javax.swing.GroupLayout pnlContenedor4Layout = new javax.swing.GroupLayout(pnlContenedor4);
        pnlContenedor4.setLayout(pnlContenedor4Layout);
        pnlContenedor4Layout.setHorizontalGroup(
            pnlContenedor4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pnlContenedor4Layout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addGroup(pnlContenedor4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(pnlVia2, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(via4_imgSemaforo, javax.swing.GroupLayout.Alignment.TRAILING))
                .addContainerGap())
        );
        pnlContenedor4Layout.setVerticalGroup(
            pnlContenedor4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pnlContenedor4Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(via4_imgSemaforo, javax.swing.GroupLayout.PREFERRED_SIZE, 165, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(pnlVia2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
        );

        javax.swing.GroupLayout pnlFondo1Layout = new javax.swing.GroupLayout(pnlFondo1);
        pnlFondo1.setLayout(pnlFondo1Layout);
        pnlFondo1Layout.setHorizontalGroup(
            pnlFondo1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pnlFondo1Layout.createSequentialGroup()
                .addGroup(pnlFondo1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(pnlContenedor, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(pnlContenedor4, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 454, Short.MAX_VALUE)
                .addGroup(pnlFondo1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(pnlContenedor3, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(pnlContenedor2, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
        );
        pnlFondo1Layout.setVerticalGroup(
            pnlFondo1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pnlFondo1Layout.createSequentialGroup()
                .addComponent(pnlContenedor2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(pnlContenedor3, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
            .addGroup(pnlFondo1Layout.createSequentialGroup()
                .addComponent(pnlContenedor, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 62, Short.MAX_VALUE)
                .addComponent(pnlContenedor4, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
        );

        pnlINF.setBorder(javax.swing.BorderFactory.createTitledBorder("Informacion"));
        pnlINF.setLayout(new java.awt.GridLayout(0, 2));

        jLabel4.setFont(new java.awt.Font("Tahoma", 1, 14)); // NOI18N
        jLabel4.setText("Horario Diurno:");
        pnlINF.add(jLabel4);

        lblDiurno.setFont(new java.awt.Font("Tahoma", 1, 12)); // NOI18N
        lblDiurno.setForeground(new java.awt.Color(0, 255, 0));
        lblDiurno.setText("    Activado");
        pnlINF.add(lblDiurno);

        jLabel6.setFont(new java.awt.Font("Tahoma", 1, 14)); // NOI18N
        jLabel6.setText("Horario Nocturno:");
        pnlINF.add(jLabel6);

        lblNocturno.setFont(new java.awt.Font("Tahoma", 1, 12)); // NOI18N
        lblNocturno.setForeground(new java.awt.Color(255, 0, 0));
        lblNocturno.setText("    Desactivado");
        pnlINF.add(lblNocturno);

        jLabel11.setFont(new java.awt.Font("Tahoma", 1, 14)); // NOI18N
        jLabel11.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        jLabel11.setText("Estado:");

        lblEstado.setFont(new java.awt.Font("Tahoma", 1, 12)); // NOI18N
        lblEstado.setForeground(new java.awt.Color(255, 0, 0));
        lblEstado.setText("    Desactivado");

        jMenu1.setText("Inicio");

        jMenuItem1.setText("Iniciar");
        jMenuItem1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItem1ActionPerformed(evt);
            }
        });
        jMenu1.add(jMenuItem1);

        jMenuItem2.setText("Detener");
        jMenuItem2.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItem2ActionPerformed(evt);
            }
        });
        jMenu1.add(jMenuItem2);
        jMenu1.add(jSeparator1);

        jMenuItem3.setText("Salir");
        jMenuItem3.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItem3ActionPerformed(evt);
            }
        });
        jMenu1.add(jMenuItem3);

        jMenuBar1.add(jMenu1);

        jMenu2.setText("Opciones");

        jMenuItem5.setText("Horario Diurno");
        jMenuItem5.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItem5ActionPerformed(evt);
            }
        });
        jMenu2.add(jMenuItem5);

        jMenuItem4.setText("Horario Nocturno");
        jMenuItem4.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItem4ActionPerformed(evt);
            }
        });
        jMenu2.add(jMenuItem4);
        jMenu2.add(jSeparator2);

        jMenu3.setText("Tiempos");

        jMenuItem6.setText("Modificar Tiempo Corto");
        jMenu3.add(jMenuItem6);

        jMenuItem7.setText("Modificar Tiempo Medio");
        jMenu3.add(jMenuItem7);

        jMenuItem8.setText("Modificar Tiempo Largo");
        jMenu3.add(jMenuItem8);

        jMenu2.add(jMenu3);

        jMenuBar1.add(jMenu2);

        setJMenuBar(jMenuBar1);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addComponent(pnlINF, javax.swing.GroupLayout.PREFERRED_SIZE, 335, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(135, 135, 135)
                .addComponent(jLabel11, javax.swing.GroupLayout.PREFERRED_SIZE, 161, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(lblEstado, javax.swing.GroupLayout.PREFERRED_SIZE, 161, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(0, 0, Short.MAX_VALUE))
            .addComponent(pnlFondo1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(pnlINF, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGroup(layout.createSequentialGroup()
                        .addContainerGap()
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jLabel11)
                            .addComponent(lblEstado, javax.swing.GroupLayout.PREFERRED_SIZE, 17, javax.swing.GroupLayout.PREFERRED_SIZE))))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(pnlFondo1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        pack();
        setLocationRelativeTo(null);
    }// </editor-fold>//GEN-END:initComponents

    private void jMenuItem3ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItem3ActionPerformed
        System.exit(0);
    }//GEN-LAST:event_jMenuItem3ActionPerformed

    private void jMenuItem5ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItem5ActionPerformed
        NOCTURNO = false;
        horarioNocturno();
    }//GEN-LAST:event_jMenuItem5ActionPerformed

    private void jMenuItem4ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItem4ActionPerformed
        NOCTURNO = true;
        horarioNocturno();
    }//GEN-LAST:event_jMenuItem4ActionPerformed

    private void via1_sensorAntItemStateChanged(java.awt.event.ItemEvent evt) {//GEN-FIRST:event_via1_sensorAntItemStateChanged
        if (via1_sensorAnt.isSelected()) {
            sVia_1.activarSensorProx(Semaforo.SENSOR_ANTICIPADO);
        } else {
            sVia_1.desactivarSensorProx(Semaforo.SENSOR_ANTICIPADO);
        }
    }//GEN-LAST:event_via1_sensorAntItemStateChanged

    private void jMenuItem1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItem1ActionPerformed
        INICIO = true;
        encenderSemaforos();
        iniciar();
    }//GEN-LAST:event_jMenuItem1ActionPerformed

    private void jMenuItem2ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItem2ActionPerformed
        INICIO = false;
        detener();
        apagarSemaforos();
    }//GEN-LAST:event_jMenuItem2ActionPerformed

    private void via1_sensorCercaStateChanged(javax.swing.event.ChangeEvent evt) {//GEN-FIRST:event_via1_sensorCercaStateChanged
        if (via1_sensorCerca.isSelected()) {
            sVia_1.activarSensorProx(Semaforo.SENSOR_CERCA);
        } else {
            sVia_1.desactivarSensorProx(Semaforo.SENSOR_CERCA);
        }
    }//GEN-LAST:event_via1_sensorCercaStateChanged

    private void via1_sensorMedioStateChanged(javax.swing.event.ChangeEvent evt) {//GEN-FIRST:event_via1_sensorMedioStateChanged
        if (via1_sensorMedio.isSelected()) {
            sVia_1.activarSensorProx(Semaforo.SENSOR_MEDIO);
        } else {
            sVia_1.desactivarSensorProx(Semaforo.SENSOR_MEDIO);
        }
    }//GEN-LAST:event_via1_sensorMedioStateChanged

    private void via1_sensorLejosStateChanged(javax.swing.event.ChangeEvent evt) {//GEN-FIRST:event_via1_sensorLejosStateChanged
        if (via1_sensorLejos.isSelected()) {
            sVia_1.activarSensorProx(Semaforo.SENSOR_LEJOS);
        } else {
            sVia_1.desactivarSensorProx(Semaforo.SENSOR_LEJOS);
        }
    }//GEN-LAST:event_via1_sensorLejosStateChanged

    private void via1_sensorSonidoStateChanged(javax.swing.event.ChangeEvent evt) {//GEN-FIRST:event_via1_sensorSonidoStateChanged
        if (via1_sensorSonido.isSelected()) {
            sVia_1.activarSensorSonido();
        } else {
            sVia_1.desactivarSensorSonido();
        }
    }//GEN-LAST:event_via1_sensorSonidoStateChanged

    private void via4_sensorAntItemStateChanged(java.awt.event.ItemEvent evt) {//GEN-FIRST:event_via4_sensorAntItemStateChanged
        if (via4_sensorAnt.isSelected()) {
            sVia_4.activarSensorProx(Semaforo.SENSOR_ANTICIPADO);
        } else {
            sVia_4.desactivarSensorProx(Semaforo.SENSOR_ANTICIPADO);
        }
    }//GEN-LAST:event_via4_sensorAntItemStateChanged

    private void via4_sensorCercaStateChanged(javax.swing.event.ChangeEvent evt) {//GEN-FIRST:event_via4_sensorCercaStateChanged
        if (via4_sensorCerca.isSelected()) {
            sVia_4.activarSensorProx(Semaforo.SENSOR_CERCA);
        } else {
            sVia_4.desactivarSensorProx(Semaforo.SENSOR_CERCA);
        }
    }//GEN-LAST:event_via4_sensorCercaStateChanged

    private void via4_sensorMedioStateChanged(javax.swing.event.ChangeEvent evt) {//GEN-FIRST:event_via4_sensorMedioStateChanged
        if (via4_sensorMedio.isSelected()) {
            sVia_4.activarSensorProx(Semaforo.SENSOR_MEDIO);
        } else {
            sVia_4.desactivarSensorProx(Semaforo.SENSOR_MEDIO);
        }
    }//GEN-LAST:event_via4_sensorMedioStateChanged

    private void via4_sensorLejosStateChanged(javax.swing.event.ChangeEvent evt) {//GEN-FIRST:event_via4_sensorLejosStateChanged
        if (via4_sensorLejos.isSelected()) {
            sVia_4.activarSensorProx(Semaforo.SENSOR_LEJOS);
        } else {
            sVia_4.desactivarSensorProx(Semaforo.SENSOR_LEJOS);
        }
    }//GEN-LAST:event_via4_sensorLejosStateChanged

    private void via4_sensorSonidoStateChanged(javax.swing.event.ChangeEvent evt) {//GEN-FIRST:event_via4_sensorSonidoStateChanged
        if (via4_sensorSonido.isSelected()) {
            sVia_4.activarSensorSonido();
        } else {
            sVia_4.desactivarSensorSonido();
        }
    }//GEN-LAST:event_via4_sensorSonidoStateChanged

    private void via2_sensorAntItemStateChanged(java.awt.event.ItemEvent evt) {//GEN-FIRST:event_via2_sensorAntItemStateChanged
        if (via2_sensorAnt.isSelected()) {
            sVia_2.activarSensorProx(Semaforo.SENSOR_ANTICIPADO);
        } else {
            sVia_2.desactivarSensorProx(Semaforo.SENSOR_ANTICIPADO);
        }
    }//GEN-LAST:event_via2_sensorAntItemStateChanged

    private void via2_sensorCercaStateChanged(javax.swing.event.ChangeEvent evt) {//GEN-FIRST:event_via2_sensorCercaStateChanged
        if (via2_sensorCerca.isSelected()) {
            sVia_2.activarSensorProx(Semaforo.SENSOR_CERCA);
        } else {
            sVia_2.desactivarSensorProx(Semaforo.SENSOR_CERCA);
        }
    }//GEN-LAST:event_via2_sensorCercaStateChanged

    private void via2_sensorMedioStateChanged(javax.swing.event.ChangeEvent evt) {//GEN-FIRST:event_via2_sensorMedioStateChanged
        if (via2_sensorMedio.isSelected()) {
            sVia_2.activarSensorProx(Semaforo.SENSOR_MEDIO);
        } else {
            sVia_2.desactivarSensorProx(Semaforo.SENSOR_MEDIO);
        }
    }//GEN-LAST:event_via2_sensorMedioStateChanged

    private void via2_sensorLejosStateChanged(javax.swing.event.ChangeEvent evt) {//GEN-FIRST:event_via2_sensorLejosStateChanged
        if (via2_sensorLejos.isSelected()) {
            sVia_2.activarSensorProx(Semaforo.SENSOR_LEJOS);
        } else {
            sVia_2.desactivarSensorProx(Semaforo.SENSOR_LEJOS);
        }
    }//GEN-LAST:event_via2_sensorLejosStateChanged

    private void via2_sensorSonidoStateChanged(javax.swing.event.ChangeEvent evt) {//GEN-FIRST:event_via2_sensorSonidoStateChanged
        if (via2_sensorSonido.isSelected()) {
            sVia_2.activarSensorSonido();
        } else {
            sVia_2.desactivarSensorSonido();
        }
    }//GEN-LAST:event_via2_sensorSonidoStateChanged

    private void via3_sensorAntItemStateChanged(java.awt.event.ItemEvent evt) {//GEN-FIRST:event_via3_sensorAntItemStateChanged
        if (via3_sensorAnt.isSelected()) {
            sVia_3.activarSensorProx(Semaforo.SENSOR_ANTICIPADO);
        } else {
            sVia_3.desactivarSensorProx(Semaforo.SENSOR_ANTICIPADO);
        }
    }//GEN-LAST:event_via3_sensorAntItemStateChanged

    private void via3_sensorCercaStateChanged(javax.swing.event.ChangeEvent evt) {//GEN-FIRST:event_via3_sensorCercaStateChanged
        if (via3_sensorCerca.isSelected()) {
            sVia_3.activarSensorProx(Semaforo.SENSOR_CERCA);
        } else {
            sVia_3.desactivarSensorProx(Semaforo.SENSOR_CERCA);
        }
    }//GEN-LAST:event_via3_sensorCercaStateChanged

    private void via3_sensorMedioStateChanged(javax.swing.event.ChangeEvent evt) {//GEN-FIRST:event_via3_sensorMedioStateChanged
        if (via3_sensorMedio.isSelected()) {
            sVia_3.activarSensorProx(Semaforo.SENSOR_MEDIO);
        } else {
            sVia_3.desactivarSensorProx(Semaforo.SENSOR_MEDIO);
        }
    }//GEN-LAST:event_via3_sensorMedioStateChanged

    private void via3_sensorLejosStateChanged(javax.swing.event.ChangeEvent evt) {//GEN-FIRST:event_via3_sensorLejosStateChanged
        if (via3_sensorLejos.isSelected()) {
            sVia_3.activarSensorProx(Semaforo.SENSOR_LEJOS);
        } else {
            sVia_3.desactivarSensorProx(Semaforo.SENSOR_LEJOS);
        }
    }//GEN-LAST:event_via3_sensorLejosStateChanged

    private void via3_sensorSonidoStateChanged(javax.swing.event.ChangeEvent evt) {//GEN-FIRST:event_via3_sensorSonidoStateChanged
        if (via3_sensorSonido.isSelected()) {
            sVia_3.activarSensorSonido();
        } else {
            sVia_3.desactivarSensorSonido();
        }
    }//GEN-LAST:event_via3_sensorSonidoStateChanged

    private void via4_sensorLejosActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_via4_sensorLejosActionPerformed
    }//GEN-LAST:event_via4_sensorLejosActionPerformed

    /**
     * @param args the command line arguments
     */
    public static void main(String args[]) {

        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                new Vista().setVisible(true);
            }
        });
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel10;
    private javax.swing.JLabel jLabel11;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JLabel jLabel8;
    private javax.swing.JLabel jLabel9;
    private javax.swing.JMenu jMenu1;
    private javax.swing.JMenu jMenu2;
    private javax.swing.JMenu jMenu3;
    private javax.swing.JMenuBar jMenuBar1;
    private javax.swing.JMenuItem jMenuItem1;
    private javax.swing.JMenuItem jMenuItem2;
    private javax.swing.JMenuItem jMenuItem3;
    private javax.swing.JMenuItem jMenuItem4;
    private javax.swing.JMenuItem jMenuItem5;
    private javax.swing.JMenuItem jMenuItem6;
    private javax.swing.JMenuItem jMenuItem7;
    private javax.swing.JMenuItem jMenuItem8;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JPanel jPanel3;
    private javax.swing.JPanel jPanel4;
    private javax.swing.JPanel jPanel5;
    private javax.swing.JPanel jPanel6;
    private javax.swing.JPanel jPanel7;
    private javax.swing.JPanel jPanel8;
    private javax.swing.JPanel jPanel9;
    private javax.swing.JPopupMenu.Separator jSeparator1;
    private javax.swing.JPopupMenu.Separator jSeparator2;
    private javax.swing.JLabel lblDiurno;
    private javax.swing.JLabel lblEstado;
    private javax.swing.JLabel lblNocturno;
    private javax.swing.JPanel pnlContenedor;
    private javax.swing.JPanel pnlContenedor2;
    private javax.swing.JPanel pnlContenedor3;
    private javax.swing.JPanel pnlContenedor4;
    private vistas.pnlFondo pnlFondo1;
    private javax.swing.JPanel pnlINF;
    private javax.swing.JPanel pnlVia1;
    private javax.swing.JPanel pnlVia2;
    private javax.swing.JPanel pnlVia3;
    private javax.swing.JPanel pnlVia4;
    private javax.swing.JLabel via1_imgSemaforo;
    private javax.swing.JCheckBox via1_sensorAnt;
    private javax.swing.JCheckBox via1_sensorCerca;
    private javax.swing.JCheckBox via1_sensorLejos;
    private javax.swing.JCheckBox via1_sensorMedio;
    private javax.swing.JCheckBox via1_sensorSonido;
    private javax.swing.JLabel via1_tiempo;
    private javax.swing.JLabel via2_imgSemaforo;
    private javax.swing.JCheckBox via2_sensorAnt;
    private javax.swing.JCheckBox via2_sensorCerca;
    private javax.swing.JCheckBox via2_sensorLejos;
    private javax.swing.JCheckBox via2_sensorMedio;
    private javax.swing.JCheckBox via2_sensorSonido;
    private javax.swing.JLabel via2_tiempo;
    private javax.swing.JLabel via3_imgSemaforo;
    private javax.swing.JCheckBox via3_sensorAnt;
    private javax.swing.JCheckBox via3_sensorCerca;
    private javax.swing.JCheckBox via3_sensorLejos;
    private javax.swing.JCheckBox via3_sensorMedio;
    private javax.swing.JCheckBox via3_sensorSonido;
    private javax.swing.JLabel via3_tiempo;
    private javax.swing.JLabel via4_imgSemaforo;
    private javax.swing.JCheckBox via4_sensorAnt;
    private javax.swing.JCheckBox via4_sensorCerca;
    private javax.swing.JCheckBox via4_sensorLejos;
    private javax.swing.JCheckBox via4_sensorMedio;
    private javax.swing.JCheckBox via4_sensorSonido;
    private javax.swing.JLabel via4_tiempo;
    // End of variables declaration//GEN-END:variables
}
