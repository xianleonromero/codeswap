package com.naix.codeswap.adapters;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.naix.codeswap.R;
import com.naix.codeswap.models.Match;
import com.naix.codeswap.models.ProgrammingLanguage;

import java.util.List;
import java.util.stream.Collectors;

public class MatchAdapter extends RecyclerView.Adapter<MatchAdapter.MatchViewHolder> {

    private List<Match> matches;
    private final Context context;
    private final OnMatchClickListener listener;

    public interface OnMatchClickListener {
        void onRequestSessionClick(Match match);
    }

    public MatchAdapter(Context context, List<Match> matches, OnMatchClickListener listener) {
        this.context = context;
        this.matches = matches;
        this.listener = listener;
    }

    @NonNull
    @Override
    public MatchViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_match, parent, false);
        return new MatchViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MatchViewHolder holder, int position) {
        Match match = matches.get(position);

        // Mostrar el nombre del otro usuario
        holder.tvUsername.setText(match.getUser2().getUsername());

        // Mostrar el tipo de match
        String matchType = match.isPotentialMatch() ? "Match Potencial ⭐⭐" : "Match Normal ⭐";
        holder.tvMatchType.setText(matchType);

        // Mostrar lo que EL OTRO USUARIO busca y ofrece
        String otherWants = match.getUser2Wants().stream()
                .map(ProgrammingLanguage::getName)
                .collect(Collectors.joining(", "));

        String otherOffers = match.getUser2Offers().stream()
                .map(ProgrammingLanguage::getName)
                .collect(Collectors.joining(", "));

        // Construir el texto mostrando info del otro usuario
        StringBuilder skillsText = new StringBuilder();

        if (!otherWants.isEmpty()) {
            skillsText.append("Busca: ").append(otherWants);
        }

        if (!otherOffers.isEmpty()) {
            if (skillsText.length() > 0) {
                skillsText.append("\n");
            }
            skillsText.append("Ofrece: ").append(otherOffers);
        }

        if (skillsText.length() == 0) {
            skillsText.append("Sin información de habilidades");
        }

        holder.tvLanguages.setText(skillsText.toString());

        // Configurar listeners de botones
        holder.btnRequestSession.setOnClickListener(v -> listener.onRequestSessionClick(match));
    }

    @Override
    public int getItemCount() {
        return matches != null ? matches.size() : 0;
    }

    public void updateData(List<Match> newMatches) {
        this.matches = newMatches;
        notifyDataSetChanged();
    }

    static class MatchViewHolder extends RecyclerView.ViewHolder {
        TextView tvUsername, tvMatchType, tvLanguages;
        Button btnRequestSession;

        public MatchViewHolder(@NonNull View itemView) {
            super(itemView);
            tvUsername = itemView.findViewById(R.id.tvUsername);
            tvMatchType = itemView.findViewById(R.id.tvMatchType);
            tvLanguages = itemView.findViewById(R.id.tvLanguages);
            btnRequestSession = itemView.findViewById(R.id.btnRequestSession);
        }
    }
}