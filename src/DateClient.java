import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.net.UnknownHostException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class DateClient {

    public static void main(String[] args) {
        try {
            Socket s=new Socket("localhost", 80);
            PrintWriter out = new PrintWriter(s.getOutputStream(), true);
            BufferedReader in = new BufferedReader(
                    new InputStreamReader(s.getInputStream()));
            out.println(new SimpleDateFormat("dd-MM-yyyy hh:mm:ss.SSS a").format(new Date()));
            out.println("bb");
            out.println("A7777777a");
            s.close();
        } catch (UnknownHostException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

    }

}
