package com.example.inginerie_software.controllers;

import com.example.inginerie_software.models.CheckoutPayment;
import com.example.inginerie_software.models.bikes_type;
import com.example.inginerie_software.services.bikes_typeService;
import com.google.common.collect.ImmutableMap;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthException;
import com.google.firebase.auth.UserRecord;
import com.google.gson.Gson;
import com.stripe.exception.StripeException;
import com.stripe.model.Customer;
import com.stripe.model.Refund;
import com.stripe.param.CustomerCreateParams;
import com.stripe.param.RefundCreateParams;
import com.stripe.param.checkout.SessionCreateParams;
import com.stripe.param.checkout.SessionListParams;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import com.example.inginerie_software.models.bikes;

import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.google.gson.Gson;

import com.stripe.Stripe;
import com.stripe.exception.StripeException;
import com.stripe.model.checkout.Session;
import com.stripe.param.checkout.SessionCreateParams;

//secret = sk_test_51N6jyuCxe9tqFGX1Lu7XzQFiVKGtDUIJqBH47EWrQ9fttV5lN5YLaAijXZbGU5ILRgG4sHehwFM1ysxaRhCqxN8d00mmyj5Z2o

    @RestController
    public class StripeController {
        // create a Gson object
        public static Gson gson = new Gson();


        private final com.example.inginerie_software.services.bikesService bikesService;

        public StripeController(com.example.inginerie_software.services.bikesService bS) { this.bikesService = bS;}

        @PostMapping("/payment")
        /**
         * Payment with Stripe checkout page
         *
         * @throws StripeException
         */
        public String paymentWithCheckoutPage(@RequestBody CheckoutPayment payment) throws StripeException, FirebaseAuthException {
            // We initialize stripe object with the api key
            init();
            bikes b = this.bikesService.getBikeById(payment.getId_bike());
            long pret = b.getTip().getPrice() * 100;
            long q = 1;
            String name = b.getTip().getNume();
            System.out.println("payment sent: " + payment);
            System.out.println("userid sent: " + payment.getUserid());


            String cus_id = (String) FirebaseAuth.getInstance().getUser(payment.getUserid()).getCustomClaims().get("customerid");
            System.out.println(cus_id);

            Map<String, String> metadata = new HashMap<>();
            metadata.put("reservedbike", String.valueOf(payment.getId_bike()));
            metadata.put("user", payment.getUserid());

            // We create a  stripe session parameters
            SessionCreateParams params = SessionCreateParams.builder()
                    // We will use the credit card payment method
                    .addPaymentMethodType(SessionCreateParams.PaymentMethodType.CARD)
                    // .setExpiresAt(LocalDateTime.now().plusHours(1).atOffset(ZoneOffset.UTC).toInstant().toEpochMilli())
                    .setMode(SessionCreateParams.Mode.PAYMENT).setSuccessUrl(payment.getSuccessUrl())
                    .setCancelUrl(
                            payment.getCancelUrl())
              .setCustomer(cus_id)
                    .addLineItem(
                            SessionCreateParams.LineItem.builder().setQuantity(q)
                                    .setPriceData(
                                            SessionCreateParams.LineItem.PriceData.builder()
                                                    .setCurrency(payment.getCurrency()).setUnitAmount(pret)
                                                    .setProductData(SessionCreateParams.LineItem.PriceData.ProductData
                                                            .builder().setName(name).addImage(b.getTip().getImage_url())
                                                            .setDescription("this is the price per hour. you will be able stop the reservation at any time and receive a refund for the time remaining ")
                                                            .build())
                                                    .build())
                                    .build()).putAllMetadata(metadata)
                    .build();
            // create a stripe session
            System.out.println("payment: " + payment);
            Session session = Session.create(params);
            System.out.println("session is " + session);
            Map<String, String> responseData = new HashMap<>();
            // We get the sessionId and we put it inside the response data you can get more info from the session object
            responseData.put("id", session.getId());
            // We can return only the sessionId as a String
            return gson.toJson(responseData);
        }

        @PostMapping("/create-stripe-customer")
        public Map<String,String> createStripeCustomer(@RequestBody Map<String, String> requestBody) throws StripeException, FirebaseAuthException {
            init();
            String uid = requestBody.get("uid");
            String email = requestBody.get("email");

            // Use the Firebase UID to identify the user and create a Stripe customer
           // createStripeCustomer()
            CustomerCreateParams params =
                    CustomerCreateParams.builder()
                            //.setName("Jenny Rosen")
                            .setEmail(email)
                            //.set
                            .build();
            Customer customer = Customer.create(params);
            System.out.println("Customer before setid: " + customer);

            // setting an admin
           // if (email.equals("dragosp0201@gmail.com")) {

              //  FirebaseAuth.getInstance().setCustomUserClaims(uid, ImmutableMap.of("admin", true));
               Map<String, Object> claims = new HashMap<>();
               claims.put("customerid", customer.getId());
               FirebaseAuth.getInstance().setCustomUserClaims(uid, claims);
              // System.out.println("email verified");
           // }
            // Add your own logic here to further process the authenticated user
            Map<String, String> response = Map.of("uid", customer.getId(), "email", customer.getEmail());
            return response;
        }


        @GetMapping("/payment-details")
        public ResponseEntity<?> getPaymentDetails(@RequestHeader(name = "X-Session-ID") String sessionId) throws StripeException {
            init();

           try{ Session session = Session.retrieve(sessionId);
               System.out.println("session details " + session);
               System.out.println("-----------------------");
               // System.out.println("payment intent " + session.getPaymentIntentObject());
              /* refunds:
              RefundCreateParams params =
                       RefundCreateParams.builder().setPaymentIntent(session.getPaymentIntent()).build();
               Refund refund = Refund.create(params);
               System.out.println(refund); */
               Map<String, Object> response = new HashMap<>();
               response.put("email", session.getCustomerDetails().getEmail());
               response.put("price: ", session.getAmountTotal()/100);
               //response.put("paymentintent", session.getPaymentIntentObject().getCreated());

               return ResponseEntity.ok(gson.toJson(response));
        }
           catch (Exception e){

               return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error retrieving payment details");
            }
        }



        @GetMapping("/transactions")
        public String getAllTransactions() throws StripeException {
            init();

            // Create parameters for listing sessions
            SessionListParams listParams = SessionListParams.builder()
                    .setLimit(100L)  // Adjust the limit as needed
                    .build();

            // List all sessions
            List<Session> sessions = Session.list(listParams).getData();

            // Extract relevant information from the sessions
            List<Map<String, Object>> transactions = new ArrayList<>();
            for (Session session : sessions) {
               if(session.getPaymentStatus().equals("paid")) {
                Map<String, Object> transactionData = new HashMap<>();
                transactionData.put("sessionId", session.getId());
                transactionData.put("customer", session.getCustomer());
               // transactionData.put("email customer", session.getCustomerEmail());
                transactionData.put("price: ", session.getAmountTotal()/100);
                transactionData.put("userdetails", session.getCustomerDetails());


               transactionData.put("transactionDate", session.getPaymentIntentObject() != null ? session.getPaymentIntentObject().getCreated() : null);
                transactionData.put("paymentStatus", session.getPaymentStatus());
                // Add more relevant information as needed

                transactions.add(transactionData);
           }
            }


            // Return the transactions as JSON
            return gson.toJson(transactions);
        }

        private static void init() {
            Stripe.apiKey = "sk_test_51N6jyuCxe9tqFGX1Lu7XzQFiVKGtDUIJqBH47EWrQ9fttV5lN5YLaAijXZbGU5ILRgG4sHehwFM1ysxaRhCqxN8d00mmyj5Z2o";
        }
    }




