package com.quantserve.quickcart.search_api;

import com.quantserve.quickcart.product_search.ProductModel;

import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Call;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.http.Body;
import retrofit2.http.GET;
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


        @Headers({"Content-Type:application/json"})
        @POST("/search")
        Call<api_response> GetProductList(@Body ProductName productName);

        @Headers("Content-Type:application/json")
        @GET("/last_product_id")
        Call<LastProductId> GetLast_product_id();

        @Headers({"Content-Type:application/json"})
        @POST("/add_product")
        Call<AddProductResponse> AddNewProduct(@Body ProductModel productModel);

        @Headers({"Content-Type:application/json"})
        @POST("/update_product")
        Call<AddProductResponse> UpdateProduct(@Body ProductModel productModel);

    }

}
