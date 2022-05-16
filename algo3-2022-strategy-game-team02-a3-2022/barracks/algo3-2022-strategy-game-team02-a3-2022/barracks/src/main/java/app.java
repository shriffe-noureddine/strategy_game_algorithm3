import java.util.*;

import io.javalin.Javalin;
import javax.ws.rs.NotFoundException;
import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.core.MediaType;

public class app {
    static Random rand = new Random();
    static Timer timer = new Timer();
    static int timeToFindTreasure = rand.nextInt(5, 8 + 1);
    static int timeToSendPlunder = rand.nextInt(5, 8 + 1);
    private static final int PORT = 7000;
    static List<Archer> archers = Collections.synchronizedList(new ArrayList<Archer>());
    private final static String TOWN_CENTER_URL = "http://localhost:5000";
    private final static String WORKSHOP_URL = "http://localhost:7070";
    private static Client client;
    static boolean archersUpgraded = false;

    // for testing since workshop doesn't work properly
    static boolean quit = false;

    public static void main(String[] args) {

        // adding 5 Archers for testing
        // for (int i = 0; i < 5; i++) {
        // archers.add(new Archer());
        // }
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                timeToSendPlunder = rand.nextInt(5, 8 + 1);
                // send an archer for plundering each 5 to 8 seconds
                plunder();
                // System.out.println("Try to send a new Archer for plundering...");
                checkWin();
            }
        }, 0, timeToSendPlunder * 1000);

        Javalin app = Javalin.create().start(PORT);

        app.get("/gold/{amount}", ctx -> {
            String amount = ctx.pathParam("amount");
            if (Gold.TOTAL >= Integer.parseInt(amount)) {
                Gold.TOTAL = Gold.TOTAL - Integer.parseInt(amount);
                ctx.json(true);
            } else {
                ctx.json(false);
            }
        });

        app.get("/win", ctx -> {
            checkWin();
        });

        app.get("/archers", ctx -> ctx.json(archers));

        app.get("/archers/{id}", ctx -> {
            String id = ctx.pathParam("id");
            Optional<Archer> archer = archers.stream()
                    .filter(f -> Objects.equals(f.getId(), id))
                    .findFirst();
            System.out.println("GET /archers/" + id + " --> " + archer);
            archer.map(ctx::json).orElseGet(() -> ctx.status(404));
        });

        System.out.println("Type 'h' to see the available instructions");
        while (!quit) {
            Scanner scanner = new Scanner(System.in);
            String input = scanner.nextLine();
            switch (input) {
                case "w":
                    System.out.println(getWorkers());
                    break;
                case "b":
                    buildArcher();
                    break;
                case "u":
                    if (!archersUpgraded) {
                        upgradeArchers();
                    } else
                        System.out.println("Archers already upgraded!");
                    break;
                case "r":
                    printRules();
                    break;
                case "s":
                    printStatus();
                    break;
                case "q":
                    quit = true;
                    break;
                default:
                    printAvailableInstructions();
                    break;
            }
        }
        System.out.println("Good Bye!");
        System.exit(0);
    }
    private static void printAvailableInstructions() {
        System.out.println("Please enter one of the following instructions: " +
                "\n\t- w (Check all workers)" +
                "\n\t- b (Build an Archer)" +
                "\n\t- u (Upgrade Archers)" +
                "\n\t- r (See Rules to play)" +
                "\n\t- s (Status)" +
                "\n\t- q (Quit)");
    }

    private static void printStatus() {
        System.out.print("Total Monuments:(" + Treasure.getMonuments() + ")");
        System.out.println(
                " | Total Gold:(" + Gold.TOTAL + ") [Gold Coin(" + Gold.getGoldCoin() + "), Bag of Gold:("
                        + Gold.getBagOfGold() + "), Treasure Chest:(" + Gold.getTreasureChest() + ")] ");
        System.out.println("Archers (" + archers.size() + "):" + archers);
        System.out.println("Archer upgraded: " + archersUpgraded);
    }

    private static void plunder() {
        boolean archerSent = false;
        if (archers.size() > 0) {
            for (Archer archer : archers) {
                if (archer.isAvailable()) {
                    archerSent = true;
                    archer.setAvailable(false);
                    new Thread() {
                        public void run() {
                            while (!archer.isDead()) {
                                // System.out.println("Archer " + archer.getId() + " is Plundering...");
                                timeToFindTreasure = rand.nextInt(5, 8 + 1);
                                new Treasure();

                                if (Treasure.getMonuments() >= 3) {
                                    threeMonumentsCollected();
                                }
                                if (archersUpgraded) {
                                    // probability to lose an archer 25% upgraded
                                    archer.setDead(rand.nextInt(4) == 0);
                                } else {
                                    // probability to lose an archer 50% non-upgraded
                                    archer.setDead(rand.nextInt(2) == 0);
                                }
                                if (archer.isDead()) {
                                    tellTownCenterArcherDeath();
                                    // System.out.println("Ohh Archer " + archer.getId() + " died...");
                                    archers.remove(archer);
                                    // Thread.currentThread().interrupt();
                                }
                                try {
                                    Thread.sleep(timeToFindTreasure * 1000);
                                } catch (InterruptedException ex) {
                                    ex.printStackTrace();
                                }
                            }
                        }
                    }.start();
                }
                if (archerSent)
                    break;
            }
        } else
            System.out.println("No Archers to send for plundering...");
    }

    private static void printRules() {
        System.out.println("To win you have to create archers which will be sent" +
                            "\n to collect gold and monuments" +
                            "\n to win you need to collect 3 monuments and the town center " +
                            "\n should be upgraded!");
    }

    private static List<Worker> getWorkers() {
        List<Worker> allWorkers;
        client = ClientBuilder.newClient();
        allWorkers = Arrays.asList(client.target(TOWN_CENTER_URL)
                .path("worker")
                .request(MediaType.APPLICATION_JSON)
                .get(Worker[].class));
        client.close();
        return allWorkers;
    }

    private static boolean getWorker() {
        client = ClientBuilder.newClient();
        try {
            return client.target(TOWN_CENTER_URL)
                    .path("/worker/archer")
                    .request(MediaType.APPLICATION_JSON)
                    .get(boolean.class);
        } catch (NotFoundException ex) {
            System.err.println("no worker information found");
        }
        client.close();
        return false;
    }

    private static boolean getFood(int amount) {
        client = ClientBuilder.newClient();
        try {
            return client.target(TOWN_CENTER_URL)
                    .path("/food/" + amount)
                    .request(MediaType.APPLICATION_JSON)
                    .get(boolean.class);
        } catch (NotFoundException ex) {
            System.err.println("no food information found");
        }
        client.close();
        return false;
    }

    private static int getWood(int amount) {
        client = ClientBuilder.newClient();
        try {
            return client.target(WORKSHOP_URL)
                    .path("/wood/" + amount)
                    .request(MediaType.APPLICATION_JSON)
                    .get(Integer.class);
        } catch (NotFoundException ex) {
            System.err.println("no wood found");
        }
        client.close();
        return 0;
    }

    private static boolean returnFood(int amount) {
        client = ClientBuilder.newClient();
        try {
            return client.target(TOWN_CENTER_URL)
                    .path("/return/food/" + amount)
                    .request(MediaType.APPLICATION_JSON)
                    .get(boolean.class);
        } catch (NotFoundException ex) {
            System.err.println("no food information found");
        }
        client.close();
        return false;
    }

    private static boolean returnWood(int amount) {
        client = ClientBuilder.newClient();
        try {
            return client.target(WORKSHOP_URL)
                    .path("/return/wood/" + amount)
                    .request(MediaType.APPLICATION_JSON)
                    .get(boolean.class);
        } catch (NotFoundException ex) {
            System.err.println("no wood information found");
        }
        client.close();
        return false;
    }

    private static boolean tellTownCenterArcherDeath() {
        client = ClientBuilder.newClient();
        try {
            return client.target(TOWN_CENTER_URL)
                    .path("/archer/dead")
                    .request(MediaType.APPLICATION_JSON)
                    .get(boolean.class);
        } catch (NotFoundException ex) {
            System.err.println("Something went wrong from TownCenter server...");
        }
        client.close();
        return false;
    }

    private static boolean threeMonumentsCollected() {
        client = ClientBuilder.newClient();
        try {
            return client.target(TOWN_CENTER_URL)
                    .path("/monuments")
                    .request(MediaType.APPLICATION_JSON)
                    .get(boolean.class);
        } catch (NotFoundException ex) {
            System.err.println(ex);
        }
        client.close();
        return false;
    }

    private static boolean isUpgradedTC() {
        client = ClientBuilder.newClient();
        try {
            return client.target(TOWN_CENTER_URL)
                    .path("/upgrade/TC")
                    .request(MediaType.APPLICATION_JSON)
                    .get(boolean.class);
        } catch (NotFoundException ex) {
            System.err.println(ex);
        }
        client.close();
        return false;
    }
    private static void buildArcher() {
        if (getFood(50)) {
            if (getWood(25) > 0) {
                if (getWorker()) {
                    Archer archer = new Archer();
                    archers.add(archer);
                    System.out.println("Archer is created.");
                } else {
                    System.out.println("No enough Workers available...");
                    // returnWood method doesn't work error from workshop part!
//                    returnWood(25);
                }
            } else {
                System.out.println("No enough Wood resource...");
                returnFood(50);
            }
        } else {
            System.out.println("No enough Food resource...");
        }
    }
    private static void upgradeArchers() {
        if (getWood(100) > 0) {
            if (getFood(50)) {
                for (Archer archer : archers) {
                    archer.setUpgraded(true);
                    archersUpgraded = true;
                    System.out.println("Archers upgraded!");
                }
            } else {
                System.out.println("No enough Food available for upgrading Archers");
            }
        } else {
            System.out.println("No enough Wood available for upgrading Archers");
        }
    }
    private static void checkWin(){
        if(Treasure.getMonuments() >= 3 && isUpgradedTC()){
            System.out.println("YOU got "+Treasure.getMonuments()+" Monuments and " +
                    "TownCenter is Upgraded\n" +
                    "Congratulation You Win!");

            terminateTownCenter();
            System.exit(0);
        }

    }
    private static void terminateTownCenter() {
        client = ClientBuilder.newClient();
        try {
            client.target(TOWN_CENTER_URL)
                    .path("/win");
        } catch (NotFoundException ex) {
            System.err.println(ex);
        }
        client.close();
    }

}
