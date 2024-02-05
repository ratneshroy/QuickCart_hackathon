package com.quantserve.quickcart.search_api;

import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Call;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.http.Body;
import retrofit2.http.Headers;
import retrofit2.http.POST;
public class API_SERVICE {
    static String Domain="https://ratneshroy.pythonanywhere.com";

    public static PostService postService=null;


    public static  PostService getPostService(){
        HttpLoggingInterceptor loggingInterceptor=new HttpLoggingInterceptor();
        loggingInterceptor.setLevel(HttpLoggingInterceptor.Level.BODY);
        OkHttpClient okHttpClient=new OkHttpClient.Builder()
                .addInterceptor(loggingInterceptor)
                .build();
        if(postService==null){

            Retrofit retrofit=new Retrofit.Builder()
                    .baseUrl(Domain)
                    .client(okHttpClient)
                    .addConverterFactory(GsonConverterFactory.create())
                    .build();
            postService=retrofit.create(PostService.class);
        }
        return postService;
    }
    public interface PostService{

//        @Headers("Content-Type:application/json")
//        @POST("/token/generate-token1")
//        Call<ApiResponse> Usercheck(@Body UserAuthentication userAuthentication);
        @Headers({"Content-Type:application/json"})
        @POST("/search")
        Call<api_response> GetAttendance(@Body ProductName productName);

//        @Headers("Content-Type:application/json")
//        @GET("CampusPortalSOA/image/studentPhoto")
//        Call<ResponseBody> getPhoto();


    }

}
