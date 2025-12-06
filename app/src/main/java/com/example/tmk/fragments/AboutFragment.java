package com.example.tmk.fragments;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.tmk.R;
import com.google.android.material.snackbar.Snackbar;

public class AboutFragment extends Fragment {

    private static final String PHONE_NUMBER = "+8343050505";
    private Button contactUsButton;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_about, container, false);
        contactUsButton = view.findViewById(R.id.contact_us_button);

        contactUsButton.setOnClickListener(v -> {
            copyPhoneNumber();
            dialPhoneNumber();
        });

        return view;
    }
    private void copyPhoneNumber() {
        try {
            ClipboardManager clipboard = (ClipboardManager) requireContext()
                    .getSystemService(Context.CLIPBOARD_SERVICE);
            ClipData clip = ClipData.newPlainText("Phone Number", PHONE_NUMBER);
            if (clipboard != null) {
                clipboard.setPrimaryClip(clip);
                Toast.makeText(getContext(), "Номер скопирован!", Toast.LENGTH_SHORT).show();
                showSnackbar("Номер телефона скопирован: " + PHONE_NUMBER);
            }
        } catch (Exception e) {
            Toast.makeText(getContext(), "Ошибка копирования: " + e.getMessage(),
                    Toast.LENGTH_SHORT).show();
        }
    }
    private void dialPhoneNumber() {
        Intent intent = new Intent(Intent.ACTION_DIAL);
        intent.setData(Uri.parse("tel:" + PHONE_NUMBER));
        startActivity(intent);
    }
    private void showSnackbar(String message) {
        View rootView = requireView();
        Snackbar snackbar = Snackbar.make(rootView, message, Snackbar.LENGTH_LONG);
        snackbar.setAction("OK", v -> snackbar.dismiss());
        snackbar.show();
    }
}
