<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform">
	<xsl:output method="html" omit-xml-declaration="yes" />
	
	<xsl:param name="query_result_url" select="''"/>
	
	<xsl:param name="sort_by" select="''"/>
	<xsl:param name="options" select="''" />
	<xsl:param name="query" select="''" />
	<xsl:param name="query_id" select="''" />
	<xsl:param name="per_hit" select="''" />
	<xsl:param name="per_doc" select="''" />
	<xsl:param name="grouped_per_hit" select="''" />
	<xsl:param name="grouped_per_doc" select="''" />
	<xsl:param name="group" select="''" />
	<xsl:param name="hits" select="''" />
	<xsl:param name="grouped_header" select="''" />
	<xsl:param name="result_export" select="''" />
	<xsl:param name="result_pagination_show" select="''" />
	<xsl:param name="result_per_page" select="''" />
	<xsl:param name="result_page" select="''" />
	<xsl:param name="result_go" select="''" />
	<xsl:param name="result_of" select="''" />
	<xsl:param name="detailed_conc" select="''" />
	<xsl:param name="load_more" select="''" />
	<xsl:param name="group_by_name" select="''" />
	<xsl:param name="group_by_name_clean" select="''" />
	
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
				$(document).find('#result_<xsl:value-of select="$query_id" /> .group_bySelect').append('<xsl:value-of select="$options" />');
				Whitelab.search.group_by = '<xsl:value-of select="$group_by_name" />';
				if (Whitelab.search.group_by.length > 0) {
					$(document).find('#result_<xsl:value-of select="$query_id" /> .group_bySelect').val(Whitelab.search.group_by);
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

	<xsl:template match="hitGroups">
		<xsl:variable name="first" select="../summary/windowFirstResult" />
		<xsl:variable name="number" select="../summary/requestedWindowSize" />
		<xsl:variable name="totalHits" select="../summary/numberOfGroups" />
		<xsl:variable name="numberOfPages" select="ceiling($totalHits div ../summary/requestedWindowSize)" />
		<div class="large-16 medium-16 small-16">
			<xsl:call-template name="tabs" />
			<div class="grouped-results result-pane tab-pane active lightbg haspadding">
				<input type="hidden" class="current-view" value="8" />
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
										<xsl:value-of select="$group" />
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
										<xsl:value-of select="$hits" />
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
							<xsl:for-each select="hitgroup">
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
												select="'Whitelab.search.result.hitProgress(this,'" /><xsl:value-of select="$apos" /><xsl:value-of select="$query" /><xsl:value-of select="$apos" />,<xsl:value-of select="$apos" /><xsl:value-of select="$group_by_name_clean" /><xsl:value-of select="$apos" />,<xsl:value-of select="$apos" /><xsl:value-of
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
											<input type="hidden" class="start" value="0" />
											<input type="hidden" class="count" value="20" />
											<div class="inline-concordance">
												<button class="btn btn-link">
													<xsl:attribute name="onclick"><xsl:value-of
														select="'Whitelab.search.result.searchHitGroupContent('"/><xsl:value-of select="$query_id" />
														<xsl:value-of select="','"/><xsl:value-of select="$apos" /><xsl:value-of select="$group_by_name_clean" /><xsl:value-of select="$apos" /><xsl:value-of select="','" /><xsl:value-of select="$apos" /><xsl:value-of
														select="identityDisplay" /><xsl:value-of select="$apos" /><xsl:value-of
														select="');'" /></xsl:attribute>
													<xsl:value-of select="$detailed_conc"/>
												</button>
												-
												<button class="btn btn-link nolink">
													<xsl:attribute name="onclick"><xsl:value-of
														select="'Whitelab.search.result.getHitGroupContent('" /><xsl:value-of select="$apos" /><xsl:value-of select="'#'" /><xsl:value-of
														select="$rowId" /><xsl:value-of select="$apos" />,<xsl:value-of select="$apos" /><xsl:value-of select="$query" /><xsl:value-of select="$apos" />,<xsl:value-of select="$apos" /><xsl:value-of select="$group_by_name_clean" /><xsl:value-of select="$apos" />,<xsl:value-of select="$apos" /><xsl:value-of
														select="identityDisplay" /><xsl:value-of select="$apos" />,<xsl:value-of select="$apos" /><xsl:value-of
														select="size" /><xsl:value-of select="$apos" />,<xsl:value-of select="$query_id"/><xsl:value-of
														select="');'" /></xsl:attribute>
													<xsl:value-of select="$load_more"/>
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
				
				$(document).find('#result_<xsl:value-of select="$query_id" /> .group_bySelect').append('<xsl:value-of select="$options" />');
				Whitelab.search.group_by = '<xsl:value-of select="$group_by_name" />';
				if (Whitelab.search.group_by.length > 0) {
					$(document).find('#result_<xsl:value-of select="$query_id" /> .group_bySelect').val(Whitelab.search.group_by);
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
					<xsl:attribute name="href">
						<xsl:value-of select="$query_result_url" />
						<xsl:text>&amp;view=1</xsl:text>
					</xsl:attribute>
					<xsl:value-of select="$per_hit" />
				</a>
			</li>
			<li>
				<a>
					<xsl:attribute name="href">
						<xsl:value-of select="$query_result_url" />
						<xsl:text>&amp;view=2</xsl:text>
					</xsl:attribute>
					<xsl:value-of select="$per_doc" />
				</a>
			</li>
			<li class="active disabled">
				<a>
					<xsl:value-of select="$grouped_per_hit" />
				</a>
			</li>
			<li>
				<a>
					<xsl:attribute name="href">
						<xsl:value-of select="$query_result_url" />
						<xsl:text>&amp;view=16</xsl:text>
					</xsl:attribute>
					<xsl:value-of select="$grouped_per_doc" />
				</a>
			</li>
		</ul>
	</xsl:template>
	
	<xsl:template name="dropdown">
		<div class="large-16 medium-16 small-16 row">
			<select class="group_bySelect">
				<xsl:attribute name="onchange">
					<xsl:text>Whitelab.search.result.selectGrouping(</xsl:text>
					<xsl:value-of select="$query_id" />
					<xsl:text>,$(this).val(),8)</xsl:text>
				</xsl:attribute>
				<option value="">
					<xsl:if test="'' = $group_by_name">
						<xsl:attribute name="selected"><xsl:value-of
							select="'true'" /></xsl:attribute>
					</xsl:if>
					<xsl:value-of select="$grouped_header"/><xsl:text>...</xsl:text>
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
						<xsl:text>,{number : $(this).val(), sort : '</xsl:text><xsl:value-of select="$sort_by" /><xsl:text>'})</xsl:text>
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
										<xsl:text>,{first:0, number:</xsl:text><xsl:value-of select="$resultsPerPage"/><xsl:text>, sort : '</xsl:text><xsl:value-of select="$sort_by" /><xsl:text>'})</xsl:text>
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
										<xsl:text>, number:</xsl:text><xsl:value-of select="$resultsPerPage"/><xsl:text>, sort : '</xsl:text><xsl:value-of select="$sort_by" /><xsl:text>'})</xsl:text>
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
										<xsl:text>, number:</xsl:text><xsl:value-of select="$resultsPerPage"/><xsl:text>, sort : '</xsl:text><xsl:value-of select="$sort_by" /><xsl:text>'})</xsl:text>
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
										<xsl:text>, number:</xsl:text><xsl:value-of select="$resultsPerPage"/><xsl:text>, sort : '</xsl:text><xsl:value-of select="$sort_by" /><xsl:text>'})</xsl:text>
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
						<xsl:text>,this,</xsl:text><xsl:value-of select="$resultsPerPage"/><xsl:text>,'</xsl:text><xsl:value-of select="$sort_by" /><xsl:text>')</xsl:text>
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
							<xsl:text>, number:</xsl:text><xsl:value-of select="$perpage"/><xsl:text>, sort : '</xsl:text><xsl:value-of select="$sort_by" /><xsl:text>'})</xsl:text>
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