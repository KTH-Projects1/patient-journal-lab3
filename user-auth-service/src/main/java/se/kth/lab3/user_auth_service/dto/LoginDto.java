package se.kth.lab3.user_auth_service.dto;

import lombok.Data;

@Data
public class LoginDto {
    private String username;
    private String password;
}