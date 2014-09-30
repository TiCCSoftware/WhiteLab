<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform">
<xsl:output method="text" omit-xml-declaration="yes"  indent="no"/>
<xsl:param name="lang" select="'nl'" />
<xsl:param name="pos_name" select="'#'" />
<xsl:param name="lemma_name" select="'#'" />
<xsl:param name="title_name" select="'#'" />
<xsl:param name="collection_name" select="'#'" />
<xsl:template match="text()" />
<xsl:template match="summary"></xsl:template>
<xsl:template match="docs"><xsl:text>Query&#x9;</xsl:text><xsl:value-of select="../summary/searchParam/patt" /><xsl:text>&#xa;</xsl:text>
<xsl:text>Filter&#x9;</xsl:text><xsl:value-of select="../summary/searchParam/filter" /><xsl:text>&#xa;</xsl:text>
<xsl:choose>
<xsl:when test="$lang = 'en'">
<xsl:text>Total hits&#x9;</xsl:text><xsl:value-of select="../summary/numberOfHits" /><xsl:text>&#xa;</xsl:text>
<xsl:text>Total documents&#x9;</xsl:text><xsl:value-of select="../summary/numberOfDocs" /><xsl:text>&#xa;</xsl:text>
<xsl:text>Layout&#x9;Document list</xsl:text><xsl:text>&#xa;&#xa;</xsl:text>
<xsl:text>Document ID&#x9;Document title&#x9;Collection&#x9;Hits&#xa;</xsl:text>
</xsl:when>
<xsl:otherwise>
<xsl:text>Totaal hits&#x9;</xsl:text><xsl:value-of select="../summary/numberOfHits" /><xsl:text>&#xa;</xsl:text>
<xsl:text>Totaal documenten&#x9;</xsl:text><xsl:value-of select="../summary/numberOfDocs" /><xsl:text>&#xa;</xsl:text>
<xsl:text>Layout&#x9;Documentenlijst</xsl:text><xsl:text>&#xa;&#xa;</xsl:text>
<xsl:text>Document ID&#x9;Document titel&#x9;Collectie&#x9;Hits&#xa;</xsl:text>
</xsl:otherwise>
</xsl:choose>
<xsl:for-each select="doc">
<xsl:value-of select="docPid" /><xsl:text>&#x9;</xsl:text><xsl:value-of select="docInfo/*[name()=$title_name]" /><xsl:text>&#x9;</xsl:text><xsl:value-of select="docInfo/*[name()=$collection_name]" /><xsl:text>&#x9;</xsl:text><xsl:value-of select="numberOfHits" /><xsl:text>&#xa;</xsl:text>
</xsl:for-each>
</xsl:template>
</xsl:stylesheet>