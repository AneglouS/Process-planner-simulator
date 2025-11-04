package ProyectoSO;
import java.util.LinkedList;
import java.util.Queue;

public class asignarMemoria {
    private Queue<Proceso> colaMemoria;
    private int limiteMemoria;
    private int memoriaUsada;

    public asignarMemoria(int limiteMemoria) {
        this.limiteMemoria = limiteMemoria;
        this.colaMemoria = new LinkedList<>();
        this.memoriaUsada = 0;
    }

    public boolean agregarProceso(Proceso proceso) {
        if (memoriaUsada + proceso.getTamaño() <= limiteMemoria) {
            colaMemoria.offer(proceso);
            memoriaUsada += proceso.getTamaño();
            System.out.println("-- Proceso " + proceso.getPid() + " cargado en memoria (" + 
                             proceso.getTamaño() + " unidades)");
            return true;
        } else {
            System.out.println("x No hay espacio suficiente en memoria para " + proceso.getPid());
            return false;
        }
    }

    public Proceso removerProceso() {
        Proceso proceso = colaMemoria.poll();
        if (proceso != null) {
            memoriaUsada -= proceso.getTamaño();
            System.out.println("-- Proceso " + proceso.getPid() + " removido de memoria");
        }
        return proceso;
    }

    public Proceso verSiguiente() {
        return colaMemoria.peek();
    }

    public boolean estaVacia() {
        return colaMemoria.isEmpty();
    }

    public int getMemoriaUsada() {
        return memoriaUsada;
    }

    public int getMemoriaDisponible() {
        return limiteMemoria - memoriaUsada;
    }

    public int getLimiteMemoria() {
        return limiteMemoria;
    }

    public int getCantidadProcesos() {
        return colaMemoria.size();
    }

    public void mostrarEstado() {
        System.out.println("\n╔════════════════════════════════════════════════════════════════╗");
        System.out.println("║                    ESTADO DE LA MEMORIA                        ║");
        System.out.println("╠════════════════════════════════════════════════════════════════╣");
        System.out.printf("║ Límite: %d | Usada: %d | Disponible: %d | Procesos: %d%n",
                limiteMemoria, memoriaUsada, getMemoriaDisponible(), colaMemoria.size());
        System.out.println("╠════════════════════════════════════════════════════════════════╣");
        
        if (colaMemoria.isEmpty()) {
            System.out.println("║ (vacía)                                                        ║");
        } else {
            System.out.printf("║ %-6s %-20s %-8s %-10s %-10s%n", 
                    "PID", "Nombre", "Tamaño", "Exec.Total", "Exec.Rest.");
            System.out.println("╠════════════════════════════════════════════════════════════════╣");
            for (Proceso p : colaMemoria) {
                System.out.printf("║ %-6s %-20s %-8d %-10d %-10d%n",
                        p.getPid(), p.getNombre(), p.getTamaño(), 
                        p.getTiempoEjecucionTotal(), p.getTiempoEjecucionRestante());
            }
        }
        System.out.println("╚════════════════════════════════════════════════════════════════╝\n");
    }
}
