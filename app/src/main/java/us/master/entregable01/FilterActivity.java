package us.master.entregable01;

import androidx.appcompat.app.AppCompatActivity;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.NumberPicker;
import android.widget.TextView;

import java.util.Calendar;
import java.util.GregorianCalendar;

public class FilterActivity extends AppCompatActivity {

    private TextView textView_startDate, textView_endDate;
    private EditText precio_max;
    private Calendar currentDate = Calendar.getInstance();
    private Calendar startDate, endDate;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_filter);

        textView_startDate = findViewById(R.id.textView_startdate);
        textView_endDate = findViewById(R.id.textView_enddate);
        precio_max = findViewById(R.id.editText_precio_max);
    }

    public void setStartDate(View view) {
        int yy = currentDate.get(Calendar.YEAR);
        int mm = currentDate.get(Calendar.MONTH);
        int dd = currentDate.get(Calendar.DAY_OF_MONTH);

        DatePickerDialog dialog = new DatePickerDialog(this, new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker datePicker, int year, int month, int day) {
                startDate = new GregorianCalendar(year, month, day);
                textView_startDate.setText(day+"/"+(month+1)+"/"+year);
            }
        },yy,mm,dd);
        dialog.getDatePicker().setMinDate(currentDate.getTimeInMillis());
        dialog.show();
    }

    public void setEndDate(View view) {
        Calendar minDate = startDate == null ? currentDate : startDate;

        int yy = minDate.get(Calendar.YEAR);
        int mm = minDate.get(Calendar.MONTH);
        int dd = minDate.get(Calendar.DAY_OF_MONTH);


        DatePickerDialog dialog = new DatePickerDialog(this, new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker datePicker, int year, int month, int day) {
                endDate = new GregorianCalendar(year, month, day);
                textView_endDate.setText(day+"/"+(month+1)+"/"+year);
            }
        },yy,mm,dd);

        dialog.getDatePicker().setMinDate(minDate.getTimeInMillis());
        dialog.show();
    }


    public void lanzarFiltro(View view) {
        String valorTextEdit = precio_max.getText().toString();
        Integer precio_maximo = null;
        if (valorTextEdit != null && valorTextEdit.length()>0) {
            precio_maximo = Integer.parseInt(valorTextEdit);
        }

        Intent intent = new Intent();
        intent.putExtra("hayFiltros", true);
        intent.putExtra("startDate", startDate);
        intent.putExtra("endDate", endDate);
        intent.putExtra("precioMaximo", precio_maximo);
        intent.putExtra("requestCode", 1);
        setResult(RESULT_OK, intent);
        finish();
    }
}
