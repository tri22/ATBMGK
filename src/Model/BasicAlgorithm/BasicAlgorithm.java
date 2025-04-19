package Model.BasicAlgorithm;

public interface BasicAlgorithm {
	boolean genKey();

	String encrypt(String text);

	String decrypt(String text);
	
	void loadKey();
	

}
