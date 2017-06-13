package com.christinequintana.hw;

import java.io.File;
import java.util.List;
import java.util.Arrays;
import java.util.HashMap;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.nio.file.Files;
import java.io.IOException;
import java.nio.charset.Charset;

public class App {

    /**
    * Creates the class and any lambda terms defined by the user. 
    *
    * @param  name  a name of the class
    * @param  exprs a list of all of the user-defined lambda terms
    * @return       a HashMap of name and a string definition of the class
    */
    public static HashMap<String, List<String>> makeClass(String name, List<String> exprs) {

        HashMap<String, List<String>> definitions = new HashMap<String, List<String>>(1);
        List<String> classDefs = new ArrayList<String>();

        String imports = "import java.util.function.Function;\n\nclass " + name + " {";
        classDefs.add(imports);

        for (String expr: exprs) {
            // Split lambda assignment on = 
            String[] tokens = expr.split("[=]+");
            String exprName = tokens[0].trim();
            String exprLambda = tokens[1].trim();

            // Split lambda expression on λ_.
            String[] lambdaExpr = exprLambda.split("(?<=λ\\p{Lower}\\.)");

            String sApplyChain = "";
            List<String> bindings = new ArrayList<String>();

            // Separate bindings and variables
            for (String s: lambdaExpr) {
                if (s.contains("λ")) {
                    char sChar = s.charAt(1);
                    bindings.add(String.valueOf(sChar));
                } else {
                    // Check for internal parentheses
                    if (s.contains("(")) {
                        s = s.replace("(", "Lib.applyChain(");
                    }
                    String[] vars = s.split("[ ]+");
                    sApplyChain = String.join(", ", vars);
                }
            }

            String applyMethod = "";
            String applyChainStr = "\nreturn Lib.applyChain(" + sApplyChain + ");";

            // Iterate through bindings. Each binding results in a return statement.
            int depth = 0;
            for (String s: bindings) {
                if (bindings.indexOf(s)!=0) {
                    applyMethod = applyMethod + "\nreturn (Function) (" + s + ") -> {";
                    depth++;
                } else {
                    applyMethod = applyMethod + "\t\tpublic Function apply(Function " + s + ") {";
                }  
            }
            applyMethod = applyMethod + applyChainStr;
            // Add correct number of closing brackets
            for (int i=0; i < depth; i++) {
                applyMethod = applyMethod + "\n};";
            }
            applyMethod = applyMethod + "\n\t\t}\n\n";

            // Class methods
            String toStringMethod = "\t\t@Override\n\t\tpublic String toString() {\n\t\t\treturn \"" + name + "." + exprName + "\";\n\t\t}\n\n";
            String toLambdaCalcMethod = "\t\tpublic String toLambdaCalc() {\n\t\t\treturn \"" + exprLambda + "\";\n\t\t}\n";

            // Class declarations
            String newImplement = "\tpublic static " + name + "." + exprName + " "+ exprName + " = new " + name + "." + exprName + "();\n";
            String newClass = "\tpublic static class " + exprName + " implements Function<Function, Function> {\n" + applyMethod + toStringMethod + toLambdaCalcMethod + "\t}";

            String classDef = newImplement + newClass + "\n";
            classDefs.add(classDef);
        }
        classDefs.add("}");
        definitions.put(name, classDefs);
        return definitions;
    }

    /**
    * Creates the variables class defined by the user. 
    *
    * @param  name  a name of the class
    * @param  exprs a list of all of the user-defined variables
    * @return       a HashMap of name and a string definition of the class
    */
    public static HashMap<String, List<String>> makeVars(String name, List<String> exprs) {

        HashMap<String, List<String>> definitions = new HashMap<String, List<String>>(1);
        List<String> classDefs = new ArrayList<String>();

        String imports = "import java.util.function.Function;\n\nclass " + name + " {";
        classDefs.add(imports);

        for (String expr: exprs) {


            String applyMethod = "\t\tpublic Function apply(Function " + expr + ") {";
            applyMethod = applyMethod + "\n\t\t\treturn " + expr + ";";
            applyMethod = applyMethod + "\n\t\t}\n\n";

            // Class methods
            String toStringMethod = "\t\t@Override\n\t\tpublic String toString() {\n\t\t\treturn \"" + name + "." + expr + "\";\n\t\t}\n\n";
            String toLambdaCalcMethod = "\t\tpublic String toLambdaCalc() {\n\t\t\treturn \"" + expr + "\";\n\t\t}\n";

            // Class declarations
            String newImplement = "\tpublic static " + name + "." + expr + " "+ expr + " = new " + name + "." + expr + "();\n";
            String newClass = "\tpublic static class " + expr + " implements Function<Function, Function> {\n" + applyMethod + toStringMethod + toLambdaCalcMethod + "\t}";

            String classDef = newImplement + newClass + "\n";
            classDefs.add(classDef);
        }
        classDefs.add("}");
        definitions.put(name, classDefs);
        return definitions;
    }

    /**
    * Creates the pre-defined lib helper class for evaluating lambda expressions.
    *
    * @return  a HashMap of name and a string definition of the class
    */
    public static HashMap<String, List<String>> makeLib() {

        HashMap<String, List<String>> definition = new HashMap<String, List<String>>(1);
        List<String> classDefs = new ArrayList<String>();

        String importDef = "import java.util.function.Function;\n\nclass Lib {\n";
        String docString = "\t// Apply a chain of functions, while casting to Function interface.\n";
        String funcDef = "\tpublic static Function applyChain(Object... fs) {\n\t\tFunction<Function, Function> tmp = null;\n";
        String forDef = "\t\tfor (Object o : fs) {\n\t\t\tFunction<Function, Function> f = (Function<Function, Function>) o;\n";
        String ifDef = "\t\t\tif (tmp == null) {\n\t\t\t\ttmp = f;\n\t\t\t} else {\n\t\t\t\ttmp = tmp.apply(f);\n\t\t\t}\n";
        String returnDef = "\t\t}\n\t\treturn tmp;\n\t}\n}";

        String classDef = importDef + docString + funcDef + forDef + ifDef + returnDef;
        classDefs.add(classDef);
        definition.put("Lib", classDefs);

        return definition;
    }

    /**
    * Creates the main class and any lambda expressions defined by the user. 
    *
    * @param  name  an name of the main class
    * @param  exprs a list of all of the user-defined lambda expressions
    * @return       a HashMap of name and a string definition of the class
    */
    public static HashMap<String, List<String>> makeMain(String name, List<String> exprs) {

        HashMap<String, List<String>> definitions = new HashMap<String, List<String>>(1);
        List<String> classDefs = new ArrayList<String>();

        String importDef = "import java.util.function.Function;\n\nclass " + name + " {\n";
        String funcDef = "\tpublic static void main(String args[]) {\n\n";

        String output = "";
        // Iterate through each lambda expression
        for (String expr: exprs) {

            String exprOrig = expr;
            if (expr.contains("(")) {
                expr = expr.replace("(", "Lib.applyChain(");
            }

            String[] tokens = expr.split("[ ]+");            
            String applyChainArgs = String.join(", ", tokens);

            // Will output "given expression -> evaluated result"
            output = output + "\tSystem.out.println(\"------------------------\");\n";
            output = output + "\tSystem.out.println(\"" + exprOrig + " -> \" + Lib.applyChain(" + applyChainArgs + "));\n";
        }

        String classDef = importDef + funcDef + output + "\n\t}\n}";
        classDefs.add(classDef);
        definitions.put(name, classDefs);

        return definitions;
    }

    /**
    * Read user-provided files containing lambda terms/expressions.
    *
    * @param  dir_path a directory path containing the files
    * @return          a HashMap of file name and a list of lines
    */
    public static HashMap<String, List<String>> readFiles(File dir_path) {

        HashMap<String, List<String>> files = new HashMap<String, List<String>>();

        // Given dir path, get list of files
        if (dir_path.exists()) {
            File[] allFiles = dir_path.listFiles();

            // Remove hidden files from list
            List<File> listOfFiles = new ArrayList<File>();
            for (File f: allFiles) {
                // Ignore hidden files
                if (!f.isHidden()) {
                    // Only accept files ending in .lc
                    if (f.toString().endsWith(".lc")) {
                        listOfFiles.add(f);
                    }
                }
            }

            for (File f: listOfFiles) {
                Path path = f.toPath();
                // Drop the .lc from the file name
                String file_name = f.getName().split("[.]+")[0];
                // Make the file name title case
                file_name = file_name.substring(0,1).toUpperCase() + file_name.substring(1).toLowerCase();

                List<String> expressions = new ArrayList<String>();
                try {
                    List<String> lines = Files.readAllLines(path);
                    // Add lambda expressions from file to a list
                    for (String line: lines) {
                        // Skip empty lines
                        if (!line.isEmpty()) {
                            // Skip lines that start with -- (comment)
                            if (!line.startsWith("--")) {
                                expressions.add(line);
                            }
                        }
                    }
                    // Add the list of lambda expressions to a Map
                    files.put(file_name, expressions);
                } catch(IOException e) {
                    System.out.println("Reading from file failed: " + e);
                }
            }
        } else {
            System.out.println("Provided file path does not exist.");
        }
        return files;
    }

    /**
    * Write files containing the generated lambda functions.
    *
    * @param  dir_path a directory path to output the files
    * @param  data     a HashMap of file name and a list of functions
    */
    public static void writeFile(String dir_path, HashMap<String, List<String>> data) {
        for (HashMap.Entry<String, List<String>> entry : data.entrySet()) {
            String name = entry.getKey() + ".java";
            List<String> code = entry.getValue();

            // Convert to path and file types
            Path path = Paths.get(dir_path, name);
            File file = path.toFile();
            try {
                // Make parent directories and file
                file.getParentFile().mkdirs();
                file.createNewFile();
                // Write code to file
                Files.write(path, code, Charset.forName("UTF-8"));
            } catch(IOException e) {
                System.out.println("Writing to file failed:  " + e);
            }
        }
    }


    public static void main(String[] args) {

        if (args.length != 2) {
            throw new IllegalArgumentException("Exactly 2 parameters required.\n1) Input directory path \n2) Output directory path");
        }

        File dir = new File(args[0]);  // Input directory
        String output = args[1];  // Output directory

        HashMap<String, List<String>> files = readFiles(dir);

        // Create .java files
        for (HashMap.Entry<String, List<String>> entry: files.entrySet()) {
            String key = entry.getKey();
            List<String> lambdas = entry.getValue();
            
            if (key.equals("Main")) {
                // Create Main.java
                HashMap<String, List<String>> result = makeMain(key, lambdas);
                writeFile(output, result);
            } else if (key.equals("Vars")) {
                // Create Vars.java
                HashMap<String, List<String>> result = makeVars(key, lambdas);
                writeFile(output, result);
            } else {
                // Create .java files for each lambda library
                HashMap<String, List<String>> result = makeClass(key, lambdas);
                writeFile(output, result);
            }
        }

        // Create Lib.java
        HashMap<String, List<String>> libDef = makeLib();
        // Write files to output
        writeFile(output, libDef);
    }
}
