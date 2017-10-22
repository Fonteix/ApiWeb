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

public class Blog {

    /*private String lien = "https://liris-ktbs01.insa-lyon.fr:8000/blogephem/";
    private String urlCherche = "https://liris-ktbs01.insa-lyon.fr:8000/blogephem/?search=";
    private String urlCreer = "https://liris-ktbs01.insa-lyon.fr:8000/blogephem/";*/

    private URL url;
    private ArrayList<Articles> ArticleList;
    private ArrayList<URL> URLList;

    public Blog(URL url) {
        this.url = url;
        this.ArticleList = new ArrayList<>();
        this.URLList = new ArrayList<>();
    }

    public URL getURL() {
        return url;
    }

    public void menu() throws IOException, ProtocolException, ParserConfigurationException, SAXException {
        try {
            listeURLArticle();
            int numSwitch = -1;
            do {
                System.out.println("0 : Quit");
                System.out.println("1 : List article");
                System.out.println("2 : Post article");
                System.out.println("3 : Search article");
                System.out.println("4 : Edit article");
                System.out.println("5 : Delete article");
                System.out.print("Choice : ");
                numSwitch = Clavier.lireInt();

                switch (numSwitch) {
                    case 0:
                        return;
                    case 1:
                        listeArticle();
                        break;
                    case 2:
                        postArticle();
                        System.out.println("Article created\n");
                        break;
                    case 3:
                        System.out.print("Search : ");
                        String find = Clavier.lireString();
                        searchArticle(find);
                        break;
                    case 4:
                        System.out.print("Small URL (you can find it by search) : ");
                        String smallURLEdit = Clavier.lireString();
                        System.out.print("\nNew title : ");
                        String newTitle = Clavier.lireString();
                        System.out.print("\nNew content : ");
                        String newBody = Clavier.lireString();
                        editArticle(smallURLEdit, newTitle, newBody);
                    case 5:
                        System.out.print("Small URL (you can find it by search): ");
                        String smallURLDelete = Clavier.lireString();
                        deleteArticle(smallURLDelete);
                }
            } while (numSwitch != 0);
        } catch (Exception ex) {
            System.out.println("Menu exception");
        }
    }

    public void listeURLArticle() throws ProtocolException, IOException, ParserConfigurationException, SAXException {
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

    public void postArticle() throws ProtocolException, MalformedURLException, IOException, ParserConfigurationException, SAXException {
        String titre, contenu, query;
        System.out.print("Title : ");
        titre = Clavier.lireString();
        System.out.print("Content : ");
        contenu = Clavier.lireString();

        query = "title=" + titre + "&body=" + contenu;
        HttpURLConnection cx = (HttpURLConnection) this.url.openConnection();
        cx.setRequestMethod("POST");
        cx.setRequestProperty("content-type", "application/x-www-form-urlencoded");
        cx.setDoInput(true);
        cx.setDoOutput(true);
        OutputStreamWriter writer = new OutputStreamWriter(cx.getOutputStream(), "UTF-8");
        writer.write(query);
        writer.close();
        cx.connect();
        int status = cx.getResponseCode();
        if (status == 200) {
            Articles article = new Articles(cx.getURL());
            this.ArticleList.add(article);
        } else {
            System.out.println("Post error");
        }
    }

    public void searchArticle(String find) {
        try {
            listeURLArticle();
        } catch (Exception ex) {
            System.out.println("search exception");
        }

        for (Articles article : ArticleList) {
            String title = article.getTitle().toLowerCase();
            String body = article.getBody().toLowerCase();
            find = find.toLowerCase();
            if (title.contains(find) || body.contains(find)) {
                System.out.println("\nTitle : " + article.getTitle());
                System.out.println("SmallURL : " + article.getShortURL() + "\n");
            }
        }

    }

    private void editArticle(String smallURL, String title, String body) {
        for (Articles article : ArticleList) {
            if (smallURL.equals(article.getShortURL())) {
                try {
                    String query = "<article><title>" + title + "</title><body>" + body + "</body></article>";
                    URL urlEdit = new URL(url.toString() + smallURL);
                    HttpURLConnection cx = (HttpURLConnection) urlEdit.openConnection();
                    cx.setRequestMethod("PUT");
                    cx.setRequestProperty("accept-charset", "UTF-8");
                    cx.setRequestProperty("content-type", "text/xml");
                    cx.setDoInput(true);
                    cx.setDoOutput(true);
                    OutputStreamWriter writer = new OutputStreamWriter(cx.getOutputStream(), "UTF-8");
                    writer.write(query);
                    writer.close();
                    cx.connect();
                    int status = cx.getResponseCode();
                    if (status == 200) {
                        System.out.println("Article modified\n");
                    } else {
                        System.out.println("Edit Error\n");
                    }
                    article.refresh();
                } catch (Exception ex) {
                    System.out.println("Edit exception");
                }
            }
        }
    }

    private void deleteArticle(String smallURL) {
        for (Articles article : ArticleList) {
            if (article.getShortURL().equals(smallURL)) {
                try {
                    article.delete();
                } catch (Exception ex) {
                    System.out.println("Delete exception");
                }
            }
        }
        try {
            listeURLArticle();
        } catch (Exception ex) {
            System.out.println("Delete refresh exception");
        }
    }

    //Anciennes méthodes pour le début du TP
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
