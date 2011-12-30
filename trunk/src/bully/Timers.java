package bully;

import java.util.Timer;
import java.util.TimerTask;

public class Timers {
	private TimerTask timerElectionExpired;
	private TimerTask timerCoordinatorExpired;
	private final int ANSWER_TIMEOUT_MS = 320;
	private final int COORDINATOR_TIMEOUT_MS = 160;
	private Timer electionTimer = null;
	private Timer coordinatorTimer = null;
	private Process process;
	
	public void setProcess(Process process){
		this.process = process;
	}
	
	public void stopAnswerMessageTimer() {
		if(electionTimer != null){
			electionTimer.cancel();
		}
	}
	
	public void startAnswerMessageTimer() {
		timerElectionExpired = new ElectionTimerExpired(process);
		electionTimer = new Timer();
		electionTimer.schedule(timerElectionExpired, ANSWER_TIMEOUT_MS);
	}
	
	public void startCoordinatorMessageTimer(){
		timerCoordinatorExpired = new CoordinatorTimerExpired(process);
		coordinatorTimer = new Timer();
		coordinatorTimer.schedule(timerCoordinatorExpired, COORDINATOR_TIMEOUT_MS);
	}
	
	public void stopCoordinatorMessageTimer() {
		if(coordinatorTimer != null){
			coordinatorTimer.cancel();
		}
	}
	
	public Timer getElectionTimer(){
		return electionTimer;
	}
}
