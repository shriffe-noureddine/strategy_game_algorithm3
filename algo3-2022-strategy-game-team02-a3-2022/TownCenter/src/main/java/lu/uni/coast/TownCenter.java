package lu.uni.coast;

import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import java.util.Timer;
import java.util.TimerTask;

import javax.ws.rs.NotFoundException;
import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;

import javax.ws.rs.core.MediaType;


import io.javalin.Javalin;




public class TownCenter{


    private static Client client;
    private static final int PORT = 5000;
    private final static String BASE_URLWorkshop = "http://localhost:7070";
    private final static String BASE_URLBaracks = "http://localhost:7000";



    private static boolean upgradedTC = false;
    private boolean weArePlaying = true;
    private boolean finish = false;
    private int currWorkers = 0;
    private int maxWorkers = 10;

    private int food = 100;

    private List<Timer> timers = new ArrayList<>();

    public void assignToGetFood()
    {
        Timer timer = new Timer();
        timers.add(timer);
        timer.scheduleAtFixedRate(new TimerTask(){
        public void run() {
        food++;
        }
        }, 0, 1000);
    }


    

    public static void main( String[] args ) {


        Javalin app = Javalin.create().start(PORT);
        client = ClientBuilder.newClient();
       
        List<Worker> worker = new ArrayList<>();
        ArrayList<Integer> deletedID = new ArrayList<>();

        TownCenter TC = new TownCenter();

        // Timer timer = new Timer();
        // TC.timers.add(timer);
        // timer.scheduleAtFixedRate(new TimerTask(){
        // public void run() {
        //     checkWin();
        // }
        // }, 0, 8000);



        app.get("/worker", ctx -> ctx.json(worker));
        app.get("/food", ctx -> ctx.json(TC.food));

        // app.get("/win", ctx -> {
        //     checkWin();
        // });
        
        app.get("/food/{amount}", ctx -> {
            String amount = ctx.pathParam("amount");
            int value = Integer.valueOf(amount);
            if(TC.food >= value)
            {
                TC.food = TC.food - value;
                ctx.json(true);
            }
            else
            {
                ctx.json(false);
            }
        });


        app.get("/upgrade/TC", ctx -> {
            ctx.json(TC.upgradedTC);
        });


        app.get("/return/food/{amount}", ctx -> {
            String amount = ctx.pathParam("amount");
            int value = Integer.valueOf(amount);
            if(TC.food >= value)
            {
                TC.food = TC.food + value;
                ctx.json(true);
            }
            else
            {
                ctx.json(false);
            }
        });



        app.get("/worker/{task}", ctx -> {
            String task = ctx.pathParam("task");
            int x = -1;
            boolean possible = false;
            if (worker.size() > 0) {
                for (int i = worker.size() - 1; i >= 0; i--) {
                    if (worker.get(i).Task().equals("idle")) {
                        x = i;
                    }
                }
                if (x != -1) {
                    int id = worker.get(x).id();
                    worker.remove(x);
                    worker.add(new Worker(id, task, false));
                    possible = true;
                }
            }
            ctx.json(possible);
        });


        app.get("/archer/dead", ctx -> {
            if (worker.size() != 0) {
                int x = -1;
                for (int i = worker.size() - 1; i >= 0; i--) {
                    if (worker.get(i).Task().equals("archer")) {
                        x = i;
                    }
                }
                if (x != -1) {
                    int id = worker.get(x).id();
                    deletedID.add(id);
                    worker.remove(x);
                    TC.currWorkers--;
                    System.out.println("Archer (id:" + id +") is dead!");
                    ctx.json(true);
                }
                else
                {
                    System.out.println("There are no Archers!");
                    ctx.json(false);
                }
            }
            else
            {
                ctx.json(false);
                System.out.println("There are no built worker yet");
            }
        });

     

        Scanner scanner = new Scanner(System.in);

        while(TC.weArePlaying && !TC.finish){
            System.out.println("Enter a single character: s(status), w(buildWorker), f(build food gatherer) u(upgrade), q(quit)");
            char input = scanner.nextLine().charAt(0);
            if(input == 's')
            {
                System.out.println("Food: " + TC.food);
                System.out.println("Is the Town Center upgraded: " + TC.upgradedTC);
                System.out.println("Unit limit: " + TC.maxWorkers);
                System.out.println("Workers (" + TC.currWorkers + "): " + worker.toString());
            }else if(input == 'w')
            {
                // Integer numberHouses = client.target(BASE_URLWorkshop)
                //         .path("house")
                //         .request(MediaType.APPLICATION_JSON)
                //         .get(Integer.class);
                // TC.maxWorkers = 10 + (5 * numberHouses);
                if(TC.food >= 25)
                {
                    if(TC.currWorkers < TC.maxWorkers)
                    {
                        if (deletedID.size() > 0) {
                            TC.currWorkers++;
                            worker.add(new Worker(deletedID.get(0), "idle", true));
                            deletedID.remove(0);
                            TC.food=TC.food - 25;
                        }
                        else
                        {
                            TC.currWorkers++;
                            worker.add(new Worker(TC.currWorkers, "idle", true));
                            TC.food=TC.food - 25;
                        }
                    }
                    else
                    {
                        System.out.println("Limmit of workers is at the limit: " + TC.currWorkers + "/" + TC.maxWorkers);
                    }
                }
                else
                {
                    System.out.println("There is no food left!");
                }
            }else if(input == 'u')
            {
                if (!TC.upgradedTC) {
                    Integer woods = client.target(BASE_URLWorkshop)
                        .path("wood/150")
                        .request(MediaType.APPLICATION_JSON)
                        .get(Integer.class);
                    if (woods > 0) {
                        Boolean golds = client.target(BASE_URLBaracks)
                            .path("gold/250")
                            .request(MediaType.APPLICATION_JSON)
                            .get(Boolean.class);
                        if (golds) {
                            TC.upgradedTC = true;
                            System.out.println("The town center is now upgraded!");
                        }
                        else
                        {
                            System.out.println("You cant upgrade there is no Gold!!!");
                        }
                    }
                    else
                    {
                        System.out.println("You cant upgrade there is no wood!!!");
                    }
                }
            }else if(input == 'f')
            {
                int x = -1;
                for (int i = worker.size() - 1; i >= 0; i--) {
                    if (worker.get(i).Task().equals("idle")) {
                        x = i;
                    }
                }
                if (x != -1) {
                    int id = worker.get(x).id();
                    worker.remove(x);
                    worker.add(new Worker(id, "food gatherer", true));
                    TC.assignToGetFood();
                }
                else
                {
                    System.out.println("There are no idle workers!");
                }
            }else if(input == 'q')
            {
                Boolean golds = client.target(BASE_URLBaracks)
                            .path("gold/110")
                            .request(MediaType.APPLICATION_JSON)
                            .get(Boolean.class);
                if (golds) {
                    System.out.println("ByeByeGold");
                }
            }
        }
        for(int i = 0; i < TC.timers.size(); i++)
        {
            TC.timers.get(i).cancel();
            TC.timers.get(i).purge();
        }
        scanner.close();
        client.close();
        app.stop();
    }

    // private static boolean threeMonumentsCollected() {
    //     client = ClientBuilder.newClient();
    //     try {
    //         return client.target(BASE_URLBaracks)
    //                 .path("/monuments")
    //                 .request(MediaType.APPLICATION_JSON)
    //                 .get(boolean.class);
    //     } catch (NotFoundException ex) {
    //         System.err.println(ex);
    //     }
    //     client.close();
    //     return false;
    // }


    // private static void checkWin(){
    //     if(threeMonumentsCollected()&& upgradedTC){
    //         System.out.println("YOU got "+ 3 +" Monuments and " +
    //                 "TownCenter is Upgraded\n" +
    //                 "Congratulation You Win!");
    //         terminateBarracks();
    //         System.exit(0);
    //     }
    // }

    // private static void terminateBarracks() {
    //     client = ClientBuilder.newClient();
    //     try {
    //         client.target(BASE_URLBaracks)
    //                 .path("/win/")
    //                 .request(MediaType.APPLICATION_JSON)
    //                 .get();
    //     } catch (NotFoundException ex) {
    //         System.err.println(ex);
    //     }
    //     client.close();
    // }
    
}
