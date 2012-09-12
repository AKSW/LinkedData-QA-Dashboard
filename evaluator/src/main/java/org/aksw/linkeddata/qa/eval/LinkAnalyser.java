package org.aksw.linkeddata.qa.eval;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import org.apache.commons.collections15.Transformer;
import org.apache.commons.collections15.iterators.TransformIterator;

import com.hp.hpl.jena.graph.Node;
import com.hp.hpl.jena.graph.Triple;
import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.ModelFactory;
import com.hp.hpl.jena.rdf.model.Property;
import com.hp.hpl.jena.rdf.model.Statement;
import com.hp.hpl.jena.util.iterator.Filter;
import com.hp.hpl.jena.vocabulary.OWL;

/*
class HashMapFactory<K, V>
	implements Factory<HashMap<K, V>>
{
	@Override
	public HashMap<K, V> create() {
		return new HashMap<K, V>();
	}
}

class MapAccessor<K, V> 
{
	public void getOrCreate(Map<K, V>)
}
*/

class NamespaceStats {
	private Map<Node, Map<String, Map<String, Integer>>> propertyToSourceToTargetToCount = new HashMap<Node, Map<String, Map<String, Integer>>>();

	
	public void add(Node property, String source, String target) {
		Map<String, Map<String, Integer>> sourceToTargetToCount = propertyToSourceToTargetToCount.get(property);
		if(sourceToTargetToCount == null) {
			sourceToTargetToCount = new HashMap<String, Map<String, Integer>>();
			propertyToSourceToTargetToCount.put(property, sourceToTargetToCount);
		}		
		
		Map<String, Integer> targetToCount = sourceToTargetToCount.get(source);
		if(targetToCount == null) {
			targetToCount = new HashMap<String, Integer>();
			sourceToTargetToCount.put(source, targetToCount);
		}
		
		Integer value = targetToCount.get(target);
		if(value == null) {
			value = 1;
		} else {
			++value;
		}
		targetToCount.put(target, value);
	}

	public Map<Node, Map<String, Map<String, Integer>>> getMap() {
		return this.propertyToSourceToTargetToCount;
	}

	@Override
	public String toString() {
		return "" + propertyToSourceToTargetToCount;
	}
}

class SymmetricPropertyFilter
	extends Filter<Property>
{
	private Set<Property> set;
	
	
	public SymmetricPropertyFilter() {
		this.set = new HashSet<Property>();
	}
	
	public SymmetricPropertyFilter(Set<Property> set) {
		this.set = set;
	}
	
	public Set<Property> getSet() {
		return set;
	}
	
	@Override
	public boolean accept(Property property) {
		return set.contains(property);
	}
	
	
	public static SymmetricPropertyFilter createDefaultInstance() {
		SymmetricPropertyFilter result = new SymmetricPropertyFilter();
		
		result.getSet().add(OWL.sameAs);
		
		return result;
	}
	
}


public class LinkAnalyser {
	
	public static NamespaceStats createNamespaceStats(Model model) {
		Iterator<Triple> it =
			new TransformIterator<Statement, Triple>(
				model.listStatements().toSet().iterator(),
				new Transformer<Statement, Triple>() {
					@Override
					public Triple transform(Statement input) {
						return input.asTriple();
					}					
				}
			);
			
		return createNamespaceStats(it);
	}
	
	public static NamespaceStats createNamespaceStats(Iterator<Triple> itTriple) {
		NamespaceStats result = new NamespaceStats();
		
		while(itTriple.hasNext()) {
			Triple triple = itTriple.next();

			if(triple.getSubject().isURI() || triple.getObject().isURI()) {
				result.add(triple.getPredicate(), triple.getSubject().getNameSpace(), triple.getObject().getNameSpace());
			}
		}
		
		return result;
	}
	
	/**
	 * Check whether there are links where the direction seems to be reversed.
	 * E.g.
	 * <http://dbpedia.org/ontology/Place> owl:sameAs <http://linkedgeodata.org/ontology/Place> .
	 * <http://linkedgeodata.org/ontology/City> owl:sameAs <http://dbpedia.org/ontology/City . 
	 * 
	 * TODO: Even better: Create a histogram between the namespaces, grouped by property.
	 * Either: Map<Property, Map<From, Map<To, count>>>>
	 * Or as a table:
	 * Property | Source Namespace | Target Namespace | count
	 * 
	 * 
	 * @param model
	 */
	public void checkReverseLinks(Model model) {

		model.getGraph().find(null, null, null).toSet();
		
	}

	public static void checkReverseLinks(NamespaceStats stats) {
		Map<Node, Map<String, Map<String, Integer>>> propertyToSourceToTargetToCount = stats.getMap();
		
		for(Entry<Node, Map<String, Map<String, Integer>>> propertyEntry : propertyToSourceToTargetToCount.entrySet()) {
			
			Map<String, Map<String, Integer>> sourceToTargetToCount = propertyEntry.getValue();
			
			for(Entry<String, Map<String, Integer>> sourceEntry : sourceToTargetToCount.entrySet()) {
				String sourceNs = sourceEntry.getKey();
				
				//for(Map<String, Integer>)
				Map<String, Integer> targetToCount = sourceEntry.getValue();
				
				
				
				for(Entry<String, Integer> targetEntry : targetToCount.entrySet()) {
					
					String targetNs = targetEntry.getKey();
					Integer count = targetEntry.getValue();
					
					
					Map<String, Integer> reverseMap = sourceToTargetToCount.get(targetNs);
					if(reverseMap == null) {
						continue;
					}
					
					Integer reverseCount = reverseMap.get(sourceNs);
					
					// TODO: Return some data
					System.out.println(sourceNs + " --- " + targetNs + ": " + count + " <> " + reverseCount);
				}				
			}
		}
		
		
		
	}

	/**
	 * 
	 * 
	 * @param model
	 * @return
	 */
	public static Model sortDirection(Model model, Filter<Property> isSymmetricProperty) {
		Model result = ModelFactory.createDefaultModel();
		
		for(Statement stmt : model.listStatements().toSet()) {
			if(!isSymmetricProperty.accept(stmt.getPredicate())) {
				continue;
			}
			
			
			String subjectStr = stmt.getSubject().toString();
			String objectStr = stmt.getObject().toString();
			
			int c = subjectStr.compareTo(objectStr);
			
			if(c <= 0 || !stmt.getObject().isResource()) {
				result.add(stmt.getSubject(), stmt.getPredicate(), stmt.getObject());
			} else {
				result.add(stmt.getObject().asResource(), stmt.getPredicate(), stmt.getSubject());
			}
		}
		
		return result;
	}
	
	
	/** 
	 * Returns the set of links of the refset that exist in reverse direction in the linkset
	 * 
	 * @param linkset
	 * @param refset
	 */
	public static Model checkReverseLinksRefSet(Model linkset, Model refset) {
		Model result = ModelFactory.createDefaultModel();
		
		for(Statement stmt : refset.listStatements().toSet()) {
			if(stmt.getObject().isResource()) {
				if(!linkset.contains(stmt) &&
					linkset.contains(stmt.getObject().asResource(), stmt.getPredicate(), stmt.getSubject())) {
					result.add(stmt);
				}
			}
		}
		return result;
	}
	
	
	public void evaluate(Model linkset, Model refset) {
		
	}
}
