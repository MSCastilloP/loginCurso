package com.curso.seguridad.Mock;

import com.curso.seguridad.model.NewUser;
import lombok.Data;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Data
@Service
public class ServiceDB {
    List<NewUser> userList = new ArrayList<>();

    public void addUser(NewUser newUser) {
        userList.add(newUser);
    }
    public NewUser getUser(String username ){
        return userList.stream().filter(
                newUser -> newUser.getUsername().equals(username)
        ).findAny().orElse(null);
    }
}
