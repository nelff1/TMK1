package com.example.tmk.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.tmk.models.Achievement;
import com.example.tmk.adapters.AchievementsAdapter;
import com.example.tmk.R;

import java.util.ArrayList;
import java.util.List;

public class AchievementsFragment extends Fragment {

    private RecyclerView recyclerView;
    private AchievementsAdapter adapter;
    private List<Achievement> achievementsList;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_achievements, container, false);

        recyclerView = view.findViewById(R.id.achievementsRecyclerView);

        achievementsList = new ArrayList<>();
        achievementsList.add(new Achievement("Чайковский Яков Николаевич", "«Особенность ТМК - это люди. Всегда готовые сделать невозможное, не унывающие и имеющие внутреннюю уверенность в том, что все будет хорошо.»", R.drawable.chel1));
        achievementsList.add(new Achievement("Тюгаев Антон Валерьевич", "«ТМК – одна из сильнейших компаний в мире с уверенным устойчивым развитием, с которой хочется достигать совместных новых горизонтов!»", R.drawable.chel2));
        achievementsList.add(new Achievement("Насыбулин Илья Равкатович", "«ТМК - компания возможностей, позволяющая реализовывать, показывать и раскрывать себя в различных сферах деятельности!»", R.drawable.chel3));
        achievementsList.add(new Achievement("Кузнецов Александр Николаевич", "«В любой ситуации быть честным. Быть честным с людьми – значит говорить им правду. Быть честным с самим собой – значит заниматься в жизни тем, что тебе действительно нравится; любить свою работу, свое предприятие, свою компанию.»", R.drawable.chel4));

        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        adapter = new AchievementsAdapter(achievementsList);
        recyclerView.setAdapter(adapter);

        return view;
    }
}
