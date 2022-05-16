package workshop;

import java.lang.Math;
import java.util.Arrays;

public class Woods {
    //create an array of int to store the number of the 3 types of wood.
    //[0] for chestnut, [1] for birch, [2] for pine.
    private int[] numStock;


    public Woods(){
        numStock = new int[] {0, 0, 0};
    }

    public synchronized void gatherWood(){
        int r = (int) (Math.random()*3);
        numStock[r]++;
    }

    public synchronized int supplyWoods(int woodDemand){
        int[] valueStock= {numStock[0] * 15, numStock[1] * 10, numStock[2] * 5};
        int sumStockValue = valueStock[0] + valueStock[1] + valueStock[2];

        //the array to store the value of wood for each type.
        int[] toSend = {0, 0, 0}; 
        int valueToSend = 0;

        if (sumStockValue < woodDemand){
            System.out.println("Failure. Not enough woods now.");
            return 0;
        }
        else{
            int i = 0;
            while(valueToSend < woodDemand){
                toSend[i]= Math.min(valueStock[i], woodDemand);
                valueStock[i] -= toSend[i];
                numStock[i] -= 1;
                valueToSend = toSend[0] + toSend[1] + toSend[2];
                i++;
            }
            System.out.println("Sending " + valueToSend + " woods.");
            return valueToSend;
        }
    }
    
    @Override
    public String toString() {
        return "Wood resources: " + Arrays.toString(numStock);
    }
}
