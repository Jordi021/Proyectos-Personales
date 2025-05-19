package com.personalprojects.db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class SqlAdmin extends SQLiteOpenHelper {

    private static final String DATABASE_NOMBRE = "gestion_proyectos_pp.db"; // Nombre único para evitar colisiones
    private static final int DATABASE_VERSION = 1;

    // Tabla Usuarios
    public static final String TABLA_USUARIOS = "usuarios";
    public static final String USUARIOS_COL_ID = "id_usuario";
    public static final String USUARIOS_COL_NOMBRE_USUARIO = "nombre_usuario";
    public static final String USUARIOS_COL_CONTRASENA = "contrasena";
    public static final String USUARIOS_COL_EMAIL = "email";

    private static final String SQL_CREAR_TABLA_USUARIOS =
            "CREATE TABLE " + TABLA_USUARIOS + " (" +
                    USUARIOS_COL_ID + " INTEGER PRIMARY KEY AUTOINCREMENT," +
                    USUARIOS_COL_NOMBRE_USUARIO + " TEXT UNIQUE NOT NULL," +
                    USUARIOS_COL_CONTRASENA + " TEXT NOT NULL," +
                    USUARIOS_COL_EMAIL + " TEXT)";

    // Tabla Proyectos
    public static final String TABLA_PROYECTOS = "proyectos";
    public static final String PROYECTOS_COL_ID = "id_proyecto";
    public static final String PROYECTOS_COL_NOMBRE = "nombre_proyecto";
    public static final String PROYECTOS_COL_DESCRIPCION = "descripcion";
    public static final String PROYECTOS_COL_FECHA_INICIO = "fecha_inicio";
    public static final String PROYECTOS_COL_FECHA_FIN = "fecha_fin";
    public static final String PROYECTOS_COL_ID_USUARIO = "id_usuario_fk";

    private static final String SQL_CREAR_TABLA_PROYECTOS =
            "CREATE TABLE " + TABLA_PROYECTOS + " (" +
                    PROYECTOS_COL_ID + " INTEGER PRIMARY KEY AUTOINCREMENT," +
                    PROYECTOS_COL_NOMBRE + " TEXT NOT NULL," +
                    PROYECTOS_COL_DESCRIPCION + " TEXT," +
                    PROYECTOS_COL_FECHA_INICIO + " TEXT," +
                    PROYECTOS_COL_FECHA_FIN + " TEXT," +
                    PROYECTOS_COL_ID_USUARIO + " INTEGER NOT NULL," +
                    "FOREIGN KEY(" + PROYECTOS_COL_ID_USUARIO + ") REFERENCES " +
                    TABLA_USUARIOS + "(" + USUARIOS_COL_ID + ") ON DELETE CASCADE)";

    // Tabla Actividades
    public static final String TABLA_ACTIVIDADES = "actividades";
    public static final String ACTIVIDADES_COL_ID = "id_actividad";
    public static final String ACTIVIDADES_COL_NOMBRE = "nombre_actividad";
    public static final String ACTIVIDADES_COL_DESCRIPCION = "descripcion";
    public static final String ACTIVIDADES_COL_FECHA_INICIO = "fecha_inicio";
    public static final String ACTIVIDADES_COL_FECHA_FIN = "fecha_fin";
    public static final String ACTIVIDADES_COL_ESTADO = "estado";
    public static final String ACTIVIDADES_COL_ID_PROYECTO = "id_proyecto_fk";

    private static final String SQL_CREAR_TABLA_ACTIVIDADES =
            "CREATE TABLE " + TABLA_ACTIVIDADES + " (" +
                    ACTIVIDADES_COL_ID + " INTEGER PRIMARY KEY AUTOINCREMENT," +
                    ACTIVIDADES_COL_NOMBRE + " TEXT NOT NULL," +
                    ACTIVIDADES_COL_DESCRIPCION + " TEXT," +
                    ACTIVIDADES_COL_FECHA_INICIO + " TEXT," +
                    ACTIVIDADES_COL_FECHA_FIN + " TEXT," +
                    ACTIVIDADES_COL_ESTADO + " TEXT NOT NULL," +
                    ACTIVIDADES_COL_ID_PROYECTO + " INTEGER NOT NULL," +
                    "FOREIGN KEY(" + ACTIVIDADES_COL_ID_PROYECTO + ") REFERENCES " +
                    TABLA_PROYECTOS + "(" + PROYECTOS_COL_ID + ") ON DELETE CASCADE)";


    public SqlAdmin(Context context) {
        super(context, DATABASE_NOMBRE, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(SQL_CREAR_TABLA_USUARIOS);
        db.execSQL(SQL_CREAR_TABLA_PROYECTOS);
        db.execSQL(SQL_CREAR_TABLA_ACTIVIDADES);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLA_ACTIVIDADES);
        db.execSQL("DROP TABLE IF EXISTS " + TABLA_PROYECTOS);
        db.execSQL("DROP TABLE IF EXISTS " + TABLA_USUARIOS);
        onCreate(db);
    }

    @Override
    public void onOpen(SQLiteDatabase db) {
        super.onOpen(db);
        if (!db.isReadOnly()) {
            db.execSQL("PRAGMA foreign_keys=ON;");
        }
    }
}
