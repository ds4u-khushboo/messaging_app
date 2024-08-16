package com.example.hl7project.service;

import com.example.hl7project.model.AppointmentDetails;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class NewModifier {

//    private static TwillioService twillioService;


//    @Autowired
//    public NewModifier(TwillioService twillioService) {
//        this.twillioService = twillioService;
//    }
//    public static void main(String[] args) {
//        String hl7Message = "MSH|^~/&|SendingApp|SendingFac|ReceivingApp|ReceivingFac|20110613061611||SIU^S12|123456789|P|2.5||||||\n" +
//                "SCH|12345^12345|2196178^2196178|||12345|OFFICE^Office visit|reason for the appointment|OFFICE|60|m|^^60^20110617084500^20110617093000|||||9^DENT^ARTHUR||||9^DENT^COREY|||||Scheduled\n" +
//                "PID|1||42||Doe^John||19781012|M|||123 Broadway St^^Anytown^IN^12345||(555)555-1234|||S||999999999|||||||||||||||||||||\n" +
//                "PV1|1|O|||||1^Smith^Miranda^A^MD|2^Withers^Peter^D^MD||||||||||||||||||||||||||||||||||||||||||99158||\n" +
//                "RGS|1|A\n" +
//                "AIG|1|A|1^White, Charles|D^^\n" +
//                "AIL|1|A|OFFICE^^^OFFICE|^Main Office||20110614084500|||45|m^Minutes||Scheduled\n" +
//                "AIP|1|A|1^White^Charles^A^MD|D^White, Douglas||20110614084500|||45|m^Minutes||Scheduled";
//
//        List<List<String>> parsedHL7 = parseHL7Message(hl7Message);
//
//        for (List<String> line : parsedHL7) {
//            System.out.println(line);
//        }
//    }

    public static void parseHL7Message(String message) {
        List<List<String>> parsedMessage = new ArrayList<>();
        String[] lines = message.split("\n");

        for (String line : lines) {
            String[] fields = line.split("\\|");
            List<String> fieldList = new ArrayList<>();
            for (String field : fields) {
                fieldList.add(field);
            }
            parsedMessage.add(fieldList);

        }
        String AppointmentId = parsedMessage.get(1).get(1);
        String patientName = parsedMessage.get(2).get(5);
        String location = parsedMessage.get(1).get(15);
        String startTime = parsedMessage.get(1).get(11);

        System.out.println("AppointmentId::" + AppointmentId);
        System.out.println("patientName::" + patientName);
        System.out.println("location::" + location);
        System.out.println("startTime::" + startTime);

        AppointmentDetails appointmentDetails = new AppointmentDetails();
        appointmentDetails.setAppointmentId(Long.valueOf(AppointmentId));
        appointmentDetails.setPatientName(patientName);

        appointmentDetails.setLocation(location);

        appointmentDetails.setStartDateTime(startTime);

        String smsBody = "Dear " + patientName + ", your appointment has been scheduled for " +
                startTime + " at " + location + ". Appointment ID: " + AppointmentId;

//        twillioService.sendSMS("+918827040821", "+18336512692", smsBody);

    }

//    public static void main(String[] args) {
//        TwillioService mockTwillioService = new TwillioService(); // Replace with a mock or real instance
//        NewModifier modifier = new NewModifier(mockTwillioService);
//        String hl7Message = "MSH|^~\\&|SendingApp|SendingFacility|ReceivingApp|ReceivingFacility|20170101120000||SIU^S12|123456789|P|2.5||||||ASCII|||\n" +
//                "SCH|12345|||12345|Consultation|Reason for the appointment|Office|60|m|20170102130000|20170102130000||||Arthur||||Dentist^Corey^|||||Scheduled\n" +
//                "PID|1||123456^^^MRN||SmithJohn||19800101|M|||123 Main St^^New York^NY^10001|+918827040821|+918827040821|+918827040821||S||123456789||||\n" +
//                "PV1|1|O|||||Physician^Primary^^^Dr.^^|||||||||||||||||||12345||RGS|1|A\n" +
//                "AIG|1|A|1^White, Charles|D^^|56|12|1|20140303054512|20140303054512|20|15||\n" +
//                "AIL|1|A|OFFICE|Main Office||20170102130000|||45|m^Minutes||Scheduled\n" +
//                "AIP|1|A|1^White^Charles^A^MD^^^^|D^White, Douglas||20170102130000|||45|m^Minutes||Scheduled";
//
//        modifier.parseHL7Message(hl7Message);
//    }
}


