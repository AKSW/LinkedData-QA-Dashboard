--create database linkeddata_qa;

TRUNCATE projects CASCADE;
TRUNCATE users CASCADE;
TRUNCATE files CASCADE;

-- Cleanup of the summary schema

DROP TABLE IF EXISTS messages;
DROP TYPE IF EXISTS loglevel;
DROP TYPE IF EXISTS targettype;


-- Cleanup of the core Schema

DROP TABLE IF EXISTS evaluations;
DROP TABLE IF EXISTS linkset_links;
DROP TABLE IF EXISTS linksets;
DROP TABLE IF EXISTS branches;
DROP TABLE IF EXISTS projects;
DROP TABLE IF EXISTS users;
DROP TABLE IF EXISTS filelinks;
DROP TABLE IF EXISTS links;
DROP TABLE IF EXISTS files;


-- Creation of the core schema

CREATE TABLE files (
	id text PRIMARY KEY
);

CREATE TABLE links (
	id text PRIMARY KEY,
	
	s text NOT NULL,
	p text NOT NULL,
	o text NOT NULL
);

CREATE TABLE filelinks (
	file_id text,
	link_id text,
	sequence_id BIGINT NOT NULL,
	
	FOREIGN KEY (file_id) REFERENCES files(id),
	FOREIGN KEY (link_id) REFERENCES links(id)
);


CREATE TABLE users (
	id text PRIMARY KEY,
	name text NOT NULL
	-- creation_tstamp TIMESTAMP
);


CREATE TABLE projects (
	id text PRIMARY KEY,
	name text NOT NULL,
	user_id text,
	creation_tstamp TIMESTAMP NOT NULL,
	
	FOREIGN KEY (user_id) REFERENCES users(id) 
);


CREATE TABLE branches (
	id text PRIMARY KEY,
	project_id text,
	name text NOT NULL,
	user_id text NOT NULL,
	creation_tstamp TIMESTAMP NOT NULL,
	
	FOREIGN KEY (project_id) REFERENCES projects(id),
	FOREIGN KEY (user_id) REFERENCES users(id)
);


CREATE TABLE linksets (
	id text PRIMARY KEY,
	branch_id text,
	user_id text,
	creation_tstamp TIMESTAMP NOT NULL,
	file_id text NOT NULL,
	
	FOREIGN KEY (branch_id) REFERENCES branches(id),
	FOREIGN KEY (user_id) REFERENCES users(id),
	
	FOREIGN KEY (file_id) REFERENCES files(id)
);


CREATE TABLE linkset_links (
	linkset_id text,
	link_id text,

	FOREIGN KEY (linkset_id) REFERENCES linksets(id),
	FOREIGN KEY (link_id) REFERENCES links(id)
);



CREATE TABLE evaluations (
	id text PRIMARY KEY,
	linkset_id text,
	user_id text,
	creation_tstamp TIMESTAMP NOT NULL,
	
	pos_file_id text NOT NULL,
	neg_file_id text NOT NULL,
	
	
	FOREIGN KEY (linkset_id) REFERENCES linksets(id),
	FOREIGN KEY (pos_file_id) REFERENCES files(id),
	FOREIGN KEY (neg_file_id) REFERENCES files(id)
);

-- Views


-- Latest assessment for a linking project



-- Summaries

--DROP TABLE IF EXISTS evaluation_summary;


CREATE TYPE targettype AS ENUM('link', 'file');
CREATE TYPE loglevel AS ENUM('trace', 'debug', 'info', 'warn', 'error', 'fatal');


CREATE TABLE messages (
	id text PRIMARY KEY, -- Suggestion: hash(triple + message) to avoid duplicate messages on same triple
	target_id text,
	target_type targettype NOT NULL,
	message text NOT NULL,
	level loglevel NOT NULL, -- trace, debug, info(0), warn, error, fatal  
	source_id text NOT NULL,
	tstamp TIMESTAMP NOT NULL
	
	--UNIQUE(link_id, message),
);


--CREATE TABLE file_summary (
--	file_id PRIMARY KEY,
--	total_triple_count INT NOT NULL,
--	actual_triple_count INT NOT NULL,
--	duplicate_count INT NOT NULL, -- Number of distinct duplicate triples
--	
--	FOREIGN KEY file_id REFERENCES files(id)
--);

--CREATE TABLE evaluation_summary (
--	evaluation_id PRIMARY KEY, 
--	
--	precision_est_low REAL NOT NULL,
--	precision_est_high REAL NOT NULL,
--	
--	precision_min REAL NOT NULL,
--	recall_min REAL NOT NULL,
--	
--	FOREIGN KEY (evaluation_id) REFERENCES evaluations(id)
--);




