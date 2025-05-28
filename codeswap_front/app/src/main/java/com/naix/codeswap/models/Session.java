package com.naix.codeswap.models;
import java.text.SimpleDateFormat;
import java.util.Locale;
import java.util.Map;
import java.util.Date;

public class Session {
    public Session(){}

    public static final String STATUS_PENDING = "PENDING";
    public static final String STATUS_CONFIRMED = "CONFIRMED";
    public static final String STATUS_COMPLETED = "COMPLETED";
    public static final String STATUS_CANCELLED = "CANCELLED";

    private int id;
    private User teacher;
    private User student;
    private ProgrammingLanguage language;
    private String status;
    private Date dateTime;
    private int durationMinutes;
    private String googleCalendarEventId;

    // Getters y Setters
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public User getTeacher() {
        return teacher;
    }

    public void setTeacher(User teacher) {
        this.teacher = teacher;
    }

    public User getStudent() {
        return student;
    }

    public void setStudent(User student) {
        this.student = student;
    }

    public ProgrammingLanguage getLanguage() {
        return language;
    }

    public void setLanguage(ProgrammingLanguage language) {
        this.language = language;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public Date getDateTime() {
        return dateTime;
    }

    public void setDateTime(Date dateTime) {
        this.dateTime = dateTime;
    }

    public int getDurationMinutes() {
        return durationMinutes;
    }

    public void setDurationMinutes(int durationMinutes) {
        this.durationMinutes = durationMinutes;
    }

    public String getGoogleCalendarEventId() {
        return googleCalendarEventId;
    }

    public void setGoogleCalendarEventId(String googleCalendarEventId) {
        this.googleCalendarEventId = googleCalendarEventId;
    }

    // Métodos de ayuda
    public boolean isPending() {
        return STATUS_PENDING.equals(status);
    }

    public boolean isConfirmed() {
        return STATUS_CONFIRMED.equals(status);
    }

    public boolean isCompleted() {
        return STATUS_COMPLETED.equals(status);
    }

    public boolean isCancelled() {
        return STATUS_CANCELLED.equals(status);
    }




    // Método estático para crear Session desde Map (JSON)
    public static Session fromMap(Map<String, Object> data) {
        Session session = new Session();

        if (data.containsKey("id")) {
            session.setId(((Double) data.get("id")).intValue());
        }

        if (data.containsKey("status")) {
            session.setStatus((String) data.get("status"));
        }

        if (data.containsKey("duration_minutes")) {
            Object duration = data.get("duration_minutes");
            if (duration instanceof Double) {
                session.setDurationMinutes(((Double) duration).intValue());
            } else if (duration instanceof Integer) {
                session.setDurationMinutes((Integer) duration);
            }
        }

        if (data.containsKey("google_calendar_event_id")) {
            session.setGoogleCalendarEventId((String) data.get("google_calendar_event_id"));
        }

        // Parsear fecha
        if (data.containsKey("date_time")) {
            try {
                String dateStr = (String) data.get("date_time");
                // Parsear fecha ISO del backend
                SimpleDateFormat isoFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", Locale.getDefault());
                Date date = isoFormat.parse(dateStr.replace("Z", ""));
                session.setDateTime(date);
            } catch (Exception e) {
                e.printStackTrace();
                session.setDateTime(new Date());
            }
        }

        // Parsear teacher
        if (data.containsKey("teacher")) {
            Map<String, Object> teacherData = (Map<String, Object>) data.get("teacher");
            User teacher = new User();
            teacher.setId(((Double) teacherData.get("id")).intValue());
            teacher.setUsername((String) teacherData.get("username"));
            String firstName = (String) teacherData.get("first_name");
            String lastName = (String) teacherData.get("last_name");
            teacher.setFullName((firstName != null ? firstName : "") + " " + (lastName != null ? lastName : ""));
            session.setTeacher(teacher);
        }

        // Parsear student
        if (data.containsKey("student")) {
            Map<String, Object> studentData = (Map<String, Object>) data.get("student");
            User student = new User();
            student.setId(((Double) studentData.get("id")).intValue());
            student.setUsername((String) studentData.get("username"));
            String firstName = (String) studentData.get("first_name");
            String lastName = (String) studentData.get("last_name");
            student.setFullName((firstName != null ? firstName : "") + " " + (lastName != null ? lastName : ""));
            session.setStudent(student);
        }

        // Parsear language
        if (data.containsKey("language")) {
            Map<String, Object> langData = (Map<String, Object>) data.get("language");
            ProgrammingLanguage language = new ProgrammingLanguage();
            language.setId(((Double) langData.get("id")).intValue());
            language.setName((String) langData.get("name"));
            if (langData.containsKey("icon")) {
                language.setIcon((String) langData.get("icon"));
            }
            session.setLanguage(language);
        }

        return session;
    }
}