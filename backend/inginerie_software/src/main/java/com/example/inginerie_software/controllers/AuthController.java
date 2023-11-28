package com.example.inginerie_software.controllers;

import com.google.common.collect.ImmutableMap;
import com.google.firebase.auth.*;
//changed here
//end
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

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
          /* setting an admin if (email.equals("dragosp0201@gmail.com")) {
              //  FirebaseAuth.getInstance().setCustomUserClaims(uid, ImmutableMap.of("admin", true));
               Map<String, Object> claims = new HashMap<>();
               claims.put("admin", true);
               FirebaseAuth.getInstance().setCustomUserClaims(uid, claims);
               System.out.println("email verified");
            }*/
            // Add your own logic here to further process the authenticated user
            System.out.println("token valid" + uid + ", email: " + email);


            //return ResponseEntity.ok().contentType(MediaType.APPLICATION_JSON).body(uid);
            Map<String, Object> claims = decodedToken.getClaims();
            boolean isAdmin = claims != null && claims.containsKey("admin") && (boolean) claims.get("admin");
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
}

