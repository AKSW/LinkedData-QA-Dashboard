package org.aksw.wrapper.eyeball;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Map;

import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.ModelFactory;


class ReaderRunnable
	implements Runnable
{
	private InputStream in;
	private OutputStream out;
	
	public ReaderRunnable(InputStream in, OutputStream out) {
		this.in = in;
		this.out = out;
	}
	
	@Override
	public void run() {
		try {
	        byte[] buffer = new byte[1024];
	        int n;
	        while((n = in.read(buffer)) != -1) {
	            out.write(buffer, 0, n);
	        }
		} catch(Exception e) {
			throw new RuntimeException(e);
		} finally {
			// TODO Close
		}
	}
	
}

public class EyeballWrapper
{
	private File executable;
	
	public EyeballWrapper(File executable)
	{
		this.executable = executable;
	}
	
	public Model check(File file, Map<String, String> options) throws IOException, InterruptedException
	{
		String cmd = executable.getAbsolutePath();
        
        Process process = Runtime.getRuntime().exec(new String[] {cmd, "-check ", file.getAbsolutePath(), "-assume", "owl", "-render", "n3"});

        InputStream in = process.getInputStream();
        ByteArrayOutputStream out = new ByteArrayOutputStream();

        // Start a thread for writing out errors
        Thread thread = new Thread(new ReaderRunnable(process.getErrorStream(), System.err));
        thread.start();
        
        
        byte[] buffer = new byte[1024];
        int n;
        while((n = in.read(buffer)) != -1) {
            out.write(buffer, 0, n);
        }

        process.waitFor();
        /*
        if(process.exitValue() != 0) {
            throw new RuntimeException(out.toString());
        }
        */

        String str = out.toString();
        Model model = ModelFactory.createDefaultModel();
        model.read(new ByteArrayInputStream(str.getBytes()), "http://dummy.org/", "N3");

        return model;		
	}
}
