package com.example.hl7project.service;

import com.example.hl7project.configuration.TwilioConfig;
import com.example.hl7project.dto.NoShowReportDTO;
import com.example.hl7project.model.Appointment;
import com.example.hl7project.model.AppointmentDetails;
import com.example.hl7project.model.MessageEntity;
import com.example.hl7project.repository.AppointmentRepository;
import com.example.hl7project.repository.MessageEntityRepo;
import com.example.hl7project.response.MessageResponse;
import com.twilio.rest.api.v2010.account.Message;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.sql.Timestamp;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.*;

@Service
public class AppointmentService {

//    @Autowired
//    private TwillioService twillioService;

    @Autowired
    private TwilioConfig twilioConfig;

    @Autowired
    private MessageEntityRepo messageEntityRepo;

    @Autowired
    private AppointmentRepository appointmentRepository;

    int creationCount = 0;
    int modifyCount = 0;
    int noShowCount = 0;

//    public ResponseEntity<String> receiveHl7Message(@RequestBody String hl7Message) {
//        String appointmentId = "";
//        String patientName = "";
//        String location = "";
//        String startTime = "";
//        String phNumber = "";
//        String countryCode = "";
//        String toNumber = "";
//        System.out.println("Received HL7 Message: " + hl7Message);
//        List<List<String>> parsedMessage = new ArrayList<>();
//        String[] lines = hl7Message.split("\r");
//
//        for (String line : lines) {
//            String[] fields = line.split("\\|");
//            List<String> fieldList = new ArrayList<>();
//            for (String field : fields) {
//                fieldList.add(field);
//            }
//            parsedMessage.add(fieldList);
//        }
//
//        for (int i = 0; i < parsedMessage.size(); i++) {
//            List<String> segment = parsedMessage.get(i);
//            System.out.println("Segment " + i + ": " + segment);
//        }
//
//        if (parsedMessage.size() > 1 && parsedMessage.get(1).size() > 15) {
//            location = parsedMessage.get(1).get(15);
//        } else {
//            System.out.println("location fields are not as expected.");
//        }
//
//        if (parsedMessage.size() > 1 && parsedMessage.get(1).size() > 11) {
//            String[] startTimeComponents = parsedMessage.get(1).get(11).split("\\^");
//            if (startTimeComponents.length > 2) {
//                startTime = startTimeComponents[4];
//            } else {
//                System.out.println("Start time field does not have the expected format.");
//            }
//        } else {
//            System.out.println("Start time are not as expected.");
//        }
//
//        if (parsedMessage.size() > 2 && parsedMessage.get(2).size() > 5) {
//            String fullName = parsedMessage.get(2).get(5);
//            String[] nameParts = fullName.split("\\^");
//            if (nameParts.length >= 2) {
//                String familyName = nameParts[0];
//                String givenName = nameParts[1];
//                patientName = givenName + " " + familyName;
//            } else {
//                patientName = fullName;
//            }
//        } else {
//            System.out.println("fullName are not as expected.");
//        }
//
//        if (parsedMessage.size() > 2 && parsedMessage.get(2).size() > 5) {
//            countryCode = parsedMessage.get(2).get(12);
//            phNumber = parsedMessage.get(2).get(13);
//            toNumber = countryCode + phNumber;
//            System.out.println("toNumber" + toNumber);
//        } else {
//            System.out.println("countryCode are not as expected.");
//        }
//
//        if (parsedMessage.size() > 1 && parsedMessage.get(1).size() > 1) {
//            String appointmentIdField = parsedMessage.get(1).get(1);
//            String[] appointmentParts = appointmentIdField.split("\\^");
//            if (appointmentParts.length >= 2) {
//                appointmentId = appointmentParts[1];
//            }
//        } else {
//            System.out.println("appointmentIdField are not as expected.");
//        }
//
//
//        if (!messageEntityRepo.existsByAppointmentId(appointmentId)) {
//
//            String messageBody = "Dear " + patientName + ", your appointment has been scheduled for " +
//                    startTime + " at " + location + ". Appointment ID: " + appointmentId;
//            try {
//                Message message = twillioService.getTwilioService(messageBody, toNumber);
//
//                MessageEntity messageEntity = new MessageEntity();
//                messageEntity.setStartTime(startTime);
//                messageEntity.setAppointmentId(appointmentId);
//                messageEntity.setAppointmentId(appointmentId);
//                messageEntity.setPatientName(patientName);
//                messageEntity.setPhNumber(toNumber);
//                messageEntity.setMessageId(message.getSid());
//                messageEntity.setTextMessage(messageBody);
//                messageEntityRepo.save(messageEntity);
//
//                System.out.println("Message sent: " + message.getSid());
//                return new ResponseEntity<>("Message received and processed", HttpStatus.OK);
//            } catch (Exception e) {
//                e.printStackTrace();
//                return new ResponseEntity<>("Error processing message", HttpStatus.INTERNAL_SERVER_ERROR);
//            }
//        }
//
//        return new ResponseEntity<>("success", HttpStatus.OK);
//    }

    public ResponseEntity<String> receiveHl7Message(String hl7Message) {
        System.out.println("Received HL7 Message: " + hl7Message);

        // Parse the HL7 message
        List<List<String>> parsedMessage = parseHl7Message(hl7Message);

        // Determine the message type
        String messageType = getMessageType(parsedMessage);
        if (messageType == null) {
            return new ResponseEntity<>("Unsupported message type", HttpStatus.BAD_REQUEST);
        }

        // Extract common fields
        Long appointmentId = Long.valueOf(extractAppointmetId(parsedMessage));
        String patientName = extractPatientName(parsedMessage);
        String location = extractField(parsedMessage, 1, 15);
        String startTime = extractStartTime(parsedMessage);
        String toNumber = extractPhoneNumber(parsedMessage);
        // parsedMessage.get(9);
        // Create message body based on the message type
        String messageBody = createMessageBody(messageType, patientName, startTime, location, String.valueOf(appointmentId));

        // Check for existing appointment
        if (!messageEntityRepo.existsById(appointmentId)) {
            try {
                // Send the message
//                Message message = twillioService.getTwilioService(messageBody, toNumber);
//
//                // Save message entity
//                saveMessageEntity(startTime, appointmentId, patientName, toNumber, message, messageBody);
//
//                System.out.println("Message sent: " + message.getSid());
//                return new ResponseEntity<>("Message received and processed", HttpStatus.OK);
            } catch (Exception e) {
                e.printStackTrace();
                return new ResponseEntity<>("Error processing message", HttpStatus.INTERNAL_SERVER_ERROR);
            }
        }

        return new ResponseEntity<>("Success", HttpStatus.OK);
    }

    private List<List<String>> parseHl7Message(String hl7Message) {
        List<List<String>> parsedMessage = new ArrayList<>();
        String[] lines = hl7Message.split("\r");
        for (String line : lines) {
            String[] fields = line.split("\\|");
            List<String> fieldList = new ArrayList<>(Arrays.asList(fields));
            parsedMessage.add(fieldList);
        }
        return parsedMessage;
    }

    private String getMessageType(List<List<String>> parsedMessage) {
        if (parsedMessage.isEmpty() || parsedMessage.size() < 1) {
            return null;
        }
        // Assuming the message type is in the first segment
        return parsedMessage.get(0).get(0); // Adjust index if necessary
    }

    private String extractField(List<List<String>> parsedMessage, int segmentIndex, int fieldIndex) {
        if (parsedMessage.size() > segmentIndex && parsedMessage.get(segmentIndex).size() > fieldIndex) {
            return parsedMessage.get(segmentIndex).get(fieldIndex);
        }
        return "";
    }


    private String extractPatientName(List<List<String>> parsedMessage) {
        String fullName = extractField(parsedMessage, 2, 5);
        String[] nameParts = fullName.split("\\^");
        if (nameParts.length >= 2) {
            return nameParts[1] + " " + nameParts[0]; // Given name + Family name
        }
        return fullName;
    }

    private String extractAppointmetId(List<List<String>> parsedMessage) {
        String appointmentId = extractField(parsedMessage, 2, 1);
        String[] nameParts = appointmentId.split("\\^");
        if (nameParts.length >= 2) {
            return nameParts[1] + " " + nameParts[1]; // Given name + Family name
        }
        return appointmentId;
    }

    private String extractStartTime(List<List<String>> parsedMessage) {
        String[] startTimeComponents = extractField(parsedMessage, 1, 11).split("\\^");
        if (startTimeComponents.length > 2) {
            return startTimeComponents[4];
        }
        return "";
    }

    private String extractPhoneNumber(List<List<String>> parsedMessage) {
        String countryCode = extractField(parsedMessage, 2, 12);
        String phNumber = extractField(parsedMessage, 2, 13);
        return countryCode + phNumber;
    }

    private String extractMessageType(List<List<String>> parsedMessage) {
        // Assuming MSH is the first segment, and message type is at MSH-9
        return parsedMessage.get(0).get(8); // Adjust indexing if necessary
    }

    private String createMessageBody(String messageType, String patientName, String startTime, String location, String appointmentId) {
        switch (messageType) {
            case "SIU_S12":
                return String.format(twilioConfig.getAppCreation(),
                        patientName, startTime, location, appointmentId);
            case "SIU_S13":
                return String.format(twilioConfig.getAppModification(),
                        patientName, startTime, location, appointmentId);
            case "SIU_S14":
                return String.format(twilioConfig.getAppNoShow(),
                        patientName, startTime, location, appointmentId);
            default:
                return "Dear " + patientName + ", your appointment information has been updated. Appointment ID: " + appointmentId;
        }
    }

    private void saveMessageEntity(String startTime, String appointmentId, String messageType, String patientName, String toNumber, Message message, String messageBody) {
        MessageEntity messageEntity = new MessageEntity();
        messageEntity.getAppointment().setStartTime(startTime);
        messageEntity.getAppointment().setAppointmentId(Long.valueOf(appointmentId));
        messageEntity.setPatientName(patientName);
        messageEntity.setPhNumber(toNumber);
        messageEntity.setMessageId(message.getSid());
        messageEntity.setTextMessage(messageBody);
        messageEntity.setMessageType(messageType);
        messageEntityRepo.save(messageEntity);
    }

    @Transactional
    public List<MessageEntity> deleteMessage(Timestamp date) {
        List<MessageEntity> appointments = messageEntityRepo.deleteByCreatedAtBefore(date);
        return appointments;
    }

    @Transactional
    public List<MessageEntity> deleteMessagesOlderThanDays(int days) {
        LocalDate cutoffDate = LocalDate.now().minus(days, ChronoUnit.DAYS);
        messageEntityRepo.deleteByCreatedAtBefore(cutoffDate);
        System.out.println("Deleted messages older than " + days + " days (cutoff date: " + cutoffDate + ")");
        return null;
    }

//    @Scheduled(cron = "0 0 0 * * ?")
//    public void scheduledMessageCleanup() {
//        int days = 30;
//        deleteMessagesOlderThanDays(days);
//    }

    //    public List<MessageEntity> getMessageByDate(Date date) {
    public MessageResponse getMessagesInRange(String startDate, String endDate) {
        try {
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss");
            LocalDateTime startDateTime = LocalDateTime.parse(startDate, formatter);
            LocalDateTime endDateTime = LocalDateTime.parse(endDate, formatter);

            Timestamp startTimestamp = Timestamp.valueOf(startDateTime);
            Timestamp endTimestamp = Timestamp.valueOf(endDateTime);

            // Query the repository
            List<MessageEntity> messages = messageEntityRepo.findByCreatedAtBetween(startTimestamp, endTimestamp);
            long count = messages.size();

            return new MessageResponse(messages, count);
        } catch (Exception e) {
            e.printStackTrace();
            throw new IllegalArgumentException("Invalid date format. Please use yyyy-MM-dd'T'HH:mm:ss.");
        }
    }

    public ResponseEntity<Long> noOfMessage(String messageType) {
        //  List<MessageEntity> allMessages = messageEntityRepo.findAll();
        long count = messageEntityRepo.countByMessageType(messageType);
        return ResponseEntity.ok(count);
    }

    private void handleAppointmentCreation(String appointmentId, String messageType, String patientName, String toNumber, Message message, String messageBody, String startTime) throws Exception {
        if (!messageEntityRepo.existsById(Long.valueOf(appointmentId))) {
            // Send the message
//            Message message = twillioService.getTwilioService(messageBody, toNumber);

            // Save message entity
            saveMessageEntity(startTime, appointmentId, messageType, patientName, toNumber, message, messageBody);
//            System.out.println("Appointment created and message sent: " + message.getSid());
        } else {
            System.out.println("Appointment already exists.");
        }
    }

    private void handleAppointmentModification(AppointmentDetails appointmentDetails) {
        Optional<MessageEntity> optionalAppointment = messageEntityRepo.findById(appointmentDetails.getAppointmentId());
        if (!optionalAppointment.isPresent()) {
            throw new RuntimeException("Appointment not found");
        }

        MessageEntity appointment = optionalAppointment.get();
        updateAppointmentFields(appointment, appointmentDetails);
        messageEntityRepo.save(appointment);
    }

    private void updateAppointmentFields(MessageEntity appointment, AppointmentDetails details) {
        ;
        appointment.getAppointment().setStartTime(details.getStartDateTime());
        appointment.getAppointment().setLocation(details.getLocation());
    }

    private void handleAppointmentNoShow(AppointmentDetails appointmentDetails) throws Exception {
        long count = 0;
        MessageEntity existingAppointment = messageEntityRepo.findById(appointmentDetails.getAppointmentId())
                .orElseThrow(() -> new Exception("Appointment not found"));

        if (existingAppointment.getMessageType() == "SIU_S26") {
            Appointment appointment = new Appointment();
            appointment.setAppointmentId(existingAppointment.getId());
            appointment.setLocation(appointmentDetails.getLocation());
            appointment.setStartTime(appointmentDetails.getStartDateTime());
            appointment.setStatus("No_Show");
            System.out.println("no show occured");
        }
        Optional<Appointment> appointment = appointmentRepository.findByStatus("No_Show");
        if (appointment.isPresent()) {
            count = appointmentRepository.countAllByStatus("No_Show");
        }

        // Update the fields to reflect the no-show
//        existingAppointment.setStatus("No-Show");
//        existingAppointment.setNoShowDate(appointmentData.getNoShowDate());
//        // Add any other fields that need to be updated
//
//        // Save the updated appointment back to the database
//        appointmentRepository.save(existingAppointment);
//    }
//        System.out.println("No-show processed.");
//    }
    }

    public long getTotalAppointmentsCount() {
        return messageEntityRepo.countByMessageType("SIU_S12");  // Assuming SIU_S12 represents appointment creation
    }

    public HashMap<String, String> getAppointmentDetails() {
        List<Appointment> appointments=appointmentRepository.findAll();
        HashMap<String,String> hashMap=new HashMap<>();

        for(Appointment appointment:appointments){
            hashMap.put("location",appointment.getLocation());
            hashMap.put("start_Time",appointment.getStartTime());

        }
        return hashMap;
    }
        public NoShowReportDTO getNoShowReport() {
            List<Object[]> result = appointmentRepository.findNoShowAppointmentsWithPatientDetails();
            List<NoShowReportDTO.NoShowAppointmentDTO> noShowAppointments = new ArrayList<>();

            for (Object[] row : result) {
                Appointment appointment = (Appointment) row[0];
                MessageEntity messageEntity = (MessageEntity) row[1];

                NoShowReportDTO.NoShowAppointmentDTO dto = new NoShowReportDTO.NoShowAppointmentDTO(
                        appointment.getAppointmentId(),
                        appointment.getStartTime(),
                        appointment.getLocation(),
                        appointment.getStatus(),
                        messageEntity.getPatientName(),
                        messageEntity.getPhNumber()
                );

                noShowAppointments.add(dto);
            }

            long noShowCount = appointmentRepository.countNoShowAppointments();
            System.out.println("No-Show Appointments: " + result.size());
            System.out.println("No-Show Count: " + noShowCount);
            return new NoShowReportDTO(noShowAppointments, noShowCount);
        }
//        return appointmentRepository.countAllByStatus("No_Show");

    public String getAllPatient(String type) {
        List<MessageEntity> messageEntities = messageEntityRepo.findByMessageType(type);
        for (MessageEntity message : messageEntities) {
            return message.getPatientName();
        }

        return null;
    }
}