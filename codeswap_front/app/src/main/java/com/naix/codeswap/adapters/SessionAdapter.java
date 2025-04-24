package com.naix.codeswap.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.naix.codeswap.R;
import com.naix.codeswap.models.Session;

import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Locale;

public class SessionAdapter extends RecyclerView.Adapter<SessionAdapter.SessionViewHolder> {

    private List<Session> sessions;
    private Context context;
    private OnSessionActionListener listener;
    private boolean isPastSessions;
    private SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault());

    public interface OnSessionActionListener {
        void onAcceptSession(Session session);
        void onRejectSession(Session session);
        void onRateSession(Session session);
    }

    public SessionAdapter(Context context, List<Session> sessions, boolean isPastSessions, OnSessionActionListener listener) {
        this.context = context;
        this.sessions = sessions;
        this.isPastSessions = isPastSessions;
        this.listener = listener;
    }

    @NonNull
    @Override
    public SessionViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_session, parent, false);
        return new SessionViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull SessionViewHolder holder, int position) {
        Session session = sessions.get(position);
        boolean isTeacher = true; // En una app real, esto dependería del usuario actual

        // Configurar información básica
        holder.tvLanguage.setText(session.getLanguage().getName());
        holder.tvDateTime.setText(dateFormat.format(session.getDateTime()));
        holder.tvDuration.setText(session.getDurationMinutes() + " minutos");

        // Configurar usuario (estudiante o profesor)
        if (isTeacher) {
            holder.tvRole.setText("Enseñarás a:");
            holder.tvOtherUser.setText(session.getStudent().getFullName());
        } else {
            holder.tvRole.setText("Aprenderás de:");
            holder.tvOtherUser.setText(session.getTeacher().getFullName());
        }

        // Configurar estado y botones
        String statusText;
        int statusColor;

        switch (session.getStatus()) {
            case Session.STATUS_PENDING:
                statusText = "Pendiente";
                statusColor = R.color.design_default_color_secondary;
                break;
            case Session.STATUS_CONFIRMED:
                statusText = "Confirmada";
                statusColor = R.color.design_default_color_primary;
                break;
            case Session.STATUS_COMPLETED:
                statusText = "Completada";
                statusColor = R.color.design_default_color_secondary_variant;
                break;
            case Session.STATUS_CANCELLED:
                statusText = "Cancelada";
                statusColor = android.R.color.holo_red_light;
                break;
            default:
                statusText = "Desconocido";
                statusColor = android.R.color.darker_gray;
        }

        holder.tvStatus.setText(statusText);
        holder.tvStatus.setTextColor(ContextCompat.getColor(context, statusColor));

        // Configurar visibilidad y texto de los botones según el estado
        if (isPastSessions) {
            // Para sesiones pasadas
            holder.btnAction1.setVisibility(session.isCompleted() ? View.VISIBLE : View.GONE);
            holder.btnAction2.setVisibility(View.GONE);

            if (session.isCompleted()) {
                holder.btnAction1.setText("Valorar");
                holder.btnAction1.setOnClickListener(v -> listener.onRateSession(session));
            }
        } else {
            // Para sesiones próximas
            if (session.isPending()) {
                holder.btnAction1.setVisibility(View.VISIBLE);
                holder.btnAction2.setVisibility(View.VISIBLE);

                holder.btnAction1.setText("Aceptar");
                holder.btnAction2.setText("Rechazar");

                holder.btnAction1.setOnClickListener(v -> listener.onAcceptSession(session));
                holder.btnAction2.setOnClickListener(v -> listener.onRejectSession(session));
            } else {
                holder.btnAction1.setVisibility(View.GONE);
                holder.btnAction2.setVisibility(View.GONE);
            }
        }
    }

    @Override
    public int getItemCount() {
        return sessions != null ? sessions.size() : 0;
    }

    public void updateData(List<Session> newSessions) {
        this.sessions = newSessions;
        notifyDataSetChanged();
    }

    static class SessionViewHolder extends RecyclerView.ViewHolder {
        TextView tvLanguage, tvDateTime, tvDuration, tvStatus, tvRole, tvOtherUser;
        Button btnAction1, btnAction2;

        public SessionViewHolder(@NonNull View itemView) {
            super(itemView);
            tvLanguage = itemView.findViewById(R.id.tvLanguage);
            tvDateTime = itemView.findViewById(R.id.tvDateTime);
            tvDuration = itemView.findViewById(R.id.tvDuration);
            tvStatus = itemView.findViewById(R.id.tvStatus);
            tvRole = itemView.findViewById(R.id.tvRole);
            tvOtherUser = itemView.findViewById(R.id.tvOtherUser);
            btnAction1 = itemView.findViewById(R.id.btnAction1);
            btnAction2 = itemView.findViewById(R.id.btnAction2);
        }
    }
}