package org.aksw.linkedqa.server.linksets;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.List;

import org.aksw.commons.sparql.api.core.QueryExecutionFactory;
import org.aksw.commons.sparql.api.http.QueryExecutionFactoryHttp;

import com.hp.hpl.jena.query.QuerySolution;
import com.hp.hpl.jena.query.ResultSet;



interface ILinksetRepository
{
	
}

interface LinksetSource {
	public List<String> getModifiedLinksets(Date date);
}


class MdsLinksetSource
	implements LinksetSource 
{
	private QueryExecutionFactory factory;
	
	public MdsLinksetSource(QueryExecutionFactory factory) {
		this.factory = factory;
	}
	
	public List<String> getModifiedLinksets(Date date) {
		String query = "Select ?s { ?s a <http://rdfs.org/ns/void#Linkset> . Filter(?s > '" + date + "') . }";
		
		ResultSet rs = factory.createQueryExecution(query).execSelect();

		List<String> result = new ArrayList<String>();
		while(rs.hasNext()) {
			QuerySolution qs = rs.next();
			result.add(qs.getResource("s").toString());
		}
		
		return result;
	}	
}


/**
 * Creates snapshots from a given source into a given repository.
 * 
 * @author raven
 *
 */
public class LinksetSnapshotCreater {

	
	public static void main(String[] args) throws Exception {
		QueryExecutionFactory factory = new QueryExecutionFactoryHttp("http://mds.lod-cloud.net/sparql", new HashSet<String>());
		
		MdsLinksetSource source = new MdsLinksetSource(factory);
		List<String> linksets = source.getModifiedLinksets(new Date());
		
		
	}
}


