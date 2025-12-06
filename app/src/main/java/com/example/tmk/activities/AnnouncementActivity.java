package com.example.tmk.activities;

import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import com.example.tmk.R;

public class AnnouncementActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.event_card);

        ImageView announcementImage = findViewById(R.id.eventImage);
        TextView announcementTitle = findViewById(R.id.eventTitle);
        TextView announcementDescription = findViewById(R.id.eventDescription);

        String title = getIntent().getStringExtra("title");
        int imageResId = getIntent().getIntExtra("imageResId", R.drawable.ic_achievement_1);

        announcementImage.setImageResource(imageResId);
        announcementTitle.setText(title != null ? title : "Идеи празднования новогодних праздников");
        announcementDescription.setText("Популярные идеи включают тематические вечеринки, тимбилдинг, квесты (офлайн или онлайн) и творческие мастер-классы, а также более нестандартные варианты, такие как научные шоу, гала-ужин или выезд в спа-отель");
    }
}