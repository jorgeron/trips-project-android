package us.master.entregable01.entity;

import android.os.Build;

import androidx.annotation.RequiresApi;

import com.google.android.gms.maps.model.LatLng;
import com.google.firebase.firestore.GeoPoint;

import java.io.Serializable;
import java.time.Duration;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Random;

import us.master.entregable01.Constantes;

public class Trip implements Serializable {

    private String uid;
    private int position;
    private String titulo;
    private String lugarSalida;
    private String urlImagen;
    private Long fechaInicio, fechaFin;
    private Integer precio;
    private boolean seleccionado;
    private boolean comprado;
    private GeoPoint coordenadasSalida;

    public Trip() {
    }

    public Trip(String uid, int position, String titulo, String lugarSalida, String urlImagen, Long fechaInicio, Long fechaFin, Integer precio, boolean seleccionado, boolean comprado, GeoPoint coordenadasSalida) {
        this.uid = uid;
        this.position = position;
        this.titulo = titulo;
        this.lugarSalida = lugarSalida;
        this.urlImagen = urlImagen;
        this.fechaInicio = fechaInicio;
        this.fechaFin = fechaFin;
        this.precio = precio;
        this.seleccionado = seleccionado;
        this.comprado = comprado;
        this.coordenadasSalida = coordenadasSalida;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public int getPosition() {
        return position;
    }

    public void setPosition(int position) {
        this.position = position;
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

    public Long getFechaInicio() {
        return fechaInicio;
    }

    public void setFechaInicio(Long fechaInicio) {
        this.fechaInicio = fechaInicio;
    }

    public Long getFechaFin() {
        return fechaFin;
    }

    public void setFechaFin(Long fechaFin) {
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

    public GeoPoint getCoordenadasSalida() {
        return coordenadasSalida;
    }

    public void setCoordenadasSalida(GeoPoint coordenadasSalida) {
        this.coordenadasSalida = coordenadasSalida;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Trip trip = (Trip) o;

        if (position != trip.position) return false;
        if (seleccionado != trip.seleccionado) return false;
        if (comprado != trip.comprado) return false;
        if (uid != null ? !uid.equals(trip.uid) : trip.uid != null) return false;
        if (titulo != null ? !titulo.equals(trip.titulo) : trip.titulo != null) return false;
        if (lugarSalida != null ? !lugarSalida.equals(trip.lugarSalida) : trip.lugarSalida != null)
            return false;
        if (urlImagen != null ? !urlImagen.equals(trip.urlImagen) : trip.urlImagen != null)
            return false;
        if (fechaInicio != null ? !fechaInicio.equals(trip.fechaInicio) : trip.fechaInicio != null)
            return false;
        if (fechaFin != null ? !fechaFin.equals(trip.fechaFin) : trip.fechaFin != null)
            return false;
        if (precio != null ? !precio.equals(trip.precio) : trip.precio != null) return false;
        return coordenadasSalida != null ? coordenadasSalida.equals(trip.coordenadasSalida) : trip.coordenadasSalida == null;
    }

    @Override
    public int hashCode() {
        int result = uid != null ? uid.hashCode() : 0;
        result = 31 * result + position;
        result = 31 * result + (titulo != null ? titulo.hashCode() : 0);
        result = 31 * result + (lugarSalida != null ? lugarSalida.hashCode() : 0);
        result = 31 * result + (urlImagen != null ? urlImagen.hashCode() : 0);
        result = 31 * result + (fechaInicio != null ? fechaInicio.hashCode() : 0);
        result = 31 * result + (fechaFin != null ? fechaFin.hashCode() : 0);
        result = 31 * result + (precio != null ? precio.hashCode() : 0);
        result = 31 * result + (seleccionado ? 1 : 0);
        result = 31 * result + (comprado ? 1 : 0);
        result = 31 * result + (coordenadasSalida != null ? coordenadasSalida.hashCode() : 0);
        return result;
    }


    @Override
    public String toString() {
        return "Trip{" +
                "uid='" + uid + '\'' +
                ", position=" + position +
                ", titulo='" + titulo + '\'' +
                ", lugarSalida='" + lugarSalida + '\'' +
                ", urlImagen='" + urlImagen + '\'' +
                ", fechaInicio=" + fechaInicio +
                ", fechaFin=" + fechaFin +
                ", precio=" + precio +
                ", seleccionado=" + seleccionado +
                ", comprado=" + comprado +
                ", coordenadasSalida=" + coordenadasSalida +
                '}';
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    public static ArrayList<Trip> generaTrips(int max) {
        ArrayList<Trip> list = new ArrayList<>(max);
        Random r=new Random();
        for(int i=0; i<max; i++) {
            String titulo = Constantes.ciudades[r.nextInt(Constantes.ciudades.length-1)];
            String lugarSalida = Constantes.lugarSalida[r.nextInt(Constantes.lugarSalida.length-1)];
            String urlimg = Constantes.urlImagenes[r.nextInt(Constantes.urlImagenes.length-1)];
            Integer precio = Util.getRandomNumberInRange(300, 2500);
            Calendar fechaInicio = Util.generateRandomDateBetween(Instant.now(), Instant.now().plus(Duration.ofDays(4 * 365)));
            Calendar fechaFin = Util.generateRandomDateBetween(fechaInicio.toInstant(), fechaInicio.toInstant().plus(Duration.ofDays(31)));
            boolean seleccionado = false;
            boolean comprado = false;
            GeoPoint coord = new GeoPoint(0,0);

            list.add(new Trip(null, i, titulo, lugarSalida, urlimg, Util.Calendar2long(fechaInicio), Util.Calendar2long(fechaFin), precio, seleccionado, comprado, coord));
        }
        return list;
    }
}
