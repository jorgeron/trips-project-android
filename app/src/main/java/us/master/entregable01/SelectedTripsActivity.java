package us.master.entregable01;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import us.master.entregable01.entity.Trip;

public class SelectedTripsActivity extends AppCompatActivity {

    ArrayList<Trip> selectedTrips;
    ArrayList<Trip> totalTrips;

    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (getIntent().hasExtra("trips")) {
            totalTrips = (ArrayList<Trip>) getIntent().getExtras().get("trips");
            selectedTrips = (ArrayList<Trip>) totalTrips.stream()
                    .filter(trip->trip.isSeleccionado()).collect(Collectors.toList());
        } else {
            selectedTrips = new ArrayList<>();
            totalTrips = null;
        }


        Intent intent = new Intent(SelectedTripsActivity.this, TripsActivity.class);
        intent.putExtra("trips", totalTrips);
        intent.putExtra("selectedTrips", selectedTrips);
        startActivity(intent);
        finish();

    }
}
