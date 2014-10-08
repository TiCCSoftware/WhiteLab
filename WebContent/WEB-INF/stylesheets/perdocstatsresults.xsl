<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet version="1.0"
	xmlns:xsl="http://www.w3.org/1999/XSL/Transform">
	<xsl:output method="html" omit-xml-declaration="yes" />
	
	<xsl:param name="result_export" select="''" />
	<xsl:param name="result_pagination_show" select="''" />
	<xsl:param name="result_per_page" select="''" />
	<xsl:param name="result_page" select="''" />
	<xsl:param name="result_go" select="''" />
	<xsl:param name="result_of" select="''" />
	<xsl:param name="title_name" select="''" />
	<xsl:param name="author_name" select="''" />
	<xsl:param name="collection_name" select="''" />
	<xsl:param name="document_id" select="''" />
	<xsl:param name="document_title" select="''" />
	<xsl:param name="document_author" select="''" />
	<xsl:param name="collection" select="''" />
	<xsl:param name="token_count" select="''" />
	<xsl:param name="view_doc" select="''" />

	<xsl:template match="error">
		<h1>Error</h1>
		<xsl:value-of select="." />
	</xsl:template>

	<xsl:template match="summary">
	</xsl:template>

	<xsl:template match="docs">
		<xsl:variable name="first" select="../summary/windowFirstResult" />
		<xsl:variable name="number" select="../summary/requestedWindowSize" />
		<xsl:variable name="totalHits" select="numberOfDocs" />
		<xsl:variable name="numberOfPages" select="ceiling($totalHits div ../summary/requestedWindowSize)" />
		<div class="large-16 medium-16 small-16">
			<div id="perdoc" class="active lightbg haspadding">
				<input type="hidden" class="current-view" value="2" />
				<input type="hidden" class="current-first"><xsl:attribute name="value"><xsl:value-of select="$first"></xsl:value-of></xsl:attribute></input>
				<input type="hidden" class="current-number"><xsl:attribute name="value"><xsl:value-of select="$number"></xsl:value-of></xsl:attribute></input>
				<div class="gradient"></div>
				<xsl:call-template name="pagination" />
				<xsl:call-template name="export" />
				<table class="documents">
					<thead>
						<tr class="tbl_head">
							<th class="tbl_docid">
								<a>
									<xsl:attribute name="onclick">
										<xsl:text>Whitelab.explore.statistics.update('docs',{sort:''})</xsl:text>
									</xsl:attribute>
									<xsl:value-of select="$document_id" />
								</a>
							</th>
							<th class="tbl_doctitle">
								<a>
									<xsl:attribute name="onclick">
										<xsl:text>Whitelab.explore.statistics.update('docs',{sort:''})</xsl:text>
									</xsl:attribute>
									<xsl:value-of select="$document_title" />
								</a>
								<a>
									<xsl:attribute name="onclick">
										<xsl:text>Whitelab.explore.statistics.update('docs',{sort:'field:</xsl:text>
										<xsl:value-of select="$title_name" />
										<xsl:text>'})</xsl:text>
									</xsl:attribute>
									&#9650;
								</a>
								<a>
									<xsl:attribute name="onclick">
										<xsl:text>Whitelab.explore.statistics.update('docs',{sort:'-field:</xsl:text>
										<xsl:value-of select="$title_name" />
										<xsl:text>'})</xsl:text>
									</xsl:attribute>
									&#9660;
								</a>
							</th>
							<th class="tbl_author">
								<a>
									<xsl:attribute name="onclick">
										<xsl:text>Whitelab.explore.statistics.update('docs',{sort:''})</xsl:text>
									</xsl:attribute>
									<xsl:value-of select="$document_author" />
								</a>
								<a>
									<xsl:attribute name="onclick">
										<xsl:text>Whitelab.explore.statistics.update('docs',{sort:'field:</xsl:text>
										<xsl:value-of select="$author_name" />
										<xsl:text>'})</xsl:text>
									</xsl:attribute>
									&#9650;
								</a>
								<a>
									<xsl:attribute name="onclick">
										<xsl:text>Whitelab.explore.statistics.update('docs',{sort:'-field:</xsl:text>
										<xsl:value-of select="$author_name" />
										<xsl:text>'})</xsl:text>
									</xsl:attribute>
									&#9660;
								</a>
							</th>
							<th class="tbl_year centered">
								<a>
									<xsl:attribute name="onclick">
										<xsl:text>Whitelab.explore.statistics.update('docs',{sort:''})</xsl:text>
									</xsl:attribute>
									<xsl:value-of select="$collection" />
								</a>
								<a>
									<xsl:attribute name="onclick">
										<xsl:text>Whitelab.explore.statistics.update('docs',{sort:'field:</xsl:text>
										<xsl:value-of select="$collection_name" />
										<xsl:text>'})</xsl:text>
									</xsl:attribute>
									&#9650;
								</a>
								<a>
									<xsl:attribute name="onclick">
										<xsl:text>Whitelab.explore.statistics.update('docs',{sort:'-field:</xsl:text>
										<xsl:value-of select="$collection_name" />
										<xsl:text>'})</xsl:text>
									</xsl:attribute>
									&#9660;
								</a>
							</th>
							<th class="tbl_hits centered">
								<a>
									<xsl:attribute name="onclick">
										<xsl:text>Whitelab.explore.statistics.update('docs',{sort:''})</xsl:text>
									</xsl:attribute>
									<xsl:value-of select="$token_count" />
								</a>
								<a>
									<xsl:attribute name="onclick">
										<xsl:text>Whitelab.explore.statistics.update('docs',{sort:'-numhits'})</xsl:text>
									</xsl:attribute>
									&#9650;
								</a>
								<a>
									<xsl:attribute name="onclick">
										<xsl:text>Whitelab.explore.statistics.update('docs',{sort:'numhits'})</xsl:text>
									</xsl:attribute>
									&#9660;
								</a>
							</th>
							<th class="tbl_show centered"></th>
						</tr>
					</thead>
					<tbody>
						<xsl:for-each select="doc">
							<xsl:variable name="docCollection" select="docInfo/*[name()=$collection_name]" />
							<xsl:variable name="docTitle" select="docInfo/*[name()=$title_name]" />
							<xsl:variable name="docAuthor" select="docInfo/*[name()=$author_name]" />
							<xsl:variable name="hitsInDoc" select="numberOfHits" />
							<tr>
								<td><b><xsl:value-of select="docPid" /></b></td>
								<td><xsl:value-of select="$docTitle" /></td>
								<td><xsl:value-of select="$docAuthor" /></td>
								<td class="centered"><xsl:value-of select="$docCollection" /></td>
								<td class="centered"><xsl:value-of select="$hitsInDoc" /></td>
								<td class="centered">
									<a class="btn">
										<xsl:attribute name="onclick">
											<xsl:text>Whitelab.explore.showDocument('</xsl:text>
											<xsl:value-of select="docPid" />
											<xsl:text>')</xsl:text>
										</xsl:attribute>
										<xsl:value-of select="$view_doc" />
									</a>
								</td>
							</tr>
						</xsl:for-each>
					</tbody>
				</table>
				<xsl:call-template name="pagination" />
			</div>
		</div>
	</xsl:template>
	
	<xsl:template name="export">
		<div class="export large-16 medium-16 small-16 row">
			<button class="small">
				<xsl:attribute name="onclick"><xsl:text>Whitelab.explore.statistics.doExport('docs');</xsl:text></xsl:attribute>
				<xsl:value-of select="$result_export"/>
			</button>
		</div>
	</xsl:template>

	<xsl:template name="pagination">
		<xsl:variable name="resultsPerPage" select="//requestedWindowSize" />
		<xsl:variable name="totalHits" select="//numberOfDocsRetrieved" />
		<xsl:variable name="startResults" select="//windowFirstResult" />
		<xsl:variable name="currentPage"
			select="floor( $startResults div $resultsPerPage ) + 1" />
		<xsl:variable name="numberOfPages"
			select="ceiling($totalHits div $resultsPerPage)" />
		<xsl:variable name="startPage">
			<xsl:call-template name="max">
				<xsl:with-param name="num1" select="$currentPage - 5" />
				<xsl:with-param name="num2" select="1" />
			</xsl:call-template>
		</xsl:variable>
		<xsl:variable name="total">
			<xsl:call-template name="min">
				<xsl:with-param name="num1" select="$currentPage + 5" />
				<xsl:with-param name="num2" select="$numberOfPages" />
			</xsl:call-template>
		</xsl:variable>
		<div class="pagination large-16 medium-16 small-16 row">
			<div class="small-text jump-left large-3 medium-3 small-4 columns">
				<xsl:value-of select="$result_pagination_show" />
				<select class="show-select meta-small">
					<xsl:attribute name="onchange">
						<xsl:text>Whitelab.explore.statistics.update('docs',{number : $(this).val()})</xsl:text>
					</xsl:attribute>
					<xsl:choose>
						<xsl:when test="$resultsPerPage = 200">
							<option value="50">50</option>
							<option value="100">100</option>
							<option value="200" selected="true">200</option>
						</xsl:when>
						<xsl:otherwise>
							<xsl:choose>
								<xsl:when test="$resultsPerPage = 100">
									<option value="50">50</option>
									<option value="100" selected="true">100</option>
									<option value="200">200</option>
								</xsl:when>
								<xsl:otherwise>
									<option value="50" selected="true">50</option>
									<option value="100">100</option>
									<option value="200">200</option>
								</xsl:otherwise>
							</xsl:choose>
						</xsl:otherwise>
					</xsl:choose>
				</select>
				<xsl:value-of select="$result_per_page" />
			</div>
			<div class="large-10 medium-10 small-8 centered columns">
				<ul class="pagebuttons">
						<xsl:choose>
							<xsl:when test="$currentPage = 1">
								<li class="disabled">
									<a href="#"><xsl:value-of select="'&lt;&lt;'" /></a>
								</li>
							</xsl:when>
							<xsl:otherwise>
								<li>
									<a>
									<xsl:attribute name="onclick">
										<xsl:text>Whitelab.explore.statistics.update('docs',{first:0, number:</xsl:text><xsl:value-of select="$resultsPerPage"/><xsl:text>})</xsl:text>
									</xsl:attribute>
										<xsl:value-of select="'&lt;&lt;'" />
									</a>
								</li>
							</xsl:otherwise>
						</xsl:choose>
					<xsl:choose>
						<xsl:when test="$currentPage = 1">
							<li class="disabled">
								<a href="#"><xsl:value-of select="'&lt;'" /></a>
							</li>
						</xsl:when>
						<xsl:otherwise>
							<li>
								<a>
									<xsl:attribute name="onclick">
										<xsl:text>Whitelab.explore.statistics.update('docs',{first:</xsl:text>
										<xsl:value-of select="($currentPage - 2) * $resultsPerPage" />
										<xsl:text>, number:</xsl:text><xsl:value-of select="$resultsPerPage"/><xsl:text>})</xsl:text>
									</xsl:attribute>
									<xsl:value-of select="'&lt;'" />
								</a>
							</li>
						</xsl:otherwise>
					</xsl:choose>
					<xsl:if test="$startPage &gt; 1">
						<li class="disabled">
							<a href="#">...</a>
						</li>
					</xsl:if>
					<xsl:call-template name="makePagination">
						<xsl:with-param name="active" select="$currentPage" />
						<xsl:with-param name="total" select="$total" />
						<xsl:with-param name="start" select="$startPage" />
						<xsl:with-param name="perpage" select="$resultsPerPage" />
					</xsl:call-template>
					<xsl:if test="$total &lt; $numberOfPages">
						<li class="disabled">
							<a href="#">...</a>
						</li>
					</xsl:if>
					<xsl:choose>
						<xsl:when test="$currentPage = $numberOfPages">
							<li class="disabled">
								<a href="#"><xsl:value-of select="'&gt;'" /></a>
							</li>
						</xsl:when>
						<xsl:otherwise>
							<li>
								<a>
									<xsl:attribute name="onclick">
										<xsl:text>Whitelab.explore.statistics.update('docs',{first:</xsl:text>
										<xsl:value-of select="($currentPage * $resultsPerPage)" />
										<xsl:text>, number:</xsl:text><xsl:value-of select="$resultsPerPage"/><xsl:text>})</xsl:text>
									</xsl:attribute>
									<xsl:value-of select="'&gt;'" />
								</a>
							</li>
						</xsl:otherwise>
					</xsl:choose>
					<xsl:choose>
						<xsl:when test="$currentPage = $numberOfPages">
							<li class="disabled">
								<a href="#"><xsl:value-of select="'&gt;&gt;'" /></a>
							</li>
						</xsl:when>
						<xsl:otherwise>
							<li>
								<a>
									<xsl:attribute name="onclick">
										<xsl:text>Whitelab.explore.statistics.update('docs',{first:</xsl:text>
										<xsl:value-of select="(($numberOfPages - 1) * $resultsPerPage)" />
										<xsl:text>, number:</xsl:text><xsl:value-of select="$resultsPerPage"/><xsl:text>})</xsl:text>
									</xsl:attribute>
									<xsl:value-of select="'&gt;&gt;'" />
								</a>
							</li>
						</xsl:otherwise>
					</xsl:choose>
				</ul>
			</div>
			<div class="small-text large-3 medium-3 small-4 columns">
				<xsl:value-of select="$result_page" />
				<input type="hidden" class="max-results"><xsl:attribute name="value"><xsl:value-of select="$resultsPerPage" /></xsl:attribute></input>
				<input class="page-select meta-small" type="number" min="1">
					<xsl:attribute name="max"><xsl:value-of select="$numberOfPages" /></xsl:attribute>
					<xsl:attribute name="value"><xsl:value-of select="$currentPage" /></xsl:attribute>
				</input>
				&#160;<xsl:value-of select="$result_of" />
				<xsl:value-of select="$numberOfPages" /><xsl:text> </xsl:text>
				<button class="small go">
					<xsl:attribute name="onclick">
						<xsl:text>Whitelab.search.result.goToPage('docs',this,</xsl:text><xsl:value-of select="$resultsPerPage"/><xsl:text>)</xsl:text>
					</xsl:attribute>
				<xsl:value-of select="$result_go" />
				</button>
			</div>
		</div>
	</xsl:template>
	
	<xsl:template name="makePagination">
		<xsl:param name="active" />
		<xsl:param name="total" />
		<xsl:param name="start" />
		<xsl:param name="perpage" />

		<xsl:choose>
			<xsl:when test="$start = $active">
				<li class="active">
					<a href="#">
						<xsl:value-of select="$start" />
					</a>
				</li>
			</xsl:when>
			<xsl:otherwise>
				<li>
					<a>
						<xsl:attribute name="onclick">
							<xsl:text>Whitelab.explore.statistics.update('docs',{first:</xsl:text>
							<xsl:value-of select="($start - 1) * $perpage" />
							<xsl:text>, number:</xsl:text><xsl:value-of select="$perpage"/><xsl:text>})</xsl:text>
						</xsl:attribute>
						<xsl:value-of select="$start" />
					</a>
				</li>
			</xsl:otherwise>
		</xsl:choose>

		<xsl:if test="$start &lt; $total">
			<xsl:call-template name="makePagination">
				<xsl:with-param name="active" select="$active" />
				<xsl:with-param name="total" select="$total" />
				<xsl:with-param name="perpage" select="$perpage" />
				<xsl:with-param name="start" select="($start + 1)" />
			</xsl:call-template>
		</xsl:if>
	</xsl:template>

	<xsl:template name="max">
		<xsl:param name="num1" />
		<xsl:param name="num2" />
		<xsl:choose>
			<xsl:when test="$num1 &gt; $num2">
				<xsl:value-of select="$num1" />
			</xsl:when>
			<xsl:otherwise>
				<xsl:value-of select="$num2" />
			</xsl:otherwise>
		</xsl:choose>
	</xsl:template>

	<xsl:template name="min">
		<xsl:param name="num1" />
		<xsl:param name="num2" />
		<xsl:choose>
			<xsl:when test="$num1 &lt; $num2">
				<xsl:value-of select="$num1" />
			</xsl:when>
			<xsl:otherwise>
				<xsl:value-of select="$num2" />
			</xsl:otherwise>
		</xsl:choose>
	</xsl:template>

	<xsl:template name="numberOrWaitingIndicator">
		<xsl:param name="number" />

		<xsl:choose>
			<xsl:when test="$number &lt; 0">
				<i class="icon-spinner icon-spin"></i>
			</xsl:when>
			<xsl:otherwise>
				<xsl:value-of select="$number" />
			</xsl:otherwise>
		</xsl:choose>
	</xsl:template>

</xsl:stylesheet>