package org.aksw.linkeddata.qa.eval;

import java.io.File;
import java.sql.Connection;

import org.aksw.commons.util.jdbc.ColumnsReference;
import org.aksw.commons.util.slf4j.LoggerCount;
import org.aksw.wrapper.eyeball.EyeballWrapper;
import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.GnuParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Options;
import org.ini4j.Ini;
import org.ini4j.Profile.Section;
import org.postgresql.ds.PGSimpleDataSource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.jolbox.bonecp.BoneCPConfig;
import com.jolbox.bonecp.BoneCPDataSource;



public class EvaluatorMain {
	private static final Logger logger = LoggerFactory.getLogger(EvaluatorMain.class);
	

	private static final Options cliOptions = new Options();

	/*
	 * @param exitCode
	 */
	public static void printHelpAndExit(int exitCode) {
		HelpFormatter formatter = new HelpFormatter();
		formatter.printHelp(EvaluatorMain.class.getName(), cliOptions);
		System.exit(exitCode);
	}


	
	/*
	public static void main(String[] args) throws IOException, ParseException {
		File repoDir = new File("/home/raven/Desktop/repo");

		evalRepository(repoDir);
		
	}
	
	
	public static void main6(String[] args) throws NumberFormatException, IOException {
		File file = new File("/home/raven/data/metrics-repo/hourly.0/localhost/home/raven/Projects/Current/Eclipse/LinkedData-QA/reports/test/sampled/onlyout/dbpedia-linkedgeodata-city/Degree.dat");
		DataSeries dataSeries = DataSeriesParser.parse(file);
		
		Gson gson = new Gson();
		String json = gson.toJson(dataSeries);
	
		System.out.println(json);
	}
	*/
	
	
	
		
	
	
	
	public static void main(String[] args)
		throws Exception
	{
		LoggerCount logger = new LoggerCount(EvaluatorMain.logger);
		
		File iniFile = new File("config.ini");
		
		Ini ini = new Ini(iniFile);
		
		Section section = ini.get("eyeball");
		if(section == null) {
			logger.error("Section 'eyeball' is missing");
		}
		
		
		String executableFileName = section.get("executable");
		if(executableFileName == null) {
			logger.error("Eyeball executable not configured");
		}
		
		File executable = new File(executableFileName);
		if(!executable.exists()) {
			logger.error("Configured Eyeball executable '" + executable.getAbsolutePath() + "' not found");
		}

		if(!executable.isFile()) {
			logger.error("Configured Eyeball executable '" + executable.getAbsolutePath() + "' is not a file.");
		}
		
		
		CommandLineParser cliParser = new GnuParser();

		cliOptions.addOption("f", "directory", true, "Repository directory");
		
		cliOptions.addOption("d", "database", true, "Database name");
		cliOptions.addOption("u", "username", true, "");
		cliOptions.addOption("p", "password", true, "");
		cliOptions.addOption("h", "hostname", true, "");

		
		CommandLine commandLine = cliParser.parse(cliOptions, args);

		
		// Parsing of command line args
		String repositoryDirname = commandLine.getOptionValue("f");
		
		if(repositoryDirname == null) {
			logger.error("Repository directory not specified");			
		}

		File repositoryFile = null;
		
		if(repositoryDirname != null) {
		
			repositoryFile = new File(repositoryDirname);
			
			if(!repositoryFile.exists()) {
				logger.error("Repository directory '" + repositoryFile.getAbsolutePath() + "' does not exist");
			} else if(!repositoryFile.isDirectory()) {
				logger.error("Repository '" + repositoryFile.getAbsolutePath() + " is not a directory");			
			}
		
		}
		
		
		
		String hostName = commandLine.getOptionValue("h", "localhost");
		String dbName = commandLine.getOptionValue("d", "");
		String userName = commandLine.getOptionValue("u", "");
		String passWord = commandLine.getOptionValue("p", "");

		PGSimpleDataSource dataSourceBean = new PGSimpleDataSource();

		dataSourceBean.setDatabaseName(dbName);
		dataSourceBean.setServerName(hostName);
		dataSourceBean.setUser(userName);
		dataSourceBean.setPassword(passWord);

		BoneCPConfig cpConfig = new BoneCPConfig();
		cpConfig.setDatasourceBean(dataSourceBean);
		/*
		cpConfig.setJdbcUrl(dbconf.getDbConnString()); // jdbc url specific to your database, eg jdbc:mysql://127.0.0.1/yourdb
		cpConfig.setUsername(dbconf.getUsername()); 
		cpConfig.setPassword(dbconf.getPassword());
		*/
		
		cpConfig.setMinConnectionsPerPartition(5);
		cpConfig.setMaxConnectionsPerPartition(20);
//		cpConfig.setMinConnectionsPerPartition(1);
//		cpConfig.setMaxConnectionsPerPartition(1);
		
		cpConfig.setPartitionCount(1);
		//BoneCP connectionPool = new BoneCP(cpConfig); // setup the connection pool	

		BoneCPDataSource dataSource = new BoneCPDataSource(cpConfig);
		
		/*
		ComboPooledDataSource pooledDataSource = new ComboPooledDataSource();
		pooledDataSource.
		*/
		
		Connection conn = dataSource.getConnection();
		
		
			
		logger.info("Errors: " + logger.getErrorCount() + ", Warnings: " + logger.getWarningCount());
		if(logger.getErrorCount() != 0) {
			logger.info("Errors encountered - Bailing out.");
			printHelpAndExit(1);
		}
		
		EyeballWrapper eyeball = new EyeballWrapper(executable);
		
		RepositoryDbWriter evaluator = new RepositoryDbWriter(repositoryFile, eyeball, conn);
				
		evaluator.evalRepository();
		
		
		conn.close();

		/*
		File linksetFile = new File("/home/raven/Projects/Current/IntelliJ/24-7-platform/link-specifications/dbpedia-linkedgeodata-airport/links.nt");
		File positiveFile = new File("/home/raven/Projects/Current/IntelliJ/24-7-platform/link-specifications/dbpedia-linkedgeodata-airport/positive.nt");
		File negativeFile = new File("/home/raven/Projects/Current/IntelliJ/24-7-platform/link-specifications/dbpedia-linkedgeodata-airport/negative.nt");
		
		Model model = eyeball.check(linksetFile, null);
		
		model.write(System.out, "N3");

		
		Model linkset = ModelUtils.read(linksetFile, "N-TRIPLES");
		//Model positive = ModelUtils.read(positiveFile, "N-TRIPLES");
		//Model negative = ModelUtils.read(negativeFile, "N-TRIPLES");
		
		//System.out.println("Check");
		//NamespaceStats nsStats = LinkAnalyser.createNamespaceStats(linkset);
		
		//System.out.println(nsStats);
		
		
		//LinkAnalyser.checkReverseLinksRefSet(linkset, positive);
		 */
	}
}
