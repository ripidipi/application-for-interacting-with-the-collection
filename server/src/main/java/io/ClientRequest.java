package io;

import java.io.ObjectInputStream;
import java.net.SocketAddress;

public class ClientRequest {
    public SocketAddress address;
    public ObjectInputStream input;

    public ClientRequest(SocketAddress address, ObjectInputStream input) {
        this.address = address;
        this.input = input;
    }
}
