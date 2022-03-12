import jakarta.json.Json;
import org.apache.jena.atlas.json.JsonArray;
import org.apache.jena.atlas.json.JsonObject;


import java.io.*;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;

import org.apache.jena.query.Dataset;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.ModelFactory;
import org.apache.jena.riot.Lang;
import org.apache.jena.riot.RDFDataMgr;
import org.apache.jena.riot.RDFLanguages;
import org.apache.jena.riot.RDFParser;
import org.apache.jena.riot.system.ErrorHandlerFactory;
import org.json.JSONArray;
import org.json.JSONObject;


public class JeuxDeMots {

    //function to read a file and parse to write a json file


    public static ArrayList<String> mySplit(String myLine) {
        ArrayList<String> resultat = new ArrayList<>();
        String tmp = "";
        boolean cond = false;
        for (int i = 0; i < myLine.length(); i++) {
            if (i + 1 == myLine.length()) {
                tmp += myLine.charAt(i);
                resultat.add(tmp);
            } else {
                if (myLine.charAt(i) == '\'' && myLine.charAt(i + 1) != ';') {
                    cond = true;
                } else if (myLine.charAt(i) == '\'' && myLine.charAt(i + 1) == ';') {
                    cond = false;
                }
                if (cond) {
                    tmp += myLine.charAt(i);
                }
                if (!cond && myLine.charAt(i) != ';') {
                    tmp += myLine.charAt(i);
                } else if (!cond && myLine.charAt(i) == ';') {
                    resultat.add(tmp);
                    tmp = "";
                }
            }
        }
        return resultat;
    }

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



        JSONArray jsonArrayNT = new JSONArray();
        JSONArray jsonArrayE = new JSONArray();
        JSONArray jsonArrayRT = new JSONArray();
        JSONArray jsonArrayR = new JSONArray();

        //Lecture du fichier TXT ------------------------------
        FileReader file = new FileReader(fileName);
        BufferedReader br2 = new BufferedReader(file);
        String line;
        //-----------------------------------------------------

        while ((line = br2.readLine()) != null) {
            if (line.length() > 0) {
                ArrayList<String> words = mySplit(line);
                System.out.println(words);
                JSONObject jsonObject = new JSONObject();
                if ("e".equals(words.get(0))) {
                    jsonObject.put("name", words.get(2));
                    jsonObject.put("type", words.get(3));
                    jsonObject.put("w", words.get(4));
                    if (words.size() > 5) jsonObject.put("formated name", words.get(5));
                    jsonArrayE.put(jsonObject);

                } else if ('r' == line.charAt(0) && 't' == line.charAt(1)) {

                    jsonObject.put("trname", words.get(2));
                    jsonObject.put("trgpname", words.get(3));
                    if(words.size() > 4)jsonObject.put("rthelp", words.get(4));
                    jsonArrayRT.put(jsonObject);

                } else if ('r' == line.charAt(0)) {

                    jsonObject.put("node1", words.get(2));
                    jsonObject.put("node2", words.get(3));
                    jsonObject.put("type", words.get(4));
                    jsonObject.put("w", words.get(5));
                    jsonArrayR.put(jsonObject);

                } else if ('n' == line.charAt(0) && 't' == line.charAt(1)) {
                    jsonObject.put("ntname", words.get(2));
                    jsonArrayNT.put(jsonObject);

                }


            }
        }
        JSONObject jsonArrayFinal = new JSONObject();
        if(jsonArrayR.length()!=0) jsonArrayFinal.put("r", jsonArrayR);
        if(jsonArrayRT.length()!=0) jsonArrayFinal.put("rt", jsonArrayRT);
        if(jsonArrayE.length()!=0) jsonArrayFinal.put("e", jsonArrayE);
        if(jsonArrayNT.length()!=0) jsonArrayFinal.put("nt", jsonArrayNT);


        //write the json file
        FileWriter file2 = new FileWriter(json);
        file2.write(jsonArrayFinal.toString());
        file2.close();
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
        /*try {
            writeJson("JeuxDeMots" + word + ".txt", "JeuxDeMots" + word + ".json");
        } catch (IOException e) {
            e.printStackTrace();
        }*/

        Model model = ModelFactory.createDefaultModel();

        try (InputStream in = new FileInputStream("JeuxDeMots" + word + ".json")) {
            RDFParser.create()
                    .source(in)
                    .lang(RDFLanguages.JSONLD11)
                    .errorHandler(ErrorHandlerFactory.errorHandlerStrict)
                    .base("http://example/base")
                    .parse(model);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        System.out.println(model);

    }
}
