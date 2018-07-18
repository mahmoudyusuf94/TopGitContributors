package com.example.blink22.topgitcontributors;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.GET;

public interface GetDataService {

    @GET("stats/contributors")
    Call<List<Contributor>> getAllContributors();

}
