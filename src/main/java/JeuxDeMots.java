import jakarta.json.Json;
import org.apache.jena.atlas.json.JsonArray;
import org.apache.jena.atlas.json.JsonObject;


import java.io.*;
import java.net.URL;
import java.net.URLConnection;

public class JeuxDeMots {

    //function to read a file and parse to write a json file


    //function to connect to an api and get the source code of the page
    public static String getSourceCode(String url) throws IOException {
        StringBuilder sb = new StringBuilder();
        URLConnection connection = new URL(url).openConnection();
        BufferedReader br = new BufferedReader(new InputStreamReader(connection.getInputStream()));
        String line;
        while ((line = br.readLine()) != null) {
            sb.append(line + "\n");
        }
        br.close();
        return sb.toString();
    }

    //open a file and write a json file
    public static void writeJson(String fileName, String json) throws IOException {

        FileReader file = new FileReader(fileName);
        BufferedReader br2 = new BufferedReader(file);
        String line;
        FileWriter fileWriter = new FileWriter(json);
        fileWriter.write("[\n");
        while ((line = br2.readLine()) != null) {
            if (line.length() > 0) {
                fileWriter.write("\t{\n");
                if ('e' == line.charAt(0)) {

                    System.out.println(line);
                    String[] words = line.split(";");

                    fileWriter.write("\t\t" + "\"" + words[1] + "\"" + ": {\n");

                    fileWriter.write("\t\t\t" + "\"" + "name" + "\"" + ": " + "\"" + words[2] + "\"" + ",\n");
                    fileWriter.write("\t\t\t" + "\"" + "type" + "\"" + ": " + "\"" + words[3] + "\"" + ",\n");
                    if (words.length == 5) {
                        fileWriter.write("\t\t\t" + "\"" + "w" + "\"" + ": " + "\"" + words[4] + "\"" + "\n");
                    } else {
                        fileWriter.write("\t\t\t" + "\"" + "w" + "\"" + ": " + "\"" + words[4] + "\"" + ",\n");
                    }
                    if (words.length == 6) {
                        fileWriter.write("\t\t\t" + "\"" + "formated name" + "\"" + ": " + "\"" + words[5] + "\"" + "\n");
                    }
                    fileWriter.write("\t\t" + "}\n");
                    fileWriter.write("\t" + "},\n");

                } else if ('r' == line.charAt(0) && 't' == line.charAt(1)) {
                    String[] words = line.split(";");
                    jsonObject.put("trname", words[2]);
                    jsonObject.put("trgpname", words[3]);
                    jsonObject.put("rthelp", words[4]);

                    jsonObject2.put(words[1], jsonObject);
                } else if ('r' == line.charAt(0)) {
                    String[] words = line.split(";");
                    jsonObject.put("node1", words[2]);
                    jsonObject.put("node2", words[3]);
                    jsonObject.put("type", words[4]);
                    jsonObject.put("w", words[5]);

                    jsonObject2.put(words[1], jsonObject);
                } else if ('n' == line.charAt(0) && 't' == line.charAt(1)) {
                    String[] words = line.split(";");
                    jsonObject.put("ntname", words[2]);

                    jsonObject2.put(words[1], jsonObject);
                }


            }
        }
    }
        fileWriter.write("]");
        fileWriter.close();

}

    public static void main(String[] args) {
        //Ask to user an word to search
        System.out.println("Entrez un mot à chercher : ");
        BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
        String word = null;
        try {
            word = br.readLine();
        } catch (IOException e) {
            e.printStackTrace();
        }
        System.out.println("Vous avez saisi : " + word);
        //verify if an file take this name exist in the folder
        File file1 = new File("JeuxDeMots" + word + ".txt");
        if (file1.exists()) {
            System.out.println("Le fichier existe");
        }
        //if the file doesn't exist, create it
        else {
            //get the source code of the page
            String sourceCode = "";
            try {
                sourceCode = getSourceCode("https://www.jeuxdemots.org/rezo-dump.php?gotermsubmit=Chercher&gotermrel=" + word + "&rel=");
                //parse to take only content on <CODE>
                sourceCode = sourceCode.substring(sourceCode.indexOf("<CODE>") + 6, sourceCode.indexOf("</CODE>"));

            } catch (IOException e) {
                e.printStackTrace();
            }
            System.out.println(sourceCode);
            //create a file and save it
            try {
                FileWriter fw = new FileWriter("JeuxDeMots" + word + ".txt", true);
                BufferedWriter bw = new BufferedWriter(fw);
                bw.write(sourceCode);
                bw.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        //create a json file with the content of the file
        try {
            writeJson("JeuxDeMots" + word + ".txt", "JeuxDeMots" + word + ".json");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
