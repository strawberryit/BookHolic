package pe.andy.bookholic.util;

import org.apache.commons.lang3.StringUtils;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

public class EncodeUtil {
    public static String toEuckr(String text){
        try {
            return StringUtils.replaceAll(
                    URLEncoder.encode(text, "EUC-KR"), "%25", "%");
        } catch (UnsupportedEncodingException e){
            e.printStackTrace();
            return "";
        }
    }
}
