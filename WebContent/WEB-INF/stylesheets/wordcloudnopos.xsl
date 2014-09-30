<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform">
	<xsl:output method="text" encoding="UTF-8" media-type="text/plain"/>
	
	<xsl:template match="empty">[]</xsl:template>

	<xsl:template match="error">[]</xsl:template>

	<xsl:template match="text()" />
	<xsl:template match="summary"></xsl:template>

	<xsl:template match="hitGroups">[<xsl:for-each select="hitgroup">
		<xsl:variable name="lemma" select="identityDisplay"></xsl:variable>
		<xsl:variable name="quot">"</xsl:variable>
		<xsl:choose>
		<xsl:when test="$lemma != $quot">{ lemma : "<xsl:value-of select="identityDisplay" />", freq : <xsl:value-of select="size" /> },</xsl:when>
		<xsl:otherwise>{ lemma : "\<xsl:value-of select="identityDisplay" />", freq : <xsl:value-of select="size" /> },</xsl:otherwise>
		</xsl:choose>
		</xsl:for-each>]</xsl:template>

</xsl:stylesheet>