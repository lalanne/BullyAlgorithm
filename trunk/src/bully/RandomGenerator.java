package bully;
import java.util.Random;

public class RandomGenerator {
	
	private Random randomGenerator = new Random();
	private final int maxRandomNumber;
	RandomGenerator(final int maxRandomNumber){
		this.maxRandomNumber = maxRandomNumber;
	}
	
	final int get(){
		return randomGenerator.nextInt(this.maxRandomNumber);
	}

}
