package com.personalprojects; // PAQUETE ACTUALIZADO

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.personalprojects.db.UsuariosDAO;
import com.personalprojects.model.Usuario;
import com.personalprojects.util.SessionManager;

public class LoginActivity extends AppCompatActivity {

    private TextInputEditText editTextNombreUsuario, editTextContrasena;
    private Button buttonIniciarSesion;
    private TextView textViewIrARegistro, textViewRecuperarContrasena;
    private UsuariosDAO usuariosDAO;
    private SessionManager sessionManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        usuariosDAO = new UsuariosDAO(this);
        sessionManager = new SessionManager(getApplicationContext());

        // Si ya está logueado, ir a ProyectosActivity directamente
        if (sessionManager.isLoggedIn()) {
            Intent intent = new Intent(LoginActivity.this, ProyectosActivity.class);
            startActivity(intent);
            finish();
            return; // Importante para no continuar con el resto del onCreate
        }

        editTextNombreUsuario = findViewById(R.id.editTextNombreUsuario);
        editTextContrasena = findViewById(R.id.editTextContrasena);
        buttonIniciarSesion = findViewById(R.id.buttonIniciarSesion);
        textViewIrARegistro = findViewById(R.id.textViewIrARegistro);
        textViewRecuperarContrasena = findViewById(R.id.textViewRecuperarContrasena);

        buttonIniciarSesion.setOnClickListener(v -> loginUsuario());

        textViewIrARegistro.setOnClickListener(v -> {
            Intent intent = new Intent(LoginActivity.this, RegistroActivity.class);
            startActivity(intent);
        });

        textViewRecuperarContrasena.setOnClickListener(v -> simularRecuperacionContrasena());
    }

    private void loginUsuario() {
        String nombreUsuario = editTextNombreUsuario.getText().toString().trim();
        String contrasena = editTextContrasena.getText().toString().trim();

        if (TextUtils.isEmpty(nombreUsuario)) {
            editTextNombreUsuario.setError("Nombre de usuario requerido");
            editTextNombreUsuario.requestFocus();
            return;
        }
        if (TextUtils.isEmpty(contrasena)) {
            editTextContrasena.setError("Contraseña requerida");
            editTextContrasena.requestFocus();
            return;
        }

        Usuario usuario = usuariosDAO.validarUsuario(nombreUsuario, contrasena);

        if (usuario != null) {
            sessionManager.createLoginSession(usuario.getIdUsuario(), usuario.getNombreUsuario());
            Toast.makeText(this, "Inicio de sesión exitoso", Toast.LENGTH_SHORT).show();
            Intent intent = new Intent(LoginActivity.this, ProyectosActivity.class);
            // Limpiar stack de actividades para que no pueda volver al login con el botón "atrás"
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
            finish();
        } else {
            Toast.makeText(this, "Nombre de usuario o contraseña incorrectos", Toast.LENGTH_LONG).show();
        }
    }

    private void simularRecuperacionContrasena() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        LayoutInflater inflater = getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.dialog_recuperar_contrasena, null); // Necesitas crear este layout
        builder.setView(dialogView);

        final EditText etNombreUsuarioRecuperar = dialogView.findViewById(R.id.editTextNombreUsuarioRecuperar);
        final EditText etEmailRecuperar = dialogView.findViewById(R.id.editTextEmailRecuperar);

        builder.setTitle("Recuperar Contraseña (Simulación)");
        builder.setPositiveButton("Buscar", (dialog, which) -> {
            String nombreUsuario = etNombreUsuarioRecuperar.getText().toString().trim();
            String email = etEmailRecuperar.getText().toString().trim();

            if (TextUtils.isEmpty(nombreUsuario) || TextUtils.isEmpty(email)) {
                Toast.makeText(this, "Por favor, ingrese nombre de usuario y email.", Toast.LENGTH_SHORT).show();
                return;
            }

            String contrasenaRecuperada = usuariosDAO.obtenerContrasenaPorNombreUsuarioYEmail(nombreUsuario, email);
            if (contrasenaRecuperada != null) {
                new AlertDialog.Builder(this)
                        .setTitle("Contraseña Encontrada")
                        .setMessage("Tu contraseña es: " + contrasenaRecuperada + "\n(En una app real, esto se manejaría de forma segura, ej. email con link de reseteo).")
                        .setPositiveButton("Entendido", null)
                        .show();
            } else {
                Toast.makeText(this, "No se encontró un usuario con ese nombre y email.", Toast.LENGTH_LONG).show();
            }
        });
        builder.setNegativeButton("Cancelar", (dialog, which) -> dialog.dismiss());
        builder.create().show();
    }
}