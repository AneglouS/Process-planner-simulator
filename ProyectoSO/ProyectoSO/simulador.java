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
                    ejecutarSimulacion();
                    break;
                case "5":
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
        
        System.out.print("Ingresa el límite de memoria (unidades): ");
        int limiteMemoria = leerEnteroPositivo();
        gestorMemoria = new asignarMemoria(limiteMemoria);

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
    System.out.println("║ 2. Listar los procesos                                         ║");
    System.out.println("║ 3. Eliminar un proceso                                         ║");
    System.out.println("║ 4. Ejecutar simulación                                         ║");
    System.out.println("║ 5. Salir                                                       ║");
    System.out.println("╚════════════════════════════════════════════════════════════════╝");
    System.out.print("Selecciona una opción: ");
    }

    private static void crearProceso() {
        System.out.println("\n═══════════════════ CREAR NUEVO PROCESO ═══════════════════");
        
        String pid = generarPidAutomatico();
        System.out.println("PID asignado: " + pid);

        System.out.print("Nombre del proceso: ");
        String nombre = scanner.nextLine().trim();
        if (nombre.isEmpty()) nombre = "Proceso " + pid;

        System.out.print("Tamaño del proceso (unidades de memoria): ");
        int tamaño = leerEnteroPositivo();

        System.out.print("Tiempo de ejecución (ms): ");
        int tiempoEjecucion = leerEnteroPositivo();

        System.out.print("Tiempo de llegada (ms, 0 por defecto): ");
        String llegadaStr = scanner.nextLine().trim();
        int tiempoLlegada = llegadaStr.isEmpty() ? 0 : Integer.parseInt(llegadaStr);

        Proceso nuevoProceso = new Proceso(pid, nombre, tamaño, tiempoEjecucion, tiempoLlegada);
        todosLosProcesos.add(nuevoProceso);

        System.out.println("✓ Proceso " + pid + " creado (llegará en t=" + tiempoLlegada + " ms)\n");
    }

    private static void listarProcesosCreados() {
        System.out.println("\n═══════════════ TODOS LOS PROCESOS CREADOS ═══════════════");
        if (todosLosProcesos.isEmpty()) {
            System.out.println("(No hay procesos creados)\n");
            return;
        }

        System.out.printf("%-6s %-20s %-8s %-10s %-10s %-10s%n", 
                "PID", "Nombre", "Tamaño", "Exec.Total", "Exec.Rest.", "Estado");
        System.out.println("─────────────────────────────────────────────────────────────────────");
        
        for (Proceso p : todosLosProcesos) {
            String estado = p.isCompletado() ? "Completado" : 
                           (p.isIniciado() ? "En proceso" : "Listo");
            System.out.printf("%-6s %-20s %-8d %-10d %-10d %-10s%n",
                    p.getPid(), p.getNombre(), p.getTamaño(), 
                    p.getTiempoEjecucionTotal(), p.getTiempoEjecucionRestante(),
                    estado);
        }
        System.out.println();
    }

    private static void eliminarProcesoDeListos() {
        System.out.print("\nIngresa el PID del proceso a eliminar: ");
        String pid = scanner.nextLine().trim();
        
        boolean eliminado = todosLosProcesos.removeIf(p -> p.getPid().equals(pid));
        if (eliminado) {
            System.out.println("✓ Proceso " + pid + " eliminado\n");
        } else {
            System.out.println("✗ Proceso " + pid + " no encontrado\n");
        }
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

        List<Proceso> procesosPorLlegar = new ArrayList<>(todosLosProcesos);
        procesosPorLlegar.sort(Comparator.comparingInt(Proceso::getTiempoLlegada));

        Queue<Proceso> colaListos = new LinkedList<>();
        Queue<Proceso> colaMemoria = new LinkedList<>();
    int quantum = planificador == null ? 1 : planificador.getQuantum();
        int tiempoActual = 0;
        int procesosAgregados = 0;

        System.out.println("Simulación iniciada. Cada ms será 1 segundo real.");

        // Lista para almacenar procesos que regresan del quantum al final del ciclo anterior
        List<Proceso> procesosQueRegresanDelAnterior = new ArrayList<>();
        
        while (procesosAgregados < procesosPorLlegar.size() || !colaListos.isEmpty() || !colaMemoria.isEmpty() || !procesosQueRegresanDelAnterior.isEmpty()) {

            
            // 1. PRIMERO: Agregar procesos recién llegados (prioridad FIFO por tiempo de llegada)
            while (procesosAgregados < procesosPorLlegar.size() && procesosPorLlegar.get(procesosAgregados).getTiempoLlegada() <= tiempoActual) {
                Proceso p = procesosPorLlegar.get(procesosAgregados);
                colaListos.offer(p);
                System.out.println("[" + tiempoActual + " ms] Proceso " + p.getPid() + " llegó y se agregó a cola de listos.");
                procesosAgregados++;
            }
            
            // 2. SEGUNDO: Agregar procesos que regresaron del quantum en el ciclo anterior
            for (Proceso p : procesosQueRegresanDelAnterior) {
                colaListos.offer(p);
                System.out.println("[" + tiempoActual + " ms] Proceso " + p.getPid() + " regresó del quantum y se agregó a cola de listos.");
            }
            procesosQueRegresanDelAnterior.clear();

            // 3. Cargar procesos de listos a memoria (FIFO estricto)
            while (!colaListos.isEmpty() && gestorMemoria.getMemoriaDisponible() >= colaListos.peek().getTamaño()) {
                Proceso p = colaListos.poll();
                gestorMemoria.agregarProceso(p);
                colaMemoria.offer(p);
                System.out.println("[" + tiempoActual + " ms] Proceso " + p.getPid() + " cargado en memoria. Memoria disponible: "
                        + gestorMemoria.getMemoriaDisponible() + "/" + gestorMemoria.getLimiteMemoria());
            }

            if (!colaMemoria.isEmpty()) {
                Proceso procesoActual = colaMemoria.poll();
                // Primero calcular el tiempo acumulado ANTES de actualizar la última subida
                procesoActual.actualizarTiempoEjecutadoAnterior();
                procesoActual.setTiempoUltimaSubidaCPU(tiempoActual);
                if (!procesoActual.isIniciado()) {
                    procesoActual.setTiempoInicioCPU(tiempoActual);
                }
                System.out.println("[" + tiempoActual + " ms] Ejecutando proceso " + procesoActual.getPid() + " (restante: " + procesoActual.getTiempoEjecucionRestante() + " ms)");
                int tiempoEjecutado = Math.min(quantum, procesoActual.getTiempoEjecucionRestante());
                
                // Ejecutar gradualmente, actualizando el tiempo restante cada ms
                for (int i = 0; i < tiempoEjecutado; i++) {
                    procesoActual.setTiempoEjecucionRestante(procesoActual.getTiempoEjecucionRestante() - 1);
                    procesoActual.setTiempoEjecucionAcumulado(procesoActual.getTiempoEjecucionAcumulado() + 1);
                    try { Thread.sleep(1000); } catch (InterruptedException e) { }
                    tiempoActual++;
                    mostrarEstadoColas(colaListos, colaMemoria, procesoActual, tiempoActual);
                }
                if (procesoActual.getTiempoEjecucionRestante() <= 0) {
                    procesoActual.setTiempoFinalizacion(tiempoActual);
                    gestorMemoria.removerProceso();
                    
                    // Asegurar que el proceso en todosLosProcesos también se actualice
                    for (Proceso p : todosLosProcesos) {
                        if (p.getPid().equals(procesoActual.getPid())) {
                            p.setTiempoEjecucionRestante(0);
                            p.setTiempoFinalizacion(tiempoActual);
                            p.setTiempoEjecucionAcumulado(procesoActual.getTiempoEjecucionAcumulado());
                            break;
                        }
                    }
                    
                    System.out.println("[" + tiempoActual + " ms] Proceso " + procesoActual.getPid() + " COMPLETADO. Memoria disponible: "
                            + gestorMemoria.getMemoriaDisponible() + "/" + gestorMemoria.getLimiteMemoria());
                } else {
                    // El proceso vuelve a la cola de listos - guardarlo para el SIGUIENTE ciclo
                    procesosQueRegresanDelAnterior.add(procesoActual);
                    gestorMemoria.removerProceso();
                    System.out.println("[" + tiempoActual + " ms] Quantum terminado. Proceso " + procesoActual.getPid() + " regresa a cola de listos. Memoria disponible: "
                            + gestorMemoria.getMemoriaDisponible() + "/" + gestorMemoria.getLimiteMemoria());
                }
            } else {
                try { Thread.sleep(1000); } catch (InterruptedException e) { }
                tiempoActual++;
                mostrarEstadoColas(colaListos, colaMemoria, null, tiempoActual);
            }

        }

        System.out.println("\nSimulación finalizada.");
        mostrarEstadisticas();
    }

    private static void mostrarEstadoColas(Queue<Proceso> colaListos, Queue<Proceso> colaMemoria, Proceso procesoActual, int tiempoActual) {
        System.out.println("─────────────────────────────────────────────────────────────");
        System.out.println("Tiempo actual: " + tiempoActual + " ms");
        System.out.println("Memoria: usada " + (gestorMemoria.getLimiteMemoria() - gestorMemoria.getMemoriaDisponible())
                + "/" + gestorMemoria.getLimiteMemoria() + ", disponible " + gestorMemoria.getMemoriaDisponible());
        System.out.print("Cola de listos: ");
        for (Proceso p : colaListos) {
            System.out.print(p.getPid() + "(" + p.getTiempoEjecucionRestante() + ") ");
        }
        System.out.println();
        System.out.print("Cola de memoria: ");
        for (Proceso p : colaMemoria) {
            System.out.print(p.getPid() + "(" + p.getTiempoEjecucionRestante() + ") ");
        }
        System.out.println();
        if (procesoActual != null) {
            System.out.println("Ejecutando: " + procesoActual.getPid() + " (restante: " + procesoActual.getTiempoEjecucionRestante() + ")");
        }
        System.out.println("─────────────────────────────────────────────────────────────");
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
                System.out.print("Alerta: Debe ser un número positivo. Intenta de nuevo: ");
            } catch (NumberFormatException e) {
                System.out.print("Alerta: Entrada inválida. Ingresa un número: ");
            }
        }
    }
}
