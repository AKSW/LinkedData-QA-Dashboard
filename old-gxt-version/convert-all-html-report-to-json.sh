for dir in `find ../LinkedData-QA/reports/sampled/onlyout -type d`; do
	
	reportFile="$dir/report.html"
	
	if [ -f "$reportFile" ]; then 

		reportDate=`stat -c '%Y' "$reportFile"`
	
		reportContent=`./convert-html-report-to-json.sh "$reportFile"`
		
		data='{"direction": 1, "sampled": true, "sampleSize": 150, "reportDate": '$reportDate', "reportContent": '$reportContent'}'
		
		targetFile="$dir/report.json"
		echo "$targetFile"
		
		echo "$data" > "$dir/report.json"
	fi
done