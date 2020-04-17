package us.master.entregable01;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Switch;

import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.snackbar.Snackbar;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.stream.Collectors;

import us.master.entregable01.adapter.TripsAdapter;
import us.master.entregable01.entity.Trip;

public class TripsActivity extends AppCompatActivity implements TripsAdapter.OnTripListener {

    RecyclerView recyclerView;
    Button filterButton;
    Switch switchColumnas;
    ArrayList<Trip> tripsMostrados;
    ArrayList<Trip> totalTrips;
    static final int FILTER_REQUEST = 1;
    static final int DETAIL_REQUEST = 2;
    GridLayoutManager gridLayoutManager;


    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_trips);

        recyclerView = findViewById(R.id.recyclerView);
        filterButton = findViewById(R.id.button_filtrar);
        switchColumnas = findViewById(R.id.switch_columnas);
        totalTrips = (ArrayList<Trip>) getIntent().getExtras().get("trips");

        boolean vistaSeleccionados = getIntent().hasExtra("selectedTrips");

        if(vistaSeleccionados) {
            ArrayList<Trip>  selectedTrips = (ArrayList<Trip>) getIntent().getExtras().get("selectedTrips");
            tripsMostrados = selectedTrips;
        } else {
            tripsMostrados = totalTrips;
        }

        refreshRecyclerView(tripsMostrados);

        int numColumnas = 1;
        if (switchColumnas.isChecked()) {
            numColumnas = 2;
        }
        gridLayoutManager = new GridLayoutManager(this, numColumnas);
        recyclerView.setLayoutManager(gridLayoutManager);

        filterButton.setOnClickListener(view -> {
            Intent intent = new Intent(TripsActivity.this, FilterActivity.class);
            if (vistaSeleccionados) {
                intent.putExtra("vistaSeleccionados", true);
            }
            startActivityForResult(intent, FILTER_REQUEST);
        });
    }

    private void refreshRecyclerView(ArrayList<Trip> trips) {
        TripsAdapter adapter = new TripsAdapter(trips, this);
        recyclerView.setAdapter(adapter);

        Snackbar snackbar = Snackbar.make(recyclerView,
                "Mostrando " + trips.size() + " trips.",
                Snackbar.LENGTH_LONG);
        snackbar.show();
    }


    @Override
    public void onTripClick(int position) {
        Trip trip = tripsMostrados.get(position);

        Intent intent = new Intent(TripsActivity.this, TripDetailActivity.class);
        intent.putExtra("Trip", trip);
        intent.putExtra("position", position);
        startActivityForResult(intent, DETAIL_REQUEST);
    }



    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == FILTER_REQUEST && resultCode == RESULT_OK) {
            Calendar fechaInicio = (Calendar) data.getExtras().get("startDate");
            Calendar fechaFin = (Calendar) data.getExtras().get("endDate");
            Integer precioMax = (Integer) data.getExtras().get("precioMaximo");

            boolean vistaSeleccionados = data.getExtras().getBoolean("vistaSeleccionados");
            if (vistaSeleccionados) { //Si estoy en la sección trips seleccionados, filtra solo entre los que se muestran
                tripsMostrados = filtrarTrips(tripsMostrados, fechaInicio, fechaFin, precioMax);
            } else { // si no, filtra entre la lista total de trips
                tripsMostrados = filtrarTrips(totalTrips, fechaInicio, fechaFin, precioMax);
            }

        } else if (requestCode == DETAIL_REQUEST && resultCode == RESULT_OK) {
            boolean hasChanged = data.getBooleanExtra("hasChanged", false);
            if(hasChanged) {
                Trip trip = (Trip) data.getSerializableExtra("trip");
                int positionMostrados = data.getIntExtra("position", 0);

                tripsMostrados.set(positionMostrados, trip);
                totalTrips.set(trip.getId()-1, trip);
                getIntent().putExtra("trips", totalTrips);
            }
        }

        refreshRecyclerView(tripsMostrados);
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    private ArrayList<Trip> filtrarTrips(ArrayList<Trip> trips, Calendar fechaInicio, Calendar fechaFin, Integer precioMax) {
        ArrayList<Trip> result = new ArrayList<>();

        if (fechaInicio != null && fechaFin == null) { // solo fecha inicio
            result = (ArrayList<Trip>) trips.stream().filter(trip->(trip.getFechaInicio().after(fechaInicio)
                    || trip.getFechaInicio().equals(fechaInicio))).collect(Collectors.toList());
        } else if (fechaInicio == null && fechaFin != null) { // solo fecha fin
            result = (ArrayList<Trip>) trips.stream().filter(trip->trip.getFechaInicio().before(fechaFin)).collect(Collectors.toList());
        } else if (fechaInicio != null && fechaFin != null) { // ambas fechas
            result = (ArrayList<Trip>) trips.stream().filter(trip->((trip.getFechaInicio().after(fechaInicio)
                    || trip.getFechaInicio().equals(fechaInicio))
                    && trip.getFechaInicio().before(fechaFin))).collect(Collectors.toList());
        } else { // no se especifica fecha
            result = trips;
        }

        if(precioMax!=null) {
            result = (ArrayList<Trip>) result.stream().filter(trip->trip.getPrecio().compareTo(precioMax)==-1).collect(Collectors.toList());
        }

        return result;

    }

    public void cambiarGrid(View view) {
        int numColumnas = switchColumnas.isChecked() ? 2 : 1;
        gridLayoutManager.setSpanCount(numColumnas);
        recyclerView.setLayoutManager(gridLayoutManager);
    }

    @Override
    public void onBackPressed() {
        Intent intent = new Intent(TripsActivity.this, EnlacesActivity.class);
        intent.putExtra("trips", totalTrips);
        startActivity(intent);
        super.onBackPressed();
    }
}