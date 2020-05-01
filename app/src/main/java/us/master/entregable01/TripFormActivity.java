package us.master.entregable01;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.app.DatePickerDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.StrictMode;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.ScrollView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.snackbar.BaseTransientBottomBar;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.GeoPoint;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.File;
import java.util.Calendar;
import java.util.GregorianCalendar;

import us.master.entregable01.database.FirestoreService;
import us.master.entregable01.entity.Trip;
import us.master.entregable01.entity.Util;

public class TripFormActivity extends AppCompatActivity implements OnMapReadyCallback {

    private static final int CAMERA_PERMISSION_REQUEST = 0x512;
    private static final int WRITE_EXTERNAL_STORAGE_PERMISSION_REQUEST = 0x513;
    private static final int TAKE_PHOTO_CODE = 0x514;

    private ScrollView scrollView;

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

    private Button take_picture_button;
    private ImageView take_picture_img;
    private ProgressBar progressBar;

    private GoogleMap googleMap;

    private Calendar currentDate = Calendar.getInstance();
    private Trip trip;
    private FirestoreService firestoreService;
    private String file;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_trip_form);

        trip = new Trip();

        scrollView = findViewById(R.id.scroll_view);

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

        take_picture_button = findViewById(R.id.take_picture_button);
        take_picture_img = findViewById(R.id.take_picture_img);
        progressBar = findViewById(R.id.progress_bar_img);
        progressBar.setVisibility(View.GONE);

        SupportMapFragment supportMapFragment = (WorkaroundMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        supportMapFragment.getMapAsync(this);
        
        take_picture_button.setOnClickListener(l -> {
            takePicture();
        });

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

    private void takePicture() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
            if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.CAMERA)) {
                // Si hay que explicar al usuario en qué consiste el permiso
                Snackbar.make(take_picture_button, R.string.take_picture_rationale, BaseTransientBottomBar.LENGTH_LONG).setAction(R.string.take_picture_rationale_ok, click -> {
                    ActivityCompat.requestPermissions(this, new String[] {Manifest.permission.CAMERA}, CAMERA_PERMISSION_REQUEST);
                });
            } else {
                ActivityCompat.requestPermissions(this, new String[] {Manifest.permission.CAMERA}, CAMERA_PERMISSION_REQUEST);
            }
        } else {
            // EL permiso está concedido

            if (ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
                    // Si hay que explicar al usuario en qué consiste el permiso
                    Snackbar.make(take_picture_button, R.string.take_picture_rationale, BaseTransientBottomBar.LENGTH_LONG).setAction(R.string.take_picture_rationale_ok, click -> {
                        ActivityCompat.requestPermissions(this, new String[] {Manifest.permission.WRITE_EXTERNAL_STORAGE}, WRITE_EXTERNAL_STORAGE_PERMISSION_REQUEST);
                    });
                } else {
                    ActivityCompat.requestPermissions(this, new String[] {Manifest.permission.WRITE_EXTERNAL_STORAGE}, WRITE_EXTERNAL_STORAGE_PERMISSION_REQUEST);
                }
            } else {
                // Todos permisos concedidos

                // ELiminar política privacidad
                StrictMode.VmPolicy.Builder builder = new StrictMode.VmPolicy.Builder();
                StrictMode.setVmPolicy(builder.build());

                String dir = this.getExternalFilesDir(Environment.DIRECTORY_PICTURES) + "/tripsapp/";
                File newFile = new File(dir);
                newFile.mkdirs();

                file = dir + Calendar.getInstance().getTimeInMillis() + ".jpg";
                File newFilePicture = new File(file);

                try {
                    newFilePicture.createNewFile();
                } catch (Exception ignore) {
                }

                Uri outputFileDir = Uri.fromFile(newFilePicture);
                Intent cameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                cameraIntent.putExtra(MediaStore.EXTRA_OUTPUT, outputFileDir);
                startActivityForResult(cameraIntent, TAKE_PHOTO_CODE);
            }
        }
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        
        if (requestCode == TAKE_PHOTO_CODE && resultCode == RESULT_OK) {
            File filePicture = new File(file);

            progressBar.setVisibility(View.VISIBLE);
            take_picture_img.setVisibility(View.GONE);

            FirebaseStorage storage = FirebaseStorage.getInstance();
            StorageReference storageReference = storage.getReference().child("users").child(FirebaseAuth.getInstance().getUid()).child(filePicture.getName());
            UploadTask uploadTask = storageReference.putFile(Uri.fromFile(filePicture));

            uploadTask.addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {
                    Log.i("TripsApp", "FirebaseStorage completed: " + task.getResult().getTotalByteCount());

                    storageReference.getDownloadUrl().addOnCompleteListener(new OnCompleteListener<Uri>() {
                        @Override
                        public void onComplete(@NonNull Task<Uri> task) {
                            if (task.isSuccessful()) {
                                Log.i("TripsAPP", "URL download image: " + task.getResult());
                                Glide.with(TripFormActivity.this)
                                        .load(task.getResult())
                                        .centerCrop()
                                        .into(take_picture_img);
                                trip.setUrlImagen(task.getResult().toString());

                                progressBar.setVisibility(View.GONE);
                                take_picture_img.setVisibility(View.VISIBLE);
                            }
                        }
                    });
                }
            });

            uploadTask.addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Log.e("TripsApp", "FirebaseStorage error: " + e.getMessage());
                }
            });
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == CAMERA_PERMISSION_REQUEST) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                takePicture();
            } else {
                Toast.makeText(this, R.string.camera_not_granted, Toast.LENGTH_SHORT).show();
            }
        } else if (requestCode == WRITE_EXTERNAL_STORAGE_PERMISSION_REQUEST) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                takePicture();
            } else {
                Toast.makeText(this, R.string.storage_not_granted, Toast.LENGTH_SHORT).show();
            }
        }
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

    @Override
    public void onMapReady(GoogleMap googleMap) {
        this.googleMap = googleMap;
        googleMap.getUiSettings().setZoomGesturesEnabled(true);

        ((WorkaroundMapFragment) getSupportFragmentManager().findFragmentById(R.id.map))
                .setListener(new WorkaroundMapFragment.OnTouchListener() {
                    @Override
                    public void onTouch()
                    {
                        scrollView.requestDisallowInterceptTouchEvent(true);
                    }
                });


        googleMap.setOnMapClickListener(new GoogleMap.OnMapClickListener() {
            @Override
            public void onMapClick(LatLng latLng) {
                GeoPoint geoPoint = new GeoPoint(latLng.latitude, latLng.longitude);

                googleMap.addMarker(new MarkerOptions()
                .position(latLng));

                googleMap.animateCamera(CameraUpdateFactory.newLatLng(latLng));
                trip.setCoordenadasSalida(geoPoint);
            }
        });
    }
}
