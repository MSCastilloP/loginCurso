package com.curso.seguridad.controller;


import com.curso.seguridad.Service.UserService;
import com.curso.seguridad.model.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RequestMapping("/sql")
@RestController
public class UserController {
    @Autowired
    private UserService userService;

    @GetMapping
    public List<User> getAllUsers() {
        return userService.getAllUsers();
    }


    @PostMapping("/submitForm")
    public String handleSearch(@RequestParam("id") String id, Model model) {
        try {
            Long userId = Long.parseLong(id);
            User user = userService.getUserById(userId);

            if (user != null) {
                model.addAttribute("userForm", user);
            } else {
                model.addAttribute("error", "Usuario no encontrado.");
            }
            return user.toString();

        } catch (NumberFormatException e) {
            model.addAttribute("error", "El ID debe ser un número válido.");
        }

        return null;
    }

    @PostMapping("/vulnerable")
    public String getUserSqlById(@RequestParam String id) {
        return userService.getUserIdSQL(id).toString();
    }

    @PostMapping
    public User createUser(@RequestBody User user) {
        return userService.saveUser(user);
    }

    @DeleteMapping("/{id}")
    public void deleteUser(@PathVariable Long id) {
        userService.deleteUser(id);
    }
}

