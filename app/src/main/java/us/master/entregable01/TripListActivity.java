 package us.master.entregable01;

import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.location.Location;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.Switch;
import android.widget.Toast;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.firestore.GeoPoint;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.stream.Collectors;

import us.master.entregable01.adapter.TripsAdapter;
import us.master.entregable01.entity.Trip;
import us.master.entregable01.entity.Util;

 public class TripListActivity extends AppCompatActivity implements TripsAdapter.OnTripListener {

     private LinearLayout linearLayout, layout_no_trips;
     private RecyclerView recyclerView;
     private Button filterButton;
     private Switch switchColumnas;
     private FloatingActionButton floatingActionButton;
     private TripsAdapter tripListAdapter;
     private GridLayoutManager gridLayoutManager;
     private Boolean vistaSeleccionados;
     private Boolean hayFiltros;

     static final int FILTER_REQUEST = 1;
     static final int DETAIL_REQUEST = 2;

    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_trip_list);

        linearLayout = findViewById(R.id.linearLayout);
        layout_no_trips = findViewById(R.id.layout_no_trips);
        recyclerView = findViewById(R.id.recyclerView);
        filterButton = findViewById(R.id.button_filtrar);
        switchColumnas = findViewById(R.id.switch_columnas);
        floatingActionButton = findViewById(R.id.create_trip_floating_button);

        // Comprobamos en el intent si estamos en la vista de solo seleccionados o no.
        vistaSeleccionados = getIntent().getBooleanExtra("vistaSeleccionados", false);
        hayFiltros = getIntent().getBooleanExtra("hayFiltros", false);

        tripListAdapter = new TripsAdapter(vistaSeleccionados, this);

        RecyclerView incoming_recycler_view = recyclerView;
        int numColumnas = switchColumnas.isChecked() ? 2 : 1;
        gridLayoutManager = new GridLayoutManager(this, numColumnas);
        incoming_recycler_view.setLayoutManager(gridLayoutManager);
        incoming_recycler_view.setAdapter(tripListAdapter);

        tripListAdapter.setDataChangedListener(() -> {
            if (tripListAdapter.getItemCount() > 0) {
                linearLayout.setVisibility(View.VISIBLE);
                incoming_recycler_view.setVisibility(View.VISIBLE);
                layout_no_trips.setVisibility(View.GONE);

                if(hayFiltros) {
                    Calendar fechaInicio = (Calendar) getIntent().getExtras().get("startDate");
                    Calendar fechaFin = (Calendar) getIntent().getExtras().get("endDate");
                    Integer precioMax = (Integer) getIntent().getExtras().get("precioMaximo");

                    aplicarFiltros(fechaInicio, fechaFin, precioMax);
                }

                Snackbar snackbar = Snackbar.make(recyclerView, "Mostrando " + tripListAdapter.getItemCount() + " viajes", Snackbar.LENGTH_SHORT);
                snackbar.show();
            } else {
                linearLayout.setVisibility(View.GONE);
                incoming_recycler_view.setVisibility(View.GONE);
                layout_no_trips.setVisibility(View.VISIBLE);
            }
        });

        tripListAdapter.setErrorListener(error -> {
            incoming_recycler_view.setVisibility(View.GONE);
        });

        floatingActionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(TripListActivity.this, TripFormActivity.class);
                startActivity(intent);
            }
        });

        filterButton.setOnClickListener(view -> {
            Intent intent = new Intent(TripListActivity.this, FilterActivity.class);
            startActivityForResult(intent, FILTER_REQUEST);
        });

    }


     @RequiresApi(api = Build.VERSION_CODES.N)
     @Override
     protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
         super.onActivityResult(requestCode, resultCode, data);

         Calendar fechaInicio = null;
         Calendar fechaFin = null;
         Integer precioMax = null;

         if (data != null && data.hasExtra("hayFiltros")) {
             hayFiltros = data.getBooleanExtra("hayFiltros", false);
             fechaInicio = (Calendar) data.getExtras().get("startDate");
             fechaFin = (Calendar) data.getExtras().get("endDate");
             precioMax = (Integer) data.getExtras().get("precioMaximo");
         }


         if (requestCode == FILTER_REQUEST && resultCode == RESULT_OK) {
             Intent intent = new Intent(TripListActivity.this, TripListActivity.class);
             intent.putExtra("vistaSeleccionados", vistaSeleccionados);

             if (hayFiltros) {
                 intent.putExtra("hayFiltros", true);
                 intent.putExtra("startDate", fechaInicio);
                 intent.putExtra("endDate", fechaFin);
                 intent.putExtra("precioMaximo", precioMax);
             }
             startActivity(intent);
             finish();

         }

     }

     @RequiresApi(api = Build.VERSION_CODES.N)
     private void aplicarFiltros(Calendar fechaInicio, Calendar fechaFin, Integer precioMax) {
        List<Trip> trips = new ArrayList<>();
        trips.addAll(tripListAdapter.trips);

         if (fechaInicio != null && fechaFin == null) { // solo fecha inicio
             trips = trips.stream().filter(trip->(trip.getFechaInicio() > (Util.Calendar2long(fechaInicio))
                     || trip.getFechaInicio() == (Util.Calendar2long(fechaInicio)))).collect(Collectors.toList());
         } else if (fechaInicio == null && fechaFin != null) { // solo fecha fin
             trips = trips.stream().filter(trip->trip.getFechaInicio() < (Util.Calendar2long(fechaFin))).collect(Collectors.toList());
         } else if (fechaInicio != null && fechaFin != null) { // ambas fechas
             trips = trips.stream().filter(trip->((trip.getFechaInicio() > (Util.Calendar2long(fechaInicio))
                     || trip.getFechaInicio() == (Util.Calendar2long(fechaInicio)))
                     && trip.getFechaInicio() < (Util.Calendar2long(fechaFin)))).collect(Collectors.toList());
         }

         if(precioMax!=null) {
             trips = trips.stream().filter(trip->trip.getPrecio().compareTo(precioMax)==-1).collect(Collectors.toList());
         }

         tripListAdapter.trips.clear();
         tripListAdapter.trips.addAll(trips);
     }


     @Override
     public void onBackPressed() {
         if(tripListAdapter != null && tripListAdapter.listenerRegistration != null) {
             tripListAdapter.listenerRegistration.remove();
         }
        super.onBackPressed();
     }

     @Override
     protected void onDestroy() {
         super.onDestroy();
         if(tripListAdapter != null && tripListAdapter.listenerRegistration != null) {
             tripListAdapter.listenerRegistration.remove();
         }
     }

     @Override
     public void onTripClick(int position) {
         String idTrip = tripListAdapter.trips.get(position).getUid();

         Intent intent = new Intent(TripListActivity.this, TripDetailsActivity.class);
         intent.putExtra("idTrip", idTrip);
         startActivityForResult(intent, DETAIL_REQUEST);
     }

     public void cambiarGrid(View view) {
         int numColumnas = switchColumnas.isChecked() ? 2 : 1;
         gridLayoutManager.setSpanCount(numColumnas);
         recyclerView.setLayoutManager(gridLayoutManager);
     }
 }
