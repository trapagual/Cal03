package es.trapasoft.android.cal03;


import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.widget.Toast;

/**
 * Created by Administrador on 09/02/2016.
 */
public class DialogoSelCuenta extends DialogFragment {

    String[] opciones;


    /* OJO
     * La activity que crea una instancia de este dialogo HA DE IMPLEMENTAR
     * esta interfaz para recibir los callbacks de los eventos.
     * Cada metodo pasa el DialogFragment para que el contenedor pueda
     * tener acceso a sus elementos. */
    public interface selCuentaListener {
        // aqui le voy a pasar el indice del array
        // que corresponde al objeto seleccionado
        public void onDialogoSelCuentaClick(int i);
    }
    // y creo un objeto de esa interfaz
    selCuentaListener objListener;

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
            objListener = (selCuentaListener) activity;
        } catch (ClassCastException e) {
            // if the activity doesn't implement the interface, throw exception
            throw new ClassCastException(activity.toString()
                    + " ha de implementar la interfaz selCuentaListener");
        }
    }

    /**
     * En vez de llamar al constructor,
     * llamamos a este metodo desde la activity padre lo que nos permite
     * pasar parametros
     * @param opciones es un array de cadenas String[]
     * @return el objeto DialogoSelCuenta con los parametros
     */
    static DialogoSelCuenta newInstance (String[] opciones) {
        DialogoSelCuenta d = new DialogoSelCuenta();

        // aqui introducimos los parametros en el dialogo
        Bundle args = new Bundle();
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
                        objListener.onDialogoSelCuentaClick(which);
                    }
                });
        return builder.create();
    }

}
