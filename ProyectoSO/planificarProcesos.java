package ProyectoSO;
import java.util.LinkedList;
import java.util.Queue;

public class planificarProcesos {
    private Queue<Proceso> colaProcesosListos;
    private int quantum;
    private int tiempoActual;

    public planificarProcesos(int quantum) {
        this.quantum = quantum;
        this.colaProcesosListos = new LinkedList<>();
        this.tiempoActual = 0;
    }

    // Agrega un proceso a la cola de listos
    public void agregarProceso(Proceso proceso) {
        colaProcesosListos.offer(proceso);
        System.out.println("✓ Proceso " + proceso.getPid() + " agregado a cola de listos");
        mostrarCola();
    }

    // Ejecuta Round Robin en CPU
    public void ejecutarRoundRobin(asignarMemoria memoria) {
        if (memoria.estaVacia()) {
            System.out.println("\n⚠ No hay procesos en memoria para ejecutar.");
            return;
        }

        System.out.println("\n╔════════════════════════════════════════════════════════════════╗");
        System.out.println("║              INICIANDO EJECUCIÓN ROUND ROBIN                   ║");
        System.out.println("║              Quantum: " + quantum + " ms                                        ║");
        System.out.println("╚════════════════════════════════════════════════════════════════╝\n");

        Queue<Proceso> colaCPU = new LinkedList<>();
        
        // Pasar todos los procesos de memoria a cola de CPU
        while (!memoria.estaVacia()) {
            Proceso p = memoria.removerProceso();
            if (p != null) {
                colaCPU.offer(p);
            }
        }

        // Ejecutar Round Robin
        while (!colaCPU.isEmpty()) {
            Proceso procesoActual = colaCPU.poll();
            
            // Marcar tiempo de inicio en CPU si es la primera vez
            if (!procesoActual.isIniciado()) {
                procesoActual.setTiempoInicioCPU(tiempoActual);
            }

            System.out.println("⏰ Tiempo: " + tiempoActual + " ms");
            System.out.println("▶ Ejecutando proceso: " + procesoActual.getPid() + 
                             " (" + procesoActual.getNombre() + ")");
            System.out.println("  Tiempo restante antes: " + procesoActual.getTiempoEjecucionRestante() + " ms");

            // Ejecutar por quantum o hasta terminar
            int tiempoEjecutado = Math.min(quantum, procesoActual.getTiempoEjecucionRestante());
            procesoActual.ejecutar(tiempoEjecutado);
            tiempoActual += tiempoEjecutado;

            System.out.println("  Tiempo ejecutado: " + tiempoEjecutado + " ms");
            System.out.println("  Tiempo restante después: " + procesoActual.getTiempoEjecucionRestante() + " ms");

            // Verificar si el proceso terminó
            if (procesoActual.isCompletado()) {
                procesoActual.setTiempoFinalizacion(tiempoActual);
                System.out.println("✓ Proceso " + procesoActual.getPid() + " COMPLETADO");
                System.out.println("  - Tiempo de respuesta: " + procesoActual.getTiempoRespuesta() + " ms");
                System.out.println("  - Tiempo de espera: " + procesoActual.getTiempoEspera() + " ms");
                System.out.println("  - Tiempo de retorno: " + procesoActual.getTiempoRetorno() + " ms");
            } else {
                System.out.println("◀ Proceso " + procesoActual.getPid() + " devuelto a cola (no terminado)");
                colaCPU.offer(procesoActual); // Volver a encolar
            }
            System.out.println();
        }

        System.out.println("╔════════════════════════════════════════════════════════════════╗");
        System.out.println("║           EJECUCIÓN ROUND ROBIN COMPLETADA                     ║");
        System.out.println("║           Tiempo total: " + tiempoActual + " ms                                ║");
        System.out.println("╚════════════════════════════════════════════════════════════════╝\n");
    }

    public void mostrarCola() {
        System.out.println("\n╔════════════════════════════════════════════════════════════════╗");
        System.out.println("║                 COLA DE PROCESOS LISTOS                        ║");
        System.out.println("╠════════════════════════════════════════════════════════════════╣");
        
        if (colaProcesosListos.isEmpty()) {
            System.out.println("║ (vacía)                                                        ║");
        } else {
            System.out.printf("║ %-6s %-20s %-8s %-10s %-10s%n", 
                    "PID", "Nombre", "Tamaño", "Exec.Total", "Prioridad");
            System.out.println("╠════════════════════════════════════════════════════════════════╣");
            for (Proceso p : colaProcesosListos) {
                System.out.printf("║ %-6s %-20s %-8d %-10d %-10d%n",
                        p.getPid(), p.getNombre(), p.getTamaño(), 
                        p.getTiempoEjecucionTotal(), p.getPrioridad());
            }
        }
        System.out.println("╚════════════════════════════════════════════════════════════════╝\n");
    }

    public boolean eliminarProceso(String pid) {
        boolean removido = colaProcesosListos.removeIf(p -> p.getPid().equals(pid));
        if (removido) {
            System.out.println("✓ Proceso " + pid + " eliminado de cola de listos");
            mostrarCola();
        } else {
            System.out.println("✗ Proceso " + pid + " no encontrado en cola de listos");
        }
        return removido;
    }

    public Proceso obtenerSiguiente() {
        return colaProcesosListos.poll();
    }

    public boolean estaVacia() {
        return colaProcesosListos.isEmpty();
    }

    public int getCantidadProcesos() {
        return colaProcesosListos.size();
    }

    public int getTiempoActual() {
        return tiempoActual;
    }

    public void resetTiempoActual() {
        this.tiempoActual = 0;
    }
}
