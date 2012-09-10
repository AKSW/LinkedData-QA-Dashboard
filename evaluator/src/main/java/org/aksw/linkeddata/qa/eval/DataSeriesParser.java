package org.aksw.linkeddata.qa.eval;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;


public class DataSeriesParser {
	
	public static DataSeries parse(File file) throws NumberFormatException, IOException {
		DataSeries result = new DataSeries();
		
		
		BufferedReader reader = new BufferedReader(new InputStreamReader(new FileInputStream(file)));
		
		try {
			boolean isFirstLine = true;
			String line;
			while((line = reader.readLine()) != null) {
				if(isFirstLine) {
					isFirstLine = false;
					continue;
				}
				
				String[] parts = line.split("\\s+");
				if(parts.length != 3) {
					throw new RuntimeException("In file " + file.getAbsolutePath() + ": Invalid line encountered: " + line);
				}
				
				Number value = Double.parseDouble(parts[0]);
				Number before = Double.parseDouble(parts[1]);
				Number after = Double.parseDouble(parts[1]);
		
				result.add(value, before, after);
				
			}
		} finally {
			reader.close();
		}
		
		return result;
	}
}
