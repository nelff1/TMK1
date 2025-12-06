package com.example.tmk.models;

import java.util.ArrayList;
import java.util.List;

public class Course {

    private String title;
    private String startDate;
    private String endDate;
    private int progress;
    private List<Integer> progressByDay;
    private int hoursSpent;
    private int totalHours;
    private int completedAssignments;
    private int totalAssignments;

    public Course(String title, int progress, String startDate, String endDate,
                  int totalHours, int hoursSpent, int completedAssignments, int totalAssignments) {
        this.title = title;
        this.progress = progress;
        this.startDate = startDate;
        this.endDate = endDate;
        this.totalHours = totalHours;
        this.hoursSpent = hoursSpent;
        this.completedAssignments = completedAssignments;
        this.totalAssignments = totalAssignments;
        this.progressByDay = new ArrayList<>();
    }

    public String getTitle() { return title; }
    public String getStartDate() { return startDate; }
    public String getEndDate() { return endDate; }
    public int getProgress() { return progress; }
    public List<Integer> getProgressByDay() { return progressByDay; }
    public int getHoursSpent() { return hoursSpent; }
    public int getTotalHours() { return totalHours; }
    public int getCompletedAssignments() { return completedAssignments; }
    public int getTotalAssignments() { return totalAssignments; }
}