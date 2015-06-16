<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform">
	<xsl:output method="html" omit-xml-declaration="yes" />
	
    <!-- How to transform hit group results to HTML -->
    
    <xsl:template match="summary|docInfos" />
    
	<xsl:template match="hits/hit">
		<div class="row-fluid large-16 medium-16 small-16">
			<div class="large-6 medium-6 small-6 columns text-right inline-concordance">... <xsl:value-of select="left" /></div>
			<div class="large-4 medium-4 small-4 columns text-center inline-concordance"><b><xsl:value-of select="match" /></b></div>
			<div class="large-6 medium-6 small-6 columns inline-concordance"><xsl:value-of select="right" /> ...</div>
		</div>
	</xsl:template>
</xsl:stylesheet>