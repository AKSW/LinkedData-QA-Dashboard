package org.aksw.linkedqa.shared;

import java.io.Serializable;

public class MyException
	extends Exception
	implements Serializable
{
	private String msg;
	
	public MyException() {
		
	}
	
	public MyException(String msg) {
		this.msg = msg;
	}
	
	
	@Override
	public String toString() {
		return msg;
	}
	
}
