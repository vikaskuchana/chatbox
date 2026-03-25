package com.chatgram.app.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table( name = "user_name")
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Data
public class User {

    @Id
    @GeneratedValue(strategy=GenerationType.IDENTITY)
    @Column(name="id")
    private int id;

    @Column(name = "user_name")
    private String username;

    @Column(name = "password")
    private String password;

    @Column(name = "email")
    private String email;

    @Column(name = "password_hash")
    private String passwordHash;

    @Column(name = "displayName")
    private String displayName;

    @Column
    private String profilePictureUrl;
}
