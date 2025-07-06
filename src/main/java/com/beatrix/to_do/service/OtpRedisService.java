package com.beatrix.to_do.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.util.Random;

@Service
public class OtpRedisService {
    private final StringRedisTemplate redisTemplate;
    private static Duration OTP_TTL = Duration.ofMinutes(2);

    @Autowired
    public OtpRedisService(StringRedisTemplate redisTemplate){
        this.redisTemplate = redisTemplate;
    }

    public String otpKey(String prefix, String email){
        return prefix + "::" + email;
    }
    public void saveOtp(String prefix , String email, String otp){
        redisTemplate.opsForValue().set(otpKey(prefix,email),otp, OTP_TTL);
    }

    public String getOtp(String prefix, String email){
        return redisTemplate.opsForValue().get(otpKey(prefix, email));
    }

    public void invalidateOtp(String prefix, String email){
        redisTemplate.delete(otpKey(prefix,email));
    }

    public String generateOtp(){
        Random random = new Random();
        int rand = random.nextInt(999999);
        return String.format("%06d", rand);
    }
}
