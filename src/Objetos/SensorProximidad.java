package Objetos;

public class SensorProximidad {
    
    private boolean lleno;

    public SensorProximidad() {
        lleno = false;
    }

    public boolean isLleno() {
        return lleno;
    }

    public void setLleno(boolean lleno) {
        this.lleno = lleno;
    }
}
