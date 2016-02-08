package es.trapasoft.android.cal03;

import android.Manifest;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.os.Build;
import android.provider.CalendarContract;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

import es.trapasoft.android.cal03.modelo.CodigoValor;
import es.trapasoft.android.cal03.modelo.DosTextos;

public class MainActivity extends AppCompatActivity {

    private Toolbar toolbar;
    private Spinner spCalendario;
    private SpinnerAdapter adapter;
    private ArrayList<CodigoValor> listaCalendarios;

    private static long ID_CALENDARIO = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        crearToolbar();


        listaCalendarios = rellenaListaCalendarios();

       // adapter = new SpinnerAdapter(this, android.R.layout.simple_spinner_dropdown_item, listaCalendarios);
        adapter = new SpinnerAdapter(this, R.layout.spinner_una_linea, listaCalendarios);

        spCalendario = (Spinner) findViewById(R.id.spCalendario);
        spCalendario.setPrompt("Selecciona un calendario...");
        spCalendario.setAdapter(adapter);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR2) {
            spCalendario.setLayoutMode(Spinner.MODE_DIALOG);
        }
        // You can create an anonymous listener to handle the event when is selected an spinner item
        spCalendario.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {

            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view,
                                       int position, long id) {
                // Here you get the current item (a User object) that is selected by its position
                CodigoValor objeto = adapter.getItem(position);
                // aqui puedo rellenar el id del calendario seleccionado
                ID_CALENDARIO = objeto.getCodigo();
                // Here you can do the action you want to...
                Toast.makeText(MainActivity.this, "ID: " + objeto.getCodigo() + " Name: " + objeto.getValor(), Toast.LENGTH_SHORT).show();
            }
            @Override
            public void onNothingSelected(AdapterView<?> adapter) {  }
        });


    }


    private ArrayList<CodigoValor> rellenaListaCalendarios() {

        ArrayList<CodigoValor> lista = new ArrayList<CodigoValor>();

        String[] projection =
                new String[]{
                        CalendarContract.Calendars._ID,
                        CalendarContract.Calendars.NAME,
                        CalendarContract.Calendars.ACCOUNT_NAME,
                        CalendarContract.Calendars.ACCOUNT_TYPE};


            Cursor calCursor =
                    getContentResolver().
                            query(CalendarContract.Calendars.CONTENT_URI,
                                    projection,
                                    CalendarContract.Calendars.VISIBLE + " = 1",
                                    null,
                                    CalendarContract.Calendars._ID + " ASC");

        if (calCursor.moveToFirst()) {
            do {
                long id = calCursor.getLong(0);
                String displayName = calCursor.getString(1);
                lista.add(new CodigoValor(id, displayName));
            } while (calCursor.moveToNext());
            return lista;
        } else {
            return null;
        }
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
}
