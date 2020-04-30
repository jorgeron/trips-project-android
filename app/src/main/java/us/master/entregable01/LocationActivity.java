package us.master.entregable01;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.app.Activity;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.material.snackbar.Snackbar;

public class LocationActivity extends AppCompatActivity {

    private static final int PERMISSION_REQUEST_CODE_LOCATION = 0x123;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_location);

        TextView location = findViewById(R.id.location);

        String[] permissions = new String[] {Manifest.permission.ACCESS_FINE_LOCATION};
        if (ContextCompat.checkSelfPermission(this, permissions[0]) != PackageManager.PERMISSION_GRANTED) {
            if (ActivityCompat.shouldShowRequestPermissionRationale(this, permissions[0])) {
                Snackbar.make(location, R.string.location_needed, Snackbar.LENGTH_SHORT).setAction(R.string.location_ok, view -> {
                    ActivityCompat.requestPermissions(LocationActivity.this, permissions, PERMISSION_REQUEST_CODE_LOCATION);
                }).show();
            } else {
                ActivityCompat.requestPermissions(LocationActivity.this, permissions, PERMISSION_REQUEST_CODE_LOCATION);
            }
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == PERMISSION_REQUEST_CODE_LOCATION) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                startLocationService();

            } else {
                Toast.makeText(this, R.string.location_cancel, Toast.LENGTH_SHORT).show();
            }
        }

    }

    private void startLocationService() {
        FusedLocationProviderClient locationServices = LocationServices.getFusedLocationProviderClient(this);
        /* LOCALIZACIÓN PUNTUAL ---------------
        locationServices.getLastLocation().addOnCompleteListener(task -> {
           if (task.isSuccessful() && task.getResult() != null) {
               Location location = task.getResult();
               Log.i("TripsApp", "Location: " + location.getLatitude() + ", " + location.getLongitude() + ", acc: " + location.getAccuracy());
           }
        });
        --------------------------*/

        LocationRequest locationRequest = new LocationRequest();
        locationRequest.setInterval(3000); //Frecuencia en ms en la que recogemos los datos de la localización (margen aproximado, no es exacto)
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY); //Prioridad de este sistema de localización dentro de la APP. Puede aprovechar localización de otras APPs
        //locationRequest.setSmallestDisplacement(10); //Si el usuario no se desplaza más de Xmetros, no actualiza localización
        locationServices.requestLocationUpdates(locationRequest, locationCallback, null);
    }

    LocationCallback locationCallback = new LocationCallback() {
        @Override
        public void onLocationResult(LocationResult locationResult) {
            if (locationResult == null || locationResult.getLastLocation() == null || !locationResult.getLastLocation().hasAccuracy()) {
                return;
            }

            Location location = locationResult.getLastLocation();
            Log.i("TripsApp", "Location: " + location.getLatitude() + ", " + location.getLongitude() + ", acc: " + location.getAccuracy());
        }
    };

    public void stopService() {
        LocationServices.getFusedLocationProviderClient(this).removeLocationUpdates(locationCallback);
    }
}
