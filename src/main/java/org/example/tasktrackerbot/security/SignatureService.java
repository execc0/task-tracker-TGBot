package org.example.tasktrackerbot.security;

import lombok.extern.slf4j.Slf4j;
import org.example.tasktrackerbot.DTO.request.LinkSocialRequestPayload;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;

import javax.crypto.Mac;
import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;

@Service
@Slf4j
public class SignatureService {

    @Value("${api.hmac}")
    private String secretKey;

    private SecretKey getSigningKey(String key) {
        byte[] keyBytes = Decoders.BASE64.decode(key);
        return Keys.hmacShaKeyFor(keyBytes);
    }

    public String createSignature(LinkSocialRequestPayload payload) {

        try {

            String dataToSign = toCanonicalString(payload);

            SecretKey secretKey = getSigningKey(this.secretKey);

            Mac sha512Hmac = Mac.getInstance("HmacSHA512");
            sha512Hmac.init(secretKey);

            byte[] hashBytes = sha512Hmac.doFinal(dataToSign.getBytes(StandardCharsets.UTF_8));

            return bytesToHex(hashBytes);
        } catch (Exception e) {
            log.error("Произошла ошибка при создании подписи! {}", e.getMessage());
        }
        return null;
    }

    private String bytesToHex(byte[] bytes) {
        StringBuilder sb = new StringBuilder();
        for (byte b : bytes) {
            sb.append(String.format("%02x", b));
        }
        return sb.toString();
    }

    private String toCanonicalString(LinkSocialRequestPayload payload) {
        return String.format("%s|%s|%d", payload.getProviderId(), payload.getProviderId(), payload.getTimestamp());
    }

}
