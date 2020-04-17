package us.master.entregable01.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.squareup.picasso.Picasso;

import java.util.List;

import us.master.entregable01.R;
import us.master.entregable01.entity.Trip;
import us.master.entregable01.entity.Util;

public class TripsAdapter extends RecyclerView.Adapter<TripsAdapter.ViewHolder> {

    List<Trip> trips;
    private OnTripListener mOnTripListener;

    public TripsAdapter(List<Trip> trips, OnTripListener onTripListener) {
        this.trips = trips;
        this.mOnTripListener = onTripListener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        LayoutInflater layoutInflater = LayoutInflater.from(context);
        View tripView = layoutInflater.inflate(R.layout.trip_item, parent, false);
        return new ViewHolder(tripView, mOnTripListener);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Trip trip = trips.get(position);
        //asigno contenido del trip a elementos del viewholder
        holder.textViewTitulo.setText(trip.getTitulo());
        String fechaInicioFormateada = Util.formateaFecha(trip.getFechaInicio());
        String fechaFinFormateada = Util.formateaFecha(trip.getFechaFin());
        holder.textViewCiudadDestino.setText("Salida: " + trip.getLugarSalida() +
                "\nFecha salida: " + fechaInicioFormateada +
                "\nFecha fin: " + fechaFinFormateada +
                "\nPrecio: " + trip.getPrecio());
        holder.imgChecked.setImageDrawable(null);
        if (trip.isSeleccionado()) {
            holder.imgChecked.setImageResource(R.drawable.ic_check_circle_24dp);
        }

        Picasso.get().setLoggingEnabled(true);
        Picasso.get()
                .load(trip.getUrlImagen())
                .placeholder(R.drawable.available_trips)
                .into(holder.imagen);

    }



    @Override
    public int getItemCount() {
        return trips.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        public TextView textViewTitulo;
        public TextView textViewCiudadDestino;
        public ImageView imagen;
        public ImageView imgChecked;
        OnTripListener onTripListener;

        public ViewHolder(@NonNull View itemView, OnTripListener mOnTripListener) {
            super(itemView);
            textViewTitulo = itemView.findViewById(R.id.textView_ciudad);
            textViewCiudadDestino = itemView.findViewById(R.id.textView_description);
            imagen = itemView.findViewById(R.id.imageView_trip);
            imgChecked = itemView.findViewById(R.id.imageView_checked);

            this.onTripListener = mOnTripListener;
            itemView.setOnClickListener(this);

        }

        @Override
        public void onClick(View v) {
            onTripListener.onTripClick(getAdapterPosition());
        }
    }

    public interface OnTripListener {
        void onTripClick(int position);
    }

}