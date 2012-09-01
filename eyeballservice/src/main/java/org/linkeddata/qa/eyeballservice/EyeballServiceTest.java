package org.linkeddata.qa.eyeballservice;

import static org.junit.Assert.*;

import org.junit.Test;

public class EyeballServiceTest {

	@Test
	public void testEyeball() {
		EyeballService ebs = new EyeballService();
		String result = ebs.eyeball("/home/joerg/java-workspace/eyeball-2.3/examples/bad-prefixes.rdf");
		
		if(!result.contains("bad namespace URI")){
			fail("Not yet implemented");
		}
		
	}

}
