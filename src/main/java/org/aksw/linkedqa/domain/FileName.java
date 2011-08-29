package org.aksw.linkedqa.domain;

import java.io.File;


/**
 * A helper class for splitting up filenames
 * 
 * @author Claus Stadler
 *
 */
public class FileName
{
	private String path;
	private String name;
	private String extension;
	
	
	public FileName(String path, String name, String extension) {
		super();
		this.path = path;
		this.name = name;
		this.extension = extension;
	}
	
	public String getPath() {
		return path;
	}
	
	public String getName() {
		return name;
	}
	
	public String getExtension() {
		return extension;
	}

	public static FileName create(String filename) {
		File file = new File(filename);
		//System.out.println("File = " + file);
		String path = file.getParentFile().getAbsolutePath();
		filename = file.getAbsolutePath().substring(file.getParentFile().getAbsolutePath().length() + 1);

		String name = filename;
		String extension = "";
		int dotIndex = filename.lastIndexOf('.');
		
		if(dotIndex >= 0) {
			name = filename.substring(0, dotIndex);
			extension = filename.substring(dotIndex + 1);
		}		
		
		return new FileName(path, name, extension);
	}

	
	public String getFullName() {
		return getName() + "." + getExtension();
	}
	
	@Override
	public String toString() {
		return "FileName [path=" + path + ", name=" + name + ", extension="
				+ extension + "]";
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result
				+ ((extension == null) ? 0 : extension.hashCode());
		result = prime * result + ((name == null) ? 0 : name.hashCode());
		result = prime * result + ((path == null) ? 0 : path.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		FileName other = (FileName) obj;
		if (extension == null) {
			if (other.extension != null)
				return false;
		} else if (!extension.equals(other.extension))
			return false;
		if (name == null) {
			if (other.name != null)
				return false;
		} else if (!name.equals(other.name))
			return false;
		if (path == null) {
			if (other.path != null)
				return false;
		} else if (!path.equals(other.path))
			return false;
		return true;
	}
	
	
}
