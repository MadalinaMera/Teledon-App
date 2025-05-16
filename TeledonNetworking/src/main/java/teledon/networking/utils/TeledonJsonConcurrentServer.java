package teledon.networking.utils;;

import teledon.networking.jsonprotocol.TeledonClientJsonWorker;
import teledon.services.ITeledonServices;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.net.Socket;

public class TeledonJsonConcurrentServer extends AbsConcurrentServer{
    private ITeledonServices teledonServer;
    private static Logger logger = LogManager.getLogger(TeledonJsonConcurrentServer.class);
    public TeledonJsonConcurrentServer(int port, ITeledonServices teledonServer) {
        super(port);
        this.teledonServer = teledonServer;
        logger.info("Chat-ChatJsonConcurrentServer");
    }

    @Override
    protected Thread createWorker(Socket client) {
        TeledonClientJsonWorker worker=new TeledonClientJsonWorker(teledonServer, client);

        Thread tw=new Thread(worker);
        return tw;
    }
}
