<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet version="1.0"
	xmlns:xsl="http://www.w3.org/1999/XSL/Transform">
	<xsl:output method="text" omit-xml-declaration="yes" />
	
	<xsl:param name="include_header" select="'true'" />
	<xsl:param name="n" select="''" />
	<xsl:param name="query" select="''" />
	<xsl:param name="filter" select="''" />
	<xsl:param name="total_hits" select="''" />
	<xsl:param name="total_docs" select="''" />
	<xsl:param name="total_groups" select="''" />
	<xsl:param name="total_exported" select="''" />
	<xsl:param name="layout" select="''" />
	<xsl:param name="grouped_per_doc" select="''" />
	<xsl:param name="docs" select="''" />
	<xsl:param name="grouped_header" select="''" />

	<xsl:template match="text()" />
	<xsl:template match="summary"></xsl:template>

	<xsl:template match="docGroups">
		<xsl:if test="$include_header = 'true'">
			<xsl:value-of select="$query" /><xsl:text>&#x9;</xsl:text><xsl:value-of select="../summary/searchParam/patt" /><xsl:text>&#xa;</xsl:text>
			<xsl:value-of select="$filter" /><xsl:text>&#x9;</xsl:text><xsl:value-of select="../summary/searchParam/filter" /><xsl:text>&#xa;</xsl:text>
			<xsl:value-of select="$total_hits" /><xsl:text>&#x9;</xsl:text><xsl:value-of select="../summary/numberOfHits" /><xsl:text>&#xa;</xsl:text>
			<xsl:value-of select="$total_docs" /><xsl:text>&#x9;</xsl:text><xsl:value-of select="../summary/numberOfDocs" /><xsl:text>&#xa;</xsl:text>
			<xsl:value-of select="$total_groups" /><xsl:text>&#x9;</xsl:text><xsl:value-of select="../summary/numberOfGroups" /><xsl:text>&#xa;</xsl:text>
			<xsl:value-of select="$total_exported" /><xsl:text>&#x9;</xsl:text><xsl:value-of select="$n" /><xsl:text>&#xa;</xsl:text>
			<xsl:value-of select="$layout" /><xsl:text>&#x9;</xsl:text><xsl:value-of select="$grouped_per_doc" /><xsl:text>&#xa;</xsl:text>
			<xsl:value-of select="$grouped_header" /><xsl:text>&#x9;</xsl:text><xsl:value-of select="../summary/searchParam/group" /><xsl:text>&#xa;&#xa;</xsl:text>
			<xsl:value-of select="../summary/searchParam/group" /><xsl:text>&#x9;</xsl:text><xsl:value-of select="$docs" /><xsl:text>&#xa;</xsl:text>
		</xsl:if>
		<xsl:for-each select="docgroup">
			<xsl:value-of select="identityDisplay" /><xsl:text>&#x9;</xsl:text><xsl:value-of select="size" /><xsl:text>&#xa;</xsl:text>
		</xsl:for-each>
	</xsl:template>

</xsl:stylesheet>