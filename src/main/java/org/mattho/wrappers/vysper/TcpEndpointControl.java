package org.mattho.wrappers.vysper;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class TcpEndpointControl {

    @Autowired
    WrappedXMPPServer xmppServer;

    @GetMapping("/stop-tcp")
    public String  stopTcpEndpoint() {
        xmppServer.stopXmppEndpoint();
        return "stopped";
    }

    @GetMapping("/start-tcp")
    public String startTcpEndpoint() throws Exception {
        xmppServer.startXmppEndpoint();
        return "started";
    }
}
