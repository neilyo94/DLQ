import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.ObjectOutputStream;
import java.net.Socket;

public class LogQueryClient implements Runnable{

    private final int port = 9090;
    private final int timeOut = 100000;
    private Socket sock;
    private String serverAddress = "fa16-cs425-g36-?.cs.illinois.edu";
    private String fileName = "";
    private String pattern;


    public LogQueryClient(String pattern, String serverNo, String fileName) {
        this.pattern = pattern;
        this.serverAddress = this.serverAddress.replaceAll("\\?",serverNo);
        this.fileName = fileName;
    }

    public void doQuery()
    {
        try
        {
            // connect to remote
            sock = new Socket(serverAddress,port);
            sock.setSoTimeout(timeOut);

            // Pass the pattern for search to remote server
            ObjectOutputStream out = new ObjectOutputStream(sock.getOutputStream());
            System.out.println(pattern + "," + fileName);
            String[] outStr = {pattern,fileName};
            out.writeObject(outStr);
            out.flush();
            System.out.println(serverAddress + " send to server");  //send to server

            // Get result back from remote server
            BufferedReader in = new BufferedReader(new InputStreamReader(sock.getInputStream()));
            String str;
            System.out.println(serverAddress + " receive from server");
            while ((str = in.readLine()) != null)
            {
                if (str.equals("LogQuery Done!")) break;
                System.out.println(str);
            }
            
            //receive query result from server
            System.out.println(serverAddress + " all received from server");    
            out.close();
            in.close();
            sock.close();
        } catch (IOException e)
        {
            System.err.println(serverAddress + " " + e);
        }
    }

    @Override
    public void run() {
        // open a socket and connect with a timeout limit
        System.out.println(serverAddress + " start");
        doQuery();
        System.out.println(serverAddress + " done");
    }
}
