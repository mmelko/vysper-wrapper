package org.mattho.wrappers.vysper;

import org.apache.camel.util.ObjectHelper;
import org.apache.vysper.mina.TCPEndpoint;
import org.apache.vysper.storage.StorageProviderRegistry;
import org.apache.vysper.storage.inmemory.MemoryStorageProviderRegistry;
import org.apache.vysper.xmpp.addressing.Entity;
import org.apache.vysper.xmpp.addressing.EntityImpl;
import org.apache.vysper.xmpp.authorization.AccountManagement;
import org.apache.vysper.xmpp.authorization.Anonymous;
import org.apache.vysper.xmpp.authorization.SASLMechanism;
import org.apache.vysper.xmpp.modules.extension.xep0045_muc.MUCModule;
import org.apache.vysper.xmpp.modules.extension.xep0045_muc.model.Conference;
import org.apache.vysper.xmpp.modules.extension.xep0045_muc.model.RoomType;
import org.apache.vysper.xmpp.server.XMPPServer;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;


import java.io.InputStream;
import java.util.Arrays;


@Component
public class WrappedXMPPServer {
    private XMPPServer xmppServer;
    private TCPEndpoint endpoint;
    private int port = 5222;

    public WrappedXMPPServer() {
        initializeXmppServer();
    }


    private void initializeXmppServer() {
        try {
            xmppServer = new XMPPServer("apache.camel");

            StorageProviderRegistry providerRegistry = new MemoryStorageProviderRegistry();
            AccountManagement accountManagement = (AccountManagement) providerRegistry.retrieve(AccountManagement.class);

            Entity user = EntityImpl.parseUnchecked("camel_consumer@apache.camel");
            accountManagement.addUser(user, "secret");

            Entity user2 = EntityImpl.parseUnchecked("camel_producer@apache.camel");
            accountManagement.addUser(user2, "secret");

            Entity user3 = EntityImpl.parseUnchecked("camel_producer1@apache.camel");
            accountManagement.addUser(user3, "secret");

            xmppServer.setStorageProviderRegistry(providerRegistry);

            endpoint = new TCPEndpoint();

            endpoint.setPort(port);

            xmppServer.addEndpoint(endpoint);

            InputStream stream = ObjectHelper.loadResourceAsStream("bogus_mina_tls.cert");
            xmppServer.setTLSCertificateInfo(stream, "boguspw");

            // allow anonymous logins
            xmppServer.setSASLMechanisms(Arrays.asList(new SASLMechanism[] { new Anonymous() }));

            xmppServer.start();

            // add the multi-user chat module and create a few test rooms
            Conference conference = new Conference("test conference");
            conference.createRoom(EntityImpl.parseUnchecked("camel-anon@apache.camel"), "camel-anon", RoomType.FullyAnonymous);
            conference.createRoom(EntityImpl.parseUnchecked("camel-test@apache.camel"), "camel-test", RoomType.Public);
            xmppServer.addModule(new MUCModule("conference", conference));
         //   endpoint.start();
        } catch (Exception e) {
            throw new RuntimeException("An error occurred when initializing the XMPP Test Server.", e);
        }
    }

    public void startXmppEndpoint() throws Exception {
        endpoint.start();
    }

    public void stopXmppEndpoint() {
        endpoint.stop();
    }

    public int getXmppPort() {
        return port;
    }

    public void stop() {
        xmppServer.stop();
    }
}
