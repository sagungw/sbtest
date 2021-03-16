package io.wijaya.external.stockbit_03;

public class App {

    public static void main(String[] args) {
        String s = findFirstStringInBracket("findFirstStringInBracket(String s) { return new String(\"findFirstStringInBracket\") }");
        System.out.println(s);
    }

    public static String findFirstStringInBracket(String s) {
        if (s.length() <= 0)
            return "";

        int index = s.indexOf("(");
        if (index < 0) {
            return "";
        }

        s = s.substring(index);
        index = s.indexOf(")");
        if (index < 0) {
            return "";
        }

        return s.substring(1,index);
    }

}
