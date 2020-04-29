package us.master.entregable01;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
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
import android.widget.Toast;


import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.ArrayList;
import java.util.List;

import us.master.entregable01.entity.Enlace;
import us.master.entregable01.entity.Trip;

public class EnlacesActivity extends AppCompatActivity {

    private long backPressedTime;
    private Toast backToast;

    ArrayList<Enlace> enlaces;
    ListView listView;
    FloatingActionButton floatingActionButton;

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_enlaces);

        listView = findViewById(R.id.listView);
        floatingActionButton = findViewById(R.id.create_trip_floating_button);

        enlaces = Enlace.generaEnlaces();

        EnlacesAdapter adaptador = new EnlacesAdapter(EnlacesActivity.this, enlaces);
        listView.setAdapter(adaptador);

        AdapterView.OnItemClickListener listener = new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Enlace seleccionado = (Enlace) parent.getItemAtPosition(position);
                Intent intent = new Intent(EnlacesActivity.this, seleccionado.getClase());

                startActivity(intent);
                // podría hacerlo también con método onResume
            }
        };

        listView.setOnItemClickListener(listener);

        floatingActionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(EnlacesActivity.this, TripFormActivity.class);
                startActivity(intent);
            }
        });
    }

    @Override
    public void onBackPressed() {

        if (backPressedTime + 2000 > System.currentTimeMillis()) {
            backToast.cancel();
            FirebaseAuth.getInstance().signOut();
            super.onBackPressed();
            return;
        } else {
            backToast = Toast.makeText(getBaseContext(), "Pulsa de nuevo para salir", Toast.LENGTH_SHORT);
            backToast.show();
        }

        backPressedTime = System.currentTimeMillis();
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
