package com.personalprojects; // PAQUETE ACTUALIZADO

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import android.app.DatePickerDialog;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log; // IMPORTACIÓN NECESARIA PARA LOGCAT
import android.view.MenuItem;
import android.widget.Button;
import android.widget.Toast;
import com.google.android.material.textfield.TextInputEditText;
import com.personalprojects.db.ProyectosDAO;
import com.personalprojects.model.Proyecto;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

public class EditorProyectoActivity extends AppCompatActivity {

    // Definimos una etiqueta (TAG) para nuestros mensajes de Logcat
    private static final String TAG = "EditorProyectoActivity";

    private TextInputEditText editTextNombre, editTextDescripcion, editTextFechaInicio, editTextFechaFin;
    private Button buttonGuardar;
    private ProyectosDAO proyectosDAO;
    private Calendar calendarioInicio, calendarioFin;
    private int idProyectoEditar = -1; // -1 significa crear nuevo, > -1 significa editar
    private int idUsuarioActual;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_editor_proyecto);

        Toolbar toolbar = findViewById(R.id.toolbarEditorProyecto);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        proyectosDAO = new ProyectosDAO(this);
        calendarioInicio = Calendar.getInstance();
        calendarioFin = Calendar.getInstance();

        editTextNombre = findViewById(R.id.editTextNombreProyectoEditor);
        editTextDescripcion = findViewById(R.id.editTextDescripcionProyectoEditor);
        editTextFechaInicio = findViewById(R.id.editTextFechaInicioProyectoEditor);
        editTextFechaFin = findViewById(R.id.editTextFechaFinProyectoEditor);
        buttonGuardar = findViewById(R.id.buttonGuardarProyecto);

        // Recoger datos del Intent (si es para editar o para asociar a usuario)
        idUsuarioActual = getIntent().getIntExtra("ID_USUARIO", -1);
        if (getIntent().hasExtra("ID_PROYECTO_EDITAR")) {
            idProyectoEditar = getIntent().getIntExtra("ID_PROYECTO_EDITAR", -1);
            if (getSupportActionBar() != null) getSupportActionBar().setTitle("Editar Proyecto");
            buttonGuardar.setText(R.string.guardar); // O "Actualizar Proyecto"
            cargarDatosProyecto();
        } else {
            if (getSupportActionBar() != null) getSupportActionBar().setTitle("Crear Proyecto");
        }

        if (idUsuarioActual == -1 && idProyectoEditar == -1) {
            // Error crítico, no se puede crear ni editar sin un ID de usuario o proyecto
            Toast.makeText(this, "Error: Falta información del usuario.", Toast.LENGTH_LONG).show();
            Log.e(TAG, "Error crítico: Falta ID_USUARIO y no se está editando un proyecto. idUsuarioActual: " + idUsuarioActual + ", idProyectoEditar: " + idProyectoEditar);
            finish();
            return;
        }


        editTextFechaInicio.setOnClickListener(v -> mostrarDatePickerDialog(calendarioInicio, editTextFechaInicio));
        editTextFechaFin.setOnClickListener(v -> mostrarDatePickerDialog(calendarioFin, editTextFechaFin));

        buttonGuardar.setOnClickListener(v -> guardarProyecto());
    }

    private void cargarDatosProyecto() {
        if (idProyectoEditar != -1) {
            Log.d(TAG, "Cargando datos para el proyecto ID: " + idProyectoEditar);
            Proyecto proyecto = proyectosDAO.getProyectoPorId(idProyectoEditar);
            if (proyecto != null) {
                editTextNombre.setText(proyecto.getNombre());
                editTextDescripcion.setText(proyecto.getDescripcion());
                editTextFechaInicio.setText(proyecto.getFechaInicio());
                editTextFechaFin.setText(proyecto.getFechaFin());
                idUsuarioActual = proyecto.getIdUsuario(); // Aseguramos el ID de usuario
                Log.i(TAG, "Proyecto cargado: " + proyecto.getNombre());

                // Parsear fechas para los Calendar
                SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
                try {
                    if (proyecto.getFechaInicio() != null && !proyecto.getFechaInicio().isEmpty())
                        calendarioInicio.setTime(sdf.parse(proyecto.getFechaInicio()));
                    if (proyecto.getFechaFin() != null && !proyecto.getFechaFin().isEmpty())
                        calendarioFin.setTime(sdf.parse(proyecto.getFechaFin()));
                } catch (Exception e) {
                    Log.e(TAG, "Error al parsear fechas durante la carga del proyecto.", e);
                    e.printStackTrace(); // También mantenemos el stack trace por si acaso
                }
            } else {
                Toast.makeText(this, "Error al cargar el proyecto para editar.", Toast.LENGTH_SHORT).show();
                Log.w(TAG, "No se pudo encontrar el proyecto con ID: " + idProyectoEditar + " para editar.");
                finish();
            }
        }
    }

    private void mostrarDatePickerDialog(Calendar calendario, TextInputEditText editText) {
        DatePickerDialog.OnDateSetListener dateSetListener = (view, year, month, dayOfMonth) -> {
            calendario.set(Calendar.YEAR, year);
            calendario.set(Calendar.MONTH, month);
            calendario.set(Calendar.DAY_OF_MONTH, dayOfMonth);
            actualizarEditTextConFecha(calendario, editText);
        };

        new DatePickerDialog(this, dateSetListener,
                calendario.get(Calendar.YEAR),
                calendario.get(Calendar.MONTH),
                calendario.get(Calendar.DAY_OF_MONTH))
                .show();
    }

    private void actualizarEditTextConFecha(Calendar calendario, TextInputEditText editText) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
        editText.setText(sdf.format(calendario.getTime()));
    }

    private void guardarProyecto() {
        String nombre = editTextNombre.getText().toString().trim();
        String descripcion = editTextDescripcion.getText().toString().trim();
        String fechaInicio = editTextFechaInicio.getText().toString().trim();
        String fechaFin = editTextFechaFin.getText().toString().trim();

        if (TextUtils.isEmpty(nombre)) {
            editTextNombre.setError("Nombre del proyecto es requerido.");
            editTextNombre.requestFocus();
            Log.w(TAG, "Intento de guardar proyecto sin nombre.");
            return;
        }
        // Validaciones adicionales (ej. fecha fin no puede ser antes que fecha inicio)
        if (!fechaInicio.isEmpty() && !fechaFin.isEmpty()) {
            if (calendarioFin.before(calendarioInicio)) {
                editTextFechaFin.setError("La fecha de fin no puede ser anterior a la fecha de inicio.");
                Toast.makeText(this, "La fecha de fin no puede ser anterior a la fecha de inicio.", Toast.LENGTH_LONG).show();
                editTextFechaFin.requestFocus();
                Log.w(TAG, "Validación fallida: Fecha de fin (" + fechaFin + ") es anterior a fecha de inicio (" + fechaInicio + ").");
                return;
            }
        }

        Proyecto proyecto;
        if (idProyectoEditar == -1) { // Crear nuevo
            Log.d(TAG, "Intentando crear nuevo proyecto para usuario ID: " + idUsuarioActual);
            proyecto = new Proyecto(nombre, descripcion, fechaInicio.isEmpty() ? null : fechaInicio, fechaFin.isEmpty() ? null : fechaFin, idUsuarioActual);
            long resultado = proyectosDAO.crearProyecto(proyecto);
            if (resultado != -1) {
                Toast.makeText(this, "Proyecto creado exitosamente.", Toast.LENGTH_SHORT).show();
                // Mensaje por consola (Logcat)
                Log.i(TAG, "Proyecto creado exitosamente. ID: " + resultado + ", Nombre: " + nombre + ", Usuario ID: " + idUsuarioActual);
                finish();
            } else {
                Toast.makeText(this, "Error al crear el proyecto.", Toast.LENGTH_SHORT).show();
                // Mensaje por consola (Logcat)
                Log.e(TAG, "Error al crear el proyecto. Nombre: " + nombre + ", Usuario ID: " + idUsuarioActual);
            }
        } else { // Actualizar existente
            Log.d(TAG, "Intentando actualizar proyecto ID: " + idProyectoEditar);
            proyecto = new Proyecto(idProyectoEditar, nombre, descripcion, fechaInicio.isEmpty() ? null : fechaInicio, fechaFin.isEmpty() ? null : fechaFin, idUsuarioActual);
            int resultado = proyectosDAO.actualizarProyecto(proyecto);
            if (resultado > 0) {
                Toast.makeText(this, "Proyecto actualizado exitosamente.", Toast.LENGTH_SHORT).show();
                // Mensaje por consola (Logcat)
                Log.i(TAG, "Proyecto actualizado exitosamente. ID: " + idProyectoEditar + ", Nombre: " + nombre + ". Filas afectadas: " + resultado);
                finish();
            } else {
                Toast.makeText(this, "Error al actualizar el proyecto.", Toast.LENGTH_SHORT).show();
                // Mensaje por consola (Logcat)
                Log.e(TAG, "Error al actualizar el proyecto. ID: " + idProyectoEditar + ", Nombre: " + nombre + ". Filas afectadas: " + resultado);
            }
        }
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