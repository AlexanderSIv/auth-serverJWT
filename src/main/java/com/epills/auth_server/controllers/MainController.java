package com.epills.auth_server.controllers;


import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.security.Principal;

@RestController
@RequiredArgsConstructor
public class MainController {
    @GetMapping("/unsecured")
    public String unsecuredData() {
        return "unsecured";
    }
    @GetMapping("/secured")
    public String securedData() {
        return "secured";
    }
    @GetMapping("/admin")
    public String adminData() {
        return "for administrator only";
    }
    @GetMapping("/info")
    public String infoData(Principal principal) {
        return principal.getName();
    }
}
