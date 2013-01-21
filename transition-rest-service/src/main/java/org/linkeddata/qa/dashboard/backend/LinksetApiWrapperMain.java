package org.linkeddata.qa.dashboard.backend;

import java.io.IOException;
import java.sql.SQLException;
import java.text.ParseException;
import java.util.Scanner;

public class LinksetApiWrapperMain {

	/**
	 * @param args
	 * @throws IOException 
	 * @throws SQLException 
	 * @throws ClassNotFoundException 
	 * @throws ParseException 
	 */
	public static void main(String[] args) throws IOException, SQLException, ClassNotFoundException, ParseException {
		// TODO Auto-generated method stub
		
		Scanner in = new Scanner(System.in);
		int choice;
		while(true)
		{
		System.out.println("Please select a choice:");
		System.out.println("To retrieve \"ALL\" link tasks, press <<1>> .");
		System.out.println("To update with \"RECENT\" link tasks, presss <<2>> .");
		System.out.println("To exit, presss <<3>> .");
		choice= in.nextInt();
		if(choice == 1 || choice == 2)
			break;
		if(choice == 3)
			System.exit(0);
		
		}
		
		LinksetApiWrapper linkingExtractor=new LinksetApiWrapper("linkSetDB","root","mofo");// set in constructor these properties inside the object
		linkingExtractor.preparingDatabase(choice);// this function m akes the back up and clears the data
		linkingExtractor.getDatedDirectories("http://linker.sindice.com/runtime-results/");
	}

}
