package com.personalprojects; // PAQUETE ACTUALIZADO

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.personalprojects.adapter.ProyectoAdapter;
import com.personalprojects.db.ActividadesDAO;
import com.personalprojects.db.ProyectosDAO;
import com.personalprojects.model.Proyecto;
import com.personalprojects.util.SessionManager;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class ProyectosActivity extends AppCompatActivity implements ProyectoAdapter.OnProyectoClickListener, ProyectoAdapter.OnProyectoOptionsListener {

    private RecyclerView recyclerViewProyectos;
    private ProyectoAdapter proyectoAdapter;
    private List<Proyecto> listaProyectos;
    private ProyectosDAO proyectosDAO;
    private ActividadesDAO actividadesDAO; // Para calcular progreso
    private FloatingActionButton fabAgregarProyecto;
    private TextView textViewSinProyectos, textViewBienvenidaUsuario;
    private SessionManager sessionManager;
    private int idUsuarioActual;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_proyectos);

        Toolbar toolbar = findViewById(R.id.toolbarProyectos);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle(R.string.titulo_proyectos);
        }


        sessionManager = new SessionManager(getApplicationContext());
        if (!sessionManager.isLoggedIn()) {
            // Si no está logueado, redirigir al Login
            Intent intent = new Intent(ProyectosActivity.this, LoginActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
            finish();
            return;
        }

        idUsuarioActual = sessionManager.getUserId();
        String nombreUsuario = sessionManager.getUserName();

        proyectosDAO = new ProyectosDAO(this);
        actividadesDAO = new ActividadesDAO(this);
        listaProyectos = new ArrayList<>();

        textViewBienvenidaUsuario = findViewById(R.id.textViewBienvenidaUsuario);
        if (nombreUsuario != null) {
            textViewBienvenidaUsuario.setText(String.format("Hola, %s!", nombreUsuario));
        } else {
            textViewBienvenidaUsuario.setText("Mis Proyectos");
        }


        recyclerViewProyectos = findViewById(R.id.recyclerViewProyectos);
        textViewSinProyectos = findViewById(R.id.textViewSinProyectos);
        fabAgregarProyecto = findViewById(R.id.fabAgregarProyecto);

        recyclerViewProyectos.setLayoutManager(new LinearLayoutManager(this));
        proyectoAdapter = new ProyectoAdapter(this, listaProyectos, this, this);
        recyclerViewProyectos.setAdapter(proyectoAdapter);

        fabAgregarProyecto.setOnClickListener(v -> {
            Intent intent = new Intent(ProyectosActivity.this, EditorProyectoActivity.class);
            intent.putExtra("ID_USUARIO", idUsuarioActual);
            startActivity(intent);
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        cargarProyectos();
    }

    private void cargarProyectos() {
        if (idUsuarioActual == -1) {
            // Manejar error, usuario no debería estar aquí
            Toast.makeText(this, "Error: Usuario no identificado.", Toast.LENGTH_SHORT).show();
            sessionManager.logoutUser();
            Intent intent = new Intent(ProyectosActivity.this, LoginActivity.class);
            startActivity(intent);
            finish();
            return;
        }

        List<Proyecto> proyectosDesdeDB = proyectosDAO.getProyectosPorUsuario(idUsuarioActual);
        listaProyectos.clear();
        listaProyectos.addAll(proyectosDesdeDB);

        // Actualizar el progreso en el adapter (esta parte es compleja de hacer bien en el adapter directamente)
        // Es mejor que el adapter solo muestre lo que le pasas.
        // Vamos a recalcular el progreso aquí y pasarlo al adapter o tener un método para actualizarlo.
        // Por simplicidad, el adapter tiene una simulación, aquí podrías calcularlo y pasarlo.
        // El adapter debería tener un método `setProgreso(idProyecto, progreso)` o el modelo Proyecto debería tener `progreso`.
        // Para este ejemplo, mantendremos la simulación del adapter.

        proyectoAdapter.actualizarProyectos(listaProyectos);


        if (listaProyectos.isEmpty()) {
            recyclerViewProyectos.setVisibility(View.GONE);
            textViewSinProyectos.setVisibility(View.VISIBLE);
        } else {
            recyclerViewProyectos.setVisibility(View.VISIBLE);
            textViewSinProyectos.setVisibility(View.GONE);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_proyectos, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == R.id.action_logout) {
            sessionManager.logoutUser();
            Intent intent = new Intent(ProyectosActivity.this, LoginActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
            finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }


    // Implementación de OnProyectoClickListener (click en el item completo)
    @Override
    public void onProyectoClick(Proyecto proyecto) {
        // Al hacer clic en un proyecto, vamos a sus actividades
        onViewActividades(proyecto);
    }

    // Implementación de OnProyectoOptionsListener (clic en el menú del item)
    @Override
    public void onEditProyecto(Proyecto proyecto) {
        Intent intent = new Intent(ProyectosActivity.this, EditorProyectoActivity.class);
        intent.putExtra("ID_PROYECTO_EDITAR", proyecto.getIdProyecto());
        intent.putExtra("ID_USUARIO", idUsuarioActual); // Aunque podría obtenerse del proyecto mismo
        startActivity(intent);
    }

    @Override
    public void onDeleteProyecto(Proyecto proyecto) {
        new AlertDialog.Builder(this)
                .setTitle(R.string.confirmar_eliminacion)
                .setMessage(getString(R.string.mensaje_confirmar_eliminacion) + "\n\"" + proyecto.getNombre() + "\"\nEsto también eliminará todas sus actividades.")
                .setPositiveButton(R.string.si, (dialog, which) -> {
                    int resultado = proyectosDAO.eliminarProyecto(proyecto.getIdProyecto());
                    if (resultado > 0) {
                        Toast.makeText(this, "Proyecto eliminado.", Toast.LENGTH_SHORT).show();
                        cargarProyectos(); // Recargar la lista
                    } else {
                        Toast.makeText(this, "Error al eliminar el proyecto.", Toast.LENGTH_SHORT).show();
                    }
                })
                .setNegativeButton(R.string.no, null)
                .setIcon(android.R.drawable.ic_dialog_alert)
                .show();
    }

    @Override
    public void onViewActividades(Proyecto proyecto) {
        Intent intent = new Intent(ProyectosActivity.this, ActividadesActivity.class);
        intent.putExtra("ID_PROYECTO", proyecto.getIdProyecto());
        intent.putExtra("NOMBRE_PROYECTO", proyecto.getNombre());
        startActivity(intent);
    }
}