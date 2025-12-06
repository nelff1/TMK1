package com.example.tmk.activities;

import android.animation.ObjectAnimator;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.FileProvider;
import androidx.core.widget.NestedScrollView;
import com.example.tmk.R;
import com.example.tmk.models.Course;
import com.example.tmk.utils.AsyncPDFGenerator;
import com.example.tmk.utils.PDFGenerator;
import com.example.tmk.utils.PermissionHelper;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.card.MaterialCardView;

import java.io.File;
import java.io.InputStream;
import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;

public class ProfileActivity extends AppCompatActivity {

    private static final int PICK_IMAGE_REQUEST = 101;
    private static final int REQUEST_WRITE_STORAGE = 100;

    private LinearLayout trainingContainer, notificationsContainer, recommendedCoursesContainer;
    private MaterialCardView calculationContainer;
    private MaterialButton showPayslipButton, downloadButton, exportTrainingButton, templatesButton;
    private TextView welcomeText, companyText, departmentText, payslipTitle, employeeNameText;
    private NestedScrollView rootScroll;
    private ImageView avatarImage, notificationsIcon, recommendedIcon;
    private SharedPreferences prefs;

    private final String[][] earnings = {
            {"Оплата по окладу (по часам)", "160", "100 000 ₽", "100 000 ₽"},
            {"Персональная надбавка %", "-", "10 000 ₽", "10 000 ₽"},
            {"Районный коэффициент", "-", "5 000 ₽", "5 000 ₽"},
            {"Повышение квалификации", "-", "3 000 ₽", "3 000 ₽"}
    };
    private final String tax = "13 000 ₽";
    private final String currentPeriod = "Ноябрь 2025";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        prefs = getSharedPreferences("profile_prefs", MODE_PRIVATE);
        initViews();
        setupGreetings();
        setupAvatar();
        setupButtonListeners();
        setupDropdownIcons();
        addSampleData();
        checkAndRequestPermissions();
    }
    private void initViews() {
        rootScroll = findViewById(R.id.rootScroll);
        welcomeText = findViewById(R.id.welcomeText);
        companyText = findViewById(R.id.companyText);
        departmentText = findViewById(R.id.departmentText);
        payslipTitle = findViewById(R.id.payslipTitle);
        employeeNameText = findViewById(R.id.employeeNameText);

        trainingContainer = findViewById(R.id.trainingContainer);
        calculationContainer = findViewById(R.id.calculationContainer);

        avatarImage = findViewById(R.id.avatarImage);
        notificationsIcon = findViewById(R.id.notificationsIcon);
        recommendedIcon = findViewById(R.id.recommendedIcon);

        notificationsContainer = findViewById(R.id.notificationsContainer);
        recommendedCoursesContainer = findViewById(R.id.recommendedCoursesContainer);

        showPayslipButton = findViewById(R.id.showPayslipButton);
        downloadButton = findViewById(R.id.downloadButton);
        exportTrainingButton = findViewById(R.id.exportTrainingButton);
        templatesButton = findViewById(R.id.templatesButton);
    }

    private void setupGreetings() {
        welcomeText.setText("Добро пожаловать, Марина!");
        companyText.setText("ООО ТМК-ЦБУ");
        departmentText.setText("Отдел роботизации процессов");
        employeeNameText.setText("Толшина Марина Юрьевна");
    }
    private void setupAvatar() {
        loadAvatarFromPrefs();
        avatarImage.setOnClickListener(v -> openImagePicker());
    }

    private void loadAvatarFromPrefs() {
        String savedUri = prefs.getString("avatar_uri", null);
        if (savedUri != null) {
            Uri uri = Uri.parse(savedUri);
            try {
                getContentResolver().takePersistableUriPermission(uri, Intent.FLAG_GRANT_READ_URI_PERMISSION);
                try (InputStream inputStream = getContentResolver().openInputStream(uri)) {
                    if (inputStream != null) {
                        avatarImage.setImageDrawable(
                                android.graphics.drawable.Drawable.createFromStream(inputStream, uri.toString())
                        );
                    } else {
                        setDefaultAvatar();
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    setDefaultAvatar();
                }
            } catch (SecurityException ignored) {
                setDefaultAvatar();
            }
        } else {
            setDefaultAvatar();
        }
    }

    private void setDefaultAvatar() {
        avatarImage.setImageResource(R.drawable.ic_default_avatar);
    }

    private void openImagePicker() {
        Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT);
        intent.setType("image/*");
        intent.addCategory(Intent.CATEGORY_OPENABLE);
        intent.addFlags(Intent.FLAG_GRANT_PERSISTABLE_URI_PERMISSION | Intent.FLAG_GRANT_READ_URI_PERMISSION);
        startActivityForResult(intent, PICK_IMAGE_REQUEST);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data != null && data.getData() != null) {
            Uri imageUri = data.getData();
            saveAvatarUri(imageUri);
            loadAvatarFromUri(imageUri);
        }
    }

    private void saveAvatarUri(Uri imageUri) {
        try {
            getContentResolver().takePersistableUriPermission(imageUri, Intent.FLAG_GRANT_READ_URI_PERMISSION);
            prefs.edit().putString("avatar_uri", imageUri.toString()).apply();
        } catch (SecurityException e) {
            e.printStackTrace();
            showToast("Не удалось сохранить доступ к изображению");
        }
    }

    private void loadAvatarFromUri(Uri imageUri) {
        try (InputStream inputStream = getContentResolver().openInputStream(imageUri)) {
            if (inputStream != null) {
                avatarImage.setImageDrawable(
                        android.graphics.drawable.Drawable.createFromStream(inputStream, imageUri.toString())
                );
            } else {
                setDefaultAvatar();
            }
        } catch (Exception e) {
            e.printStackTrace();
            setDefaultAvatar();
        }
    }
    private void setupDropdownIcons() {
        notificationsIcon.setOnClickListener(v -> toggleDropdown(notificationsContainer, recommendedCoursesContainer));
        recommendedIcon.setOnClickListener(v -> toggleDropdown(recommendedCoursesContainer, notificationsContainer));
    }

    private void toggleDropdown(LinearLayout toShow, LinearLayout toHide) {
        if (toShow.getVisibility() == View.GONE) {
            if (toHide.getVisibility() == View.VISIBLE) {
                toHide.animate().alpha(0f).setDuration(200).withEndAction(() -> toHide.setVisibility(View.GONE)).start();
            }
            toShow.setVisibility(View.VISIBLE);
            toShow.setAlpha(0f);
            toShow.animate().alpha(1f).setDuration(200).start();
        } else {
            toShow.animate().alpha(0f).setDuration(200).withEndAction(() -> toShow.setVisibility(View.GONE)).start();
        }
    }
    private void addSampleData() {
        addSampleNotifications();
        addSampleTrainings();
        addRecommendedCourses();
    }

    private void addSampleNotifications() {
        List<String> notifications = new ArrayList<>();
        notifications.add("Новый расчетный лист доступен");
        notifications.add("Срок выполнения задания по курсу истекает завтра");
        notifications.add("Не забудьте заполнить отчет по проекту");

        for (String notification : notifications) {
            TextView tv = new TextView(this);
            tv.setText(notification);
            tv.setTextSize(14f);
            tv.setTextColor(getResources().getColor(R.color.black));
            tv.setGravity(Gravity.LEFT);
            tv.setPadding(dpToPx(8), dpToPx(8), dpToPx(8), dpToPx(8));
            notificationsContainer.addView(tv);
        }
        notificationsContainer.setVisibility(View.GONE);
    }

    private void addSampleTrainings() {
        List<Course> courses = new ArrayList<>();
        courses.add(new Course("Курс Android-разработки", 70, "01.11.2025", "01.12.2025", 15, 15, 5, 5));
        courses.add(new Course("Курс по безопасности данных", 40, "15.10.2025", "15.11.2025", 15, 15, 5, 5));
        courses.add(new Course("Тренинг по корпоративной культуре", 100, "01.09.2025", "30.09.2025", 15, 15, 5, 5));

        for (Course course : courses) {
            addTrainingItem(course, trainingContainer);
        }
    }

    private void addRecommendedCourses() {
        List<Course> recommended = new ArrayList<>();
        recommended.add(new Course("Курс Kotlin", 20, "01.12.2025", "31.12.2025", 15, 15, 5, 5));
        recommended.add(new Course("Тренинг по Agile", 0, "15.11.2025", "30.11.2025", 15, 15, 5, 5));

        for (Course course : recommended) {
            addTrainingItem(course, recommendedCoursesContainer);
        }
        recommendedCoursesContainer.setVisibility(View.GONE);
    }

    private void addTrainingItem(final Course course, LinearLayout container) {
        MaterialCardView card = new MaterialCardView(this);
        card.setCardElevation(6);
        card.setRadius(16);
        card.setUseCompatPadding(true);

        LinearLayout layout = new LinearLayout(this);
        layout.setOrientation(LinearLayout.VERTICAL);
        int pad = dpToPx(12);
        layout.setPadding(pad, pad, pad, pad);

        TextView tvTitle = new TextView(this);
        tvTitle.setText(course.getTitle());
        tvTitle.setTextSize(16f);

        ProgressBar progressBar = new ProgressBar(this, null, android.R.attr.progressBarStyleHorizontal);
        progressBar.setMax(100);
        progressBar.setProgress(0);

        ObjectAnimator animator = ObjectAnimator.ofInt(progressBar, "progress", 0, course.getProgress());
        animator.setDuration(800);
        animator.start();

        layout.addView(tvTitle);
        layout.addView(progressBar);
        card.addView(layout);

        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
        );
        int margin = dpToPx(8);
        params.setMargins(margin, margin, margin, margin);
        card.setLayoutParams(params);

        card.setOnClickListener(v -> showCourseDialog(course));
        container.addView(card);
    }
    private void showCourseDialog(Course course) {
        new AlertDialog.Builder(this)
                .setTitle(course.getTitle())
                .setMessage(
                        "Прогресс: " + course.getProgress() + "%\n" +
                                "Начало: " + course.getStartDate() + "\n" +
                                "Окончание: " + course.getEndDate()
                )
                .setPositiveButton("ОК", (dialog, which) -> dialog.dismiss())
                .show();
    }
    private void showPayslip() {
        calculationContainer.setVisibility(View.VISIBLE);

        LinearLayout table = findViewById(R.id.payslipTable);
        table.removeAllViews();
        table.setOrientation(LinearLayout.VERTICAL);

        int paddingVertical = dpToPx(4);
        int paddingHorizontal = dpToPx(8);

        LinearLayout headerRow = createTableRow(
                new String[]{"Начислено", "Часы", "Оплачено", "Сумма"},
                new int[]{2, 1, 1, 1},
                paddingHorizontal,
                paddingVertical
        );
        headerRow.setBackgroundColor(getResources().getColor(R.color.light_gray));
        table.addView(headerRow);

        for (String[] rowData : earnings) {
            LinearLayout row = createTableRow(rowData, new int[]{2, 1, 1, 1}, paddingHorizontal, paddingVertical);
            row.setBackgroundResource(R.drawable.table_row_border);
            table.addView(row);
        }

        TextView taxView = new TextView(this);
        taxView.setText("Удержано НДФЛ: " + tax);
        taxView.setTypeface(null, android.graphics.Typeface.BOLD);
        taxView.setPadding(paddingHorizontal, paddingVertical, paddingHorizontal, paddingVertical);
        table.addView(taxView);

        rootScroll.post(() -> rootScroll.scrollTo(0, calculationContainer.getTop()));
    }

    private LinearLayout createTableRow(String[] data, int[] weights, int paddingHorizontal, int paddingVertical) {
        LinearLayout row = new LinearLayout(this);
        row.setOrientation(LinearLayout.HORIZONTAL);
        row.setPadding(paddingHorizontal, paddingVertical, paddingHorizontal, paddingVertical);

        for (int i = 0; i < data.length; i++) {
            TextView textView = new TextView(this);
            textView.setText(data[i]);
            if (i == 0) {
                textView.setTypeface(null, android.graphics.Typeface.BOLD);
            }
            textView.setLayoutParams(new LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT, weights[i]));
            row.addView(textView);
        }

        return row;
    }

    private void setupButtonListeners() {
        showPayslipButton.setOnClickListener(v -> showPayslip());
        downloadButton.setOnClickListener(v -> generatePayslipPDF());
        exportTrainingButton.setOnClickListener(v -> generateCoursesPDF());
        templatesButton.setOnClickListener(v -> showTemplatesDialog());
    }

    private void generatePayslipPDF() {
        if (!PermissionHelper.hasStoragePermission(this)) {
            PermissionHelper.requestStoragePermission(this, REQUEST_WRITE_STORAGE);
            return;
        }

        WeakReference<Context> weakRef = new WeakReference<>(this);
        AsyncPDFGenerator.generatePayslipPDF(
                weakRef,
                earnings,
                tax,
                employeeNameText.getText().toString(),
                departmentText.getText().toString(),
                currentPeriod,
                new AsyncPDFGenerator.PDFGenerationListener() {
                    @Override
                    public void onPDFGenerated(File pdfFile) {
                        runOnUiThread(() -> {
                            showToast("PDF расчётного листа сохранён");
                            showFileSavedDialog(pdfFile, "Расчётный лист");
                        });
                    }

                    @Override
                    public void onPDFGenerationError(String error) {
                        runOnUiThread(() ->
                                showToast("Ошибка при создании PDF: " + error)
                        );
                    }
                }
        );
    }

    private void generateCoursesPDF() {
        if (!PermissionHelper.hasStoragePermission(this)) {
            PermissionHelper.requestStoragePermission(this, REQUEST_WRITE_STORAGE);
            return;
        }

        List<Course> courses = getCoursesList();
        if (courses.isEmpty()) {
            showToast("Нет данных по курсам для экспорта");
            return;
        }

        WeakReference<Context> weakRef = new WeakReference<>(this);
        AsyncPDFGenerator.generateCoursesPDF(
                weakRef,
                courses,
                "Статистика по курсам",
                employeeNameText.getText().toString(),
                new AsyncPDFGenerator.PDFGenerationListener() {
                    @Override
                    public void onPDFGenerated(File pdfFile) {
                        runOnUiThread(() -> {
                            showToast("PDF статистики по курсам сохранён");
                            showFileSavedDialog(pdfFile, "Статистика по курсам");
                        });
                    }

                    @Override
                    public void onPDFGenerationError(String error) {
                        runOnUiThread(() ->
                                showToast("Ошибка при создании PDF: " + error)
                        );
                    }
                }
        );
    }

    private void showTemplatesDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Шаблоны заявлений");
        String[] templates = {"Отпуск без сохранения зарплаты", "Учебный отпуск", "Командировка"};
        builder.setItems(templates, (dialog, which) -> generateTemplatePDF(templates[which]));
        builder.setNegativeButton("Отмена", (d, w) -> d.dismiss());
        builder.show();
    }

    private void generateTemplatePDF(String templateType) {
        if (!PermissionHelper.hasStoragePermission(this)) {
            PermissionHelper.requestStoragePermission(this, REQUEST_WRITE_STORAGE);
            return;
        }

        new Thread(() -> {
            try {
                PDFGenerator pdfGenerator = new PDFGenerator(this);
                File pdfFile = pdfGenerator.generateTemplatePDF(
                        templateType,
                        employeeNameText.getText().toString(),
                        departmentText.getText().toString(),
                        "Сотрудник"
                );

                runOnUiThread(() -> {
                    showToast("Шаблон '" + templateType + "' сохранён");
                    showFileSavedDialog(pdfFile, "Шаблон заявления");
                });
            } catch (Exception e) {
                e.printStackTrace();
                runOnUiThread(() -> showToast("Ошибка при создании шаблона: " + e.getMessage()));
            }
        }).start();
    }

    private List<Course> getCoursesList() {
        List<Course> courses = new ArrayList<>();
        courses.add(new Course("Курс Android", 70, "01.11.2025", "01.12.2025", 15, 15, 5, 5));
        courses.add(new Course("Kotlin", 100, "01.12.2025", "31.12.2025", 20, 20, 5, 5));
        courses.add(new Course("Agile", 40, "15.11.2025", "30.11.2025", 10, 4, 1, 4));
        return courses;
    }

    private void showFileSavedDialog(File file, String title) {
        new AlertDialog.Builder(this)
                .setTitle(title + " сохранён")
                .setMessage("Файл: " + file.getName())
                .setPositiveButton("Открыть", (dialog, which) -> openFile(file))
                .setNegativeButton("Закрыть", (dialog, which) -> dialog.dismiss())
                .show();
    }

    private void openFile(File file) {
        try {
            Uri fileUri = FileProvider.getUriForFile(
                    this,
                    getApplicationContext().getPackageName() + ".provider",
                    file
            );

            Intent intent = new Intent(Intent.ACTION_VIEW);
            intent.setDataAndType(fileUri, "application/pdf");
            intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
            intent.addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);

            startActivity(intent);
        } catch (Exception e) {
            e.printStackTrace();
            showToast("Не удалось открыть файл. Установите приложение для просмотра PDF.");
        }
    }

    private void showToast(String msg) {
        Toast.makeText(this, msg, Toast.LENGTH_SHORT).show();
    }

    private int dpToPx(int dp) {
        return (int) (dp * getResources().getDisplayMetrics().density);
    }

    private void checkAndRequestPermissions() {
        if (!PermissionHelper.hasStoragePermission(this)) {
            PermissionHelper.requestStoragePermission(this, REQUEST_WRITE_STORAGE);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == REQUEST_WRITE_STORAGE) {
            if (grantResults.length > 0 && grantResults[0] == android.content.pm.PackageManager.PERMISSION_GRANTED) {
                showToast("Разрешение на запись предоставлено");
            } else {
                showToast("Разрешение на запись не предоставлено. Некоторые функции могут быть недоступны.");
            }
        }
    }
}