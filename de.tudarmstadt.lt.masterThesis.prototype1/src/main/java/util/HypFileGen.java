package util;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

public class HypFileGen {

	public static void main(String[] args) {

		String line = "hyp1,d=1:1/1, hyp2,d=1:1/1, hyp3,d=1:1/1";
		String fo = "hyps-mwe.txt";
		File f = new File(fo);
		FileWriter fw;
		try {
			fw = new FileWriter(f);
			for (int i=0;i<6005;i++){
				fw.write(i+"\t"+line+"\n");
			}
			fw.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

}
