package util;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;

public class SplitTextFileLetters {
	
	/* assumes input file with already alphabetically sorted lines
	 * splits the file into several files according to the lines first letter 
	 * (file for 'a', file for 'b'...)
	**/
	public static void main(String[] args) throws Exception {

		if (args.length==1){
			String inputFile = args[0];
			FileReader fileReader = new FileReader(inputFile);
			BufferedReader bufferedReader = new BufferedReader(fileReader);
			String inputLine;
			List<String> lineList = new ArrayList<String>();
			char currentLetter = 'a';
			char previousLetter = 'a';
			while ((inputLine = bufferedReader.readLine()) != null) {
				previousLetter = currentLetter;
				currentLetter = inputLine.toLowerCase().charAt(0);
				if (currentLetter!=previousLetter){
					createTextFileByLetter(previousLetter,inputFile,lineList);
					lineList.clear();					
				}
				lineList.add(inputLine);					
			}
			fileReader.close();			
		} else {
			System.out.println("need input file as argument");
		}

	}

	private static void createTextFileByLetter(char letter,
			String inputFile, List<String> lineList) {
		String outputFile = inputFile.substring(0, inputFile.length()-10) + "-"+letter+".csv";
		FileWriter fileWriter;
		try {
			fileWriter = new FileWriter(outputFile);
			PrintWriter out = new PrintWriter(fileWriter);
			for (String outputLine : lineList) {
				out.println(outputLine);
			}
			out.flush();
			out.close();
			fileWriter.close();		
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
}
