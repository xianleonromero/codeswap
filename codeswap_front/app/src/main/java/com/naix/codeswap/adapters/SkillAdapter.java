package com.naix.codeswap.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.naix.codeswap.R;
import com.naix.codeswap.models.ProgrammingLanguage;

import java.util.List;

public class SkillAdapter extends RecyclerView.Adapter<SkillAdapter.SkillViewHolder> {

    public interface OnSkillDeleteListener {
        void onSkillDelete(ProgrammingLanguage skill);
    }

    private List<ProgrammingLanguage> skills;
    private Context context;
    private OnSkillDeleteListener deleteListener;

    // Constructor sin listener (para mantener compatibilidad)
    public SkillAdapter(Context context, List<ProgrammingLanguage> skills) {
        this.context = context;
        this.skills = skills;
        this.deleteListener = null;
    }

    // Constructor con listener para eliminar
    public SkillAdapter(Context context, List<ProgrammingLanguage> skills, OnSkillDeleteListener deleteListener) {
        this.context = context;
        this.skills = skills;
        this.deleteListener = deleteListener;
    }

    @NonNull
    @Override
    public SkillViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_skill, parent, false);
        return new SkillViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull SkillViewHolder holder, int position) {
        ProgrammingLanguage skill = skills.get(position);
        holder.tvSkillName.setText(skill.getName());
        if (deleteListener != null) {
            holder.ivDeleteSkill.setVisibility(View.VISIBLE);
            holder.ivDeleteSkill.setOnClickListener(v -> deleteListener.onSkillDelete(skill));
        } else {
            holder.ivDeleteSkill.setVisibility(View.GONE);
        }
    }

    @Override
    public int getItemCount() {
        return skills != null ? skills.size() : 0;
    }

    public void updateData(List<ProgrammingLanguage> newSkills) {
        this.skills = newSkills;
        notifyDataSetChanged();
    }

    static class SkillViewHolder extends RecyclerView.ViewHolder {
        TextView tvSkillName;
        ImageView ivDeleteSkill;

        public SkillViewHolder(@NonNull View itemView) {
            super(itemView);
            tvSkillName = itemView.findViewById(R.id.tvSkillName);
            ivDeleteSkill = itemView.findViewById(R.id.ivDeleteSkill);
        }
    }
}