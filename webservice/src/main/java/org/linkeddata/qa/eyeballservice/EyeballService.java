package org.linkeddata.qa.eyeballservice;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.ws.rs.FormParam;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;

import com.hp.hpl.jena.assembler.assemblers.RDBModelAssembler;
import com.hp.hpl.jena.eyeball.ExportModel;
import com.hp.hpl.jena.eyeball.Eyeball;
import com.hp.hpl.jena.eyeball.Inspector;
import com.hp.hpl.jena.eyeball.Renderer;
import com.hp.hpl.jena.eyeball.inspectors.PrefixInspector;
import com.hp.hpl.jena.eyeball.inspectors.URIInspector;
import com.hp.hpl.jena.eyeball.renderers.N3Renderer;
import com.hp.hpl.jena.eyeball.util.Setup;
import com.hp.hpl.jena.ontology.OntModelSpec;
import com.hp.hpl.jena.ontology.impl.OntModelImpl;
import com.hp.hpl.jena.util.FileManager;

import jena.eyeball;

@Path("/eyeball")
public class EyeballService {

	List<String> confparams = null;

	public EyeballService() {
		// Using contructor to configure eyeballservice

		confparams = Collections.unmodifiableList(
		// Arrays.asList( "-inspectors", "cardinality")
				new ArrayList<String>());

	}

	@GET
	@Path("eyeball")
	public String eyeball(@QueryParam("filename")String filename) {

		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		PrintStream ps = new PrintStream(baos);

		List<String> params = new ArrayList<String>(confparams);
		params.addAll(Arrays.asList("-check", filename));

		Setup.declareEyeballAssemblers();

		eyeball eye = new eyeball(params.toArray(new String[0]));
		boolean allPassed = eye.run(ps, ExportModel.ignore);

		return baos.toString();
	}
	
	
	/*
	 * Set<Inspector> inspectors = new HashSet<Inspector>();
		inspectors.add(new PrefixInspector());
		inspectors.add(new URIInspector());
		


		
		
		Eyeball eb = new Eyeball(Inspector.Operations.create(inspectors),new OntModelImpl(OntModelSpec.OWL_DL_MEM_RDFS_INF, FileManager.get().loadModel(filename)), new N3Renderer(null));
			
	 */

}
