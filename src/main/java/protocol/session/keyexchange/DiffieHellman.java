package protocol.session.keyexchange;

import protocol.transport.hamming.BinaryString;

import java.math.BigInteger;

public class DiffieHellman {
    BigInteger p, g;
    public DiffieHellman(){}

    public void genPrimeAndPrimitiveRoot(){
        this.p = BigInteger.valueOf(new PrimeNumberGen().getPrimeNumber());
        this.g = BigInteger.valueOf(new PrimitiveRootGen(this.p.intValue()).getPr());
    }

    public void setPrimeAndPrimitveRoot(BigInteger p, BigInteger g) {
        this.p = p;
        this.g = g;
    }

    public BigInteger getP() {
        return p;
    }

    public BigInteger getG() {
        return g;
    }

    public BigInteger genPubKey(BigInteger secretKey){
        return this.g.modPow(secretKey, this.p);
    }

    public BigInteger genPremasterkey(BigInteger fPubKey, BigInteger secretKey){
        return fPubKey.modPow(secretKey, this.p);
    }

}
