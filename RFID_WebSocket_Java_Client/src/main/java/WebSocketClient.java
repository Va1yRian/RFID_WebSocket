import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.websocket.*;
import java.io.IOException;
import java.net.URI;

@ClientEndpoint
public class WebSocketClient {

    private Logger logger = LoggerFactory.getLogger(this.getClass());

    private Session session = null;

    private int count = 0;
    private Object Client;

    @OnOpen
    public void onOpen(Session session) {
        System.out.println("Connected to endpoint: " + session.getBasicRemote());
        try {
            session.getBasicRemote().sendText("Start");
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }

    @OnMessage
    public void processMessage(String message) {
        System.out.println("Received message :" + message);
    }

    @OnError
    public void processError(Throwable t) {
        t.printStackTrace();
    }

    public WebSocketClient() {
        super();
    }

    public WebSocketClient(URI endpointURI) {
        super();
        try {
            WebSocketContainer container = ContainerProvider.getWebSocketContainer();	// 获得WebSocketContainer
            this.session = container.connectToServer(WebSocketClient.class, endpointURI);	// 该方法会阻塞
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public Session getSession() {
        return session;
    }

    public void setSession(Session session) {
        this.session = session;
    }


    public void sendMessage(String message){
        try {
            session.getBasicRemote().sendText(message);

        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }finally {
            try {
                this.session.getBasicRemote().flushBatch();
            } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }
    }

}