package com.naix.codeswap.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.naix.codeswap.R;
import com.naix.codeswap.models.ProgrammingLanguage;

import java.util.List;

public class SkillAdapter extends RecyclerView.Adapter<SkillAdapter.SkillViewHolder> {

    private List<ProgrammingLanguage> skills;
    private Context context;

    public SkillAdapter(Context context, List<ProgrammingLanguage> skills) {
        this.context = context;
        this.skills = skills;
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

        public SkillViewHolder(@NonNull View itemView) {
            super(itemView);
            tvSkillName = itemView.findViewById(R.id.tvSkillName);
        }
    }
}