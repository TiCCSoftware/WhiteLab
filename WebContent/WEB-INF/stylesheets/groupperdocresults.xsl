<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform">
	<xsl:output method="html" omit-xml-declaration="yes" />
	
	<xsl:param name="sortBy" select="''"/>
	<xsl:param name="options" select="''" />
	<xsl:param name="query" select="'#'" />
	<xsl:param name="by" select="'#'" />
	
	<xsl:param name="query_id" select="'#'" />
	<xsl:param name="per_hit" select="'#'" />
	<xsl:param name="per_doc" select="'#'" />
	<xsl:param name="grouped_per_hit" select="'#'" />
	<xsl:param name="grouped_per_doc" select="'#'" />
	<xsl:param name="context_left" select="'#'" />
	<xsl:param name="context_right" select="'#'" />
	<xsl:param name="word" select="'#'" />
	<xsl:param name="lemma" select="'#'" />
	<xsl:param name="pos" select="'#'" />
	<xsl:param name="result_export" select="'#'" />
	<xsl:param name="result_pagination_show" select="'#'" />
	<xsl:param name="result_per_page" select="'#'" />
	<xsl:param name="result_page" select="'#'" />
	<xsl:param name="result_go" select="'#'" />
	<xsl:param name="result_of" select="'#'" />
	
	<xsl:param name="resulttab_url" select="'#'" />
	<xsl:param name="resultgroup_url" select="'#'" />
	<xsl:param name="result_pagination_url" select="'#'" />
	<xsl:param name="resultsort_url" select="'#'" />
	<xsl:param name="concordance_url" select="'#'" />
	<xsl:param name="max_url" select="'#'" />
	
	<xsl:param name="lang" select="'nl'" />
	<xsl:param name="max" select="'50'" />
	<xsl:param name="queryview" select="'16'" />

	<xsl:param name="baseurl" select="'#'" />
	<xsl:param name="resultkey" select="'#'" />
	<xsl:param name="sessionid" select="'#'" />

	<xsl:param name="author_name" select="'#'" />
	<xsl:param name="collection_name" select="CollectionName" />
	<xsl:param name="keywords" select="TextKeywords" />
	<xsl:param name="pseudonym" select="Pseudonym" />
	<xsl:param name="sex" select="Sex" />
	<xsl:param name="age" select="Age" />
	<xsl:param name="translated" select="Translated" />
	<xsl:param name="translator" select="Translator" />
	<xsl:param name="license" select="LicenseCode" />
	<xsl:param name="source" select="SourceName" />
	<xsl:param name="country" select="Country" />
	<xsl:param name="published" select="Published" />
	<xsl:param name="publisher" select="Publisher" />
	<xsl:param name="date_name" select="'#'" />
	<xsl:param name="source_name" select="'#'" />
	<xsl:param name="groupBy_name" select="''" />
	<xsl:param name="groupBy_name_clean" select="''" />
	
	<xsl:template match="empty">
		<div class="large-16 medium-16 small-16">
			<xsl:call-template name="tabs" />
			<div class="grouped-results result-pane tab-pane active lightbg haspadding">
				<div class="gradient"></div>
				<xsl:call-template name="dropdown" />
			</div>
		</div>
		<script>
			$(document).ready(function() {
				
				$(document).find('#result_<xsl:value-of select="$query_id" /> .groupBySelect').append('<xsl:value-of select="$options" />');
				Whitelab.search.groupBy = '<xsl:value-of select="$groupBy_name" />';
				if (Whitelab.search.groupBy.length > 0) {
					$(document).find('#result_<xsl:value-of select="$query_id" /> .groupBySelect').val(Whitelab.search.groupBy);
				}
			});
		</script>
	</xsl:template>

	<xsl:template match="error">
		<h1>Error</h1>
		<xsl:value-of select="." />
	</xsl:template>

	<xsl:template match="summary">
	</xsl:template>

	<xsl:template match="docGroups">
		<xsl:variable name="first" select="../summary/windowFirstResult" />
		<xsl:variable name="number" select="../summary/requestedWindowSize" />
		<xsl:variable name="totalHits" select="../summary/numberOfGroups" />
		<xsl:variable name="numberOfPages" select="ceiling($totalHits div ../summary/requestedWindowSize)" />
		<div class="large-16 medium-16 small-16">
			<xsl:call-template name="tabs" />
			<div class="grouped-results result-pane tab-pane active lightbg haspadding">
				<input type="hidden" class="current-view" value="16" />
				<input type="hidden" class="current-first"><xsl:attribute name="value"><xsl:value-of select="$first"></xsl:value-of></xsl:attribute></input>
				<input type="hidden" class="current-number"><xsl:attribute name="value"><xsl:value-of select="$number"></xsl:value-of></xsl:attribute></input>
				<div class="gradient"></div>
				<xsl:call-template name="dropdown" />
				<xsl:call-template name="pagination" />
				<xsl:call-template name="export" />
				<div class="large-16 medium-16 small-16 row">
					<table>
						<thead>
							<tr>
								<th class="tbl_groupname">
									<a>
										<xsl:attribute name="onclick">
											<xsl:text>Whitelab.search.update(</xsl:text>
											<xsl:value-of select="$query_id" />
											<xsl:text>,{sort:''})</xsl:text>
										</xsl:attribute>
										<xsl:choose>
											<xsl:when test="$lang = 'en'">
												Group
											</xsl:when>
											<xsl:otherwise>
												Groep
											</xsl:otherwise>
										</xsl:choose>
									</a>
									<a>
										<xsl:attribute name="onclick">
											<xsl:text>Whitelab.search.update(</xsl:text>
											<xsl:value-of select="$query_id" />
											<xsl:text>,{sort:'identity'})</xsl:text>
										</xsl:attribute>
										&#9650;
									</a>
									<a>
										<xsl:attribute name="onclick">
											<xsl:text>Whitelab.search.update(</xsl:text>
											<xsl:value-of select="$query_id" />
											<xsl:text>,{sort:'-identity'})</xsl:text>
										</xsl:attribute>
										&#9660;
									</a>
								</th>
								<th>
									<a>
										<xsl:attribute name="onclick">
											<xsl:text>Whitelab.search.update(</xsl:text>
											<xsl:value-of select="$query_id" />
											<xsl:text>,{sort:''})</xsl:text>
										</xsl:attribute>
										<xsl:choose>
											<xsl:when test="$lang = 'en'">
												Documents
											</xsl:when>
											<xsl:otherwise>
												Documenten
											</xsl:otherwise>
										</xsl:choose>
									</a>
									<a>
										<xsl:attribute name="onclick">
											<xsl:text>Whitelab.search.update(</xsl:text>
											<xsl:value-of select="$query_id" />
											<xsl:text>,{sort:'-size'})</xsl:text>
										</xsl:attribute>
										&#9650;
									</a>
									<a>
										<xsl:attribute name="onclick">
											<xsl:text>Whitelab.search.update(</xsl:text>
											<xsl:value-of select="$query_id" />
											<xsl:text>,{sort:'size'})</xsl:text>
										</xsl:attribute>
										&#9660;
									</a>
								</th>
							</tr>
						</thead>
						<tbody>
							<xsl:for-each select="docgroup">
								<xsl:variable name="width" select="size * 100 div /blacklabResponse/summary/largestGroupSize" />
								<xsl:variable name="rowId" select="generate-id()" />
								<xsl:variable name="gr" select="identityDisplay" />
								<xsl:variable name="apos">'</xsl:variable>
								<tr>
									<td>
										<xsl:value-of select="$gr" />
									</td>
									<td>
										<div class="progress" data-toggle="collapse">
											<xsl:attribute name="data-target"><xsl:value-of
												select="'.'" /><xsl:value-of select="$rowId" /></xsl:attribute>
											<xsl:attribute name="onclick"><xsl:value-of
												select="'Whitelab.search.result.docProgress(this,'" /><xsl:value-of select="$apos" /><xsl:value-of select="$query" /><xsl:value-of select="$apos" />,<xsl:value-of select="$apos" /><xsl:value-of select="$groupBy_name_clean" /><xsl:value-of select="$apos" />,<xsl:value-of select="$apos" /><xsl:value-of
												select="identityDisplay" /><xsl:value-of select="$apos" />,<xsl:value-of select="$apos" /><xsl:value-of
												select="size" /><xsl:value-of select="$apos" />,<xsl:value-of select="$query_id"/><xsl:value-of
												select="');'" />
											</xsl:attribute>
											<div class="meter">
												<xsl:attribute name="style"><xsl:value-of
													select="'width: '" /><xsl:value-of select="$width" /><xsl:value-of
													select="'%;'" /></xsl:attribute>
												<xsl:value-of select="size" />
											</div>
										</div>
										<div>
											<xsl:attribute name="class"><xsl:value-of
												select="$rowId" /><xsl:value-of select="' collapse groupcontent hide first'"></xsl:value-of></xsl:attribute>
											<xsl:attribute name="id"><xsl:value-of
												select="$rowId" /></xsl:attribute>
											<xsl:attribute name="data-group"><xsl:value-of
												select="identityDisplay" /></xsl:attribute>
											<div class="inline-concordance">
												<button class="btn btn-link">
													<xsl:attribute name="onclick"><xsl:value-of
														select="'Whitelab.search.result.searchDocGroupContent('"/><xsl:value-of select="$query_id" />
														<xsl:value-of select="','"/><xsl:value-of select="$apos" /><xsl:value-of select="$groupBy_name_clean" /><xsl:value-of select="$apos" /><xsl:value-of select="','" /><xsl:value-of select="$apos" /><xsl:value-of
														select="identityDisplay" /><xsl:value-of select="$apos" /><xsl:value-of
														select="');'" /></xsl:attribute>
													<xsl:choose>
														<xsl:when test="$lang = 'en'">
															&#171; View detailed documents in this group
														</xsl:when>
														<xsl:otherwise>
															&#171; Toon gedetailleerde documenten in deze groep
														</xsl:otherwise>
													</xsl:choose>
												</button>
												-
												<button class="btn btn-link nolink">
													<xsl:attribute name="onclick"><xsl:value-of
														select="'Whitelab.search.result.getDocGroupContent('" /><xsl:value-of select="$apos" /><xsl:value-of select="'#'" /><xsl:value-of
														select="$rowId" /><xsl:value-of select="$apos" />,<xsl:value-of select="$apos" /><xsl:value-of select="$query" /><xsl:value-of select="$apos" />,<xsl:value-of select="$apos" /><xsl:value-of select="$groupBy_name_clean" /><xsl:value-of select="$apos" />,<xsl:value-of select="$apos" /><xsl:value-of
														select="identityDisplay" /><xsl:value-of select="$apos" />,<xsl:value-of select="$apos" /><xsl:value-of
														select="size" /><xsl:value-of select="$apos" />,<xsl:value-of select="$query_id"/><xsl:value-of
														select="');'" /></xsl:attribute>
													<xsl:choose>
														<xsl:when test="$lang = 'en'">
															Load more documents...
														</xsl:when>
														<xsl:otherwise>
															Laad meer documenten...
														</xsl:otherwise>
													</xsl:choose>
												</button>
												<div class="loading"><img src="../web/img/spinner.gif" /></div>
											</div>
										</div>
									</td>
								</tr>
							</xsl:for-each>
						</tbody>
					</table>
				</div>
				<xsl:call-template name="pagination" />
			</div>
		</div>
		<script>
			$(document).ready(function() {
				
				$(document).find('#result_<xsl:value-of select="$query_id" /> .groupBySelect').append('<xsl:value-of select="$options" />');
				Whitelab.search.groupBy = '<xsl:value-of select="$groupBy_name" />';
				if (Whitelab.search.groupBy.length > 0) {
					$(document).find('#result_<xsl:value-of select="$query_id" /> .groupBySelect').val(Whitelab.search.groupBy);
				}
			
				$('.nolink').click(function(event) {
					event.preventDefault();
				});
			});

		</script>
	</xsl:template>
	
	<xsl:template name="export">
		<div class="export large-16 medium-16 small-16 row">
			<button class="small">
				<xsl:attribute name="onclick"><xsl:text>Whitelab.search.doExport(</xsl:text><xsl:value-of select="$query_id"/><xsl:text>);</xsl:text></xsl:attribute>
				<xsl:value-of select="$result_export"/>
			</button>
		</div>
	</xsl:template>
	
	<xsl:template name="tabs">
		<ul class="nav nav-tabs" id="contentTabs">
			<li>
				<a>
					<xsl:attribute name="onclick">
						<xsl:text>Whitelab.search.update(</xsl:text>
						<xsl:value-of select="$query_id" />
						<xsl:text>,{view : 1})</xsl:text>
					</xsl:attribute>
					<xsl:value-of select="$per_hit" />
				</a>
			</li>
			<li>
				<a>
					<xsl:attribute name="onclick">
						<xsl:text>Whitelab.search.update(</xsl:text>
						<xsl:value-of select="$query_id" />
						<xsl:text>,{view : 2})</xsl:text>
					</xsl:attribute>
					<xsl:value-of select="$per_doc" />
				</a>
			</li>
			<li>
				<a>
					<xsl:attribute name="onclick">
						<xsl:text>Whitelab.search.update(</xsl:text>
						<xsl:value-of select="$query_id" />
						<xsl:text>,{groupBy: "", view : 8})</xsl:text>
					</xsl:attribute>
					<xsl:value-of select="$grouped_per_hit" />
				</a>
			</li>
			<li class="active disabled">
				<a>
					<xsl:value-of select="$grouped_per_doc" />
				</a>
			</li>
		</ul>
	</xsl:template>
	
	<xsl:template name="dropdown">
		<div class="large-16 medium-16 small-16 row">
			<select class="groupBySelect">
				<xsl:attribute name="onchange">
					<xsl:text>Whitelab.search.result.selectGrouping(</xsl:text>
					<xsl:value-of select="$query_id" />
					<xsl:text>,$(this).val(),16)</xsl:text>
				</xsl:attribute>
				<option value="">
					<xsl:if test="'' = $groupBy_name">
						<xsl:attribute name="selected"><xsl:value-of
							select="'true'" /></xsl:attribute>
					</xsl:if>
					<xsl:choose>
						<xsl:when test="$lang = 'en'">
							Group by...
						</xsl:when>
						<xsl:otherwise>
							Groepeer per...
						</xsl:otherwise>
					</xsl:choose>
				</option>
			</select>
		</div>
	</xsl:template>


	<xsl:template name="pagination">
		<xsl:variable name="resultsPerPage" select="//requestedWindowSize" />
		<xsl:variable name="totalHits" select="//numberOfGroups" />
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
						<xsl:text>Whitelab.search.update(</xsl:text>
						<xsl:value-of select="$query_id" />
						<xsl:text>,{number : $(this).val(), sort : '</xsl:text><xsl:value-of select="$sortBy" /><xsl:text>'})</xsl:text>
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
										<xsl:text>Whitelab.search.update(</xsl:text>
										<xsl:value-of select="$query_id" />
										<xsl:text>,{first:0, number:</xsl:text><xsl:value-of select="$resultsPerPage"/><xsl:text>, sort : '</xsl:text><xsl:value-of select="$sortBy" /><xsl:text>'})</xsl:text>
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
										<xsl:text>Whitelab.search.update(</xsl:text>
										<xsl:value-of select="$query_id" />
										<xsl:text>,{first:</xsl:text>
										<xsl:value-of select="($currentPage - 2) * $resultsPerPage" />
										<xsl:text>, number:</xsl:text><xsl:value-of select="$resultsPerPage"/><xsl:text>, sort : '</xsl:text><xsl:value-of select="$sortBy" /><xsl:text>'})</xsl:text>
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
										<xsl:text>Whitelab.search.update(</xsl:text>
										<xsl:value-of select="$query_id" />
										<xsl:text>,{first:</xsl:text>
										<xsl:value-of select="($currentPage * $resultsPerPage)" />
										<xsl:text>, number:</xsl:text><xsl:value-of select="$resultsPerPage"/><xsl:text>, sort : '</xsl:text><xsl:value-of select="$sortBy" /><xsl:text>'})</xsl:text>
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
										<xsl:text>Whitelab.search.update(</xsl:text>
										<xsl:value-of select="$query_id" />
										<xsl:text>,{first:</xsl:text>
										<xsl:value-of select="(($numberOfPages - 1) * $resultsPerPage)" />
										<xsl:text>, number:</xsl:text><xsl:value-of select="$resultsPerPage"/><xsl:text>, sort : '</xsl:text><xsl:value-of select="$sortBy" /><xsl:text>'})</xsl:text>
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
						<xsl:text>Whitelab.search.result.goToPage(</xsl:text>
						<xsl:value-of select="$query_id" />
						<xsl:text>,this,</xsl:text><xsl:value-of select="$resultsPerPage"/><xsl:text>,'</xsl:text><xsl:value-of select="$sortBy" /><xsl:text>')</xsl:text>
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
							<xsl:text>Whitelab.search.update(</xsl:text>
							<xsl:value-of select="$query_id" />
							<xsl:text>,{first:</xsl:text>
							<xsl:value-of select="($start - 1) * $perpage" />
							<xsl:text>, number:</xsl:text><xsl:value-of select="$perpage"/><xsl:text>, sort : '</xsl:text><xsl:value-of select="$sortBy" /><xsl:text>'})</xsl:text>
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