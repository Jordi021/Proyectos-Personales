package com.personalprojects; // PAQUETE ACTUALIZADO

import androidx.annotation.NonNull; // Necesario para @NonNull si usas esa anotación
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import android.app.DatePickerDialog;
import android.content.Intent; // Para setResult
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log; // Para logging
import android.view.MenuItem;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.Toast;
import com.google.android.material.textfield.TextInputEditText;
import com.personalprojects.db.ActividadesDAO;
import com.personalprojects.model.Actividad; // Importa tu modelo Actividad con las constantes
import java.text.ParseException; // Para manejar errores de parseo de fechas
import java.text.SimpleDateFormat;
import java.util.ArrayList; // Para la lista de estados del spinner
// import java.util.Arrays; // Ya no es necesario si llenas el spinner con constantes
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

public class EditorActividadActivity extends AppCompatActivity {

    private static final String TAG = "EditorActividadDebug"; // TAG para logs

    private TextInputEditText editTextNombre, editTextDescripcion, editTextFechaInicio, editTextFechaFin;
    private Spinner spinnerEstado;
    private Button buttonGuardar;
    private ActividadesDAO actividadesDAO;
    private Calendar calendarioInicio, calendarioFin;

    private int idProyectoPadre = -1; // ID del proyecto al que pertenece esta actividad
    private Actividad actividadAEditar = null; // Objeto actividad si estamos editando, null si es nueva

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_editor_actividad);
        Log.d(TAG, "onCreate - Iniciando EditorActividadActivity");

        Toolbar toolbar = findViewById(R.id.toolbarEditorActividad);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        actividadesDAO = new ActividadesDAO(this);
        calendarioInicio = Calendar.getInstance();
        calendarioFin = Calendar.getInstance();

        editTextNombre = findViewById(R.id.editTextNombreActividadEditor);
        editTextDescripcion = findViewById(R.id.editTextDescripcionActividadEditor);
        editTextFechaInicio = findViewById(R.id.editTextFechaInicioActividadEditor);
        editTextFechaFin = findViewById(R.id.editTextFechaFinActividadEditor);
        spinnerEstado = findViewById(R.id.spinnerEstadoActividadEditor);
        buttonGuardar = findViewById(R.id.buttonGuardarActividad);

        // Configurar Spinner de Estados usando las constantes de Actividad.java
        List<String> listaDeEstados = new ArrayList<>();
        listaDeEstados.add(Actividad.ESTADO_PENDIENTE);
        listaDeEstados.add(Actividad.ESTADO_EN_PROGRESO);
        listaDeEstados.add(Actividad.ESTADO_REALIZADO);
        // Si tienes más estados como "Planificado" como constante, añádelo aquí
        // listaDeEstados.add(Actividad.ESTADO_PLANIFICADO);


        ArrayAdapter<String> adapterSpinner = new ArrayAdapter<>(this,
                android.R.layout.simple_spinner_item, listaDeEstados);
        adapterSpinner.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerEstado.setAdapter(adapterSpinner);
        Log.d(TAG, "onCreate - Spinner de estados configurado.");

        // Recoger datos del Intent
        // idProyectoPadre es crucial SIEMPRE, ya sea para crear una nueva actividad bajo este proyecto,
        // o para asegurar la consistencia si se edita (aunque la actividad ya debería tener su idProyecto).
        idProyectoPadre = getIntent().getIntExtra("ID_PROYECTO", -1);
        Log.d(TAG, "onCreate - ID_PROYECTO recibido del Intent: " + idProyectoPadre);


        if (getIntent().hasExtra("ID_ACTIVIDAD_EDITAR")) {
            int idActividad = getIntent().getIntExtra("ID_ACTIVIDAD_EDITAR", -1);
            Log.d(TAG, "onCreate - Modo Edición. ID_ACTIVIDAD_EDITAR: " + idActividad);
            if (idActividad != -1) {
                actividadAEditar = actividadesDAO.getActividadPorId(idActividad); // Carga la actividad completa
                if (actividadAEditar != null) {
                    if (getSupportActionBar() != null) getSupportActionBar().setTitle("Editar Actividad");
                    buttonGuardar.setText("Actualizar Actividad"); // Cambia el texto del botón
                    cargarDatosActividad();
                    // Aseguramos que idProyectoPadre sea el del proyecto de la actividad que se edita
                    // Esto es importante si no se pasó explícitamente o si hubiera una discrepancia.
                    idProyectoPadre = actividadAEditar.getIdProyecto();
                    Log.d(TAG, "onCreate - Actividad cargada para editar. idProyectoPadre actualizado a: " + idProyectoPadre);
                } else {
                    Log.e(TAG, "onCreate - Error: No se pudo cargar la actividad con ID: " + idActividad + " para editar.");
                    Toast.makeText(this, "Error al cargar la actividad para editar.", Toast.LENGTH_SHORT).show();
                    finish(); // Salir si no se puede cargar la actividad
                    return;
                }
            } else {
                Log.w(TAG, "onCreate - Se pasó ID_ACTIVIDAD_EDITAR pero era -1. Tratando como nueva actividad.");
                if (getSupportActionBar() != null) getSupportActionBar().setTitle("Crear Actividad");
                // Se establece el estado por defecto para nueva actividad
                spinnerEstado.setSelection(adapterSpinner.getPosition(Actividad.ESTADO_PENDIENTE));
            }
        } else {
            Log.d(TAG, "onCreate - Modo Crear Nueva Actividad.");
            if (getSupportActionBar() != null) getSupportActionBar().setTitle("Crear Actividad");
            // Establecer estado por defecto para una nueva actividad
            spinnerEstado.setSelection(adapterSpinner.getPosition(Actividad.ESTADO_PENDIENTE));
        }

        // Si después de todo, idProyectoPadre sigue siendo -1 (ej. no se pasó para nueva actividad), es un error.
        if (idProyectoPadre == -1) {
            Toast.makeText(this, "Error: Falta ID del proyecto para la actividad.", Toast.LENGTH_LONG).show();
            Log.e(TAG, "onCreate - Error crítico: idProyectoPadre es -1. Finalizando.");
            finish();
            return;
        }

        editTextFechaInicio.setOnClickListener(v -> mostrarDatePickerDialog(calendarioInicio, editTextFechaInicio, "fechaInicio"));
        editTextFechaFin.setOnClickListener(v -> mostrarDatePickerDialog(calendarioFin, editTextFechaFin, "fechaFin"));

        buttonGuardar.setOnClickListener(v -> guardarActividad());
    }

    private void cargarDatosActividad() {
        if (actividadAEditar != null) { // actividadAEditar ya fue cargada en onCreate
            Log.d(TAG, "cargarDatosActividad - Poblando campos con datos de: " + actividadAEditar.getNombre());
            editTextNombre.setText(actividadAEditar.getNombre());
            editTextDescripcion.setText(actividadAEditar.getDescripcion());
            editTextFechaInicio.setText(actividadAEditar.getFechaInicio());
            editTextFechaFin.setText(actividadAEditar.getFechaFin());

            // Seleccionar el estado en el Spinner
            ArrayAdapter<String> adapter = (ArrayAdapter<String>) spinnerEstado.getAdapter();
            if (actividadAEditar.getEstado() != null) {
                int spinnerPosition = adapter.getPosition(actividadAEditar.getEstado());
                if (spinnerPosition >= 0) {
                    spinnerEstado.setSelection(spinnerPosition);
                    Log.d(TAG, "cargarDatosActividad - Estado '" + actividadAEditar.getEstado() + "' seleccionado en Spinner.");
                } else {
                    Log.w(TAG, "cargarDatosActividad - Estado '" + actividadAEditar.getEstado() + "' no encontrado en Spinner. Se usará el primer item.");
                    spinnerEstado.setSelection(0); // Fallback al primer item si el estado no se encuentra
                }
            } else {
                Log.w(TAG, "cargarDatosActividad - Estado de actividadAEditar es null. Seleccionando PENDIENTE por defecto.");
                spinnerEstado.setSelection(adapter.getPosition(Actividad.ESTADO_PENDIENTE));
            }


            // Parsear fechas para los Calendar, para el DatePickerDialog
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
            try {
                if (actividadAEditar.getFechaInicio() != null && !actividadAEditar.getFechaInicio().isEmpty()) {
                    calendarioInicio.setTime(sdf.parse(actividadAEditar.getFechaInicio()));
                }
                if (actividadAEditar.getFechaFin() != null && !actividadAEditar.getFechaFin().isEmpty()) {
                    calendarioFin.setTime(sdf.parse(actividadAEditar.getFechaFin()));
                }
            } catch (ParseException e) {
                Log.e(TAG, "cargarDatosActividad - Error al parsear fechas.", e);
                // No es necesario terminar la actividad, pero el DatePicker podría no mostrar la fecha correcta.
            }
        } else {
            Log.w(TAG, "cargarDatosActividad - actividadAEditar es null. No se pueden cargar datos.");
        }
    }

    private void mostrarDatePickerDialog(final Calendar calendario, final TextInputEditText editText, final String tipoFecha) {
        DatePickerDialog.OnDateSetListener dateSetListener = (view, year, month, dayOfMonth) -> {
            calendario.set(Calendar.YEAR, year);
            calendario.set(Calendar.MONTH, month);
            calendario.set(Calendar.DAY_OF_MONTH, dayOfMonth);
            actualizarEditTextConFecha(calendario, editText);
            Log.d(TAG, "mostrarDatePickerDialog - Fecha seleccionada para " + tipoFecha + ": " + editText.getText().toString());
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

    private void guardarActividad() {
        Log.d(TAG, "guardarActividad - Iniciando guardado...");
        String nombre = editTextNombre.getText().toString().trim();
        String descripcion = editTextDescripcion.getText().toString().trim();
        String fechaInicioStr = editTextFechaInicio.getText().toString().trim();
        String fechaFinStr = editTextFechaFin.getText().toString().trim();
        String estadoSeleccionado = spinnerEstado.getSelectedItem().toString();

        if (TextUtils.isEmpty(nombre)) {
            editTextNombre.setError("Nombre de actividad requerido.");
            editTextNombre.requestFocus();
            Log.w(TAG, "guardarActividad - Nombre vacío.");
            return;
        }

        // Validaciones de fechas
        try {
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
            Calendar calInicioTemp = Calendar.getInstance();
            Calendar calFinTemp = Calendar.getInstance();
            boolean tieneFechaInicio = !fechaInicioStr.isEmpty();
            boolean tieneFechaFin = !fechaFinStr.isEmpty();

            if (tieneFechaInicio) calInicioTemp.setTime(sdf.parse(fechaInicioStr));
            if (tieneFechaFin) calFinTemp.setTime(sdf.parse(fechaFinStr));

            if (tieneFechaInicio && tieneFechaFin) {
                if (calFinTemp.before(calInicioTemp)) {
                    editTextFechaFin.setError("La fecha de fin no puede ser anterior a la fecha de inicio.");
                    Toast.makeText(this, "La fecha de fin no puede ser anterior a la fecha de inicio.", Toast.LENGTH_LONG).show();
                    editTextFechaFin.requestFocus();
                    Log.w(TAG, "guardarActividad - Validación fallida: Fecha de fin anterior a fecha de inicio.");
                    return;
                }
            }
        } catch (ParseException e) {
            // Esto no debería ocurrir si los DatePickers funcionan bien, pero es una validación extra.
            Toast.makeText(this, "Formato de fecha inválido.", Toast.LENGTH_SHORT).show();
            Log.e(TAG, "guardarActividad - ParseException en fechas.", e);
            return;
        }


        if (actividadAEditar == null) { // CREANDO NUEVA ACTIVIDAD
            Log.d(TAG, "guardarActividad - Creando nueva actividad para proyecto ID: " + idProyectoPadre);
            Actividad nuevaActividad = new Actividad(
                    idProyectoPadre,
                    nombre,
                    descripcion,
                    fechaInicioStr.isEmpty() ? null : fechaInicioStr,
                    fechaFinStr.isEmpty() ? null : fechaFinStr,
                    estadoSeleccionado // El Spinner ya tiene un valor por defecto
            );
            long resultadoId = actividadesDAO.insertarActividad(nuevaActividad); // Usa tu método DAO para insertar
            if (resultadoId != -1) {
                Toast.makeText(this, "Actividad creada exitosamente.", Toast.LENGTH_SHORT).show();
                Log.i(TAG, "guardarActividad - Nueva actividad creada con ID: " + resultadoId);
                setResult(RESULT_OK); // Para notificar a ActividadesActivity que refresque
                finish();
            } else {
                Toast.makeText(this, "Error al crear la actividad.", Toast.LENGTH_SHORT).show();
                Log.e(TAG, "guardarActividad - Error al insertar nueva actividad en la BD.");
            }
        } else { // ACTUALIZANDO ACTIVIDAD EXISTENTE
            Log.d(TAG, "guardarActividad - Actualizando actividad ID: " + actividadAEditar.getIdActividad());

            // Modificamos el objeto existente que ya fue cargado
            actividadAEditar.setNombre(nombre);
            actividadAEditar.setDescripcion(descripcion);
            actividadAEditar.setFechaInicio(fechaInicioStr.isEmpty() ? null : fechaInicioStr);
            actividadAEditar.setFechaFin(fechaFinStr.isEmpty() ? null : fechaFinStr);
            actividadAEditar.setEstado(estadoSeleccionado);
            // idProyecto y idActividad ya están en actividadAEditar

            int resultadoFilas = actividadesDAO.actualizarActividad(actividadAEditar);
            if (resultadoFilas > 0) {
                Toast.makeText(this, "Actividad actualizada exitosamente.", Toast.LENGTH_SHORT).show();
                Log.i(TAG, "guardarActividad - Actividad ID: " + actividadAEditar.getIdActividad() + " actualizada.");
                setResult(RESULT_OK); // Para notificar a ActividadesActivity que refresque
                finish();
            } else {
                Toast.makeText(this, "Error al actualizar la actividad o no hubo cambios.", Toast.LENGTH_SHORT).show();
                Log.e(TAG, "guardarActividad - Error al actualizar actividad ID: " + actividadAEditar.getIdActividad() + " o no hubo cambios. Filas afectadas: " + resultadoFilas);
            }
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            Log.d(TAG, "onOptionsItemSelected - Botón Home presionado.");
            // Considerar si hay cambios sin guardar y preguntar al usuario
            setResult(RESULT_CANCELED); // Si no se guardó nada
            finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}