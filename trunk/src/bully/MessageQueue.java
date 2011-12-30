package bully;

import java.util.concurrent.LinkedBlockingQueue;

public class MessageQueue {
	private LinkedBlockingQueue<Message> messageQueue = new LinkedBlockingQueue<Message>();
	
	public void put(Message message){
		this.messageQueue.offer(message);
	}
	
	public void clear(){
		messageQueue.clear();
	}
	
	public Message get(){
		Message message;
		try{
			message = this.messageQueue.take();			
		}
		catch(InterruptedException ie){
			return null;//check later.
		}		
		return message;
	}
}
