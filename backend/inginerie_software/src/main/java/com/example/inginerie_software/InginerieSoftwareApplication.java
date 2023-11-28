package com.example.inginerie_software;

import com.google.auth.oauth2.GoogleCredentials;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.filter.CorsFilter;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Arrays;
import java.util.Objects;

@RestController
@SpringBootApplication
public class InginerieSoftwareApplication {

    @GetMapping("/")
    String home() {
        return "Bikes4all spring backend; /bikes; /bikes_type -> available endpoints";
    }


    public static void main(String[] args) throws IOException {


        SpringApplication.run(InginerieSoftwareApplication.class, args);
        ClassLoader classLoader = InginerieSoftwareApplication.class.getClassLoader();
        File file = new File(Objects.requireNonNull(classLoader.getResource("admin-sdk.json")).getFile());
        FileInputStream fs = new FileInputStream(file.getAbsolutePath());

        FirebaseOptions options = new FirebaseOptions.Builder()
                .setCredentials(GoogleCredentials.fromStream(fs))
                .build();

        FirebaseApp svg = FirebaseApp.initializeApp(options);
        System.out.println(svg);
        //FirebaseApp svg = FirebaseApp.initializeApp(options, "s12qjoasljdoairj1223");
        //System.out.println(svg);


    }

    @Bean
    public CorsFilter corsFilter() {
        CorsConfiguration corsConfiguration = new CorsConfiguration();
        corsConfiguration.setAllowCredentials(true);
        corsConfiguration.setAllowedOrigins(Arrays.asList("http://localhost:4200", "localhost:4200", "192.168.0.248:4200",
                "http://192.168.0.248:4200", "https://frontend-bikes4all.fly.dev", "frontend-bikes4all.fly.dev", 
                "https://bikesv2.fly.dev", "bikesv2.fly.dev"));
        corsConfiguration.setAllowedHeaders(Arrays.asList("Origin", "Access-Control-Allow-Origin", "Content-Type",
                "Accept", "Authorization", "Origin, Accept", "X-Requested-With",
                "Access-Control-Request-Method", "Access-Control-Request-Headers"));
        corsConfiguration.setExposedHeaders(Arrays.asList("Origin", "Content-Type", "Accept", "Authorization",
                "Access-Control-Allow-Origin", "Access-Control-Allow-Origin", "Access-Control-Allow-Credentials"));
        corsConfiguration.setAllowedMethods(Arrays.asList("GET", "POST", "PUT", "DELETE", "OPTIONS"));
        UrlBasedCorsConfigurationSource urlBasedCorsConfigurationSource = new UrlBasedCorsConfigurationSource();
        urlBasedCorsConfigurationSource.registerCorsConfiguration("/**", corsConfiguration);
        return new CorsFilter(urlBasedCorsConfigurationSource);
    }

}