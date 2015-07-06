<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet version="1.0" xmlns="http://www.w3.org/1999/xhtml" xmlns:xlink="http://www.w3.org/1999/xlink" xmlns:imdi="http://www.mpi.nl/IMDI/Schema/IMDI" xmlns:xsl="http://www.w3.org/1999/XSL/Transform">
	<xsl:output method="html" encoding="UTF-8" omit-xml-declaration="yes" />
	
	<xsl:param name="title_name" select="''"/>
	<xsl:param name="author_name" select="''"/>
	<xsl:param name="description_name" select="''"/>
	<xsl:param name="document_id_name" select="''"/>
	<xsl:param name="texttype_name" select="''"/>
	<xsl:param name="collection_name" select="''"/>
	<xsl:param name="license_code_name" select="''"/>
	<xsl:param name="license_date_name" select="''"/>
	<xsl:param name="country_name" select="''"/>
	<xsl:param name="continent_name" select="''"/>
	<xsl:param name="language_name" select="''"/>
	
	<xsl:param name="doc_id" select="''"/>
	
	<xsl:param name="result_by" select="''"/>
	<xsl:param name="document_id" select="''"/>
	<xsl:param name="texttype" select="''"/>
	<xsl:param name="collection" select="''"/>
	<xsl:param name="license_code" select="''"/>
	<xsl:param name="license_date" select="''"/>
	<xsl:param name="country" select="''"/>
	<xsl:param name="continent" select="''"/>
	<xsl:param name="language" select="''"/>

	<xsl:template match="error">
		<h1>Error</h1>
		<xsl:value-of select="." />
	</xsl:template>
	
	<xsl:template match="docPid">
	</xsl:template>
	
	<xsl:template match="docInfo">
		<table>
		<tr><td colspan="2"><h2><xsl:value-of select="*[name()=$title_name]" /></h2></td></tr>
		<tr><td colspan="2"><h4><i><xsl:value-of select="$result_by" />:</i>&#160;&#160;<xsl:value-of select="*[name()=$author_name]" /></h4></td></tr>
		<tr><td colspan="2"><xsl:value-of select="*[name()=$description_name]" /></td></tr>
		<tr><td><b><xsl:value-of select="$document_id" /></b></td><td><xsl:value-of select="*[name()=$document_id_name]" /></td></tr>
		<tr><td><b><xsl:value-of select="$texttype" /></b></td><td><xsl:value-of select="*[name()=$texttype_name]" /></td></tr>
		<tr><td><b><xsl:value-of select="$collection" /></b></td><td><xsl:value-of select="*[name()=$collection_name]" /></td></tr>
		<tr><td><b><xsl:value-of select="$license_code" /></b></td><td><xsl:value-of select="*[name()=$license_code_name]" /></td></tr>
		<tr><td><b><xsl:value-of select="$license_date" /></b></td><td><xsl:value-of select="*[name()=$license_date_name]" /></td></tr>
		<tr><td><b><xsl:value-of select="$country" /></b></td><td><xsl:value-of select="*[name()=$country_name]" /></td></tr>
		<tr><td><b><xsl:value-of select="$continent" /></b></td><td><xsl:value-of select="*[name()=$continent_name]" /></td></tr>
		<tr><td><b><xsl:value-of select="$language" /></b></td><td><xsl:value-of select="*[name()=$language_name]" /></td></tr>
		</table>
	</xsl:template>
	
	<xsl:template match="docFields">
	</xsl:template>
	
</xsl:stylesheet>