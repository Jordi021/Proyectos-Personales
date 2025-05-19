package com.personalprojects; // Asegúrate que el paquete sea el correcto

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.personalprojects.adapter.ActividadAdapter;
import com.personalprojects.db.ActividadesDAO;
import com.personalprojects.model.Actividad; // Importa tu modelo Actividad

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class ActividadesActivity extends AppCompatActivity implements ActividadAdapter.OnActividadClickListener {

    private static final String TAG = "ActividadesDebug"; // TAG para logs

    private RecyclerView recyclerViewActividades;
    private ActividadAdapter actividadAdapter;
    private List<Actividad> listaActividadesInterna; // Lista que maneja esta Activity y pasa al Adapter
    private ActividadesDAO actividadesDAO;
    private FloatingActionButton fabAgregarActividad;
    private TextView textViewSinActividades, textViewNombreProyectoEnActividades, textViewProgresoGeneral;
    private ProgressBar progressBarProyectoGeneral;

    private int idProyectoActual;
    private String nombreProyectoActual;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_actividades);
        Log.d(TAG, "onCreate - Iniciando ActividadesActivity");

        Toolbar toolbar = findViewById(R.id.toolbarActividades);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            // El título se establece después de obtener nombreProyectoActual
        }

        idProyectoActual = getIntent().getIntExtra("ID_PROYECTO", -1);
        nombreProyectoActual = getIntent().getStringExtra("NOMBRE_PROYECTO");
        Log.d(TAG, "onCreate - idProyectoActual: " + idProyectoActual + ", nombreProyectoActual: " + nombreProyectoActual);

        if (idProyectoActual == -1) {
            Toast.makeText(this, "Error: ID de proyecto no válido.", Toast.LENGTH_LONG).show();
            Log.e(TAG, "onCreate - ID de proyecto no válido. Finalizando actividad.");
            finish();
            return;
        }

        if (getSupportActionBar() != null) {
            if (nombreProyectoActual != null && !nombreProyectoActual.isEmpty()) {
                getSupportActionBar().setTitle(nombreProyectoActual);
            } else {
                getSupportActionBar().setTitle("Actividades"); // Título de fallback
            }
        }

        actividadesDAO = new ActividadesDAO(this);
        listaActividadesInterna = new ArrayList<>();

        textViewNombreProyectoEnActividades = findViewById(R.id.textViewNombreProyectoEnActividades); // Asumiendo este ID en tu layout
        if (nombreProyectoActual != null) {
            textViewNombreProyectoEnActividades.setText(nombreProyectoActual);
        } else {
            textViewNombreProyectoEnActividades.setText("Actividades del Proyecto"); // Fallback
        }

        progressBarProyectoGeneral = findViewById(R.id.progressBarProyectoGeneral);
        textViewProgresoGeneral = findViewById(R.id.textViewProgresoGeneral);
        recyclerViewActividades = findViewById(R.id.recyclerViewActividades);
        textViewSinActividades = findViewById(R.id.textViewSinActividades);
        fabAgregarActividad = findViewById(R.id.fabAgregarActividad);

        recyclerViewActividades.setLayoutManager(new LinearLayoutManager(this));
        actividadAdapter = new ActividadAdapter(this, listaActividadesInterna, this);
        recyclerViewActividades.setAdapter(actividadAdapter);
        Log.d(TAG, "onCreate - RecyclerView y Adapter configurados.");

        fabAgregarActividad.setOnClickListener(v -> {
            Log.d(TAG, "fabAgregarActividad - Clicked. Lanzando EditorActividadActivity para idProyecto: " + idProyectoActual);
            Intent intent = new Intent(ActividadesActivity.this, EditorActividadActivity.class);
            intent.putExtra("ID_PROYECTO", idProyectoActual);
            // No se envía ID_ACTIVIDAD_EDITAR porque es una nueva actividad
            startActivity(intent);
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.d(TAG, "onResume - Actividad reanudada. idProyectoActual: " + idProyectoActual);
        cargarActividades(); // Carga o recarga las actividades
        calcularYMostrarProgresoGeneral(); // Calcula y muestra el progreso
    }

    private void cargarActividades() {
        Log.d(TAG, "cargarActividades - Intentando cargar actividades para idProyecto: " + idProyectoActual);
        if (idProyectoActual == -1) {
            Log.w(TAG, "cargarActividades - idProyectoActual es -1. No se cargarán actividades.");
            textViewSinActividades.setText("ID de proyecto no válido.");
            textViewSinActividades.setVisibility(View.VISIBLE);
            recyclerViewActividades.setVisibility(View.GONE);
            return;
        }

        List<Actividad> actividadesDesdeDB = actividadesDAO.getActividadesPorProyecto(idProyectoActual);
        Log.d(TAG, "cargarActividades - Actividades obtenidas desde DAO: " + (actividadesDesdeDB != null ? actividadesDesdeDB.size() : "null"));

        listaActividadesInterna.clear(); // Limpia la lista actual
        if (actividadesDesdeDB != null && !actividadesDesdeDB.isEmpty()) {
            listaActividadesInterna.addAll(actividadesDesdeDB); // Añade las nuevas actividades
            Log.d(TAG, "cargarActividades - listaActividadesInterna actualizada. Tamaño: " + listaActividadesInterna.size());
            textViewSinActividades.setVisibility(View.GONE);
            recyclerViewActividades.setVisibility(View.VISIBLE);
        } else {
            Log.d(TAG, "cargarActividades - No hay actividades para mostrar o la lista de BD es nula/vacía.");
            textViewSinActividades.setText(R.string.no_hay_actividades); // Usa un string resource
            textViewSinActividades.setVisibility(View.VISIBLE);
            recyclerViewActividades.setVisibility(View.GONE);
        }
        actividadAdapter.notifyDataSetChanged(); // Notifica al adapter
        Log.d(TAG, "cargarActividades - actividadAdapter.notifyDataSetChanged() llamado.");
    }

    private void calcularYMostrarProgresoGeneral() {
        Log.d(TAG, "calcularYMostrarProgresoGeneral - Calculando para idProyecto: " + idProyectoActual);

        // Usar la constante del modelo Actividad para el estado completado
        String estadoCompletado = Actividad.ESTADO_REALIZADO;

        int totalActividades = actividadesDAO.contarTotalActividadesPorProyecto(idProyectoActual);
        int actividadesRealizadas = actividadesDAO.contarActividadesPorEstado(idProyectoActual, estadoCompletado);
        Log.d(TAG, "calcularYMostrarProgresoGeneral - Total: " + totalActividades + ", Realizadas: " + actividadesRealizadas + " (usando estado: '" + estadoCompletado + "')");

        if (totalActividades > 0) {
            // El casting a double es crucial para la precisión decimal antes de multiplicar por 100
            double progresoDouble = ((double) actividadesRealizadas / totalActividades) * 100.0;
            int progresoInt = (int) progresoDouble; // Trunca a entero para la ProgressBar

            progressBarProyectoGeneral.setProgress(progresoInt);
            textViewProgresoGeneral.setText(String.format(Locale.getDefault(), "%d%%", progresoInt));
            Log.d(TAG, "calcularYMostrarProgresoGeneral - Progreso double: " + progresoDouble + "%, Progreso UI: " + progresoInt + "%");
        } else {
            progressBarProyectoGeneral.setProgress(0);
            textViewProgresoGeneral.setText("0%");
            Log.d(TAG, "calcularYMostrarProgresoGeneral - Progreso: 0% (no hay actividades o idProyecto incorrecto)");
        }
    }

    @Override
    public void onActividadClick(Actividad actividad) {
        Log.d(TAG, "onActividadClick - Actividad: " + actividad.getNombre() + ", ID: " + actividad.getIdActividad());
        // Opciones para el diálogo
        final CharSequence[] items = {"Editar Actividad", "Eliminar Actividad", "Cancelar"};
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Opciones para: " + actividad.getNombre()); // Título más descriptivo
        builder.setItems(items, (dialog, item) -> {
            if (items[item].equals("Editar Actividad")) {
                Log.d(TAG, "onActividadClick - Opción: Editar Actividad");
                Intent intent = new Intent(ActividadesActivity.this, EditorActividadActivity.class);
                intent.putExtra("ID_ACTIVIDAD_EDITAR", actividad.getIdActividad());
                intent.putExtra("ID_PROYECTO", idProyectoActual); // Pasa el idProyecto también, puede ser útil
                startActivity(intent);
            } else if (items[item].equals("Eliminar Actividad")) {
                Log.d(TAG, "onActividadClick - Opción: Eliminar Actividad");
                confirmarEliminacionActividad(actividad);
            } else if (items[item].equals("Cancelar")) {
                Log.d(TAG, "onActividadClick - Opción: Cancelar");
                dialog.dismiss();
            }
        });
        builder.show();
    }

    private void confirmarEliminacionActividad(Actividad actividad) {
        Log.d(TAG, "confirmarEliminacionActividad - Para actividad: " + actividad.getNombre());
        new AlertDialog.Builder(this)
                .setTitle(R.string.confirmar_eliminacion_titulo) // Usa string resources
                .setMessage(getString(R.string.mensaje_confirmar_eliminacion_actividad, actividad.getNombre())) // Usa string resources
                .setPositiveButton(R.string.opcion_si, (dialog, which) -> {
                    Log.d(TAG, "confirmarEliminacionActividad - Confirmado. Eliminando actividad ID: " + actividad.getIdActividad());
                    int resultado = actividadesDAO.eliminarActividad(actividad.getIdActividad());
                    if (resultado > 0) {
                        Toast.makeText(this, "Actividad '" + actividad.getNombre() + "' eliminada.", Toast.LENGTH_SHORT).show();
                        Log.d(TAG, "confirmarEliminacionActividad - Actividad eliminada. Recargando datos.");
                        cargarActividades(); // Recargar la lista
                        calcularYMostrarProgresoGeneral(); // Recalcular el progreso
                    } else {
                        Toast.makeText(this, "Error al eliminar la actividad.", Toast.LENGTH_SHORT).show();
                        Log.e(TAG, "confirmarEliminacionActividad - Error al eliminar actividad ID: " + actividad.getIdActividad());
                    }
                })
                .setNegativeButton(R.string.opcion_no, (dialog, which) -> {
                    Log.d(TAG, "confirmarEliminacionActividad - Cancelado.");
                    dialog.dismiss();
                })
                .setIcon(android.R.drawable.ic_dialog_alert) // Icono estándar de alerta
                .show();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            Log.d(TAG, "onOptionsItemSelected - Botón Home presionado. Finalizando actividad.");
            finish(); // Vuelve a la actividad anterior (ProyectosActivity)
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}