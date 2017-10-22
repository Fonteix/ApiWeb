package APIWeb;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.ProtocolException;
import java.net.URL;
import javax.xml.parsers.ParserConfigurationException;
import org.xml.sax.SAXException;
import static APIWeb.XML.getStringFromXML;


public class Articles {
    private URL url;
    private String title;
    private String body;
    private String date;
    private String shortURL;
    
    public Articles(URL url) throws ProtocolException, IOException, ParserConfigurationException, SAXException {
        this.url = url;
        refresh();
    }
    protected void refresh() throws ProtocolException, IOException, ParserConfigurationException, SAXException
    {
        HttpURLConnection cx = (HttpURLConnection) this.url.openConnection();
        cx.setRequestMethod("GET");
        cx.setRequestProperty("accept-charset", "UTF-8");
        cx.setRequestProperty("accept", "text/xml");
        cx.connect();
        InputStream bodyStream = cx.getInputStream();
        ByteArrayOutputStream result = new ByteArrayOutputStream();
        byte[] buffer = new byte[1024];
        int length;
        while ((length = bodyStream.read(buffer)) != -1)
        {
            result.write(buffer, 0, length);
        }
        String xmlString = result.toString("UTF-8");
        
        this.body = getStringFromXML("body", xmlString);
        this.title = getStringFromXML("title", xmlString);
        this.date = getStringFromXML("date", xmlString);
        this.shortURL = this.url.toString().replace("https://liris-ktbs01.insa-lyon.fr:8000/blogephem/", "");
    }

    public URL getUrl() {
        return url;
    }

    public String getTitle() {
        return title;
    }

    public String getBody() {
        return body;
    }

    public String getDate() {
        return date;
    }

    public void setUrl(URL url) {
        this.url = url;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public void setBody(String body) {
        this.body = body;
    }

    public void setDate(String date) {
        this.date = date;
    }
    
    public void delete() throws IOException {
        HttpURLConnection cx = (HttpURLConnection) this.url.openConnection();
        cx.setRequestMethod("DELETE");
        cx.connect();
        int status = cx.getResponseCode();
        if(status == 200) {
            System.out.println("Message deleted");
        }
        else {
           System.out.println("Delete error"); 
        }
    }
    
}
