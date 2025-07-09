package com.beatrix.taskflow.service;

import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import redis.clients.jedis.UnifiedJedis;

import java.time.Duration;
import java.util.Random;
@Slf4j
@Service
public class OtpRedisService {
    @Value("${spring.redis.host}")
    private String redisHost;

    @Value("${spring.redis.port}")
    private String redisPort;

    UnifiedJedis jedis;
    private static final Duration OTP_TTL = Duration.ofMinutes(2);

    @PostConstruct
    public void init(){
        this.jedis = new UnifiedJedis("redis://" + redisHost + ":" + redisPort);
    }
    public String otpKey(String prefix, String email){
        return prefix + "::" + email;
    }

    public void saveOtp(String prefix , String email, String otp){
        log.info("Received request to save the otp : {}", otp);
        jedis.set(otpKey(prefix,email), otp);
        log.info("Saved the otp inside redis");
    }

    public String getOtp(String prefix, String email){
        return jedis.get(otpKey(prefix,email));
    }

    public void invalidateOtp(String prefix, String email){
        jedis.del(otpKey(prefix,email));
    }

    public String generateOtp(){
        Random random = new Random();
        int rand = random.nextInt(999999);
        return String.format("%06d", rand);
    }

}