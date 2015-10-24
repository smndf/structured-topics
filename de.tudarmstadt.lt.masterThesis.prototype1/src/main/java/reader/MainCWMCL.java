package reader;

import java.io.IOException;

public class MainCWMCL {

	public static void main(String[] args) throws IOException {

		
		String fileName = "";
		if (args.length==0){
			fileName = "/Filtered1.txt";			
		} else {
			fileName = args[0];
		}
		System.out.println("fileName = " + fileName);
		
		ReaderCW fr = new ReaderCW();
		fr.readTextFile(fileName);
		
		/*
		ReaderMCLSparse rMCL = new ReaderMCLSparse();		
		int nbNodes = rMCL.getNumberNodes(fileName);
		System.out.println(nbNodes + " nodes");
		rMCL.readTextFile(fileName,nbNodes);
		 */
		
		
		//TestMCL testMCL = new TestMCL();
		//testMCL.testRun();
		//TestCW.test();
		
	}

}
