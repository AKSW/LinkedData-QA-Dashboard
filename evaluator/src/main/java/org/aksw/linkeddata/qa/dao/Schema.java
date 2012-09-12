package org.aksw.linkeddata.qa.dao;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.Map;

import org.aksw.commons.util.jdbc.ForeignKey;
import org.aksw.commons.util.jdbc.JdbcUtils;
import org.aksw.commons.util.jdbc.PrimaryKey;
import org.aksw.commons.util.jdbc.Relation;

import com.google.common.collect.Multimap;

public class Schema {
	
	private Connection conn;
	
	private Map<String, Relation> relations = null;
	private Map<String, PrimaryKey> primaryKeys = null;
	private Multimap<String, ForeignKey> foreignKeys = null;
	
	public Schema(Connection conn) {
		this.conn = conn;
	}
	

	public void preload() throws SQLException {
		getRelations();
		getPrimaryKeys();
		getForeignKeys();
	}
	
	
	public void clearCache() {
		relations = null;
		primaryKeys = null;
		foreignKeys = null;
	}
	
	
	public Map<String, Relation> getRelations() throws SQLException {
		if(relations == null) {
			synchronized(this) {
				if(relations == null) {
					relations = JdbcUtils.fetchColumns(conn);
				}
			}
		}
		
		return relations;
	}

	public Map<String, PrimaryKey> getPrimaryKeys() throws SQLException {
		if(primaryKeys == null) {
			synchronized(this) {
				if(primaryKeys == null) {
					primaryKeys = JdbcUtils.fetchPrimaryKeys(conn);
				}
			}
		}
		
		return primaryKeys;
	}

	
	public Multimap<String, ForeignKey> getForeignKeys() throws SQLException {
		if(foreignKeys == null) {
			synchronized(this) {
				if(foreignKeys == null) {
					foreignKeys = JdbcUtils.fetchForeignKeys(conn);
				}
			}
		}
		
		return foreignKeys;
	}
}