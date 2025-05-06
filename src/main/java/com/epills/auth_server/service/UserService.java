package com.epills.auth_server.service;

import com.epills.auth_server.dtos.RegistrationUserDTO;
import com.epills.auth_server.entities.User;
import com.epills.auth_server.repositories.RoleRepository;
import com.epills.auth_server.repositories.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;


@Service
@RequiredArgsConstructor
public class UserService implements UserDetailsService {
    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;

    public Optional<User> findByUsername(String username) {
        return userRepository.findByUsername(username);
    }

//    public Collection<? extends GrantedAuthority> getAuthorities(String role) {
//        List<GrantedAuthority> list = new ArrayList<GrantedAuthority>();
//
//        list.add(new SimpleGrantedAuthority(role));
//
//        return list;
//    }
    
    @Override
    @Transactional
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        User user = userRepository.findByUsername(username).orElseThrow(() -> new UsernameNotFoundException(String.format("User %s not found", username)));
        return new org.springframework.security.core.userdetails.User(
                user.getUsername(),
                user.getPassword(),
                user.getRoles().stream().map(role -> new SimpleGrantedAuthority(role.getName())).collect(Collectors.toList())
        );
    }

    public User createUser(RegistrationUserDTO registrationUserDTO) {
        User newUser = new User();
        newUser.setUsername(registrationUserDTO.getUsername());
        newUser.setPassword(passwordEncoder.encode(registrationUserDTO.getPassword()));
        newUser.setEmail(registrationUserDTO.getEmail());
        newUser.setRoles(List.of(roleRepository.findByName("ROLE_USER").get()));
        return userRepository.save(newUser);
    }
}
