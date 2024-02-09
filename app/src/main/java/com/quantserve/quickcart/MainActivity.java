package com.quantserve.quickcart;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Dialog;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.provider.MediaStore;
import android.os.Bundle;
import android.provider.OpenableColumns;
import android.speech.RecognizerIntent;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.SearchView;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.quantserve.quickcart.product_search.ProductModel;
import com.quantserve.quickcart.product_search.SearchResultActivity;
import com.quantserve.quickcart.search_api.API_SERVICE;
import com.quantserve.quickcart.search_api.AddProductResponse;
import com.quantserve.quickcart.search_api.LastProductId;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.Locale;
import java.util.Objects;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MainActivity extends AppCompatActivity {
    private SearchView searchView;
    private Uri uri;
    private ImageView imageViewMic,imageViewImageSearch;
    private static final int SPEECH_INPUT=1;
    private Button aNPButton,uEPButton;
    private static final int REQUEST_IMAGE_CAPTURE = 1;
    private static final int REQUEST_IMAGE_PICK = 2;


    /*-------------------------------------------------------------------------*/
    ImageView selectedImageView;
    TextView uidTextView;
    EditText  nameEditText, descriptionEditText, priceEditText, quantityEditText;
    Button pickFromGalleryButton, clickImageButton, uploadButton;
    ProgressBar progressBar;
    Bitmap bitmap;
    /*-------------------------------------------------------------------------*/

    private ActivityResultLauncher<Intent> galleryLauncher;
    private ActivityResultLauncher<Intent> cameraLauncher;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        searchView =findViewById(R.id.editTextSearch);
        imageViewMic=findViewById(R.id.imageViewMic);
        imageViewImageSearch=findViewById(R.id.imageViewImageSearch);
        aNPButton=findViewById(R.id.aNPButton);
        uEPButton=findViewById(R.id.uEPButton);

        searchView.setIconifiedByDefault(false);
        searchView.setQueryHint("Search Product");
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                searchProduct(query);
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                if(newText.isEmpty()){
                    imageViewImageSearch.setVisibility(View.VISIBLE);
                }
                else
                    imageViewImageSearch.setVisibility(View.GONE);
                // Handle the search query as the user types (optional)
                return false;
            }
        });
        findViewById(android.R.id.content).setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (event.getAction() == MotionEvent.ACTION_DOWN) {
                    if (searchView != null && searchView.hasFocus()) {
                        searchView.clearFocus();
                        hideKeyboard(searchView);
                    }
                }
                return false;
            }
        });

        // Initialize gallery and camera launchers
        galleryLauncher = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), result -> {
            if (result.getResultCode() == RESULT_OK && result.getData() != null) {
                handleGalleryResult(result.getData());
            }
        });

        cameraLauncher = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), result -> {
            if (result.getResultCode() == RESULT_OK && result.getData() != null) {

                handleCameraResult(result.getData());
            }
        });

        imageViewMic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent=new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
                intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL,RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
                intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, Locale.getDefault());
                intent.putExtra(RecognizerIntent.EXTRA_PROMPT, "speech to text");
                try {
                    startActivityForResult(intent,SPEECH_INPUT);
                }
                catch (Exception e){
                    Toast.makeText(MainActivity.this , "" + e.getMessage() , Toast.LENGTH_SHORT).show();
                }
            }
        });

        aNPButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showDialog();
            }
        });
        boolean openANPButton = getIntent().getBooleanExtra("openANPButton", false);
        if (openANPButton) {
            // Trigger the click event for aNPButton
            aNPButton.performClick();
        }


    }

    private boolean searchProduct(String str){
        str=str.trim();
        if(!str.isEmpty()) {
            Intent intent = new Intent(MainActivity.this, SearchResultActivity.class);
            intent.putExtra("productQuery", str);
            startActivity(intent);
            return true;
        }

        return false;
    }

    private void hideKeyboard(View view) {
        InputMethodManager imm = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
        if (imm != null) {
            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }
    }

    private void showDialog() {


        final Dialog dialog = new Dialog(this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.bottomsheetlayout);
        /*-------------------------------------------------------------------------*/
        selectedImageView = dialog.findViewById(R.id.selected_image_view);

        uidTextView = dialog.findViewById(R.id.uid_text_view);
        nameEditText = dialog.findViewById(R.id.name_edit_text);
        descriptionEditText = dialog.findViewById(R.id.description_edit_text);
        priceEditText = dialog.findViewById(R.id.price_edit_text);
        quantityEditText = dialog.findViewById(R.id.quantity_edit_text);
        pickFromGalleryButton = dialog.findViewById(R.id.pick_from_gallery_button);
        clickImageButton =dialog.findViewById(R.id.click_image_button);
        uploadButton = dialog.findViewById(R.id.upload_button);
        ImageView closeButton = dialog.findViewById(R.id.closeButton);
        progressBar=dialog.findViewById(R.id.progressBar);
        /*-------------------------------------------------------------------------*/
        Call<LastProductId> res = API_SERVICE.getPostService().GetLast_product_id();
        res.enqueue(new Callback<LastProductId>() {
            @Override
            public void onResponse(Call<LastProductId> call, Response<LastProductId> response) {
                uidTextView.setText(response.body().getLast_product_id().toString());

            }

            @Override
            public void onFailure(Call<LastProductId> call, Throwable t) {

            }
        });
        closeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss(); // Close the dialog when close button is clicked
            }
        });

        pickFromGalleryButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent galleryIntent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                galleryLauncher.launch(galleryIntent);
            }
        });

        clickImageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent cameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                cameraLauncher.launch(cameraIntent);
            }
        });

        uploadButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                progressBar.setVisibility(View.VISIBLE);
                uploadButton.setVisibility(View.GONE);
                Integer uid = Integer.parseInt(uidTextView.getText().toString());
                String name = nameEditText.getText().toString();
                String description = descriptionEditText.getText().toString();
                String price = priceEditText.getText().toString();
                Integer quantity =Integer.parseInt(quantityEditText.getText().toString()) ;
                String imageName=name;

                ProductModel productModel=new ProductModel(uid,name,description,price,quantity,imageName);

                FirebaseStorage firebaseStorage = FirebaseStorage.getInstance();
                StorageReference storageReference=firebaseStorage.getReference();
                if(uri!=null){
                storageReference.child("Product Images/"+imageName).putFile(uri)
                        .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {

                        addNewProduct(productModel);
                    }
                }).addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Toast.makeText(MainActivity.this, "failed", Toast.LENGTH_SHORT).show();
                            }
                        });
                }
                else {
                    ByteArrayOutputStream baos = new ByteArrayOutputStream();
                    bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);
                    byte[] data = baos.toByteArray();
                    storageReference.child("Product Images/"+imageName).putBytes(data)
                            .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                                @Override
                                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {

                                    addNewProduct(productModel);
                                }
                            }).addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    Toast.makeText(MainActivity.this, "failed", Toast.LENGTH_SHORT).show();
                                }
                            });

                }


            }

            private void addNewProduct(ProductModel productModel) {
                Call<AddProductResponse> res=API_SERVICE.getPostService().AddNewProduct(productModel);
                res.enqueue(new Callback() {
                    @Override
                    public void onResponse(Call call, Response response) {
                        Toast.makeText(MainActivity.this, "Product added successfully", Toast.LENGTH_SHORT).show();
                        progressBar.setVisibility(View.GONE);
                        dialog.dismiss();
                    }

                    @Override
                    public void onFailure(Call call, Throwable t) {
                        Toast.makeText(MainActivity.this, t.toString(), Toast.LENGTH_SHORT).show();
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

    private void handleGalleryResult(Intent data) {
        if (data != null) {
            uri=data.getData();
            selectedImageView.setImageURI(data.getData());


        }
    }

    private void handleCameraResult(Intent data) {
        if (data != null) {
            Bundle extras = data.getExtras();
            if (extras != null && extras.get("data") != null) {
               bitmap = (Bitmap) extras.get("data");
                selectedImageView.setImageBitmap(bitmap);

            }
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == SPEECH_INPUT) {
            if (resultCode == RESULT_OK && data != null) {
                ArrayList<String> result = data.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);
                searchProduct(Objects.requireNonNull(result).get(0));
            }
        }
    }

}
