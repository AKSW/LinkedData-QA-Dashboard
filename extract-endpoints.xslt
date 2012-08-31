<xsl:stylesheet version='2.0'
	xmlns:xsl='http://www.w3.org/1999/XSL/Transform'>
	
	<xsl:output method="text" version="1.0" encoding="UTF-8" indent="no" />

	<xsl:variable name="newline">
		<xsl:text>&#xa;</xsl:text>
	</xsl:variable>


	<xsl:template match="/">
		<xsl:for-each select="//DataSource/Param[@name='endpointURI']">
			<xsl:value-of select="@value" /><xsl:value-of select="$newline" />
		</xsl:for-each>
	</xsl:template>
</xsl:stylesheet>

