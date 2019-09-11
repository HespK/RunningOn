package pacesong.pacesongdemo;

import android.support.annotation.NonNull;


import retrofit2.Call;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.http.GET;

/**
 * Service from retrofit framework which provides internet requests
 * how to use : {@code ServiceFactory.getPaceService().getPace}
 */

public interface PaceService {

    @GET("/v1/devices/1c002a000547343232363230/pace/?access_token=38d147666fbaed6f1d6383e620ee60866548cdd1")
    Call<Result> getPace();
}

class ServiceFactory {

    public static PaceService getPaceService() {
        return buildRetrofit().create(PaceService.class);
    }

    @NonNull
    private static Retrofit buildRetrofit() {
        return new Retrofit.Builder()
                .baseUrl("https://api.particle.io")
                .addConverterFactory(GsonConverterFactory.create())
                .build();
    }
}
