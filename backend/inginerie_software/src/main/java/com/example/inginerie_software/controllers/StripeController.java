package com.example.inginerie_software.controllers;

import com.example.inginerie_software.models.CheckoutPayment;
import com.example.inginerie_software.models.bikes_type;
import com.example.inginerie_software.services.bikes_typeService;
import com.google.gson.Gson;
import com.stripe.exception.StripeException;
import com.stripe.param.checkout.SessionCreateParams;
import com.stripe.param.checkout.SessionListParams;
import org.springframework.web.bind.annotation.*;
import com.example.inginerie_software.models.bikes;

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
        public String paymentWithCheckoutPage(@RequestBody CheckoutPayment payment) throws StripeException {
            // We initilize stripe object with the api key
            init();
            bikes b = this.bikesService.getBikeById(payment.getId_bike());
            long pret = b.getTip().getPrice() * 100;
            long q = 1;
            String name = b.getTip().getNume();

            // We create a  stripe session parameters
            SessionCreateParams params = SessionCreateParams.builder()
                    // We will use the credit card payment method
                    .addPaymentMethodType(SessionCreateParams.PaymentMethodType.CARD)
                    .setMode(SessionCreateParams.Mode.PAYMENT).setSuccessUrl(payment.getSuccessUrl())
                    .setCancelUrl(
                            payment.getCancelUrl())
               //     .setCustomer("cus_P2KyuBUoyjZ7As")
                    .addLineItem(
                            SessionCreateParams.LineItem.builder().setQuantity(q)
                                    .setPriceData(
                                            SessionCreateParams.LineItem.PriceData.builder()
                                                    .setCurrency(payment.getCurrency()).setUnitAmount(pret)
                                                    .setProductData(SessionCreateParams.LineItem.PriceData.ProductData
                                                            .builder().setName(name).build())
                                                    .build())
                                    .build())
                    .build();
            // create a stripe session
            System.out.println(payment);
            Session session = Session.create(params);
            System.out.println(session);
            Map<String, String> responseData = new HashMap<>();
            // We get the sessionId and we putted inside the response data you can get more info from the session object
            responseData.put("id", session.getId());
            // We can return only the sessionId as a String
            return gson.toJson(responseData);
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




