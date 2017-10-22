package APIWeb;

import java.io.IOException;
import java.net.URL;
import static APIWeb.XML.getStringFromXML;


public class Main {
    public static void main(String[] args) throws IOException {
        URL url = new URL("https://liris-ktbs01.insa-lyon.fr:8000/blogephem/");
        APIWeb1 APIWeb1 = new APIWeb1(url);
        APIWeb1.menu();
    }
}
