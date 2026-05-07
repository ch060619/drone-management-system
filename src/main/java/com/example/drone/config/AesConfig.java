package com.example.drone.config;

import com.example.drone.common.AesUtil;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class AesConfig {

    @Bean
    public AesUtil aesUtil(@Value("${aes.secret-key:your_base64_encoded_aes_key}") String base64Key) {
        return new AesUtil(base64Key);
    }
}
