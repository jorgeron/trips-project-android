package us.master.entregable01.adapter;

import android.content.Context;
import android.graphics.Color;
import android.location.Location;
import android.location.LocationManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.android.gms.maps.model.LatLng;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.GeoPoint;
import com.google.firebase.firestore.ListenerRegistration;
import com.google.firebase.firestore.QuerySnapshot;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import us.master.entregable01.LocationActivity;
import us.master.entregable01.R;
import us.master.entregable01.TripListActivity;
import us.master.entregable01.database.FirestoreService;
import us.master.entregable01.entity.Trip;
import us.master.entregable01.entity.Util;

public class TripsAdapter extends RecyclerView.Adapter<TripsAdapter.ViewHolder> implements EventListener<QuerySnapshot> {

    public final List<Trip> trips;
    private OnTripListener mOnTripListener;
    private DataChangedListener mDataChangedListener;
    private ItemErrorListener errorListener;
    public final ListenerRegistration listenerRegistration;

    public TripsAdapter(Boolean vistaSeleccionados, OnTripListener onTripListener) {
        this.trips = new ArrayList<>();
        String userId = Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser().getUid());
        this.mOnTripListener = onTripListener;

        if (vistaSeleccionados) {
            listenerRegistration = FirestoreService.getServiceInstance().getSelectedTrips(this);
        } else {
            listenerRegistration = FirestoreService.getServiceInstance().getTrips(this);
        }
    }



    public TripsAdapter(List<Trip> trips, ListenerRegistration listenerRegistration, OnTripListener onTripListener) {
        this.trips = trips;
        this.mOnTripListener = onTripListener;
        this.listenerRegistration = listenerRegistration;
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
                "\nPrecio: " + trip.getPrecio() + " €");
        holder.imgChecked.setImageDrawable(null);
        if (trip.isSeleccionado()) {
            holder.imgChecked.setImageResource(R.drawable.ic_check_circle_24dp);
        }

        Glide.with(holder.imagen.getContext())
                .load(trip.getUrlImagen())
                .placeholder(R.drawable.available_trips)
                .centerCrop()
                .into(holder.imagen);

        int distancia = Util.calculaDistancia(LocationActivity.lastLocation, trip);
        holder.textViewDistancia.setText(Util.muestraTextoDistancia(distancia));
        if(distancia > 2000) {
            holder.textViewDistancia.setTextColor(Color.RED);
        } else {
            holder.textViewDistancia.setTextColor(Color.rgb(0,216,56));
        }

    }


    @Override
    public int getItemCount() {
        return trips.size();
    }

    @Override
    public void onEvent(@Nullable QuerySnapshot queryDocumentSnapshots, @Nullable FirebaseFirestoreException e) {
        if (e != null) {
            errorListener.onItemError(e);
        }

        trips.clear();
        if (queryDocumentSnapshots != null) {
            for(DocumentSnapshot documentSnapshot : queryDocumentSnapshots.getDocuments()) {
                Trip t = documentSnapshot.toObject(Trip.class);
                t.setUid(documentSnapshot.getId());
                trips.add(t);
            }
        }

        notifyDataSetChanged();
        mDataChangedListener.onDataChanged();
    }

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        public TextView textViewTitulo, textViewCiudadDestino, textViewDistancia;
        public ImageView imagen;
        public ImageView imgChecked;
        OnTripListener onTripListener;

        public ViewHolder(@NonNull View itemView, OnTripListener mOnTripListener) {
            super(itemView);
            textViewTitulo = itemView.findViewById(R.id.textView_ciudad);
            textViewCiudadDestino = itemView.findViewById(R.id.textView_description);
            textViewDistancia = itemView.findViewById(R.id.textView_distance);
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

    public void setErrorListener(ItemErrorListener itemErrorListener) {
        errorListener = itemErrorListener;
    }

    public interface ItemErrorListener {
        void onItemError(FirebaseFirestoreException error);
    }

    public void setDataChangedListener(DataChangedListener dataChangedListener) {
        mDataChangedListener = dataChangedListener;
    }

    public interface DataChangedListener {
        void onDataChanged();
    }

}
