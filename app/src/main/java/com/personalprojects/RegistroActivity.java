package com.personalprojects; // PAQUETE ACTUALIZADO

import androidx.appcompat.app.AppCompatActivity;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import com.google.android.material.textfield.TextInputEditText;
import com.personalprojects.db.UsuariosDAO;
import com.personalprojects.model.Usuario;

public class RegistroActivity extends AppCompatActivity {

    private TextInputEditText editTextNombreUsuario, editTextEmail, editTextContrasena, editTextConfirmarContrasena;
    private Button buttonRegistrarse;
    private TextView textViewIrALogin;
    private UsuariosDAO usuariosDAO;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registro);

        usuariosDAO = new UsuariosDAO(this);

        editTextNombreUsuario = findViewById(R.id.editTextNombreUsuarioRegistro);
        editTextEmail = findViewById(R.id.editTextEmailRegistro);
        editTextContrasena = findViewById(R.id.editTextContrasenaRegistro);
        editTextConfirmarContrasena = findViewById(R.id.editTextConfirmarContrasenaRegistro);
        buttonRegistrarse = findViewById(R.id.buttonRegistrarse);
        textViewIrALogin = findViewById(R.id.textViewIrALogin);

        buttonRegistrarse.setOnClickListener(v -> registrarUsuario());

        textViewIrALogin.setOnClickListener(v -> {
            // Ir a LoginActivity
            Intent intent = new Intent(RegistroActivity.this, LoginActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP); // Limpia la pila sobre LoginActivity
            startActivity(intent);
            finish(); // Finaliza RegistroActivity
        });
    }

    private void registrarUsuario() {
        String nombreUsuario = editTextNombreUsuario.getText().toString().trim();
        String email = editTextEmail.getText().toString().trim(); // Opcional, pero validamos si se ingresa
        String contrasena = editTextContrasena.getText().toString().trim();
        String confirmarContrasena = editTextConfirmarContrasena.getText().toString().trim();

        if (TextUtils.isEmpty(nombreUsuario)) {
            editTextNombreUsuario.setError("Nombre de usuario requerido");
            editTextNombreUsuario.requestFocus();
            return;
        }
        // Simple validación de email si se ingresa algo
        if (!TextUtils.isEmpty(email) && !android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            editTextEmail.setError("Ingrese un email válido");
            editTextEmail.requestFocus();
            return;
        }
        if (TextUtils.isEmpty(contrasena)) {
            editTextContrasena.setError("Contraseña requerida");
            editTextContrasena.requestFocus();
            return;
        }
        if (contrasena.length() < 6) { // Ejemplo de validación de longitud de contraseña
            editTextContrasena.setError("La contraseña debe tener al menos 6 caracteres");
            editTextContrasena.requestFocus();
            return;
        }
        if (TextUtils.isEmpty(confirmarContrasena)) {
            editTextConfirmarContrasena.setError("Confirme la contraseña");
            editTextConfirmarContrasena.requestFocus();
            return;
        }
        if (!contrasena.equals(confirmarContrasena)) {
            editTextConfirmarContrasena.setError("Las contraseñas no coinciden");
            editTextConfirmarContrasena.requestFocus();
            return;
        }

        // Verificar si el usuario ya existe
        if (usuariosDAO.existeUsuario(nombreUsuario)) {
            Toast.makeText(this, "El nombre de usuario ya está en uso.", Toast.LENGTH_LONG).show();
            editTextNombreUsuario.requestFocus();
            return;
        }

        Usuario nuevoUsuario = new Usuario(nombreUsuario, contrasena, email.isEmpty() ? null : email);
        long id = usuariosDAO.registrarUsuario(nuevoUsuario);

        if (id != -1) {
            Toast.makeText(this, "Usuario registrado exitosamente. Ahora puedes iniciar sesión.", Toast.LENGTH_LONG).show();
            Intent intent = new Intent(RegistroActivity.this, LoginActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(intent);
            finish();
        } else {
            Toast.makeText(this, "Error al registrar el usuario. Inténtalo de nuevo.", Toast.LENGTH_LONG).show();
        }
    }
}