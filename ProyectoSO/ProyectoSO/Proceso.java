package ProyectoSO;
public class Proceso {
    private String pid;
    private String nombre;
    private int tamaño;
    private int tiempoEjecucionTotal; // ms
    private int tiempoEjecucionRestante; // ms
    private int prioridad;
    private int tiempoLlegada; // ms
    private int tiempoInicioCPU; // cuando empieza en CPU por primera vez
    private int tiempoFinalizacion; // cuando termina
    private boolean iniciado; // si ya ha estado en CPU

    public Proceso(String pid, String nombre, int tamaño, int tiempoEjecucion, int prioridad, int tiempoLlegada) {
        this.pid = pid;
        this.nombre = nombre;
        this.tamaño = tamaño;
        this.tiempoEjecucionTotal = tiempoEjecucion;
        this.tiempoEjecucionRestante = tiempoEjecucion;
        this.prioridad = prioridad;
        this.tiempoLlegada = tiempoLlegada;
        this.tiempoInicioCPU = -1;
        this.tiempoFinalizacion = -1;
        this.iniciado = false;
    }

    // Getters
    public String getPid() { return pid; }
    public String getNombre() { return nombre; }
    public int getTamaño() { return tamaño; }
    public int getTiempoEjecucionTotal() { return tiempoEjecucionTotal; }
    public int getTiempoEjecucionRestante() { return tiempoEjecucionRestante; }
    public int getPrioridad() { return prioridad; }
    public int getTiempoLlegada() { return tiempoLlegada; }
    public int getTiempoInicioCPU() { return tiempoInicioCPU; }
    public int getTiempoFinalizacion() { return tiempoFinalizacion; }
    public boolean isIniciado() { return iniciado; }
    public boolean isCompletado() { return tiempoEjecucionRestante <= 0; }

    // Setters
    public void setTiempoEjecucionRestante(int tiempo) { this.tiempoEjecucionRestante = tiempo; }
    public void setTiempoInicioCPU(int tiempo) { 
        if (tiempoInicioCPU == -1) {
            this.tiempoInicioCPU = tiempo; 
        }
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
    }

    // Métodos para calcular tiempos
    public int getTiempoRespuesta() {
        // Tiempo desde llegada hasta primer ejecución en CPU
        if (tiempoInicioCPU == -1) return -1;
        return tiempoInicioCPU - tiempoLlegada;
    }

    public int getTiempoRetorno() {
        // Tiempo desde llegada hasta finalización
        if (tiempoFinalizacion == -1) return -1;
        return tiempoFinalizacion - tiempoLlegada;
    }

    public int getTiempoEspera() {
        // Tiempo de retorno - tiempo de ejecución
        if (tiempoFinalizacion == -1) return -1;
        return getTiempoRetorno() - tiempoEjecucionTotal;
    }

    @Override
    public String toString() {
        return String.format("%-6s %-20s %-8d %-10d %-10d %-8d %-12d",
                pid, nombre, tamaño, tiempoEjecucionTotal, tiempoEjecucionRestante, prioridad, tiempoLlegada);
    }

    public String toStringDetallado() {
        return String.format("PID: %s | Nombre: %s | Tamaño: %d | Ejecución: %d/%d | Prioridad: %d",
                pid, nombre, tamaño, tiempoEjecucionRestante, tiempoEjecucionTotal, prioridad);
    }
}
