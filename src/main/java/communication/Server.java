package communication;

import communication.Phone;
import lombok.SneakyThrows;
import protocol.application.AP;
import protocol.application.ControlType;
import protocol.session.SP;
import protocol.transport.EnhancedUDP;
import protocol.transport.TP;

import java.util.Scanner;

public class Server extends Thread {
    private Phone phone = new Phone(this);
    AP aProtocol;
    AP foreingAProtocol;

    Thread fServerListeningThread = new Thread() {
        @SneakyThrows
        public void run() {
            fServerListen();
        }
    };

    public Server(AP client, AP fServer) {

        this.aProtocol = client;
        this.foreingAProtocol = fServer;
    }

    public Phone getPhone() {
        return phone;
    }

    public void startServer() throws Exception {
        this.start();
        fServerListeningThread.start();
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

    private void fServerListen() throws Exception {
        String recMessage;
        while(true) {
            byte[] bytes = foreingAProtocol.recivePacket();
            recMessage = new String(bytes);
            processMessage(recMessage);
        }
    }

    private void processMessage(String message) throws Exception {
        if(message.equals("/CALL")) {
            phone.call();
        }
        if(message.equals("/EXIT")) {
            phone.exit();
        }
        if(message.equals("/NOTIFY_CALL")) {
            phone.ringing();
        }
        if(message.equals("/NOTIFY_ACCEPT_CALL")) {
            phone.setCurrentState(StateType.TALKING);
            sendCommand(ControlType.NOTIFY_ACCEPT_CALL);
        }
        if(phone.getCurrentState() == StateType.TALKING && !message.startsWith("/")) {
            if(message.startsWith("|")) sendMessage(message.substring(1));
            else sendMessageToServ("|" + message);
        }
    }

    private void sendMessage(String message) throws Exception {
        aProtocol.sendMessage(message);
        System.out.println("sent");
    }

    private void sendMessageToServ(String message) throws Exception {
        foreingAProtocol.sendMessage(message);
        System.out.println("sent");
    }

    private void getUserInput() throws Exception {
        Scanner input = new Scanner(System.in);
        String in;
        while (true) {
            in = input.nextLine();
            sendMessage(in);
        }
    }

    protected void sendCommand(ControlType controlType) {
        try {
            aProtocol.sendControlPacket(controlType);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    protected void sendCommandToServ(ControlType controlType) {
        try {
            foreingAProtocol.sendControlPacket(controlType);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
