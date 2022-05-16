package workshop;

public class House {
    private Woods woods;
    private int houseCounter;

    public House(Woods woods){
        this.woods = woods;
        this.houseCounter = 0;
    }

    public void buildHouse(){
        int result = woods.supplyWoods(40);
        if(result == 0){
            System.out.println("Fail buidling a house.");
        }
        else{
            houseCounter++;
            System.out.println("Succeed buidling a house. Current houses number: " + houseCounter);
        }
    }

    public int getHouseCounter(){
        return houseCounter;
    }

}
