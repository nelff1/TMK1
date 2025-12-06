package com.example.tmk.interfaces;

import java.io.File;

public interface PDFGeneratorCallback {
    void onPDFGenerated(File pdfFile);
    void onPDFGenerationFailed(Exception e);
}