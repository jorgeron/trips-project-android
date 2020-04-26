package us.master.entregable01.entity;

import android.os.Build;

import androidx.annotation.RequiresApi;

import java.text.DateFormat;
import java.time.Instant;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.Locale;
import java.util.Random;
import java.util.concurrent.ThreadLocalRandom;

public class Util {
    public static String formateaFecha(Calendar calendar) {
        int yy=calendar.get(Calendar.YEAR);
        int mm=calendar.get(Calendar.MONTH);
        int dd=calendar.get(Calendar.DAY_OF_MONTH);
        DateFormat formatoFecha=DateFormat.getDateInstance(DateFormat.MEDIUM, Locale.UK);
        calendar.setTimeInMillis(0);
        calendar.set(yy, mm, dd, 0, 0, 0);
        Date chosenDate = calendar.getTime();
        return(formatoFecha.format(chosenDate));

    }

    public static String formateaFecha(long fecha) {
        Calendar calendar=Calendar.getInstance();
        calendar.setTimeInMillis(fecha*1000);
        DateFormat formatoFecha=DateFormat.getDateInstance(DateFormat.MEDIUM, Locale.UK);
        Date chosenDate = calendar.getTime();
        return(formatoFecha.format(chosenDate));
    }

    public static long Calendar2long(Calendar fecha) {
        return(fecha.getTimeInMillis()/1000);
    }

    public static Calendar Long2Calendar(Long dateInLong) {
        Calendar result = Calendar.getInstance();
        result.setTimeInMillis(dateInLong*1000);
        return result;
    }

    public static int getRandomNumberInRange(int min, int max) {

        if (min >= max) {
            throw new IllegalArgumentException("max must be greater than min");
        }

        Random r = new Random();
        return r.nextInt((max - min) + 1) + min;
    }



    @RequiresApi(api = Build.VERSION_CODES.O)
    public static Calendar generateRandomDateBetween(Instant startInclusive, Instant endExclusive) {
        long startSeconds = startInclusive.getEpochSecond();
        long endSeconds = endExclusive.getEpochSecond();
        long random = ThreadLocalRandom
                .current()
                .nextLong(startSeconds, endSeconds);

        ZonedDateTime zdt = ZonedDateTime.ofInstant(Instant.ofEpochSecond(random), ZoneId.systemDefault());
        Calendar result = GregorianCalendar.from(zdt);
        return result;
    }


    /*public static Calendar generateRandomDate(int maxYear) {

        //GregorianCalendar gc = new GregorianCalendar();
        Calendar result = Calendar.getInstance();
        result.setTime(new Date());

        int year = getRandomNumberInRange(Calendar.YEAR, maxYear);
        result.set(Calendar.YEAR, year);

        int dayOfYear = getRandomNumberInRange(1, result.getActualMaximum(Calendar.DAY_OF_YEAR));
        result.set(Calendar.DAY_OF_YEAR, dayOfYear);

        //Calendar result = gc.getInstance();
        return result;
    }*/
}