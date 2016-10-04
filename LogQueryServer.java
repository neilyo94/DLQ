import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;

public class LogQueryServer {
    private static String grep_line = "grep -n 123 file /home/xzhou14/";
    private static String grep_lineCount = "grep -c 123 file /home/xzhou14/";

    /**
     * Runs the server.
     */
    public static void main(String[] args) throws IOException {
        ServerSocket listener = new ServerSocket(9090);
        try {
            while (true) {
                Socket socket = listener.accept();
                try {
                    //set up objects to write and read to socket
                    ObjectInputStream in = new ObjectInputStream(socket.getInputStream());
                    PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
                        try {
                            //receive to grep pattern 
                            String[] inStr = (String[]) in.readObject();
                            String pattern = inStr[0];
                            String fileName = inStr[1];
                            if (pattern == null || pattern.equals(".") || fileName == null || fileName.equals("."))  {
                                break;
                            }
                            // setting up grep command to run
                            String grep_line_cmd = grep_line;
                            String grep_lineCount_cmd = grep_lineCount;
                            grep_line_cmd = grep_line_cmd.replaceAll("123",pattern);
                            grep_line_cmd = grep_line_cmd.concat(fileName);
                            grep_lineCount_cmd = grep_lineCount_cmd.replaceAll("123",pattern);
                            grep_lineCount_cmd = grep_lineCount_cmd.concat(fileName);
                            System.out.println(pattern + "," + fileName);
                            System.out.println(grep_line_cmd);
                            Process p1,p2;

                            //run grep command on line
                            p1 = Runtime.getRuntime().exec(grep_line_cmd);
                            //put result on buffer
                            BufferedReader result = new BufferedReader(new InputStreamReader(p1.getInputStream()));
                            String line = "";
                            //send result to socket 
                            while ((line = result.readLine()) != null) {
                                System.out.println(line);
                                out.println(line);
                                out.flush();
                            }
                            p1.waitFor();
                            p1.destroy();
                            // run grep command with count
                            p2 = Runtime.getRuntime().exec(grep_lineCount_cmd);

                            //sending to socket
                            result = new BufferedReader(new InputStreamReader(p2.getInputStream()));
                            while ((line = result.readLine()) != null) {
                                System.out.println(line);
                                out.println(line);
                                out.flush();
                            }
                            out.println("LogQuery Done!");  //finish and ending process
                            p2.waitFor();
                            p2.destroy();
                        } catch (Exception e) {
                            System.err.println(e);
                        }
                        System.out.println("Grep Done");

                } finally {
                    socket.close();
                }
            }
        }
        finally {
            listener.close();
        }
    }

}