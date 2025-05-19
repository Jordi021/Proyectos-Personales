package com.personalprojects.db; // Asegúrate que el paquete sea el correcto

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.DatabaseUtils;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.personalprojects.model.Actividad; // Importa tu modelo Actividad

import java.util.ArrayList;
import java.util.List;

public class ActividadesDAO {

    private static final String TAG = "ActividadesDebug"; // Mismo TAG para consistencia
    private SqlAdmin dbHelper;
    // private Context context; // El contexto no es estrictamente necesario aquí si no instancias otros DAOs

    public ActividadesDAO(Context context) {
        // this.context = context;
        // Si SqlAdmin es un Singleton: SqlAdmin.getInstance(context);
        // Si no: new SqlAdmin(context);
        // Asumo que no es Singleton por la forma en que lo proporcionaste.
        this.dbHelper = new SqlAdmin(context);
    }

    // Método para insertar una nueva actividad
    public long insertarActividad(Actividad actividad) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        ContentValues values = new ContentValues();

        // Usa las constantes de SqlAdmin.java
        values.put(SqlAdmin.ACTIVIDADES_COL_ID_PROYECTO, actividad.getIdProyecto());
        values.put(SqlAdmin.ACTIVIDADES_COL_NOMBRE, actividad.getNombre());
        values.put(SqlAdmin.ACTIVIDADES_COL_DESCRIPCION, actividad.getDescripcion());
        values.put(SqlAdmin.ACTIVIDADES_COL_FECHA_INICIO, actividad.getFechaInicio());
        values.put(SqlAdmin.ACTIVIDADES_COL_FECHA_FIN, actividad.getFechaFin());
        values.put(SqlAdmin.ACTIVIDADES_COL_ESTADO, actividad.getEstado());

        long nuevoId = -1;
        try {
            db.beginTransaction();
            nuevoId = db.insertOrThrow(SqlAdmin.TABLA_ACTIVIDADES, null, values);
            db.setTransactionSuccessful();
            Log.d(TAG, "DAO.insertarActividad - Actividad '" + actividad.getNombre() + "' insertada con ID: " + nuevoId + " para proyecto ID: " + actividad.getIdProyecto());
        } catch (Exception e) {
            Log.e(TAG, "DAO.insertarActividad - Error al insertar actividad: " + actividad.getNombre(), e);
        } finally {
            if (db != null && db.inTransaction()) { // Solo finalizar si la transacción está activa
                db.endTransaction();
            }
            // No cierres db aquí, SQLiteOpenHelper lo gestiona
        }
        return nuevoId;
    }

    // Método para actualizar una actividad existente
    public int actualizarActividad(Actividad actividad) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        ContentValues values = new ContentValues();

        values.put(SqlAdmin.ACTIVIDADES_COL_ID_PROYECTO, actividad.getIdProyecto());
        values.put(SqlAdmin.ACTIVIDADES_COL_NOMBRE, actividad.getNombre());
        values.put(SqlAdmin.ACTIVIDADES_COL_DESCRIPCION, actividad.getDescripcion());
        values.put(SqlAdmin.ACTIVIDADES_COL_FECHA_INICIO, actividad.getFechaInicio());
        values.put(SqlAdmin.ACTIVIDADES_COL_FECHA_FIN, actividad.getFechaFin());
        values.put(SqlAdmin.ACTIVIDADES_COL_ESTADO, actividad.getEstado());

        int filasAfectadas = 0;
        try {
            db.beginTransaction();
            filasAfectadas = db.update(SqlAdmin.TABLA_ACTIVIDADES, values,
                    SqlAdmin.ACTIVIDADES_COL_ID + " = ?", // Condición WHERE por id_actividad
                    new String[]{String.valueOf(actividad.getIdActividad())});
            db.setTransactionSuccessful();
            Log.d(TAG, "DAO.actualizarActividad - Actividad ID " + actividad.getIdActividad() + " ('" + actividad.getNombre() + "'). Filas afectadas: " + filasAfectadas);
        } catch (Exception e) {
            Log.e(TAG, "DAO.actualizarActividad - Error actualizando actividad ID: " + actividad.getIdActividad(), e);
        } finally {
            if (db != null && db.inTransaction()) {
                db.endTransaction();
            }
        }
        return filasAfectadas;
    }

    // Método para obtener una actividad por su ID
    public Actividad getActividadPorId(int idActividad) {
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        Cursor cursor = null;
        Actividad actividad = null;

        String query = "SELECT * FROM " + SqlAdmin.TABLA_ACTIVIDADES +
                " WHERE " + SqlAdmin.ACTIVIDADES_COL_ID + " = ?";
        Log.d(TAG, "DAO.getActividadPorId - Query: " + query + ", idActividad: " + idActividad);

        try {
            cursor = db.rawQuery(query, new String[]{String.valueOf(idActividad)});
            if (cursor != null && cursor.moveToFirst()) {
                actividad = new Actividad(); // Usa constructor vacío
                // Mapea los datos del cursor al objeto Actividad usando las constantes de SqlAdmin
                actividad.setIdActividad(cursor.getInt(cursor.getColumnIndexOrThrow(SqlAdmin.ACTIVIDADES_COL_ID)));
                actividad.setIdProyecto(cursor.getInt(cursor.getColumnIndexOrThrow(SqlAdmin.ACTIVIDADES_COL_ID_PROYECTO)));
                actividad.setNombre(cursor.getString(cursor.getColumnIndexOrThrow(SqlAdmin.ACTIVIDADES_COL_NOMBRE)));
                actividad.setDescripcion(cursor.getString(cursor.getColumnIndexOrThrow(SqlAdmin.ACTIVIDADES_COL_DESCRIPCION)));
                actividad.setFechaInicio(cursor.getString(cursor.getColumnIndexOrThrow(SqlAdmin.ACTIVIDADES_COL_FECHA_INICIO)));
                actividad.setFechaFin(cursor.getString(cursor.getColumnIndexOrThrow(SqlAdmin.ACTIVIDADES_COL_FECHA_FIN)));
                actividad.setEstado(cursor.getString(cursor.getColumnIndexOrThrow(SqlAdmin.ACTIVIDADES_COL_ESTADO)));
                Log.d(TAG, "DAO.getActividadPorId - Actividad encontrada: " + actividad.getNombre());
            } else {
                Log.w(TAG, "DAO.getActividadPorId - No se encontró actividad con ID: " + idActividad);
            }
        } catch (Exception e) {
            Log.e(TAG, "DAO.getActividadPorId - Error al obtener actividad", e);
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }
        return actividad;
    }

    // Método para obtener todas las actividades de un proyecto específico
    public List<Actividad> getActividadesPorProyecto(int idProyecto) {
        List<Actividad> listaActividades = new ArrayList<>();
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        Cursor cursor = null;

        String query = "SELECT * FROM " + SqlAdmin.TABLA_ACTIVIDADES +
                " WHERE " + SqlAdmin.ACTIVIDADES_COL_ID_PROYECTO + " = ?" +
                " ORDER BY " + SqlAdmin.ACTIVIDADES_COL_FECHA_INICIO + " ASC, " + SqlAdmin.ACTIVIDADES_COL_NOMBRE + " ASC"; // Ejemplo de orden

        Log.d(TAG, "DAO.getActividadesPorProyecto - Query: " + query + ", idProyecto: " + idProyecto);

        try {
            cursor = db.rawQuery(query, new String[]{String.valueOf(idProyecto)});
            if (cursor != null) {
                Log.d(TAG, "DAO.getActividadesPorProyecto - Cursor count para proyecto ID " + idProyecto + ": " + cursor.getCount());
                if (cursor.moveToFirst()) {
                    do {
                        Actividad actividad = new Actividad();
                        actividad.setIdActividad(cursor.getInt(cursor.getColumnIndexOrThrow(SqlAdmin.ACTIVIDADES_COL_ID)));
                        actividad.setIdProyecto(cursor.getInt(cursor.getColumnIndexOrThrow(SqlAdmin.ACTIVIDADES_COL_ID_PROYECTO)));
                        actividad.setNombre(cursor.getString(cursor.getColumnIndexOrThrow(SqlAdmin.ACTIVIDADES_COL_NOMBRE)));
                        actividad.setDescripcion(cursor.getString(cursor.getColumnIndexOrThrow(SqlAdmin.ACTIVIDADES_COL_DESCRIPCION)));
                        actividad.setFechaInicio(cursor.getString(cursor.getColumnIndexOrThrow(SqlAdmin.ACTIVIDADES_COL_FECHA_INICIO)));
                        actividad.setFechaFin(cursor.getString(cursor.getColumnIndexOrThrow(SqlAdmin.ACTIVIDADES_COL_FECHA_FIN)));
                        actividad.setEstado(cursor.getString(cursor.getColumnIndexOrThrow(SqlAdmin.ACTIVIDADES_COL_ESTADO)));
                        listaActividades.add(actividad);
                        Log.v(TAG, "DAO.getActividadesPorProyecto - Leída actividad: " + actividad.getNombre()); // Verbose para no llenar tanto el log
                    } while (cursor.moveToNext());
                } else {
                    Log.d(TAG, "DAO.getActividadesPorProyecto - El cursor está vacío para proyecto ID " + idProyecto);
                }
            } else {
                Log.w(TAG, "DAO.getActividadesPorProyecto - El cursor es null para proyecto ID " + idProyecto);
            }
        } catch (Exception e) {
            Log.e(TAG, "DAO.getActividadesPorProyecto - Error al obtener actividades", e);
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }
        Log.d(TAG, "DAO.getActividadesPorProyecto - Retornando " + listaActividades.size() + " actividades para idProyecto: " + idProyecto);
        return listaActividades;
    }

    // Método para eliminar una actividad por su ID
    public int eliminarActividad(int idActividad) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        int filasAfectadas = 0;
        try {
            db.beginTransaction();
            filasAfectadas = db.delete(SqlAdmin.TABLA_ACTIVIDADES,
                    SqlAdmin.ACTIVIDADES_COL_ID + " = ?",
                    new String[]{String.valueOf(idActividad)});
            db.setTransactionSuccessful();
            Log.d(TAG, "DAO.eliminarActividad - Filas afectadas para ID " + idActividad + ": " + filasAfectadas);
        } catch (Exception e) {
            Log.e(TAG, "DAO.eliminarActividad - Error eliminando actividad ID: " + idActividad, e);
        } finally {
            if (db != null && db.inTransaction()) {
                db.endTransaction();
            }
        }
        return filasAfectadas;
    }

    // Método para contar el total de actividades de un proyecto
    public int contarTotalActividadesPorProyecto(int idProyecto) {
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        long count = 0;
        try {
            count = DatabaseUtils.queryNumEntries(db, SqlAdmin.TABLA_ACTIVIDADES,
                    SqlAdmin.ACTIVIDADES_COL_ID_PROYECTO + "=?",
                    new String[]{String.valueOf(idProyecto)});
            Log.d(TAG, "DAO.contarTotalActividadesPorProyecto - Total para idProyecto " + idProyecto + ": " + count);
        } catch (Exception e) {
            Log.e(TAG, "DAO.contarTotalActividadesPorProyecto - Error contando actividades", e);
        }
        return (int) count;
    }

    // Método para contar actividades de un proyecto por un estado específico
    public int contarActividadesPorEstado(int idProyecto, String estado) {
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        long count = 0;
        try {
            count = DatabaseUtils.queryNumEntries(db, SqlAdmin.TABLA_ACTIVIDADES,
                    SqlAdmin.ACTIVIDADES_COL_ID_PROYECTO + "=? AND " + SqlAdmin.ACTIVIDADES_COL_ESTADO + "=?",
                    new String[]{String.valueOf(idProyecto), estado});
            Log.d(TAG, "DAO.contarActividadesPorEstado - Total para idProyecto " + idProyecto + " con estado '" + estado + "': " + count);
        } catch (Exception e) {
            Log.e(TAG, "DAO.contarActividadesPorEstado - Error contando actividades por estado", e);
        }
        return (int) count;
    }
}