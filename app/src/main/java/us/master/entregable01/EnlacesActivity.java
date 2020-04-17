package us.master.entregable01;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;


import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import us.master.entregable01.entity.Enlace;
import us.master.entregable01.entity.Trip;

public class EnlacesActivity extends AppCompatActivity {

    ArrayList<Enlace> enlaces;
    //RecyclerView recyclerView;
    ListView listView;
    ArrayList<Trip> trips;

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_enlaces);

        listView = findViewById(R.id.listView);
        enlaces = Enlace.generaEnlaces();

        if (getIntent().hasExtra("trips")) {
            trips = (ArrayList<Trip>) getIntent().getExtras().get("trips");
        } else {
            trips = Trip.generaTrips(50);
        }

        EnlacesAdapter adaptador = new EnlacesAdapter(EnlacesActivity.this, enlaces);
        listView.setAdapter(adaptador);

        AdapterView.OnItemClickListener listener = new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Enlace seleccionado = (Enlace) parent.getItemAtPosition(position);
                Intent intent = new Intent(EnlacesActivity.this, seleccionado.getClase());
                intent.putExtra("trips", trips);
                startActivity(intent);
                finish();
                // podría hacerlo también con método onResume
            }
        };

        listView.setOnItemClickListener(listener);
    }


}

class EnlacesAdapter extends ArrayAdapter<Enlace> {

    List<Enlace> enlaces;

    public EnlacesAdapter(@NonNull Context context, @NonNull List<Enlace> enlaces) {
        super(context, R.layout.enlace_item, enlaces);
        this.enlaces = enlaces;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        LayoutInflater layoutInflater = LayoutInflater.from(getContext());
        convertView = layoutInflater.inflate(R.layout.enlace_item, null);
        TextView textViewEnlace = convertView.findViewById(R.id.textView_enlace);
        ImageView imageViewEnlace = convertView.findViewById(R.id.imageView_trip);


        textViewEnlace.setText(enlaces.get(position).getNombre());
        imageViewEnlace.setImageDrawable(getContext().getDrawable(enlaces.get(position).getRecursoImageView()));
        return convertView;

    }

}
