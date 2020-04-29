package us.master.entregable01;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Switch;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.squareup.picasso.Picasso;

import java.util.HashMap;

import us.master.entregable01.database.FirestoreService;
import us.master.entregable01.entity.Trip;
import us.master.entregable01.entity.Util;

public class TripDetailsActivity extends AppCompatActivity {

    LinearLayout linearLayout;
    TextView textView_title,textView_precio, textView_fechaSalida, textView_fechaLlegada, textView_lugarSalida;
    ImageView imageView_trip;
    Switch switch_seleccionado;
    Button button_comprar, button_delete;
    Trip trip;
    final Context context = this;

    String idTrip;
    FirestoreService firestoreService;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_trip_details);

        linearLayout=findViewById(R.id.linearLayout);
        textView_title=findViewById(R.id.textView_trip_title);
        textView_precio=findViewById(R.id.textView_trip_precio);
        textView_fechaSalida=findViewById(R.id.textView_fechaSalida);
        textView_fechaLlegada=findViewById(R.id.textView_fechaLlegada);
        textView_lugarSalida=findViewById(R.id.textView_lugarSalida);
        imageView_trip=findViewById(R.id.imageView_trip_detail);
        switch_seleccionado=findViewById(R.id.switch_seleccionado);
        button_comprar=findViewById(R.id.buttonComprar);
        button_delete=findViewById(R.id.delete_trip_button);


        firestoreService = FirestoreService.getServiceInstance();
        idTrip = getIntent().getStringExtra("idTrip");

        firestoreService.getTrip(idTrip, new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(@Nullable DocumentSnapshot documentSnapshot, @Nullable FirebaseFirestoreException e) {
                if (documentSnapshot != null && documentSnapshot.exists()) {
                    trip = documentSnapshot.toObject(Trip.class);
                    renderizaCampos();
                }
            }
        });


        button_comprar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(
                        context);

                alertDialogBuilder
                        .setTitle("Comprar viaje")
                        .setMessage("¿Está seguro que desea comprar este viaje?")
                        .setCancelable(false)
                        .setPositiveButton("Sí",new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                HashMap<String, Object> map = new HashMap<>();
                                map.put("comprado", true);
                                firestoreService.updateTrip(idTrip, map);

                                button_comprar.setEnabled(false);
                                button_comprar.setVisibility(View.GONE);
                                dialog.cancel();
                                Snackbar snackbar = Snackbar.make(linearLayout,
                                        "Has comprado el viaje a " + trip.getTitulo(),
                                        Snackbar.LENGTH_LONG);
                                snackbar.show();
                            }
                        })
                        .setNegativeButton("No",new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog,int id) {
                                dialog.cancel();
                            }
                        });

                AlertDialog alertDialog = alertDialogBuilder.create();
                alertDialog.show();
            }
        });


        button_delete.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(
                        context);

                alertDialogBuilder
                        .setTitle("Borrar viaje")
                        .setMessage("¿Está seguro que desea eliminar este viaje?")
                        .setCancelable(false)
                        .setPositiveButton("Sí",new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                firestoreService.deleteTrip(idTrip);
                                dialog.cancel();

                                Snackbar snackbar = Snackbar.make(linearLayout,
                                        "Has eliminado el viaje a " + trip.getTitulo(),
                                        Snackbar.LENGTH_LONG);
                                snackbar.show();

                                onBackPressed();
                            }
                        })
                        .setNegativeButton("No",new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog,int id) {
                                dialog.cancel();
                            }
                        });

                AlertDialog alertDialog = alertDialogBuilder.create();
                alertDialog.show();
            }
        });

    }

    public void onSwitchSeleccionado(View view) {
        boolean seleccionado = trip.isSeleccionado();
        HashMap<String, Object> map = new HashMap<>();
        map.put("seleccionado", !seleccionado);

        firestoreService.updateTrip(idTrip, map);
    }

    private void renderizaCampos() {
        textView_title.setText(trip.getTitulo());
        textView_precio.setText(trip.getPrecio().toString());
        textView_fechaSalida.setText(Util.formateaFecha(trip.getFechaInicio()));
        textView_fechaLlegada.setText(Util.formateaFecha(trip.getFechaFin()));
        textView_lugarSalida.setText(trip.getLugarSalida());
        switch_seleccionado.setChecked(trip.isSeleccionado());
        button_comprar.setVisibility(View.VISIBLE);
        if(trip.isComprado()) {
            button_comprar.setVisibility(View.GONE);
        }
        button_comprar.setEnabled(!trip.isComprado());

        Glide.with(TripDetailsActivity.this)
                .load(trip.getUrlImagen())
                .placeholder(R.drawable.available_trips)
                .centerCrop()
                .into(imageView_trip);

        /*
        Picasso.get().setLoggingEnabled(true);
        Picasso.get()
                .load(trip.getUrlImagen())
                .fit().centerInside()
                .rotate(90)
                .placeholder(R.drawable.available_trips)
                .into(imageView_trip);

         */


    }


    @Override
    public void onBackPressed() {
        Intent intent = getIntent();
        intent.putExtra("requestCode", 2);
        setResult(RESULT_OK, intent);

        super.onBackPressed();
    }
}
