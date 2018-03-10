package com.jmtelfer.varro;

import javax.persistence.*;
import java.io.Serializable;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.Arrays;
import java.util.Random;

@Entity
@NamedQuery(name="findUserCredentials",
            query="SELECT entry FROM JournalEntry entry")
public class UserCredentials implements Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    //Entity fields
    @Column(name = "USERNAME")
    private String userName;

    @Lob
    @Column(name = "PASSWORD")
    private byte[] hashedPassword;

    @Lob
    @Column(name = "SALT")
    private byte[] salt = new byte[32];

    public UserCredentials() {
    }

    public UserCredentials(String userName, String unHashedPassword) {
        this.userName = userName;

        final Random r = new SecureRandom();
        r.nextBytes(salt);

        try {
            MessageDigest md = MessageDigest.getInstance("SHA-512");
            md.update(salt);
            hashedPassword = md.digest(unHashedPassword.getBytes());
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public byte[] getHashedPassword() {
        return hashedPassword;
    }

    public void setHashedPassword(byte[] hashedPassword) {
        this.hashedPassword = hashedPassword;
    }

    public byte[] getSalt() {
        return salt;
    }

    public void setSalt(byte[] salt) {
        this.salt = salt;
    }

    public boolean matches(String username, String password) {
        if(username.equals(this.userName)) {
            MessageDigest md = null;

            try {
                md = MessageDigest.getInstance("SHA-512");
            } catch (NoSuchAlgorithmException e) {
                e.printStackTrace();
            }
            md.update(this.salt);

            if(Arrays.equals(md.digest(password.getBytes()), this.getHashedPassword())) {
                return true;
            }
            return false;
        }
        return false;
    }
}