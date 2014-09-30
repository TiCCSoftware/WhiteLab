<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet version="1.0"
	xmlns:xsl="http://www.w3.org/1999/XSL/Transform">
	<xsl:output method="text" omit-xml-declaration="yes" />
	
	<xsl:param name="lang" select="'nl'" />

	<xsl:param name="pos_name" select="'#'" />
	<xsl:param name="lemma_name" select="'#'" />
	<xsl:param name="title_name" select="'#'" />

	<xsl:template match="text()" />
	<xsl:template match="summary"></xsl:template>

	<xsl:template match="docInfos"></xsl:template>

	<xsl:template match="hits"><xsl:text>Query&#x9;</xsl:text><xsl:value-of select="../summary/searchParam/patt" /><xsl:text>&#xa;</xsl:text>
		<xsl:choose>
			<xsl:when test="$lang = 'en'">
				<xsl:text>Duration&#x9;</xsl:text><xsl:value-of select="../summary/searchTime" /><xsl:text> ms</xsl:text><xsl:text>&#xa;</xsl:text>
				<xsl:text>Total hits&#x9;</xsl:text><xsl:value-of select="../summary/numberOfHits" /><xsl:text>&#xa;</xsl:text>
				<xsl:text>Layout&#x9;Per hit</xsl:text><xsl:text>&#xa;&#xa;</xsl:text>
				<xsl:text>Left context&#x9;Hit text&#x9;Right context&#x9;Lemma&#x9;Part of Speech&#x9;Document title</xsl:text><xsl:text>&#xa;</xsl:text>
			</xsl:when>
			<xsl:otherwise>
				<xsl:text>Duur&#x9;</xsl:text><xsl:value-of select="../summary/searchTime" /><xsl:text> ms</xsl:text><xsl:text>&#xa;</xsl:text>
				<xsl:text>Totaal hits&#x9;</xsl:text><xsl:value-of select="../summary/numberOfHits" /><xsl:text>&#xa;</xsl:text>
				<xsl:text>Layout&#x9;Per hit</xsl:text><xsl:text>&#xa;&#xa;</xsl:text>
				<xsl:text>Context links&#x9;Hit text&#x9;Context rechts&#x9;Lemma&#x9;Woordsoort&#x9;Document titel</xsl:text><xsl:text>&#xa;</xsl:text>
			</xsl:otherwise>
		</xsl:choose>
		<xsl:for-each select="hit">
			<xsl:variable name="docPid" select="docPid/text()" />
			<xsl:variable name="docInfo" select="/blacklabResponse/docInfos/docInfo[@pid=$docPid]" />
			
			<xsl:call-template name="left"/>
			<xsl:text>&#x9;</xsl:text>
			<xsl:value-of select="match" />
			<xsl:text>&#x9;</xsl:text>
			<xsl:call-template name="right"/>
			<xsl:text>&#x9;</xsl:text>
			<xsl:value-of select="match/w/@*[name()=$lemma_name]" />
			<xsl:text>&#x9;</xsl:text>
			<xsl:value-of select="match/w/@*[name()=$pos_name]" />
			<xsl:text>&#x9;</xsl:text>
			<xsl:value-of select="$docInfo/*[name()=$title_name]" />
			<xsl:text>&#xa;</xsl:text>
		</xsl:for-each>
	</xsl:template>
	
	<xsl:template name="left">
		<xsl:for-each select="left/w">
    		<xsl:value-of select="." />
    		<xsl:if test="position() != last()">&#160;</xsl:if>
		</xsl:for-each>
	</xsl:template>
	
	<xsl:template name="right">
		<xsl:for-each select="right/w">
    		<xsl:value-of select="." />
    		<xsl:if test="position() != last()">&#160;</xsl:if>
		</xsl:for-each>
	</xsl:template>

</xsl:stylesheet>