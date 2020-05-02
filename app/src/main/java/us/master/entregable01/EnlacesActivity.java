package us.master.entregable01;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
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

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import us.master.entregable01.entity.Enlace;
import us.master.entregable01.entity.Trip;
import us.master.entregable01.entity.Util;
import us.master.entregable01.resttypes.WeatherResponse;
import us.master.entregable01.resttypes.WeatherRetrofitInterface;

public class EnlacesActivity extends AppCompatActivity {

    private long backPressedTime;
    private Toast backToast;

    ArrayList<Enlace> enlaces;
    ListView listView;
    FloatingActionButton floatingActionButton;

    TextView textView_welcome, textView_temperature, textView_city;
    private Retrofit retrofit;

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_enlaces);

        listView = findViewById(R.id.listView);
        floatingActionButton = findViewById(R.id.create_trip_floating_button);

        retrofit = new Retrofit.Builder().baseUrl("https://api.openweathermap.org/")
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        textView_welcome = findViewById(R.id.textView_welcome);
        textView_temperature = findViewById(R.id.textView_temperature);
        textView_city = findViewById(R.id.textView_city);
        renderizaCampos();

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

    private void renderizaCampos() {
        String username = FirebaseAuth.getInstance().getCurrentUser().getDisplayName();
        if (username == null) {
            username = FirebaseAuth.getInstance().getCurrentUser().getEmail();
        }
        textView_welcome.setText("Bienvenido " + username);

        WeatherRetrofitInterface service = retrofit.create(WeatherRetrofitInterface.class);
        Call<WeatherResponse> response = service.getCurrentWeather((float)LocationActivity.lastLocation.getLatitude(), (float)LocationActivity.lastLocation.getLongitude(), getString(R.string.open_weather_api_key));
        response.enqueue(new Callback<WeatherResponse>() {
            @Override
            public void onResponse(Call<WeatherResponse> call, Response<WeatherResponse> response) {
                if(response.isSuccessful() && response.body() != null) {
                    String cityname = response.body().getName();
                    Double degrees = response.body().getMain().getTemp()-273.15;
                    textView_city.setText(cityname);
                    textView_temperature.setText(String.format("%.2f", degrees) + "ºC" );
                }
            }

            @Override
            public void onFailure(Call<WeatherResponse> call, Throwable t) {
                Log.i("TripsApp", "REST: error en la llamada: " + t.getMessage());
            }
        });
    }

    @Override
    public void onBackPressed() {

        if (backPressedTime + 2000 > System.currentTimeMillis()) {
            backToast.cancel();
            FirebaseAuth.getInstance().signOut();
            super.onBackPressed();
            finishAffinity();
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
