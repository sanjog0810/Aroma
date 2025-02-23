package com.aroma.aromaBack.controller;

import com.aroma.aromaBack.model.Product;
import com.aroma.aromaBack.model.User;
import com.aroma.aromaBack.repository.ProductRepo;
import com.aroma.aromaBack.repository.UserRepo;
import com.aroma.aromaBack.services.JwtService;
import com.aroma.aromaBack.services.UserDetailService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.Optional;

@RestController
@CrossOrigin
public class SecCtrl {
    @Autowired
    private ProductRepo productRepo;


    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private JwtService jwtService;

    @Autowired
    private UserDetailService userDetailService;

    @Autowired
    private UserRepo userRepo;

    @Autowired
    private BCryptPasswordEncoder bCryptPasswordEncoder; // ✅ Uses strength 10 from SecurityConfig

    @PostMapping("/login")
    public String login(@RequestBody User user) {
        System.out.println("Login called");

        // 🔹 Check if user exists in DB
        User dbUser = userRepo.findByEmail(user.getEmail());
        if (dbUser==null) {
            System.out.println("User not found");
            return "User not found";
        }

        // 🔹 Authenticate using Spring Security
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(user.getEmail(), user.getPassword()));

        if (authentication.isAuthenticated()) {
            return jwtService.generateToken(user.getEmail());
        } else {
            System.out.println("Authentication failed");
            return "Authentication failed";
        }
    }

    @PostMapping("/register")
    public String register(@RequestBody User user) {
        // 🔹 Check if email already exists

        // 🔹 Hash the password before saving (using strength 10)
        user.setPassword(bCryptPasswordEncoder.encode(user.getPassword()));
        userRepo.save(user);
        return "User registered successfully";
    }
    @GetMapping("/validate-token")
    public boolean validate(@RequestHeader("Authorization") String authorizationHeader){
        System.out.println("validating");
        if(authorizationHeader==null || !authorizationHeader.startsWith("Bearer ")){
            return false;
        }
        String token = authorizationHeader.substring(7);
        String username=  jwtService.extractUsername(token);
        UserDetails userDetails = userDetailService.loadUserByUsername(username);
        return jwtService.validateToken(token,userDetails);

    }
    @PostMapping("/addProduct")
    public void addProduct(@RequestPart Product product,
                           @RequestPart MultipartFile image) throws IOException {
        product.setImgData(image.getBytes());
        product.setImgName(image.getOriginalFilename());
        product.setImgType(image.getContentType());
        productRepo.save(product);
    }
    @GetMapping("/products")
    public List<Product> getAllProducts(){
        return productRepo.findAll();
    }
    @GetMapping("/products/{id}")
    public Product getProduct(@PathVariable int id){
        return productRepo.findById(id);
    }
    @GetMapping("/products/{id}/image")
    public byte[] getProductImage(@PathVariable int id){
        return productRepo.findById(id).getImgData();
    }
    @GetMapping("/user")
    public User getUser(@RequestHeader("Authorization") String authorizationHeader){
        String token = authorizationHeader.substring(7);
        String username=  jwtService.extractUsername(token);
        return userRepo.findByEmail(username);
    }
    @GetMapping("/role")
    public User getUserRole(@RequestHeader("Authorization") String authorizationHeader){
        String token = authorizationHeader.substring(7);
        String username=  jwtService.extractUsername(token);
        return userRepo.findByEmail(username);
    }
    @DeleteMapping("/products/{id}")
    public void deleteProduct(@PathVariable int id){
        productRepo.deleteById(id);
    }
    @PutMapping("/products/{id}")
    public void updateProduct(@PathVariable int id,@RequestPart Product product,
                           @RequestPart MultipartFile image) throws IOException {
        product.setId(id);
        product.setName(product.getName());
        product.setDescription(product.getDescription());
        product.setPrice(product.getPrice());
        if(image!=null){
            product.setImgData(image.getBytes());
            product.setImgName(image.getOriginalFilename());
            product.setImgType(image.getContentType());
        }
        productRepo.save(product);
    }
    @PutMapping("/productsNoImg/{id}")
    public void updateProduct(@PathVariable int id,@RequestBody Product product) throws IOException {
        product.setId(id);
        product.setName(product.getName());
        product.setDescription(product.getDescription());
        product.setPrice(product.getPrice());
        productRepo.save(product);
    }

}
