package ru.cubly.iopc.module.websocket;

import lombok.Getter;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.KeyAgreement;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.interfaces.DHPublicKey;
import javax.crypto.spec.DHParameterSpec;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import java.io.IOException;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.KeyFactory;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.NoSuchAlgorithmException;
import java.security.PublicKey;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.X509EncodedKeySpec;
import java.util.UUID;

@Slf4j
public class CryptoUtils {

    private static final int KEY_SIZE = 2048;

    public static class KeyHolder {
        @Getter
        private final UUID keyId = UUID.randomUUID();

        private byte[] sharedKey;

        private byte[] serverDHPublicKey;

        public byte[] getServerDHPublicKeyAndForget() {
            byte[] dhKey = serverDHPublicKey;
            serverDHPublicKey = null;

            return dhKey;
        }
    }

    public static class EncryptedMessage {
        private byte[] payload;
        private byte[] iv;
    }

    @SneakyThrows({NoSuchAlgorithmException.class})
    public static KeyHolder generateSharedKey(byte[] clientPubKeyEnc) throws InvalidKeyException, InvalidKeySpecException, InvalidAlgorithmParameterException {
        KeyFactory serverKeyFactory = KeyFactory.getInstance("DH");
        X509EncodedKeySpec x509KeySpec = new X509EncodedKeySpec(clientPubKeyEnc);

        if (clientPubKeyEnc.length != KEY_SIZE) {
            throw new IllegalArgumentException("Key size must be " + KEY_SIZE);
        }

        PublicKey clientPubKey = serverKeyFactory.generatePublic(x509KeySpec);

        DHParameterSpec dhParamFromClientPubKey = ((DHPublicKey) clientPubKey).getParams();

        KeyPairGenerator serverKpairGen = KeyPairGenerator.getInstance("DH");
        serverKpairGen.initialize(dhParamFromClientPubKey);
        KeyPair serverKPair = serverKpairGen.generateKeyPair();

        KeyAgreement serverKeyAgree = KeyAgreement.getInstance("DH");
        serverKeyAgree.init(serverKPair.getPrivate());

        KeyHolder holder = new KeyHolder();
        holder.serverDHPublicKey = serverKPair.getPublic().getEncoded();
        serverKeyAgree.doPhase(clientPubKey, true);
        holder.sharedKey = serverKeyAgree.generateSecret();

        return holder;
    }

    @SneakyThrows({NoSuchAlgorithmException.class, NoSuchPaddingException.class})
    public static EncryptedMessage encrypt(KeyHolder keyHolder, byte[] payload) throws InvalidKeyException, BadPaddingException, IllegalBlockSizeException, IOException {
        SecretKeySpec sharedKey = new SecretKeySpec(keyHolder.sharedKey, 0, 16, "AES");

        Cipher bobCipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
        bobCipher.init(Cipher.ENCRYPT_MODE, sharedKey);

        EncryptedMessage message = new EncryptedMessage();
        message.payload = bobCipher.doFinal(payload);
        message.iv = bobCipher.getIV();

        return message;
    }

    @SneakyThrows({NoSuchAlgorithmException.class, NoSuchPaddingException.class})
    public static byte[] decrypt(KeyHolder keyHolder, EncryptedMessage message) throws InvalidKeyException, BadPaddingException, IllegalBlockSizeException, IOException, InvalidAlgorithmParameterException {
        SecretKeySpec sharedKey = new SecretKeySpec(keyHolder.sharedKey, 0, 16, "AES");

        IvParameterSpec ivParameterSpec = new IvParameterSpec(message.iv);

        Cipher aliceCipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
        aliceCipher.init(Cipher.DECRYPT_MODE, sharedKey, ivParameterSpec);

        return aliceCipher.doFinal(message.payload);
    }

}
