package com.example.hl7project.repository;

import com.example.hl7project.model.MessageEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import java.sql.Timestamp;
import java.time.LocalDate;
import java.util.List;

@Repository
public interface MessageEntityRepo extends JpaRepository<MessageEntity,Long> {

    public boolean existsById(Long appointmentId);

    public List<MessageEntity> findByPatientName(String patientName);

    public List<MessageEntity> findByPhNumber(String phNumber);

    public List<MessageEntity> findMessageEntityByAppointment_StartTime(String start);

    public List<MessageEntity> deleteByCreatedAtBefore(Timestamp date);

    public List<MessageEntity> findByMessageType(String type);

    List<MessageEntity> findByCreatedAtBetween(Timestamp startTimestamp, Timestamp endTimestamp);

    @Query("SELECT COUNT(me) FROM MessageEntity me WHERE me.messageType = :messageType")
    long countByMessageType(String messageType);

    void deleteByCreatedAtBefore(LocalDate cutoffDate);
}
