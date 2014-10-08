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
	<xsl:param name="total_exported" select="''" />
	<xsl:param name="layout" select="''" />
	<xsl:param name="per_hit" select="''" />
	<xsl:param name="context_left" select="''" />
	<xsl:param name="context_right" select="''" />
	<xsl:param name="lemma" select="''" />
	<xsl:param name="pos" select="''" />
	<xsl:param name="document_title" select="''" />
	<xsl:param name="pos_name" select="''" />
	<xsl:param name="title_name" select="''" />
	
	<xsl:template match="text()" />
	<xsl:template match="summary"></xsl:template>

	<xsl:template match="docInfos"></xsl:template>

	<xsl:template match="hits">
		<xsl:if test="$include_header = 'true'">
			<xsl:value-of select="$query" /><xsl:text>&#x9;</xsl:text><xsl:value-of select="../summary/searchParam/patt" /><xsl:text>&#xa;</xsl:text>
			<xsl:value-of select="$filter" /><xsl:text>&#x9;</xsl:text><xsl:value-of select="../summary/searchParam/filter" /><xsl:text>&#xa;</xsl:text>
			<xsl:value-of select="$total_hits" /><xsl:text>&#x9;</xsl:text><xsl:value-of select="../summary/numberOfHits" /><xsl:text>&#xa;</xsl:text>
			<xsl:value-of select="$total_docs" /><xsl:text>&#x9;</xsl:text><xsl:value-of select="../summary/numberOfDocs" /><xsl:text>&#xa;</xsl:text>
			<xsl:value-of select="$total_exported" /><xsl:text>&#x9;</xsl:text><xsl:value-of select="$n" /><xsl:text>&#xa;</xsl:text>
			<xsl:value-of select="$layout" /><xsl:text>&#x9;</xsl:text><xsl:value-of select="$per_hit" /><xsl:text>&#xa;&#xa;</xsl:text>
			<xsl:value-of select="$context_left" /><xsl:text>&#x9;Hit&#x9;</xsl:text><xsl:value-of select="$context_right" /><xsl:text>&#x9;</xsl:text><xsl:value-of select="$lemma" /><xsl:text>&#x9;</xsl:text><xsl:value-of select="$pos" /><xsl:text>&#x9;</xsl:text><xsl:value-of select="$document_title" /><xsl:text>&#xa;</xsl:text>
		</xsl:if>
		<xsl:for-each select="hit">
			<xsl:variable name="docPid" select="docPid/text()" />
			<xsl:variable name="docInfo" select="/blacklabResponse/docInfos/docInfo[@pid=$docPid]" />
			<xsl:call-template name="left"/>
			<xsl:text>&#x9;</xsl:text>
			<xsl:value-of select="match" />
			<xsl:text>&#x9;</xsl:text>
			<xsl:call-template name="right"/>
			<xsl:text>&#x9;</xsl:text>
			<xsl:call-template name="lemma"/>
			<xsl:text>&#x9;</xsl:text>
			<xsl:call-template name="pos"/>
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
	
	<xsl:template name="lemma">
		<xsl:for-each select="match/w">
			<xsl:value-of select="@*[name()=$lemma]" /><xsl:text> </xsl:text>
		</xsl:for-each>
	</xsl:template>
	
	<xsl:template name="pos">
		<xsl:for-each select="match/w">
			<xsl:value-of select="@*[name()=$pos_name]" /><xsl:text> </xsl:text>
		</xsl:for-each>
	</xsl:template>

</xsl:stylesheet>