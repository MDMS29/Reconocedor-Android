package com.example.myapplication;

import androidx.appcompat.app.AppCompatActivity;

import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;

import android.widget.Button;

public class MenuPrincipal extends AppCompatActivity {

    Button btnReconocer, btnAcercaDe, btnSalir;
    Dialog dialog;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menu_principal);

        btnReconocer = findViewById(R.id.btnReconoer);
        btnAcercaDe = findViewById(R.id.btnAcercaDe);
        btnSalir = findViewById(R.id.btnSalir);
        dialog = new Dialog(this);

        btnReconocer.setOnClickListener(view -> startActivity(new Intent(MenuPrincipal.this, MainActivity.class)));
        btnAcercaDe.setOnClickListener(view -> mostrarDialog());
        btnSalir.setOnClickListener(view ->finish());
    }

    private void mostrarDialog(){
        Button btnEntendido;
        dialog.setContentView(R.layout.acerca_de);

        btnEntendido = dialog.findViewById(R.id.btnEntendido);
        btnEntendido.setOnClickListener(view -> dialog.dismiss());
        dialog.show();
        dialog.setCanceledOnTouchOutside(false); //PARA QUE AL PRESIONAR AL ALREDEDOR NO SE CIERRE
    }
}