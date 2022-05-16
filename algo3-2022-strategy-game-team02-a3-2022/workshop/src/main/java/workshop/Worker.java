package workshop;
import java.util.concurrent.TimeUnit;

public class Worker implements Runnable {
    private Woods woods;
	private int workerCounter;
	private boolean gameOver;

    public Worker(Woods woods) {
        this.woods = woods;
		this.workerCounter = 0;
		this.gameOver = false;
    }
    @Override
    public void run() {
        while(!gameOver){
			for(int i = 0; i < workerCounter; i++){
				woods.gatherWood();
			}
			wait1Sec();
			//System.out.println(woods);
		}
    }

	public void gameIsOver() {
		gameOver = true;
	}	

	private static void wait1Sec() {
		try {
			TimeUnit.SECONDS.sleep(1);
		}
		catch(InterruptedException e) {
		}
	}

	public void addWorker(){
		workerCounter++; 
	}
	public int getWorkerCounter(){
		return workerCounter;
	}
}