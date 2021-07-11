package com.group3s2i.springboot.Controller;

import com.group3s2i.springboot.DAO.CustomerRepository;
import com.group3s2i.springboot.DAO.UserRepository;
import com.group3s2i.springboot.Exceptions.ResourceNotFoundException;
import com.group3s2i.springboot.Model.Customer;
import com.group3s2i.springboot.Model.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;


import java.util.*;

/**
 * @author tfifha youssef
 */
@RestController
@RequestMapping("/api")
public class UserController {
    @Autowired
    UserRepository userRepository;

    @Autowired
    PasswordEncoder encoder;

    @CrossOrigin(origins = "http://localhost:4200")
    @GetMapping("/Users")
    public List<User> getAllUser() {
        System.out.println("Get all User...");

        return new ArrayList<> (userRepository.findAll ());
    }
    @CrossOrigin(origins = "http://localhost:4200")
    @GetMapping("/Users/{id}")
    public ResponseEntity<User> getUserById(@RequestParam(value = "id") Long UtilisateurId)
            throws ResourceNotFoundException {
        User user = userRepository.findById(UtilisateurId)
                .orElseThrow(() -> new ResourceNotFoundException("user not found for this id :: " + UtilisateurId));
        return ResponseEntity.ok().body(user);
    }
    @CrossOrigin(origins = "http://localhost:4200")
    @GetMapping("/Users/{username}")
    public ResponseEntity<User> getUserByUsername(@PathVariable String username){
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException ("User Not Found with username: " + username));

        return ResponseEntity.ok (user);
    }

    @CrossOrigin(origins = "http://localhost:4200")
    @DeleteMapping("/Users/{id}")
    public Map<String, Boolean> deleteUser(@PathVariable(value = "id") Long UtilisateurId)
            throws ResourceNotFoundException {
        User user = userRepository.findById(UtilisateurId)
                .orElseThrow(() -> new ResourceNotFoundException("user not found  id :: " + UtilisateurId));

        userRepository.delete(user);
        Map<String, Boolean> response = new HashMap<> ();
        response.put("deleted", Boolean.TRUE);
        return response;
    }

    @CrossOrigin(origins = "http://localhost:4200")
    @DeleteMapping("/Users/delete")
    public ResponseEntity<String> deleteAllUser() {
        System.out.println("Delete All User...");

        userRepository.deleteAll();

        return new ResponseEntity<>("All User have been deleted!", HttpStatus.OK);
    }



    @CrossOrigin(origins = "http://localhost:4200")
    @PutMapping("/Users/{id}")
    public ResponseEntity<User> updateCustomer(@PathVariable("id") long id, @RequestBody User user) {
        System.out.println("Update User with ID = " + id + "...");

        Optional<User> userOptional = userRepository.findById(id);

        if (userOptional.isPresent()) {
            User utilisateur = userOptional.get();
            utilisateur.setUsername (user.getUsername ());
            utilisateur.setPassword (encoder.encode(user.getPassword()));
            utilisateur.setEmail (user.getEmail ());
            return new ResponseEntity<>(userRepository.save(user), HttpStatus.OK);
        } else {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }
}