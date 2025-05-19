package com.personalprojects.db; // PAQUETE ACTUALIZADO

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import com.personalprojects.model.Proyecto; // PAQUETE ACTUALIZADO
import java.util.ArrayList;
import java.util.List;

public class ProyectosDAO {
    private SqlAdmin adminDbHelper;
    private SQLiteDatabase db;

    public ProyectosDAO(Context context) {
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

    public long crearProyecto(Proyecto proyecto) {
        openWritable();
        ContentValues values = new ContentValues();
        values.put(SqlAdmin.PROYECTOS_COL_NOMBRE, proyecto.getNombre());
        values.put(SqlAdmin.PROYECTOS_COL_DESCRIPCION, proyecto.getDescripcion());
        values.put(SqlAdmin.PROYECTOS_COL_FECHA_INICIO, proyecto.getFechaInicio());
        values.put(SqlAdmin.PROYECTOS_COL_FECHA_FIN, proyecto.getFechaFin());
        values.put(SqlAdmin.PROYECTOS_COL_ID_USUARIO, proyecto.getIdUsuario());

        long id = db.insert(SqlAdmin.TABLA_PROYECTOS, null, values);
        close();
        return id;
    }

    public Proyecto getProyectoPorId(int idProyecto) {
        openReadable();
        Proyecto proyecto = null;
        String[] projection = {
                SqlAdmin.PROYECTOS_COL_ID,
                SqlAdmin.PROYECTOS_COL_NOMBRE,
                SqlAdmin.PROYECTOS_COL_DESCRIPCION,
                SqlAdmin.PROYECTOS_COL_FECHA_INICIO,
                SqlAdmin.PROYECTOS_COL_FECHA_FIN,
                SqlAdmin.PROYECTOS_COL_ID_USUARIO
        };
        String selection = SqlAdmin.PROYECTOS_COL_ID + " = ?";
        String[] selectionArgs = {String.valueOf(idProyecto)};

        Cursor cursor = db.query(
                SqlAdmin.TABLA_PROYECTOS,
                projection,
                selection,
                selectionArgs,
                null,
                null,
                null
        );

        if (cursor != null) {
            if (cursor.moveToFirst()) {
                proyecto = new Proyecto(
                        cursor.getInt(cursor.getColumnIndexOrThrow(SqlAdmin.PROYECTOS_COL_ID)),
                        cursor.getString(cursor.getColumnIndexOrThrow(SqlAdmin.PROYECTOS_COL_NOMBRE)),
                        cursor.getString(cursor.getColumnIndexOrThrow(SqlAdmin.PROYECTOS_COL_DESCRIPCION)),
                        cursor.getString(cursor.getColumnIndexOrThrow(SqlAdmin.PROYECTOS_COL_FECHA_INICIO)),
                        cursor.getString(cursor.getColumnIndexOrThrow(SqlAdmin.PROYECTOS_COL_FECHA_FIN)),
                        cursor.getInt(cursor.getColumnIndexOrThrow(SqlAdmin.PROYECTOS_COL_ID_USUARIO))
                );
            }
            cursor.close();
        }
        close();
        return proyecto;
    }

    public List<Proyecto> getProyectosPorUsuario(int idUsuario) {
        openReadable();
        List<Proyecto> listaProyectos = new ArrayList<>();
        String[] projection = {
                SqlAdmin.PROYECTOS_COL_ID,
                SqlAdmin.PROYECTOS_COL_NOMBRE,
                SqlAdmin.PROYECTOS_COL_DESCRIPCION,
                SqlAdmin.PROYECTOS_COL_FECHA_INICIO,
                SqlAdmin.PROYECTOS_COL_FECHA_FIN,
                SqlAdmin.PROYECTOS_COL_ID_USUARIO
        };
        String selection = SqlAdmin.PROYECTOS_COL_ID_USUARIO + " = ?";
        String[] selectionArgs = {String.valueOf(idUsuario)};
        String orderBy = SqlAdmin.PROYECTOS_COL_NOMBRE + " ASC"; // O por fecha, etc.

        Cursor cursor = db.query(
                SqlAdmin.TABLA_PROYECTOS,
                projection,
                selection,
                selectionArgs,
                null,
                null,
                orderBy
        );

        if (cursor != null) {
            while (cursor.moveToNext()) {
                Proyecto proyecto = new Proyecto(
                        cursor.getInt(cursor.getColumnIndexOrThrow(SqlAdmin.PROYECTOS_COL_ID)),
                        cursor.getString(cursor.getColumnIndexOrThrow(SqlAdmin.PROYECTOS_COL_NOMBRE)),
                        cursor.getString(cursor.getColumnIndexOrThrow(SqlAdmin.PROYECTOS_COL_DESCRIPCION)),
                        cursor.getString(cursor.getColumnIndexOrThrow(SqlAdmin.PROYECTOS_COL_FECHA_INICIO)),
                        cursor.getString(cursor.getColumnIndexOrThrow(SqlAdmin.PROYECTOS_COL_FECHA_FIN)),
                        cursor.getInt(cursor.getColumnIndexOrThrow(SqlAdmin.PROYECTOS_COL_ID_USUARIO))
                );
                listaProyectos.add(proyecto);
            }
            cursor.close();
        }
        close();
        return listaProyectos;
    }

    public int actualizarProyecto(Proyecto proyecto) {
        openWritable();
        ContentValues values = new ContentValues();
        values.put(SqlAdmin.PROYECTOS_COL_NOMBRE, proyecto.getNombre());
        values.put(SqlAdmin.PROYECTOS_COL_DESCRIPCION, proyecto.getDescripcion());
        values.put(SqlAdmin.PROYECTOS_COL_FECHA_INICIO, proyecto.getFechaInicio());
        values.put(SqlAdmin.PROYECTOS_COL_FECHA_FIN, proyecto.getFechaFin());
        // No actualizamos el idUsuario, ya que el dueño no debería cambiar fácilmente.

        String selection = SqlAdmin.PROYECTOS_COL_ID + " = ?";
        String[] selectionArgs = {String.valueOf(proyecto.getIdProyecto())};

        int count = db.update(
                SqlAdmin.TABLA_PROYECTOS,
                values,
                selection,
                selectionArgs
        );
        close();
        return count; // Número de filas afectadas
    }

    public int eliminarProyecto(int idProyecto) {
        openWritable();
        // Gracias a ON DELETE CASCADE, las actividades asociadas también se eliminarán.
        String selection = SqlAdmin.PROYECTOS_COL_ID + " = ?";
        String[] selectionArgs = {String.valueOf(idProyecto)};
        int count = db.delete(SqlAdmin.TABLA_PROYECTOS, selection, selectionArgs);
        close();
        return count; // Número de filas eliminadas
    }
}