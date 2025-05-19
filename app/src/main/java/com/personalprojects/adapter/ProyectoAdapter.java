package com.personalprojects.adapter; // PAQUETE ACTUALIZADO

import android.content.Context;
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
import androidx.recyclerview.widget.RecyclerView;
import com.personalprojects.R; // PAQUETE ACTUALIZADO para R
import com.personalprojects.model.Proyecto; // PAQUETE ACTUALIZADO
import java.util.List;
import java.util.Locale;

public class ProyectoAdapter extends RecyclerView.Adapter<ProyectoAdapter.ProyectoViewHolder> {

    private List<Proyecto> listaProyectos;
    private Context context;
    private OnProyectoClickListener clickListener;
    private OnProyectoOptionsListener optionsListener;

    public interface OnProyectoClickListener {
        void onProyectoClick(Proyecto proyecto);
    }

    public interface OnProyectoOptionsListener {
        void onEditProyecto(Proyecto proyecto);
        void onDeleteProyecto(Proyecto proyecto);
        void onViewActividades(Proyecto proyecto);
    }

    public ProyectoAdapter(Context context, List<Proyecto> listaProyectos, OnProyectoClickListener clickListener, OnProyectoOptionsListener optionsListener) {
        this.context = context;
        this.listaProyectos = listaProyectos;
        this.clickListener = clickListener;
        this.optionsListener = optionsListener;
    }

    @NonNull
    @Override
    public ProyectoViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_proyecto, parent, false);
        return new ProyectoViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ProyectoViewHolder holder, int position) {
        Proyecto proyecto = listaProyectos.get(position);
        holder.tvNombreProyecto.setText(proyecto.getNombre());
        holder.tvDescripcionProyecto.setText(proyecto.getDescripcion());

        String fechas = String.format(Locale.getDefault(),"Inicio: %s - Fin: %s",
                proyecto.getFechaInicio() != null ? proyecto.getFechaInicio() : "N/A",
                proyecto.getFechaFin() != null ? proyecto.getFechaFin() : "N/A");
        holder.tvFechasProyecto.setText(fechas);

        // Lógica para calcular y mostrar el progreso (simplificado)
        // Necesitarás obtener el progreso real desde ActividadesDAO
        int progresoSimulado = (proyecto.getIdProyecto() % 5) * 20; // Simulación
        holder.progressBarProyecto.setProgress(progresoSimulado);
        holder.tvProgresoProyecto.setText(String.format(Locale.getDefault(), "%d%%", progresoSimulado));


        holder.itemView.setOnClickListener(v -> {
            if (clickListener != null) {
                clickListener.onProyectoClick(proyecto);
            }
        });

        holder.btnMenuProyecto.setOnClickListener(v -> {
            PopupMenu popup = new PopupMenu(context, holder.btnMenuProyecto);
            MenuInflater inflater = popup.getMenuInflater();
            inflater.inflate(R.menu.menu_item_options, popup.getMenu()); // Necesitas crear este menu
            popup.setOnMenuItemClickListener(item -> {
                int itemId = item.getItemId();
                if (itemId == R.id.action_ver_actividades_item) { // IDs del menu_item_options
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
        });
    }

    @Override
    public int getItemCount() {
        return listaProyectos.size();
    }

    public void actualizarProyectos(List<Proyecto> nuevosProyectos) {
        this.listaProyectos.clear();
        this.listaProyectos.addAll(nuevosProyectos);
        notifyDataSetChanged();
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
        }
    }
}