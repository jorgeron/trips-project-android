package us.master.entregable01;

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
import android.widget.Switch;
import android.widget.TextView;

import com.google.android.material.snackbar.Snackbar;
import com.squareup.picasso.Picasso;

import us.master.entregable01.entity.Trip;
import us.master.entregable01.entity.Util;

public class TripDetailActivity extends AppCompatActivity {

    ConstraintLayout constraintLayout;
    TextView textView_title,textView_precio, textView_fechaSalida, textView_fechaLlegada, textView_lugarSalida;
    ImageView imageView_trip;
    Switch switch_seleccionado;
    Button button_comprar;
    Trip trip;
    boolean hasChanged = false;
    int position, positionTotal;
    final Context context = this;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_trip_detail);

        constraintLayout=findViewById(R.id.constraintLayout);
        textView_title=findViewById(R.id.textView_trip_title);
        textView_precio=findViewById(R.id.textView_trip_precio);
        textView_fechaSalida=findViewById(R.id.textView_fechaSalida);
        textView_fechaLlegada=findViewById(R.id.textView_fechaLlegada);
        textView_lugarSalida=findViewById(R.id.textView_lugarSalida);
        imageView_trip=findViewById(R.id.imageView_trip_detail);
        switch_seleccionado=findViewById(R.id.switch_seleccionado);
        button_comprar=findViewById(R.id.buttonComprar);


        trip = (Trip) getIntent().getSerializableExtra("Trip");
        position = getIntent().getIntExtra("position", 0);
        positionTotal = getIntent().getIntExtra("positionTotal", 0);
        textView_title.setText(trip.getTitulo());
        textView_precio.setText(trip.getPrecio().toString());
        textView_fechaSalida.setText(Util.formateaFecha(trip.getFechaInicio()));
        textView_fechaLlegada.setText(Util.formateaFecha(trip.getFechaFin()));
        textView_lugarSalida.setText(trip.getLugarSalida());
        switch_seleccionado.setChecked(trip.isSeleccionado());
        button_comprar.setEnabled(!trip.isComprado());
        button_comprar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(
                        context);

                // set title
                alertDialogBuilder.setTitle("Comprar viaje");

                // set dialog message
                alertDialogBuilder
                        .setMessage("¿Está seguro que desea comprar este viaje?")
                        .setCancelable(false)
                        .setPositiveButton("Sí",new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                trip.setComprado(true);
                                button_comprar.setEnabled(false);
                                hasChanged = true;
                                dialog.cancel();
                                Snackbar snackbar = Snackbar.make(constraintLayout,
                                        "Has comprado el viaje a " + trip.getTitulo(),
                                        Snackbar.LENGTH_LONG);
                                snackbar.show();
                            }
                        })
                        .setNegativeButton("No",new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog,int id) {
                                // if this button is clicked, just close
                                // the dialog box and do nothing
                                dialog.cancel();
                            }
                        });

                // create alert dialog
                AlertDialog alertDialog = alertDialogBuilder.create();

                // show it
                alertDialog.show();
            }
        });

        Picasso.get().setLoggingEnabled(true);
        Picasso.get()
                .load(trip.getUrlImagen())
                .placeholder(R.drawable.available_trips)
                .into(imageView_trip);
    }

    public void onSwitchSeleccionado(View view) {
        boolean seleccionado = trip.isSeleccionado();
        trip.setSeleccionado(!seleccionado);
        hasChanged = true;
    }


    @Override
    public void onBackPressed() {
        Intent intent = getIntent();
        intent.putExtra("hasChanged", hasChanged);
        intent.putExtra("trip", trip);
        intent.putExtra("position", position);
        intent.putExtra("positionTotal", positionTotal);
        intent.putExtra("requestCode", 2);
        setResult(RESULT_OK, intent);

        super.onBackPressed();
    }
}
