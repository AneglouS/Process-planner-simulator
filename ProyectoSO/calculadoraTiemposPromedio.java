package ProyectoSO;
import java.util.List;

public class calculadoraTiemposPromedio {
    private double promedioRespuesta;
    private double promedioEspera;
    private double promedioRetorno;
    private double promedioEjecucion;

    public void calcularEstadisticas(List<Proceso> procesosCompletados) {
        if (procesosCompletados == null || procesosCompletados.isEmpty()) {
            promedioRespuesta = 0.0;
            promedioEspera = 0.0;
            promedioRetorno = 0.0;
            promedioEjecucion = 0.0;
            return;
        }

        int totalRespuesta = 0;
        int totalEspera = 0;
        int totalRetorno = 0;
        int totalEjecucion = 0;

        for (Proceso p : procesosCompletados) {
            totalRespuesta += p.getTiempoRespuesta();
            totalEspera += p.getTiempoEspera();
            totalRetorno += p.getTiempoEjecucion();
            totalEjecucion += p.getTiempoEjecucion();
        }

        int cantidad = procesosCompletados.size();
        promedioRespuesta = (double) totalRespuesta / cantidad;
        promedioEspera = (double) totalEspera / cantidad;
        promedioRetorno = (double) totalRetorno / cantidad;
        promedioEjecucion = (double) totalEjecucion / cantidad;
    }

    public double getPromedioRespuesta() {
        return promedioRespuesta;
    }

    public double getPromedioEspera() {
        return promedioEspera;
    }

    public double getPromedioRetorno() {
        return promedioRetorno;
    }

    public double getPromedioEjecucion() {
        return promedioEjecucion;
    }
}