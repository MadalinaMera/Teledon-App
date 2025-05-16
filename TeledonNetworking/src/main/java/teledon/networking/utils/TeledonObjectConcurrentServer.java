package teledon.networking.utils;


import teledon.networking.objectProtocol.TeledonClientObjectWorker;
import teledon.services.ITeledonServices;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import teledon.networking.objectProtocol.TeledonClientObjectWorker;
import teledon.services.ITeledonServices;

import java.net.Socket;


public class TeledonObjectConcurrentServer extends AbsConcurrentServer {

    private ITeledonServices teledonServer;

    private static Logger logger = LogManager.getLogger(TeledonObjectConcurrentServer.class);

    public TeledonObjectConcurrentServer(int port, ITeledonServices teledonServer) {
        super(port);
        this.teledonServer = teledonServer;
        logger.info("Chat-ChatObjectConcurrentServer");
    }

    @Override
    protected Thread createWorker(Socket client) {
        TeledonClientObjectWorker worker=new TeledonClientObjectWorker(teledonServer, client);
        Thread tw=new Thread(worker);
        return tw;
    }
}
