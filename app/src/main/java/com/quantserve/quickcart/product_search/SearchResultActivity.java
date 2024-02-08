package com.quantserve.quickcart.product_search;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;

import com.quantserve.quickcart.ProductNotFound;
import com.quantserve.quickcart.R;
import com.quantserve.quickcart.search_api.API_SERVICE;
import com.quantserve.quickcart.search_api.ProductName;
import com.quantserve.quickcart.search_api.api_response;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class SearchResultActivity extends AppCompatActivity {
    private List<ProductModel> productList=new ArrayList<>();
    private ProductAdapter adapter;
    private RecyclerView recyclerView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_result);
        Intent intent=getIntent();
        String productQuery=intent.getStringExtra("productQuery");
        recyclerView=findViewById(R.id.searchedProductRecycleView);
        recyclerView.setLayoutManager(new LinearLayoutManager(SearchResultActivity.this));


        Call<api_response> res= API_SERVICE.getPostService().GetProductList(new ProductName(productQuery));
        res.enqueue(new Callback<api_response>() {
            @Override
            public void onResponse(Call<api_response> call, Response<api_response> response) {
                productList=response.body().getProductList();
                if(productList!=null){
                    adapter=new ProductAdapter(productList);
                    recyclerView.setAdapter(adapter);
                    adapter.notifyDataSetChanged();
                }
                else productNotFoundPage();

            }

            @Override
            public void onFailure(Call<api_response> call, Throwable t) {

            }
        });





    }

    private void productNotFoundPage() {
            Intent intent=new Intent(this, ProductNotFound.class);
            startActivity(intent);
            finish();
    }
}