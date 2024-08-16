package com.example.hl7project.model;

import ca.uhn.hl7v2.model.v25.datatype.ST;
import jakarta.persistence.*;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "appointment")

public class Appointment {

    @Id
    @Column(name = "appointment_id")
    private Long appointmentId;

    @Column(name = "start_time")
    private String startTime;

    @Column(name = "location")
    private String location;

    @Column(name = "status")
    private String status;
    @OneToMany(mappedBy = "appointment", cascade = CascadeType.ALL)
    private List<MessageEntity> messages = new ArrayList<>();

    public Long getAppointmentId() {
        return appointmentId;
    }

    public void setAppointmentId(Long appointmentId) {
        this.appointmentId = appointmentId;
    }

    public String getStartTime() {
        return startTime;
    }

    public void setStartTime(String startTime) {
        this.startTime = startTime;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public List<MessageEntity> getMessages() {
        return messages;
    }

    public void setMessages(List<MessageEntity> messages) {
        this.messages = messages;
    }
}
