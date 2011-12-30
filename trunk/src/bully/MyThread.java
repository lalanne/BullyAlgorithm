package bully;

import java.util.Calendar;

public class MyThread extends Thread{
	private Protocol protocol;
	
	MyThread(Protocol protocol){
		this.protocol = protocol;
	}
	
	public void run() {
		System.out.println("[" +Calendar.getInstance().getTimeInMillis()+ "] Initializing process " +
						" id: " + this.getId());
		execute();
	}
	
	public void execute(){
		protocol.execute();
	}
}
