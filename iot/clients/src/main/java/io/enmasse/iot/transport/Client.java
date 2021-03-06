/*
 * Copyright 2017 Red Hat Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package io.enmasse.iot.transport;

import io.vertx.core.AsyncResult;
import io.vertx.core.Handler;
import io.vertx.core.Vertx;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Properties;

/**
 * Base class for different transport protocol clients (AMQP, MQTT, ...)
 */
public abstract class Client {

    protected final Logger log = LoggerFactory.getLogger(this.getClass());

    protected final String hostname;
    protected final int port;
    protected final String serverCert;
    protected final Vertx vertx;
    protected Handler<MessageDelivery> receivedHandler;

    /**
     * Constructor
     *
     * @param hostname  hostname to connect to
     * @param port  host port to connect to
     * @param serverCert    path to the Server certificate for authentication
     * @param vertx Vert.x instance used for the client
     */
    public Client(String hostname, int port, String serverCert, Vertx vertx) {
        this.hostname = hostname;
        this.port = port;
        this.serverCert = serverCert;
        this.vertx = vertx;
    }

    /**
     * Client initialization
     *
     * @param config    properties bag with client configuration parameters
     */
    public abstract void init(Properties config);

    /**
     * Connect to the remote system
     *
     * @param connectHandler    handler called when connection is established (or not)
     */
    public abstract void connect(Handler<AsyncResult<Client>> connectHandler);

    /**
     * Connect to the remote system with username/password credentials
     *
     * @param username  username
     * @param password  password
     * @param connectHandler    handler called when connection is established (or not)
     */
    public abstract void connect(String username, String password, Handler<AsyncResult<Client>> connectHandler);

    /**
     * Disconnect from the remote system
     */
    public abstract void disconnect();

    /**
     * Send a message to the remote system
     *
     * @param address   address to send the message
     * @param data   data to send
     * @param sendCompletionHandler handler to call on sent completion providing a message identifier
     */
    public abstract void send(String address, byte[] data, Handler<String> sendCompletionHandler);

    /**
     * Send a message to the remote system
     *
     * @param address   address to send the message
     * @param data  data to send
     */
    public void send(String address, byte[] data) {
        this.send(address, data, null);
    }

    /**
     * Subscribe to receive messages
     *
     * @param address   address on which receiving messages
     */
    public abstract void receive(String address);

    /**
     * Set the handler for incoming messages
     *
     * @param handler   handler to call when a message is received
     * @return  current client instance
     */
    public Client receivedHandler(Handler<MessageDelivery> handler) {
        this.receivedHandler = handler;
        return this;
    }

}
