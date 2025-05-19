package com.personalprojects.adapter; // PAQUETE ACTUALIZADO

import android.content.Context;
import android.content.res.ColorStateList; // Para los colores de la ProgressBar
import android.util.Log; // Para logs
import android.view.LayoutInflater;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.PopupMenu;
import android.widget.ProgressBar;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat; // Para los colores
import androidx.recyclerview.widget.RecyclerView;
import com.personalprojects.R; // PAQUETE ACTUALIZADO para R
import com.personalprojects.db.ActividadesDAO; // IMPORTANTE
import com.personalprojects.model.Actividad; // IMPORTANTE para la constante de estado
import com.personalprojects.model.Proyecto; // PAQUETE ACTUALIZADO
import java.util.List;
import java.util.Locale;

public class ProyectoAdapter extends RecyclerView.Adapter<ProyectoAdapter.ProyectoViewHolder> {

    private static final String TAG = "ProyectoAdapterDebug"; // TAG para logs

    private List<Proyecto> listaProyectos;
    private Context context;
    private OnProyectoClickListener clickListener;
    private OnProyectoOptionsListener optionsListener;
    private ActividadesDAO actividadesDAO; // DAO para calcular el progreso

    public interface OnProyectoClickListener {
        void onProyectoClick(Proyecto proyecto);
    }

    public interface OnProyectoOptionsListener {
        void onEditProyecto(Proyecto proyecto);
        void onDeleteProyecto(Proyecto proyecto);
        void onViewActividades(Proyecto proyecto);
    }

    public ProyectoAdapter(Context context, List<Proyecto> listaProyectos,
                           OnProyectoClickListener clickListener, OnProyectoOptionsListener optionsListener) {
        this.context = context;
        this.listaProyectos = listaProyectos;
        this.clickListener = clickListener;
        this.optionsListener = optionsListener;
        this.actividadesDAO = new ActividadesDAO(context); // Inicializa el DAO aquí
        Log.d(TAG, "Constructor - Adapter creado. DAO de actividades inicializado.");
    }

    @NonNull
    @Override
    public ProyectoViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        Log.d(TAG, "onCreateViewHolder - Inflando item_proyecto");
        View view = LayoutInflater.from(context).inflate(R.layout.item_proyecto, parent, false);
        return new ProyectoViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ProyectoViewHolder holder, int position) {
        if (listaProyectos == null || listaProyectos.isEmpty() || position >= listaProyectos.size()) {
            Log.w(TAG, "onBindViewHolder - Lista nula, vacía o posición fuera de límites.");
            return;
        }
        Proyecto proyecto = listaProyectos.get(position);
        Log.d(TAG, "onBindViewHolder - Proyecto: " + proyecto.getNombre() + ", ID: " + proyecto.getIdProyecto());

        holder.tvNombreProyecto.setText(proyecto.getNombre());
        holder.tvDescripcionProyecto.setText(proyecto.getDescripcion());

        String fechaInicio = proyecto.getFechaInicio() != null ? proyecto.getFechaInicio() : "N/A";
        String fechaFin = proyecto.getFechaFin() != null ? proyecto.getFechaFin() : "N/A";
        String fechasFormateadas = String.format(Locale.getDefault(), "Inicio: %s - Fin: %s", fechaInicio, fechaFin);
        holder.tvFechasProyecto.setText(fechasFormateadas);

        // --- CÁLCULO DEL PROGRESO REAL ---
        if (actividadesDAO != null) {
            // Usamos los mismos métodos de conteo que en ActividadesActivity
            int totalActividades = actividadesDAO.contarTotalActividadesPorProyecto(proyecto.getIdProyecto());
            // Asegúrate que Actividad.ESTADO_REALIZADO sea la constante correcta
            int actividadesRealizadas = actividadesDAO.contarActividadesPorEstado(proyecto.getIdProyecto(), Actividad.ESTADO_REALIZADO);

            Log.d(TAG, "onBindViewHolder - Progreso para proyecto '" + proyecto.getNombre() + "': Total=" + totalActividades + ", Realizadas=" + actividadesRealizadas);

            if (totalActividades > 0) {
                double progresoDouble = ((double) actividadesRealizadas / totalActividades) * 100.0;
                int progresoInt = (int) progresoDouble;
                holder.progressBarProyecto.setProgress(progresoInt);
                holder.tvProgresoProyecto.setText(String.format(Locale.getDefault(), "%d%%", progresoInt));

                // (Opcional) Colorear la barra de progreso según el porcentaje
                // Asegúrate de tener estos colores definidos en colors.xml
                // if (progresoInt < 30) {
                //     holder.progressBarProyecto.setProgressTintList(ColorStateList.valueOf(ContextCompat.getColor(context, R.color.colorProgressRed)));
                // } else if (progresoInt < 70) {
                //     holder.progressBarProyecto.setProgressTintList(ColorStateList.valueOf(ContextCompat.getColor(context, R.color.colorProgressYellow)));
                // } else {
                //     holder.progressBarProyecto.setProgressTintList(ColorStateList.valueOf(ContextCompat.getColor(context, R.color.colorProgressGreen)));
                // }

            } else {
                holder.progressBarProyecto.setProgress(0);
                holder.tvProgresoProyecto.setText("0%");
            }
        } else {
            Log.e(TAG, "onBindViewHolder - actividadesDAO es null! No se puede calcular el progreso.");
            holder.progressBarProyecto.setProgress(0);
            holder.tvProgresoProyecto.setText("N/A"); // O algún indicador de error
        }
        // --- FIN CÁLCULO DEL PROGRESO REAL ---

        holder.itemView.setOnClickListener(v -> {
            if (clickListener != null) {
                clickListener.onProyectoClick(proyecto);
            }
        });

        holder.btnMenuProyecto.setOnClickListener(v -> {
            if (optionsListener != null) {
                PopupMenu popup = new PopupMenu(context, holder.btnMenuProyecto);
                MenuInflater inflater = popup.getMenuInflater();
                inflater.inflate(R.menu.menu_item_options, popup.getMenu());
                popup.setOnMenuItemClickListener(item -> {
                    int itemId = item.getItemId();
                    if (itemId == R.id.action_ver_actividades_item) {
                        optionsListener.onViewActividades(proyecto);
                        return true;
                    } else if (itemId == R.id.action_editar_item) {
                        optionsListener.onEditProyecto(proyecto);
                        return true;
                    } else if (itemId == R.id.action_eliminar_item) {
                        optionsListener.onDeleteProyecto(proyecto);
                        return true;
                    }
                    return false;
                });
                popup.show();
            }
        });
    }

    @Override
    public int getItemCount() {
        int count = (listaProyectos != null ? listaProyectos.size() : 0);
        Log.d(TAG, "getItemCount - Devolviendo: " + count);
        return count;
    }

    public void actualizarProyectos(List<Proyecto> nuevosProyectos) {
        Log.d(TAG, "actualizarProyectos - Recibida nueva lista. Tamaño: " + (nuevosProyectos != null ? nuevosProyectos.size() : "null"));
        this.listaProyectos.clear();
        if (nuevosProyectos != null) {
            this.listaProyectos.addAll(nuevosProyectos);
        }
        Log.d(TAG, "actualizarProyectos - Lista interna actualizada. Nuevo tamaño: " + this.listaProyectos.size());
        notifyDataSetChanged();
        Log.d(TAG, "actualizarProyectos - notifyDataSetChanged() llamado.");
    }

    static class ProyectoViewHolder extends RecyclerView.ViewHolder {
        TextView tvNombreProyecto, tvDescripcionProyecto, tvFechasProyecto, tvProgresoProyecto;
        ProgressBar progressBarProyecto;
        ImageButton btnMenuProyecto;

        public ProyectoViewHolder(@NonNull View itemView) {
            super(itemView);
            tvNombreProyecto = itemView.findViewById(R.id.textViewNombreProyectoItem);
            tvDescripcionProyecto = itemView.findViewById(R.id.textViewDescripcionProyectoItem);
            tvFechasProyecto = itemView.findViewById(R.id.textViewFechasProyectoItem);
            progressBarProyecto = itemView.findViewById(R.id.progressBarProyectoItem);
            tvProgresoProyecto = itemView.findViewById(R.id.textViewProgresoProyectoItem);
            btnMenuProyecto = itemView.findViewById(R.id.imageButtonMenuProyecto);

            // Verifica si los findViewById devuelven null
            if (tvNombreProyecto == null) Log.e(TAG, "ViewHolder - tvNombreProyecto es NULL!");
            if (progressBarProyecto == null) Log.e(TAG, "ViewHolder - progressBarProyecto es NULL!");
            if (tvProgresoProyecto == null) Log.e(TAG, "ViewHolder - tvProgresoProyecto es NULL!");
        }
    }
}