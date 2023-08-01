package com.example.myapplication;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.ContentValues;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import androidx.navigation.ui.AppBarConfiguration;

import com.example.myapplication.databinding.ActivityMainBinding;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.mlkit.vision.common.InputImage;
import com.google.mlkit.vision.text.Text;
import com.google.mlkit.vision.text.TextRecognition;
import com.google.mlkit.vision.text.TextRecognizer;
import com.google.mlkit.vision.text.latin.TextRecognizerOptions;

import java.io.IOException;

public class MainActivity extends AppCompatActivity {
    private AppBarConfiguration appBarConfiguration;
    private ActivityMainBinding binding;
    private Button ReconcerTexto;
    private ImageView imagen;
    private EditText TextoReonocidoEt;
    private Uri uri = null;

    private ProgressDialog progressDialog;

    private TextRecognizer textRecognizer;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        setSupportActionBar(binding.toolbar);

        ReconcerTexto = findViewById(R.id.ReconocerTexto);
        imagen = findViewById(R.id.imagen);
        TextoReonocidoEt = findViewById(R.id.TextoReconocidoEt);

        progressDialog = new ProgressDialog(this);
        progressDialog.setTitle("Espere por favor");
        progressDialog.setCanceledOnTouchOutside(false);

        textRecognizer = TextRecognition.getClient(TextRecognizerOptions.DEFAULT_OPTIONS);

        ReconcerTexto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(uri == null ){
                    Toast.makeText(MainActivity.this, "Seleccione una imagen", Toast.LENGTH_SHORT).show();
                }else{
                    reconocerTextoDeImagen();
                }
            }
        });
    }

    private void reconocerTextoDeImagen() {
        progressDialog.setMessage("Preparando imagen...");
        progressDialog.show();

        try {
            InputImage inputImage = InputImage.fromFilePath(this, uri);
            progressDialog.setMessage("Reconociendo Texto...");
            Task<Text> textTask = textRecognizer.process(inputImage).addOnSuccessListener(new OnSuccessListener<Text>() {
                @Override
                public void onSuccess(Text text) {
                    progressDialog.dismiss();
                    String texto = text.getText();
                    TextoReonocidoEt.setText(texto);
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    progressDialog.dismiss();
                    Toast.makeText(MainActivity.this, "No se pudo reconocer el texto debido a: " +e.getMessage(), Toast.LENGTH_SHORT).show();
                }
            });
        } catch (IOException e) {
            progressDialog.dismiss();
            Toast.makeText(MainActivity.this, "Error al preparar la imagen: " +e.getMessage(), Toast.LENGTH_SHORT).show();

        }
    }

    private  void abrirGaleria(){
        Intent intent = new Intent(Intent.ACTION_PICK);
        intent.setType("image/*");
        galeriaARL.launch(intent);
    }
    private ActivityResultLauncher<Intent> galeriaARL = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            new ActivityResultCallback<ActivityResult>() {
                @Override
                public void onActivityResult(ActivityResult result) {
                    if (result.getResultCode() == Activity.RESULT_OK) {
                        Intent data = result.getData();
                        uri = data.getData();
                        imagen.setImageURI(uri);
                        TextoReonocidoEt.setText("");
                    } else {
                        Toast.makeText(MainActivity.this, "Cancelado por el usuario", Toast.LENGTH_SHORT).show();
                    }
                }
            }
    );

    private void abrirCamara(){
        ContentValues values = new ContentValues();
        values.put(MediaStore.Images.Media.TITLE, "Titulo");
        values.put(MediaStore.Images.Media.DESCRIPTION, "Descripcion");
        uri = getContentResolver().insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values);
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        intent.putExtra(MediaStore.EXTRA_OUTPUT, uri);
        camaraARL.launch(intent);
    }

    private ActivityResultLauncher<Intent> camaraARL = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            new ActivityResultCallback<ActivityResult>() {
                @Override
                public void onActivityResult(ActivityResult result) {
                    if(result.getResultCode() == Activity.RESULT_OK){
                        imagen.setImageURI(uri);
                        TextoReonocidoEt.setText("");
                    }else{
                        Toast.makeText(MainActivity.this, "Cancelado por el usuario", Toast.LENGTH_SHORT).show();
                    }
                }
            }
    );
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.mi_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if(item.getItemId() == R.id.menu_abrir_galeria){
            abrirGaleria();
        }
        if(item.getItemId() == R.id.menu_abrir_camara){
            abrirCamara();
        }
        return super.onOptionsItemSelected(item);
    }
}