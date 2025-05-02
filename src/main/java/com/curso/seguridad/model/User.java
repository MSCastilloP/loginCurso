package com.curso.seguridad.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Entity
@Table(name="user")
public class User {
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Id
    @Column(name="id_user")
    private Long id_user;
    @Column(name="user_identify")
    private String user_identify;
    @Column(name="state_user")
    private Boolean state_user;
    @Column(name="email")
    private String email;
    @Column(name="area_id")
    private Long area_id;
    @Column(name="job_id")
    private Long job_id;
    @Column(name="role_id")
    private Long role_id;
    @Column(name="created")
    private Date created;

}
