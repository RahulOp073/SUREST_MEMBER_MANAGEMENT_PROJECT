package org.surest.util;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

public class PasswordHashGenerator {

    public static void main(String[] args) {
        PasswordEncoder encoder = new BCryptPasswordEncoder();

        String adminPassword = "admin@123";
        String userPassword = "user@123";

        System.out.println("admin@123 => " + encoder.encode(adminPassword));
        System.out.println("user@123  => " + encoder.encode(userPassword));
    }
}
