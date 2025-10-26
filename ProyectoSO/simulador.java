package ProyectoSO;
import java.util.*;

public class simulador {
    private static Scanner scanner = new Scanner(System.in);
    private static planificarProcesos planificador;
    private static asignarMemoria gestorMemoria;
    private static List<Proceso> todosLosProcesos = new ArrayList<>();
    private static calculadoraTiemposPromedio calculadora;

    public static void main(String[] args) {
        System.out.println("╔════════════════════════════════════════════════════════════════╗");
        System.out.println("║     SIMULADOR DE PLANIFICADOR DE PROCESOS - ROUND ROBIN       ║");
        System.out.println("╚════════════════════════════════════════════════════════════════╝\n");

        // Configuración inicial
        configurarSimulador();

        boolean ejecutando = true;
        while (ejecutando) {
            mostrarMenu();
            String opcion = scanner.nextLine().trim();

            switch (opcion) {
                case "1":
                    crearProceso();
                    break;
                case "2":
                    listarProcesosCreados();
                    break;
                case "3":
                    eliminarProcesoDeListos();
                    break;
                case "4":
                    cargarProcesosAMemoria();
                    break;
                case "5":
                    gestorMemoria.mostrarEstado();
                    break;
                case "6":
                    planificador.mostrarCola();
                    break;
                case "7":
                    ejecutarSimulacion();
                    break;
                case "8":
                    mostrarEstadisticas();
                    break;
                case "0":
                    System.out.println("\n✓ Simulador finalizado.");
                    ejecutando = false;
                    break;
                default:
                    System.out.println("⚠ Opción no válida. Intenta de nuevo.\n");
            }
        }
        scanner.close();
    }

    private static void configurarSimulador() {
        System.out.println("═══════════════════ CONFIGURACIÓN INICIAL ═══════════════════");
        
        // Configurar límite de memoria
        System.out.print("Ingresa el límite de memoria (unidades): ");
        int limiteMemoria = leerEnteroPositivo();
        gestorMemoria = new asignarMemoria(limiteMemoria);

        // Configurar quantum
        System.out.print("Ingresa el quantum para Round Robin (ms): ");
        int quantum = leerEnteroPositivo();
        planificador = new planificarProcesos(quantum);

        calculadora = new calculadoraTiemposPromedio();

        System.out.println("\n✓ Configuración completada:");
        System.out.println("  - Límite de memoria: " + limiteMemoria + " unidades");
        System.out.println("  - Quantum: " + quantum + " ms\n");
    }

    private static void mostrarMenu() {
        System.out.println("╔════════════════════════════════════════════════════════════════╗");
        System.out.println("║                         MENÚ PRINCIPAL                         ║");
        System.out.println("╠════════════════════════════════════════════════════════════════╣");
        System.out.println("║ 1. Crear nuevo proceso                                         ║");
        System.out.println("║ 2. Listar todos los procesos creados                           ║");
        System.out.println("║ 3. Eliminar proceso de cola de listos                          ║");
        System.out.println("║ 4. Cargar procesos de listos a memoria (FIFO)                  ║");
        System.out.println("║ 5. Mostrar estado de la memoria                                ║");
        System.out.println("║ 6. Mostrar cola de procesos listos                             ║");
        System.out.println("║ 7. Ejecutar simulación (Round Robin)                           ║");
        System.out.println("║ 8. Mostrar estadísticas de procesos completados                ║");
        System.out.println("║ 0. Salir                                                       ║");
        System.out.println("╚════════════════════════════════════════════════════════════════╝");
        System.out.print("Selecciona una opción: ");
    }

    private static void crearProceso() {
        System.out.println("\n═══════════════════ CREAR NUEVO PROCESO ═══════════════════");
        
        // Generar PID automático
        String pid = generarPidAutomatico();
        System.out.println("PID asignado: " + pid);

        System.out.print("Nombre del proceso: ");
        String nombre = scanner.nextLine().trim();
        if (nombre.isEmpty()) nombre = "Proceso " + pid;

        System.out.print("Tamaño del proceso (unidades de memoria): ");
        int tamaño = leerEnteroPositivo();

        System.out.print("Tiempo de ejecución (ms): ");
        int tiempoEjecucion = leerEnteroPositivo();

        System.out.print("Prioridad: ");
        int prioridad = leerEnteroPositivo();

        System.out.print("Tiempo de llegada (ms, 0 por defecto): ");
        String llegadaStr = scanner.nextLine().trim();
        int tiempoLlegada = llegadaStr.isEmpty() ? 0 : Integer.parseInt(llegadaStr);

        Proceso nuevoProceso = new Proceso(pid, nombre, tamaño, tiempoEjecucion, prioridad, tiempoLlegada);
        todosLosProcesos.add(nuevoProceso);
        
        // Agregar automáticamente a cola de listos
        planificador.agregarProceso(nuevoProceso);

        System.out.println("✓ Proceso " + pid + " creado y agregado a cola de listos\n");
    }

    private static void listarProcesosCreados() {
        System.out.println("\n═══════════════ TODOS LOS PROCESOS CREADOS ═══════════════");
        if (todosLosProcesos.isEmpty()) {
            System.out.println("(No hay procesos creados)\n");
            return;
        }

        System.out.printf("%-6s %-20s %-8s %-10s %-10s %-8s %-10s%n", 
                "PID", "Nombre", "Tamaño", "Exec.Total", "Exec.Rest.", "Prioridad", "Estado");
        System.out.println("─────────────────────────────────────────────────────────────────────────");
        
        for (Proceso p : todosLosProcesos) {
            String estado = p.isCompletado() ? "Completado" : 
                           (p.isIniciado() ? "En proceso" : "Listo");
            System.out.printf("%-6s %-20s %-8d %-10d %-10d %-8d %-10s%n",
                    p.getPid(), p.getNombre(), p.getTamaño(), 
                    p.getTiempoEjecucionTotal(), p.getTiempoEjecucionRestante(),
                    p.getPrioridad(), estado);
        }
        System.out.println();
    }

    private static void eliminarProcesoDeListos() {
        System.out.print("\nIngresa el PID del proceso a eliminar: ");
        String pid = scanner.nextLine().trim();
        planificador.eliminarProceso(pid);
    }

    private static void cargarProcesosAMemoria() {
        System.out.println("\n═══════════════ CARGAR PROCESOS A MEMORIA (FIFO) ═══════════════");
        
        if (planificador.estaVacia()) {
            System.out.println("⚠ No hay procesos en la cola de listos\n");
            return;
        }

        int procesosMovidos = 0;
        while (!planificador.estaVacia()) {
            Proceso proceso = planificador.obtenerSiguiente();
            if (proceso != null) {
                if (gestorMemoria.agregarProceso(proceso)) {
                    procesosMovidos++;
                } else {
                    // Si no hay espacio, devolver el proceso a la cola de listos
                    planificador.agregarProceso(proceso);
                    System.out.println("⚠ Memoria llena. No se pueden cargar más procesos.");
                    break;
                }
            }
        }

        System.out.println("\n✓ " + procesosMovidos + " proceso(s) cargado(s) en memoria");
        gestorMemoria.mostrarEstado();
    }

    private static void ejecutarSimulacion() {
        System.out.println("\n═══════════════════ EJECUTAR SIMULACIÓN ═══════════════════");
        
        if (gestorMemoria.estaVacia()) {
            System.out.println("⚠ No hay procesos en memoria.");
            System.out.println("   Primero debes cargar procesos de la cola de listos a memoria.\n");
            return;
        }

        planificador.ejecutarRoundRobin(gestorMemoria);
        // Imprimir automáticamente los promedios al finalizar
        mostrarEstadisticas();
    }

    private static void mostrarEstadisticas() {
        System.out.println("\n╔════════════════════════════════════════════════════════════════╗");
        System.out.println("║              ESTADÍSTICAS DE PROCESOS COMPLETADOS              ║");
        System.out.println("╠════════════════════════════════════════════════════════════════╣");

        List<Proceso> procesosCompletados = new ArrayList<>();
        for (Proceso p : todosLosProcesos) {
            if (p.isCompletado()) {
                procesosCompletados.add(p);
            }
        }

        if (procesosCompletados.isEmpty()) {
            System.out.println("║ No hay procesos completados aún                                ║");
            System.out.println("╚════════════════════════════════════════════════════════════════╝\n");
            return;
        }

        calculadora.calcularEstadisticas(procesosCompletados);

        System.out.printf("║ %-6s %-12s %-12s %-12s %-12s%n", 
                "PID", "T.Respuesta", "T.Espera", "T.Retorno", "T.Ejecución");
        System.out.println("╠════════════════════════════════════════════════════════════════╣");

        for (Proceso p : procesosCompletados) {
            System.out.printf("║ %-6s %-12d %-12d %-12d %-12d%n",
                    p.getPid(), 
                    p.getTiempoRespuesta(),
                    p.getTiempoEspera(),
                    p.getTiempoRetorno(),
                    p.getTiempoEjecucionTotal());
        }

    System.out.println("╠════════════════════════════════════════════════════════════════╣");
    System.out.printf("║ PROMEDIOS:                                                     ║%n");
    System.out.printf("║   Tiempo de espera:    %.2f ms                                 ║%n", 
        calculadora.getPromedioEspera());
    System.out.printf("║   Tiempo de respuesta: %.2f ms                                 ║%n", 
        calculadora.getPromedioRespuesta());
    System.out.printf("║   Tiempo de ejecución: %.2f ms                                 ║%n", 
        calculadora.getPromedioEjecucion());
        System.out.println("╚════════════════════════════════════════════════════════════════╝\n");
    }

    private static String generarPidAutomatico() {
        int maxNum = 0;
        for (Proceso p : todosLosProcesos) {
            String pid = p.getPid();
            if (pid.startsWith("P")) {
                try {
                    int num = Integer.parseInt(pid.substring(1));
                    if (num > maxNum) maxNum = num;
                } catch (NumberFormatException e) {
                    // Ignorar PIDs que no sigan el formato P#
                }
            }
        }
        return "P" + (maxNum + 1);
    }

    private static int leerEnteroPositivo() {
        while (true) {
            try {
                String input = scanner.nextLine().trim();
                int valor = Integer.parseInt(input);
                if (valor > 0) {
                    return valor;
                }
                System.out.print("⚠ Debe ser un número positivo. Intenta de nuevo: ");
            } catch (NumberFormatException e) {
                System.out.print("⚠ Entrada inválida. Ingresa un número: ");
            }
        }
    }
}
