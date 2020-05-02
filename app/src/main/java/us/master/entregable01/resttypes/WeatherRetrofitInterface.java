package us.master.entregable01.resttypes;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface WeatherRetrofitInterface {
    @GET("data/2.5/weather")
    Call<WeatherResponse> getCurrentWeather(@Query("lat") float latitude, @Query("lon") float longitude, @Query("appid") String appId);
}
