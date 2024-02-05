package com.quantserve.quickcart.product_search;

import android.content.Context;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.quantserve.quickcart.MainActivity;
import com.quantserve.quickcart.R;

import java.util.List;

public class ProductAdapter extends RecyclerView.Adapter <ProductAdapter.ProductViewHolder>{
    private List<ProductModel> productList;
    private FirebaseStorage firebaseStorage;
    private StorageReference storageReference;
    public ProductAdapter(List<ProductModel> productList) {
        this.productList = productList;
    }

    @NonNull
    @Override
    public ProductViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view= LayoutInflater.from(parent.getContext()).inflate(R.layout.product_card,parent,false);
        return new ProductViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ProductViewHolder holder, int position) {
        ProductModel product=productList.get(position);
        firebaseStorage=FirebaseStorage.getInstance();
        storageReference=firebaseStorage.getReference();
        StorageReference st=storageReference.child("Product Images/"+product.getImage());
        st.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
            @Override
            public void onSuccess(Uri uri) {
                Glide.with(holder.context).load(uri).error(R.drawable.ic_launcher_background)
                        .into(holder.productImageView);
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {

            }
        });

            holder.productName.setText(product.getName());
            holder.productPrice.setText("$"+product.getPrice());
            holder.addToCart.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Toast.makeText(holder.addToCart.getContext(), "added successfully", Toast.LENGTH_SHORT).show();
                }
            });
    }

    @Override
    public int getItemCount() {
        return productList.size();
    }

    public static class ProductViewHolder extends RecyclerView.ViewHolder{
        TextView productName;
        TextView productPrice;
        Button addToCart;
        ImageView productImageView;
        Context context;
        public ProductViewHolder(@NonNull View itemView) {
            super(itemView);
            productName=itemView.findViewById(R.id.productNameTextView);
            productPrice=itemView.findViewById(R.id.productPriceTextView);
            addToCart=itemView.findViewById(R.id.addToCartButton);
            productImageView=itemView.findViewById(R.id.productImageView);
           context=itemView.getContext();
        }
    }

}
