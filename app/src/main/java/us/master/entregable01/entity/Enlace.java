package us.master.entregable01.entity;

import java.util.ArrayList;

import us.master.entregable01.R;
import us.master.entregable01.SelectedTripsActivity;
import us.master.entregable01.TripListActivity;

public class Enlace {

    private String nombre;
    private int recursoImageView;
    private Class clase;

    public Enlace(String nombre, int recursoImageView, Class clase) {
        this.nombre = nombre;
        this.recursoImageView = recursoImageView;
        this.clase = clase;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public int getRecursoImageView() {
        return recursoImageView;
    }

    public void setRecursoImageView(int recursoImageView) {
        this.recursoImageView = recursoImageView;
    }

    public Class getClase() {
        return clase;
    }

    public void setClase(Class clase) {
        this.clase = clase;
    }

    public static ArrayList<Enlace> generaEnlaces() {
        ArrayList<Enlace> result = new ArrayList<>();
        result.add(new Enlace("Viajes disponibles", R.drawable.available_trips, TripListActivity.class));
        result.add(new Enlace("Viajes seleccionados", R.drawable.selected, SelectedTripsActivity.class));

        return result;
    }
}
