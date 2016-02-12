package es.trapasoft.android.cal03;

import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.util.Log;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

import es.trapasoft.android.cal03.modelo.Cita;

/**
 * Clase para los metodos de utilidad del calendario
 * Created by Administrador on 12/02/2016.
 */
public class UtilCalendar {

    /**
     * Lee TODOS los eventos del calendario id_calendario
     *
     * Falta filtrar fechadesde, fechahasta
     *
     * @param context
     * @param id_calendario
     * @return un ArrayList de objetos Cita
     */
    public static ArrayList<Cita> leerEventosCalendario(Context context, long id_calendario) {


        /* para hacerlo con filtros de fechas
        Uri content = Uri.parse("content://com.android.calendar/events");
        String[] vec = new String[] { "calendar_id", "title", "description", "dtstart", "dtend", "allDay", "eventLocation" };
        String selectionClause = "(dtstart >= ? AND dtend <= ?) OR (dtstart >= ? AND allDay = ?)";
        String[] selectionsArgs = new String[]{"" + dtstart, "" + dtend, "" + dtstart, "1"};

        ContentResolver contentResolver = context.getContentResolver();
           Cursor cursor = contentResolver.query(content, vec, selectionClause, selectionsArgs, null);
         */
        ArrayList<Cita> listaCitas = new ArrayList<>();

        Cursor cursor = context.getContentResolver()
                .query(
                        Uri.parse("content://com.android.calendar/events"),
                        new String[] {
                                "calendar_id",  //0
                                "title",        //1
                                "description",  //2
                                "dtstart",      //3
                                "dtend",        //4
                                "eventLocation" //5
                                },
                        null,
                        null,
                        null);


        if (cursor.moveToFirst()) {
            do {
                Cita cita = new Cita();
                cita.setNombrePaciente(cursor.getString(1));
                cita.setObservaciones(cursor.getString(2));
                cita.setFhInicio(getDateAsDate(cursor.getString(3)));
                cita.setFhFin(getDateAsDate(cursor.getString(4)));
                listaCitas.add(cita);
            } while (cursor.moveToNext());

            return listaCitas;
        } else {
            Log.e("Cal03.leerEventos", "No hay eventos en el calendario");
            return null;
        }



    }

    public static Date getDateAsDate(String segundos) {
        if (segundos != null && !segundos.isEmpty()) {
            Long milliSeconds = Long.parseLong(segundos);
            return new Date(milliSeconds);
        } else {
            return null;
        }
    }
    public static Date getDateAsDate(long milliSeconds) {
        return new Date(milliSeconds);
    }

    public static String getDateAsString(long milliSeconds) {
        SimpleDateFormat formatter = new SimpleDateFormat(
                "dd/MM/yyyy hh:mm:ss");
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(milliSeconds);
        return formatter.format(calendar.getTime());
    }
}
