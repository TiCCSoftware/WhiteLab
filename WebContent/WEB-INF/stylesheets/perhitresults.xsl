<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform">
	<xsl:output method="html" omit-xml-declaration="yes" />
	
	<xsl:param name="query_result_url" select="''"/>
	<xsl:param name="query_document_url" select="''"/>
	<xsl:param name="query_export_url" select="''"/>
	
	<xsl:param name="sort_by" select="''"/>
	<xsl:param name="query" select="''" />
	<xsl:param name="query_id" select="''" />
	<xsl:param name="per_hit" select="''" />
	<xsl:param name="per_doc" select="''" />
	<xsl:param name="grouped_per_hit" select="''" />
	<xsl:param name="grouped_per_doc" select="''" />
	<xsl:param name="context_left" select="''" />
	<xsl:param name="context_right" select="''" />
	<xsl:param name="word" select="''" />
	<xsl:param name="lemma" select="''" />
	<xsl:param name="pos" select="''" />
	<xsl:param name="result_export" select="''" />
	<xsl:param name="result_pagination_show" select="''" />
	<xsl:param name="result_per_page" select="''" />
	<xsl:param name="result_titles" select="''" />
	<xsl:param name="result_page" select="''" />
	<xsl:param name="result_go" select="''" />
	<xsl:param name="result_of" select="''" />
	<xsl:param name="result_by" select="''" />
	<xsl:param name="title_name" select="''" />
	<xsl:param name="author_name" select="''" />
	<xsl:param name="date_name" select="''" />
	<xsl:param name="pos_name" select="''" />

	<xsl:template match="error">
		<h1>Error</h1>
		<xsl:value-of select="." />
	</xsl:template>
	
    <xsl:template match="docInfos" />

	<xsl:template match="summary">
	</xsl:template>
	
	<xsl:template match="hits">
		<xsl:variable name="first" select="../summary/windowFirstResult" />
		<xsl:variable name="number" select="../summary/requestedWindowSize" />
		<xsl:variable name="totalHits" select="../summary/numberOfHits" />
		<xsl:variable name="numberOfPages" select="ceiling($totalHits div ../summary/requestedWindowSize)" />
		<div class="large-16 medium-16 small-16">
			<xsl:call-template name="tabs"/>
			<div id="perhit" class="result-pane tab-pane active lightbg haspadding">
				<input type="hidden" class="current-view" value="1" />
				<input type="hidden" class="current-first"><xsl:attribute name="value"><xsl:value-of select="$first"></xsl:value-of></xsl:attribute></input>
				<input type="hidden" class="current-number"><xsl:attribute name="value"><xsl:value-of select="$number"></xsl:value-of></xsl:attribute></input>
				<div class="gradient"></div>
	            <xsl:call-template name="pagination"/>
				<xsl:call-template name="export" />
				<table>
					<thead>
						<tr class="tbl_head">
							<th class="tbl_conc_left">
								<div class="dropdown pull-right">
									<a class="dropdown-toggle" data-toggle="dropdown" href="#" id="left">
										<xsl:value-of select="$context_left"></xsl:value-of> <b class="caret"></b>
									</a>
									<ul class="dropdown-menu" role="menu" aria-labelledby="left">
										<li>
											<a>
												<xsl:attribute name="href">
													<xsl:value-of select="$query_result_url" />
													<xsl:text>&amp;view=1&amp;sort=wordleft:word</xsl:text>
												</xsl:attribute>
												&#9650;
											</a>
											<a>
												<xsl:attribute name="href">
													<xsl:value-of select="$query_result_url" />
													<xsl:text>&amp;view=1&amp;sort=-wordleft:word</xsl:text>
												</xsl:attribute>
												&#9660;
											</a>
											<a>
												<xsl:attribute name="href">
													<xsl:value-of select="$query_result_url" />
													<xsl:text>&amp;view=1&amp;sort=</xsl:text>
												</xsl:attribute>
												<xsl:value-of select="$word" />
											</a>
										</li>
										<li>
											<a>
												<xsl:attribute name="href">
													<xsl:value-of select="$query_result_url" />
													<xsl:text>&amp;view=1&amp;sort=wordleft:lemma</xsl:text>
												</xsl:attribute>
												&#9650;
											</a>
											<a>
												<xsl:attribute name="href">
													<xsl:value-of select="$query_result_url" />
													<xsl:text>&amp;view=1&amp;sort=-wordleft:lemma</xsl:text>
												</xsl:attribute>
												&#9660;
											</a>
											<a>
												<xsl:attribute name="href">
													<xsl:value-of select="$query_result_url" />
													<xsl:text>&amp;view=1&amp;sort=</xsl:text>
												</xsl:attribute>
												<xsl:value-of select="$lemma" />
											</a>
										</li>
										<li>
											<a>
												<xsl:attribute name="href">
													<xsl:value-of select="$query_result_url" />
													<xsl:text>&amp;view=1&amp;sort=wordleft:pos</xsl:text>
												</xsl:attribute>
												&#9650;
											</a>
											<a>
												<xsl:attribute name="href">
													<xsl:value-of select="$query_result_url" />
													<xsl:text>&amp;view=1&amp;sort=-wordleft:pos</xsl:text>
												</xsl:attribute>
												&#9660;
											</a>
											<a>
												<xsl:attribute name="href">
													<xsl:value-of select="$query_result_url" />
													<xsl:text>&amp;view=1&amp;sort=</xsl:text>
												</xsl:attribute>
												<xsl:value-of select="$pos" />
											</a>
										</li>
									</ul>
								</div>
							</th>
							<th class="tbl_conc_hit">
								<a>
									<xsl:attribute name="href">
										<xsl:value-of select="$query_result_url" />
										<xsl:text>&amp;view=1&amp;sort=</xsl:text>
									</xsl:attribute>
									Hit text
								</a>
								<a>
									<xsl:attribute name="href">
										<xsl:value-of select="$query_result_url" />
										<xsl:text>&amp;view=1&amp;sort=hit:word</xsl:text>
									</xsl:attribute>
									&#9650;
								</a>
								<a>
									<xsl:attribute name="href">
										<xsl:value-of select="$query_result_url" />
										<xsl:text>&amp;view=1&amp;sort=-hit:word</xsl:text>
									</xsl:attribute>
									&#9660;
								</a>
							</th>
							<th class="tbl_conc_right">
								<div class="dropdown">
									<a class="dropdown-toggle" data-toggle="dropdown" href="#" id="right">
										<xsl:value-of select="$context_right"></xsl:value-of> <b class="caret"></b>
									</a>
									<ul class="dropdown-menu" role="menu" aria-labelledby="right">
										<li>
											<a>
												<xsl:attribute name="href">
													<xsl:value-of select="$query_result_url" />
													<xsl:text>&amp;view=1&amp;sort=wordright:word</xsl:text>
												</xsl:attribute>
												&#9650;
											</a>
											<a>
												<xsl:attribute name="href">
													<xsl:value-of select="$query_result_url" />
													<xsl:text>&amp;view=1&amp;sort=-wordright:word</xsl:text>
												</xsl:attribute>
												&#9660;
											</a>
											<a>
												<xsl:attribute name="href">
													<xsl:value-of select="$query_result_url" />
													<xsl:text>&amp;view=1&amp;sort=</xsl:text>
												</xsl:attribute>
												<xsl:value-of select="$word" />
											</a>
										</li>
										<li class="disabled">
											<a>
												<xsl:attribute name="href">
													<xsl:value-of select="$query_result_url" />
													<xsl:text>&amp;view=1&amp;sort=wordright:lemma</xsl:text>
												</xsl:attribute>
												&#9650;
											</a>
											<a>
												<xsl:attribute name="href">
													<xsl:value-of select="$query_result_url" />
													<xsl:text>&amp;view=1&amp;sort=-wordright:lemma</xsl:text>
												</xsl:attribute>
												&#9660;
											</a>
											<a>
												<xsl:attribute name="href">
													<xsl:value-of select="$query_result_url" />
													<xsl:text>&amp;view=1&amp;sort=</xsl:text>
												</xsl:attribute>
												<xsl:value-of select="$lemma" />
											</a>
										</li>
										<li class="disabled">
											<a>
												<xsl:attribute name="href">
													<xsl:value-of select="$query_result_url" />
													<xsl:text>&amp;view=1&amp;sort=wordright:pos</xsl:text>
												</xsl:attribute>
												&#9650;
											</a>
											<a>
												<xsl:attribute name="href">
													<xsl:value-of select="$query_result_url" />
													<xsl:text>&amp;view=1&amp;sort=-wordright:pos</xsl:text>
												</xsl:attribute>
												&#9660;
											</a>
											<a>
												<xsl:attribute name="href">
													<xsl:value-of select="$query_result_url" />
													<xsl:text>&amp;view=1&amp;sort=</xsl:text>
												</xsl:attribute>
												<xsl:value-of select="$pos" />
											</a>
										</li>
									</ul>
								</div>							
							</th>
							<th class="tbl_lemma">
								<a>
									<xsl:attribute name="href">
										<xsl:value-of select="$query_result_url" />
										<xsl:text>&amp;view=1&amp;sort=</xsl:text>
									</xsl:attribute>
									<xsl:value-of select="$lemma" />
								</a>
								<a>
									<xsl:attribute name="href">
										<xsl:value-of select="$query_result_url" />
										<xsl:text>&amp;view=1&amp;sort=hit:lemma</xsl:text>
									</xsl:attribute>
									&#9650;
								</a>
								<a>
									<xsl:attribute name="href">
										<xsl:value-of select="$query_result_url" />
										<xsl:text>&amp;view=1&amp;sort=-hit:lemma</xsl:text>
									</xsl:attribute>
									&#9660;
								</a>
							</th>
							<th class="tbl_pos">
								<a>
									<xsl:attribute name="href">
										<xsl:value-of select="$query_result_url" />
										<xsl:text>&amp;view=1&amp;sort=</xsl:text>
									</xsl:attribute>
									<xsl:value-of select="$pos" />
								</a>
								<a>
									<xsl:attribute name="href">
										<xsl:value-of select="$query_result_url" />
										<xsl:text>&amp;view=1&amp;sort=hit:pos</xsl:text>
									</xsl:attribute>
									&#9650;
								</a>
								<a>
									<xsl:attribute name="href">
										<xsl:value-of select="$query_result_url" />
										<xsl:text>&amp;view=1&amp;sort=-hit:pos</xsl:text>
									</xsl:attribute>
									&#9660;
								</a>
							</th>
						</tr>
					</thead>
					<tbody>
									
					<xsl:for-each select="hit">
					
						<xsl:variable name="current_doc" select="docPid" />
						<xsl:variable name="currentId" select="generate-id()" />
						<xsl:variable name="apos">'</xsl:variable>
						<xsl:variable name="previous_doc" select="preceding-sibling::hit[1]/docPid" />
						
						<xsl:if test="$current_doc != $previous_doc or not($previous_doc)">
                            <xsl:variable name="docPid" select="docPid/text()" />
						    <xsl:variable name="docInfo" select="/blacklabResponse/docInfos/docInfo[@pid=$docPid]" />
							<tr class="titlerow hidden">
								<td colspan="5">
									<div class="doctitle collapse">
										<a class="text-error">
											<xsl:attribute name="href">
												<xsl:value-of select="$query_document_url" />
												<xsl:text>&amp;docpid=</xsl:text>
												<xsl:value-of select="docPid" />
											</xsl:attribute>
									 		<b><xsl:value-of select="$docPid" />:</b>&#160;
									 		<xsl:value-of select="$docInfo/*[name()=$title_name]" />
											<i>&#160;<xsl:value-of select="$result_by" />&#160;</i>
 									 		<xsl:value-of select="$docInfo/*[name()=$author_name]" />
											(<xsl:value-of select="$docInfo/*[name()=$date_name]" />)
										</a>
									</div>
								</td>
							</tr>
							
							
						</xsl:if>
			
						<tr class="concordance"><xsl:attribute name="onclick">Whitelab.search.result.showCitation('#<xsl:value-of select="$currentId" />', '<xsl:value-of select="docPid" />', <xsl:value-of select="start" />, <xsl:value-of select="end" />);</xsl:attribute>
							<td class="tbl_conc_left">...  <xsl:value-of select="left" /></td>
							<td class="tbl_conc_hit"><xsl:value-of select="match" /></td>
							<td><xsl:value-of select="right" /> ...</td>
							<td class="tbl_lemma">
							<xsl:for-each select="match/w">
								<xsl:value-of select="@*[name()=$lemma]" /><xsl:text> </xsl:text>
							</xsl:for-each>
							</td>
							<td class="tbl_pos">
							<xsl:for-each select="match/w">
								<xsl:value-of select="@*[name()=$pos_name]" /><xsl:text> </xsl:text>
							</xsl:for-each>
							</td>
						</tr> 
						<tr class="citationrow hidden">
							<td colspan="5">
								<div class="collapse inline-concordance"><xsl:attribute name="id"><xsl:value-of select="$currentId"/></xsl:attribute>Loading...</div>
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
				<xsl:attribute name="onclick">
					<xsl:text>document.location.href='</xsl:text>
					<xsl:value-of select="$query_export_url" />
					<xsl:text>'</xsl:text>
				</xsl:attribute>
				<xsl:value-of select="$result_export"/>
			</button>
		</div>
	</xsl:template>
	
	<xsl:template name="tabs">
		<ul class="nav nav-tabs" id="contentTabs">
			<li class="active disabled">
				<a>
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
			<li>
				<a>
					<xsl:attribute name="href">
						<xsl:value-of select="$query_result_url" />
						<xsl:text>&amp;view=8</xsl:text>
					</xsl:attribute>
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

	<xsl:template name="pagination">
		<xsl:variable name="resultsPerPage" select="//requestedWindowSize" />
		<xsl:variable name="totalHits" select="//numberOfHitsRetrieved" />
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
						<xsl:text>window.location.href=&quot;</xsl:text>
						<xsl:value-of select="$query_result_url" />
						<xsl:text>&amp;view=1&amp;number=&quot;+$(this).val()+&quot;&amp;sort=</xsl:text><xsl:value-of select="$sort_by" /><xsl:text>&quot;;</xsl:text>
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
									<xsl:attribute name="href">
										<xsl:value-of select="$query_result_url" />
										<xsl:text>&amp;view=1&amp;first=0&amp;number=</xsl:text>
										<xsl:value-of select="$resultsPerPage"/>
										<xsl:text>&amp;sort=</xsl:text>
										<xsl:value-of select="$sort_by" />
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
									<xsl:attribute name="href">
										<xsl:value-of select="$query_result_url" />
										<xsl:text>&amp;view=1&amp;first=</xsl:text>
										<xsl:value-of select="($currentPage - 2) * $resultsPerPage" />
										<xsl:text>&amp;number=</xsl:text>
										<xsl:value-of select="$resultsPerPage"/>
										<xsl:text>&amp;sort=</xsl:text>
										<xsl:value-of select="$sort_by" />
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
									<xsl:attribute name="href">
										<xsl:value-of select="$query_result_url" />
										<xsl:text>&amp;view=1&amp;first=</xsl:text>
										<xsl:value-of select="($currentPage * $resultsPerPage)" />
										<xsl:text>&amp;number=</xsl:text>
										<xsl:value-of select="$resultsPerPage"/>
										<xsl:text>&amp;sort=</xsl:text>
										<xsl:value-of select="$sort_by" />
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
									<xsl:attribute name="href">
										<xsl:value-of select="$query_result_url" />
										<xsl:text>&amp;view=1&amp;first=</xsl:text>
										<xsl:value-of select="(($numberOfPages - 1) * $resultsPerPage)" />
										<xsl:text>&amp;number=</xsl:text>
										<xsl:value-of select="$resultsPerPage"/>
										<xsl:text>&amp;sort=</xsl:text>
										<xsl:value-of select="$sort_by" />
									</xsl:attribute>
									<xsl:value-of select="'&gt;&gt;'" />
								</a>
							</li>
						</xsl:otherwise>
					</xsl:choose>
				</ul>
				<button onclick="Whitelab.search.result.toggleTitles()" type="button" class="toggle small" data-toggle="collapse" data-target=".doctitle" style="vertical-align: top; margin-top: 2px;">
					<xsl:value-of select="'Toggle '" /><xsl:value-of select="$result_titles" />
				</button>
			</div>
			<div class="small-text large-3 medium-3 small-4 columns">
				<xsl:value-of select="$result_page" />
				<input type="hidden" class="max-results"><xsl:attribute name="value"><xsl:value-of select="$resultsPerPage" /></xsl:attribute></input>
				<input id="page-select" class="page-select meta-small" type="number" min="1">
					<xsl:attribute name="max"><xsl:value-of select="$numberOfPages" /></xsl:attribute>
					<xsl:attribute name="value"><xsl:value-of select="$currentPage" /></xsl:attribute>
				</input>
				&#160;<xsl:value-of select="$result_of" />
				<xsl:value-of select="$numberOfPages" /><xsl:text> </xsl:text>
				<button class="small go">
					<xsl:attribute name="onclick">
						<xsl:text>var value = ($('#page-select').val() - 1) *</xsl:text><xsl:value-of select="$resultsPerPage"/><xsl:text>;</xsl:text>
						<xsl:text>document.location.href='</xsl:text>
						<xsl:value-of select="$query_result_url" />
						<xsl:text>&amp;view=1&amp;first='+value+'&amp;number=</xsl:text>
						<xsl:value-of select="$resultsPerPage"/>
						<xsl:text>&amp;sort=</xsl:text>
						<xsl:value-of select="$sort_by" />
						<xsl:text>';</xsl:text>
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
						<xsl:attribute name="href">
							<xsl:value-of select="$query_result_url" />
							<xsl:text>&amp;view=1&amp;first=</xsl:text>
							<xsl:value-of select="($start - 1) * $perpage" />
							<xsl:text>&amp;number=</xsl:text>
							<xsl:value-of select="$perpage"/>
							<xsl:text>&amp;sort=</xsl:text>
							<xsl:value-of select="$sort_by" />
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