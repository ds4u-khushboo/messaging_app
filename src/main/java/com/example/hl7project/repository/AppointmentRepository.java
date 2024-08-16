package com.example.hl7project.repository;

import com.example.hl7project.model.Appointment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface AppointmentRepository extends JpaRepository<Appointment,Long> {

    public Optional<Appointment> findByStatus(String status);

    public long countAllByStatus(String status);

    @Query("SELECT a FROM Appointment a WHERE a.status = 'No-Show' ORDER BY a.startTime DESC")
    List<Appointment> findNoShowAppointmentsOrderByStartTimeDesc();

    @Query("SELECT a, m FROM Appointment a JOIN a.messages m WHERE a.status = 'No_Show' ORDER BY a.startTime DESC")
    List<Object[]> findNoShowAppointmentsWithPatientDetails();
    @Query("SELECT COUNT(a) FROM Appointment a WHERE a.status = 'No_Show'")
    long countNoShowAppointments();
}
