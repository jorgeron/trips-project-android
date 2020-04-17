package us.master.entregable01.entity;

import android.os.Build;

import androidx.annotation.RequiresApi;

import java.io.Serializable;
import java.time.Duration;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Random;

import us.master.entregable01.Constantes;

public class Trip implements Serializable {

    private int id;
    private String titulo;
    private String lugarSalida;
    private String urlImagen;
    private Calendar fechaInicio, fechaFin;
    private Integer precio;
    private boolean seleccionado;


    private boolean comprado;

    public Trip(int id, String titulo, String lugarSalida, String urlImagen, Calendar fechaInicio, Calendar fechaFin, Integer precio, boolean seleccionado, boolean comprado) {
        this.id = id;
        this.titulo = titulo;
        this.lugarSalida = lugarSalida;
        this.urlImagen = urlImagen;
        this.fechaInicio = fechaInicio;
        this.fechaFin = fechaFin;
        this.precio = precio;
        this.seleccionado = seleccionado;
        this.comprado = comprado;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getTitulo() {
        return titulo;
    }

    public void setTitulo(String titulo) {
        this.titulo = titulo;
    }

    public String getLugarSalida() {
        return lugarSalida;
    }

    public void setLugarSalida(String lugarSalida) {
        this.lugarSalida = lugarSalida;
    }

    public String getUrlImagen() {
        return urlImagen;
    }

    public void setUrlImagen(String urlImagen) {
        this.urlImagen = urlImagen;
    }

    public Calendar getFechaInicio() {
        return fechaInicio;
    }

    public void setFechaInicio(Calendar fechaInicio) {
        this.fechaInicio = fechaInicio;
    }

    public Calendar getFechaFin() {
        return fechaFin;
    }

    public void setFechaFin(Calendar fechaFin) {
        this.fechaFin = fechaFin;
    }

    public Integer getPrecio() {
        return precio;
    }

    public void setPrecio(Integer precio) {
        this.precio = precio;
    }

    public boolean isSeleccionado() {
        return seleccionado;
    }

    public void setSeleccionado(boolean seleccionado) {
        this.seleccionado = seleccionado;
    }

    public boolean isComprado() {
        return comprado;
    }

    public void setComprado(boolean comprado) {
        this.comprado = comprado;
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    public static ArrayList<Trip> generaTrips(int max) {
        ArrayList<Trip> list = new ArrayList<>(max);
        Random r=new Random();
        for(int i=1; i<max; i++) {
            String titulo = Constantes.ciudades[r.nextInt(Constantes.ciudades.length-1)];
            String lugarSalida = Constantes.lugarSalida[r.nextInt(Constantes.lugarSalida.length-1)];
            String urlimg = Constantes.urlImagenes[r.nextInt(Constantes.urlImagenes.length-1)];
            Integer precio = Util.getRandomNumberInRange(300, 2500);
            Calendar fechaInicio = Util.generateRandomDateBetween(Instant.now(), Instant.now().plus(Duration.ofDays(4 * 365)));
            Calendar fechaFin = Util.generateRandomDateBetween(fechaInicio.toInstant(), fechaInicio.toInstant().plus(Duration.ofDays(31)));
            boolean seleccionado = false;
            boolean comprado = false;

            list.add(new Trip(i, titulo, lugarSalida, urlimg, fechaInicio, fechaFin, precio, seleccionado, comprado));
        }
        return list;
    }
}
