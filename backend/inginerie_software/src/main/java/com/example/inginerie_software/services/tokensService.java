package com.example.inginerie_software.services;


import com.example.inginerie_software.models.tokens;
import com.example.inginerie_software.repositories.tokensRepository;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
@Transactional
public class tokensService {

    private final tokensRepository tokensRepository;

    @Autowired
    public tokensService(tokensRepository tR) {
        this.tokensRepository = tR;

    }

    public tokens saveToken(tokens t) {
        return tokensRepository.save(t);
    }

    public tokens createToken(String old_mail, String new_mail) {
        tokens t = new tokens(old_mail, new_mail);
        return tokensRepository.save(t);
    }

    public tokens getTokenByValue(String token_value) {
        return tokensRepository.findByValue(token_value);
    }


    public void deleteToken(String tokenValue) {
        tokens token = tokensRepository.findByValue(tokenValue);
        if (token != null) {
            tokensRepository.delete(token);
        } else {
            // Handle the case where the token does not exist
            throw new IllegalArgumentException("Token not found for value: " + tokenValue);
        }
    }


}
