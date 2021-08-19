package cn.xanderye.util;

/**
 * @author XanderYe
 * @description:
 * @date 2021/8/19 21:23
 */
public class StringUtil {

    private static final char UNDERLINE = '_';

    public static String camelToUnderline(String param) {
        if (param == null) {
            return null;
        }
        StringBuilder sb = new StringBuilder();
        int len = param.length();
        for (int i = 0; i < len; i++) {
            char c = param.charAt(i);
            if (Character.isUpperCase(c)) {
                sb.append(UNDERLINE);
                sb.append(Character.toLowerCase(c));
            } else {
                sb.append(c);
            }
        }
        return sb.toString();
    }
}
