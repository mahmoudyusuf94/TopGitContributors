package com.example.blink22.topgitcontributors;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;

public interface GetDataService {

    @GET("{owner}/{repo}/stats/contributors")
    Call<List<Contributor>> getAllContributors(@Path("owner") String owner, @Path("repo") String repo);

}
