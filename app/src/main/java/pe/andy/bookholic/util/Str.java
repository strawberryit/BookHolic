package pe.andy.bookholic.util;

import android.text.TextUtils;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.Queue;

public class Str {

    public static String def(String text) {
        return (text != null) ? text : "";
    }

    public static Queue<String> splitToQ(final String str, final String separatorRegex) {

        return new LinkedList<>(
            Arrays.asList(
                TextUtils.split( (str != null) ? str : "",
                        (separatorRegex != null) ? separatorRegex : "")
            )
        );
    }

    public static int extractInt(final String str) {
        if (str == null)
            return 0;

        try {
            return Integer.parseInt(str.replaceAll("\\D*", ""));
        }
        catch (Exception e){
            return 0;
        }
    }

}
