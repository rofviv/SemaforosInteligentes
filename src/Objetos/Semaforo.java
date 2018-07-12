package Objetos;

import java.util.ArrayList;
import reglas.ReglaSemaforo;

public class Semaforo {
    
    private boolean rojo, amarillo, verde;
    private final ArrayList<SensorProximidad> sensoresProx;
    private final SensorSonido sensorSonido;
    public ReglaSemaforo reglas;
    
    public static int TIEMPO_CORTO = 5;
    public static int TIEMPO_MEDIO = 10;
    public static int TIEMPO_LARGO = 15;
    public static int TIEMPO_EXTRA = 5;
    public static int TIEMPO_AMARILLO = 2;
    
    public static final int SENSOR_ANTICIPADO = 0;
    public static final int SENSOR_CERCA = 1;
    public static final int SENSOR_MEDIO = 2;
    public static final int SENSOR_LEJOS = 3;

    public Semaforo() {
        this.rojo = true;
        this.amarillo = this.verde = false;
        this.sensoresProx = new ArrayList<>();
        this.sensorSonido = new SensorSonido();
        reglas = new ReglaSemaforo(this);
        initSensores();
    }
    
    public ReglaSemaforo getReglas() {
        return reglas;
    }
    
    private void initSensores() {
        SensorProximidad sAnticipado = new SensorProximidad();
        SensorProximidad sCercaDis = new SensorProximidad();
        SensorProximidad sMediaDis = new SensorProximidad();
        SensorProximidad sLejanaDis = new SensorProximidad();
        this.sensoresProx.add(sAnticipado);
        this.sensoresProx.add(sCercaDis);
        this.sensoresProx.add(sMediaDis);
        this.sensoresProx.add(sLejanaDis);
    }

    public boolean isRojo() {
        return rojo;
    }

    public boolean isAmarillo() {
        return amarillo;
    }

    public boolean isVerde() {
        return verde;
    }
    
    public void detener() {
        this.rojo = true;
        this.amarillo = this.verde = false;
    }
    
    public void activarSensorSonido() {
        this.sensorSonido.setActivado(true);
    }
    
    public void desactivarSensorSonido() {
        this.sensorSonido.setActivado(false);  
    }
    
    public void activarSensorProx(int pos) {
        this.sensoresProx.get(pos).setLleno(true);
    }
    
    public void desactivarSensorProx(int pos) {
        this.sensoresProx.get(pos).setLleno(false);
    }

    public ArrayList<SensorProximidad> getSensoresProx() {
        return sensoresProx;
    }

    public SensorSonido getSensorSonido() {
        return sensorSonido;
    }

    public static int getTIEMPO_CORTO() {
        return TIEMPO_CORTO;
    }

    public static void setTIEMPO_CORTO(int TIEMPO_CORTO) {
        Semaforo.TIEMPO_CORTO = TIEMPO_CORTO;
    }

    public static int getTIEMPO_MEDIO() {
        return TIEMPO_MEDIO;
    }

    public static void setTIEMPO_MEDIO(int TIEMPO_MEDIO) {
        Semaforo.TIEMPO_MEDIO = TIEMPO_MEDIO;
    }

    public static int getTIEMPO_LARGO() {
        return TIEMPO_LARGO;
    }

    public static void setTIEMPO_LARGO(int TIEMPO_LARGO) {
        Semaforo.TIEMPO_LARGO = TIEMPO_LARGO;
    }

    public static int getTIEMPO_EXTRA() {
        return TIEMPO_EXTRA;
    }

    public static void setTIEMPO_EXTRA(int TIEMPO_EXTRA) {
        Semaforo.TIEMPO_EXTRA = TIEMPO_EXTRA;
    }
    
    public void encenderRojo() {
        this.verde = false;
        this.amarillo = true;
        try {
            Thread.sleep(3000);
            this.amarillo = false;
            this.rojo = true;
        } catch (InterruptedException ex) {
            System.out.println("No se pudo encender el ROJO");
        }
    }

    public void encenderVerde() {
        this.amarillo = false;
        this.rojo = false;
        this.verde = true;
    }
}
