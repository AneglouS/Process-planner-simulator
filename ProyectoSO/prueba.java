import java.util.*;
import java.util.regex.*;

public class prueba {

    // Clase interna simple que representa un proceso
    static class Process {
        private String pid;
        private String name;
        private int size;
        private int execTimeMs;
        private int priority;
        private int arrivalTimeMs;

        public Process(String pid, String name, int size, int execTimeMs, int priority, int arrivalTimeMs) {
            this.pid = pid;
            this.name = name;
            this.size = size;
            this.execTimeMs = execTimeMs;
            this.priority = priority;
            this.arrivalTimeMs = arrivalTimeMs;
        }

        public String getPid() { return pid; }

        @Override
        public String toString() {
            return String.format("%-6s %-20s %-6d %-12d %-8d %-12d",
                    pid, name, size, execTimeMs, priority, arrivalTimeMs);
        }
    }

    private static final Scanner SC = new Scanner(System.in);
    private static final List<Process> processes = new ArrayList<>();

    public static void main(String[] args) {
        while (true) {
            System.out.println("Simulador planificador de procesos");
            System.out.println("1) Agregar proceso");
            System.out.println("2) Listar procesos");
            System.out.println("3) Eliminar proceso por Id");
            System.out.println("4) Limpiar todos los procesos");
            System.out.println("0) Salir");
            System.out.print("Selecciona una opción: ");
            String choice = SC.nextLine().trim();
            switch (choice) {
                case "1": addProcess(); break;
                case "2": listProcesses(); break;
                case "3": deleteProcess(); break;
                case "4": clearProcesses(); break;
                case "0": System.out.println("Saliendo."); return;
                default: System.out.println("Opción no válida. Intenta de nuevo.\n");
            }
        }
    }

    private static void addProcess() {
        System.out.println("\n--- Agregar nuevo proceso ---");
        System.out.print("Id del proceso (vacío para asignar automáticamente): ");
        String pid = SC.nextLine().trim();
        if (pid.isEmpty()) {
            pid = generateAutoId();
            System.out.println("Id asignado: " + pid);
        } else {
            if (processes.stream().anyMatch(p -> p.getPid().equals(pid))) {
                System.out.println("Error: Id ya existe. Operación cancelada.\n");
                return;
            }
        }

        String name = inputNonEmpty("Nombre del proceso: ");
        int size = inputInt("Tamaño del proceso (entero): ");
        int execTime = inputInt("Tiempo de ejecución en ms (entero): ");
        int priority = inputInt("Prioridad (entero, mayor = más prioridad): ");
        int arrival = inputIntAllowEmpty("Tiempo de llegada en ms (entero, vacío=0): ", 0);

        Process p = new Process(pid, name, size, execTime, priority, arrival);
        processes.add(p);
        System.out.println("Proceso " + pid + " añadido.\n");
    }

    private static String inputNonEmpty(String prompt) {
        while (true) {
            System.out.print(prompt);
            String v = SC.nextLine().trim();
            if (!v.isEmpty()) return v;
            System.out.println("Valor obligatorio, intenta de nuevo.");
        }
    }

    private static int inputInt(String prompt) {
        while (true) {
            System.out.print(prompt);
            String v = SC.nextLine().trim();
            try {
                int iv = Integer.parseInt(v);
                if (iv < 0) {
                    System.out.println("El valor debe ser >= 0.");
                    continue;
                }
                return iv;
            } catch (NumberFormatException e) {
                System.out.println("Ingresa un número entero válido.");
            }
        }
    }

    private static int inputIntAllowEmpty(String prompt, int defaultValue) {
        while (true) {
            System.out.print(prompt);
            String v = SC.nextLine().trim();
            if (v.isEmpty()) return defaultValue;
            try {
                int iv = Integer.parseInt(v);
                if (iv < 0) {
                    System.out.println("El valor debe ser >= 0.");
                    continue;
                }
                return iv;
            } catch (NumberFormatException e) {
                System.out.println("Ingresa un número entero válido o deja vacío.");
            }
        }
    }

    private static void listProcesses() {
        if (processes.isEmpty()) {
            System.out.println("\nNo hay procesos capturados.\n");
            return;
        }
        System.out.println();
        System.out.printf("%-6s %-20s %-6s %-12s %-8s %-12s%n",
                "Id", "Nombre", "Tamaño", "ExecTime(ms)", "Prioridad", "Llegada(ms)");
        System.out.println("-------------------------------------------------------------------");
        for (Process p : processes) {
            System.out.println(p);
        }
        System.out.println();
    }

    private static void deleteProcess() {
        System.out.print("Id del proceso a eliminar: ");
        String id = SC.nextLine().trim();
        if (id.isEmpty()) {
            System.out.println("Id vacío. Operación cancelada.\n");
            return;
        }
        boolean removed = processes.removeIf(p -> p.getPid().equals(id));
        if (removed) System.out.println("Proceso " + id + " eliminado.\n");
        else System.out.println("No existe proceso con Id " + id + ".\n");
    }

    private static void clearProcesses() {
        System.out.print("¿Seguro que deseas eliminar todos los procesos? (s/N): ");
        String r = SC.nextLine().trim().toLowerCase();
        if (r.equals("s") || r.equals("y")) {
            processes.clear();
            System.out.println("Todos los procesos eliminados.\n");
        } else {
            System.out.println("Operación cancelada.\n");
        }
    }

    private static String generateAutoId() {
        Pattern pat = Pattern.compile("^P(\\d+)$", Pattern.CASE_INSENSITIVE);
        Set<Integer>