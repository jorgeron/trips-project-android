package us.master.entregable01;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.DatePickerDialog;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.firestore.DocumentReference;

import java.util.Calendar;
import java.util.GregorianCalendar;

import us.master.entregable01.database.FirestoreService;
import us.master.entregable01.entity.Trip;
import us.master.entregable01.entity.Util;

public class TripFormActivity extends AppCompatActivity {

    private EditText trip_titulo_et;
    private EditText trip_lugar_salida_et;
    private EditText trip_fecha_inicio_et;
    private EditText trip_fecha_fin_et;
    private EditText trip_precio_et;

    private TextInputLayout trip_titulo;
    private TextInputLayout trip_lugar_salida;
    private TextInputLayout trip_fecha_inicio;
    private TextInputLayout trip_fecha_fin;
    private TextInputLayout trip_precio;

    private Calendar currentDate = Calendar.getInstance();
    private Trip trip;
    private FirestoreService firestoreService;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_trip_form);

        trip = new Trip();

        trip_titulo_et = findViewById(R.id.trip_titulo_et);
        trip_lugar_salida_et = findViewById(R.id.trip_lugar_salida_et);
        trip_fecha_inicio_et = findViewById(R.id.trip_fecha_inicio_et);
        trip_fecha_fin_et = findViewById(R.id.trip_fecha_fin_et);
        trip_precio_et = findViewById(R.id.trip_precio_et);

        trip_titulo = findViewById(R.id.trip_titulo);
        trip_lugar_salida = findViewById(R.id.trip_lugar_salida);
        trip_fecha_inicio = findViewById(R.id.trip_fecha_inicio);
        trip_fecha_fin = findViewById(R.id.trip_fecha_fin);
        trip_precio = findViewById(R.id.trip_precio);

        findViewById(R.id.create_trip_button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (trip_titulo_et.getText().length() == 0) {
                    trip_titulo.setErrorEnabled(true);
                    trip_titulo.setError(getString(R.string.empty_field_error));
                } else if (trip_lugar_salida_et.getText().length() == 0) {
                    trip_lugar_salida.setErrorEnabled(true);
                    trip_lugar_salida.setError(getString(R.string.empty_field_error));
                } else if (trip_fecha_inicio_et.getText().length() == 0) {
                    trip_fecha_inicio.setErrorEnabled(true);
                    trip_fecha_inicio.setError(getString(R.string.empty_field_error));
                } else if (trip_fecha_fin_et.getText().length() == 0) {
                    trip_fecha_fin.setErrorEnabled(true);
                    trip_fecha_fin.setError(getString(R.string.empty_field_error));
                } else if (trip_precio_et.getText().length() == 0) {
                    trip_precio.setErrorEnabled(true);
                    trip_precio.setError(getString(R.string.empty_field_error));
                } else if (trip.getFechaInicio().compareTo(trip.getFechaFin()) > 0) {
                    trip_fecha_inicio.setErrorEnabled(true);
                    trip_fecha_fin.setErrorEnabled(true);
                    trip_fecha_inicio.setError(getString(R.string.dates_error));
                    trip_fecha_fin.setError(getString(R.string.dates_error));
                } else {
                    trip.setComprado(false);
                    trip.setSeleccionado(false);
                    trip.setTitulo(trip_titulo_et.getText().toString());
                    trip.setLugarSalida(trip_lugar_salida_et.getText().toString());
                    trip.setPrecio(Integer.parseInt(trip_precio_et.getText().toString()));


                    firestoreService = FirestoreService.getServiceInstance();

                    firestoreService.saveTrip(trip, new OnCompleteListener<DocumentReference>() {
                        @Override
                        public void onComplete(@NonNull Task<DocumentReference> task) {
                            if (task.isSuccessful()) {
                                Log.i("TripsApp", "Viaje insertado");
                                finish();
                            } else {
                                Log.i("TripsApp", "Error al insertar viaje " + task.getException());
                                Toast.makeText(TripFormActivity.this, R.string.trip_created_error, Toast.LENGTH_LONG).show();
                            }
                        }
                    });
                }



            }
        });
    }


    public void setStartDate(View view) {
        int yy = currentDate.get(Calendar.YEAR);
        int mm = currentDate.get(Calendar.MONTH);
        int dd = currentDate.get(Calendar.DAY_OF_MONTH);

        DatePickerDialog dialog = new DatePickerDialog(this, new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker datePicker, int year, int month, int day) {
                trip.setFechaInicio(Util.Calendar2long(new GregorianCalendar(year, month, day)));
                trip_fecha_inicio_et.setText(day+"/"+(month+1)+"/"+year);
            }
        },yy,mm,dd);
        dialog.getDatePicker().setMinDate(currentDate.getTimeInMillis());
        dialog.show();
    }


    public void setEndDate(View view) {
        Calendar minDate;
        if (trip.getFechaInicio() == null) {
            minDate = currentDate;
        } else {
            minDate = Util.Long2Calendar(trip.getFechaInicio());
        }

        int yy = minDate.get(Calendar.YEAR);
        int mm = minDate.get(Calendar.MONTH);
        int dd = minDate.get(Calendar.DAY_OF_MONTH);


        DatePickerDialog dialog = new DatePickerDialog(this, new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker datePicker, int year, int month, int day) {
                trip.setFechaFin(Util.Calendar2long(new GregorianCalendar(year, month, day)));
                trip_fecha_fin_et.setText(day+"/"+(month+1)+"/"+year);
            }
        },yy,mm,dd);

        dialog.getDatePicker().setMinDate(minDate.getTimeInMillis());
        dialog.show();
    }
}
