package com.personalprojects; // PAQUETE ACTUALIZADO

import androidx.appcompat.app.AppCompatActivity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;

import com.personalprojects.util.SessionManager;

public class MainActivity extends AppCompatActivity {

    private SessionManager sessionManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main); // Layout simple con ProgressBar

        sessionManager = new SessionManager(getApplicationContext());

        // Pequeño retraso para simular carga o splash
        new Handler(Looper.getMainLooper()).postDelayed(() -> {
            if (sessionManager.isLoggedIn()) {
                Intent intent = new Intent(MainActivity.this, ProyectosActivity.class);
                startActivity(intent);
            } else {
                Intent intent = new Intent(MainActivity.this, LoginActivity.class);
                startActivity(intent);
            }
            finish(); // Finaliza MainActivity para que no se pueda volver con el botón atrás
        }, 1500); // 1.5 segundos de retraso
    }
}