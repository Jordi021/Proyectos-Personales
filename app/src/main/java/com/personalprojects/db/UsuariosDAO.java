package com.personalprojects.db; // PAQUETE ACTUALIZADO

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import com.personalprojects.model.Usuario; // PAQUETE ACTUALIZADO

public class UsuariosDAO {
    private SqlAdmin adminDbHelper;
    private SQLiteDatabase db; // Para optimizar aperturas y cierres

    public UsuariosDAO(Context context) {
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

    public long registrarUsuario(Usuario usuario) {
        openWritable();
        ContentValues values = new ContentValues();
        values.put(SqlAdmin.USUARIOS_COL_NOMBRE_USUARIO, usuario.getNombreUsuario());
        values.put(SqlAdmin.USUARIOS_COL_CONTRASENA, usuario.getContrasena()); // En un proyecto real, hashear la contraseña
        values.put(SqlAdmin.USUARIOS_COL_EMAIL, usuario.getEmail());

        long id = -1;
        try {
            id = db.insertOrThrow(SqlAdmin.TABLA_USUARIOS, null, values);
        } catch (android.database.sqlite.SQLiteConstraintException e) {
            // Esto sucedería si el nombre de usuario ya existe (debido a UNIQUE)
            System.err.println("Error al insertar usuario, posible duplicado: " + e.getMessage());
        } finally {
            close();
        }
        return id; // Retorna el ID del nuevo usuario o -1 si hay error/duplicado
    }

    public Usuario validarUsuario(String nombreUsuario, String contrasena) {
        openReadable();
        String[] projection = {
                SqlAdmin.USUARIOS_COL_ID,
                SqlAdmin.USUARIOS_COL_NOMBRE_USUARIO,
                SqlAdmin.USUARIOS_COL_CONTRASENA,
                SqlAdmin.USUARIOS_COL_EMAIL
        };

        String selection = SqlAdmin.USUARIOS_COL_NOMBRE_USUARIO + " = ? AND " + SqlAdmin.USUARIOS_COL_CONTRASENA + " = ?";
        String[] selectionArgs = {nombreUsuario, contrasena};

        Cursor cursor = db.query(
                SqlAdmin.TABLA_USUARIOS,
                projection,
                selection,
                selectionArgs,
                null,
                null,
                null
        );

        Usuario usuario = null;
        if (cursor != null) {
            if (cursor.moveToFirst()) {
                usuario = new Usuario(
                        cursor.getInt(cursor.getColumnIndexOrThrow(SqlAdmin.USUARIOS_COL_ID)),
                        cursor.getString(cursor.getColumnIndexOrThrow(SqlAdmin.USUARIOS_COL_NOMBRE_USUARIO)),
                        cursor.getString(cursor.getColumnIndexOrThrow(SqlAdmin.USUARIOS_COL_CONTRASENA)),
                        cursor.getString(cursor.getColumnIndexOrThrow(SqlAdmin.USUARIOS_COL_EMAIL))
                );
            }
            cursor.close();
        }
        close();
        return usuario;
    }

    public boolean existeUsuario(String nombreUsuario) {
        openReadable();
        String[] projection = {SqlAdmin.USUARIOS_COL_ID};
        String selection = SqlAdmin.USUARIOS_COL_NOMBRE_USUARIO + " = ?";
        String[] selectionArgs = {nombreUsuario};

        Cursor cursor = db.query(
                SqlAdmin.TABLA_USUARIOS,
                projection,
                selection,
                selectionArgs,
                null,
                null,
                null
        );

        boolean existe = false;
        if (cursor != null) {
            existe = cursor.getCount() > 0;
            cursor.close();
        }
        close();
        return existe;
    }

    // Método para simular recuperación de contraseña (solo muestra la info)
    public String obtenerContrasenaPorNombreUsuarioYEmail(String nombreUsuario, String email) {
        openReadable();
        String contrasena = null;
        String[] projection = {SqlAdmin.USUARIOS_COL_CONTRASENA};
        String selection = SqlAdmin.USUARIOS_COL_NOMBRE_USUARIO + " = ? AND " + SqlAdmin.USUARIOS_COL_EMAIL + " = ?";
        String[] selectionArgs = {nombreUsuario, email};

        Cursor cursor = db.query(
                SqlAdmin.TABLA_USUARIOS,
                projection,
                selection,
                selectionArgs,
                null,
                null,
                null
        );

        if (cursor != null) {
            if (cursor.moveToFirst()) {
                contrasena = cursor.getString(cursor.getColumnIndexOrThrow(SqlAdmin.USUARIOS_COL_CONTRASENA));
            }
            cursor.close();
        }
        close();
        return contrasena; // Retorna la contraseña o null si no se encuentra la combinación
    }
}