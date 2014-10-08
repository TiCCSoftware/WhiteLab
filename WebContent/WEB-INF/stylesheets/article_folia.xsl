<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet version="1.0" xmlns="http://www.w3.org/1999/xhtml" xmlns:xlink="http://www.w3.org/1999/xlink" xmlns:imdi="http://www.mpi.nl/IMDI/Schema/IMDI" xmlns:xsl="http://www.w3.org/1999/XSL/Transform" xmlns:folia="http://ilk.uvt.nl/folia">
	<xsl:output method="html" encoding="UTF-8" omit-xml-declaration="yes" />
	
	<xsl:param name="title_name" select="'#'"/>
	<xsl:param name="current_page" select="//folia:CurrentPage"/>
	<xsl:param name="total_pages" select="//folia:TotalPages"/>
	<xsl:param name="window" select="//folia:WindowSize"/>
	<xsl:param name="query" select="''"/>
	<xsl:param name="doc_id" select="''"/>
	<xsl:param name="result_by" select="''"/>
	<xsl:param name="whitelab_page" select="'#'"/>

	<xsl:template match="text()" />
	<xsl:template match="error">
		<h1>Error</h1>
		<xsl:value-of select="." />
	</xsl:template>
	
	<xsl:template name="pagination"> 
	  <!--recursive loop until done--> 
	  <xsl:param name="i"/> 
	  <xsl:param name="max"/> 
	  <xsl:if test="$i &lt;= $max"> 
	    <!-- Repeated content Here --> 
	    <!-- use value-of i to get loop index -->
	    <xsl:choose>
	    	<xsl:when test="$i = $current_page">
	    		<li class="active"><a><xsl:value-of select="$i"/></a></li>
	    	</xsl:when>
	    	<xsl:otherwise>
	    		<xsl:variable name="startP" select="($i * $window) - ($window - 1)"/>
	    		<xsl:variable name="endP" select="$i * $window"/>
	    		<li><a><xsl:attribute name="onclick"><xsl:text>Whitelab.</xsl:text><xsl:value-of select="$whitelab_page"/><xsl:text>.document.load('</xsl:text><xsl:value-of select="$doc_id" /><xsl:text>','</xsl:text><xsl:value-of select="$query" /><xsl:text>&amp;start=</xsl:text><xsl:value-of select="$startP" /><xsl:text>&amp;end=</xsl:text><xsl:value-of select="$endP" /><xsl:text>')</xsl:text></xsl:attribute><xsl:value-of select="$i"/></a></li>
	    	</xsl:otherwise>
	    </xsl:choose>
	      <xsl:call-template name="pagination"> 
	        <xsl:with-param name="i" select="$i + 1"/> 
	        <xsl:with-param name="max" select="$max"/> 
	      </xsl:call-template> 
	  </xsl:if> 
	</xsl:template>
	
	<xsl:template match="HitsInDocument">
		<p>HitsInDocument: <xsl:value-of select="." /></p>
	</xsl:template>
	<xsl:template match="//folia:CurrentPage">
		<p>CurrentPage: <xsl:value-of select="." /></p>
	</xsl:template>
	<xsl:template match="//folia:TotalPages">
		<p>TotalPages: <xsl:value-of select="." /></p>
	</xsl:template>
	<xsl:template match="//folia:StartPar">
		<p>StartPar: <xsl:value-of select="." /></p>
	</xsl:template>
	<xsl:template match="//folia:EndPar">
		<p>EndPar: <xsl:value-of select="." /></p>	
	</xsl:template>
	<xsl:template match="//folia:ParCount">	
		<p>ParCount: <xsl:value-of select="." /></p>
	</xsl:template>
	
	<xsl:template match="folia:DocumentFields">
		<xsl:variable name="numhits" select="./HitsInDocument" />
		<xsl:variable name="title" select="*[name()=$title_name]" />
		<xsl:variable name="author" select="./AuthorName" />
		<div class="large-16 medium-16 small-16 article_metadata">
			<xsl:if test="string-length($title)!=0">
				<h4>
					<span class="document_title">
						<xsl:value-of select="*[name()=$title_name]" />
					</span>
				</h4>
			</xsl:if>
			<xsl:if test="string-length($author)!=0">
				<h4><i>
				<xsl:value-of select="$result_by" />
				</i>
				<span class="doc_author">
					<xsl:value-of select="$author" />
				</span>
				</h4>
			</xsl:if>
		</div>
		<xsl:if test="$total_pages &gt; 1">
			<div class="pagination"><ul class="pagebuttons">
		    <xsl:choose>
		    	<xsl:when test="$current_page = 1">
		    		<xsl:text><li class="disabled"><a href="#">Prev</a></li></xsl:text>
		    	</xsl:when>
		    	<xsl:otherwise>
		    		<xsl:variable name="a">
					   <xsl:choose>
					     <xsl:when test="((($current_page - 1) * $window) - ($window - 1)) &lt; 1">1</xsl:when>
					     <xsl:otherwise><xsl:value-of select="(($current_page - 1) * $window) - ($window - 1)"/></xsl:otherwise>
					   </xsl:choose>
					 </xsl:variable>
		    		<xsl:variable name="b">
					   <xsl:choose>
					     <xsl:when test="($current_page - 1) &gt; $total_pages"><xsl:value-of select="$total_pages"/></xsl:when>
					     <xsl:otherwise><xsl:value-of select="($current_page - 1) * $window"/></xsl:otherwise>
					   </xsl:choose>
					 </xsl:variable>
		    		<li><a><xsl:attribute name="onclick"><xsl:text>Whitelab.</xsl:text><xsl:value-of select="$whitelab_page"/><xsl:text>.document.load('</xsl:text><xsl:value-of select="$doc_id" /><xsl:text>','</xsl:text><xsl:value-of select="$query" /><xsl:text>&amp;start=</xsl:text><xsl:value-of select="$a" /><xsl:text>&amp;end=</xsl:text><xsl:value-of select="$b" /><xsl:text>')</xsl:text></xsl:attribute>Prev</a></li>
		    	</xsl:otherwise>
		    </xsl:choose>
		    <xsl:choose>
		    	<xsl:when test="$current_page &lt; 11">
		    		<xsl:variable name="a" select="1"/>
		    		<xsl:variable name="b">
					   <xsl:choose>
					     <xsl:when test="($current_page + 10) &gt; $total_pages"><xsl:value-of select="$total_pages"/></xsl:when>
					     <xsl:otherwise><xsl:value-of select="$current_page + 10"/></xsl:otherwise>
					   </xsl:choose>
					 </xsl:variable>
		    		<xsl:call-template name="pagination"> 
					  <xsl:with-param name="i" select="$a"/> 
					  <xsl:with-param name="max" select="$b"/> 
					</xsl:call-template>
		    		<xsl:if test="$b &lt; $total_pages">
		    			<xsl:text><li class="disabled"><a href="#">...</a></li></xsl:text>
		    		</xsl:if>
		    	</xsl:when>
		    	<xsl:when test="$current_page &gt; ($total_pages - 10)">
		    		<xsl:variable name="a">
					   <xsl:choose>
					     <xsl:when test="($current_page - 10) &lt; 1">1</xsl:when>
					     <xsl:otherwise><xsl:value-of select="$current_page - 10"/></xsl:otherwise>
					   </xsl:choose>
					 </xsl:variable>
		    		<xsl:variable name="b" select="$total_pages"/>
		    		<xsl:if test="$a &gt; 1">
		    			<xsl:text><li class="disabled"><a href="#">...</a></li></xsl:text>
		    		</xsl:if>
		    		<xsl:call-template name="pagination"> 
					  <xsl:with-param name="i" select="$a"/> 
					  <xsl:with-param name="max" select="$b"/> 
					</xsl:call-template>
		    	</xsl:when>
		    	<xsl:otherwise>
		    		<xsl:variable name="a">
					   <xsl:choose>
					     <xsl:when test="($current_page - 10) &lt; 1">1</xsl:when>
					     <xsl:otherwise><xsl:value-of select="$current_page - 10"/></xsl:otherwise>
					   </xsl:choose>
					 </xsl:variable>
		    		<xsl:variable name="b">
					   <xsl:choose>
					     <xsl:when test="($current_page + 10) &gt; $total_pages"><xsl:value-of select="$total_pages"/></xsl:when>
					     <xsl:otherwise><xsl:value-of select="$current_page + 10"/></xsl:otherwise>
					   </xsl:choose>
					 </xsl:variable>
		    		<xsl:if test="$a &gt; 1">
		    			<xsl:text><li class="disabled"><a href="#">...</a></li></xsl:text>
		    		</xsl:if>
		    		<xsl:call-template name="pagination"> 
					  <xsl:with-param name="i" select="$a"/> 
					  <xsl:with-param name="max" select="$b"/> 
					</xsl:call-template>
		    		<xsl:if test="$b &lt; $total_pages">
		    			<xsl:text><li class="disabled"><a href="#">...</a></li></xsl:text>
		    		</xsl:if>
		    	</xsl:otherwise>
		    </xsl:choose>
			
			
		    <xsl:choose>
		    	<xsl:when test="$current_page = $total_pages">
		    		<xsl:text><li class="disabled"><a href="#">Next</a></li></xsl:text>
		    	</xsl:when>
		    	<xsl:otherwise>
		    		<xsl:variable name="a">
					   <xsl:choose>
					     <xsl:when test="((($current_page + 1) * $window) - ($window - 1)) &lt; 1">1</xsl:when>
					     <xsl:otherwise><xsl:value-of select="(($current_page + 1) * $window) - ($window - 1)"/></xsl:otherwise>
					   </xsl:choose>
					 </xsl:variable>
		    		<xsl:variable name="b">
					   <xsl:choose>
					     <xsl:when test="($current_page + 1) &gt; $total_pages"><xsl:value-of select="$total_pages"/></xsl:when>
					     <xsl:otherwise><xsl:value-of select="($current_page + 1) * $window"/></xsl:otherwise>
					   </xsl:choose>
					 </xsl:variable>
		    		<li><a><xsl:attribute name="onclick"><xsl:text>Whitelab.</xsl:text><xsl:value-of select="$whitelab_page"/><xsl:text>.document.load('</xsl:text><xsl:value-of select="$doc_id" /><xsl:text>','</xsl:text><xsl:value-of select="$query" /><xsl:text>&amp;start=</xsl:text><xsl:value-of select="$a" /><xsl:text>&amp;end=</xsl:text><xsl:value-of select="$b" /><xsl:text>')</xsl:text></xsl:attribute>Next</a></li>
		    	</xsl:otherwise>
		    </xsl:choose>
			</ul></div>
		</xsl:if>
	</xsl:template>
	
	<xsl:template match="/folia:FoLiA">
		<div class="large-16 medium-16 small-16 contentbox">
	        <xsl:apply-templates />
	    </div>
	</xsl:template>
	
	<xsl:template match="folia:text">
	 <div class="large-16 medium-16 small-16 contentbox text">
	 	<xsl:apply-templates />
	 </div>
	</xsl:template>
	
	<xsl:template match="folia:div">
	 <div class="div"> 
	   <xsl:apply-templates />
	 </div>
	</xsl:template>
	
	<xsl:template match="folia:p">
	 <p id="{@xml:id}">
	  <xsl:apply-templates />
	 </p>
	</xsl:template>
	
	<xsl:template match="folia:event">
	  <xsl:choose>
	    <xsl:when test="@actor">
	         <p id="{@xml:id}"><span class="actor"><xsl:value-of select="@actor" />: </span>
			  <xsl:apply-templates select=".//folia:s" />
			 </p>
	    </xsl:when>
	    <xsl:otherwise>
	         <p id="{@xml:id}">
			  <xsl:apply-templates select=".//folia:s" />
			 </p>
	    </xsl:otherwise>
	  </xsl:choose>
	</xsl:template>
	
	
	<xsl:template match="folia:gap">
	 <pre class="gap">
	  <xsl:value-of select="folia:content" />
	 </pre>
	</xsl:template>
	
	
	<xsl:template match="folia:head">
	    <h6>
	        <xsl:apply-templates />
	    </h6>
	</xsl:template>
	
	<xsl:template match="folia:list">
	<ul>
	    <xsl:apply-templates />
	</ul>
	</xsl:template>
	
	<xsl:template match="folia:listitem">
	<li><xsl:apply-templates /></li>
	</xsl:template>
	
	<xsl:template match="folia:s">
	 <span id="{@xml:id}" class="s"><xsl:apply-templates select=".//folia:w|folia:whitespace|folia:br" /></span>
	</xsl:template>
	
	<xsl:template match="folia:w">
		<xsl:choose>
			<xsl:when test="parent::folia:hl">
				<span id="{@xml:id}" class="word"><a>
					<xsl:attribute name="name">
						<xsl:value-of select="generate-id()" />
					</xsl:attribute>
					<xsl:attribute name="class">anchor hl</xsl:attribute>
					<!-- <xsl:apply-templates /> -->
					<span class="t"><xsl:value-of select=".//folia:t[1]"/></span>
				</a><xsl:call-template name="tokenannotations" /></span>
			</xsl:when>
			<xsl:otherwise>
				<span id="{@xml:id}" class="word"><span class="t"><a class="anchor"><xsl:value-of select=".//folia:t[1]"/></a></span><xsl:call-template name="tokenannotations" /></span>
			</xsl:otherwise>
		</xsl:choose>
		<xsl:choose>
		   <xsl:when test="@space = 'no'"></xsl:when>
		   <xsl:otherwise>
		    <xsl:text> </xsl:text>
		   </xsl:otherwise>
		</xsl:choose>
	</xsl:template>
	
	<xsl:template name="tokenannotations">
	 <span class="attributes hoverdiv">
	 	<span class="attrlabel tokenid"><xsl:value-of select="@xml:id" /></span><br />
		<xsl:if test="folia:phon">
	        	<span class="attrlabel">Phonetics</span><span class="attrvalue"><xsl:value-of select="folia:phon/@class" /></span><br />
	    </xsl:if>
	        <xsl:if test="folia:pos">
	        	<span class="attrlabel">PoS</span><span class="attrvalue"><xsl:value-of select="folia:pos/@class" /></span><br />
	        </xsl:if>
	        <xsl:if test="folia:lemma">
				<span class="attrlabel">Lemma</span><span class="attrvalue"><xsl:value-of select="folia:lemma/@class" /></span><br />
	        </xsl:if>
	        <xsl:if test="folia:sense">
				<span class="attrlabel">Sense</span><span class="attrvalue"><xsl:value-of select="folia:sense/@class" /></span><br />
	        </xsl:if>
	        <xsl:if test="folia:subjectivity">
				<span class="attrlabel">Subjectivity</span><span class="attrvalue"><xsl:value-of select="folia:subjectivity/@class" /></span><br />
	        </xsl:if>
	        <xsl:if test="folia:errordetection[@errors='yes']">
				<span class="attrlabel">Error detection</span><span class="attrvalue">Possible errors</span><br />        
	        </xsl:if>
	        <xsl:if test="folia:correction">
	            <xsl:if test="folia:correction/folia:suggestion/folia:t">
	            	<span class="attrlabel">Suggestion(s) for text correction</span><span class="attrvalue"><xsl:for-each select="folia:correction/folia:suggestion/folia:t">
	                    <em><xsl:value-of select="." /></em><xsl:text> </xsl:text>
	                </xsl:for-each></span><br />        
	            </xsl:if>
	            <xsl:if test="folia:correction/folia:original/folia:t">
	            	<span class="attrlabel">Original pre-corrected text</span>
	            	<span class="attrvalue">                
	                <xsl:for-each select="folia:correction/folia:original/folia:t[1]">
	                    <em><xsl:value-of select="." /></em><xsl:text> </xsl:text>
	                </xsl:for-each>      
	                </span><br />            
	            </xsl:if>            
	        </xsl:if>
	 </span>
	</xsl:template>
	
	<xsl:template match="folia:whitespace">
		<xsl:text> </xsl:text>
	</xsl:template>
	
	<xsl:template match="folia:br">
		<br />
	</xsl:template>
	
	<xsl:template match="folia:figure">
	 <div class="figure">
	  <img>
	      <xsl:attribute name="src">
	        <xsl:value-of select="@src" />
	      </xsl:attribute>
	      <xsl:attribute name="alt">
	        <xsl:value-of select="folia:desc" />
	      </xsl:attribute>      
	  </img>
	  <xsl:if test="folia:caption">
	   <div class="caption">
	     <xsl:apply-templates select="folia:caption/*" />
	   </div>
	  </xsl:if>
	 </div>
	</xsl:template>
	
</xsl:stylesheet>