package com.quantserve.quickcart;

import androidx.appcompat.app.AppCompatActivity;

import android.view.MotionEvent;
import android.view.inputmethod.InputMethodManager;
import android.widget.SearchView;
import android.content.Intent;
import android.os.Bundle;
import android.speech.RecognizerIntent;

import android.view.View;

import android.widget.ImageView;

import android.widget.Toast;

import com.google.android.material.search.SearchBar;

import com.quantserve.quickcart.product_search.SearchResultActivity;

import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Locale;
import java.util.Objects;

public class MainActivity extends AppCompatActivity {
    private SearchView editTextSearch;
    private ImageView imageViewMic,imageViewImageSearch;
    private static final int SPEECH_INPUT=1;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        editTextSearch=findViewById(R.id.editTextSearch);
        imageViewMic=findViewById(R.id.imageViewMic);
        imageViewImageSearch=findViewById(R.id.imageViewImageSearch);

        editTextSearch.setIconifiedByDefault(false);
        editTextSearch.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
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
                    if (editTextSearch != null && editTextSearch.hasFocus()) {
                        editTextSearch.clearFocus();
                        hideKeyboard(editTextSearch);
                    }
                }
                return false;
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

    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode== SPEECH_INPUT){
            if (resultCode== RESULT_OK && data !=null){
                ArrayList<String> result=data.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);
               // editTextSearch.setText(Objects.requireNonNull(result).get(0));
                searchProduct(Objects.requireNonNull(result).get(0));
            }
        }
    }
    private boolean searchProduct(String str){
         str=str.trim();
        //editTextSearch.setText(str);
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
}