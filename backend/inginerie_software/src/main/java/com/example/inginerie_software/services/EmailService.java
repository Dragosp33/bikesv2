package com.example.inginerie_software.services;


import com.sendgrid.Method;
import com.sendgrid.Request;
import com.sendgrid.Response;
import com.sendgrid.SendGrid;
import com.sendgrid.helpers.mail.Mail;
import com.sendgrid.helpers.mail.objects.Email;
import com.sendgrid.helpers.mail.objects.Personalization;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.IOException;

@Service
public class EmailService {



        private final SendGrid sendGrid;
        private final String fromEmail;

        public EmailService(
                // get the SendGrid bean automatically created by Spring Boot
                @Autowired SendGrid sendGrid,
                // read your email to use as sender from application.properties
                //@Value("${twilio.sendgrid.from-email}") String fromEmail
                @Value("dragos.polifronie@s.unibuc.ro") String fromEmail
        ) {
            this.sendGrid = sendGrid;

            this.fromEmail = fromEmail;
        }

    public void sendTemplateEmail(String to, Long item_price, String expiryTime,
                                  String item_name, String  item_image, Long total_amount) throws IOException {
        Email from = new Email(fromEmail);
        Email toEmail = new Email(to);

        Mail mail = new Mail();
        mail.setFrom(from);

        Personalization personalization = new Personalization();
        personalization.addTo(toEmail);

        // Set dynamic values
        personalization.addDynamicTemplateData("item_price", item_price);
        personalization.addDynamicTemplateData("expiry_time", expiryTime);
        personalization.addDynamicTemplateData("item_image", item_image);
        personalization.addDynamicTemplateData("item_name", item_name);
        personalization.addDynamicTemplateData("total_price", total_amount);

        mail.addPersonalization(personalization);
        mail.setTemplateId("d-dcd008b0acd44e869162244ba5cad667");

        Request request = new Request();
        request.setMethod(Method.POST);
        request.setEndpoint("mail/send");
        request.setBody(mail.build());



        Response response = sendGrid.api(request);

        System.out.println("====EMAIL RESPONSE========");


        System.out.println(this.sendGrid);


        System.out.println(this.fromEmail);

        System.out.println(response.getStatusCode());
        System.out.println(response.getBody());

        System.out.println("req body: ->>> " + request.getBody());
    }

    public void sendResetPasswordEmail(String to, String link) throws IOException {
        Email from = new Email(fromEmail);
        Email toEmail = new Email(to);

        Mail mail = new Mail();
        mail.setFrom(from);

        Personalization personalization = new Personalization();
        personalization.addTo(toEmail);

        // Set dynamic values
        personalization.addDynamicTemplateData("link", link);


        mail.addPersonalization(personalization);
        mail.setTemplateId("d-449bdf40940142fea2acc184099d7ff0");

        Request request = new Request();
        request.setMethod(Method.POST);
        request.setEndpoint("mail/send");
        request.setBody(mail.build());



        Response response = sendGrid.api(request);

        System.out.println("====EMAIL RESPONSE========");


        System.out.println(this.sendGrid);


        System.out.println(this.fromEmail);

        System.out.println(response.getStatusCode());
        System.out.println(response.getBody());

        System.out.println("req body: ->>> " + request.getBody());
    }


    public void sendChangeEmail(String to, String link, String new_mail) throws IOException {
        Email from = new Email(fromEmail);
        Email toEmail = new Email(to);

        Mail mail = new Mail();
        mail.setFrom(from);

        Personalization personalization = new Personalization();
        personalization.addTo(toEmail);

        // Set dynamic values
        personalization.addDynamicTemplateData("link", link);
        personalization.addDynamicTemplateData("new_mail", new_mail);

        mail.addPersonalization(personalization);
        mail.setTemplateId("d-f289a20f6de9465da812dad20f49e702");

        Request request = new Request();
        request.setMethod(Method.POST);
        request.setEndpoint("mail/send");
        request.setBody(mail.build());



        Response response = sendGrid.api(request);

        System.out.println("====EMAIL RESPONSE========");


        System.out.println(this.sendGrid);


        System.out.println(this.fromEmail);

        System.out.println(response.getStatusCode());
        System.out.println(response.getBody());

        System.out.println("req body: ->>> " + request.getBody());
    }
}