package protocol;

import protocol.transport.hamming.BinaryString;
import protocol.transport.hamming.EncodedString;

import java.io.UnsupportedEncodingException;
import java.math.BigInteger;
import java.util.Arrays;
import java.util.List;

public class Helper {
    public static byte[] concatByteArrays(byte[] ar1, byte[] ar2) {
        byte[] res = new byte[ar1.length + ar2.length];
        System.arraycopy(ar1, 0, res, 0, ar1.length);
        System.arraycopy(ar2, 0, res, ar1.length, ar2.length);
        return res;
    }

    public static byte[] concatByteArrays(List<byte[]> arrays) {
        byte[] res = new byte[0];
        for (byte[] array : arrays) {
            res = concatByteArrays(res, array);
        }
        return res;
    }

    public static void printArray(byte[] arr) {
        System.out.println("arr len = " + arr.length);
        for(int i=0; i<arr.length; i++) {
            System.out.print(arr[i] + " ");
        }
        System.out.println();
    }

    public static byte[] subArrayOf(byte[] arr, int b, int e) {
        return Arrays.copyOfRange(arr, b, e);
    }

    public static int[] encodeToBitsArray(byte[] bytes) {
        return convertToBitArray(encodeToBitString(bytes));
    }


    public static String encodeToBitString(byte[] bytes) {
        String convertedBytes = "";
        for(int i=0; i<bytes.length; i++) {
            convertedBytes += encodeToBitString(bytes[i]);
        }
        return convertedBytes;
    }

    public static String encodeToBitString(byte b) {
        String convertedByte = Integer.toBinaryString(b);
        int len = convertedByte.length();
        if(len < 8) {
            for(int i=0; i< 8-len; i++) {
                convertedByte = "0" + convertedByte;
            }
        }
        return convertedByte;
    }

    public static int[] convertToBitArray(String stringBits) {
        int[] bits =new int[stringBits.length()];
        for(int i=0; i<stringBits.length(); i++) {
            bits[i] = Integer.parseInt(String.valueOf(stringBits.charAt(i)));
        }
        return bits;
    }

    public static byte[] decodeBitsToBytes(int[] bits) { // decodes each 8 bits as an utf char and returns its code
        String stringBits = "";
        for(int i=0; i<bits.length; i++) {
            stringBits += bits[i];
        }

        byte[] bytes = new byte[stringBits.length() / 8];

        for(int i=0; i<bytes.length; i++) {
            bytes[i] = Byte.parseByte(stringBits.substring(0, 8), 2);
            stringBits = stringBits.substring(8, stringBits.length());
        }

        return bytes;
    }

    public static String convertToBitString(int[] bits) {
        String convertedBits = Arrays
                .stream(bits)
                .mapToObj(String::valueOf)
                .reduce((a, b) -> a.concat("").concat(b))
                .get();
        return convertedBits;
    }

    public static byte[] encodeBitsAsChar(int[] bits) {
        return convertToBitString(bits).getBytes();
    }

    public static String decodeUTFCodeAsChar(byte[] bytes) {
        try {
            return new String(bytes, "UTF-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static byte[] toBytes(String stringBit) {
        int[] bits = convertToBitArray(stringBit);
        return encodeBitsAsChar(bits);
    }



    public static BinaryString toBinStr(byte[] bytes) {
        return new BinaryString(new BigInteger(bytes).toString(2));
    }

    public static EncodedString toEncBinStr(byte[] bytes) {
        return new EncodedString(new BigInteger(bytes).toString(2));
    }

    public static byte[] toBytes(BinaryString binaryString) {
        return  new BigInteger(binaryString.getValue(), 2).toByteArray();
    }





}
