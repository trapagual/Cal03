package es.trapasoft.android.cal03;

import android.Manifest;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.os.Build;
import android.provider.CalendarContract;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import java.io.FileDescriptor;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import es.trapasoft.android.cal03.modelo.CodigoValor;
import es.trapasoft.android.cal03.modelo.DosTextos;

public class MainActivity extends AppCompatActivity
        implements DialogoSelCuenta.selCuentaListener, DialogoSelCalendario.selCalendarioListener {

    private Toolbar toolbar;
    private Spinner spCalendario;
    private SpinnerAdapter adapter;
    private ArrayList<CodigoValor> listaCalendarios;
    private CodigoValor[] arrayListaCalendarios;
    private ArrayList<String> listaCuentas;
    private String[] arrayListaCuentas;

    private static long ID_CALENDARIO = 0;
    private static String NOMBRE_CALENDARIO = "";
    private static String ID_CUENTA = null;

    private TextView tvUsuario, tvCalendario;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // CREA Y MUESTRA UNA TOOLBAR CON BOTONES DE MENU
        crearToolbar();

        tvUsuario = (TextView)findViewById(R.id.tvUsuario);
        tvCalendario = (TextView)findViewById(R.id.tvCalendario);

        // LEER LAS PREFERENCIAS PARA OBTENER EL ID_CUENTA
        ID_CUENTA = leerPreferencias("ID_CUENTA");
        if (ID_CUENTA == null || ID_CUENTA.isEmpty() || "ERROR".equals(ID_CUENTA)) {
            // si no tengo ID_CUENTA, mostrar el dialogo para cargarla
            mostrarDialogoPreferenciasCuenta();
        } else {
            tvUsuario.setText("Usando cuenta: "+ID_CUENTA);
            Toast.makeText(MainActivity.this, "ID_CUENTA: "+ID_CUENTA, Toast.LENGTH_LONG).show();
        }


        // LEER LAS PREFERENCIAS PARA OBTENER EL ID_CALENDARIO
        try {  // si me devuelve "ERROR" va a cantar
            ID_CALENDARIO = Long.parseLong(leerPreferencias("ID_CALENDARIO"));
            NOMBRE_CALENDARIO = leerPreferencias("NOMBRE_CALENDARIO");
            tvCalendario.setText("Usando calendario: " + NOMBRE_CALENDARIO);
            // si no existe, va a salir por el error de abajo (pq leerPreferencias() devuelve "ERROR" si no encuentra la preferencia)
        } catch (NumberFormatException e) {
            // si estoy aqui es porque me ha devuelto "ERROR"
            // pedir que se rellene la variable
            arrayListaCalendarios = rellenaListaCalendarios(ID_CUENTA);
            mostrarDialogoPreferenciasCalendario();
        }


        /******************** SUSTITUIDO POR UN DIALOGO DE SELECCION ***********************************
        // TODA ESTA PARTE DEL SPINNER PUEDE QUE LO SUSTITUYA POR UN DIALOGO
        // COMO LA SELECCION DE CUENTAS, QUE VA MUY BIEN
       // adapter = new SpinnerAdapter(this, android.R.layout.simple_spinner_dropdown_item, listaCalendarios);
        adapter = new SpinnerAdapter(this, R.layout.spinner_una_linea, listaCalendarios);


        spCalendario = (Spinner) findViewById(R.id.spCalendario);
        spCalendario.setPrompt("Selecciona un calendario...");
        spCalendario.setAdapter(adapter);
        // esto no hace nada, pero ponerlo en el XML, si: android:spinnerMode="dialog"
        // y se ve el prompt como cabecera del dialogo
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR2) {
            spCalendario.setLayoutMode(Spinner.MODE_DIALOG);
        }
        // esto es para que se vea el prompt --> NO SE MUESTRA
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        // You can create an anonymous listener to handle the event when is selected an spinner item
        spCalendario.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {

            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view,
                                       int position, long id) {
                // Here you get the current item (a User object) that is selected by its position
                CodigoValor objeto = adapter.getItem(position);
                // aqui puedo rellenar el id del calendario seleccionado
                ID_CALENDARIO = objeto.getCodigo();
                NOMBRE_CALENDARIO = objeto.getValor();
                // Here you can do the action you want to...
                Toast.makeText(MainActivity.this, "ID: " + objeto.getCodigo() + " Name: " + objeto.getValor(), Toast.LENGTH_SHORT).show();
            }
            @Override
            public void onNothingSelected(AdapterView<?> adapter) {  }
        });
        **************** FIN SUSTITUIDO POR UN DIALOGO DE SELECCION **********************/

    }


    // *********************************** GESTION DE DIALOGOS **************************************
    private void mostrarDialogoPreferenciasCalendario() {
        // primero consulto las distintas cuentas a que pertenecen los calendarios
        // configurados por el usuario

        // inicializar campo para la lista
        arrayListaCalendarios = rellenaListaCalendarios(ID_CUENTA);
        // ya tengo la lista ahora
        // llamamos al fragmentmanager
        FragmentManager fm = getSupportFragmentManager();

        if (arrayListaCalendarios!=null && arrayListaCalendarios.length > 0) {
            DialogoSelCalendario dial = DialogoSelCalendario.newInstance(arrayListaCalendarios);
            dial.show(fm, "tagListaCalendarios");
        } else {
            Toast.makeText(MainActivity.this, "NO HAY LISTA DE CALENDARIOS PARA LA CUENTA "+ID_CUENTA, Toast.LENGTH_SHORT).show();
            finish();
        }

    }

    private void mostrarDialogoPreferenciasCuenta() {

        // inicializar campo para la lista
        listaCalendarios = new ArrayList<>();

        String[] consulta = new String[] {
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
                do {
                    String nombre = cursor.getString(0);
                    // si el nombre no esta en el array
                    if (!listaCuentas.contains(nombre)) {
                        // lo añado
                        listaCuentas.add(nombre);
                    }
                } while (cursor.moveToNext());

                // necesito un String[]
                arrayListaCuentas = listaCuentas.toArray(new String[listaCuentas.size()]);
                // ya tengo la lista ahora
                // llamamos al fragmentmanager
                FragmentManager fm = getSupportFragmentManager();
                // DialogoSelCuenta dial = new DialogoSelCuenta();
                DialogoSelCuenta dial = DialogoSelCuenta.newInstance(arrayListaCuentas);
                dial.show(fm, "tagSelCuenta");
            } // fin moveToFirst
        } // fin != null

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
        ID_CUENTA = arrayListaCuentas[i];
        if (validarCuenta(ID_CUENTA)) {
            tvUsuario.setText(ID_CUENTA);
            // guardar las preferencias
            guardarPreferencias("ID_CUENTA", ID_CUENTA);
            Toast.makeText(MainActivity.this, "MainActivity: Cuenta seleccionada:" + ID_CUENTA, Toast.LENGTH_SHORT).show();
        } else { // si la cuenta no es un correo valido, avisamos y nos vamos: no podemos hacer nada
            tvUsuario.setText("CUENTA INVÁLIDA " + ID_CUENTA);
            Toast.makeText(MainActivity.this, "La cuenta de calendario no es válida." + ID_CUENTA, Toast.LENGTH_LONG).show();
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
        String[] parametros =
                new String[]{idCuenta};
            Cursor calCursor =
                    getContentResolver().
                            query(CalendarContract.Calendars.CONTENT_URI,
                                    projection,
                                    "(" + CalendarContract.Calendars.VISIBLE + " = 1) AND (" + CalendarContract.Calendars.ACCOUNT_NAME + "= ?)",
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


    private void rellenarSpinner(ArrayList<CodigoValor> lista, SpinnerAdapter adapter) {
        spCalendario.setPrompt("Selecciona un calendario...");
        spCalendario.setAdapter(adapter);
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
