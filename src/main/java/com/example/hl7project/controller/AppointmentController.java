package com.example.hl7project.controller;

import com.example.hl7project.dto.NoShowReportDTO;
import com.example.hl7project.model.MessageEntity;
import com.example.hl7project.repository.MessageEntityRepo;
import com.example.hl7project.response.MessageResponse;
import com.example.hl7project.service.AppointmentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/hl7")
public class AppointmentController {

//    @Autowired
//    private Hl7Service hl7Service;
//
//    @Autowired
//    private HL7ParseService hl7parseService;


    @Autowired
    private AppointmentService appointmentService;

    @Autowired
    private MessageEntityRepo messageEntityRepo;

//    @RequestMapping("/sendSMS")
//    public ResponseEntity<String> sendSMS(@RequestParam String body, @RequestParam String number) {
//        return twillioService.getTwilioService(body, number);
//    }

    @PostMapping("/test")
    public void sendMessge(@RequestBody String hl7mesage) {
        appointmentService.receiveHl7Message(hl7mesage);
    }

    @RequestMapping("/listByName")
    public List<MessageEntity> getListByName(@RequestParam String patientName) {
        return messageEntityRepo.findByPatientName(patientName);
    }

    @RequestMapping("/listByPhNumber")

    public List<MessageEntity> getListByPhNumber(@RequestParam String phNumber) {
        List<MessageEntity> messages = messageEntityRepo.findByPhNumber("+"+phNumber);
        return messages;
    }

    @RequestMapping("/listByTimeRange")
    public List<MessageEntity> getMessagesSentInRange(String startTime) {
        return messageEntityRepo.findMessageEntityByAppointment_StartTime(startTime);
    }

    @DeleteMapping("/deleteByDate")
    public List<MessageEntity> getDeleteMessageByDate(@RequestParam("date") String dateString) {
//        if (dateString == null) {
//            return ResponseEntity.badRequest().body("Date parameter is required and cannot be null.");
//        }
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss");

        // Parse the string to LocalDateTime
        LocalDateTime localDateTime = LocalDateTime.parse(dateString, formatter);

        // Convert to Timestamp
        Timestamp timestamp = Timestamp.valueOf(localDateTime);

        // Delete the messages
        return appointmentService.deleteMessage(timestamp);
    }


    @DeleteMapping("/deleteByDays")
    public List<MessageEntity> getDeleteMessageByDate(@RequestParam int days) {
        return appointmentService.deleteMessagesOlderThanDays(days);
    }

    @RequestMapping("/getMessageByRange")
    public MessageResponse getMessageByTimRange(@RequestParam String startDate, @RequestParam String endDate) {
        return appointmentService.getMessagesInRange(startDate, endDate);
    }

    @GetMapping("/count")
    public ResponseEntity<Long> countMessagesByType(@RequestParam String type) {
        return appointmentService.noOfMessage(type);
    }

    @GetMapping("/noshow-rate")
    public ResponseEntity<Map<String, Object>> getNoShowRate() {
        try {
            long totalAppointments = appointmentService.getTotalAppointmentsCount();
            NoShowReportDTO noShowCount = appointmentService.getNoShowReport();
            HashMap<String,String> appointments=appointmentService.getAppointmentDetails();
            String messageEntities=appointmentService.getAllPatient("SIU_S26");
//            double noShowRate = (double) noShowCount / totalAppointments * 100;
            Map<String, Object> response = new HashMap<>();
            response.put("totalAppointments", totalAppointments);
            response.put("noShowCount", noShowCount);
//            response.put("noShowRate", noShowRate);
            response.put("appointment",appointments);
            response.put("Patient",messageEntities);

            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }
    @GetMapping("/no-shows")
    public NoShowReportDTO getNoShowReport() {
        return appointmentService.getNoShowReport();
    }
}

