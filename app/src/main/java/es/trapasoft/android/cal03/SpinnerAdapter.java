package es.trapasoft.android.cal03;

import android.content.Context;
import android.graphics.Color;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import es.trapasoft.android.cal03.modelo.CodigoValor;

/**
 * Created by Administrador on 08/02/2016.
 */
public class SpinnerAdapter extends ArrayAdapter<CodigoValor> {
    private Context context;

    private ArrayList<CodigoValor> valores;


    public SpinnerAdapter(Context context, int textViewResourceId, ArrayList<CodigoValor> objects) {
        super(context, textViewResourceId, objects);
        this.context = context;
        this.valores = objects;
    }

    public int getCount(){
        return valores.size();
    }

    public CodigoValor getItem(int position) {
        return valores.get(position);
    }

    public long getItemId(int position) {
        return position;
    }

    // And the "magic" goes here
    // This is for the "passive" state of the spinner
    @Override
    public View getView(int position, View convertView, ViewGroup parent){
        // I created a dynamic TextView here, but you can reference your own  custom layout for each spinner item
        TextView label = new TextView(context);
        label.setTextColor(Color.BLACK);
        // Then you can get the current item using the values array (Users array) and the current position
        // You can NOW reference each method you has created in your bean object (User class)
        label.setText(valores.get(position).getValor());

        // And finally return your dynamic (or custom) view for each spinner item
        return label;
    }

    // And here is when the "chooser" is popped up
    // Normally is the same view, but you can customize it if you want
    @Override
    public View getDropDownView(int position, View convertView,
                                ViewGroup parent) {
        TextView label = new TextView(context);
        label.setTextColor(Color.BLACK);
        label.setText(valores.get(position).getValor());

        return label;
    }
}
