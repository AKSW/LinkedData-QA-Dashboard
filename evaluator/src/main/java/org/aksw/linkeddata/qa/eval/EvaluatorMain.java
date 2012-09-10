package org.aksw.linkeddata.qa.eval;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.text.DateFormat;
import java.text.ParseException;

import org.aksw.commons.jena.ModelUtils;
import org.aksw.commons.util.datetime.ISO8601Utils;
import org.aksw.linkeddata.qa.domain.Evaluation;
import org.aksw.linkeddata.qa.domain.LinkingBranch;
import org.aksw.linkeddata.qa.domain.LinkingProject;
import org.aksw.linkeddata.qa.domain.Linkset;
import org.aksw.linkeddata.qa.domain.Repository;
import org.aksw.wrapper.eyeball.EyeballWrapper;
import org.ini4j.Ini;
import org.ini4j.Profile.Section;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.gson.Gson;
import com.hp.hpl.jena.rdf.model.Model;

public class EvaluatorMain {
	private static final Logger logger = LoggerFactory.getLogger(EvaluatorMain.class);
	
	public static void main(String[] args) throws NumberFormatException, IOException {
		File file = new File("/home/raven/data/metrics-repo/hourly.0/localhost/home/raven/Projects/Current/Eclipse/LinkedData-QA/reports/test/sampled/onlyout/dbpedia-linkedgeodata-city/Degree.dat");
		DataSeries dataSeries = DataSeriesParser.parse(file);
		
		Gson gson = new Gson();
		String json = gson.toJson(dataSeries);
	
		System.out.println(json);
	}
	
	
	public static void main5(String[] args) throws IOException, ParseException {
		
		File repoDir = new File("/home/raven/Desktop/repo");
		
		RepositoryRdfConverter converter = new RepositoryRdfConverter();
		Repository repository = converter.indexRepo(repoDir);		
		
		
		DateFormat dateFormat = ISO8601Utils.createDateFormat();
		
		for(LinkingProject project : repository.getLinkingProjects()) {
			System.out.println("|- " + project.getName());
			for(LinkingBranch branch : project.getBranches()) {
				System.out.println("    |- " + branch.getName());	
				for(Linkset linkset : branch.getLinksets()) {
				
					System.out.println("        |- " + linkset.getRevisionName() + " (" + dateFormat.format(linkset.getDate().getTime()) + ")");	
			
				
					for(Evaluation evaluation : linkset.getEvaluations()) {
						System.out.println("            |- " + evaluation.getAuthorName() + " (" + dateFormat.format(evaluation.getTimestamp().getTime()) + ")");	
		
						doEval(linkset, evaluation);
					}
				}
			}
		}
	}
	
	
	
	public static void doEval(Linkset linkset, Evaluation evaluation) {
		linkset.getFile();
	}
	
	
	
	
	public static void main2(String[] args)
		throws Exception
	{
		File iniFile = new File("config.ini");
		
		Ini ini = new Ini(iniFile);
		
		Section section = ini.get("eyeball");
		if(section == null) {
			throw new RuntimeException("Section 'eyeball' is missing");
		}
		
		
		String executableFileName = section.get("executable");
		if(executableFileName == null) {
			throw new RuntimeException("Eyeball executable not configured");
		}
		
		File executable = new File(executableFileName);
		if(!executable.exists()) {
			throw new FileNotFoundException("Configured Eyeball executable '" + executable.getAbsolutePath() + "' not found");
		}

		if(!executable.isFile()) {
			throw new RuntimeException("Configured Eyeball executable '" + executable.getAbsolutePath() + "' is not a file.");
		}
		
		
		EyeballWrapper eyeball = new EyeballWrapper(executable);
		
		File linksetFile = new File("/home/raven/Projects/Current/IntelliJ/24-7-platform/link-specifications/dbpedia-linkedgeodata-airport/links.nt");
		File positiveFile = new File("/home/raven/Projects/Current/IntelliJ/24-7-platform/link-specifications/dbpedia-linkedgeodata-airport/positive.nt");
		File negativeFile = new File("/home/raven/Projects/Current/IntelliJ/24-7-platform/link-specifications/dbpedia-linkedgeodata-airport/negative.nt");
		
		Model model = eyeball.check(linksetFile, null);
		
		model.write(System.out, "N3");

		
		Model linkset = ModelUtils.read(linksetFile, "N-TRIPLES");
		//Model positive = ModelUtils.read(positiveFile, "N-TRIPLES");
		//Model negative = ModelUtils.read(negativeFile, "N-TRIPLES");
		
		System.out.println("Check");
		NamespaceStats nsStats = LinkAnalyser.createNamespaceStats(linkset);
		
		System.out.println(nsStats);
		
		
		//LinkAnalyser.checkReverseLinksRefSet(linkset, positive);
	}
}
