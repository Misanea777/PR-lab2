import communication.Client;
import protocol.application.AP;
import protocol.session.SP;
import protocol.transport.EnhancedUDP;
import protocol.transport.TP;

public class ClientMain1 {
    public static void main(String[] args) throws Exception {
        EnhancedUDP udp = new EnhancedUDP(9998, 9999);
        TP tp = new TP(udp);
        SP sp = new SP(tp);
        AP ap = new AP(sp);

        Client c = new Client(ap);
        c.startClient();
    }
}



