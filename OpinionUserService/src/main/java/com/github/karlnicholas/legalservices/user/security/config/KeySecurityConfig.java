package com.github.karlnicholas.legalservices.user.security.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.security.KeyFactory;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.util.Base64;

/**
 * WebSecurityConfig class
 *
 * @author Karl Nicholas
 */
@Configuration
public class KeySecurityConfig {
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    private byte[] sharedSecret = null;

    @Value("${jwt.shared_secret_hex}")
    private String sharedSecretHex;

    @Bean
    public byte[] getSecret() {
        if ( sharedSecret == null ) {
            sharedSecret = new byte[32];
            int l = sharedSecretHex.length()/2;
            for (int i = 0; i < l; i++) {
                int j = Integer.parseInt(sharedSecretHex.substring(i*2, i*2+2), 16);
                sharedSecret[i] = (byte) j;
            }
        }
        return sharedSecret;
    }
//    private String loadResourceFile(String fileName) {
//        ClassLoader classLoader = getClass().getClassLoader();
//        StringBuilder sb = new StringBuilder();
//        try (InputStream inputStream = classLoader.getResourceAsStream(fileName);
//            InputStreamReader streamReader = new InputStreamReader(inputStream, StandardCharsets.UTF_8);
//            BufferedReader reader = new BufferedReader(streamReader)) {
//            String line;
//            while ((line = reader.readLine()) != null) {
//                sb.append(line);
//            }
//        } catch ( IOException e) {
//            throw new RuntimeException(e);
//        }
//        return sb.toString();
//    }
}
