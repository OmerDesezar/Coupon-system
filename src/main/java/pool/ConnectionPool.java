package pool;

import java.sql.*;
import java.util.*;

public class ConnectionPool {
    private static ConnectionPool instance = null;

    private Set<Connection> connections;
    private static final int MAX_CONNECTION = 15;
    private static final String URL = "jdbc:mysql://localhost:3306/my_db" + "?user=root"
            + "&password=root" + "&useUnicode=true" + "&useJDBCCompliantTimezoneShift=true"
            + "&useLegacyDatetimeCode=false" + "&serverTimezone=UTC";

    private ConnectionPool() {
        connections = new HashSet<>();
        for (int i = 0; i < MAX_CONNECTION; i++) {
            try {
                Connection connection = DriverManager.getConnection(URL);
                connections.add(connection);
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }

    public synchronized static ConnectionPool getInstance() {
        if (instance == null) {
            instance = new ConnectionPool();
        }
        return instance;
    }

    public synchronized Connection getConnection() {
        while (connections.isEmpty()) {
            try {
                wait();
            } catch (InterruptedException e) {
                System.err.println("Some one interrupted waiting");
            }
        }
        Iterator<Connection> it = connections.iterator();
        Connection connection = it.next();
        it.remove();
        return connection;
    }

    public synchronized void returnConnection(Connection connection) {
        connections.add(connection);
        notifyAll();
    }

    public synchronized void closeAllConnections() {
        int counter = 0;
        while (counter < MAX_CONNECTION) {
            while (connections.isEmpty()) {
                try {
                    wait();
                } catch (InterruptedException e) {
                    System.err.println("Someone interrupt waiting");
                }
            }
            Iterator<Connection> itCon = connections.iterator();
            while (itCon.hasNext()) {
                Connection currentConnection = itCon.next();

                try {
                    currentConnection.close();
                    counter++;
                } catch (SQLException e) {
                    System.err.println("Couldnt close the current connection");
                }
            }
        }
    }
}

