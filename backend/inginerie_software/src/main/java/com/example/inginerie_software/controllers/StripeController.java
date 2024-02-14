package com.example.inginerie_software.controllers;

import com.example.inginerie_software.models.*;
import com.example.inginerie_software.services.EmailService;
import com.example.inginerie_software.services.bikes_typeService;
import com.example.inginerie_software.services.reservationService;
import com.example.inginerie_software.services.tokensService;
import com.google.common.collect.ImmutableMap;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthException;
import com.google.firebase.auth.FirebaseToken;
import com.google.firebase.auth.UserRecord;
import com.google.gson.*;
import com.stripe.exception.EventDataObjectDeserializationException;
import com.stripe.exception.SignatureVerificationException;
import com.stripe.exception.StripeException;
import com.stripe.model.*;
import com.stripe.net.ApiResource;
import com.stripe.net.Webhook;
import com.stripe.param.CustomerCreateParams;
import com.stripe.param.CustomerUpdateParams;
import com.stripe.param.RefundCreateParams;
import com.stripe.param.checkout.SessionCreateParams;
import com.stripe.param.checkout.SessionListParams;
import org.apache.coyote.Response;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.lang.reflect.Type;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.*;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.google.gson.Gson;

import com.stripe.Stripe;
import com.stripe.exception.StripeException;
import com.stripe.model.checkout.Session;
import com.stripe.param.checkout.SessionCreateParams;
import org.springframework.web.util.UriComponentsBuilder;

//secret = sk_test_51N6jyuCxe9tqFGX1Lu7XzQFiVKGtDUIJqBH47EWrQ9fttV5lN5YLaAijXZbGU5ILRgG4sHehwFM1ysxaRhCqxN8d00mmyj5Z2o

    @RestController
    public class StripeController {
        // create a Gson object
        public static Gson gson = new Gson();


        private final com.example.inginerie_software.services.bikesService bikesService;
        private final reservationService reservationService;
        private final EmailService emailService;
        private final com.example.inginerie_software.services.tokensService tokensService;

        public StripeController(
                com.example.inginerie_software.services.bikesService bS,
                reservationService rS, EmailService eS, tokensService tS) {
            this.bikesService = bS; this.reservationService = rS; this.emailService=eS; this.tokensService = tS;}

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
                    .setMode(SessionCreateParams.Mode.PAYMENT)
                    .setPaymentIntentData(SessionCreateParams.PaymentIntentData.builder().setSetupFutureUsage(SessionCreateParams.PaymentIntentData.SetupFutureUsage.ON_SESSION).build())
                    .setSuccessUrl(payment.getSuccessUrl())

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


        // creates a stripe customer for received account - for first signup;
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


        // production - return receipt to user after payment.
        /* TODO */
        // next - production - to be replaced with gson serializer for response
        @GetMapping("/success-details")
        public ResponseEntity<?> getSuccessDetails(
                @RequestHeader(name = "X-Session-ID") String sessionId,
                @RequestHeader("Authorization") String idToken
        ) throws StripeException, FirebaseAuthException {

            init();


            try {
                Session session = Session.retrieve(sessionId);
                FirebaseToken decodedToken = FirebaseAuth.getInstance().verifyIdToken(idToken);
                if(!decodedToken.getUid().equals(session.getMetadata().get("user"))){
                    return ResponseEntity.status(HttpStatus.FORBIDDEN).body("You can't access this");
                }
                reservation res = reservationService.getReservationByStripeid(sessionId);
                Map<String, Object> response = new HashMap<>();
                response.put("reservation", res);
                response.put("paid: ", session.getAmountTotal()/100);
                response.put("email: ", session.getCustomerDetails().getEmail());
                return ResponseEntity.ok(response);

            }
            catch( StripeException s) {
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Stripe error, session doesn't exist");
            }
            catch (FirebaseAuthException f) {
                f.printStackTrace();
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("You are not authorized.");
            }
            catch (NoSuchElementException n) {
                n.printStackTrace();
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Reservation has not been successfull");
            }
            catch (Exception e) {
                e.printStackTrace();
            } {return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error retrieving payment details");}
        }


        // stripe -webhook, creates reservation if checkout completed.
        @PostMapping("/stripe-webhook")
        public ResponseEntity<String> handleStripeWebhook(@RequestBody String payload,
                                                          @RequestHeader("Stripe-Signature") String signature) throws Exception {

            Event event = null;
            try {
                event = ApiResource.GSON.fromJson(payload, Event.class);
                System.out.println("event::::::::::::::"+event);
            } catch (JsonSyntaxException e) {
                // Invalid payload
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("error signature");
            }

            EventDataObjectDeserializer dataObjectDeserializer = event.getDataObjectDeserializer();
            StripeObject stripeObject = null;
            if (dataObjectDeserializer.getObject().isPresent()) {
                stripeObject = dataObjectDeserializer.getObject().get();
            } else {
                System.out.println("deserialization failed...............");
                stripeObject = dataObjectDeserializer.deserializeUnsafe();
                System.out.println("unsafe deserialization:::::::::" + stripeObject);
            }
            switch (event.getType()) {
               /* case "payment_intent.succeeded":
                    PaymentIntent paymentIntent = (PaymentIntent) stripeObject;
                    // Then define and call a method to handle the successful payment intent.
                    // handlePaymentIntentSucceeded(paymentIntent);
                    break;*/
                case "checkout.session.completed":
                    Session session = (Session) stripeObject;

                    String bikeid = session.getMetadata().get("reservedbike");
                    String userid = session.getMetadata().get("user");

                    bikes b = this.bikesService.getBikeById(Long.parseLong(bikeid));
                    System.out.println("BIKE IN CREATE RESERVATION: =>>>>>>>>>> " + b);
                    b.setAvailable(false);
                    this.bikesService.saveBike(b);
                    this.reservationService.createReservation(userid, b, session.getId());
                    System.out.println("reserved bike " + bikeid + " by user: " + userid + " with stripeid:: " + session.getId());
                    break;
                case "refund.created":
                    Refund refund = (Refund) stripeObject;
                    //String cus = refund.getPaymentIntentObject().getCustomer();
                    String sessionId = refund.getMetadata().get("session_id");
                    //String userid = session.getMetadata().get("user");
                    reservation res = this.reservationService.getReservationByStripeid(sessionId);
                   // System.out.println(" RESERVATION: " + res);
                    if(res.getExpiryDate().isAfter(LocalDateTime.now())) {
                    bikes b2 = res.getBike();
                   // System.out.println("BIKE IN CREATE RESERVATION: =>>>>>>>>>> " + b2);
                    b2.setAvailable(true);
                    res.setExpiryDate(LocalDateTime.now());
                    this.bikesService.saveBike(b2);
                    this.reservationService.saveReservation(res);
                    }
                    break;
                case "charge.refund.updated":
                    System.out.println("==================================");
                    System.out.println(event.getType() + " CASE ===================");
                    System.out.println("=====STRIPEOBJECT CLASS========  " + stripeObject.getClass());

                   Refund charge = (Refund) stripeObject;
                    System.out.println("=====STRIPEOBJECT VALUE=======  " + stripeObject.getClass());
                    System.out.println("===============END STRIPEOBJECT VALUE======================================");

                   String sess_id = charge.getMetadata().get("session_id");
                    System.out.println("STRIPE SESSION ID: = " + sess_id);

                    // String sess_id = charge.getPaymentIntentObject().getMetadata().get("session_id");
                    reservation res2 = this.reservationService.getReservationByStripeid(sess_id);
                    bikes b2 = res2.getBike();
                    System.out.println("BIKE IN CANCEL RESERVATION: =>>>>>>>>>> " + b2);
                    b2.setAvailable(true);
                    res2.setExpiryDate(LocalDateTime.now());
                    this.bikesService.saveBike(b2);
                    this.reservationService.saveReservation(res2);
                    break;

                default:
                    System.out.println("Unhandled event type: " + event.getType());
            }


            return ResponseEntity.ok("");

        }


        @GetMapping("/current-reservation")
        public ResponseEntity<?> getCurrentReservation( @RequestHeader("Authorization") String idToken ) {
            try{
                FirebaseToken decodedToken = FirebaseAuth.getInstance().verifyIdToken(idToken);
                String uid = decodedToken.getUid();
                Optional<reservation> highestExpiryReservation = reservationService.getHighestExpiryReservationForUser(uid);
                System.out.println("found   ++ " + highestExpiryReservation);
                return highestExpiryReservation.map(ResponseEntity::ok)
                        .orElse(ResponseEntity.notFound().build());


            }
            catch(Exception e) {

                e.printStackTrace();
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error retrieving payment details");
            }
        }


        @PostMapping("/refund")
        public ResponseEntity<?> refundCurrentReservation( @RequestHeader("Authorization") String idToken ) {
                init();
            try{
                FirebaseToken decodedToken = FirebaseAuth.getInstance().verifyIdToken(idToken);
                String uid = decodedToken.getUid();
                Optional<reservation> highestExpiryReservation = reservationService.getHighestExpiryReservationForUser(uid);
                Session session = Session.retrieve(highestExpiryReservation.get().getStripeid());
                System.out.println("SESSION IN /REFUND: " + session);

                LocalDateTime now = LocalDateTime.now();
                if(highestExpiryReservation.get().getExpiryDate().isBefore(LocalDateTime.now())) {
                    return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("This payment has been refunded already.");
                }
                // PaymentIntent paymentIntent = session.getPaymentIntentObject();
                PaymentIntent paymentIntent = PaymentIntent.retrieve(session.getPaymentIntent());

                paymentIntent.setMetadata(Map.of("session_id", session.getId()));
                System.out.println("===================PAYMENT INTENT: " + paymentIntent);
                long minutesDifference = ChronoUnit.MINUTES.between(now, highestExpiryReservation.get().getExpiryDate());
                Long amount = Math.round(session.getAmountTotal() * ((double)minutesDifference / 60));
                System.out.println("amount " + amount + " minutes difference: " + minutesDifference + " Stripe amount" + session.getAmountTotal());
                RefundCreateParams params =
                        RefundCreateParams.builder()
                                .setPaymentIntent(paymentIntent.getId())
                                .setAmount(amount)
                                .putMetadata("session_id", session.getId())
                                .build();

                Refund refund = Refund.create(params);

                // mail send:
                this.emailService.sendTemplateEmail(decodedToken.getEmail(), session.getAmountTotal()/100,
                        highestExpiryReservation.get().getExpiryDate().toString(),
                        highestExpiryReservation.get().getBike().getTip().getNume(),
                        highestExpiryReservation.get().getBike().getTip().getImage_url(),
                        highestExpiryReservation.get().getBike().getTip().getPrice() - amount
                        );

                return highestExpiryReservation.map(ResponseEntity::ok)
                        .orElse(ResponseEntity.notFound().build());


            }
            catch(Exception e) {
                System.out.println(e);
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error retrieving payment details");
            }
        }


        @PostMapping("/reset-email")
        public ResponseEntity<?> resetEmail(
                @RequestHeader("Authorization") String idToken, @RequestBody String email) {


            init();
            try{
                FirebaseToken decodedToken = FirebaseAuth.getInstance().verifyIdToken(idToken);
                String old_email = decodedToken.getEmail();


                // create expiry date to verify in /verify-code
                tokens t = this.tokensService.createToken(old_email, email);

                String resetLink = UriComponentsBuilder.fromUriString("https://frontend-bikes4all.fly.dev/auth?mode=changeEmail&oobCode="+t.getValue()).build().toString();
                // create link from token, send email
                this.emailService.sendChangeEmail(old_email, resetLink, email);


                // this.emailService.sendChangeEmail(email, resetLink, email);




                return ResponseEntity.ok(Map.of("message", "Email sent. Link is available for 60minutes"));


            } catch (FirebaseAuthException e) {
                e.printStackTrace();
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(Map.of("message", "You are not authorized."));
            }
            catch ( IOException e) {
                System.out.println("Error sending the email: " + e.getMessage());
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Map.of("message", "Couldn't send email"));
            }
            catch (Exception e ) {
                System.out.println("Error retrieving request email: " + e.getMessage());
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Map.of("message", "Email invalid"));
            }

        }


        @GetMapping("/verify-email-code")
        public ResponseEntity<?> verifyEmailChange
                (@RequestParam("oobCode") String oobCode) {

            try {
                init();
                tokens t = tokensService.getTokenByValue(oobCode);
                if(t.getExpiryDate().isBefore(LocalDateTime.now()))  {
                   return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Map.of("message", "Link expired."));

                }


                String old_mail = t.getCurrent_email();
                String new_mail = t.getNew_email();


                UserRecord userRecord = FirebaseAuth.getInstance().
                        getUserByEmail(old_mail);

                String cus_id = userRecord.getCustomClaims().get("customerid").toString();


                Customer resource = Customer.retrieve(cus_id);


                CustomerUpdateParams params =
                        CustomerUpdateParams.builder().setEmail(new_mail).build();
                Customer customer = resource.update(params);

                System.out.println("Stripe customer updated  " + customer);

                UserRecord.UpdateRequest request = new UserRecord.UpdateRequest(userRecord.getUid()).setEmail(new_mail);

                UserRecord userRecord_updated = FirebaseAuth.getInstance().updateUser(request);



                System.out.println("Successfully updated user: " + userRecord_updated);

                tokensService.deleteToken(oobCode);

                return ResponseEntity.ok(Map.of("message", "Email changed successfully"));


            }
             catch (FirebaseAuthException e) {
                e.printStackTrace();
                 return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Map.of("message", "Couldn't get a user"));
            }
            catch ( StripeException e) {
                e.printStackTrace();
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Map.of("message", "Couldn't update the customer"));
            }
            catch (NullPointerException e) {
                e.printStackTrace();
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Map.of("message", "It looks like your code has already been used"));
            }
            catch (Exception e) {
                e.printStackTrace();
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Map.of("message", "Something went wrong.."));
            }

        }

        /*
        ------------------------------------------------------------------------------------------------------------------
                                                    DEV/TEST HELPERS HERE
                                             COMMENT/DELETE THEM BEFORE PRODUCTION
        -----------------------------------------------------------------------------------------------------------------
         */

        /*
        // endpoint for cancel-reservation, for production mode,
        // add a stripe webhook and then if refunded success, cancel the reservation.
        @PostMapping("/cancel-reservation")
        public ResponseEntity<?> cancelReservation( @RequestHeader("Authorization") String idToken ){
                                                   // @RequestBody String s) {
            try{
                FirebaseToken decodedToken = FirebaseAuth.getInstance().verifyIdToken(idToken);
                String uid = decodedToken.getUid();
                Optional<reservation> highestExpiryReservation = reservationService.getHighestExpiryReservationForUser(uid);

                highestExpiryReservation.ifPresent(reservation -> {
                    // Set the expiry date to the current time
                    reservation.setExpiryDate(LocalDateTime.now());
                    reservation.getBike().setAvailable(true);
                    bikesService.saveBike(reservation.getBike());
                    // Save the updated reservation to the database
                    reservationService.saveReservation(reservation);
                });

                return highestExpiryReservation.map(ResponseEntity::ok)
                        .orElse(ResponseEntity.notFound().build());


            }
            catch(Exception e) {
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error canceling reservation");
            }
        }

        */
        @GetMapping("/reservations")
        public ResponseEntity<?> getResrevations() {
            GsonBuilder gsonBuilder = new GsonBuilder();


            gsonBuilder.registerTypeAdapter(LocalDateTime.class, new LocalDateTimeSerializer());
            gsonBuilder.registerTypeAdapter(LocalDateTime.class, new LocalDateTimeDeserializer());

            gsonBuilder.registerTypeAdapter(reservation.class, new ReservationSerializer());
            gsonBuilder.registerTypeAdapter(reservation.class, new ReservationDeserializer());

            Gson gsontwo = gsonBuilder.setPrettyPrinting().create();
            System.out.println("get reservations");
            List<reservation> restwo = reservationService.getReservations();
            System.out.println("RESERVATIONS----------------------: " + restwo);
            return ResponseEntity.ok(gsontwo.toJson(reservationService.getReservations()));
        }



        // !! Test - only
        // Get all reservations;
        @GetMapping("/reservations-two")
            public List<reservation> getReservations() {
            List<reservation> restwo = reservationService.getReservations();
            return restwo;
            }

        @GetMapping("/payment-details")
        public ResponseEntity<?> getPaymentDetails(
                @RequestHeader(name = "X-Session-ID") String sessionId,
                @RequestHeader("Authorization") String idToken
                        ) throws StripeException, FirebaseAuthException {

            init();


           try{
               FirebaseToken decodedToken = FirebaseAuth.getInstance().verifyIdToken(idToken);


               Session session = Session.retrieve(sessionId);
               System.out.println(decodedToken.getUid());
              // System.out.println("========session==========" + session);
               if(!decodedToken.getUid().equals(session.getMetadata().get("user"))){
                   return ResponseEntity.status(HttpStatus.FORBIDDEN).body("You can't access this");
               }

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






        // TO DO :: ADMIN - ONLY
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



// Serializers/Deserializers For DateTime Format and reservations type
// Will use for next delivery with the /reservation endpoint, when reservations schema is set for good
class LocalDateSerializer implements JsonSerializer <LocalDate> {
    private static final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-d");

    @Override
    public JsonElement serialize(LocalDate localDate, Type srcType, JsonSerializationContext context) {
        return new JsonPrimitive(formatter.format(localDate));
    }
}

class LocalDateTimeSerializer implements JsonSerializer < LocalDateTime > {
    private static final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("uuuu::MM::d HH::mm::ss::ms");

    @Override
    public JsonElement serialize(LocalDateTime localDateTime, Type srcType, JsonSerializationContext context) {
        return new JsonPrimitive(formatter.format(localDateTime));
    }
}

class LocalDateDeserializer implements JsonDeserializer < LocalDate > {
    @Override
    public LocalDate deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context)
            throws JsonParseException {
        return LocalDate.parse(json.getAsString(),
                DateTimeFormatter.ofPattern("yyyy-MM-d").withLocale(Locale.ENGLISH));
    }
}

class LocalDateTimeDeserializer implements JsonDeserializer < LocalDateTime > {
    @Override
    public LocalDateTime deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context)
            throws JsonParseException {
        return LocalDateTime.parse(json.getAsString(),
                DateTimeFormatter.ofPattern("uuuu::MM::d HH::mm::ss::ms").withLocale(Locale.ENGLISH));
    }
}


class ReservationSerializer implements JsonSerializer<reservation> {
    @Override
    public JsonElement serialize(reservation src, Type typeOfSrc, JsonSerializationContext context) {
        JsonObject jsonReservation = new JsonObject();
     //   jsonReservation.addProperty("id", src.getUserid());
        jsonReservation.addProperty("userid", src.getUserid());
        //jsonReservation.addProperty("stripeid", src.getBike());

        // Add other properties as needed

        // Example: Serialize the associated bike
        /*if (src.getBike() != null) {
            jsonReservation.add("bike", context.serialize(src.getBike(), bikes.class));
        }*/

        return jsonReservation;
    }
}

class ReservationDeserializer implements JsonDeserializer<reservation> {
    @Override
    public reservation deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context)
            throws JsonParseException {
        JsonObject jsonObject = json.getAsJsonObject();

        String userid = jsonObject.get("userid").getAsString();
        String stripeid = jsonObject.get("stripeid").getAsString();

        // Example: Deserialize the associated bike
        bikes bike = context.deserialize(jsonObject.get("bike"), bikes.class);

        // Create and return the reservation
        return new reservation(userid, bike, stripeid);
    }
}