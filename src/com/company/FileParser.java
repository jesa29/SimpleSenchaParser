package com.company;

import java.io.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by jesa on 13.06.2016.
 */
public class FileParser {
    private static final String
            extendTemplate = "Ext.extend",
            namespaceTemplate = "Ext.ns",
            callParentTemplate = "superclass.execute.call",
            cregTemplate = "Sigma.creg";

    public static void write(String fileName, String text) {
        //Определяем файл
        File file = new File(fileName);

        try {
            //проверяем, что если файл не существует то создаем его
            if (!file.exists()) {
                file.createNewFile();
            }

            //PrintWriter обеспечит возможности записи в файл
            PrintWriter out = new PrintWriter(file.getAbsoluteFile());

            try {
                //Записываем текст у файл
                out.print(text);
            } finally {
                //После чего мы должны закрыть файл
                //Иначе файл не запишется
                out.close();
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public static String[] read(String fileName) throws FileNotFoundException {
        //Этот спец. объект для построения строки
        StringBuilder sb = new StringBuilder();
        StringBuilder sbFor6 = new StringBuilder();
        File file = new File(fileName);
        file.exists();

        try {
            //Объект для чтения файла в буфер
            BufferedReader in = new BufferedReader(new FileReader(file.getAbsoluteFile()));
            try {
                //В цикле построчно считываем файл
                String s;
                while ((s = in.readLine()) != null) {
                    //System.out.println(s);
                    if (!findSubstring(s, namespaceTemplate) && !findSubstring(s, cregTemplate)) {
                        if (findSubstring(s, extendTemplate)) {
                            sb.append(migrateExtendToDefine(s, false, file.getName().replaceFirst("[.][^.]+$", "")));
                            sb.append("\n");
                            sbFor6.append(migrateExtendToDefine(s, true, file.getName().replaceFirst("[.][^.]+$", "")));
                            sbFor6.append("\n");
                        } else if (findSubstring(s, callParentTemplate)) {
                            sb.append(changeCallParent(s));
                            sb.append("\n");
                            sbFor6.append(changeCallParent(s));
                            sbFor6.append("\n");
                        } else {
                            sb.append(s);
                            sb.append("\n");
                            sbFor6.append(s);
                            sbFor6.append("\n");
                        }
                    }
                }
            } finally {
                //Также не забываем закрыть файл
                in.close();
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        String[] result = new String[2];
        result[0] = sb.toString();
        result[1] = sbFor6.toString();
        //Возвращаем полученный текст с файла
        return result;
    }

    public static String changeCallParent(String s) {
        StringBuilder sb = new StringBuilder();
        sb.append("this.callParent([");
        sb.append((s.substring(s.indexOf(",")+1, s.indexOf(")"))).trim());
        sb.append("]);");
        return sb.toString();
    }

    public static String readForChange(String fileName, String needToReplace, String replaceWithWhat) throws FileNotFoundException {
        //Этот спец. объект для построения строки
        StringBuilder sb = new StringBuilder();

        File file = new File(fileName);
        file.exists();

        try {
            //Объект для чтения файла в буфер
            BufferedReader in = new BufferedReader(new FileReader(file.getAbsoluteFile()));
            try {
                //В цикле построчно считываем файл
                String s;
                while ((s = in.readLine()) != null) {
                    //System.out.println(s);
                    if (findSubstring(s, needToReplace))
                        s = s.replaceAll(needToReplace, replaceWithWhat);
                    sb.append(s);
                    sb.append("\n");
                }
            } finally {
                //Также не забываем закрыть файл
                in.close();
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        String result = new String();
        result = sb.toString();
        //Возвращаем полученный текст с файла
        return result;
    }

    private static String migrateExtendToDefine(String string, boolean mode, String s) {
        StringBuilder sb = new StringBuilder();
        sb.append("Ext.define('Sigma.");
        if (!mode)
            sb.append(Main.js3PackagePath[3]);
        else sb.append(Main.js6PackagePath[5]);
        sb.append(".");
        if (!mode)
            sb.append(Main.js3PackagePath[4]);
        else sb.append(Main.js6PackagePath[6]);
        sb.append(".");
        sb.append(s);
        sb.append("', {\n");
        //extjs6 special section
        sb.append("extend: '");
        sb.append((string.substring(string.indexOf("extend") + 7, string.indexOf("{") - 2)).trim());
        sb.append("',\n");

        sb.append("alias: 'command.");
        sb.append(s);
        sb.append("',\n");

        if (mode) {
            sb.append("alternateClassName: '");
            sb.append((string.substring(string.indexOf("Sigma"), string.indexOf("="))).trim());
            sb.append("',\n");
        }
        return sb.toString();
    }

    private static boolean findSubstring(String currentString, String needToFind) {
        Pattern pattern = Pattern.compile(needToFind, Pattern.COMMENTS);
        Matcher matcher = pattern.matcher(currentString);
        return matcher.find();
    }
}
