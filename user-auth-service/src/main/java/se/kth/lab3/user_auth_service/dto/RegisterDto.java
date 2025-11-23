package se.kth.lab3.user_auth_service.dto;

import lombok.Data;
import java.util.Set;

@Data
public class RegisterDto {
    private String username;
    private String password;
    private String email;
    private Set<String> roles;
}