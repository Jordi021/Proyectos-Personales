package com.personalprojects; // PAQUETE ACTUALIZADO

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log; // IMPORTANTE para el logging
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

    private static final String TAG = "ProyectosActivity"; // Etiqueta para Logcat

    private RecyclerView recyclerViewProyectos;
    private ProyectoAdapter proyectoAdapter;
    private List<Proyecto> listaProyectos; // Esta es la lista que se pasa y usa el adapter
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
            Log.w(TAG, "Usuario no logueado. Redirigiendo a LoginActivity.");
            Intent intent = new Intent(ProyectosActivity.this, LoginActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
            finish();
            return; // Importante salir para evitar ejecutar más código si no está logueado
        }

        idUsuarioActual = sessionManager.getUserId();
        String nombreUsuario = sessionManager.getUserName();
        Log.d(TAG, "onCreate - Usuario ID: " + idUsuarioActual + ", Nombre: " + nombreUsuario);

        proyectosDAO = new ProyectosDAO(this);
        actividadesDAO = new ActividadesDAO(this); // Asegúrate de que la lógica de progreso no interfiera o lance errores.

        listaProyectos = new ArrayList<>(); // Es crucial inicializarla ANTES de pasarla al adapter

        textViewBienvenidaUsuario = findViewById(R.id.textViewBienvenidaUsuario);
        if (nombreUsuario != null && !nombreUsuario.isEmpty()) {
            textViewBienvenidaUsuario.setText(String.format(Locale.getDefault(), "Hola, %s!", nombreUsuario));
        } else {
            // Considera un texto por defecto si el nombre no está disponible por alguna razón
            textViewBienvenidaUsuario.setText(getString(R.string.titulo_proyectos)); // Usar un string resource es buena práctica
        }

        recyclerViewProyectos = findViewById(R.id.recyclerViewProyectos);
        textViewSinProyectos = findViewById(R.id.textViewSinProyectos); // Mensaje para cuando no hay proyectos
        fabAgregarProyecto = findViewById(R.id.fabAgregarProyecto);

        recyclerViewProyectos.setLayoutManager(new LinearLayoutManager(this));

        // Creas el adapter y le pasas la instancia de 'listaProyectos'.
        // El adapter conservará una referencia a esta misma instancia.
        proyectoAdapter = new ProyectoAdapter(this, listaProyectos, this, this);
        recyclerViewProyectos.setAdapter(proyectoAdapter);

        fabAgregarProyecto.setOnClickListener(v -> {
            Intent intent = new Intent(ProyectosActivity.this, EditorProyectoActivity.class);
            intent.putExtra("ID_USUARIO", idUsuarioActual); // Pasar el ID del usuario para crear el proyecto
            Log.d(TAG, "Abriendo EditorProyectoActivity para crear proyecto para usuario ID: " + idUsuarioActual);
            startActivity(intent);
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.d(TAG, "onResume - Llamando a cargarProyectos()...");
        cargarProyectos(); // Correcto: se recargan los datos cada vez que la actividad es visible
    }

    private void cargarProyectos() {
        if (idUsuarioActual == -1) {
            Log.e(TAG, "cargarProyectos - idUsuarioActual es -1. Esto no debería ocurrir si el chequeo en onCreate funciona. Redirigiendo a Login.");
            Toast.makeText(this, "Error de sesión. Por favor, inicie sesión de nuevo.", Toast.LENGTH_LONG).show();
            sessionManager.logoutUser(); // Limpiar la sesión incorrecta
            Intent intent = new Intent(ProyectosActivity.this, LoginActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
            finish();
            return;
        }

        Log.i(TAG, "cargarProyectos - Cargando proyectos para el usuario ID: " + idUsuarioActual);
        List<Proyecto> proyectosDesdeDB = proyectosDAO.getProyectosPorUsuario(idUsuarioActual);

        if (proyectosDesdeDB == null) {
            Log.w(TAG, "cargarProyectos - proyectosDAO.getProyectosPorUsuario() devolvió null. Se tratará como lista vacía.");
            proyectosDesdeDB = new ArrayList<>(); // Evita NullPointerExceptions
        }

        Log.d(TAG, "cargarProyectos - Número de proyectos obtenidos de la BD: " + proyectosDesdeDB.size());
        if(proyectosDesdeDB.isEmpty()){
            Log.i(TAG, "cargarProyectos - No se encontraron proyectos en la BD para el usuario ID: " + idUsuarioActual);
        } else {
            for(Proyecto p : proyectosDesdeDB) {
                Log.d(TAG, "cargarProyectos - Proyecto desde BD: ID=" + p.getIdProyecto() + ", Nombre=" + p.getNombre());
            }
        }

        // Ya que `listaProyectos` es la misma instancia que usa el adapter:
        // 1. Limpia la lista actual (que el adapter está usando).
        // 2. Añade los nuevos datos a esa misma lista.
        // 3. Notifica al adapter que los datos han cambiado.
        listaProyectos.clear();
        listaProyectos.addAll(proyectosDesdeDB);
        proyectoAdapter.notifyDataSetChanged(); // ¡Crucial!

        // Tu método `proyectoAdapter.actualizarProyectos(listaProyectos);` también funciona porque internamente
        // hace algo similar (limpia su lista, añade todo de la nueva y llama a notifyDataSetChanged()).
        // La forma anterior (listaProyectos.clear(); listaProyectos.addAll(); proyectoAdapter.notifyDataSetChanged();)
        // es un poco más directa si sabes que el adapter comparte la instancia de la lista.
        // Si decides mantener tu llamada original:
        // proyectoAdapter.actualizarProyectos(proyectosDesdeDB); // Esto también es correcto.
        // Solo asegúrate de que la lista que usas para el if/else de visibilidad sea la misma que se muestra.


        if (listaProyectos.isEmpty()) { // O `proyectosDesdeDB.isEmpty()` si usaste `actualizarProyectos(proyectosDesdeDB)`
            Log.d(TAG, "cargarProyectos - La lista de proyectos está vacía. Mostrando mensaje 'Sin Proyectos'.");
            recyclerViewProyectos.setVisibility(View.GONE);
            textViewSinProyectos.setVisibility(View.VISIBLE);
        } else {
            Log.d(TAG, "cargarProyectos - Mostrando " + listaProyectos.size() + " proyectos.");
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
            Log.d(TAG, "onOptionsItemSelected - Cerrar sesión seleccionado.");
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
        Log.d(TAG, "onProyectoClick - Proyecto: " + proyecto.getNombre() + " (ID: " + proyecto.getIdProyecto() + ")");
        onViewActividades(proyecto); // Reutilizas este método, lo cual es bueno.
    }

    // Implementación de OnProyectoOptionsListener (clic en el menú del item)
    @Override
    public void onEditProyecto(Proyecto proyecto) {
        Log.d(TAG, "onEditProyecto - Editando proyecto: " + proyecto.getNombre() + " (ID: " + proyecto.getIdProyecto() + ")");
        Intent intent = new Intent(ProyectosActivity.this, EditorProyectoActivity.class);
        intent.putExtra("ID_PROYECTO_EDITAR", proyecto.getIdProyecto());
        // No necesitas pasar ID_USUARIO si EditorProyectoActivity puede obtenerlo al cargar el proyecto para editar.
        // Sin embargo, pasarlo no hace daño y puede ser útil si el proyecto aún no tiene uno asignado (aunque no debería ser el caso al editar).
        intent.putExtra("ID_USUARIO", idUsuarioActual);
        startActivity(intent);
    }

    @Override
    public void onDeleteProyecto(Proyecto proyecto) {
        Log.d(TAG, "onDeleteProyecto - Solicitud para eliminar proyecto: " + proyecto.getNombre() + " (ID: " + proyecto.getIdProyecto() + ")");
        new AlertDialog.Builder(this)
                .setTitle(R.string.confirmar_eliminacion)
                .setMessage(getString(R.string.mensaje_confirmar_eliminacion_proyecto, proyecto.getNombre())) // Usar placeholder en strings.xml
                .setPositiveButton(R.string.si, (dialog, which) -> {
                    Log.i(TAG, "Confirmada la eliminación del proyecto: " + proyecto.getNombre());
                    // Asegúrate de que ProyectosDAO.eliminarProyecto maneje la eliminación en cascada de actividades
                    // o implementa esa lógica aquí o en el DAO.
                    int resultado = proyectosDAO.eliminarProyecto(proyecto.getIdProyecto()); // Suponiendo que tienes este método
                    // Si solo tienes proyectosDAO.eliminarProyecto(proyecto.getIdProyecto());
                    // considera también: actividadesDAO.eliminarActividadesPorProyecto(proyecto.getIdProyecto());

                    if (resultado > 0) { // `eliminarProyecto` debería devolver el número de filas afectadas.
                        Toast.makeText(this, "Proyecto '" + proyecto.getNombre() + "' eliminado.", Toast.LENGTH_SHORT).show();
                        Log.i(TAG, "Proyecto eliminado exitosamente de la BD.");
                        cargarProyectos(); // ¡Importante! Recargar la lista para reflejar el cambio.
                    } else {
                        Toast.makeText(this, "Error al eliminar el proyecto.", Toast.LENGTH_SHORT).show();
                        Log.e(TAG, "Error al eliminar el proyecto de la BD. DAO devolvió: " + resultado);
                    }
                })
                .setNegativeButton(R.string.no, (dialog, which) -> {
                    Log.d(TAG, "Cancelada la eliminación del proyecto: " + proyecto.getNombre());
                })
                .setIcon(android.R.drawable.ic_dialog_alert) // Considera usar un ícono propio.
                .show();
    }

    @Override
    public void onViewActividades(Proyecto proyecto) {
        Log.d(TAG, "onViewActividades - Navegando a ActividadesActivity para proyecto: " + proyecto.getNombre() + " (ID: " + proyecto.getIdProyecto() + ")");
        Intent intent = new Intent(ProyectosActivity.this, ActividadesActivity.class);
        intent.putExtra("ID_PROYECTO", proyecto.getIdProyecto());
        intent.putExtra("NOMBRE_PROYECTO", proyecto.getNombre());
        // Podrías pasar ID_USUARIO si ActividadesActivity lo necesita directamente.
        // intent.putExtra("ID_USUARIO", idUsuarioActual);
        startActivity(intent);
    }
}