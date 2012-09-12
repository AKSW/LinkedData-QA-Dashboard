package org.aksw.linkeddata.qa.eval;

import java.io.File;
import java.io.IOException;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.Map;

import org.aksw.commons.util.IniUtils;
import org.aksw.commons.util.slf4j.LoggerCount;
import org.aksw.linkeddata.qa.domain.Evaluation;
import org.aksw.linkeddata.qa.domain.LinkingBranch;
import org.aksw.linkeddata.qa.domain.LinkingProject;
import org.aksw.linkeddata.qa.domain.Linkset;
import org.aksw.linkeddata.qa.domain.Repository;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.ModelFactory;

public class RepositoryParser {

	private static final Logger logger = LoggerFactory.getLogger(RepositoryParser.class);
	
	//private DateFormat dateFormat = ISO8601Utils.createDateFormat();
	private DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ssZ");
	
	/**
	 * An eval dir consists of
	 * metadata.init (creation data, author name)
	 * positive.nt
	 * negative.nt
	 * (sample.nt) -> could be computed automatically if not exists 
	 * 
	 */
	
	public static String strToId(String str) {
		String result = org.aksw.commons.util.strings.StringUtils.urlEncode(str.trim().toLowerCase().replaceAll("\\s+", " "));
		return result;
	}

	
	/**
	 * a link qa dir is comprised of:
	 * 
	 * summary.json
	 * degree.json
	 * cluster....json
	 * 
	 */
	
	public static boolean requireFile(File file, String name) {
		boolean result = file.exists();
		
		if(!result) {
			logger.warn(name + " '" + file.getAbsolutePath() + " not found");
		}
		
		return result;
	}
	
	/**
	 * A linkset dir has the structure
	 * metadata.ini
	 * links.nt
	 * evals/[0-n]/
	 * linkqa/[0-m]/ (link qa reports, e.g. indegree, cluster coefficient, ...)
	 * 
	 * 
	 * 
	 * @param linksetDir
	 * @return
	 * @throws ParseException 
	 * @throws IOException 
	 */
	public Linkset indexLinkset(String branchId, File linksetDir) throws ParseException, IOException {
		logger.debug("Processing linking project dir '" + linksetDir + "'");

		String linksetId = branchId + "-" + strToId(linksetDir.getName());
		String name = linksetId;

		LoggerCount loggerCount = new LoggerCount(logger);
		
		File metadataFile = new File(linksetDir.getAbsolutePath() + "/metadata.ini");
		if(!metadataFile.exists()) {
			loggerCount.error("No revision metadata found for " + linksetDir.getAbsolutePath() + " - Skipping");
		}
		
		File linksetFile = new File(linksetDir.getAbsolutePath() + "/links.nt");
		if(!linksetFile.exists()) {
			loggerCount.error("No linkset file found " + linksetFile + " - Skipping");
		}
		
		if(loggerCount.getErrorCount() > 0) {
			return null;
		}
		
		
		Map<String, String> metadata = IniUtils.loadIniFile(metadataFile);
		if(!StringUtils.isEmpty(metadata.get("name"))) {
			name = metadata.get("name");
		}

		String authorName = StringUtils.isEmpty(metadata.get("author"))
				? "unknown"
				: metadata.get("author");

		
		String dateStr = metadata.get("timestamp");
		if(StringUtils.isEmpty(dateStr)) {
			logger.warn("No timestamp for '" + linksetDir + "' - skipping");
		}
		
		Date date = dateFormat.parse(dateStr);
		GregorianCalendar cal = new GregorianCalendar();
		cal.setTime(date);
		
		//Ini
		
		Linkset linkset = new Linkset();
		linkset.setId(linksetId);
		linkset.setSpecAuthorName(authorName);
		linkset.setFile(linksetFile);
		linkset.setRevisionName(name);
		linkset.setDate(cal);

		
		/*
		 * TODO HACK Evaluation creation - use proper sub dirs for that 
		 */
		
		File positiveRefsetFile = new File(linksetDir.getAbsolutePath() + "/positive.nt");
		if(!positiveRefsetFile.exists()) {
			loggerCount.warn("No refset file found " + positiveRefsetFile.getAbsolutePath() + " - Skipping");
		}
		
		File negativeRefsetFile = new File(linksetDir.getAbsolutePath() + "/positive.nt");
		if(!negativeRefsetFile.exists()) {
			loggerCount.warn("No refset file found " + negativeRefsetFile.getAbsolutePath() + " - Skipping");
		}

		if(positiveRefsetFile.exists() && negativeRefsetFile.exists()) {
			String evalLocalId = strToId(authorName);
			String evalId = linksetId + "-eval-" + evalLocalId;
			
			Evaluation evaluation = new Evaluation();
			evaluation.setId(evalId);
			evaluation.setTimestamp(cal);
			evaluation.setAuthorName(authorName);
			evaluation.setPositiveRefsetFile(positiveRefsetFile);
			evaluation.setNegativeRefsetFile(negativeRefsetFile);

			linkset.getEvaluations().add(evaluation);
		}
		
		/*
		for(File revisionDir : projectDir.listFiles()) {
			
		}
		*/
		
		return linkset;
	}
	
	/**
	 * A linking project consists of several linkset revisions.
	 * 
	 * 
	 * FIXME What about the spec files? They could be revisioned too.
	 * Or at least it can be hashed, so that we can check which revisions used the same spec.
	 * 
	 * 
	 * 
	 * 
	 * @param file
	 * @throws IOException 
	 * @throws ParseException 
	 */
	public LinkingProject indexLinkingProject(File projectDir) throws IOException, ParseException
	{
		logger.debug("Processing linking project dir '" + projectDir + "'");

		String id = strToId(projectDir.getName());
		String name = projectDir.getName();


		File metadataFile = new File(projectDir.getAbsolutePath() + "/metadata.ini");

		if(metadataFile.exists()) {
		
			Map<String, String> metadata = IniUtils.loadIniFile(metadataFile);
			if(StringUtils.isEmpty(metadata.get("name"))) {
				name = metadata.get("name");
			}
			
			String dateStr = metadata.get("timestamp");
			if(StringUtils.isEmpty(dateStr)) {
				logger.warn("No timestamp for '" + projectDir + "' - skipping");
			}

			Date date = dateFormat.parse(dateStr);
			GregorianCalendar cal = new GregorianCalendar();
			cal.setTime(date);
		}
		
				
		LinkingProject project = new LinkingProject();
		project.setId(id);
		project.setName(name);

		
		String branchName = "default";
		String branchId = project.getId() + "-" + branchName;

		for(File file : projectDir.listFiles()) {
			if(!file.isDirectory()) {
				continue;
			}
			
			Linkset linkset = indexLinkset(branchId, file);
			if(linkset != null) {
				
				LinkingBranch branch = new LinkingBranch();
				branch.setId(branchId);
				branch.setName(branchName);
				branch.getLinksets().add(linkset);
				
				project.getBranches().add(branch);
			}
		}
		
		
		return project;
	}
		
	public Repository indexRepo(File root) throws IOException, ParseException {
		Repository result = new Repository();

		for(File file : root.listFiles()) {
			if(!file.isDirectory()) {
				logger.warn("Skipping non linking project directory: " + file);
			}
			
			
			LinkingProject project = indexLinkingProject(file);
			
			if(project != null) {
				result.getLinkingProjects().add(project);
			}
			
		}
		
		
		return result;
	}
	
	public void indexDir(File directory) {
		// Read the metadata file
		
		
		
	}
	
	public Model convert(File directory) {
		
		
		Model result = ModelFactory.createDefaultModel();
		
		
		return result;
	}
	
}
