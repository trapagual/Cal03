package es.trapasoft.android.cal03;

import android.content.Context;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.provider.CalendarContract;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import es.trapasoft.android.cal03.modelo.Cita;
import es.trapasoft.android.cal03.modelo.CodigoValor;

public class MainActivity extends AppCompatActivity
        implements DialogoSelCuenta.selCuentaListener, DialogoSelCalendario.selCalendarioListener {

    private Toolbar toolbar;
    private CodigoValor[] arrayListaCalendarios;
    private String[] arrayListaCuentas;

    private static long ID_CALENDARIO = 0;
    private static String NOMBRE_CALENDARIO = "";
    private static String NOMBRE_CUENTA = null;

    private TextView tvUsuario, tvCalendario;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // CREA Y MUESTRA UNA TOOLBAR CON BOTONES DE MENU
        crearToolbar();

        tvUsuario = (TextView)findViewById(R.id.tvUsuario);
        tvCalendario = (TextView)findViewById(R.id.tvCalendario);

        // LEER LAS PREFERENCIAS PARA OBTENER EL NOMBRE_CUENTA
        NOMBRE_CUENTA = leerPreferencias("NOMBRE_CUENTA");
        Log.i("Cal03.NombreCuenta", "NombreCuenta es " + NOMBRE_CUENTA);

        if (NOMBRE_CUENTA == null || NOMBRE_CUENTA.isEmpty() || "ERROR".equals(NOMBRE_CUENTA)) {
            // si no tengo NOMBRE_CUENTA, mostrar el dialogo para cargarla
            Log.i("Cal03.NombreCuenta", "NombreCuenta es " + NOMBRE_CUENTA + ". Voy a llamar al dialogo");
            mostrarDialogoPreferenciasCuenta();
        } else {
            tvUsuario.setText("Usando cuenta: "+ NOMBRE_CUENTA);
            //Toast.makeText(MainActivity.this, "NOMBRE_CUENTA: "+ NOMBRE_CUENTA, Toast.LENGTH_LONG).show();
        }


        // LEER LAS PREFERENCIAS PARA OBTENER EL ID_CALENDARIO
        String tmpIdCalendario = leerPreferencias("ID_CALENDARIO");
        if ("ERROR".equals(tmpIdCalendario)) {
            // no hay preferencias, llamar al dialogo
            Log.i("Cal03.CalError", "El ID_CALENDARIO es nulo o Error");
            mostrarDialogoPreferenciasCalendario();
        }
        try {  // si me devuelve "ERROR" va a cantar
            ID_CALENDARIO = Long.parseLong(tmpIdCalendario);
            NOMBRE_CALENDARIO = leerPreferencias("NOMBRE_CALENDARIO");
            tvCalendario.setText("Usando calendario: " + NOMBRE_CALENDARIO);
            // si no existe, va a salir por el error de abajo (pq leerPreferencias() devuelve "ERROR" si no encuentra la preferencia)
        } catch (NumberFormatException e) {
            // si estoy aqui es porque me ha devuelto "ERROR"
            // pedir que se rellene la variable
            arrayListaCalendarios = rellenaListaCalendarios(NOMBRE_CUENTA);
            mostrarDialogoPreferenciasCalendario();
        }


        // YA TENGO GRABADOS EN PREFERENCIAS Y EN LAS VARIABLES GLOBALES
        // EL NOMBRE DE LA CUENTA Y EL CODIGO DEL CALENDARIO QUE VAMOS A USAR

        // RECUPERAR EVENTOS
        ArrayList listaCitas = UtilCalendar.leerEventosCalendario(this, ID_CALENDARIO);

        // debug: pintarlo en pantalla a ver que sale
        Iterator it = listaCitas.iterator();
        while(it.hasNext()) {
            Cita evento = (Cita) it.next();
            Log.i("Cal03.ListaCitas", evento.getNombrePaciente() +" / " + evento.getFechaHoraAsString(evento.getFhInicio()) + "->" + evento.getFechaHoraAsString(evento.getFhFin()));
        }




    }


    // *********************************** GESTION DE DIALOGOS **************************************
    private void mostrarDialogoPreferenciasCalendario() {

        // inicializar campo para la lista
        arrayListaCalendarios = rellenaListaCalendarios(NOMBRE_CUENTA);
        // ya tengo la lista ahora
        // llamamos al fragmentmanager
        FragmentManager fm = getSupportFragmentManager();

        if (arrayListaCalendarios!=null && arrayListaCalendarios.length > 0) {
            DialogoSelCalendario dial = DialogoSelCalendario.newInstance(arrayListaCalendarios);
            dial.show(fm, "tagListaCalendarios");
        } else {
            Toast.makeText(MainActivity.this, "NO HAY LISTA DE CALENDARIOS PARA LA CUENTA "+ NOMBRE_CUENTA, Toast.LENGTH_SHORT).show();
            //finish();
        }

    }

    private void mostrarDialogoPreferenciasCuenta() {

        arrayListaCuentas = rellenaListaCuentas();

        Log.i("Cal03.mostrarDCuenta", "arrayListaCuentas tiene " + arrayListaCuentas.length);
        // ya tengo la lista ahora
        // llamamos al fragmentmanager
        FragmentManager fm = getSupportFragmentManager();
        if (arrayListaCuentas == null || arrayListaCuentas.length == 0) {
            Toast.makeText(MainActivity.this, "NO HAY LISTA DE CUENTAS", Toast.LENGTH_SHORT).show();
            finish();
        } else {
            Log.i("Cal03.mostrarDCuenta", "Llamo al dialogo de cuentas");
            DialogoSelCuenta dial = DialogoSelCuenta.newInstance(arrayListaCuentas);
            dial.show(fm, "tagSelCuenta");
        }
    }
    /*
        Funcion callback para recibir las pulsaciones de boton del fragmento dialogo
     */
    @Override
    public void onDialogoSelCalendarioClick(int i) {
        // aqui en "i" me viene la posicion del objeto
        // CodigoValor del que tengo que recoger el codigo y el nombre
        ID_CALENDARIO = arrayListaCalendarios[i].getCodigo();
        NOMBRE_CALENDARIO = arrayListaCalendarios[i].getValor();
        tvCalendario.setText("Usando calendario: " + NOMBRE_CALENDARIO);
        // y lo guardo en las preferencias
        guardarPreferencias("ID_CALENDARIO", String.valueOf(ID_CALENDARIO));
        guardarPreferencias("NOMBRE_CALENDARIO", NOMBRE_CALENDARIO);
        Toast.makeText(MainActivity.this, "MainActivity: Calendario seleccionado:" + ID_CALENDARIO +": " + NOMBRE_CALENDARIO, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onDialogoSelCuentaClick(int i) {
        // relleno el campo de clase con el nombre
        NOMBRE_CUENTA = arrayListaCuentas[i];
        if (validarCuenta(NOMBRE_CUENTA)) {
            tvUsuario.setText(NOMBRE_CUENTA);
            // guardar las preferencias
            guardarPreferencias("NOMBRE_CUENTA", NOMBRE_CUENTA);
            // si cambiamos la cuenta, tenemos que cambiar el calendario
            mostrarDialogoPreferenciasCalendario();
            //Toast.makeText(MainActivity.this, "MainActivity: Cuenta seleccionada:" + NOMBRE_CUENTA, Toast.LENGTH_SHORT).show();
        } else { // si la cuenta no es un correo valido, avisamos y nos vamos: no podemos hacer nada
            tvUsuario.setText("CUENTA INVÁLIDA " + NOMBRE_CUENTA);
            Toast.makeText(MainActivity.this, "La cuenta de calendario no es válida." + NOMBRE_CUENTA, Toast.LENGTH_LONG).show();
            Log.e("Cal03.MainActivity", "EL CORREO NO ES VALIDO", new Exception("EL USUARIO NO HA RELLENADO EL CORREO"));
            finish();
        }
    }
    /*
        GESTION DE PREFERENCIAS
        Metodos para leer una preferencia del fichero de la aplicacion
        y para grabarla
     */
    private String leerPreferencias(String idPref) {
        SharedPreferences prefs = getSharedPreferences("Cal03Prefs", Context.MODE_PRIVATE);
        return prefs.getString(idPref, "ERROR");
    }

    private void guardarPreferencias(String idPref, String valorPref) {
        SharedPreferences prefs =
                getSharedPreferences("Cal03Prefs",Context.MODE_PRIVATE);

        SharedPreferences.Editor editor = prefs.edit();
        editor.putString(idPref, valorPref);
        editor.commit();

    }

    /**
     * Este es un metodo de debug
     * que borra las preferencias para probar que se recrean correctamente
     * No utilizar en producción
     */
    private void borrarPreferencias(){
        SharedPreferences settings = getSharedPreferences("Cal03Prefs", Context.MODE_PRIVATE);
        settings.edit().clear().commit();
    }



    /**
     * Rellena un array de Strings
     * @return una lista de los nombres de cuenta diferentes que tienen calendarios activos
     */
    private String[] rellenaListaCuentas() {

        ArrayList<String> lista = new ArrayList<>();
        String[] arrayLista;

        String[] consulta = new String[]{
                // solo quiero los nombres de cuenta
                CalendarContract.Calendars.ACCOUNT_NAME
        };
        // de los calendarios visibles
        String where = CalendarContract.Calendars.VISIBLE + " = 1";
        // ordenados alfabeticamente
        String orderby = CalendarContract.Calendars.ACCOUNT_NAME + " ASC";

        Cursor cursor =
                getContentResolver().
                        query(CalendarContract.Calendars.CONTENT_URI,
                                consulta,
                                where,
                                null,
                                orderby);
        if (cursor != null) {
            if (cursor.moveToFirst()) {
                int contador = 0;
                do {
                    String nombre = cursor.getString(0);
                    Log.i("Cal03.PrefCuenta", "" + contador++ + ":" + nombre);
                    // si el nombre no esta en el array
                    if (!lista.contains(nombre)) {
                        // lo añado
                        Log.i("Cal03.PrefCuenta", "Voy a añadir:" + nombre);
                        lista.add(nombre);
                    }
                } while (cursor.moveToNext());

                // necesito un String[]
                arrayLista = lista.toArray(new String[lista.size()]);
                return arrayLista;
            }
        }
        return null;
    }
    /**
     * Rellena una lista de calendarios
     * que pertenezcan al idCuenta
     *
     * @param idCuenta
     * @return un array de objetos CodigoValor
     */
    private CodigoValor[] rellenaListaCalendarios(String idCuenta) {

        ArrayList<CodigoValor> lista = new ArrayList<CodigoValor>();
        CodigoValor[] arrayLista;

        String[] projection =
                new String[]{
                        CalendarContract.Calendars._ID,
                        CalendarContract.Calendars.NAME,
                        CalendarContract.Calendars.ACCOUNT_NAME,
                        CalendarContract.Calendars.ACCOUNT_TYPE};
        // convierto el parametro que me pasan a String[]
        // solo quiero los que es propietario, no todos los que tenga
        // configurados, pq si no son suyos no puedo hacer nada con ellos
        // por eso OWNER_ACCOUNT=?
        String[] parametros =
                new String[]{idCuenta, idCuenta};
            Cursor calCursor =
                    getContentResolver().
                            query(CalendarContract.Calendars.CONTENT_URI,
                                    projection,
                                    "(" + CalendarContract.Calendars.VISIBLE + " = 1) AND ("
                                            + CalendarContract.Calendars.ACCOUNT_NAME + "= ?) AND ("
                                            + CalendarContract.Calendars.OWNER_ACCOUNT+ "= ?)",
                                    parametros,
                                    CalendarContract.Calendars.NAME + " ASC");

        if (calCursor != null) {
            if (calCursor.moveToFirst()) {
                do {
                    long id = calCursor.getLong(0);
                    String displayName = calCursor.getString(1);
                    lista.add(new CodigoValor(id, displayName));
                } while (calCursor.moveToNext());

                arrayLista = lista.toArray(new CodigoValor[lista.size()]);
                return arrayLista;
            } else {
                return null;
            }
        }
        return null;
    }


    private void crearToolbar() {
        toolbar = (Toolbar) findViewById(R.id.app_bar);
        setSupportActionBar(toolbar);
    }



    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle item selection
        switch (item.getItemId()) {
            case R.id.mnu_config:
                //display in short period of time
                Toast.makeText(getApplicationContext(), "Has pulsado CONFIGURACION", Toast.LENGTH_SHORT).show();
                return true;
            case R.id.mnu_siguiente:
                //display in short period of time
                Toast.makeText(getApplicationContext(), "Has pulsado SIGUIENTE", Toast.LENGTH_SHORT).show();
                return true;
            case R.id.mnu_borrar_preferencias:
                borrarPreferencias();
                return true;
            /* otros casos
            case R.id.help:
                showHelp();
                return true;
            */
            default:
                return super.onOptionsItemSelected(item);
        }
    }



    /**
     * Valida que una cadena que recibe como parametro
     * cumpla la esctructura de una cuenta de correo
     * estandar, mediante una cadea regex
     * @param email
     * @return true si lo cumple, false si no
     */
    private boolean validarCuenta(String email) {

        Log.i("Cal03.validarCuenta", "He recibido ["+email+"]");

        String PATTERN_EMAIL = "^[_A-Za-z0-9-\\+]+(\\.[_A-Za-z0-9-]+)*@"
                + "[A-Za-z0-9-]+(\\.[A-Za-z0-9]+)*(\\.[A-Za-z]{2,})$";

        // Compila la expresion regular (regex) en un patron
        Pattern pattern = Pattern.compile(PATTERN_EMAIL);

        // comprueba si el patron concuerda con la cadena recibida
        Matcher matcher = pattern.matcher(email);
        if (!matcher.matches()) {
            Toast toastErr=Toast.makeText(getApplicationContext(),
                    "La cuenta de correo no es válida", Toast.LENGTH_LONG);
            toastErr.show();
        }
        return matcher.matches();

    }


}
