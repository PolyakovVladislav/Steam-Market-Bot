package com.example.steammarketbot.core;

import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

//Strings v0.5

public class Strings {

    public static ArrayList<String> search(String target, String start, String end, boolean include, boolean onlyFirst) {

        return privateSearch(target, start, end, include, onlyFirst);
    }

    public static String search(String target, String start, String end, boolean include) {
        ArrayList<String> al = privateSearch(target, start, end, include, true);
        if (al.size() < 1)
            return "";
        else
            return al.get(0);
    }

    public static String search(String target, String start, String end) {
        ArrayList<String> al = privateSearch(target, start, end, false, true);
        if (al.size() < 1)
            return "";
        else
            return al.get(0);
    }

    private static ArrayList<String> privateSearch(String target, String begin, String end, boolean include, boolean onlyFirst) {
        ArrayList<String> list = new ArrayList<>();
        String mBegin = begin;
        String mEnd = end;
        String startTag = "mStartTag";
        if (begin.contains("^")) {
            begin = begin.replace("^", startTag);
            mBegin = begin;
            target = startTag + target;
        }
        String endTag = "mEndTag";
        if (end.contains("$")) {
            end = end.replace("$", endTag);
            mEnd = end;
            target = target + endTag;
        }
        Pattern pattern = Pattern.compile(mBegin + ".*?" + mEnd);
        target = target
                .replaceAll("\\t", "\\\\t")
                .replaceAll("\\n", "\\\\n")
                .replaceAll("\\r", "\\\\r");
        Matcher matcher = pattern.matcher(target);
        String result;
        while (matcher.find()) {
            if (include) {
                result = target.substring(matcher.start(), matcher.end());
            }
            else {
                result = matcher.group();
                result = result.replaceFirst(mBegin, "");
                result = result.replaceFirst(mEnd, "");
//                result = result.substring(begin.length(), result.length()-end.length());
            }
            result = result.replace(startTag, "");
            result = result.replace(endTag, "");
            list.add(result);
            if (onlyFirst)
                break;
        }
        return list;
    }

    /**
     * ^ - начало строки
     $ - конец строки
     . - любой символ
     \s - пробел
     \d - любая цифра [0-9]
     \D - любой не цифровой символ
     [0-1], [123] - любой из группы симвлолов
     [^0-1] - любой кроме перечисленных
     \n - начало новой строки
     \\ - убирает специальное значение спец символов (^, $ и т.д.)
     Квантификаторы:
     * - ноль или более
     + - один или более
     ? - ноль или один
     {n} - n раз
     {n,m} - от n до m раз
     ".+" - жадный режим
     ".++" - сверхжадный режим
     ".+?" - ленивый режим
     */

    public static boolean find(String target, String mask) {
        Pattern pattern = Pattern.compile(mask);
        Matcher matcher = pattern.matcher(target);
        return matcher.find();
    }

    public static boolean stringMask(String target, String mask) {
        Pattern pattern = Pattern.compile(mask);
        Matcher matcher = pattern.matcher(target);
        return matcher.matches();
    }

    public static String stringPartLeft (String target, String delimiter) {
        String[] words = privateStringPart(target, delimiter);
        return words[0];
    }

    public static String stringPartRight(String target, String delimiter) {
        String[] words = privateStringPart(target, delimiter);
        if (words.length < 2)
            return "";
        String word = words[1];
        for (int i = 2; words.length > i; i++)
            word = word + delimiter + words[i];
        return word;
    }

    public static String[] stringPart(String target, String delimiter) {
        return privateStringPart(target, delimiter);
    }

    private static String[] privateStringPart(String target, String delimiter) {
        Pattern pattern = Pattern.compile(delimiter);
        return pattern.split(target);
    }

    /*public static String prepareForPattern(String mask) {
        String fixedMask = mask;
        String[] meta = new String[] {"(", ")", "[", "]", "{", "}", "?", ".", "+", "*", "^", "$"};
        int i = 0;
        do {
            for (String s : meta) {
                if (fixedMask.substring(i, i + 1).equals(s)) {
                    if (i >= 1 && !fixedMask.substring(i - 1, i).equals("\\"))
                        fixedMask = fixedMask.substring(0, i)
                                + fixedMask.substring(i, i + 1).replace(s, "\\" + s)
                                + fixedMask.substring(i + 1);
                    else if (i >= 2 && fixedMask.substring(i - 2, i).equals("\\\\"))
                        fixedMask = fixedMask.substring(0, i)
                                + fixedMask.substring(i, i + 1).replace(s, "\\" + s)
                                + fixedMask.substring(i + 1);
                    else if (i < 1)
                        fixedMask = fixedMask.substring(i, i + 1).replace(s, "\\" + s)
                                + fixedMask.substring(i + 1);
                }
            }
            i++;
        } while (i < fixedMask.length());
        fixedMask = fixedMask.replace(" ", "\\s");
        return fixedMask;
    }*/
}
