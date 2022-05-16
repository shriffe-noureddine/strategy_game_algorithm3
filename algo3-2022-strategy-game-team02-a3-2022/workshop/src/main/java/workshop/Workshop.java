package workshop;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.core.MediaType;
import java.util.Scanner;

import io.javalin.Javalin;

public class Workshop {
	public static void main(String[] args) {
		Scanner scanner = new Scanner(System.in);
		Woods woods = new Woods();
		Worker worker = new Worker(woods);
		House house = new House(woods);
		Client client = ClientBuilder.newClient();
		Thread woodWorker = startWoodWorker(worker);
		Javalin app = Javalin.create().start(7070);

		//to send wood
		app.get("/wood/{quantity}", 
		  ctx -> {
			int w = Integer.parseInt(ctx.pathParam("quantity"));
			int woodToSend = woods.supplyWoods(w);
			ctx.result("" + woodToSend);
		  });
		//to send house counter
		app.get("/house", ctx -> ctx.result("" + house.getHouseCounter()));
		

		//game loop
		System.out.print("Type: 1: Add a worker to gather woods. 2: Build a house. 3: Get the status. 4: Quit.");
		Boolean game = true;
		while(game){
			int input = scanner.nextInt();
			switch (input){
				case 1:
					Boolean workerAvailable = client.target("http://localhost:5000")
					.path("worker/woodworker")
					.request(MediaType.APPLICATION_JSON)
					.get(Boolean.class);
					if(workerAvailable){
						worker.addWorker();
					}
					break;
				case 2:
					house.buildHouse();
					break;
				case 3:
					getStatus(woods, worker, house);
					break;
				case 4:
					game = false;
					worker.gameIsOver();
					break;
			}
		}
		//waiting for the thread to terminate
		try {
			woodWorker.join();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		client.close();
		scanner.close();
		app.close();
	}

	private static Thread startWoodWorker(Worker worker){
		Thread thread = new Thread(worker);
		thread.start();
		return thread;
	}

	public static void getStatus(Woods woods, Worker worker, House house){
		//print woods number:
		System.out.print(woods);
		System.out.println("for chestnut, birch and pine respectively");
		//print the number of workers that are gathering woods now.
		System.out.println("Workers gathering woods: " + worker.getWorkerCounter());
		//print house num
		System.out.println("Houses Built: " + house.getHouseCounter());
	}
}