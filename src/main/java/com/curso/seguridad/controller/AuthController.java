package com.curso.seguridad.controller;

import com.curso.seguridad.Mock.ServiceDB;
import com.curso.seguridad.model.NewUser;
import de.mkammerer.argon2.Argon2;
import de.mkammerer.argon2.Argon2Factory;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import org.springframework.web.util.UriComponentsBuilder;

@Controller
public class AuthController {

    ServiceDB serviceDB;

    public AuthController(ServiceDB serviceDB) {
        this.serviceDB = serviceDB;
    }


    @GetMapping("/login")
    public String login() {
        return "login";  // Vista de login
    }

    @GetMapping("/buscar")
    public String buscar() {
        return "buscarUsuarioId";  // Vista de login
    }
    @GetMapping("/vulnerable")
    public String vulnerable() {
        return "buscarUsuarioIdVulnerable";  // Vista de login
    }

    @GetMapping("/crear-usuario")
    public String crearUsuario(@RequestParam(value = "error", required = false) Boolean error,
                               @RequestParam(value = "nickname", required = false) Boolean nickname,
                               @RequestParam(value = "MensajeError", required = false) String MensajeError,
                               @RequestParam(value = "password", required = false) Boolean password,
                               @RequestParam(value = "confirm-password", required = false) Boolean confirmPassword,
                               Model model) {
        model.addAttribute("error", error);
        model.addAttribute("MensajeError", MensajeError);
        model.addAttribute("nickname", nickname);
        model.addAttribute("password", password);
        model.addAttribute("confirmPassword", confirmPassword);

        return "CrearUsuario";  // Vista de login
    }


    @PostMapping("/crear")
    public String crear(
            @RequestParam(value = "nickname", required = false) String nickname,
            @RequestParam(value = "password", required = false) String password,
            @RequestParam(value = "confirm-password", required = false) String confirmPassword,
            Model model,
            HttpServletRequest request,
            RedirectAttributes ra) {

        model.addAttribute("nickname", nickname);
        model.addAttribute("password", password);
        model.addAttribute("confirmPassword", confirmPassword);

        NewUser newUser = NewUser.builder()
                .username(nickname)
                .FirstPassword(generateArgon2idHash(password))
                .SecondPassword(confirmPassword)
                .build();

        // Verificamos si la contraseña es segura y obtenemos el mensaje de error
        String mensajeError = esSegura(password, confirmPassword);

        if (mensajeError != null) { // Si hay un mensaje de error (es decir, la contraseña no es segura)
            ra.addAttribute("error", true);
            ra.addAttribute("MensajeError", mensajeError); // Mostramos el error específico

            return "redirect:/crear-usuario";  // Redirigimos de nuevo a la vista de registro
        }

        // Si la contraseña es segura, agregamos el nuevo usuario
        serviceDB.addUser(newUser);
        System.out.println(serviceDB.getUserList());

        System.out.println(generateArgon2idHash(password));
        return "login"; // Redirigimos al login
    }


    private static String generateArgon2idHash(String password) {
        Argon2 argon2 = Argon2Factory.create();
        return argon2.hash(2, 65536, 1, password);
    }

    // Función para verificar si la contraseña ingresada coincide con el hash almacenado
    public static boolean verificarContrasena(String password, String hashAlmacenado) {
        Argon2 argon2 = Argon2Factory.create();

        // Verificamos si la contraseña coincide con el hash utilizando la función 'verify'
        return argon2.verify(hashAlmacenado, password.toCharArray());
    }


    public static String esSegura(String password, String password2) {
        if (password.length() <= 8) {
            return "La contraseña debe tener al menos 8 caracteres.";
        }

        boolean mayuscula = false;
        boolean numero = false;
        boolean coincide = password.equals(password2);

        for (int i = 0; i < password.length(); i++) {
            char c = password.charAt(i);
            if (Character.isDigit(c)) {
                numero = true;
            }
            if (Character.isUpperCase(c)) {
                mayuscula = true;
            }
        }

        if (!mayuscula) {
            return "La contraseña debe contener al menos una letra mayúscula.";
        }
        if (!numero) {
            return "La contraseña debe contener al menos un número.";
        }
        if (!coincide) {
            return "Las contraseñas no coinciden.";
        }

        return null; // Si la contraseña es segura, no retornamos ningún mensaje de error.
    }

    @PostMapping("/authenticate")
    public String authenticate(@RequestParam(value = "username") String username,
                               @RequestParam(value = "password") String password,
                               @RequestParam("g-recaptcha-response") String recaptchaResponse,
                               Model model) {

        var user = serviceDB.getUser(username);

        // Verificamos si el usuario existe
        if (user == null) {
            model.addAttribute("error", "Usuario o contraseña incorrectos.");
            return "login";
        }

        // Verificamos si la contraseña ingresada coincide con el hash almacenado
        Argon2 argon2 = Argon2Factory.create();

        // Aquí usamos el hash almacenado en la base de datos (user.getFirstPassword()) y verificamos la contraseña
        boolean esCorrecta = argon2.verify(user.getFirstPassword(), password.toCharArray());

        if (!esCorrecta) {
            model.addAttribute("error", "Usuario o contraseña incorrectos.");
            return "login";
        }

        // Verificamos el CAPTCHA
        if (!validateRecaptcha(recaptchaResponse)) {
            model.addAttribute("error", "Verificación CAPTCHA fallida. Por favor, intente nuevamente.");
            return "login";
        }

        return "home"; // Si todo es correcto, redirigimos al usuario a la página principal
    }


    private boolean validateRecaptcha(String recaptchaResponse) {
        String secretKey = "####"; // Clave secreta de Google reCAPTCHA
        String url = "https://www.google.com/recaptcha/api/siteverify";

        UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl(url)
                .queryParam("secret", secretKey)
                .queryParam("response", recaptchaResponse);

        RestTemplate restTemplate = new RestTemplate();
        ResponseEntity<String> response = restTemplate.postForEntity(builder.toUriString(), null, String.class);

        System.out.println(response.getBody());
        return response.getBody().contains("\"success\": true");
    }

    @GetMapping("/home")
    public String home() {
        return "home";
    }


}
