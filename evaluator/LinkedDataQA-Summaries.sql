CREATE VIEW filelink_duplicates AS
	SELECT file_id, link_id, COUNT(*) AS duplicate_count FROM filelinks GROUP BY file_id, link_id HAVING COUNT(*) > 1; 

CREATE VIEW filelink_unique_counts AS
	SELECT file_id, COUNT(DISTINCT link_id) AS unique_count FROM filelinks GROUP BY file_id; 
	
CREATE VIEW filelink_total_counts AS
	SELECT file_id, COUNT(link_id) AS raw_count FROM filelinks GROUP BY file_id;
	

CREATE VIEW evaluation_sizes AS
	SELECT a.id AS evaluation_id, c.unique_count AS linkset_size, d.unique_count AS pos_refset_size, e.unique_count AS neg_refset_size, d.unique_count + e.unique_count AS sample_size
		FROM evaluations a
		JOIN linksets b ON (b.id = a.linkset_id)
		JOIN filelink_unique_counts c ON (c.file_id = b.file_id)
		JOIN filelink_unique_counts d ON (d.file_id = a.pos_file_id)
		JOIN filelink_unique_counts e ON (e.file_id = a.neg_file_id);
		
CREATE VIEW evaluation_summaries AS
	SELECT a.evaluation_id, 

CREATE VIEW evaluation_walds AS
	SELECT id, 
	
	
CREATE TABLE file_summaries AS
	SELECT DISTINCT(COUNT link_id) AS distinct_count FROM 