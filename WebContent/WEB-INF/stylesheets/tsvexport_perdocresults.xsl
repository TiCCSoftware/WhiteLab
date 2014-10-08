<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform">
	<xsl:output method="text" omit-xml-declaration="yes" indent="no" />
	
	<xsl:param name="include_header" select="'true'" />
	<xsl:param name="n" select="''" />
	<xsl:param name="query" select="''" />
	<xsl:param name="filter" select="''" />
	<xsl:param name="total_hits" select="''" />
	<xsl:param name="total_docs" select="''" />
	<xsl:param name="total_exported" select="''" />
	<xsl:param name="layout" select="''" />
	<xsl:param name="per_doc" select="''" />
	<xsl:param name="document_id" select="''" />
	<xsl:param name="document_title" select="''" />
	<xsl:param name="collection" select="''" />
	<xsl:param name="hits" select="''" />
	<xsl:param name="title_name" select="''" />
	<xsl:param name="collection_name" select="''" />
	
	<xsl:template match="text()" />
	<xsl:template match="summary"></xsl:template>
	<xsl:template match="docs">
		<xsl:if test="$include_header = 'true'">
			<xsl:value-of select="$query" /><xsl:text>&#x9;</xsl:text><xsl:value-of select="../summary/searchParam/patt" /><xsl:text>&#xa;</xsl:text>
			<xsl:value-of select="$filter" /><xsl:text>&#x9;</xsl:text><xsl:value-of select="../summary/searchParam/filter" /><xsl:text>&#xa;</xsl:text>
			<xsl:value-of select="$total_hits" /><xsl:text>&#x9;</xsl:text><xsl:value-of select="../summary/numberOfHits" /><xsl:text>&#xa;</xsl:text>
			<xsl:value-of select="$total_docs" /><xsl:text>&#x9;</xsl:text><xsl:value-of select="../summary/numberOfDocs" /><xsl:text>&#xa;</xsl:text>
			<xsl:value-of select="$total_exported" /><xsl:text>&#x9;</xsl:text><xsl:value-of select="$n" /><xsl:text>&#xa;</xsl:text>
			<xsl:value-of select="$layout" /><xsl:text>&#x9;</xsl:text><xsl:value-of select="$per_doc" /><xsl:text>&#xa;&#xa;</xsl:text>
			<xsl:value-of select="$document_id" /><xsl:text>&#x9;</xsl:text><xsl:value-of select="$document_title" /><xsl:text>&#x9;</xsl:text><xsl:value-of select="$collection" /><xsl:text>&#x9;</xsl:text><xsl:value-of select="$hits" /><xsl:text>&#xa;</xsl:text>
		</xsl:if>
		<xsl:for-each select="doc">
			<xsl:value-of select="docPid" />
			<xsl:text>&#x9;</xsl:text>
			<xsl:value-of select="docInfo/*[name()=$title_name]" />
			<xsl:text>&#x9;</xsl:text>
			<xsl:value-of select="docInfo/*[name()=$collection_name]" />
			<xsl:text>&#x9;</xsl:text>
			<xsl:value-of select="numberOfHits" />
			<xsl:text>&#xa;</xsl:text>
		</xsl:for-each>
	</xsl:template>
</xsl:stylesheet>