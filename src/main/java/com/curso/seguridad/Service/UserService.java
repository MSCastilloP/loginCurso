package com.curso.seguridad.Service;

import com.curso.seguridad.model.User;
import com.curso.seguridad.repositories.UserRepository;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.sql.*;
import java.util.List;

@Service
public class UserService {

    private String host = "#";
    private String userName = "#";
    private String password = "#";


    @PersistenceContext
    private EntityManager entityManager;

    @Autowired
    private UserRepository userRepository;

    public Connection connection() throws SQLException {
        return DriverManager.getConnection(host, userName, password);
    }


    public List<User> getAllUsers() {
        return userRepository.findAll();
    }

    public User getUserById(Long id) {
        return userRepository.findById(id).orElse(null);
    }

    public User saveUser(User user) {
        return userRepository.save(user);

    }


    public String getUserIdSQL(String idUser) {

        String query = "SELECT * FROM user as u WHERE u.user_identify = "+idUser;
        Connection connection = null;
        StringBuilder prueba= new StringBuilder();
        try {
            connection = connection();
            Statement statement = connection.createStatement();
            ResultSet resultSet = statement.executeQuery(query);

            while (resultSet.next()) {
                prueba.append(resultSet.getString("user_identify"));
                System.out.println(resultSet.getString("user_identify"));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        System.out.println(query);
        return prueba.toString();
    }

    public void deleteUser(Long id) {
        userRepository.deleteById(id);
    }
}
