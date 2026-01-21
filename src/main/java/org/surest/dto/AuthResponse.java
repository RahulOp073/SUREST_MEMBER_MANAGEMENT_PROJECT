package org.surest.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.List;

@Getter
@AllArgsConstructor
public class AuthResponse {

    private final String token;
    private final String username;
    private final List<String> roles;
}
