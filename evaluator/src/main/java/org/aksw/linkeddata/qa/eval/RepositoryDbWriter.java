package org.aksw.linkeddata.qa.eval;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.text.DateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;

import org.aksw.commons.jena.reader.NTripleIterator;
import org.aksw.commons.sparql.api.cache.extra.SqlUtils;
import org.aksw.commons.sparql.api.model.QueryExecutionFactoryModel;
import org.aksw.commons.util.datetime.ISO8601Utils;
import org.aksw.commons.util.jdbc.ColumnsReference;
import org.aksw.commons.util.strings.StringUtils;
import org.aksw.linkeddata.qa.dao.Inserter;
import org.aksw.linkeddata.qa.dao.Schema;
import org.aksw.linkeddata.qa.domain.Evaluation;
import org.aksw.linkeddata.qa.domain.LinkingBranch;
import org.aksw.linkeddata.qa.domain.LinkingProject;
import org.aksw.linkeddata.qa.domain.Linkset;
import org.aksw.linkeddata.qa.domain.Repository;
import org.aksw.linkeddata.qa.util.Md5Utils;
import org.aksw.wrapper.eyeball.EyeballWrapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.hp.hpl.jena.graph.Triple;
import com.hp.hpl.jena.query.QueryExecution;
import com.hp.hpl.jena.query.QuerySolution;
import com.hp.hpl.jena.query.ResultSet;
import com.hp.hpl.jena.rdf.arp.ParseException;
import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.ModelFactory;
import com.hp.hpl.jena.rdf.model.RDFErrorHandler;
import com.hp.hpl.jena.rdf.model.Statement;
import com.hp.hpl.jena.shared.JenaException;
import com.hp.hpl.jena.sparql.util.ModelUtils;




class ParseResult {
	
	private long totalTripleCount; // Including duplicates
	private Map<Statement, Long> cardinalities;
	private Model model;
	
	
	public ParseResult(long totalTripleCount, Map<Statement, Long> cardinalities,
			Model model) {
		super();
		this.totalTripleCount = totalTripleCount;
		this.cardinalities = cardinalities;
		this.model = model;
	}


	public long getTotalTripleCount() {
		return totalTripleCount;
	}


	public Map<Statement, Long> getCardinalities() {
		return cardinalities;
	}


	public Model getModel() {
		return model;
	}
	
}

class MyRDFErrorHandler
	implements RDFErrorHandler {

    private Logger logger = LoggerFactory.getLogger(MyRDFErrorHandler.class);
	private boolean silent = false;
	
	public MyRDFErrorHandler(Logger logger) {
		this.logger = logger;
	}

	/*
    public MyRDFErrorHandler() {
    }
    */

    public void warning(Exception e) {
        if (!silent) logger.warn(ParseException.formatMessage(e));
    }

    public void error(Exception e) {
    	if (!silent) logger.error(ParseException.formatMessage(e));
    }

    public void fatalError(Exception e) {
    	if (!silent) logger.error(ParseException.formatMessage(e));
        throw e instanceof RuntimeException 
            ? (RuntimeException) e
            : new JenaException( e );
    }
}


public class RepositoryDbWriter {
	private static final Logger logger = LoggerFactory.getLogger(RepositoryDbWriter.class);
	
	/*
	 * TODO Don't reference eyeball directly, but use some indirection via some plugin architecture 
	 */
	private EyeballWrapper eyeball;
	
	
	private Connection conn;
	
	private Map<File, String> fileToHash = new HashMap<File, String>();
	//private SetMultimap<String, File> hashToFiles = HashMultimap.create();
	
	private File repoDir;
	
	
	private String getFileHash(File file) throws IOException {
		String result = fileToHash.get(file);
		
		if(result == null) {
			result = Md5Utils.md5sum(file);
			fileToHash.put(file, result);
		}
		
		return result;
	}
	
	
	private Schema schema; 
			
	public RepositoryDbWriter(File repoDir, EyeballWrapper eyeball, Connection conn) {
		this.eyeball = eyeball;
		this.conn = conn;
		this.repoDir = repoDir;
		
		this.schema = new Schema(conn);
	}

	
	public void evalRepository() throws Exception {
		
		
		RepositoryParser converter = new RepositoryParser();
		Repository repository = converter.indexRepo(repoDir);		
		
		
		DateFormat dateFormat = ISO8601Utils.createDateFormat();
		
		
		/*
		for(LinkingProject project : repository.getLinkingProjects()) {						
			System.out.println("|- " + project.getName());
			for(LinkingBranch branch : project.getBranches()) {
				System.out.println("    |- " + branch.getName());	
				for(Linkset linkset : branch.getLinksets()) {
				
					System.out.println("        |- " + linkset.getRevisionName() + " (" + dateFormat.format(linkset.getDate().getTime()) + ")");	
			
				
					for(Evaluation evaluation : linkset.getEvaluations()) {
						System.out.println("            |- " + evaluation.getAuthorName() + " (" + dateFormat.format(evaluation.getTimestamp().getTime()) + ")");	
		
						doEval(linkset);
					}
				}
			}
		}
		*/
		for(LinkingProject project : repository.getLinkingProjects()) {						
			System.out.println("|- " + project.getId());
			for(LinkingBranch branch : project.getBranches()) {
				System.out.println("    |- " + branch.getId());	
				for(Linkset linkset : branch.getLinksets()) {
				
					System.out.println("        |- " + linkset.getId());	
			
				
					for(Evaluation evaluation : linkset.getEvaluations()) {
						System.out.println("            |- " + evaluation.getId());	
		
						//doEval(linkset);
					}
				}
			}
		}
		
		
		boolean dryRun = false;
		
		if(dryRun) {
			return;
		}


		for(LinkingProject project : repository.getLinkingProjects()) {
			write(project);
		}
		
	}
	
	
	public boolean doesIdExist(String tableName, Object id) throws SQLException {
		Object tmp = SqlUtils.execute(conn, "SELECT id FROM " + tableName + " WHERE id = ?", Object.class, id);
		boolean result = tmp != null;
		
		return result;
	}

	public void writeUser(String userId, String userName) throws SQLException {		
		if(doesIdExist("users", userId)) {
			return;
		}
		
		SqlUtils.execute(conn, "INSERT INTO users(id, name) VALUES (?, ?)", Void.class, userId, userName);
	};

	

	public void write(LinkingProject project) throws SQLException, FileNotFoundException {
		if(doesIdExist("projects", project.getId())) {
			return;
		}
		
		String userId = "unknown";
		writeUser(userId, "Unknown");
		
		SqlUtils.execute(
			conn,
			"INSERT INTO projects(id, name, user_id, creation_tstamp) VALUES (?, ?, ?, ?)",
			Void.class,
			project.getId(),
			project.getName(),
			userId,
			new Timestamp(2011, 1, 1, 0, 0, 0, 0)
		);
				
			
		for(LinkingBranch branch : project.getBranches()) {
			write(branch);
		}
	}
	
	public void write(LinkingBranch branch) throws SQLException, FileNotFoundException {
		if(doesIdExist("branches", branch.getId())) {
			return;
		}
		
		String userId = "unknown";
		writeUser(userId, "Unknown");


		SqlUtils.execute(
			conn,
			"INSERT INTO branches(id, name, user_id, creation_tstamp) VALUES (?, ?, ?, ?)",
			Void.class,
			branch.getId(),
			branch.getName(),
			userId,
			new Timestamp(2011, 1, 1, 0, 0, 0, 0)
		);

		
		for(Linkset linkset : branch.getLinksets()) {
			write(branch.getId(), linkset);
		}
		
	}
	
	
	public String fileToId(File file) {
		String result = file.getAbsolutePath().substring(repoDir.getAbsolutePath().length() + 1);
		return result;
	}
	
	
	public void write(String branchId, Linkset linkset) throws SQLException, FileNotFoundException {
		if(doesIdExist("linksets", linkset.getId())) {
			return;
		}
		
		write(linkset.getId(), linkset.getFile());

		String userId = RepositoryParser.strToId(linkset.getSpecAuthorName());
		writeUser(userId, linkset.getSpecAuthorName());
		
		String fileId = fileToId(linkset.getFile());
		writeFile(fileId);
		
		SqlUtils.execute(
			conn,
			"INSERT INTO linksets(id, branch_id, user_id, creation_tstamp, file_id) VALUES (?, ?, ?, ?, ?)",
			Void.class,
			linkset.getId(),
			branchId,
			userId,
			new Timestamp(2011, 1, 1, 0, 0, 0, 0),
			fileId
		);

	}

	public Timestamp now() {
		Date date = new Date();
		return new Timestamp(date.getTime());
	}

	public void writeMessage(String targetType, String target, String loglevel, String msg, String source, Timestamp tstamp) throws SQLException {
		String hashId = StringUtils.md5Hash(targetType + " " + target +  " " + msg);
		
		if(doesIdExist("messages", hashId)) {
			return;
		}
		
		SqlUtils.execute(
			conn,
			"INSERT INTO messages(id, target_id, target_type, message, loglevel, source_id, tstamp) VALUES (?, ?, ?, ?, ?, ?, ?)",
			Void.class,
			hashId,
			targetType,
			target,
			loglevel,
			msg,
			source,			
			tstamp
		);
		
	}
	
	public void writeFile(String fileId) throws SQLException {
		if(doesIdExist("files", fileId)) {
			return;
		}

		SqlUtils.execute(
			conn,
			"INSERT INTO files(id) VALUES (?)",
			Void.class,
			fileId
		);		
	}
	

	public void write(String fileId, Triple triple, long sequence_id) throws SQLException {

		// TODO: Convert the triple to N-Triple first
		String tripleStr = triple.toString();
		String hashId = StringUtils.md5Hash(tripleStr);

		if(!triple.getSubject().isURI() || !triple.getPredicate().isURI() || !triple.getObject().isURI()) {
			writeMessage("file", fileId, "warn", "Triple " + tripleStr + " is not a valid link because of non-URIs", "loader", now());
		}

		
		if(!doesIdExist("links", hashId)) {
			SqlUtils.execute(
				conn,
				"INSERT INTO links(id, s, p, o) VALUES (?, ?, ?, ?)",
				Void.class,
				hashId,
				triple.getSubject().getURI(),
				triple.getPredicate().getURI(),
				triple.getObject().getURI()
			);		
		}		

		if(!doesIdExist("files", fileId)) {
			writeFile(fileId);
		}		

		
		SqlUtils.execute(
			conn,
			"INSERT INTO filelinks(file_id, link_id, sequence_id) VALUES (?, ?, ?)",
			Void.class,
			fileId,
			hashId,
			sequence_id
		);		
		
		
	}
	
	public void write(String fileId, File file) throws SQLException, FileNotFoundException {
		
		
		if(!doesIdExist("files", fileId)) {
			writeFile(fileId);
		}		

		
		
		RDFErrorHandler errorHandler = new MyRDFErrorHandler(logger);
		
		InputStream in = new FileInputStream(file);
		NTripleIterator it = new NTripleIterator(in, "http://example.org", errorHandler);
		
		
		
		
		
		ColumnsReference tripleTarget = new ColumnsReference("links", "id", "s", "p", "o");
		Inserter tripleInserter = new Inserter(tripleTarget, schema);
		

		ColumnsReference filelinkTarget = new ColumnsReference("filelinks", "file_id", "link_id", "sequence_id");
		Inserter filelinkInserter = new Inserter(filelinkTarget, schema);
		
		
		
		long sequence_id = 1;
		while(it.hasNext()) {
			Triple triple = it.next();
			
			// TODO: Convert the triple to N-Triple first
			String tripleStr = triple.toString();
			String hashId = StringUtils.md5Hash(tripleStr);

			
			tripleInserter.add(
				hashId,
				triple.getSubject().getURI(),
				triple.getPredicate().getURI(),
				triple.getObject().getURI()
			);
			

			filelinkInserter.add(
				fileId,
				hashId,
				sequence_id
			);
			
			
			//write(fileId, triple, sequence_id);			
			++sequence_id;
			
			
			if(sequence_id % 1000 == 0 || !it.hasNext()) {
				tripleInserter.flush(conn);
				filelinkInserter.flush(conn);
			}			
		}
	}

	
	
	
	public ParseResult parseNtriples(File file) throws IOException {
		//org.openjena.riot.RiotReader.createParserNTriples(input, sink)
		//LoggerCount logger = new LoggerCount(this);
		RDFErrorHandler errorHandler = new MyRDFErrorHandler(logger);
		
		InputStream in = new FileInputStream(file);
		//Set<Statement> cache = new HashSet<Statement>();
		Map<Statement, Long> cardinalities = new HashMap<Statement, Long>();
		Model model = ModelFactory.createDefaultModel();
		
		long totalTripleCount = 0;
		
		try {
			NTripleIterator it = new NTripleIterator(in, "http://example.org", errorHandler);
			
			while(it.hasNext()) {
				Triple triple = it.next();
				
				++totalTripleCount;
				
				Statement stmt = ModelUtils.tripleToStatement(model, triple);
				
				Long count = cardinalities.get(stmt);
				Long newCount = count == null ? 1 : count + 1;
				
				
				cardinalities.put(stmt, newCount);
				model.add(stmt);
			}
			
			
			// Only retain duplicates
			Iterator<Entry<Statement, Long>> itEntry = cardinalities.entrySet().iterator();
			while(itEntry.hasNext()) {
				Entry<Statement, Long> entry = itEntry.next();
				if(entry.getValue().equals(1)) {
					itEntry.remove();
				}				
			}
			
			ParseResult result = new ParseResult(totalTripleCount, cardinalities, model);
			return result;
		} finally {
			in.close();
		}
		
	}
	
	public void validateFile(File file) throws IOException, InterruptedException {
		Model m = eyeball.check(file, null);
		m.write(System.out, "N-TRIPLES");
		
		QueryExecutionFactoryModel qef = new QueryExecutionFactoryModel(m);
		
		QueryExecution qe = qef.createQueryExecution("Prefix eye:<http://jena.hpl.hp.com/Eyeball#> Select Distinct ?s { ?s ?p ?o }");
		try {
		
			ResultSet rs = qe.execSelect();
			while(rs.hasNext()) {
				QuerySolution qs = rs.next();
				
			}
		} finally {
			qe.close();
		}
	}
	
	public Model readModel(File file) throws IOException {
		Model result = ModelFactory.createDefaultModel();
		InputStream in = new FileInputStream(file);
		try {
			result.read(in, null, "N-TRIPLES");
		} finally {
			in.close();
		}
		
		return result;
	}
	
		
	public void checkLinks(Model model) {
		NamespaceStats stats = LinkAnalyser.createNamespaceStats(model);
		LinkAnalyser.checkReverseLinks(stats);
		
	}
	
	public void doEval(Linkset linkset) throws Exception {
		
		// Validate all involved files
		validateFile(linkset.getFile());
		Model links = readModel(linkset.getFile());
		checkLinks(links);
		
		for(Evaluation evaluation : linkset.getEvaluations()) {
			validateFile(evaluation.getPositiveRefsetFile());
			validateFile(evaluation.getNegativeRefsetFile());			
			
			Model posRefset = readModel(evaluation.getPositiveRefsetFile());
			Model negRefset = readModel(evaluation.getNegativeRefsetFile());

			checkLinks(posRefset);
			checkLinks(negRefset);
			
			Model reverseLinksPos = LinkAnalyser.checkReverseLinksRefSet(links, posRefset);
			Model reverseLinksNeg = LinkAnalyser.checkReverseLinksRefSet(links, negRefset);
			
		}
		
				
		
		
		
		//{ ?s a , model)
	}

	

}
