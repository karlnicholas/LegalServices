package com.github.karlnicholas.legalservices.user.security.config;

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
    @Bean
    public PublicKey getPublicKey() throws NoSuchAlgorithmException, InvalidKeySpecException {
        String rsaPublicKey = loadResourceFile("security/rsa_public.pem");
        rsaPublicKey = rsaPublicKey.replace("-----BEGIN PUBLIC KEY-----", "");
        rsaPublicKey = rsaPublicKey.replace("-----END PUBLIC KEY-----", "");
        X509EncodedKeySpec keySpec = new X509EncodedKeySpec(Base64.getDecoder().decode(rsaPublicKey));
        KeyFactory kf = KeyFactory.getInstance("RSA");
        PublicKey publicKey = kf.generatePublic(keySpec);
        return publicKey;
    }
    @Bean
    public PrivateKey getPrivateKey() throws NoSuchAlgorithmException, InvalidKeySpecException {
        String rsaPrivateKey = loadResourceFile("security/rsa_private.pem");
        rsaPrivateKey = rsaPrivateKey.replace("-----BEGIN PRIVATE KEY-----", "");
        rsaPrivateKey = rsaPrivateKey.replace("-----END PRIVATE KEY-----", "");
        PKCS8EncodedKeySpec keySpec = new PKCS8EncodedKeySpec(Base64.getDecoder().decode(rsaPrivateKey));
        KeyFactory kf = KeyFactory.getInstance("RSA");
        PrivateKey privKey = kf.generatePrivate(keySpec);
        return privKey;
    }
    private String loadResourceFile(String fileName) {
        ClassLoader classLoader = getClass().getClassLoader();
        StringBuilder sb = new StringBuilder();
        try (InputStream inputStream = classLoader.getResourceAsStream(fileName);
            InputStreamReader streamReader = new InputStreamReader(inputStream, StandardCharsets.UTF_8);
            BufferedReader reader = new BufferedReader(streamReader)) {
            String line;
            while ((line = reader.readLine()) != null) {
                sb.append(line);
            }
        } catch ( IOException e) {
            throw new RuntimeException(e);
        }
        return sb.toString();
    }
}
