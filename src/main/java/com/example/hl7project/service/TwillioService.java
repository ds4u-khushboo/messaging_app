//package com.example.hl7project.service;
//
//import com.example.hl7project.configuration.TwilioConfig;
//import com.twilio.Twilio;
//import com.twilio.rest.api.v2010.account.Message;
//import com.twilio.type.PhoneNumber;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.stereotype.Service;
//
//@Service
//public class TwillioService {
//
//  @Autowired
//  private TwilioConfig twilioConfig;
//
//    public Message getTwilioService(String body,String number) {
//        Twilio.init(twilioConfig.getAccountSid(), twilioConfig.getAuthToken());
//        //String smsBody = "Your appointment has been rescheduled to " + startDate.toString() + " at " + location + ". Reason: " + changeReason;
//
//       return Message.creator(new com.twilio.type.PhoneNumber("+"+number),
//                new PhoneNumber(twilioConfig.getFromNumber()), body).create();
//    }
//}
//