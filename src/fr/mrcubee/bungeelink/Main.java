package fr.mrcubee.bungeelink;

import fr.mrcubee.bungeelink.security.KeyUtils;

import java.io.DataOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.security.Key;
import java.security.KeyPair;

public class Main {

    private static void saveKeyToFile(String fileName, Key key) {
        File file = new File(fileName);
        DataOutputStream dataOutputStream;

        if (file.exists())
            file.delete();
        try {
            file.createNewFile();
            dataOutputStream = new DataOutputStream(new FileOutputStream(file));
            dataOutputStream.write(key.getEncoded());
            dataOutputStream.flush();
            dataOutputStream.close();
        } catch (IOException ignored) {}
    }

    public static void main(String[] args) {
        KeyPair keyPair;

        if (args.length < 1) {
            System.err.println("Please specify the size of the key you want to generate.");
            return;
        }
        keyPair = KeyUtils.createKeyPair(Integer.parseInt(args[0]));
        if (keyPair == null)
            return;
        saveKeyToFile("public.spki", keyPair.getPublic());
        saveKeyToFile("private.p8", keyPair.getPrivate());
        System.out.println("Private Key: private.p8\nPublic Key: public.spki\nKeys are generated !");
    }

}
