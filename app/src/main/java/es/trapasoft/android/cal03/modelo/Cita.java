package es.trapasoft.android.cal03.modelo;

import java.util.Calendar;
import java.util.Date;

/**
 * Created by Administrador on 07/02/2016.
 */
public class Cita {
    private int idPaciente;
    private String nombrePaciente;
    private int fAnio;
    private int fMes;
    private int fDia;
    private int hHoraInicio;
    private int hHoraFin;
    private int hMinutoInicio;
    private int hMinutoFin;

    private int idDespacho;
    private String observaciones;
    private Double tarifa;
    private boolean facturar;

    @Override
    public String toString() {
        return "Cita{" +
                "nombrePaciente='" + nombrePaciente + '\'' +
                ", cita=" + getfHoraInicio() +
                '}';
    }

    public Cita() {
    }

    public Cita(int idPaciente, String nombrePaciente, int fAnio, int fMes, int fDia, int hHoraInicio, int hHoraFin, int hMinutoInicio, int hMinutoFin, int idDespacho, String observaciones, Double tarifa, boolean facturar) {
        this.idPaciente = idPaciente;
        this.nombrePaciente = nombrePaciente;
        this.fAnio = fAnio;
        this.fMes = fMes;
        this.fDia = fDia;
        this.hHoraInicio = hHoraInicio;
        this.hHoraFin = hHoraFin;
        this.hMinutoInicio = hMinutoInicio;
        this.hMinutoFin = hMinutoFin;
        this.idDespacho = idDespacho;
        this.observaciones = observaciones;
        this.tarifa = tarifa;
        this.facturar = facturar;
    }

    public int getIdPaciente() {
        return idPaciente;
    }

    public void setIdPaciente(int idPaciente) {
        this.idPaciente = idPaciente;
    }

    public String getNombrePaciente() {
        return nombrePaciente;
    }

    public void setNombrePaciente(String nombrePaciente) {
        this.nombrePaciente = nombrePaciente;
    }

    public int getfAnio() {
        return fAnio;
    }

    public void setfAnio(int fAnio) {
        this.fAnio = fAnio;
    }

    public int getfMes() {
        return fMes;
    }

    public void setfMes(int fMes) {
        this.fMes = fMes;
    }

    public int getfDia() {
        return fDia;
    }

    public void setfDia(int fDia) {
        this.fDia = fDia;
    }

    public int gethHoraInicio() {
        return hHoraInicio;
    }

    public void sethHoraInicio(int hHoraInicio) {
        this.hHoraInicio = hHoraInicio;
    }

    public int gethHoraFin() {
        return hHoraFin;
    }

    public void sethHoraFin(int hHoraFin) {
        this.hHoraFin = hHoraFin;
    }

    public int gethMinutoInicio() {
        return hMinutoInicio;
    }

    public void sethMinutoInicio(int hMinutoInicio) {
        this.hMinutoInicio = hMinutoInicio;
    }

    public int gethMinutoFin() {
        return hMinutoFin;
    }

    public void sethMinutoFin(int hMinutoFin) {
        this.hMinutoFin = hMinutoFin;
    }

    public int getIdDespacho() {
        return idDespacho;
    }

    public void setIdDespacho(int idDespacho) {
        this.idDespacho = idDespacho;
    }

    public String getObservaciones() {
        return observaciones;
    }

    public void setObservaciones(String observaciones) {
        this.observaciones = observaciones;
    }

    public Double getTarifa() {
        return tarifa;
    }

    public void setTarifa(Double tarifa) {
        this.tarifa = tarifa;
    }

    public boolean isFacturar() {
        return facturar;
    }

    public void setFacturar(boolean facturar) {
        this.facturar = facturar;
    }

    // metodos de gestion de fechas
    public String getDia() {
        return fAnio + "/" + fMes + "/" + fDia;
    }

    public Date getDiaAsDate() {
        Calendar cal = Calendar.getInstance();
        cal.set(fAnio, fMes + 1, fDia);
        return cal.getTime();
    }

    public String getHoraInicio() {
        return String.format("%02d:%02d", hHoraInicio, hMinutoInicio);
    }

    public String getHoraFin() {
        return String.format("%02d:%02d", hHoraFin, hMinutoFin);
    }

    public String getfHoraInicio() {
        return String.format("%02d/%02d/%04d %02d:%02d", fDia, fMes, fAnio, hHoraInicio, hMinutoInicio);
    }

    public String getfHoraFin() {
        return String.format("%02d/%02d/%04d %02d:%02d", fDia, fMes, fAnio, hHoraFin, hMinutoFin);
    }

    // un array para las pruebas que contenga unas cuantas citas
    public static Cita[] ITEMS = {
            new Cita(1, "1.María", 2016, 2, 1, 10, 11, 0, 0, 1, "Está muy mal", 65.0, false),
            new Cita(2, "2.Juan", 2016, 2, 1, 11, 12, 30, 30, 1, "Viene con su hijo", 60.0, true),
            new Cita(3, "3.Antonia", 2016, 2, 1, 17, 18, 0, 0, 1, null, 65.0, false),
            new Cita(4, "4.Julián", 2016, 2, 2, 10, 11, 0, 0, 1, "Cuidado", 65.0, false),
            new Cita(5, "5.Isabel", 2016, 2, 2, 14, 15, 0, 0, 1, null, 65.0, false),
            new Cita(6, "6.Andrés", 2016, 2, 3, 10, 11, 0, 30, 1, "Primera Cita", 80.0, false),
            new Cita(7, "7.Angelines", 2016, 2, 3, 10, 11, 0, 0, 1, null, 65.0, true),
            new Cita(8, "8.Paco", 2016, 2, 3, 12, 13, 0, 0, 1, "Es el marido de Antonia", 65.0, false)
    };

    /**
     * Devuelve el hash del objeto
     * basado en su cadena toString
     *
     * @return
     */
    public int getId() {
        return toString().hashCode();
    }

    /**
     * Devuelve un item del array segun su identificador
     *
     * @param id el identificador hash
     * @return un objeto Cita del array
     */
    public static Cita getItem(int id) {
        for (Cita item : ITEMS) {
            if (item.getId() == id) return item;
        }
        return null;
    }
}
