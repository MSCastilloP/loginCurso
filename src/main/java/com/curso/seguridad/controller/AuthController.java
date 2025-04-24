package com.curso.seguridad.controller;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

@Controller
public class AuthController {

    @Autowired
    private AuthenticationManager authenticationManager;

    @GetMapping("/login")
    public String login() {
        return "login";  // Vista de login
    }


    @PostMapping("/authenticate")
    public String authenticate(@RequestParam("username") String username,
                               @RequestParam("password") String password,
                               @RequestParam("g-recaptcha-response") String recaptchaResponse,
                               HttpServletRequest request,
                               Model model) {


        if (!validateRecaptcha(recaptchaResponse)) {
            model.addAttribute("error", "Verificación CAPTCHA fallida. Por favor, intente nuevamente.");
            return "login";
        }

        try {
            UsernamePasswordAuthenticationToken authenticationToken =
                    new UsernamePasswordAuthenticationToken(username, password);

            Authentication authentication = authenticationManager.authenticate(authenticationToken);

            SecurityContextHolder.getContext().setAuthentication(authentication);

            return "redirect:/home";
        } catch (BadCredentialsException e) {
            model.addAttribute("error", "Usuario o contraseña incorrectos.");
            return "login";
        } catch (Exception e) {
            model.addAttribute("error", "Error de autenticación. Intente nuevamente.");
            return "login";
        }
    }




    private boolean validateRecaptcha(String recaptchaResponse) {
        String secretKey = "6LfVbBErAAAAAI_LyTBKBSTI_PbqnW0ib74oTY7F"; // Clave secreta de Google reCAPTCHA
        String url = "https://www.google.com/recaptcha/api/siteverify";

        UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl(url)
                .queryParam("secret", secretKey)
                .queryParam("response", recaptchaResponse);

        RestTemplate restTemplate = new RestTemplate();
        ResponseEntity<String> response = restTemplate.postForEntity(builder.toUriString(), null, String.class);

        return response.getBody().contains("\"success\": true");
    }

    @GetMapping("/home")
    public String home() {
        return "home";
    }
}
