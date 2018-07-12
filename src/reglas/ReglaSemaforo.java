package reglas;

import Objetos.Semaforo;
import java.util.ArrayList;

public class ReglaSemaforo {
    
    Semaforo semaforo;
    
    public ReglaSemaforo(Semaforo semaforo) {
        this.semaforo = semaforo;
    }
    
    /**
     * Regla 1: Si se detecta un vehiculo de Emergencia acercandose a un semaforo
     * Se debe colocar todos los semaforos en ROJO y habilitar VERDE a la via por donde
     * se acerca el Vehiculo de EMERGENCIA
     * 
     * @param semaforos
     * @return 
     */
    public static boolean vehiculoEmergencia(ArrayList<Semaforo> semaforos) {
       boolean r = false;
        for (Semaforo sem : semaforos) {
            if (sem.getSensorSonido().isActivado()) {
                r = true;
                break;
            }
        }
       return r;
    }
    
    public boolean otrosSemaforosVacios() {
        boolean r = false;
        return r;
    }
    
    /**
     * Si el sensor Anticipado esta lleno, no dejar Pasar
     * @return 
     */
    public boolean sensorAnticipadoLleno() {
        boolean r = false;
        if (semaforo.getSensoresProx().get(Semaforo.SENSOR_ANTICIPADO).isLleno()) {
            r = true;
        }
        return r;
    }
    
    /**
     * Si la cola es demasiado larga (Sensor uno, dos y tres estan llenos) y el sensor anticipado esta vacio, dejar Pasar
     * @return 
     */
    public boolean colaLarga() {
        boolean r = false;
        if (semaforo.getSensoresProx().get(Semaforo.SENSOR_LEJOS).isLleno() 
                && semaforo.getSensoresProx().get(Semaforo.SENSOR_MEDIO).isLleno() 
                && semaforo.getSensoresProx().get(Semaforo.SENSOR_CERCA).isLleno()) {
            r = true;
        }
        return r;
    }
    /**
     * Si solo dos sensores estan ACTIVADOS devuelve TRUE
     * @return 
     */
    public boolean colaMedia() {
        boolean r = false;
        if (!semaforo.getSensoresProx().get(Semaforo.SENSOR_LEJOS).isLleno() 
                && semaforo.getSensoresProx().get(Semaforo.SENSOR_MEDIO).isLleno() 
                && semaforo.getSensoresProx().get(Semaforo.SENSOR_CERCA).isLleno()) {
            r = true;
        } else if (semaforo.getSensoresProx().get(Semaforo.SENSOR_LEJOS).isLleno() 
                && !semaforo.getSensoresProx().get(Semaforo.SENSOR_MEDIO).isLleno() 
                && semaforo.getSensoresProx().get(Semaforo.SENSOR_CERCA).isLleno()) {
            r = true;
        } else if (semaforo.getSensoresProx().get(Semaforo.SENSOR_LEJOS).isLleno() 
                && semaforo.getSensoresProx().get(Semaforo.SENSOR_MEDIO).isLleno() 
                && !semaforo.getSensoresProx().get(Semaforo.SENSOR_CERCA).isLleno()) {
            r = true;
        }
        return r;
    }
    /**
     * Si solo un sensor esta ACTIVADO devuelve TRUE
     * @return 
     */
    public boolean colaCorta() {
        boolean r = false;
        if (!semaforo.getSensoresProx().get(Semaforo.SENSOR_LEJOS).isLleno() 
                && !semaforo.getSensoresProx().get(Semaforo.SENSOR_MEDIO).isLleno() 
                && semaforo.getSensoresProx().get(Semaforo.SENSOR_CERCA).isLleno()) {
            r = true;
        } else if (!semaforo.getSensoresProx().get(Semaforo.SENSOR_LEJOS).isLleno() 
                && semaforo.getSensoresProx().get(Semaforo.SENSOR_MEDIO).isLleno() 
                && !semaforo.getSensoresProx().get(Semaforo.SENSOR_CERCA).isLleno()) {
            r = true;
        } else if (semaforo.getSensoresProx().get(Semaforo.SENSOR_LEJOS).isLleno() 
                && !semaforo.getSensoresProx().get(Semaforo.SENSOR_MEDIO).isLleno() 
                && !semaforo.getSensoresProx().get(Semaforo.SENSOR_CERCA).isLleno()) {
            r = true;
        }
        return r;
    }
}
