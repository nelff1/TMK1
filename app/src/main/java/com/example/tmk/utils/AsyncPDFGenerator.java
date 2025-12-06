package com.example.tmk.utils;

import android.content.Context;
import android.os.AsyncTask;

import com.example.tmk.models.Course;

import java.io.File;
import java.lang.ref.WeakReference;
import java.util.List;

public class AsyncPDFGenerator {

    public interface PDFGenerationListener {
        void onPDFGenerated(File pdfFile);
        void onPDFGenerationError(String error);
    }

    public static void generatePayslipPDF(WeakReference<Context> contextRef,
                                          String[][] earnings, String tax,
                                          String employeeName, String department,
                                          String period, PDFGenerationListener listener) {

        new AsyncTask<Void, Void, File>() {
            private Exception exception;

            @Override
            protected File doInBackground(Void... voids) {
                try {
                    Context context = contextRef.get();
                    if (context == null) return null;

                    PDFGenerator generator = new PDFGenerator(context);
                    return generator.generatePayslipPDF(earnings, tax, employeeName, department, period);
                } catch (Exception e) {
                    exception = e;
                    return null;
                }
            }

            @Override
            protected void onPostExecute(File pdfFile) {
                if (pdfFile != null) {
                    listener.onPDFGenerated(pdfFile);
                } else if (exception != null) {
                    listener.onPDFGenerationError(exception.getMessage());
                }
            }
        }.execute();
    }

    public static void generateCoursesPDF(WeakReference<Context> contextRef,
                                          List<Course> courses, String title,
                                          String employeeName, PDFGenerationListener listener) {

        new AsyncTask<Void, Void, File>() {
            private Exception exception;

            @Override
            protected File doInBackground(Void... voids) {
                try {
                    Context context = contextRef.get();
                    if (context == null) return null;

                    PDFGenerator generator = new PDFGenerator(context);
                    return generator.generateCoursesPDF(courses, title, employeeName);
                } catch (Exception e) {
                    exception = e;
                    return null;
                }
            }

            @Override
            protected void onPostExecute(File pdfFile) {
                if (pdfFile != null) {
                    listener.onPDFGenerated(pdfFile);
                } else if (exception != null) {
                    listener.onPDFGenerationError(exception.getMessage());
                }
            }
        }.execute();
    }

    public static void generateChartPDF(WeakReference<Context> contextRef,
                                        List<Course> courses, String title,
                                        PDFGenerationListener listener) {

        new AsyncTask<Void, Void, File>() {
            private Exception exception;

            @Override
            protected File doInBackground(Void... voids) {
                try {
                    Context context = contextRef.get();
                    if (context == null) return null;

                    PDFGenerator generator = new PDFGenerator(context);
                    return generator.generateChartPDF(courses, title);
                } catch (Exception e) {
                    exception = e;
                    return null;
                }
            }

            @Override
            protected void onPostExecute(File pdfFile) {
                if (pdfFile != null) {
                    listener.onPDFGenerated(pdfFile);
                } else if (exception != null) {
                    listener.onPDFGenerationError(exception.getMessage());
                }
            }
        }.execute();
    }
}