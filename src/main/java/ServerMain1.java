import communication.Server;
import protocol.application.AP;
import protocol.session.SP;
import protocol.transport.EnhancedUDP;
import protocol.transport.TP;

public class ServerMain1 {
    public static void main(String[] args) throws Exception {
        EnhancedUDP udp = new EnhancedUDP(9999, 9998);
        TP tp = new TP(udp);
        SP sp = new SP(tp);
        AP ap = new AP(sp);

        EnhancedUDP udp2 = new EnhancedUDP(9990, 9991);
        TP tp2 = new TP(udp2);
        SP sp2 = new SP(tp2);
        AP ap2 = new AP(sp2);

        Server s = new Server(ap, ap2);
        s.startServer();
    }
}


