package uk.ac.aston.cs3mdd.outfitapp.ui.home;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;
public interface GetTodaysWeather {
        @GET("onecall")
        Call<TodaysWeather> getForecast(@Query("lat") double lat,
                                   @Query("lon") double lon,
                                   @Query("exclude") String exclude,
                                   @Query("units") String units,
                                   @Query("appid") String apikey);
}
