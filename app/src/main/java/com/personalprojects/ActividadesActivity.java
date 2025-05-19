package com.personalprojects; // PAQUETE ACTUALIZADO

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.personalprojects.adapter.ActividadAdapter;
import com.personalprojects.db.ActividadesDAO;
import com.personalprojects.model.Actividad;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class ActividadesActivity extends AppCompatActivity implements ActividadAdapter.OnActividadClickListener {

    private RecyclerView recyclerViewActividades;
    private ActividadAdapter actividadAdapter;
    private List<Actividad> listaActividades;
    private ActividadesDAO actividadesDAO;
    private FloatingActionButton fabAgregarActividad;
    private TextView textViewSinActividades, textViewNombreProyecto, textViewProgresoGeneral;
    private ProgressBar progressBarProyectoGeneral;

    private int idProyectoActual;
    private String nombreProyectoActual;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_actividades);

        Toolbar toolbar = findViewById(R.id.toolbarActividades);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        idProyectoActual = getIntent().getIntExtra("ID_PROYECTO", -1);
        nombreProyectoActual = getIntent().getStringExtra("NOMBRE_PROYECTO");

        if (idProyectoActual == -1) {
            Toast.makeText(this, "Error: ID de proyecto no encontrado.", Toast.LENGTH_LONG).show();
            finish();
            return;
        }

        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle("Actividades"); // O el nombre del proyecto
        }


        actividadesDAO = new ActividadesDAO(this);
        listaActividades = new ArrayList<>();

        textViewNombreProyecto = findViewById(R.id.textViewNombreProyectoEnActividades);
        if (nombreProyectoActual != null) {
            textViewNombreProyecto.setText(nombreProyectoActual);
        } else {
            textViewNombreProyecto.setText("Actividades del Proyecto"); // Fallback
        }

        progressBarProyectoGeneral = findViewById(R.id.progressBarProyectoGeneral);
        textViewProgresoGeneral = findViewById(R.id.textViewProgresoGeneral);

        recyclerViewActividades = findViewById(R.id.recyclerViewActividades);
        textViewSinActividades = findViewById(R.id.textViewSinActividades);
        fabAgregarActividad = findViewById(R.id.fabAgregarActividad);

        recyclerViewActividades.setLayoutManager(new LinearLayoutManager(this));
        actividadAdapter = new ActividadAdapter(this, listaActividades, this);
        recyclerViewActividades.setAdapter(actividadAdapter);

        fabAgregarActividad.setOnClickListener(v -> {
            Intent intent = new Intent(ActividadesActivity.this, EditorActividadActivity.class);
            intent.putExtra("ID_PROYECTO", idProyectoActual);
            startActivity(intent);
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        cargarActividades();
        calcularYMostrarProgresoGeneral();
    }

    private void cargarActividades() {
        List<Actividad> actividadesDesdeDB = actividadesDAO.getActividadesPorProyecto(idProyectoActual);
        listaActividades.clear();
        listaActividades.addAll(actividadesDesdeDB);
        actividadAdapter.actualizarActividades(listaActividades);

        if (listaActividades.isEmpty()) {
            recyclerViewActividades.setVisibility(View.GONE);
            textViewSinActividades.setVisibility(View.VISIBLE);
        } else {
            recyclerViewActividades.setVisibility(View.VISIBLE);
            textViewSinActividades.setVisibility(View.GONE);
        }
    }

    private void calcularYMostrarProgresoGeneral() {
        int totalActividades = actividadesDAO.contarTotalActividadesPorProyecto(idProyectoActual);
        int actividadesRealizadas = actividadesDAO.contarActividadesPorEstado(idProyectoActual, "Realizado");

        if (totalActividades > 0) {
            int progreso = (int) (((double) actividadesRealizadas / totalActividades) * 100);
            progressBarProyectoGeneral.setProgress(progreso);
            textViewProgresoGeneral.setText(String.format(Locale.getDefault(), "%d%%", progreso));
        } else {
            progressBarProyectoGeneral.setProgress(0);
            textViewProgresoGeneral.setText("0%");
        }
    }


    @Override
    public void onActividadClick(Actividad actividad) {
        // Mostrar opciones: Editar o Eliminar
        final CharSequence[] items = {"Editar Actividad", "Eliminar Actividad", "Cancelar"};
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Opciones de Actividad");
        builder.setItems(items, (dialog, item) -> {
            if (items[item].equals("Editar Actividad")) {
                Intent intent = new Intent(ActividadesActivity.this, EditorActividadActivity.class);
                intent.putExtra("ID_ACTIVIDAD_EDITAR", actividad.getIdActividad());
                intent.putExtra("ID_PROYECTO", idProyectoActual); // Pasar por si acaso
                startActivity(intent);
            } else if (items[item].equals("Eliminar Actividad")) {
                confirmarEliminacionActividad(actividad);
            } else if (items[item].equals("Cancelar")) {
                dialog.dismiss();
            }
        });
        builder.show();
    }

    private void confirmarEliminacionActividad(Actividad actividad) {
        new AlertDialog.Builder(this)
                .setTitle(R.string.confirmar_eliminacion)
                .setMessage(getString(R.string.mensaje_confirmar_eliminacion) + "\n\"" + actividad.getNombre() + "\"")
                .setPositiveButton(R.string.si, (dialog, which) -> {
                    int resultado = actividadesDAO.eliminarActividad(actividad.getIdActividad());
                    if (resultado > 0) {
                        Toast.makeText(this, "Actividad eliminada.", Toast.LENGTH_SHORT).show();
                        cargarActividades(); // Recargar la lista
                        calcularYMostrarProgresoGeneral(); // Recalcular progreso
                    } else {
                        Toast.makeText(this, "Error al eliminar la actividad.", Toast.LENGTH_SHORT).show();
                    }
                })
                .setNegativeButton(R.string.no, null)
                .setIcon(android.R.drawable.ic_dialog_alert)
                .show();
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish(); // Volver atrás
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}