package org.aksw.linkeddata.qa.util;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;


public class Md5Utils {
	public static String md5sum(File file) throws IOException {
		FileInputStream fis = new FileInputStream(file);
		String md5 = null;
		try {
			md5 = org.apache.commons.codec.digest.DigestUtils.md5Hex(fis);
		} finally {
			fis.close();
		}

		return md5;
	}
	
/*
	public byte[] md5sum(File file) {
		MessageDigest md = MessageDigest.getInstance("MD5");

		
		InputStream is = new FileInputStream("file.txt");
		try {
		  is = new DigestInputStream(is, md);
		  // read stream to EOF as normal...
		}
		finally {
		  is.close();
		}
		byte[] result = md.digest();

		return result;
	}
*/
}
