package com.naix.codeswap.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.naix.codeswap.R;
import com.naix.codeswap.models.SessionRequest;

import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Locale;

public class SessionRequestAdapter extends RecyclerView.Adapter<SessionRequestAdapter.RequestViewHolder> {

    private List<SessionRequest> requests;
    private Context context;
    private OnRequestActionListener listener;
    private SimpleDateFormat displayFormat = new SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault());

    public interface OnRequestActionListener {
        void onAcceptRequest(SessionRequest request);
        void onRejectRequest(SessionRequest request);
    }

    public SessionRequestAdapter(Context context, List<SessionRequest> requests, OnRequestActionListener listener) {
        this.context = context;
        this.requests = requests;
        this.listener = listener;
    }

    @NonNull
    @Override
    public RequestViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_session_request, parent, false);
        return new RequestViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull RequestViewHolder holder, int position) {
        SessionRequest request = requests.get(position);

        // Información básica
        holder.tvRequesterName.setText(request.getRequester().getFullName());
        holder.tvLanguage.setText(request.getLanguage().getName());
        holder.tvDuration.setText(request.getDurationMinutes() + " minutos");

        // Fecha y hora propuesta
        if (request.getProposedDateTime() != null) {
            holder.tvDateTime.setText(displayFormat.format(request.getProposedDateTime()));
        } else {
            holder.tvDateTime.setText("Fecha por confirmar");
        }

        // Mensaje (si existe)
        if (request.getMessage() != null && !request.getMessage().trim().isEmpty()) {
            holder.tvMessage.setText(request.getMessage());
            holder.tvMessage.setVisibility(View.VISIBLE);
        } else {
            holder.tvMessage.setVisibility(View.GONE);
        }

        // Solo mostrar botones si está pendiente
        if (request.isPending()) {
            holder.btnAccept.setVisibility(View.VISIBLE);
            holder.btnReject.setVisibility(View.VISIBLE);

            holder.btnAccept.setOnClickListener(v -> listener.onAcceptRequest(request));
            holder.btnReject.setOnClickListener(v -> listener.onRejectRequest(request));
        } else {
            holder.btnAccept.setVisibility(View.GONE);
            holder.btnReject.setVisibility(View.GONE);
        }
    }

    @Override
    public int getItemCount() {
        return requests != null ? requests.size() : 0;
    }

    static class RequestViewHolder extends RecyclerView.ViewHolder {
        TextView tvRequesterName, tvLanguage, tvDateTime, tvDuration, tvMessage;
        Button btnAccept, btnReject;

        public RequestViewHolder(@NonNull View itemView) {
            super(itemView);
            tvRequesterName = itemView.findViewById(R.id.tvRequesterName);
            tvLanguage = itemView.findViewById(R.id.tvLanguage);
            tvDateTime = itemView.findViewById(R.id.tvDateTime);
            tvDuration = itemView.findViewById(R.id.tvDuration);
            tvMessage = itemView.findViewById(R.id.tvMessage);
            btnAccept = itemView.findViewById(R.id.btnAccept);
            btnReject = itemView.findViewById(R.id.btnReject);
        }
    }
}