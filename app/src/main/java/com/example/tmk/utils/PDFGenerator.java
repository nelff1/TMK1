package com.example.tmk.utils;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Typeface;
import android.graphics.pdf.PdfDocument;
import android.os.Environment;
import android.util.Log;

import com.example.tmk.R;
import com.example.tmk.models.Course;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

public class PDFGenerator {

    private static final String TAG = "PDFGenerator";
    private final Context context;
    private Bitmap logoBitmap;

    public PDFGenerator(Context context) {
        this.context = context;
        loadLogo();
    }

    private void loadLogo() {
        try {
            logoBitmap = BitmapFactory.decodeResource(context.getResources(), R.drawable.logotmk);
            if (logoBitmap != null) {
                int targetWidth = 150;
                int targetHeight = (int) ((float) logoBitmap.getHeight() / logoBitmap.getWidth() * targetWidth);
                logoBitmap = Bitmap.createScaledBitmap(logoBitmap, targetWidth, targetHeight, true);
            }
        } catch (Exception e) {
            Log.e(TAG, "Ошибка загрузки логотипа", e);
        }
    }
    private void addLogoToPDF(Canvas canvas, int pageWidth) {
        if (logoBitmap != null) {
            try {
                int logoWidth = logoBitmap.getWidth();
                int logoHeight = logoBitmap.getHeight();
                int x = (pageWidth - logoWidth) / 2;
                int y = 30;

                canvas.drawBitmap(logoBitmap, x, y, null);
            } catch (Exception e) {
                Log.e(TAG, "Ошибка при добавлении логотипа", e);
            }
        }
    }

    public File generatePayslipPDF(String[][] earnings, String tax,
                                   String employeeName, String department,
                                   String period) throws IOException {

        PdfDocument pdf = new PdfDocument();
        int pageWidth = 595;
        int pageHeight = 842;

        PdfDocument.PageInfo pageInfo = new PdfDocument.PageInfo.Builder(
                pageWidth, pageHeight, 1).create();
        PdfDocument.Page page = pdf.startPage(pageInfo);
        Canvas canvas = page.getCanvas();

        addLogoToPDF(canvas, pageWidth);

        Paint titlePaint = new Paint();
        titlePaint.setTextSize(20);
        titlePaint.setTypeface(Typeface.create(Typeface.DEFAULT, Typeface.BOLD));
        titlePaint.setColor(Color.BLACK);

        Paint headerPaint = new Paint();
        headerPaint.setTextSize(14);
        headerPaint.setTypeface(Typeface.create(Typeface.DEFAULT, Typeface.BOLD));
        headerPaint.setColor(Color.BLACK);

        Paint normalPaint = new Paint();
        normalPaint.setTextSize(12);
        normalPaint.setColor(Color.BLACK);

        Paint linePaint = new Paint();
        linePaint.setColor(Color.GRAY);
        linePaint.setStrokeWidth(1f);

        Paint fillPaint = new Paint();
        fillPaint.setColor(Color.parseColor("#F5F5F5"));
        fillPaint.setStyle(Paint.Style.FILL);

        int margin = 40;
        int x = margin;
        int y = margin;

        if (logoBitmap != null) {
            y += logoBitmap.getHeight() + 20;
        }

        String title = "РАСЧЕТНЫЙ ЛИСТОК";
        canvas.drawText(title, pageWidth / 2 - titlePaint.measureText(title) / 2, y, titlePaint);
        y += 30;

        canvas.drawText("Период: " + period, x, y, normalPaint);
        y += 20;
        canvas.drawText("Сотрудник: " + employeeName, x, y, normalPaint);
        y += 20;
        canvas.drawText("Подразделение: " + department, x, y, normalPaint);
        y += 30;

        int col1Width = 250;
        int col2Width = 100;
        int col3Width = 100;
        int col4Width = 100;
        int tableWidth = col1Width + col2Width + col3Width + col4Width;
        int tableStartX = (pageWidth - tableWidth) / 2;

        canvas.drawRect(tableStartX, y, tableStartX + tableWidth, y + 30, fillPaint);
        canvas.drawText("Начислено", tableStartX + 10, y + 20, headerPaint);
        canvas.drawText("Часы", tableStartX + col1Width + 10, y + 20, headerPaint);
        canvas.drawText("Оплачено", tableStartX + col1Width + col2Width + 10, y + 20, headerPaint);
        canvas.drawText("Сумма", tableStartX + col1Width + col2Width + col3Width + 10, y + 20, headerPaint);

        y += 35;

        for (int i = 0; i < earnings.length; i++) {
            String[] row = earnings[i];

            if (i % 2 == 0) {
                canvas.drawRect(tableStartX, y - 15, tableStartX + tableWidth, y + 10, fillPaint);
            }

            canvas.drawText(row[0], tableStartX + 10, y, normalPaint); // Начислено
            canvas.drawText(row[1], tableStartX + col1Width + 10, y, normalPaint); // Часы
            canvas.drawText(row[2], tableStartX + col1Width + col2Width + 10, y, normalPaint); // Оплачено
            canvas.drawText(row[3], tableStartX + col1Width + col2Width + col3Width + 10, y, normalPaint); // Сумма

            canvas.drawLine(tableStartX, y + 5, tableStartX + tableWidth, y + 5, linePaint);

            y += 25;
        }

        y += 15;

        Paint totalPaint = new Paint();
        totalPaint.setTextSize(14);
        totalPaint.setTypeface(Typeface.create(Typeface.DEFAULT, Typeface.BOLD));
        totalPaint.setColor(Color.BLACK);

        float totalAmount = 0;
        try {
            for (String[] row : earnings) {
                String amountStr = row[3].replaceAll("[^\\d]", "");
                if (!amountStr.isEmpty()) {
                    totalAmount += Float.parseFloat(amountStr);
                }
            }
        } catch (NumberFormatException e) {
            Log.e(TAG, "Error parsing amount", e);
        }

        canvas.drawText("Итого начислено: " + String.format(Locale.getDefault(), "%.0f ₽", totalAmount),
                tableStartX, y, totalPaint);
        y += 20;

        canvas.drawText("Удержано НДФЛ: " + tax, tableStartX, y, totalPaint);
        y += 20;

        try {
            float taxAmount = Float.parseFloat(tax.replaceAll("[^\\d]", ""));
            canvas.drawText("К выплате: " + String.format(Locale.getDefault(), "%.0f ₽", totalAmount - taxAmount),
                    tableStartX, y, totalPaint);
        } catch (NumberFormatException e) {
            Log.e(TAG, "Error parsing tax", e);
        }

        y += 40;
        canvas.drawText("Дата формирования: " + getCurrentDate(), x, y, normalPaint);
        y += 20;
        canvas.drawText("Подпись руководителя: ___________________", x, y, normalPaint);

        pdf.finishPage(page);

        File pdfFile = savePDFToFile(pdf, "Payslip_" + employeeName.replace(" ", "_") + "_" + getTimestamp());
        pdf.close();

        return pdfFile;
    }

    public File generateCoursesPDF(List<Course> courses, String title,
                                   String employeeName) throws IOException {

        if (courses == null || courses.isEmpty()) {
            throw new IllegalArgumentException("Список курсов пуст");
        }

        PdfDocument pdf = new PdfDocument();
        int pageWidth = 595;
        int pageHeight = 842;

        PdfDocument.PageInfo pageInfo = new PdfDocument.PageInfo.Builder(
                pageWidth, pageHeight, 1).create();
        PdfDocument.Page page = pdf.startPage(pageInfo);
        Canvas canvas = page.getCanvas();

        addLogoToPDF(canvas, pageWidth);

        Paint titlePaint = new Paint();
        titlePaint.setTextSize(22);
        titlePaint.setTypeface(Typeface.create(Typeface.DEFAULT, Typeface.BOLD));
        titlePaint.setColor(Color.BLACK);

        Paint subtitlePaint = new Paint();
        subtitlePaint.setTextSize(16);
        subtitlePaint.setTypeface(Typeface.create(Typeface.DEFAULT, Typeface.BOLD));
        subtitlePaint.setColor(Color.DKGRAY);

        Paint headerPaint = new Paint();
        headerPaint.setTextSize(12);
        headerPaint.setTypeface(Typeface.create(Typeface.DEFAULT, Typeface.BOLD));
        headerPaint.setColor(Color.BLACK);

        Paint normalPaint = new Paint();
        normalPaint.setTextSize(11);
        normalPaint.setColor(Color.BLACK);

        Paint highlightPaint = new Paint();
        highlightPaint.setTextSize(11);
        highlightPaint.setColor(Color.parseColor("#3F51B5"));
        highlightPaint.setTypeface(Typeface.create(Typeface.DEFAULT, Typeface.BOLD));

        Paint fillPaint = new Paint();
        fillPaint.setColor(Color.parseColor("#F5F5F5"));
        fillPaint.setStyle(Paint.Style.FILL);

        int margin = 40;
        int x = margin;
        int y = margin;

        if (logoBitmap != null) {
            y += logoBitmap.getHeight() + 20;
        }

        canvas.drawText(title, pageWidth / 2 - titlePaint.measureText(title) / 2, y, titlePaint);
        y += 30;

        canvas.drawText("Сотрудник: " + employeeName, x, y, subtitlePaint);
        y += 25;
        canvas.drawText("Дата формирования: " + getCurrentDate(), x, y, normalPaint);
        y += 30;

        int totalCourses = courses.size();
        int completedCourses = 0;
        int totalProgress = 0;

        for (Course course : courses) {
            if (course.getProgress() >= 100) {
                completedCourses++;
            }
            totalProgress += course.getProgress();
        }

        float avgProgress = totalCourses > 0 ? (float) totalProgress / totalCourses : 0;

        canvas.drawText("Общая статистика:", x, y, headerPaint);
        y += 20;
        canvas.drawText("• Всего курсов: " + totalCourses, x + 20, y, normalPaint);
        y += 18;
        canvas.drawText("• Завершено: " + completedCourses + " (" +
                        String.format(Locale.getDefault(), "%.0f%%",
                                totalCourses > 0 ? (completedCourses * 100f) / totalCourses : 0) + ")",
                x + 20, y, normalPaint);
        y += 18;
        canvas.drawText("• Средний прогресс: " + String.format(Locale.getDefault(), "%.1f%%", avgProgress),
                x + 20, y, normalPaint);
        y += 30;

        int[] colWidths = {180, 80, 100, 120}; // Название, Прогресс, Часы, Задания
        int tableWidth = 0;
        for (int width : colWidths) tableWidth += width;
        int tableStartX = (pageWidth - tableWidth) / 2;

        canvas.drawRect(tableStartX, y, tableStartX + tableWidth, y + 25, fillPaint);

        canvas.drawText("Курс", tableStartX + 10, y + 18, headerPaint);
        canvas.drawText("Прогресс", tableStartX + colWidths[0] + 10, y + 18, headerPaint);
        canvas.drawText("Часы", tableStartX + colWidths[0] + colWidths[1] + 10, y + 18, headerPaint);
        canvas.drawText("Задания", tableStartX + colWidths[0] + colWidths[1] + colWidths[2] + 10, y + 18, headerPaint);

        y += 30;

        for (int i = 0; i < courses.size(); i++) {
            Course course = courses.get(i);

            if (i % 2 == 0) {
                canvas.drawRect(tableStartX, y - 12, tableStartX + tableWidth, y + 5, fillPaint);
            }

            String courseName = course.getTitle();
            if (courseName.length() > 30) {
                courseName = courseName.substring(0, 27) + "...";
            }
            canvas.drawText(courseName, tableStartX + 10, y, normalPaint);

            String progressText = course.getProgress() + "%";
            Paint progressPaint = normalPaint;
            if (course.getProgress() >= 80) {
                progressPaint = highlightPaint;
                progressPaint.setColor(Color.parseColor("#4CAF50")); // Green
            } else if (course.getProgress() >= 50) {
                progressPaint = highlightPaint;
                progressPaint.setColor(Color.parseColor("#FF9800")); // Orange
            } else {
                progressPaint.setColor(Color.parseColor("#F44336")); // Red
            }
            canvas.drawText(progressText, tableStartX + colWidths[0] + 10, y, progressPaint);

            String hoursText = course.getHoursSpent() + "/" + course.getTotalHours() + " ч";
            canvas.drawText(hoursText, tableStartX + colWidths[0] + colWidths[1] + 10, y, normalPaint);

            String assignmentsText = course.getCompletedAssignments() + "/" +
                    course.getTotalAssignments() + " зад.";
            canvas.drawText(assignmentsText,
                    tableStartX + colWidths[0] + colWidths[1] + colWidths[2] + 10, y, normalPaint);

            Paint linePaint = new Paint();
            linePaint.setColor(Color.LTGRAY);
            linePaint.setStrokeWidth(0.5f);
            canvas.drawLine(tableStartX, y + 7, tableStartX + tableWidth, y + 7, linePaint);

            y += 25;

            if (y > pageHeight - 100 && i < courses.size() - 1) {
                pdf.finishPage(page);
                page = pdf.startPage(pageInfo);
                canvas = page.getCanvas();
                y = margin;

                addLogoToPDF(canvas, pageWidth);
                if (logoBitmap != null) {
                    y += logoBitmap.getHeight() + 20;
                }

                canvas.drawText(title + " (продолжение)",
                        pageWidth / 2 - titlePaint.measureText(title + " (продолжение)") / 2, y, titlePaint);
                y += 40;

                canvas.drawRect(tableStartX, y, tableStartX + tableWidth, y + 25, fillPaint);
                canvas.drawText("Курс", tableStartX + 10, y + 18, headerPaint);
                canvas.drawText("Прогресс", tableStartX + colWidths[0] + 10, y + 18, headerPaint);
                canvas.drawText("Часы", tableStartX + colWidths[0] + colWidths[1] + 10, y + 18, headerPaint);
                canvas.drawText("Задания", tableStartX + colWidths[0] + colWidths[1] + colWidths[2] + 10, y + 18, headerPaint);
                y += 30;
            }
        }

        y += 30;

        canvas.drawText("Легенда прогресса:", x, y, headerPaint);
        y += 20;

        Paint legendPaint = new Paint();
        legendPaint.setTextSize(10);

        int legendX = x;
        int legendY = y;

        // Зелёный (≥80%)
        legendPaint.setColor(Color.parseColor("#4CAF50"));
        canvas.drawCircle(legendX + 5, legendY + 5, 5, legendPaint);
        legendPaint.setColor(Color.BLACK);
        canvas.drawText("≥80% - отлично", legendX + 15, legendY + 9, legendPaint);
        legendX += 120;

        // Оранжевый (50-79%)
        legendPaint.setColor(Color.parseColor("#FF9800"));
        canvas.drawCircle(legendX + 5, legendY + 5, 5, legendPaint);
        legendPaint.setColor(Color.BLACK);
        canvas.drawText("50-79% - нормально", legendX + 15, legendY + 9, legendPaint);
        legendX += 140;

        // Красный (<50%)
        legendPaint.setColor(Color.parseColor("#F44336"));
        canvas.drawCircle(legendX + 5, legendY + 5, 5, legendPaint);
        legendPaint.setColor(Color.BLACK);
        canvas.drawText("<50% - требуется внимание", legendX + 15, legendY + 9, legendPaint);

        pdf.finishPage(page);

        File pdfFile = savePDFToFile(pdf, "Courses_Report_" + getTimestamp());
        pdf.close();

        return pdfFile;
    }

    public File generateChartPDF(List<Course> courses, String title) throws IOException {

        PdfDocument pdf = new PdfDocument();
        int pageWidth = 595;
        int pageHeight = 842;

        PdfDocument.PageInfo pageInfo = new PdfDocument.PageInfo.Builder(
                pageWidth, pageHeight, 1).create();
        PdfDocument.Page page = pdf.startPage(pageInfo);
        Canvas canvas = page.getCanvas();

        addLogoToPDF(canvas, pageWidth);

        Paint titlePaint = new Paint();
        titlePaint.setTextSize(22);
        titlePaint.setTypeface(Typeface.create(Typeface.DEFAULT, Typeface.BOLD));
        titlePaint.setColor(Color.BLACK);

        Paint normalPaint = new Paint();
        normalPaint.setTextSize(12);
        normalPaint.setColor(Color.BLACK);

        int margin = 40;
        int x = margin;
        int y = margin;

        if (logoBitmap != null) {
            y += logoBitmap.getHeight() + 20;
        }

        canvas.drawText(title, pageWidth / 2 - titlePaint.measureText(title) / 2, y, titlePaint);
        y += 40;

        canvas.drawText("Диаграмма прогресса по курсам", x, y, normalPaint);
        y += 20;
        canvas.drawText("Дата: " + getCurrentDate(), x, y, normalPaint);
        y += 40;

        // Гистограмма
        int chartWidth = pageWidth - 2 * margin;
        int chartHeight = 400;
        int chartX = margin;
        int chartY = y;

        Paint borderPaint = new Paint();
        borderPaint.setColor(Color.GRAY);
        borderPaint.setStyle(Paint.Style.STROKE);
        borderPaint.setStrokeWidth(2);
        canvas.drawRect(chartX, chartY, chartX + chartWidth, chartY + chartHeight, borderPaint);

        if (courses != null && !courses.isEmpty()) {
            int barCount = Math.min(courses.size(), 10);
            int barWidth = (chartWidth - 40) / barCount;
            int maxValue = 100;

            Paint axisPaint = new Paint();
            axisPaint.setColor(Color.DKGRAY);
            axisPaint.setStrokeWidth(1);

            for (int i = 0; i <= 5; i++) {
                int value = i * 20;
                int yPos = chartY + chartHeight - (chartHeight * value / maxValue);

                axisPaint.setStyle(Paint.Style.STROKE);
                canvas.drawLine(chartX, yPos, chartX + chartWidth, yPos, axisPaint);

                axisPaint.setStyle(Paint.Style.FILL);
                canvas.drawText(value + "%", chartX - 25, yPos + 5, axisPaint);
            }

            for (int i = 0; i < barCount; i++) {
                Course course = courses.get(i);
                int progress = course.getProgress();
                int barHeight = (chartHeight * progress) / maxValue;
                int barX = chartX + 20 + i * barWidth;
                int barY = chartY + chartHeight - barHeight;

                Paint barPaint = new Paint();
                if (progress >= 80) {
                    barPaint.setColor(Color.parseColor("#4CAF50")); // Green
                } else if (progress >= 50) {
                    barPaint.setColor(Color.parseColor("#FF9800")); // Orange
                } else {
                    barPaint.setColor(Color.parseColor("#F44336")); // Red
                }
                barPaint.setStyle(Paint.Style.FILL);

                canvas.drawRect(barX, barY, barX + barWidth - 10, chartY + chartHeight, barPaint);

                String courseName = course.getTitle();
                if (courseName.length() > 10) {
                    courseName = courseName.substring(0, 7) + "...";
                }

                canvas.save();
                canvas.rotate(-45, barX + (barWidth - 10) / 2, chartY + chartHeight + 25);
                canvas.drawText(courseName,
                        barX + (barWidth - 10) / 2 - normalPaint.measureText(courseName) / 2,
                        chartY + chartHeight + 25,
                        normalPaint);
                canvas.restore();

                String progressText = progress + "%";
                canvas.drawText(progressText,
                        barX + (barWidth - 10) / 2 - normalPaint.measureText(progressText) / 2,
                        barY - 10,
                        normalPaint);
            }

            y += chartHeight + 80;

            canvas.drawText("Легенда:", x, y, normalPaint);
            y += 25;

            int legendX = x;
            String[] legends = {"<50% - требуется внимание", "50-79% - нормально", "≥80% - отлично"};
            int[] colors = {Color.parseColor("#F44336"), Color.parseColor("#FF9800"), Color.parseColor("#4CAF50")};

            for (int i = 0; i < legends.length; i++) {
                Paint legendColorPaint = new Paint();
                legendColorPaint.setColor(colors[i]);
                legendColorPaint.setStyle(Paint.Style.FILL);

                canvas.drawRect(legendX, y - 8, legendX + 15, y + 2, legendColorPaint);
                canvas.drawText(legends[i], legendX + 20, y, normalPaint);

                legendX += 180;
                if (i == 1) {
                    legendX = x;
                    y += 25;
                }
            }
        } else {
            canvas.drawText("Нет данных для построения графика",
                    pageWidth / 2 - normalPaint.measureText("Нет данных для построения графика") / 2,
                    chartY + chartHeight / 2,
                    normalPaint);
        }

        pdf.finishPage(page);

        File pdfFile = savePDFToFile(pdf, "Progress_Chart_" + getTimestamp());
        pdf.close();

        return pdfFile;
    }
    public File generateTemplatePDF(String templateType, String employeeName,
                                    String department, String position) throws IOException {

        PdfDocument pdf = new PdfDocument();
        int pageWidth = 595;
        int pageHeight = 842;

        PdfDocument.PageInfo pageInfo = new PdfDocument.PageInfo.Builder(
                pageWidth, pageHeight, 1).create();
        PdfDocument.Page page = pdf.startPage(pageInfo);
        Canvas canvas = page.getCanvas();

        addLogoToPDF(canvas, pageWidth);

        Paint titlePaint = new Paint();
        titlePaint.setTextSize(18);
        titlePaint.setTypeface(Typeface.create(Typeface.DEFAULT, Typeface.BOLD));
        titlePaint.setColor(Color.BLACK);
        titlePaint.setTextAlign(Paint.Align.CENTER);

        Paint headerPaint = new Paint();
        headerPaint.setTextSize(14);
        headerPaint.setTypeface(Typeface.create(Typeface.DEFAULT, Typeface.BOLD));
        headerPaint.setColor(Color.BLACK);

        Paint normalPaint = new Paint();
        normalPaint.setTextSize(12);
        normalPaint.setColor(Color.BLACK);

        Paint underlinePaint = new Paint();
        underlinePaint.setColor(Color.BLACK);
        underlinePaint.setStrokeWidth(1);

        int margin = 50;
        int x = margin;
        int y = margin;

        if (logoBitmap != null) {
            y += logoBitmap.getHeight() + 30;
        }

        if (logoBitmap == null) {
            canvas.drawText("ООО \"ТМК-ЦБУ\"", pageWidth / 2, y, titlePaint);
            y += 30;
            canvas.drawText("ИНН 1234567890, КПП 123456789", pageWidth / 2, y, normalPaint);
            y += 20;
            canvas.drawText("Адрес: г. Екатеринбург, ул. Розы Люксембург, стр. 41", pageWidth / 2, y, normalPaint);
            y += 40;
        }

        canvas.drawText("Генеральному директору", x, y, headerPaint);
        y += 20;
        canvas.drawText("ООО \"ТМК-ЦБУ\"", x, y, normalPaint);
        y += 40;

        canvas.drawText("от " + employeeName, x, y, normalPaint);
        y += 20;
        canvas.drawText("должность: " + position, x, y, normalPaint);
        y += 20;
        canvas.drawText("подразделение: " + department, x, y, normalPaint);
        y += 40;

        String statementTitle = "";
        String statementBody = "";

        switch (templateType.toLowerCase()) {
            case "отпуск без сохранения зарплаты":
                statementTitle = "ЗАЯВЛЕНИЕ";
                statementBody = "Прошу предоставить мне отпуск без сохранения заработной платы продолжительностью _______ календарных дней с \"____\" ___________ 20__ г. по \"____\" ___________ 20__ г.";
                break;
            case "учебный отпуск":
                statementTitle = "ЗАЯВЛЕНИЕ";
                statementBody = "Прошу предоставить мне учебный отпуск с сохранением средней заработной платы для сдачи сессии в ___________________________ продолжительностью _______ календарных дней с \"____\" ___________ 20__ г. по \"____\" ___________ 20__ г.";
                break;
            case "командировка":
                statementTitle = "СЛУЖЕБНАЯ ЗАПИСКА";
                statementBody = "Прошу направить меня в командировку в г. ___________________________ в период с \"____\" ___________ 20__ г. по \"____\" ___________ 20__ г. Цель командировки: ___________________________";
                break;
            default:
                statementTitle = "ЗАЯВЛЕНИЕ";
                statementBody = "Прошу _____________________________________________________________";
        }

        canvas.drawText(statementTitle, pageWidth / 2, y, titlePaint);
        y += 40;

        String[] lines = statementBody.split(" ");
        StringBuilder currentLine = new StringBuilder();
        int lineWidth = pageWidth - 2 * margin;

        for (String word : lines) {
            if (normalPaint.measureText(currentLine + " " + word) < lineWidth) {
                if (currentLine.length() > 0) {
                    currentLine.append(" ");
                }
                currentLine.append(word);
            } else {
                canvas.drawText(currentLine.toString(), x, y, normalPaint);
                y += 20;
                currentLine = new StringBuilder(word);
            }
        }

        if (currentLine.length() > 0) {
            canvas.drawText(currentLine.toString(), x, y, normalPaint);
            y += 40;
        }

        y += 40;
        canvas.drawText("Дата: \"____\" ___________ 20__ г.", x, y, normalPaint);
        y += 30;

        canvas.drawLine(x + 100, y, x + 300, y, underlinePaint);
        canvas.drawText("Подпись", x + 400, y + 5, normalPaint);

        pdf.finishPage(page);

        String fileName = "Template_" + templateType.replace(" ", "_") + "_" + getTimestamp();
        File pdfFile = savePDFToFile(pdf, fileName);
        pdf.close();

        return pdfFile;
    }

    private File savePDFToFile(PdfDocument pdf, String fileName) throws IOException {
        File documentsDir = Environment.getExternalStoragePublicDirectory(
                Environment.DIRECTORY_DOCUMENTS);
        File tmkDir = new File(documentsDir, "TMK_Documents");

        if (!tmkDir.exists()) {
            if (!tmkDir.mkdirs()) {
                throw new IOException("Не удалось создать директорию для сохранения файлов");
            }
        }

        File pdfFile = new File(tmkDir, fileName + ".pdf");

        try (FileOutputStream fos = new FileOutputStream(pdfFile)) {
            pdf.writeTo(fos);
            Log.i(TAG, "PDF сохранён: " + pdfFile.getAbsolutePath());
        }

        return pdfFile;
    }

    public void cleanupOldPDFs(int daysToKeep) {
        File documentsDir = Environment.getExternalStoragePublicDirectory(
                Environment.DIRECTORY_DOCUMENTS);
        File tmkDir = new File(documentsDir, "TMK_Documents");

        if (!tmkDir.exists() || !tmkDir.isDirectory()) {
            return;
        }

        File[] files = tmkDir.listFiles();
        if (files == null) return;

        long cutoffTime = System.currentTimeMillis() - (daysToKeep * 24L * 60 * 60 * 1000);

        for (File file : files) {
            if (file.isFile() && file.getName().endsWith(".pdf")) {
                if (file.lastModified() < cutoffTime) {
                    if (file.delete()) {
                        Log.i(TAG, "Удалён старый файл: " + file.getName());
                    }
                }
            }
        }
    }

    public List<File> getSavedPDFs() {
        List<File> pdfFiles = new ArrayList<>();
        File documentsDir = Environment.getExternalStoragePublicDirectory(
                Environment.DIRECTORY_DOCUMENTS);
        File tmkDir = new File(documentsDir, "TMK_Documents");

        if (tmkDir.exists() && tmkDir.isDirectory()) {
            File[] files = tmkDir.listFiles((dir, name) -> name.toLowerCase().endsWith(".pdf"));
            if (files != null) {
                for (File file : files) {
                    pdfFiles.add(file);
                }
            }
        }

        return pdfFiles;
    }

    private String getCurrentDate() {
        SimpleDateFormat sdf = new SimpleDateFormat("dd.MM.yyyy", Locale.getDefault());
        return sdf.format(Calendar.getInstance().getTime());
    }

    private String getTimestamp() {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault());
        return sdf.format(Calendar.getInstance().getTime());
    }

    private String formatCurrency(float amount) {
        return String.format(Locale.getDefault(), "%.0f ₽", amount);
    }
}