package communication;

import communication.Phone;
import lombok.SneakyThrows;
import protocol.application.AP;
import protocol.application.ControlType;
import protocol.session.SP;
import protocol.transport.EnhancedUDP;
import protocol.transport.TP;

import java.util.Scanner;

public class Client extends Thread {
    AP aProtocol;

    public Client(AP aProtocol) {
        this.aProtocol = aProtocol;
    }

    public void startClient() throws Exception {
        this.start();
        getUserInput();
    }

    @SneakyThrows
    public void run() {
        listen();
    }

    private void listen() throws Exception {
        String recMessage;
        while(true) {
            byte[] bytes = aProtocol.recivePacket();
            recMessage = new String(bytes);
            processMessage(recMessage);
        }
    }

    private void processMessage(String message) throws Exception {
        if(message.equals("/NOTIFY_CALL")) {
            //sendCommand(ControlType.CALL);
            System.out.println("The phone is ringing");
        }
        if(message.equals("/NOTIFY_ACCEPT_CALL")) {
            System.out.println("User anwered, you can speak know");
        }
    }

    private void sendMessage(String message) throws Exception {
        aProtocol.sendMessage(message);
        System.out.println("sent");
    }

    private void getUserInput() throws Exception {
        Scanner input = new Scanner(System.in);
        String in;
        while (true) {
            in = input.nextLine();

            if(in.startsWith("/")) {
                if(in.equals("/c")) call();
                if(in.equals("/e")) exit();
                continue;
            }

            sendMessage(in);
        }
    }

    private void sendCommand(ControlType controlType) throws Exception {
        aProtocol.sendControlPacket(controlType);
        System.out.println("sent");
    }

    private void call() throws Exception {
        sendCommand(ControlType.CALL);
    }

    private void exit() throws Exception {
        sendCommand(ControlType.EXIT);
    }
}
