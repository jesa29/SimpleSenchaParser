package com.company;


import java.io.File;
import java.io.FileNotFoundException;

public class Main {

    final public static String[] js3PackagePath = {"D:\\work\\swe\\src\\main\\webapp", "js", "Sigma", "commands", "workflow"};
    final public static String[] js6PackagePath = {"D:\\work\\swe\\src\\main\\webapp", "packages", "local", "Sigma", "src", "command", "workflow"};

    public static void migrateFiles(StringBuilder stringBuilder1, StringBuilder stringBuilderOut1) throws FileNotFoundException {
        String[] resultText = FileParser.read((stringBuilder1).toString());
        FileParser.write((stringBuilder1).toString(), resultText[0]);
        FileParser.write((stringBuilderOut1).toString(), resultText[1]);
    }

    public static void changeSomething(StringBuilder stringBuilder1, StringBuilder stringBuilderOut1) throws FileNotFoundException {
        String resultText = FileParser.readForChange((stringBuilderOut1).toString(), "\\)\\]", "\\]\\)");
        //FileParser.write((stringBuilder1).toString(), resultText);
        FileParser.write((stringBuilderOut1).toString(), resultText);
    }

    public static void main(String[] args) throws FileNotFoundException {
        StringBuilder stringBuilder = new StringBuilder();
        StringBuilder stringBuilderOut = new StringBuilder();

        for (int i = 0; i < js3PackagePath.length; i++) {
            stringBuilder.append(js3PackagePath[i]);
            stringBuilder.append("\\");
        }
        for (int i = 0; i < js6PackagePath.length; i++) {
            stringBuilderOut.append(js6PackagePath[i]);
            stringBuilderOut.append("\\");
        }
        File[] files = new File(String.valueOf(stringBuilder)).listFiles();
        for (File file : files) {
            if (file.isFile()) {
                StringBuilder stringBuilder1 = new StringBuilder();
                StringBuilder stringBuilderOut1 = new StringBuilder();
                stringBuilder1.append(stringBuilder.toString());
                stringBuilderOut1.append(stringBuilderOut.toString());
                stringBuilder1.append(file.getName());
                stringBuilderOut1.append(file.getName());
                migrateFiles(stringBuilder1, stringBuilderOut1);
                //changeSomething(stringBuilder1, stringBuilderOut1);
            }
        }

        //System.out.println(FileParser.changeCallParent("Sigma.commands.workflow.WorkFlowEventTypeDeleteCommand.superclass.execute.call(this, data);"));
        //System.out.println(FileParser.read(startPath)[1]);
        //System.out.println(files);
    }
}
