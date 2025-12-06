package com.example.tmk.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.tmk.models.Achievement;
import com.example.tmk.R;

import java.util.List;

public class AchievementsAdapter extends RecyclerView.Adapter<AchievementsAdapter.AchievementViewHolder> {

    private final List<Achievement> achievements;

    public AchievementsAdapter(List<Achievement> achievements) {
        this.achievements = achievements;
    }

    @NonNull
    @Override
    public AchievementViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_achievement, parent, false);
        return new AchievementViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull AchievementViewHolder holder, int position) {
        Achievement achievement = achievements.get(position);

        holder.title.setText(achievement.getTitle());
        holder.description.setText(achievement.getDescription());
        holder.icon.setImageResource(achievement.getImageResId());
    }

    @Override
    public int getItemCount() {
        return achievements.size();
    }

    static class AchievementViewHolder extends RecyclerView.ViewHolder {

        TextView title, description;
        ImageView icon;

        public AchievementViewHolder(@NonNull View itemView) {
            super(itemView);

            title = itemView.findViewById(R.id.achievementTitle);
            description = itemView.findViewById(R.id.achievementDescription);
            icon = itemView.findViewById(R.id.achievementIcon);
        }
    }
}
