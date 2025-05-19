package com.personalprojects; // PAQUETE ACTUALIZADO

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import android.app.DatePickerDialog;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.MenuItem;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.Toast;
import com.google.android.material.textfield.TextInputEditText;
import com.personalprojects.db.ActividadesDAO;
import com.personalprojects.model.Actividad;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

public class EditorActividadActivity extends AppCompatActivity {

    private TextInputEditText editTextNombre, editTextDescripcion, editTextFechaInicio, editTextFechaFin;
    private Spinner spinnerEstado;
    private Button buttonGuardar;
    private ActividadesDAO actividadesDAO;
    private Calendar calendarioInicio, calendarioFin;

    private int idProyectoPadre;
    private int idActividadEditar = -1; // -1 para crear, > -1 para editar

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_editor_actividad);

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

        // Configurar Spinner de Estados
        ArrayAdapter<CharSequence> adapterSpinner = ArrayAdapter.createFromResource(this,
                R.array.estados_actividad, android.R.layout.simple_spinner_item);
        adapterSpinner.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerEstado.setAdapter(adapterSpinner);

        // Recoger datos del Intent
        idProyectoPadre = getIntent().getIntExtra("ID_PROYECTO", -1);
        if (getIntent().hasExtra("ID_ACTIVIDAD_EDITAR")) {
            idActividadEditar = getIntent().getIntExtra("ID_ACTIVIDAD_EDITAR", -1);
            if (getSupportActionBar() != null) getSupportActionBar().setTitle("Editar Actividad");
            buttonGuardar.setText(R.string.guardar); // O "Actualizar Actividad"
            cargarDatosActividad();
        } else {
            if (getSupportActionBar() != null) getSupportActionBar().setTitle("Crear Actividad");
        }

        if (idProyectoPadre == -1 && idActividadEditar == -1) {
            Toast.makeText(this, "Error: Falta ID del proyecto.", Toast.LENGTH_LONG).show();
            finish();
            return;
        }

        editTextFechaInicio.setOnClickListener(v -> mostrarDatePickerDialog(calendarioInicio, editTextFechaInicio));
        editTextFechaFin.setOnClickListener(v -> mostrarDatePickerDialog(calendarioFin, editTextFechaFin));

        buttonGuardar.setOnClickListener(v -> guardarActividad());
    }

    private void cargarDatosActividad() {
        if (idActividadEditar != -1) {
            Actividad actividad = actividadesDAO.getActividadPorId(idActividadEditar);
            if (actividad != null) {
                editTextNombre.setText(actividad.getNombre());
                editTextDescripcion.setText(actividad.getDescripcion());
                editTextFechaInicio.setText(actividad.getFechaInicio());
                editTextFechaFin.setText(actividad.getFechaFin());
                idProyectoPadre = actividad.getIdProyecto(); // Asegurar ID de proyecto

                // Seleccionar el estado en el Spinner
                List<String> estadosArray = Arrays.asList(getResources().getStringArray(R.array.estados_actividad));
                int spinnerPosition = estadosArray.indexOf(actividad.getEstado());
                if (spinnerPosition >= 0) {
                    spinnerEstado.setSelection(spinnerPosition);
                }

                SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
                try {
                    if (actividad.getFechaInicio() != null && !actividad.getFechaInicio().isEmpty())
                        calendarioInicio.setTime(sdf.parse(actividad.getFechaInicio()));
                    if (actividad.getFechaFin() != null && !actividad.getFechaFin().isEmpty())
                        calendarioFin.setTime(sdf.parse(actividad.getFechaFin()));
                } catch (Exception e) {
                    e.printStackTrace();
                }
            } else {
                Toast.makeText(this, "Error al cargar la actividad para editar.", Toast.LENGTH_SHORT).show();
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

    private void guardarActividad() {
        String nombre = editTextNombre.getText().toString().trim();
        String descripcion = editTextDescripcion.getText().toString().trim();
        String fechaInicio = editTextFechaInicio.getText().toString().trim();
        String fechaFin = editTextFechaFin.getText().toString().trim();
        String estado = spinnerEstado.getSelectedItem().toString();

        if (TextUtils.isEmpty(nombre)) {
            editTextNombre.setError("Nombre de actividad requerido.");
            editTextNombre.requestFocus();
            return;
        }
        if (!fechaInicio.isEmpty() && !fechaFin.isEmpty()) {
            if (calendarioFin.before(calendarioInicio)) {
                editTextFechaFin.setError("La fecha de fin no puede ser anterior a la fecha de inicio.");
                Toast.makeText(this, "La fecha de fin no puede ser anterior a la fecha de inicio.", Toast.LENGTH_LONG).show();
                editTextFechaFin.requestFocus();
                return;
            }
        }


        Actividad actividad;
        if (idActividadEditar == -1) { // Crear nueva
            actividad = new Actividad(nombre, descripcion, fechaInicio.isEmpty() ? null : fechaInicio, fechaFin.isEmpty() ? null : fechaFin, estado, idProyectoPadre);
            long resultado = actividadesDAO.crearActividad(actividad);
            if (resultado != -1) {
                Toast.makeText(this, "Actividad creada.", Toast.LENGTH_SHORT).show();
                finish();
            } else {
                Toast.makeText(this, "Error al crear actividad.", Toast.LENGTH_SHORT).show();
            }
        } else { // Actualizar
            actividad = new Actividad(idActividadEditar, nombre, descripcion, fechaInicio.isEmpty() ? null : fechaInicio, fechaFin.isEmpty() ? null : fechaFin, estado, idProyectoPadre);
            int resultado = actividadesDAO.actualizarActividad(actividad);
            if (resultado > 0) {
                Toast.makeText(this, "Actividad actualizada.", Toast.LENGTH_SHORT).show();
                finish();
            } else {
                Toast.makeText(this, "Error al actualizar actividad.", Toast.LENGTH_SHORT).show();
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