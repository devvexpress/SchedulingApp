package org.ftsolutions.schedulingsystem;

import com.google.gson.annotations.Expose;

/**
 * Created by Andrew on 08/20/2017.
 */

public class LoginDetails{


    @Expose

    public String FirstName;

    @Expose
    public String LastName;

    @Expose
    public int role;

    @Expose
    public int active;


    public String username;
    public String password;

    @Expose
    public String status;

    public LoginDetails(String status, String FirstName, String LastName, int role, int active){
        this.status=status;
        this.FirstName =FirstName;
        this.LastName =LastName;
        this.role=role;
        this.active=active;
    }

    public LoginDetails(){

    }
}