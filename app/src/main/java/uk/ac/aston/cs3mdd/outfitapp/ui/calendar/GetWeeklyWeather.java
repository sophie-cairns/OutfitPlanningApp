package uk.ac.aston.cs3mdd.outfitapp.ui.calendar;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;
public interface GetWeeklyWeather {
    @GET("onecall")
    Call<WeeklyWeather> getWeekly(@Query("lat") double lat,
                                    @Query("lon") double lon,
                                    @Query("exclude") String exclude,
                                    @Query("units") String units,
                                    @Query("appid") String apikey);
}
