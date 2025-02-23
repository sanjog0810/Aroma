package com.aroma.aromaBack.services;

import com.aroma.aromaBack.model.User;
import com.aroma.aromaBack.model.UserPrincipal;
import com.aroma.aromaBack.repository.UserRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
public class UserDetailService implements UserDetailsService {

    @Autowired
    private UserRepo repo; // Injects the User Repository

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        User user = repo.findByEmail(email); // Fetch user by email instead of username

        if (user==null) {
            System.out.println("User not found with email: " + email);
            throw new UsernameNotFoundException("User not found with email: " + email);
        }

        return new UserPrincipal(user); // Wrap user in UserPrincipal
    }
}
