package com.example.uberapp.Model;

public class User
{
    private String email,password,name;
    private String phone;


    public User() {
        this.email = email;
        this.password = password;
        this.name = name;
        this.phone = phone;
    }

    public User(String name, String email, String number) {
        this.email = email;
        this.name = name;
        this.phone = number;

    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }


}
