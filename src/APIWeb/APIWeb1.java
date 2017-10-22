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
import javax.xml.parsers.ParserConfigurationException;
import org.xml.sax.SAXException;
import static APIWeb.XML.getStringFromXML;
import java.util.logging.Level;
import java.util.logging.Logger;

public class APIWeb1 {

    /*private String lien = "https://liris-ktbs01.insa-lyon.fr:8000/blogephem/";
    private String urlCherche = "https://liris-ktbs01.insa-lyon.fr:8000/blogephem/?search=";
    private String urlCreer = "https://liris-ktbs01.insa-lyon.fr:8000/blogephem/";
    private String urlBillet = "";*/
    private URL url;
    private ArrayList<Articles> ArticleList;
    private ArrayList<URL> URLList;

    public APIWeb1(URL url) {
        this.url = url;
        this.ArticleList = new ArrayList<>();
        this.URLList = new ArrayList<>();
    }

    public URL getURL() {
        return url;
    }

    public void menu() throws IOException, ProtocolException, ParserConfigurationException, SAXException {
        try {
            listeURLBillet();
            int numSwitch = -1;
            do {
                System.out.println("0 : quitter");
                System.out.println("1 : Liste billet");
                System.out.println("2 : Poster un billet");
                System.out.println("Choix : ");
                numSwitch = Clavier.lireInt();

                switch (numSwitch) {
                    case 0:
                        return;
                    case 1:
                        listeArticle();
                        break;
                    case 2:
                        //postHTML();
                        break;
                }
            } while (numSwitch != 0);
        } catch (Exception ex) {
            System.out.println("bug");
        }
    }

    public void listeURLBillet() throws ProtocolException, IOException, ParserConfigurationException, SAXException {
        this.ArticleList = new ArrayList<>();
        this.URLList = new ArrayList<>();

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

        Pattern pattern = Pattern.compile("href=\\\"([^\\\"]*)\\\"");
        Matcher matcher = pattern.matcher(HTML);
        String lienBrut;
        while (matcher.find()) {
            lienBrut = matcher.group();
            lienBrut = lienBrut.substring(1, lienBrut.length() - 1);
            if (lienBrut.contains("href")) {
                lienBrut = lienBrut.substring(6);
            } else {
                lienBrut = lienBrut.substring(5);
            }
            URL link = new URL(url + lienBrut);
            URLList.add(link);
            Articles article = new Articles(link);
            ArticleList.add(article);
        }
    }
    
    private void listeArticle() {
        for (Articles article : this.ArticleList) {
            System.out.println("\nTitle : " + article.getTitle());
            System.out.println("Content : " + article.getBody());
            System.out.println("Date : " + article.getDate());
            System.out.println("URL : " + article.getUrl() + "\n");
        }
    }

    /*public String getHTML() throws ProtocolException, MalformedURLException, IOException {
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
    }*/
    
 /*public void postHTML() throws ProtocolException, MalformedURLException, IOException {
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
    }*/
    
 /*public ArrayList<Articles> getArticles()
    {
        return this.ArticleList;
    }*/
    
 /*private ArrayList<String> parseLiens(String HTML) {
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
    }*/
}
