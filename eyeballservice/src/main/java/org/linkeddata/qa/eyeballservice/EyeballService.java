package org.linkeddata.qa.eyeballservice;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import com.hp.hpl.jena.eyeball.ExportModel;
import com.hp.hpl.jena.eyeball.util.Setup;

import jena.eyeball;

public class EyeballService {
	
	List<String> confparams = null;
	
	public EyeballService() {
		//Using contructor to configure eyeballservice
		
		confparams = Collections.unmodifiableList(
			//	Arrays.asList(				"-inspectors", "cardinality")
				new ArrayList<String>()
				);
		
		
		
	}
	
	
	
	public String eyeball(String filename){
		
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		PrintStream ps = new PrintStream(baos);
		
		List<String> params = new ArrayList<String>(confparams);
		params.addAll(Arrays.asList("-check",filename));
		
		
		Setup.declareEyeballAssemblers();   

      eyeball eye = new eyeball( params.toArray(new String[0]) );
       boolean allPassed = eye.run( ps, ExportModel.ignore );
		
	
		
		
		
		
		return baos.toString();
	}

}
