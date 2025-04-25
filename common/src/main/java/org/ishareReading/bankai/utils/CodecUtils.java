package org.ishareReading.bankai.utils;

import org.apache.commons.codec.DecoderException;
import org.apache.commons.codec.binary.Base64;
import org.apache.commons.codec.binary.Hex;
import org.apache.commons.codec.net.QuotedPrintableCodec;
import org.apache.commons.codec.net.URLCodec;

import java.io.UnsupportedEncodingException;
import java.nio.charset.StandardCharsets;

/**
 * Apache common-codec 常用
 */
public class CodecUtils {
    private static final URLCodec URL_CODEC = new URLCodec();
    private static final QuotedPrintableCodec QUOTED_PRINTABLE_CODEC = new QuotedPrintableCodec();


    private CodecUtils() {
    }

    /**
     * str 转为 base64
     */
    public static String str2Base64(String str) {
        return new String(Base64.encodeBase64(str.getBytes(StandardCharsets.UTF_8)), StandardCharsets.UTF_8);
    }

    /**
     * base64转为str
     */
    public static String base642Str(String base64Str) {
        return new String(Base64.decodeBase64(base64Str.getBytes(StandardCharsets.UTF_8)), StandardCharsets.UTF_8);
    }

    /**
     * 十六进制 转 字节str
     */
    public static String hex2Str(String hexStr) {
        return new String(Hex.encodeHex(hexStr.getBytes(StandardCharsets.UTF_8)));
    }

    /**
     * 字节str 转 binaryStr
     */
    public static String str2Hex(String str) {
        try {
            return new String(Hex.decodeHex(str.toCharArray()), StandardCharsets.UTF_8);
        } catch (DecoderException e) {
            return null;
        }
    }

    /**
     * url
     */
    public static String urlEncode(String url) {
        try {
            return URL_CODEC.encode(url, "UTF-8");
        } catch (UnsupportedEncodingException e) {
            return null;
        }
    }

    /**
     * url 解码
     */
    public static String urlDecode(String url) {
        try {
            return URL_CODEC.decode(url, "UTF-8");
        } catch (DecoderException | UnsupportedEncodingException e) {
            return null;
        }
    }

    /**
     * str 转 quotedPrintable
     */
    public static String str2quotedPrintable(String str) {
        return QUOTED_PRINTABLE_CODEC.encode(str, StandardCharsets.UTF_8);
    }

    public static byte[] str2quotedPrintable(byte[] bytes) {
        return QUOTED_PRINTABLE_CODEC.encode(bytes);
    }

    /**
     * quotedPrintable 转 str
     */
    public static String quotedPrintable2Str(String quotedPrintable) {
        try {
            return QUOTED_PRINTABLE_CODEC.decode(quotedPrintable, StandardCharsets.UTF_8);
        } catch (DecoderException e) {
            return null;
        }
    }

    public static byte[] quotedPrintable2Str(byte[] bytes) {
        try {
            return QUOTED_PRINTABLE_CODEC.decode(bytes);
        } catch (DecoderException e) {
            return null;
        }
    }

}
