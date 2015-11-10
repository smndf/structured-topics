package reader;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.StringWriter;

public class Reader {


	public void readFile(String arg) {

		InputStream is2 = getClass().getResourceAsStream(arg);
		BufferedReader mapText = new BufferedReader(new InputStreamReader(is2));
		String line = null;
		try {
			int i = 0;
			while ( (line = mapText.readLine()) != null && i<3){ 
				i++;
				System.out.println(line);
			}	
		} catch (NumberFormatException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}	
}
