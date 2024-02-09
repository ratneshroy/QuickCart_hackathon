package com.quantserve.quickcart.product_search;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import com.quantserve.quickcart.R;
import com.quantserve.quickcart.search_api.API_SERVICE;
import com.quantserve.quickcart.search_api.AddProductResponse;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.internal.EverythingIsNonNull;

public class ProductAdapter extends RecyclerView.Adapter <ProductAdapter.ProductViewHolder>{
    private List<ProductModel> productList;

    private FirebaseStorage firebaseStorage=FirebaseStorage.getInstance();
    private StorageReference storageReference=firebaseStorage.getReference();
    ImageView selectedImageView;
    TextView uidEditText;
    EditText nameEditText, descriptionEditText, priceEditText, quantityEditText;
    Button pickFromGalleryButton, clickImageButton, uploadButton;

    ProgressBar progressBar;

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
    public void onBindViewHolder(@NonNull ProductViewHolder holder, @SuppressLint("RecyclerView") int position) {

       ProductModel product=productList.get(position);

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
        setProductDetails(holder,product);

        holder.update.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    showDialogUpdate(holder,product, holder.context,position);
                }
            });
    }

    @Override
    public int getItemCount() {
        return productList.size();
    }

    public static class ProductViewHolder extends RecyclerView.ViewHolder{
        TextView productName,productQtyTextView,productUidTextView,productPrice;

        Button update;
        ImageView productImageView;
        Context context;
        public ProductViewHolder(@NonNull View itemView) {
            super(itemView);
            productUidTextView=itemView.findViewById(R.id.productUidTextView);
            productQtyTextView=itemView.findViewById(R.id.productQtyTextView);
            productName=itemView.findViewById(R.id.productNameTextView);
            productPrice=itemView.findViewById(R.id.productPriceTextView);
            update=itemView.findViewById(R.id.editProductDetails);
            productImageView=itemView.findViewById(R.id.productImageView);
           context=itemView.getContext();
        }
    }

    private void showDialogUpdate(ProductViewHolder holder,ProductModel productModel,Context context,int pos) {
        final Dialog dialog = new Dialog(context);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.bottomsheetlayout);
        /*-------------------------------------------------------------------------*/
        selectedImageView = dialog.findViewById(R.id.selected_image_view);

        uidEditText = dialog.findViewById(R.id.uid_text_view);
        nameEditText = dialog.findViewById(R.id.name_edit_text);
        descriptionEditText = dialog.findViewById(R.id.description_edit_text);
        priceEditText = dialog.findViewById(R.id.price_edit_text);
        quantityEditText = dialog.findViewById(R.id.quantity_edit_text);
        progressBar=dialog.findViewById(R.id.progressBar);
        pickFromGalleryButton = dialog.findViewById(R.id.pick_from_gallery_button);
        pickFromGalleryButton.setVisibility(View.GONE);
        clickImageButton =dialog.findViewById(R.id.click_image_button);
        clickImageButton.setVisibility(View.GONE);

        uploadButton = dialog.findViewById(R.id.upload_button);
        uploadButton.setText("Update");
        /*-------------------------------------------------------------------------*/
        ImageView closeButton = dialog.findViewById(R.id.closeButton);
        closeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss(); // Close the dialog when close button is clicked
            }
        });

        /*-------------------------------------------------------------------------*/
        uidEditText.setText(productModel.getId().toString());
        nameEditText.setText(productModel.getName());
        descriptionEditText.setText(productModel.getDescription());
        priceEditText.setText(productModel.getPrice());
        quantityEditText.setText(productModel.getQty().toString());

        /*-------------------------------------------------------------------------*/
        StorageReference st=storageReference.child("Product Images/"+productModel.getImage());

        st.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
            @Override
            public void onSuccess(Uri uri) {

                Glide.with(holder.context).load(uri).error(R.drawable.ic_launcher_background)
                        .into(selectedImageView);
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {

            }
        });
        uploadButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                uploadButton.setVisibility(View.GONE);
                progressBar.setVisibility(View.VISIBLE);
                String uid = uidEditText.getText().toString();
                String name = nameEditText.getText().toString();
                String description = descriptionEditText.getText().toString();
                String price = priceEditText.getText().toString();
                String quantity = quantityEditText.getText().toString();

                ProductModel updatedProduct=new ProductModel(Integer.parseInt(uid)
                        ,name,description,price,Integer.parseInt(quantity),productModel.getImage());

                Call<AddProductResponse> res= API_SERVICE.getPostService().UpdateProduct(updatedProduct);
                res.enqueue(new Callback<AddProductResponse>() {
                    @Override
                    public void onResponse(Call<AddProductResponse> call, Response<AddProductResponse> response) {
                        Toast.makeText(context, "Product details updated successfully", Toast.LENGTH_SHORT).show();
                        setProductDetails(holder,updatedProduct);
                        productList.set(pos,updatedProduct);

                        progressBar.setVisibility(View.GONE);
                        dialog.dismiss();
                    }

                    @Override
                    public void onFailure(Call<AddProductResponse> call, Throwable t) {
                        progressBar.setVisibility(View.GONE);
                        uploadButton.setVisibility(View.VISIBLE);
                    }
                });
            }
        });

        dialog.setCancelable(true);
        dialog.setCanceledOnTouchOutside(false);

        dialog.show();
        dialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT,ViewGroup.LayoutParams.WRAP_CONTENT);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.getWindow().getAttributes().windowAnimations = R.style.DialogAnimation;
        dialog.getWindow().setGravity(Gravity.BOTTOM);
    }

    private void setProductDetails(ProductViewHolder holder, ProductModel product){
        holder.productUidTextView.setText("Uid: "+ product.getId());
        holder.productName.setText("Name: "+product.getName());
        holder.productPrice.setText("Price: ₹"+product.getPrice());
        if(product.getQty()>0)
        holder.productQtyTextView.setText("Qty: "+ product.getQty());
        else {
            holder.productQtyTextView.setText("Not Available");
            holder.productQtyTextView.setTextColor(holder.itemView.getResources().getColor(R.color.red));
        }
    }

}
