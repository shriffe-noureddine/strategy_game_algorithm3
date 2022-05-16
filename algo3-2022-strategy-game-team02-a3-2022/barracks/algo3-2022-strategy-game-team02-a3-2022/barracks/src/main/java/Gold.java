import java.util.Random;

public class Gold {
    Random rand = new Random();

    private static int goldCoin;
    private static int bagOfGold;
    private static int treasureChest;
    static int TOTAL;
    public Gold() {
        int randomNum = rand.nextInt(1, 3 + 1);
        // the same possibility to get whatever gold type
        switch (randomNum) {
            case 1:
                goldCoin += 5;
                TOTAL +=5;
                break;
            case 2:
                bagOfGold += 25;
                TOTAL+= 25;
                break;
            case 3:
                treasureChest += 50;
                TOTAL+=50;
                break;
        }
    }

    public static int getGoldCoin() {
        return goldCoin;
    }

    public static int getBagOfGold() {
        return bagOfGold;
    }

    public static int getTreasureChest() {
        return treasureChest;
    }

    // public static void recalculateGold(int amount) {
    // if (amount <= 0) {
    // System.out.println("Please provide positive amount...");
    // } else {
    // if (goldTotal() > 0) {
    // if (amount >= 50 && treasureChest > amount) {
    // treasureChest -= (Math.floor(treasureChest / amount)) * 50;
    // amount -= (Math.floor(treasureChest / amount)) * 50;
    // }
    // if (amount > 25 && bagOfGold > amount) {
    // bagOfGold -= (Math.floor(bagOfGold / amount)) * 25;
    // }
    // if (amount > 5 && goldCoin > amount) {
    // goldCoin -= (Math.floor(goldCoin / amount)) * 5;
    // }
    // } else
    // System.out.println("No enough Gold available...");
    // }
    // }

    @Override
    public String toString() {
        return "{" +
                "Gold Coin='" + goldCoin + '\'' +
                ", Bag of Gold='" + bagOfGold + '\'' +
                ", Treasure Chest='" + treasureChest + '\'' +
                '}';
    }
}