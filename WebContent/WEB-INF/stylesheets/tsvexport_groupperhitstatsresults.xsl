<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet version="1.0"
	xmlns:xsl="http://www.w3.org/1999/XSL/Transform">
	<xsl:output method="text" omit-xml-declaration="yes" />
	
	<xsl:param name="lang" select="'nl'" />

	<xsl:param name="pos_name" select="'#'" />
	<xsl:param name="lemma_name" select="'#'" />
	<xsl:param name="title_name" select="'#'" />
	<xsl:param name="groupBy_name" select="'#'" />

	<xsl:template match="text()" />
	<xsl:template match="summary"></xsl:template>

	<xsl:template match="hitGroups"><xsl:text>Query&#x9;</xsl:text><xsl:value-of select="../summary/searchParam/patt" /><xsl:text>&#xa;</xsl:text>
		<xsl:choose>
			<xsl:when test="$lang = 'en'">
				<xsl:text>Duration&#x9;</xsl:text><xsl:value-of select="../summary/searchTime" /><xsl:text> ms</xsl:text><xsl:text>&#xa;</xsl:text>
				<xsl:text>Layout&#x9;N-gram list</xsl:text><xsl:text>&#xa;&#xa;</xsl:text>
				<xsl:text>Grouped by&#x9;</xsl:text><xsl:value-of select="$groupBy_name" /><xsl:text>&#xa;&#xa;</xsl:text>
				<xsl:value-of select="$groupBy_name" /><xsl:text>&#x9;Hits</xsl:text><xsl:text>&#xa;</xsl:text>
			</xsl:when>
			<xsl:otherwise>
				<xsl:text>Duur&#x9;</xsl:text><xsl:value-of select="../summary/searchTime" /><xsl:text> ms</xsl:text><xsl:text>&#xa;</xsl:text>
				<xsl:text>Layout&#x9;N-gram lijst</xsl:text><xsl:text>&#xa;&#xa;</xsl:text>
				<xsl:text>Gegroepeerd per&#x9;</xsl:text><xsl:value-of select="$groupBy_name" /><xsl:text>&#xa;&#xa;</xsl:text>
				<xsl:value-of select="$groupBy_name" /><xsl:text>&#x9;Hits</xsl:text><xsl:text>&#xa;</xsl:text>
			</xsl:otherwise>
		</xsl:choose>
		<xsl:for-each select="hitgroup">
			<xsl:value-of select="identityDisplay" /><xsl:text>&#x9;</xsl:text><xsl:value-of select="size" /><xsl:text>&#xa;</xsl:text>
		</xsl:for-each>
	</xsl:template>

</xsl:stylesheet>