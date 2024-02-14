package com.example.inginerie_software.controllers;

import com.example.inginerie_software.services.EmailService;
import com.example.inginerie_software.services.reservationService;
import com.google.common.collect.ImmutableMap;
import com.google.firebase.auth.*;

import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.springframework.web.util.UriComponentsBuilder;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final EmailService emailService;

    public AuthController(EmailService eS) { this.emailService=eS;}

    @PostMapping("/login")
    public ResponseEntity<String> login(@RequestBody Map<String, String> loginRequest) {
        String email = loginRequest.get("email");
        String password = loginRequest.get("password");

        try {
            // Authenticate user with Firebase
            String token = FirebaseAuth.getInstance().createCustomToken(email);
            System.out.println(token);
            // You can return the Firebase token or any other response as needed
            return ResponseEntity.ok(token.toString());
        } catch (FirebaseAuthException e) {
            // Handle authentication error
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Authentication failed");
        }
    }

    @PostMapping("/verify-token")
    public ResponseEntity<?> verifyToken(@RequestBody Map<String, String> tokenRequest) {
        String idToken = tokenRequest.get("idToken");
        System.out.println("this is the token: ");
        // System.out.println(tokenRequest);

        //return ResponseEntity.ok("Token is valid. UID: " );
        try {
            FirebaseToken decodedToken = FirebaseAuth.getInstance().verifyIdToken(idToken);

            // You can now use information from the decoded token, such as UID, email, etc.
            String uid = decodedToken.getUid();
            String email = decodedToken.getEmail();
            // setting an admin
           // Map<String, Object> existingClaims = new HashMap<>(FirebaseAuth.getInstance().getUser(uid).getCustomClaims());

         /* if (email.equals("dragosp0201@gmail.com")) {

              //  FirebaseAuth.getInstance().setCustomUserClaims(uid, ImmutableMap.of("admin", true));

               existingClaims.put("admin", true);
               FirebaseAuth.getInstance().setCustomUserClaims(uid, existingClaims);
               System.out.println("email verified");
            } */
            // Add your own logic here to further process the authenticated user
            System.out.println("token valid" + uid + ", email: " + email);


            //return ResponseEntity.ok().contentType(MediaType.APPLICATION_JSON).body(uid);
            Map<String, Object> claims = decodedToken.getClaims();
            boolean isAdmin = claims != null && claims.containsKey("admin") && (boolean) claims.get("admin");
            if (claims != null) {
                System.out.println("Custom Claims:");
                for (Map.Entry<String, Object> entry : claims.entrySet()) {
                    System.out.println(entry.getKey() + ": " + entry.getValue());
                }
            }
            UserRecord userRecord = FirebaseAuth.getInstance().getUser(uid);
         /*  if (isAdmin) {

           }
            Map<String, Object> response = new HashMap<>();
            response.put("key1", "value1");
            response.put("key2", "value2");
            response.put("key3", "value3");*/


            return ResponseEntity.ok(userRecord);
        } catch (FirebaseAuthException e) {
            // Handle verification error
            System.out.println("EXCEPTION " + e.toString());
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Token verification failed");
        }
    }

    // GET USERS ENDPOINT:
    @GetMapping("/admin/users")
    public ResponseEntity<?> getAllUsers(@RequestHeader("Authorization") String idToken) {
        try {
            FirebaseToken decodedToken = FirebaseAuth.getInstance().verifyIdToken(idToken);
            boolean isAdmin = decodedToken.getClaims() != null &&
                    decodedToken.getClaims().containsKey("admin") &&
                    (boolean) decodedToken.getClaims().get("admin");

            if (!isAdmin) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Only admin can access this resource");
            }

            ListUsersPage page = FirebaseAuth.getInstance().listUsers(null);
            List<Map<String, Object>> usersResponse = new ArrayList<>();
            while (page != null) {
                for (ExportedUserRecord userRecord : page.getValues()) {
                    // You can customize the information you want to include in the response
                    Map<String, Object> userData = Map.of(
                            "uid", userRecord.getUid(),
                            "email", userRecord.getEmail(),
                            "role", userRecord.getCustomClaims() != null &&
                                    userRecord.getCustomClaims().containsKey("admin") &&
                                    (boolean) userRecord.getCustomClaims().get("admin") ? "admin" : "user"
                            // Add other user properties as needed
                    );
                    usersResponse.add(userData);
                }
                page = page.getNextPage();
            }


            return ResponseEntity.ok(usersResponse);
        } catch (FirebaseAuthException e) {
            // Handle verification error
            System.out.println("EXCEPTION " + e.toString());
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Token verification failed");
        }
    }


    // reset password mail:
    @PostMapping("/reset-password")
    public ResponseEntity<?> getResetLink(@RequestBody String email) {
        ActionCodeSettings actionCodeSettings = ActionCodeSettings.builder()
                .setUrl("https://frontend-bikes4all.fly.dev")
                .setHandleCodeInApp(true)
                //.setDynamicLinkDomain("ingineriesoftware.web.app")
                .build();

        System.out.println("EMAIL:: " + email);
        // String email = "polifronie.dragos@yahoo.com";
        try {
            String link = FirebaseAuth.getInstance().generatePasswordResetLink(
                    email, actionCodeSettings);
            // Construct email verification template, embed the link and send
            // using custom SMTP server.
           // sendCustomEmail(email, displayName, link);
            UriComponentsBuilder builder = UriComponentsBuilder.fromUriString(link);
            String oobCode = builder.build().getQueryParams().get("oobCode").get(0);
            System.out.println(oobCode);
            String resetLink = UriComponentsBuilder.fromUriString("https://frontend-bikes4all.fly.dev/auth?mode=resetPassword&oobCode="+oobCode).build().toString();
            this.emailService.sendResetPasswordEmail(email, resetLink);

            return ResponseEntity.ok(Map.of("message", "email sent"));


        } catch (FirebaseAuthException e) {
            System.out.println("Error generating email link: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Map.of("message", "Couldn't generate link"));
        } catch ( IOException e) {
            System.out.println("Error sending the email: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Map.of("message", "Couldn't send email"));
        }
        catch (Exception e) {
            System.out.println("Error internal " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Map.of("message", "Generic error. Make sure the email is valid"));
        }

    }

}

