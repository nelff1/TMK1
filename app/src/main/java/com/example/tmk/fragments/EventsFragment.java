package com.example.tmk.fragments;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CalendarView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.tmk.R;
import com.example.tmk.adapters.EventAdapter;
import com.example.tmk.models.Event;
import com.example.tmk.activities.AnnouncementActivity;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class EventsFragment extends Fragment {

    private RecyclerView eventsRecyclerView;
    private EventAdapter eventAdapter;
    private List<Event> eventsList;
    private TextView selectedDateText;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_events, container, false);

        selectedDateText = view.findViewById(R.id.selectedDateText);
        CalendarView calendarView = view.findViewById(R.id.calendarView);
        eventsRecyclerView = view.findViewById(R.id.eventsRecyclerView);
        Calendar calendar = Calendar.getInstance();
        String todayDate = formatDate(calendar.getTimeInMillis());
        selectedDateText.setText("Выбранная дата: " + todayDate);
        setupCalendarConstraints(calendarView);
        calendarView.setOnDateChangeListener((calView, year, month, dayOfMonth) -> {
            updateSelectedDate(year, month, dayOfMonth);
        });

        setupEventsRecyclerView();

        return view;
    }
    private void setupCalendarConstraints(CalendarView calendarView) {
        Calendar calendar = Calendar.getInstance();

        calendarView.setMinDate(calendar.getTimeInMillis());

        calendar.add(Calendar.MONTH, 6);
        calendarView.setMaxDate(calendar.getTimeInMillis());

        calendarView.setDate(System.currentTimeMillis(), false, true);
    }

    private void updateSelectedDate(int year, int month, int dayOfMonth) {
        String formattedDate = String.format(Locale.getDefault(),
                "%02d/%02d/%d", dayOfMonth, month + 1, year);

        selectedDateText.setText("Выбранная дата: " + formattedDate);
    }

    private void setupEventsRecyclerView() {
        eventsList = new ArrayList<>();

        eventsList.add(new Event(
                "Украшаем город новогодними арт-объектами - детский конкурс",
                R.drawable.ic_achievement_1,
                "25.12.2024"
        ));

        eventsList.add(new Event(
                "Конкурс хорового пения для взрослых и детей",
                R.drawable.ic_achievement_3,
                "15.01.2025"
        ));

        eventsList.add(new Event(
                "Корпоративные мероприятия на заводах",
                R.drawable.ic_achievement_2,
                "10.02.2025"
        ));

        eventsList.add(new Event(
                "Тренинг по лидерству для руководителей",
                R.drawable.ic_achievement_1,
                "05.03.2025"
        ));

        eventsList.add(new Event(
                "Выставка инновационных проектов ТМК",
                R.drawable.ic_achievement_3,
                "20.03.2025"
        ));

        eventsRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        eventsRecyclerView.setHasFixedSize(true);

        eventAdapter = new EventAdapter(eventsList, this::onEventClick);
        eventsRecyclerView.setAdapter(eventAdapter);
    }

    private void onEventClick(Event event) {
        Intent intent = new Intent(getActivity(), AnnouncementActivity.class);
        intent.putExtra("event_title", event.getTitle());
        intent.putExtra("event_image_res_id", event.getImageResId());
        if (event.getEventDate() != null) {
            intent.putExtra("event_date", event.getEventDate());
        }
        startActivity(intent);
    }
    private String formatDate(long timestamp) {
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
        return sdf.format(new Date(timestamp));
    }
}