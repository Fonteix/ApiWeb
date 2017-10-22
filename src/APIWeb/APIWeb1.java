package APIWeb;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;
import Imports.Clavier;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class APIWeb1 {

    private static String lien = "https://liris-ktbs01.insa-lyon.fr:8000/blogephem/";
    private static String urlCherche = "https://liris-ktbs01.insa-lyon.fr:8000/blogephem/?search=";
    private static String urlCreer = "https://liris-ktbs01.insa-lyon.fr:8000/blogephem/";
    private static String urlBillet = "";

    public static void main(String[] args) throws MalformedURLException, IOException {

        int numSwitch = -1;
        do {
            System.out.println("0 : quitter");
            System.out.println("1 : Poster un billet");
            System.out.println("2 : Liste billet");
            System.out.println("Choix : ");
            numSwitch = Clavier.lireInt();
            
            switch (numSwitch) {
            case 0 : 
                return;
            case 1 : 
                postHTML();
                break;
            case 2 : 
                listeBillet();
                break;
            }
        } while (numSwitch != 0);
}

    public static String getHTML() throws ProtocolException, MalformedURLException, IOException {
        URL url = new URL(lien);
        HttpURLConnection cx = (HttpURLConnection) url.openConnection();
        cx.setRequestMethod("GET");
        cx.connect();
        InputStream body = cx.getInputStream();
        ByteArrayOutputStream result = new ByteArrayOutputStream();
        byte[] buffer = new byte[1024];
        int length;
        while ((length = body.read(buffer)) != -1) {
            result.write(buffer, 0, length);
        }
        String HTML = result.toString("UTF-8");
        return HTML;
    }
    
    public static void postHTML() throws ProtocolException, MalformedURLException, IOException {
        String titre, contenu, query;
        System.out.print("Titre : ");
        titre = Clavier.lireString();
        System.out.print("Contenu : ");
        contenu = Clavier.lireString();
        query = "title=" + titre + "&body=" + contenu;
        URL url = new URL(urlCreer);
        HttpURLConnection cx = (HttpURLConnection)url.openConnection();
        cx.setRequestMethod("POST");
        cx.addRequestProperty("User-Agent", "titre");
        cx.setRequestProperty("accept-charset", "UTF-8");
        cx.setRequestProperty("content-type", "application/x-www-form-urlencoded");
        cx.setDoInput(true);
        cx.setDoOutput(true);
        OutputStreamWriter writer = new OutputStreamWriter(cx.getOutputStream(), "UTF-8");
        writer.write(query);
        writer.close();
        cx.connect();
        System.out.println("Status : " + cx.getResponseCode());
        System.out.println("URL : " + cx.getURL() + "\n");
        urlBillet = cx.getURL().toString();
    }
    
    public static void listeBillet() throws ProtocolException, IOException {
        URL url = new URL(lien);
        HttpURLConnection cx = (HttpURLConnection) url.openConnection();
        cx.setRequestMethod("GET");
        cx.connect();
        InputStream body = cx.getInputStream();
        ByteArrayOutputStream result = new ByteArrayOutputStream();
        byte[] buffer = new byte[1024];
        int length;
        while ((length = body.read(buffer)) != -1) {
            result.write(buffer, 0, length);
        }
        String HTML = result.toString("UTF-8");
        ArrayList<String> liens = parseLiens(HTML);        
    }
    
    private static ArrayList<String> parseLiens(String HTML) {
        Pattern pattern = Pattern.compile("href=\\\"([^\\\"]*)\\\"");
        Matcher matcher = pattern.matcher(HTML);
        ArrayList<String> liens = new ArrayList<String>();
        String lienBrut;
        while (matcher.find()) {
            lienBrut = matcher.group();
            lienBrut = lienBrut.substring(1, lienBrut.length()-1);
            if(lienBrut.contains("href")) {
                lienBrut = lienBrut.substring(6);
            }
            else {
                lienBrut = lienBrut.substring(5);
            }
            liens.add(lienBrut);
        }
        pattern = Pattern.compile(">([^<>]*)</a>");
        matcher = pattern.matcher(HTML);
        String title;
        
        System.out.println("------------------");
        int i = 0;
        while (matcher.find()) {
            title = matcher.group();
            title = title.replace("<", "");
            title = title.replace(">", "");
            title = title.substring(0, title.length()-2);
            System.out.println("Titre : " + title + "\nContenu : " + liens.get(i) + "\n");
            liens.set(i, liens.get(i) + ";" + title);
            i++;
        }
        System.out.println("------------------");
        return liens;
    }
}
