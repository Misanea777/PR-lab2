package protocol.session;

import protocol.Helper;
import protocol.session.encryption.AESEncryptionManager;
import protocol.session.keyexchange.DiffieHellman;
import protocol.transport.TP;

import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import java.io.IOException;
import java.math.BigInteger;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class SP {
    private TP tProtocol;
    private final int secretKey = new Random().nextInt(100);
    private BigInteger premasterKey = BigInteger.ZERO;
    private DiffieHellman diffieHellman = new DiffieHellman();
    private boolean secureConnection = false;

    public SP(TP tProtocol) {
        this.tProtocol = tProtocol;
    }

    public void sendBytes(byte[] message) throws IOException, InterruptedException {
        tProtocol.sendBytes(message);
    }

    public byte[] reciveBytes() throws Exception {
        byte[] message = tProtocol.reciveBytes();
        return processPacket(message);
    }

    private byte[] processPacket(byte[] mesage) throws Exception {
        if(mesage.length == 0) return new byte[0];

        SPHeader spHeader = new SPHeader(mesage);
        mesage = spHeader.extract(mesage);
//        System.out.println("type = " + spHeader.getType());
//        System.out.println("data len = " + spHeader.getLen());

        if(spHeader.getType() == SPType.HANDSHAKE) {
            processHandshake(mesage);
            return new byte[0];
        }
        if(spHeader.getType() == SPType.DATA && spHeader.getLen() != 0) {
            return processData(mesage, spHeader.getLen());
        }
        return new byte[0];
    }

    private byte[] processData(byte[] message, int len) throws NoSuchPaddingException, InvalidKeyException, NoSuchAlgorithmException, IllegalBlockSizeException, BadPaddingException, InvalidAlgorithmParameterException, InvalidKeySpecException {
        message = Helper.subArrayOf(message, 0, len);
        message = AESEncryptionManager.decryptData(premasterKey.toString(2), message);
        String raw = new String(message);
//        System.out.println(raw);
//        System.out.println();
        return message;
    }

    private void processHandshake(byte[] message) throws Exception {
        HandShakeProtocol handShakeProtocol = new HandShakeProtocol(message);
        message = handShakeProtocol.extract(message);
//        System.out.println("handshake type = " + handShakeProtocol.getType());

        if(handShakeProtocol.getType() == HandshakeType.CHELLO) processClientHello(message);
        if(handShakeProtocol.getType() == HandshakeType.SHELLO) processServerHello(message);
        if(handShakeProtocol.getType() == HandshakeType.CFHAND) processClientFinHand(message);
    }

    private ClientHello processClientHello(byte[] message) throws Exception {
        ClientHello clientHello = new ClientHello(message);
//        System.out.println("random cHello number = " + clientHello.getRandNumber());

//        System.out.println("responding with serverHello");
        serverHello();

        System.out.println();
        return clientHello;
    }

    public ServerHello processServerHello(byte[] message) throws Exception {
        ServerHello serverHello = new ServerHello(message);

//        System.out.println("Generating pubKey");
        DiffieHellman diffieHellman = serverHello.getDiffieHellman();
        BigInteger pubKey = diffieHellman.genPubKey(BigInteger.valueOf(secretKey));
//        System.out.println("Sending pubKey");
        clientFinHandshake(pubKey);
//        System.out.println("Generating premaster");
        premasterKey = diffieHellman.genPremasterkey(serverHello.getPKey(), BigInteger.valueOf(secretKey));
//        System.out.println("Premaster = " + premasterKey);

        System.out.println();
        return serverHello;
    }

    public ClientFinHandshake processClientFinHand(byte[] message) {
        ClientFinHandshake clientFinHandshake = new ClientFinHandshake(message);
//        System.out.println("Generating premaster");
        premasterKey = diffieHellman.genPremasterkey(clientFinHandshake.getPubKey(), BigInteger.valueOf(secretKey));
//        System.out.println("Premaster = " + premasterKey);

        this.secureConnection = true;
        System.out.println();
        return clientFinHandshake;
    }


    public void clientHello() throws Exception {
        byte[] handshake = new HandShakeProtocol(HandshakeType.CHELLO).construct();
        byte[] header = new SPHeader(SPType.HANDSHAKE, handshake.length).contsruct();
        byte[] res = Helper.concatByteArrays(header, handshake);
        sendBytes(res);

//        System.out.println("waitng for response from server");
    }

    public void serverHello() throws Exception {
        byte[] handshake = new HandShakeProtocol(HandshakeType.SHELLO).construct(secretKey, diffieHellman);
        byte[] header = new SPHeader(SPType.HANDSHAKE, handshake.length).contsruct();
        byte[] res = Helper.concatByteArrays(header, handshake);
        sendBytes(res);

//        System.out.println("Waiting for clients pubKey");
    }

    public void clientFinHandshake(BigInteger pubKey) throws Exception {
        byte[] handshake = new HandShakeProtocol(HandshakeType.CFHAND).construct(pubKey);
        byte[] header = new SPHeader(SPType.HANDSHAKE, handshake.length).contsruct();
        byte[] res = Helper.concatByteArrays(header, handshake);
        sendBytes(res);
        this.secureConnection = true;
    }

    public void data(byte[] message) throws Exception {
        if(!this.secureConnection) {
            clientHello();
        }
        while(!this.secureConnection){
            Thread.sleep(10);
        }

        byte[] byteMessage = message;

        byteMessage = AESEncryptionManager.encryptData(premasterKey.toString(2), byteMessage);

        byte[] header = new SPHeader(SPType.DATA, byteMessage.length).contsruct();
        byte[] res = Helper.concatByteArrays(header, byteMessage);
        sendBytes(res);
    }

    public void data(String message) throws Exception {
        data(message.getBytes());
    }
    
}

class SPHeader {
    private SPType spType;
    private int HEADER_LEN;

    public SPHeader(SPType spType, int len) {
        this.spType = spType;
        this.HEADER_LEN = len;
    }

    public SPType getType() {
        return spType;
    }

    public int getLen() {
        return HEADER_LEN;
    }

    public SPHeader(byte[] message) {
        byte[] byteMessage = message;
        if(byteMessage[0] == 0)  this.spType = SPType.DATA;
        if(byteMessage[0] == 1) this.spType =  SPType.HANDSHAKE;
        this.HEADER_LEN = byteMessage[1];
    }

    public byte[] extract(byte[] message) {
        return Helper.subArrayOf(message, 2, message.length);
    }

    byte[] contsruct() {
        byte[] type = new byte[] {2};
        byte[] len = new byte[] {(byte) this.HEADER_LEN};
        if(spType == SPType.DATA) type = new byte[] {0};
        if(spType == SPType.HANDSHAKE) type = new byte[] {1};
        byte[] res = Helper.concatByteArrays(type, len);
        return res;
    }

}

enum SPType {
    HANDSHAKE,
    DATA;
}


class HandShakeProtocol {
    private HandshakeType CONTENT_TYPE;

    public HandShakeProtocol(HandshakeType type) {
        this.CONTENT_TYPE = type;
    }

    public HandShakeProtocol(byte[] message) {
        byte[] byteMessage = message;
        if(byteMessage[0] == 0)  this.CONTENT_TYPE = HandshakeType.CHELLO;
        if(byteMessage[0] == 1)  this.CONTENT_TYPE = HandshakeType.SHELLO;
        if(byteMessage[0] == 2)  this.CONTENT_TYPE = HandshakeType.CFHAND;
    }

    public HandshakeType getType() {
        return this.CONTENT_TYPE;
    }

    public byte[] extract(byte[] message) {
        return Helper.subArrayOf(message, 1, message.length);
    }

    byte[] construct() throws Exception {
        if(CONTENT_TYPE != HandshakeType.CHELLO) throw new Exception("content type must be client hello");
        byte[] ctype = new byte[] {0};
        byte[] clientHello = new ClientHello().construct();
        byte[] res = Helper.concatByteArrays(ctype, clientHello);
        return res;
    }

    byte[] construct(int secretkey, DiffieHellman diffieHellman) throws Exception {
        if(CONTENT_TYPE != HandshakeType.SHELLO) throw new Exception("content type must be server hello");
        byte[] ctype = new byte[] {1};
        byte[] serverHello = new ServerHello(diffieHellman).construct(secretkey);
        byte[] res = Helper.concatByteArrays(ctype, serverHello);
        return res;
    }

    byte[] construct(BigInteger pubKey) throws Exception {
        if(CONTENT_TYPE != HandshakeType.CFHAND) throw new Exception("content type must be client fin hanfshake");
        byte[] ctype = new byte[] {2};
        byte[] clientFinHandsh = ClientFinHandshake.construct(pubKey);
        byte[] res = Helper.concatByteArrays(ctype, clientFinHandsh);
        return res;
    }
}

enum HandshakeType {
    CHELLO,
    SHELLO,
    CFHAND;
}


class ClientHello {
    private int randNumber = new Random().nextInt(100);
    private final int HEADER_LEN = 1;

    public ClientHello(){}

    public ClientHello(byte[] message) {
        byte[] byteMessage = message;
        this.randNumber = byteMessage[0];
    }

    public int getRandNumber() {
        return this.randNumber;
    }

    public byte[] construct() {
        byte[] clientHello = new byte[HEADER_LEN];
        clientHello[0] = (byte) randNumber;
        return clientHello;
    }
}

class ServerHello {
    private DiffieHellman diffieHellman;
    private BigInteger recPubKey;

    public ServerHello(DiffieHellman diffieHellman) {
        this.diffieHellman = diffieHellman;
        this.diffieHellman.genPrimeAndPrimitiveRoot();

    }

    public ServerHello(byte[] message) {

        byte pLen = message[0];
        byte[] p = Helper.subArrayOf(message, 1, pLen+1);
        message = Helper.subArrayOf(message, pLen+1, message.length);
//        System.out.println("p = " + new BigInteger(p));

        byte gLen = message[0];
        byte[] g = Helper.subArrayOf(message, 1, gLen+1);
        message = Helper.subArrayOf(message, gLen+1, message.length);
//        System.out.println("g = " + new BigInteger(g));

        byte pubKeyLen = message[0];
        byte[] pubKey = Helper.subArrayOf(message, 1, pubKeyLen+1);
        message = Helper.subArrayOf(message, pubKeyLen+1, message.length);
//        System.out.println("pKey = " + new BigInteger(pubKey));

        this.diffieHellman = new DiffieHellman();
        diffieHellman.setPrimeAndPrimitveRoot(new BigInteger(p), new BigInteger(g));
        this.recPubKey = new BigInteger(pubKey);
    }

    public BigInteger getPKey() {
        return this.recPubKey;
    }

    public DiffieHellman getDiffieHellman() {
        return this.diffieHellman;
    }

    public byte[] construct(int secretKey) {
        byte[] p = diffieHellman.getP().toByteArray();
        byte[] g = diffieHellman.getG().toByteArray();
        byte[] pubKey = diffieHellman.genPubKey(BigInteger.valueOf(secretKey)).toByteArray();

//        System.out.println("p = " + new BigInteger(p));
//        System.out.println("g = " + new BigInteger(g));
//        System.out.println("pKey = " + new BigInteger(pubKey));

        List<byte[]> genBytes = new ArrayList<>();
        genBytes.add(new byte[] {(byte) p.length}); genBytes.add(p);
        genBytes.add(new byte[] {(byte) g.length}); genBytes.add(g);
        genBytes.add(new byte[] {(byte) pubKey.length}); genBytes.add(pubKey);

        byte[] bytes = Helper.concatByteArrays(genBytes);
        return bytes;
    }
}

class ClientFinHandshake {
    private BigInteger pubKey;

    public ClientFinHandshake(byte[] message) {
        byte len = message[0];
        byte[] pKey = Helper.subArrayOf(message, 1, len+1);
        this.pubKey = new BigInteger(pKey);
    }

    public BigInteger getPubKey() {
        return pubKey;
    }

    public static byte[] construct(BigInteger pubLey) {
        byte[] pKey = pubLey.toByteArray();
        byte[] len = new byte[] {(byte) pKey.length};
        byte[] res = Helper.concatByteArrays(len, pKey);
        return res;
    }
}
