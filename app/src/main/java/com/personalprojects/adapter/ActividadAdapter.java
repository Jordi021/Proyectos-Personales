package com.personalprojects.adapter; // PAQUETE ACTUALIZADO

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;
import com.google.android.material.chip.Chip;
import com.personalprojects.R; // PAQUETE ACTUALIZADO para R
import com.personalprojects.model.Actividad; // PAQUETE ACTUALIZADO
import java.util.List;
import java.util.Locale;

public class ActividadAdapter extends RecyclerView.Adapter<ActividadAdapter.ActividadViewHolder> {

    private List<Actividad> listaActividades;
    private Context context;
    private OnActividadClickListener clickListener;

    public interface OnActividadClickListener {
        void onActividadClick(Actividad actividad);
    }

    public ActividadAdapter(Context context, List<Actividad> listaActividades, OnActividadClickListener clickListener) {
        this.context = context;
        this.listaActividades = listaActividades;
        this.clickListener = clickListener;
    }

    @NonNull
    @Override
    public ActividadViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_actividad, parent, false);
        return new ActividadViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ActividadViewHolder holder, int position) {
        Actividad actividad = listaActividades.get(position);
        holder.tvNombreActividad.setText(actividad.getNombre());
        holder.tvDescripcionActividad.setText(actividad.getDescripcion());

        String fechas = String.format(Locale.getDefault(),"Inicio: %s - Fin: %s",
                actividad.getFechaInicio() != null ? actividad.getFechaInicio() : "N/A",
                actividad.getFechaFin() != null ? actividad.getFechaFin() : "N/A");
        holder.tvFechasActividad.setText(fechas);

        holder.chipEstadoActividad.setText(actividad.getEstado());
        // Cambiar color del chip según el estado
        switch (actividad.getEstado()) {
            case "Planificado":
                holder.chipEstadoActividad.setChipBackgroundColorResource(R.color.chip_planificado); // Define estos colores
                holder.chipEstadoActividad.setTextColor(ContextCompat.getColor(context, R.color.chip_text_planificado));
                break;
            case "En ejecución":
                holder.chipEstadoActividad.setChipBackgroundColorResource(R.color.chip_en_ejecucion);
                holder.chipEstadoActividad.setTextColor(ContextCompat.getColor(context, R.color.chip_text_en_ejecucion));
                break;
            case "Realizado":
                holder.chipEstadoActividad.setChipBackgroundColorResource(R.color.chip_realizado);
                holder.chipEstadoActividad.setTextColor(ContextCompat.getColor(context, R.color.chip_text_realizado));
                break;
            default:
                holder.chipEstadoActividad.setChipBackgroundColorResource(android.R.color.darker_gray);
                holder.chipEstadoActividad.setTextColor(Color.WHITE);
                break;
        }


        holder.itemView.setOnClickListener(v -> {
            if (clickListener != null) {
                clickListener.onActividadClick(actividad);
            }
        });
    }

    @Override
    public int getItemCount() {
        return listaActividades.size();
    }

    public void actualizarActividades(List<Actividad> nuevasActividades) {
        this.listaActividades.clear();
        this.listaActividades.addAll(nuevasActividades);
        notifyDataSetChanged();
    }


    static class ActividadViewHolder extends RecyclerView.ViewHolder {
        TextView tvNombreActividad, tvDescripcionActividad, tvFechasActividad;
        Chip chipEstadoActividad;

        public ActividadViewHolder(@NonNull View itemView) {
            super(itemView);
            tvNombreActividad = itemView.findViewById(R.id.textViewNombreActividadItem);
            tvDescripcionActividad = itemView.findViewById(R.id.textViewDescripcionActividadItem);
            tvFechasActividad = itemView.findViewById(R.id.textViewFechasActividadItem);
            chipEstadoActividad = itemView.findViewById(R.id.chipEstadoActividadItem);
        }
    }
}