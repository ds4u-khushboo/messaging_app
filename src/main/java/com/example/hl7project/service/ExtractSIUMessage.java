package com.example.hl7project.service;

import ca.uhn.hl7v2.HL7Exception;
import ca.uhn.hl7v2.model.Type;
import ca.uhn.hl7v2.model.v25.message.SIU_S12;
import ca.uhn.hl7v2.model.v25.segment.PID;
import ca.uhn.hl7v2.model.v25.segment.SCH;
import ca.uhn.hl7v2.parser.PipeParser;

public class ExtractSIUMessage {

    public static SIU_S12 parseSIUS12Message(String hl7Message) throws HL7Exception {
        PipeParser parser = new PipeParser();
        return (SIU_S12) parser.parse(hl7Message);
    }
    public static void inspectMessageStructure(SIU_S12 message) {
        try {
            // Print the entire message structure
            System.out.println("Parsed HL7 Message:");
            System.out.println("message.encode()"+message.encode());

            PID pid = message.getPATIENT().getPID();
            System.out.println("PID Segment: " + pid.encode());

            // Inspect SCH Segment
            SCH sch = message.getSCH();
            System.out.println("SCH Segment: " + sch.encode());

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    public static void extractPatientAndSchedulingData(SIU_S12 message) {
        try {
            PID pid = message.getPATIENT().getPID();
            SCH sch = message.getSCH();
            System.out.println("PID Segment: " + pid.encode());
            System.out.println("SCH Segment: " + sch.encode());
            String patientId = pid.getPatientIdentifierList(0).getIDNumber().getValue();
            String patientLastName = pid.getPatientName(0).getFamilyName().getSurname().getValue();
            String patientFirstName = pid.getPatientName(0).getGivenName().getValue();
            String appointmentId = sch.getPlacerAppointmentID().getEntityIdentifier().getValue();
            String startTime = sch.getAppointmentTimingQuantity(0).getStartDateTime().getTs1_Time().getValue();

            System.out.println("Patient ID: " + patientId);
            System.out.println("Patient Name: " + patientLastName + ", " + patientFirstName);
            System.out.println("Appointment ID: " + appointmentId);
            System.out.println("Start Time: " + startTime);
        } catch (HL7Exception e) {
            throw new RuntimeException(e);
        }
    }
    public static void printAllFields(SIU_S12 message) {
        try {
            PID pid = message.getPATIENT().getPID();
            SCH sch = message.getSCH();

            // Print all fields in the PID segment
            System.out.println("PID Segment:");
            for (int i = 2; i <= pid.numFields(); i++) {
                Type[] fieldReps =  pid.getField(i - 1);
                for (Type varies : fieldReps) {
                    System.out.println("Field " + i + ": " + varies.encode());
                }
            }

            // Print all fields in the SCH segment
            System.out.println("SCH Segment:");
            for (int i = 2; i <= sch.numFields(); i++) {
                Type[] fieldReps =  sch.getField(i - 1);
                for (Type varies : fieldReps) {
                    System.out.println("Field " + i + ": " + varies.encode());
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
        public static void main(String[] args) {
        String hl7Message = "MSH|^~/&|SendingApp|SendingFac|ReceivingApp|ReceivingFac|20110613061611||SIU^S12|123456789|P|2.5||||||\n" +
                "SCH|12345^12345|2196178^2196178|||12345|OFFICE^Office visit|reason for the appointment|OFFICE|60|m|^^60^20110617084500^20110617093000|||||9^DENT^ARTHUR||||9^DENT^COREY|||||Scheduled\n" +
                "PID|1||42||Doe^John||19781012|M|||123 Broadway St^^Anytown^IN^12345||(555)555-1234|||S||999999999|||||||||||||||||||||\n" +
                "PV1|1|O|||||1^Smith^Miranda^A^MD|2^Withers^Peter^D^MD||||||||||||||||||||||||||||||||||||||||||99158||\n" +
                "RGS|1|A\n" +
                "AIG|1|A|1^White, Charles|D^^\n" +
                "AIL|1|A|OFFICE^^^OFFICE|^Main Office||20110614084500|||45|m^Minutes||Scheduled\n" +
                "AIP|1|A|1^White^Charles^A^MD|D^White, Douglas||20110614084500|||45|m^Minutes||Scheduled";

        try {
            SIU_S12 message = parseSIUS12Message(hl7Message);
            printAllFields(message);
            inspectMessageStructure(message);
            extractPatientAndSchedulingData(message);
        } catch (HL7Exception e) {
            e.printStackTrace();
        }
    }
}
