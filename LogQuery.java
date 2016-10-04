public class LogQuery {

    public static void main(String[] args) {

        //taking arugments 
        int num_machine = Integer.parseInt(args[0]);    // Run the # of VM's
        String pattern = args[1];
        String fileNameTemp = args[2];


        Thread[] td = new Thread[num_machine];
        // create different client threads to communicate with servers
        for (int i = 0; i < num_machine; i++){
            StringBuffer serverAddress = new StringBuffer("0").append(i+1);
            td[i] = new Thread(new LogQueryClient(pattern, serverAddress.toString(), fileNameTemp.replaceAll("\\?", String.valueOf(i + 1))));
            td[i].start();
        }
        // wait for for threads to end and handles error
        for (int i = 0; i < num_machine; i++){
            try {
                td[i].join();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        System.out.println("All LogQuery Done!");
    }
}
