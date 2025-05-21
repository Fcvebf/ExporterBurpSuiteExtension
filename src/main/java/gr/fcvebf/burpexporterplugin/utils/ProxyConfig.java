package gr.fcvebf.burpexporterplugin.utils;

import java.util.Optional;

public class ProxyConfig {

    // Helper class to encapsulate proxy configuration

    private String host;
    private int port;
    private Optional<String> username = Optional.empty();
    private Optional<String> password = Optional.empty();

    public ProxyConfig(String host, int port) {
        this.host = host;
        this.port = port;
    }

    public ProxyConfig(String host, int port, String username, String password) {
        this.host = host;
        this.port = port;
        this.username = Optional.ofNullable(username);
        this.password = Optional.ofNullable(password);
    }

    public String getHost() {
        return host;
    }

    public int getPort() {
        return port;
    }

    public Optional<String> getUsername() {
        return username;
    }

    public Optional<String> getPassword() {
        return password;
    }
}


