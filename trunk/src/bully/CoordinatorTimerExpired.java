package bully;

import java.util.Calendar;
import java.util.TimerTask;

public class CoordinatorTimerExpired extends TimerTask{
	Process process;
	
	CoordinatorTimerExpired(Process process){
		this.process = process;
	}
	
	public void run() {
        System.out.println("[" +Calendar.getInstance().getTimeInMillis()+ "]Coordinator timer expired......., id " + process.getIdentifier() + 
        		" sending time out message");
        Message timeOut = new TimeOutCoordinatorMessage(process.getIdentifier());
        process.cleanQueue();
        process.sendMessage(timeOut);
    }
}
