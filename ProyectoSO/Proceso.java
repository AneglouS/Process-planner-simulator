package ProyectoSO;
public class Proceso {
    private String pid;
    private String nombre;
    private int tamaño;
    private int tiempoEjecucionTotal;
    private int tiempoEjecucionRestante;
    private int tiempoLlegada;
    private int tiempoInicioCPU;
    private int tiempoUltimaSubidaCPU;
    private int tiempoFinalizacion;
    private int tiempoEjecucionAcumulado;
    private boolean iniciado;

    public Proceso(String pid, String nombre, int tamaño, int tiempoEjecucion, int tiempoLlegada) {
        this.pid = pid;
        this.nombre = nombre;
        this.tamaño = tamaño;
        this.tiempoEjecucionTotal = tiempoEjecucion;
        this.tiempoEjecucionRestante = tiempoEjecucion;
        this.tiempoLlegada = tiempoLlegada;
        this.tiempoInicioCPU = -1;
        this.tiempoUltimaSubidaCPU = -1;
        this.tiempoFinalizacion = -1;
        this.tiempoEjecucionAcumulado = 0;
        this.iniciado = false;
    }

    public String getPid() { return pid; }
    public String getNombre() { return nombre; }
    public int getTamaño() { return tamaño; }
    public int getTiempoEjecucionTotal() { return tiempoEjecucionTotal; }
    public int getTiempoEjecucionRestante() { return tiempoEjecucionRestante; }
    public int getTiempoLlegada() { return tiempoLlegada; }
    public int getTiempoInicioCPU() { return tiempoInicioCPU; }
    public int getTiempoUltimaSubidaCPU() { return tiempoUltimaSubidaCPU; }
    public int getTiempoFinalizacion() { return tiempoFinalizacion; }
    public int getTiempoEjecucionAcumulado() { return tiempoEjecucionAcumulado; }
    public boolean isIniciado() { return iniciado; }
    public boolean isCompletado() { return tiempoEjecucionRestante <= 0; }

    public void setTiempoEjecucionRestante(int tiempo) { this.tiempoEjecucionRestante = tiempo; }
    public void setTiempoInicioCPU(int tiempo) { 
        if (tiempoInicioCPU == -1) {
            this.tiempoInicioCPU = tiempo; 
        }
    }
    public void setTiempoUltimaSubidaCPU(int tiempo) { 
        this.tiempoUltimaSubidaCPU = tiempo; 
    }
    public void setTiempoFinalizacion(int tiempo) { this.tiempoFinalizacion = tiempo; }
    public void setIniciado(boolean iniciado) { this.iniciado = iniciado; }

    public void ejecutar(int quantum) {
        if (!iniciado) {
            iniciado = true;
        }
        tiempoEjecucionRestante -= quantum;
        if (tiempoEjecucionRestante < 0) {
            tiempoEjecucionRestante = 0;
        }
        tiempoEjecucionAcumulado += Math.min(quantum, tiempoEjecucionRestante + quantum);
    }

    public int getTiempoRespuesta() {
        if (tiempoInicioCPU == -1) return -1;
        return tiempoInicioCPU - tiempoLlegada;
    }

    public int getTiempoEjecucion() {
        if (tiempoFinalizacion == -1) return -1;
        return tiempoFinalizacion - tiempoLlegada;
    }

    public int getTiempoEspera() {
        if (tiempoUltimaSubidaCPU == -1) return -1;
        return tiempoUltimaSubidaCPU - tiempoLlegada - tiempoEjecucionAcumulado;
    }

    public int getTiempoRetorno() {
        return getTiempoEjecucion();
    }

    @Override
    public String toString() {
        return String.format("%-6s %-20s %-8d %-10d %-10d %-12d",
                pid, nombre, tamaño, tiempoEjecucionTotal, tiempoEjecucionRestante, tiempoLlegada);
    }

    public String toStringDetallado() {
        return String.format("PID: %s | Nombre: %s | Tamaño: %d | Ejecución: %d/%d",
                pid, nombre, tamaño, tiempoEjecucionRestante, tiempoEjecucionTotal);
    }
}