package com.curso.seguridad.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
public class NewUser {

    private String username;
    private String FirstPassword;
    private String SecondPassword;

}
