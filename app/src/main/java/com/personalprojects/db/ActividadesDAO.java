package com.personalprojects.db; // PAQUETE ACTUALIZADO

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import com.personalprojects.model.Actividad; // PAQUETE ACTUALIZADO
import java.util.ArrayList;
import java.util.List;

public class ActividadesDAO {
    private SqlAdmin adminDbHelper;
    private SQLiteDatabase db;

    public ActividadesDAO(Context context) {
        adminDbHelper = new SqlAdmin(context);
    }

    private void openWritable() {
        db = adminDbHelper.getWritableDatabase();
    }

    private void openReadable() {
        db = adminDbHelper.getReadableDatabase();
    }

    private void close() {
        if (db != null && db.isOpen()) {
            db.close();
        }
    }

    public long crearActividad(Actividad actividad) {
        openWritable();
        ContentValues values = new ContentValues();
        values.put(SqlAdmin.ACTIVIDADES_COL_NOMBRE, actividad.getNombre());
        values.put(SqlAdmin.ACTIVIDADES_COL_DESCRIPCION, actividad.getDescripcion());
        values.put(SqlAdmin.ACTIVIDADES_COL_FECHA_INICIO, actividad.getFechaInicio());
        values.put(SqlAdmin.ACTIVIDADES_COL_FECHA_FIN, actividad.getFechaFin());
        values.put(SqlAdmin.ACTIVIDADES_COL_ESTADO, actividad.getEstado());
        values.put(SqlAdmin.ACTIVIDADES_COL_ID_PROYECTO, actividad.getIdProyecto());

        long id = db.insert(SqlAdmin.TABLA_ACTIVIDADES, null, values);
        close();
        return id;
    }

    public Actividad getActividadPorId(int idActividad) {
        openReadable();
        Actividad actividad = null;
        Cursor cursor = db.query(
                SqlAdmin.TABLA_ACTIVIDADES,
                null, // Todas las columnas
                SqlAdmin.ACTIVIDADES_COL_ID + " = ?",
                new String[]{String.valueOf(idActividad)},
                null, null, null
        );

        if (cursor != null) {
            if (cursor.moveToFirst()) {
                actividad = new Actividad(
                        cursor.getInt(cursor.getColumnIndexOrThrow(SqlAdmin.ACTIVIDADES_COL_ID)),
                        cursor.getString(cursor.getColumnIndexOrThrow(SqlAdmin.ACTIVIDADES_COL_NOMBRE)),
                        cursor.getString(cursor.getColumnIndexOrThrow(SqlAdmin.ACTIVIDADES_COL_DESCRIPCION)),
                        cursor.getString(cursor.getColumnIndexOrThrow(SqlAdmin.ACTIVIDADES_COL_FECHA_INICIO)),
                        cursor.getString(cursor.getColumnIndexOrThrow(SqlAdmin.ACTIVIDADES_COL_FECHA_FIN)),
                        cursor.getString(cursor.getColumnIndexOrThrow(SqlAdmin.ACTIVIDADES_COL_ESTADO)),
                        cursor.getInt(cursor.getColumnIndexOrThrow(SqlAdmin.ACTIVIDADES_COL_ID_PROYECTO))
                );
            }
            cursor.close();
        }
        close();
        return actividad;
    }

    public List<Actividad> getActividadesPorProyecto(int idProyecto) {
        openReadable();
        List<Actividad> listaActividades = new ArrayList<>();
        String selection = SqlAdmin.ACTIVIDADES_COL_ID_PROYECTO + " = ?";
        String[] selectionArgs = {String.valueOf(idProyecto)};
        String orderBy = SqlAdmin.ACTIVIDADES_COL_FECHA_INICIO + " ASC"; // O por nombre, etc.

        Cursor cursor = db.query(
                SqlAdmin.TABLA_ACTIVIDADES,
                null, // Todas las columnas
                selection,
                selectionArgs,
                null, null, orderBy
        );

        if (cursor != null) {
            while (cursor.moveToNext()) {
                Actividad actividad = new Actividad(
                        cursor.getInt(cursor.getColumnIndexOrThrow(SqlAdmin.ACTIVIDADES_COL_ID)),
                        cursor.getString(cursor.getColumnIndexOrThrow(SqlAdmin.ACTIVIDADES_COL_NOMBRE)),
                        cursor.getString(cursor.getColumnIndexOrThrow(SqlAdmin.ACTIVIDADES_COL_DESCRIPCION)),
                        cursor.getString(cursor.getColumnIndexOrThrow(SqlAdmin.ACTIVIDADES_COL_FECHA_INICIO)),
                        cursor.getString(cursor.getColumnIndexOrThrow(SqlAdmin.ACTIVIDADES_COL_FECHA_FIN)),
                        cursor.getString(cursor.getColumnIndexOrThrow(SqlAdmin.ACTIVIDADES_COL_ESTADO)),
                        cursor.getInt(cursor.getColumnIndexOrThrow(SqlAdmin.ACTIVIDADES_COL_ID_PROYECTO))
                );
                listaActividades.add(actividad);
            }
            cursor.close();
        }
        close();
        return listaActividades;
    }

    public int actualizarActividad(Actividad actividad) {
        openWritable();
        ContentValues values = new ContentValues();
        values.put(SqlAdmin.ACTIVIDADES_COL_NOMBRE, actividad.getNombre());
        values.put(SqlAdmin.ACTIVIDADES_COL_DESCRIPCION, actividad.getDescripcion());
        values.put(SqlAdmin.ACTIVIDADES_COL_FECHA_INICIO, actividad.getFechaInicio());
        values.put(SqlAdmin.ACTIVIDADES_COL_FECHA_FIN, actividad.getFechaFin());
        values.put(SqlAdmin.ACTIVIDADES_COL_ESTADO, actividad.getEstado());

        String selection = SqlAdmin.ACTIVIDADES_COL_ID + " = ?";
        String[] selectionArgs = {String.valueOf(actividad.getIdActividad())};

        int count = db.update(
                SqlAdmin.TABLA_ACTIVIDADES,
                values,
                selection,
                selectionArgs
        );
        close();
        return count;
    }

    public int eliminarActividad(int idActividad) {
        openWritable();
        String selection = SqlAdmin.ACTIVIDADES_COL_ID + " = ?";
        String[] selectionArgs = {String.valueOf(idActividad)};
        int count = db.delete(SqlAdmin.TABLA_ACTIVIDADES, selection, selectionArgs);
        close();
        return count;
    }

    public int contarActividadesPorEstado(int idProyecto, String estado) {
        openReadable();
        int count = 0;
        String selection = SqlAdmin.ACTIVIDADES_COL_ID_PROYECTO + " = ? AND " + SqlAdmin.ACTIVIDADES_COL_ESTADO + " = ?";
        String[] selectionArgs = {String.valueOf(idProyecto), estado};

        Cursor cursor = db.query(
                SqlAdmin.TABLA_ACTIVIDADES,
                new String[]{"COUNT(*) AS total"},
                selection,
                selectionArgs,
                null, null, null
        );

        if (cursor != null) {
            if (cursor.moveToFirst()) {
                count = cursor.getInt(cursor.getColumnIndexOrThrow("total"));
            }
            cursor.close();
        }
        close();
        return count;
    }

    public int contarTotalActividadesPorProyecto(int idProyecto) {
        openReadable();
        int count = 0;
        String selection = SqlAdmin.ACTIVIDADES_COL_ID_PROYECTO + " = ?";
        String[] selectionArgs = {String.valueOf(idProyecto)};

        Cursor cursor = db.query(
                SqlAdmin.TABLA_ACTIVIDADES,
                new String[]{"COUNT(*) AS total"},
                selection,
                selectionArgs,
                null, null, null
        );

        if (cursor != null) {
            if (cursor.moveToFirst()) {
                count = cursor.getInt(cursor.getColumnIndexOrThrow("total"));
            }
            cursor.close();
        }
        close();
        return count;
    }
}