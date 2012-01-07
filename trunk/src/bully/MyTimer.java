package bully;

import java.util.Timer;
import java.util.TimerTask;

public class MyTimer {
	private Timer timer = null;
	TimerTask task = null;
	final long time;
	
	MyTimer(TimerTask task, final long time){
		timer = new Timer();
		this.task = task;
		this.time = time;
	}
	
	public void start(){
		timer.schedule(task, time);
	}
	
	public void stop(){
		timer.cancel();
	}
}
