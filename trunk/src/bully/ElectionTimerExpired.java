package bully;

import java.util.Calendar;
import java.util.TimerTask;


public class ElectionTimerExpired extends TimerTask{
	Process process;
	
	ElectionTimerExpired(Process process){
		this.process = process;
	}
	
	public void run() {
        System.out.println("[" +Calendar.getInstance().getTimeInMillis()+ "]Election timer expired......., id " + process.getIdentifier() + 
        		" sending time out message");
        Message timeOut = new TimeOutAnswerMessage(process.getIdentifier());
        process.clear();
        process.sendMessage(timeOut);
    }
}
