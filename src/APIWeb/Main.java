package APIWeb;

import java.io.IOException;
import java.net.URL;
import static APIWeb.XML.getStringFromXML;
import java.net.ProtocolException;
import javax.xml.parsers.ParserConfigurationException;
import org.xml.sax.SAXException;


public class Main {
    public static void main(String[] args) throws IOException, ProtocolException, ParserConfigurationException, SAXException {
        URL url = new URL("https://liris-ktbs01.insa-lyon.fr:8000/blogephem/");
        APIWeb1 APIWeb1 = new APIWeb1(url);
        APIWeb1.menu();
    }
}
