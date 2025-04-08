package com.naix.codeswap.models;

public class User {
    private int id;
    private String username;
    private String email;
    private String firstName;
    private String lastName;

    // Getters y Setters
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    // Añadir el resto de getters y setters

    public String getFullName() {
        if (firstName != null && !firstName.isEmpty() && lastName != null && !lastName.isEmpty()) {
            return firstName + " " + lastName;
        } else if (firstName != null && !firstName.isEmpty()) {
            return firstName;
        } else {
            return username;
        }
    }
}