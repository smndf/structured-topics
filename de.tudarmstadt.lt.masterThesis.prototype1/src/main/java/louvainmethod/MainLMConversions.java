package louvainmethod;
import java.io.IOException;


public class MainLMConversions {

	public static void main(String[] args) {
		
		
		
		LMGraphConversion lmgc = new LMGraphConversion();
		try {
			lmgc.convertToLM("/Filtered1.txt");
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		/*
		String inputFile = "/Filtered1LM_lvl2.txt";
		String mapFile = "/mapItoS.txt";
		Clusters c = new Clusters();
		c.convertFromLM(inputFile, mapFile);
		*/
	}

}
