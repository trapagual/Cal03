package es.trapasoft.android.cal03;


import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.widget.Toast;

import es.trapasoft.android.cal03.modelo.CodigoValor;

/**
 * Created by Administrador on 09/02/2016.
 */
public class DialogoSelCalendario extends DialogFragment {

    private static String[] opciones;
    private static double[] codOpciones;


    /* OJO
     * La activity que crea una instancia de este dialogo HA DE IMPLEMENTAR
     * esta interfaz para recibir los callbacks de los eventos.
     * Cada metodo pasa el DialogFragment para que el contenedor pueda
     * tener acceso a sus elementos. */
    public interface selCalendarioListener {
        // aqui le voy a pasar el indice del array
        // que corresponde al objeto seleccionado
        public void onDialogoSelCalendarioClick(int i);
    }
    // y creo un objeto de esa interfaz
    selCalendarioListener objListener;

    /*
    La activity padre ha de implementar esta interfaz para poder recibir los clicks
    de los botones
    Usamos aqui un bloque try/catch en el metodo onAttach del gragmento para asegurarnos
    de que la actividad implementa la interfaz
     */
    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        // asegurar que la actividad padre inplementa la interfaz de callback
        try {
            // Instanciar la interfaz para poder enviar los clicks al host
            objListener = (selCalendarioListener) activity;
        } catch (ClassCastException e) {
            // if the activity doesn't implement the interface, throw exception
            throw new ClassCastException(activity.toString()
                    + " ha de implementar la interfaz selCalendarioListener");
        }
    }

    /**
     * En vez de llamar al constructor,
     * llamamos a este metodo desde la activity padre lo que nos permite
     * pasar parametros
     * @param cvArray es un array de objetos CodigoValor para luego poder
     *                devolver el codigo y el nombre del elegido
     * @return el objeto DialogoSelCuenta con los parametros
     */
    static DialogoSelCalendario newInstance (CodigoValor[] cvArray) {
        DialogoSelCalendario d = new DialogoSelCalendario();

        // aqui introducimos los parametros en el dialogo
        Bundle args = new Bundle();
        // OJO el parametro que recibimos es un array de CodigoValor
        // al dialogo hay que pasarle un array de cadenas
        // y luego tenemos que averiguar el codigo del nombre seleccionado
        opciones = new String[cvArray.length];
        codOpciones = new double[cvArray.length];
        for (int i=0; i < opciones.length; i++) {
            opciones[i] = cvArray[i].getValor();
            codOpciones[i] = cvArray[i].getCodigo();
        }
        // uso directamente un String[] para no tener que convertir
        // args.putStringArrayList("opciones", opciones);
        args.putStringArray("opciones", opciones);

        d.setArguments(args);

        return d;
    }

    /*execute when dialog cancelled*/
    @Override
    public void onCancel(DialogInterface dialog) {
        super.onCancel(dialog);
        //on pressing back button or touching screen outside dialog
        Toast.makeText(getActivity(), "Diálogo de Selección cancelado", Toast.LENGTH_SHORT).show();
        dialog.dismiss();
    }

    /**
     * Crear el dialogo recogiendo los argumentos
     * @param savedInstanceState
     * @return
     */
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        super.onCreateDialog(savedInstanceState);
        // aqui cargamos la variable local
        opciones = getArguments().getStringArray("opciones");

        // creamos un dialogo Alert normal
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        // lo personalizamos con los datos que tenemos
        builder.setTitle("Elige una cuenta")
                .setSingleChoiceItems(opciones, -1, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        // aqui devuelvo el valor de which, que es la posicion
                        // dentro del array de CodValor que corresponde al nombre elegido
                        objListener.onDialogoSelCalendarioClick(which);
                    }
                });
        return builder.create();
    }

}
