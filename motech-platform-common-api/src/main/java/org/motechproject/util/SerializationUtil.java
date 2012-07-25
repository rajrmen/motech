package org.motechproject.util;

import org.apache.commons.codec.binary.Base64;
import org.apache.commons.lang.SerializationUtils;

import java.io.Serializable;

public class SerializationUtil {
    static private Base64 codec = new Base64();

    public static String toString(Serializable obj) {
        if (obj == null) return null;

        byte[] bytes = SerializationUtils.serialize(obj);
        return new String(codec.encode(bytes));
    }

    public static Object toObject(String str) {
        return SerializationUtils.deserialize(codec.decode(str.getBytes()));
    }
}