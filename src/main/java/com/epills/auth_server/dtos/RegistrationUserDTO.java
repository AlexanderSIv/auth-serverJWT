package com.epills.auth_server.dtos;

import lombok.Data;

@Data
public class RegistrationUserDTO {
    private String username;
    private String password;
    private String confirmPassword;
    private String email;
}
