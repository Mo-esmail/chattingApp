import java.io.BufferedReader;
import java.io.PrintWriter;

public class ClientObject {
    PrintWriter out;
    BufferedReader in;
    String name;
    public void setoutput(PrintWriter printWriter){
        out=printWriter;
    }
    public void setbufferedReader(BufferedReader bufferedReader){
        in=bufferedReader;
    }
    public void setName(String name) {
        this.name = name;
    }
    public String getName() {
        return name;
    }
    public PrintWriter getOut() {
        return out;
    }
    public BufferedReader getIn() {
        return in;
    }
    public ClientObject(String name){
        this.name = name;
    }
    public ClientObject(){

    }

    public String toString() {
        return name;

    }

}
