import java.util.Random;

public class Treasure {

    Random rand = new Random();
    private static int monuments;
    public Treasure() {
        int randomNum = rand.nextInt(1, 4 + 1);
        // 25% probability of getting monuments and 75% probability of getting gold
        switch (randomNum){
            case 1:
                monuments+=1;
                break;
            case 2: case 3: case 4:
                new Gold();
                break;
        }
    }

    public static int getMonuments() {
        return monuments;
    }

    @Override
    public String toString() {
        return "{" +
                "Monuments='" + monuments + '\'' +
                '}';
    }
}