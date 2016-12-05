package redis;

import java.io.Serializable;

public class User implements Serializable {

    private String handle;
    private String password;

    public User() {}

    public String getHandle() { return handle; }

    public User setHandle(String handle) { this.handle = handle; return this; }

    public String getPassword() { return password; }

    public User setPassword(String password) { this.password = password; return this; }
}