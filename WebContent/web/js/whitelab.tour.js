Whitelab.tour = {
	intro : null,
	page : null,
	
	getOptions : function(ns,page,step,lang) {
		if (ns === "explore") {
			if (page === "corpus") {
				if (lang === "nl") {
					return {
						steps: [
						    {
						    	// 1
						    	element: "#explore-nav",
			                      intro: "<p>Dit is de WhiteLab Verken interface. Hier kunt u:" +
			                      		"<ul>" +
			                      		"<li>Corpusverdelingen bekijken</li>" +
			                      		"<li>Statistieken van subcorpora opvragen</li>" +
			                      		"<li>N-grammen van subcorpora ophalen</li>" +
			                      		"<li>Specifieke documenten opzoeken a.h.v. de document ID</li>" +
			                      		"</ul></p>",
			                      position: "right"
						    },
		                    {
		                    	// 2
		                      element: "#treemap-options",
		                      intro: "<p>In het <b>corpus</b> overzicht kunt u uit verschillende kenmerken kiezen om het corpus te verdelen.</p>",
		                      position: "right"
		                    },
		                    {
		                    	// 3
		                      element: "#treemap-display",
		                      intro: "<p>Het blokkenschema toont hoe de verschillende subcorpora zich tot elkaar verhouden in grootte.</p>" +
		                      		"<p>Let op dat de verdeling in eerste instantie getoond wordt op een logaritmische schaal. " +
		                      		"De radio buttons bovenaan kunnen gebruikt worden om snel te schakelen tussen logaritmische en absolute schaal.</p>" +
		                      		"<p>E&eacute;n blok staat gelijk aan &eacute;&eacute;n subcorpus. Als u met de muis over een blok beweegt, dan verschijnt de grootte van het subcorpus. De grootte wordt uitgedrukt in " +
		                      		"het aantal documenten, zowel absoluut als procentueel.</p>",
		                      position: "left"
		                    }
						]
					};
				} else {
					return {
						steps: [
						    {
						    	// 1
						    	element: "#explore-nav",
			                      intro: "<p>This is the WhiteLab Explore view. It provides interfaces to:" +
		                      		"<ul>" +
		                      		"<li>Explore corpus composition</li>" +
		                      		"<li>Retrieve statistics for a user defined subcorpus</li>" +
		                      		"<li>Retrieve n-grams for a user defined subcorpus</li>" +
		                      		"<li>Look up specific documents by their ID</li>" +
		                      		"</ul></p>",
			                      position: "right"
						    },
		                    {
		                    	// 2
		                      element: "#treemap-options",
		                      intro: "<p>The <b>corpus</b> composition view allows for selection of several main attributes to divide the corpus.</p>",
		                      position: "right"
		                    },
		                    {
		                    	// 3
		                      element: "#treemap-display",
		                      intro: "<p>The treemap display shows the sizes of the different subcorpora.</p>" +
		                      		"<p>Please note that the composition is initially " +
		                      		"shown on a logarithmic scale. Radio buttons at the top of the display provide an easy way to switch between logarithmic " +
		                      		"and absolute scale.</p>" +
		                      		"<p>One block represents one subcorpus. When the mouse is placed on a block, its size is shown. The size is expressed in " +
		                      		"number of documents and the percentage of all documents that is contained within the selected subcorpus.</p>",
		                      position: "left"
		                    }
						]
					};
				}
			} else if (page === "statistics") {
				if (step == 1) {
					if (lang === "nl") {
						return {
							steps: [
										{
											// 4
											element: "#metadata",
											intro: "<p>In het <b>statistieken</b> overzicht kunt u verschillende filters selecteren en zo naar eigen wens een subcorpus samenstellen.</p>",
											position: "bottom"
										},
										{
											// 5
											element: "#stats-table",
											intro: "<p>Tevens kunt u selecteren of u een frequentielijst wil van woorden, lemmas of woordsoorten.</p>",
											position: "bottom"
										}
							        ]
						};
					} else {
						return {
							steps: [
										{
											// 4
											element: "#metadata",
											intro: "<p>In the <b>statistics</b> view various filters can be applied to select only those parts of the corpus that you are interested in.</p>",
											position: "bottom"
										},
										{
											// 5
											element: "#stats-table",
											intro: "<p>Additionally, you can select to retrieve a frequency list of either words, lemmas, or part-of-speech tags.</p>",
											position: "bottom"
										}
							        ]
						};
					}
				} else if (step == 2) {
					if (lang === "nl") {
						return {
							steps: [
										{
											// 6
											element: "#stats-info",
											intro: "<p>Nadat u op de 'Zoek' knop heeft geklikt, verschijnt hier een overzicht van de door u geselecteerde opties en eenvoudige statistieken van de resultaten.</p>",
											position: "top"
										},
										{
											// 7
											element: "#result_statistics",
											intro: "<p>De resultaten zijn verdeeld in 4 secties: frequentielijst, documentenlijst, vocabulaire groei en word cloud.</p>" +
													"<p>Afhankelijk van de selectie die u gemaakt heeft bij 'Frequentielijst type', wordt de <b>frequentielijst</b> getoond van alle woorden, lemmas of woordsoorten " +
													"in het subcorpus, aflopend gesorteerd op frequentie. De sortering van de lijst kan gewijzigd worden met behulp van de driehoeken (▲ en ▼) " +
													"naast de kolomnamen.</p>",
											position: "top"
										}
							        ]
						};
					} else {
						return {
							steps: [
										{
											// 6
											element: "#stats-info",
											intro: "<p>After clicking the 'Search' button, the selected options and basic search statistics are displayed here.</p>",
											position: "top"
										},
										{
											// 7
											element: "#result_statistics",
											intro: "<p>The results section is divided into 4 sections: frequency list, document list, vocabulary growth, and word cloud.</p>" +
													"<p>Depending on the selection made under 'Frequency list type', the <b>frequency list</b> shows all words, lemmas, or part-of-speech tag " +
													"present in the subcorpus, ordered by their frequency (descending). The sort order of the list can be changed using the triangles (▲ and ▼) " +
													"next to the table headers.</p>",
											position: "top"
										}
							        ]
						};
					}
				} else if (step == 3) {
					if (lang === "nl") {
						return {
							steps: [
										{
											// 8
											element: "#result_statistics",
											intro: "<p>De <b>documentenlijst</b> toont alle documenten in het subcorpus. Wederom kan de sortering gewijzigd worden met de driehoeken (▲ en ▼) naast de kolomnamen. " +
													"De inhoud van een specifiek document vraagt u op door op 'Toon document' te klikken.</p>",
											position: "top"
										}
							        ]
						};
					} else {
						return {
							steps: [
										{
											// 8
											element: "#result_statistics",
											intro: "<p>The <b>document list</b> shows all documents contained in the subcorpus. Again, the list can be sorted using the triangles (▲ and ▼) next to the table headers. " +
													"The contents of a specific document can be viewed by clicking the 'Show document' button.</p>",
											position: "top"
										}
							        ]
						};
					}
				} else if (step == 4) {
					if (lang === "nl") {
						return {
							steps: [
										{
											// 9
											element: "#result_statistics",
											intro: "<p>De <b>vocabulaire groei</b> wordt bepaald over de documenten in het subcorpus. In eerste instantie wordt alleen de groei van het eerste document getoond. " +
													"U kunt meer documenten toevoegen aan de grafiek door op de 'Laad meer' knop te drukken.</p>",
											position: "top"
										}
							        ]
						};
					} else {
						return {
							steps: [
										{
											// 9
											element: "#result_statistics",
											intro: "<p>The <b>vocabulary growth</b> is determined over the documents in the subcorpus. When first switching to this view, only the growth of the first document " +
													"is shown. More documents can be loaded into the graph by clicking the 'Load more' button.</p>",
											position: "top"
										}
							        ]
						};
					}
				} else if (step == 5) {
					if (lang === "nl") {
						return {
							steps: [
										{
											// 10
											element: "#result_statistics",
											intro: "<p>De <b>word cloud</b> bevat de meest frequente termen in het subcorpus en is logischerwijs gebaseerd op de frequentielijst.</p>" +
													"<p>U kunt meer woorden toevoegen aan de cloud door het aantal resultaten per pagina aan te passen bij de <b>frequentielijst</b>.</p>",
											position: "top"
										}
							        ]
						};
					} else {
						return {
							steps: [
										{
											// 10
											element: "#result_statistics",
											intro: "<p>The <b>word cloud</b> contains the most frequent terms in the subcorpus and is logically based on the frequency list.</p>" +
													"<p>More words can be loaded into the word cloud by changing the amount of terms per page in the frequency list.</p>",
											position: "top"
										}
							        ]
						};
					}
				}
			} else if (page === "ngrams") {
				if (step == 1) {
					if (lang === "nl") {
						return {
							steps: [
									{
										// 11
										element: "#metadata",
										intro: "<p>In het <b>n-gram</b> overzicht kunt u verschillende filters selecteren en zo naar eigen wens een subcorpus samenstellen.</p>",
										position: "bottom"
									},
									{
										// 12
										element: "#ngram-table",
										intro: "<p>U kunt zoeken naar n-grammen van groottes 1 tot en met 5. Voor elke positie in het n-gram kunt u een term invullen die op die positie voor " +
												"moet komen, alsmede het type waarmee deze term moet overeenstemmen (woord, lemma, of woordsoort). Als u geen term invult, " +
												"dan zijn op die positie alle woorden toegestaan.</p>",
										position: "top"
									}
							        ]
						};
					} else {
						return {
							steps: [
				                    {
				                    	// 11
				                    	element: "#metadata",
				                    	intro: "<p>In the <b>n-gram</b> view various filters can be applied to select only those parts of the corpus that you are interested in.</p>",
				                    	position: "bottom"
				                    },
				                    {
				                    	// 12
				                    	element: "#ngram-table",
				                    	intro: "<p>You can search for n-grams of sizes 1 through 5. For each position in the n-gram, you have the option to provide a pattern to " +
				                    			"be matched on that position, and the type against which the pattern is matched (word, lemma, or part of speech). If no pattern " +
				                    			"is provided, then any word occurring in that position will be considered a match.</p>",
				                    	position: "top"
				                    }
							        ]
						};
					}
				} else if (step == 2) {
					if (lang === "nl") {
						return {
							steps: [
				                    {
				                    	// 13
				                    	element: "#ngram-info",
				                    	intro: "<p>Nadat u op de 'Zoek' knop heeft geklikt, verschijnt hier een overzicht van de door u geselecteerde opties en eenvoudige statistieken van de resultaten.</p>",
				                    	position: "top"
				                    },
				                    {
				                    	// 14
				                    	element: "#result_ngram",
				                    	intro: "<p>De n-gram resultaten worden aflopend gesorteerd op hun frequentie in het subcorpus. De sortering kan gewijzigd worden met de driehoeken (▲ en ▼) naast de kolomnamen.</p>",
				                    	position: "top"
				                    }
							        ]
						};
					} else {
						return {
							steps: [
				                    {
				                    	// 13
				                    	element: "#ngram-info",
				                    	intro: "<p>After clicking the 'Search' button, the selected options and basic search statistics are displayed here.</p>",
				                    	position: "top"
				                    },
				                    {
				                    	// 14
				                    	element: "#result_ngram",
				                    	intro: "<p>The n-gram results are listed in reverse order by frequency of occurrence in the subcorpus. Sort order can be changed using the triangles (▲ and ▼) next to the table headers.</p>",
				                    	position: "top"
				                    }
							        ]
						};
					}
				}
			} else if (page === "document") {
				if (step == 1) {
					if (lang === "nl") {
						return {
							steps: [
				                    {
				                    	// 15
				                    	element: "#document",
				                    	intro: "<p>In het <b>document</b> overzicht kunt u een specifieke ID invoeren om dat document te bekijken.</p>",
				                    	position: "bottom"
				                    }
							        ]
						};
					} else {
						return {
							steps: [
				                    {
				                    	// 15
				                    	element: "#document",
				                    	intro: "<p>In the <b>document</b> view, a single document identifier can be submitted to retrieve that document from the corpus.</p>",
				                    	position: "bottom"
				                    }
							        ]
						};
					}
				} else if (step == 2) {
					if (lang === "nl") {
						return {
							steps: [
				                    {
				                    	// 16
				                    	element: "#doc-display",
				                    	intro: "<p>Het overzicht is onderverdeeld in 4 secties: tekst, metadata, statistieken en word cloud.</p>" +
				                    			"<p>De sectie <b>tekst</b> toont de daadwerkelijke inhoud van het document. Wanneer u met de muis over een woord beweegt wordt extra, lingu&iuml;stische informatie van dat woord getoond.</p>",
				                    	position: "top"
				                    }
							        ]
						};
					} else {
						return {
							steps: [
				                    {
				                    	// 16
				                    	element: "#doc-display",
				                    	intro: "<p>The document result is divided into 4 sections: text, metadata, statistics, and word cloud.</p>" +
				                    			"<p>The <b>text</b> section displays the actual contents of the document. Hovering over a word with the mouse reveals additional linguistic information for that word.</p>",
				                    	position: "top"
				                    }
							        ]
						};
					}
				} else if (step == 3) {
					if (lang === "nl") {
						return {
							steps: [
				                    {
				                    	// 17
				                    	element: "#doc-display",
				                    	intro: "<p>De sectie <b>metadata</b> toont de metadata van het huidige document.</p>",
				                    	position: "top"
				                    }
							        ]
						};
					} else {
						return {
							steps: [
				                    {
				                    	// 17
				                    	element: "#doc-display",
				                    	intro: "<p>The <b>metadata</b> tab shows metadata associated with the document.</p>",
				                    	position: "top"
				                    }
							        ]
						};
					}
				} else if (step == 4) {
					if (lang === "nl") {
						return {
							steps: [
				                    {
				                    	// 18
				                    	element: "#doc-display",
				                    	intro: "<p>De <b>statistieken</b> tonen de vocabulaire groei van het document en informatie omtrent de woorsoorten die in het document voorkomen.</p>",
				                    	position: "top"
				                    }
							        ]
						};
					} else {
						return {
							steps: [
				                    {
				                    	// 18
				                    	element: "#doc-display",
				                    	intro: "<p>The <b>statistics</b> tab shows the document's vocabulary growth, and information on the parts of speech used.</p>",
				                    	position: "top"
				                    }
							        ]
						};
					}
				} else if (step == 5) {
					if (lang === "nl") {
						return {
							steps: [
				                    {
				                    	// 19
				                    	element: "#doc-display",
				                    	intro: "<p>Tenslotte wordt een word cloud gegenereerd van de meest voorkomende termen in het document.</p>",
				                    	position: "top"
				                    },
				                    {
				                    	// 20
				                    	element: "#main-search",
				                    	intro: "<p>U heeft het einde van deze rondleiding bereikt. Om verder te gaan klikt u op 'Zoek' en vervolgens op het groene vraagteken <img src='../web/img/info_icon.png' style='width: 15px; height: 15px;'/> links boven.</p>",
				                    	position: "bottom"
				                    }
							        ]
						};
					} else {
						return {
							steps: [
				                    {
				                    	// 19
				                    	element: "#doc-display",
				                    	intro: "<p>Finally, a word cloud of the most frequent words in the document is generated.</p>",
				                    	position: "top"
				                    },
				                    {
				                    	// 20
				                    	element: "#main-search",
				                    	intro: "<p>You have reached the end of this tour. To continue, please click 'Search' followed by the green question mark <img src='../web/img/info_icon.png' style='width: 15px; height: 15px;'/> at the top left.</p>",
				                    	position: "bottom"
				                    }
							        ]
						};
					}
				}
			}
		} else if (ns === "search") {
			if (page === "simple") {
				if (lang === "nl") {
					return {
						steps: [
			                    {
			                    	// 1
			                      element: "#search-nav",
			                      intro: "<p>Dit is de WhiteLab Zoek interface. Het biedt interfaces voor verschillende soorten gebruikers en zoekstrategieën:" +
			                      		"<ul>" +
			                      		"<li>Simpel zoeken voor beginners</li>" +
			                      		"<li>Uitgebreid zoeken voor gevorderde gebruikers</li>" +
			                      		"<li>Geavanceerd zoeken voor ervaren gebruikers</li>" +
			                      		"<li>Expert zoeken voor experts</li>" +
			                      		"</ul></p>",
			                      position: "right"
			                    },
			                    {
			                    	// 2
			                    	element: "#simple-input",
			                    	intro: "<p><b>Simpel</b> zoeken geeft slechts een zoekveld. Hier kunt u een term of frase invullen om te zoeken naar alle voorkomens in het gehele corpus.</p>",
			                    	positon: "bottom"
			                    }
						        ]
					};
				} else {
					return {
						steps: [
			                    {
			                    	// 1
			                      element: "#search-nav",
			                      intro: "<p>This is the WhiteLab Search view. It provides interfaces for different types of users and search strategies:" +
			                      		"<ul>" +
			                      		"<li>Simple search for beginners</li>" +
			                      		"<li>Extended search for intermediate users</li>" +
			                      		"<li>Advanced search for experienced users</li>" +
			                      		"<li>Expert search for expert users</li>" +
			                      		"</ul></p>",
			                      position: "right"
			                    },
			                    {
			                    	// 2
			                    	element: "#simple-input",
			                    	intro: "<p>The <b>simple</b> view offers a single input box. Here you can enter a word or a phrase to search for its occurrence throughout the entire corpus.</p>",
			                    	positon: "bottom"
			                    }
						        ]
					};
				}
			} else if (page === "extended") {
				if (lang === "nl") {
					return {
						steps: [				
								{
									// 3
									element: "#metadata",
									intro: "<p>Bij <b>uitgebreid</b>, <b>geavanceerd</b> en <b>expert</b> zoeken kunt u verschillende filters selecteren en zo naar eigen wens een subcorpus samenstellen.</p>" +
											"<p>U kunt er ook voor kiezen om de resultaten onmiddellijk te groeperen op basis van verschillende kenmerken, en aangeven of een match zins- of paragraafgrenzen mag overschrijden met 'Zoek binnen'.</p>",
									position: "bottom"
								},
								{
									// 4
									element: "#extended",
									intro: "<p>Bij <b>uitgebreid</b> zoeken heeft u de mogelijkheid om te zoeken naar een term of frase in zijn oppervlakte vorm (woord) of zijn gelemmatiseerde vorm (lemma), of om een specifieke woordsoort te selecteren.</p>",
									position: "top"
								},
								{
									// 5
									element: "#extended",
									intro: "<p>Het is ook mogelijk om de velden te combineren en een meer precieze zoekopdracht te cre&euml;ren. Let op dat het aantal termen in ieder veld overeen moet komen om de zoekopdracht succesvol uit te voeren.</p>",
									position: "top"
								},
								{
									// 6
									element: "#extended",
									intro: "<p>Bij <b>uitgebreid</b> zoeken kunt u meerdere zoekopdrachten tegelijkertijd uitvoeren (als een \"batch\"). Om dit te doen klikt u op de batch knop <img src='../web/img/load.png' style='width: 15px; height: 15px; border:1px solid #555;'/>" +
											" en selecteert u een bestand (.txt) van uw harde schijf met de termen of frases waar u naar wilt zoeken. Nadat het bestand geladen is wordt de inhoud weergegeven in het zoekveld en kunt u deze nog aanpassen indien nodig.</p>",
									position: "top"
								},
								{
									// 7
									element: "#extended input.splitcheck",
									intro: "<p>Wanneer u een batch laadt uit een bestand, dan wordt automatisch dit vakje aangevinkt. Als het aangevinkt is, dan worden de zoekopdrachten in de batch ieder als aparte zoekopdracht uitgevoerd met de resultaten in afzonderlijke schermen.</p>" +
											"<p>Indien u de zoekopdrachten in de batch als &eacute;&eacute;n zoekopdracht uit wil voeren en de resultaten in &eacute;&eacute;n scherm gepresenteerd wil zien, zet u dan dit vakje uit.</p>",
									position: "top"
								}
						        ]
					};
				} else {
					return {
						steps: [
			                    {
			                    	// 3
			                    	element: "#metadata",
			                    	intro: "<p>The <b>extended</b>, <b>advanced</b>, and <b>expert</b> views all allow you to apply filters to select only those parts of the corpus that you are interested in.</p>" +
			                    			"<p>You can also select to immediately group the hits or documents by several attributes, and define if matches may cross sentence or paragraph boundaries using the 'Search within' option.</p>",
			                    	position: "bottom"
			                    },
			                    {
			                    	// 4
			                    	element: "#extended",
			                    	intro: "<p>In the <b>extended</b> view, you have the option to search for a word or phrase in its surface form (word) or its lemmatized form (lemma), or to select a specific part of speech.</p>",
			                    	position: "top"
			                    },
			                    {
			                    	// 5
			                    	element: "#extended",
			                    	intro: "<p>It is also possible to combine the fields to create a more precise query. Be aware that the term length of the phrases in each field must match for the query to be successful.</p>",
			                    	position: "top"
			                    },
			                    {
			                    	// 6
			                    	element: "#extended",
			                    	intro: "<p>The <b>extended</b> view allows for queries to be performed in batches. To achieve this, you press the batch button <img src='../web/img/load.png' style='width: 15px; height: 15px; border:1px solid #555;'/>" +
			                    			" and select a file (.txt) from your hard drive that contains the terms or phrases that you want to search for. After loading the file, the contents are displayed in the input field and can " +
			                    			"be adjusted if needed.</p>",
			                    	position: "top"
			                    },
			                    {
			                    	// 7
			                    	element: "#extended input.splitcheck",
			                    	intro: "<p>When you load a batch list from file, this box is automatically checked. When checked, the queries in the batch are all executed as separate queries with the results presented in separate screens.</p>" +
			                    			"<p>If you wish to execute the batch queries as a single query and have the results accumulated over all queries, uncheck this box.</p>",
			                    	position: "top"
			                    }
						        ]
					};
				}
			} else if (page === "advanced") {
				if (lang === "nl") {
					return {
						steps: [
			                    {
			                    	// 8
			                    	element: "#advanced",
			                    	intro: "<p>Bij <b>geavanceerd</b> zoeken kunt u complexe zoekopdrachten op een visuele manier samenstellen. Elke kolom staat gelijk aan een positie in de frase.</p><p>De huidige zoekopdracht zal bijvoorbeeld " +
			                    			"zoeken naar frases van 3 termen die beginnen met een lidwoord en eindigen met een zelfstandig naamwoord. Wanneer u het invulveld leeg laat, zoals gedaan is in de tweede kolom, dan is ieder woord toegestaan op deze positie.</p>",
			                    	position: "bottom"
			                    },
			                    {
			                    	// 9
			                    	element: "#advanced",
			                    	intro: "<p>Complexiteit kan ook toegevoegd worden binnen een kolom door condities te defini&euml;ren met behulp van OR en AND (<img src='../web/img/plus.png' style='width: 15px; height: 15px;'/>).</p><p>De huidige zoekopdracht " +
			                    			"zoekt bijvoorbeeld naar alle termen met de oppervlakte vorm 'hond' of 'kat' die geclassificeerd zijn als zelfstandig naamwoord.</p>",
			                    	position: "bottom"
			                    },
			                    {
			                    	// 10
			                    	element: "#advanced",
			                    	intro: "<p>Verder kunt u verschillende manieren selecteren om een term te matchen met een woord in het corpus.</p><p>De huidige zoekopdracht zoekt bijvoorbeeld naar alle woorden die beginnen met 'hond'.</p>",
			                    	position: "bottom"
			                    },
			                    {
			                    	// 11
			                    	element: "#advanced",
			                    	intro: "<p>Evenals bij uitgebreid zoeken kunt u bij <b>geavanceerd</b> zoeken meerdere zoekopdrachten als een batch uitvoeren. Om dit te doen klikt u op de batch knop <img src='../web/img/load.png' style='width: 15px; height: 15px; border:1px solid #555;'/>" +
			                    			" en selecteert u een bestand (.txt) van uw harde schijf met de termen of frases waar u naar wilt zoeken. Nadat het bestand geladen is wordt de inhoud weergegeven in het zoekveld en kunt u deze nog aanpassen indien nodig.</p>" +
			                    			"<p>Let u alstublieft op dat, in tegenstelling tot de batch optie bij uitgebreid zoeken, een batch lijst hier slechts &eacute;&eacute;n term per regel mag bevatten.</p>",
			                    	position: "bottom"
			                    },
			                    {
			                    	// 12
			                    	element: "#advanced input.splitcheck",
			                    	intro: "<p>Wanneer u een batch laadt uit een bestand, dan wordt automatisch dit vakje aangevinkt. Als het aangevinkt is, dan worden de zoekopdrachten in de batch ieder als aparte zoekopdracht uitgevoerd met de resultaten in afzonderlijke schermen.</p>" +
		                			"<p>Indien u de zoekopdrachten in de batch als &eacute;&eacute;n zoekopdracht uit wil voeren en de resultaten in &eacute;&eacute;n scherm gepresenteerd wil zien, zet u dan dit vakje uit.</p>",
			                    	position: "bottom"
			                    }
						        ]
					};
				} else {
					return {
						steps: [
			                    {
			                    	// 8
			                    	element: "#advanced",
			                    	intro: "<p>In the <b>advanced</b> view, complex queries can be constructed in a visual manner. Each column represents one position in a phrase.</p><p>For instance, the current query will " +
			                    			"search for phrases of 3 words which start with a determiner and end in a noun. Leaving the input field blank, as is done in the second column box, will match any word to that position.</p>",
			                    	position: "bottom"
			                    },
			                    {
			                    	// 9
			                    	element: "#advanced",
			                    	intro: "<p>Complexity can also be added within a column by specifying conditions with OR and AND (<img src='../web/img/plus.png' style='width: 15px; height: 15px;'/>).</p><p>For instance, the current " +
			                    			"query searches for all one term phrases that have the surface form 'hond' or 'kat' and are classified as nouns.</p>",
			                    	position: "bottom"
			                    },
			                    {
			                    	// 10
			                    	element: "#advanced",
			                    	intro: "<p>Furthermore, different operators may be selected to define the manner in which a term may match a word in the corpus.</p><p>For instance, the current query searches for all words starting with 'hond'.</p>",
			                    	position: "bottom"
			                    },
			                    {
			                    	// 11
			                    	element: "#advanced",
			                    	intro: "<p>Similar to extended search, the <b>advanced</b> view allows for queries to be performed in batches. To achieve this, you press the batch button <img src='../web/img/load.png' style='width: 15px; height: 15px; border:1px solid #555;'/>" +
			                    			" next to an input field and select a file (.txt) from your hard drive that contains the terms that you want to search for at that position. After loading the file, the contents are displayed in the input field and can " +
			                    			"be adjusted if needed.</p>" +
			                    			"<p>Please note that, contrary to the batch option in the extended view, here every batch list may contain only a single term per line.</p>",
			                    	position: "bottom"
			                    },
			                    {
			                    	// 12
			                    	element: "#advanced input.splitcheck",
			                    	intro: "<p>When you load a batch list from file, this box is automatically checked. When checked, the queries in the batch are all executed as separate queries with the results presented in separate screens.</p>" +
			                    			"<p>If you wish to execute the batch queries as a single query and have the results accumulated over all queries, uncheck this box.</p>",
			                    	position: "bottom"
			                    }
						        ]
					};
				}
			} else if (page === "expert") {
				if (lang === "nl") {
					return {
						steps: [
			                    {
			                    	// 13
			                    	element: "#expert",
			                    	intro: "<p>Bij <b>expert</b> zoeken kunt u een zoekopdracht invoeren uitgedrukt in de Corpus Query Language (CQL). Een goede tutorial over CQL vindt u <a href='http://cwb.sourceforge.net/files/CQP_Tutorial/' target='_blank'>hier</a>.</p>",
			                    	position: "top"
			                    },
			                    {
			                    	// 14
			                    	element: "#cql_info",
			                    	intro: "<p>Eenvoudige voorbeelden van CQL zoekopdrachten worden weergegeven als u op het groene vraagteken klikt <img src='../web/img/info_icon.png' style='width: 15px; height: 15px;'/>.</p>",
			                    	position: "left"
			                    }
						        ]
					};
				} else {
					return {
						steps: [
			                    {
			                    	// 13
			                    	element: "#expert",
			                    	intro: "<p>The <b>expert</b> view allows for the input of queries in pure Corpus Query Language (CQL). A good tutorial on CQL can be found <a href='http://cwb.sourceforge.net/files/CQP_Tutorial/' target='_blank'>here</a>.</p>",
			                    	position: "top"
			                    },
			                    {
			                    	// 14
			                    	element: "#cql_info",
			                    	intro: "<p>Some examples of basic CQL queries are displayed when you click the green question mark <img src='../web/img/info_icon.png' style='width: 15px; height: 15px;'/>.</p>",
			                    	position: "left"
			                    }
						        ]
					};
				}
			} else if (page === "results") {
				if (step == 1) {
					if (lang === "nl") {
						return {
							steps: [
				                    {
				                    	// 15
				                    	element: "#result_link",
				                    	intro: "<p>Nadat u in een van de interfaces een zoekopdracht heeft ingegeven en op 'Zoek' heeft geklikt, wordt het 'Resultaten' scherm getoond.</p>",
				                    	position: "right"
				                    },
				                    {
				                    	// 16
				                    	element: "#queries",
				                    	intro: "<p>Bovenaan het <b>resultaten</b> scherm vindt u een lijst van alle uitgevoerde zoekopdrachten, inclusief status en simpele statistieken. Klik op een " +
				                    			"regel in de tabel om de resultaten van die zoekopdracht te bekijken. Tevens kunt u zoekopdrachten aanpassen of verwijderen.</p><p>Als u op 'edit' klikt, dan wordt de zoekopdracht " +
				                    			"geladen in hetzelfde scherm als waar deze oorspronkelijk is ingevoerd, plus enige meer geavanceerde schermen. Bijvoorbeeld, een zoekopdracht ingevoerd bij <b>uitgebreid</b> wordt " +
				                    			"in de schermen <b>uitgebreid</b>, <b>geavanceerd</b> en <b>expert</b> geladen.</p>",
				                    	position: "bottom"
				                    },
				                    {
				                    	// 17
				                    	element: "#results",
				                    	intro: "<p>De resultaten zijn onderverdeeld in 4 secties: hits, documenten, gegroepeerde hits, and gegroepeerde documenten.</p>" +
				                    			"<p>De <b>hits</b> sectie toont alle afzonderlijke hits in het corpus. In eerste instantie is er geen sortering toegepast. De sortering kan gewijzigd worden met de driehoeken (▲ en ▼) naast de kolomnamen.</p>",
				                    	position: "top"
				                    },
				                    {
				                    	// 18
				                    	element: "#results",
				                    	intro: "<p>Als u op 'Toggle titels' klikt worden de document titels en ID's weergegeven. Een klik op de document titel opent het document in een apart scherm.</p>",
				                    	position: "top"
				                    },
				                    {
				                    	// 19
				                    	element: "#results",
				                    	intro: "<p>Als u op een specifieke hit klikt wordt de context waarin de hit gevonden is weergegeven.</p>",
				                    	position: "top"
				                    }
							        ]
						};
					} else {
						return {
							steps: [
				                    {
				                    	// 15
				                    	element: "#result_link",
				                    	intro: "<p>After you have entered a query in one of the views and clicked the 'Search' button, the <b>results</b> view is revealed.</p>",
				                    	position: "right"
				                    },
				                    {
				                    	// 16
				                    	element: "#queries",
				                    	intro: "<p>At the top of the <b>results</b> view a list of all executed queries is displayed, along with their status and basic search statistics. Clicking on a " +
				                    			"line in the table displays the results for that query. Here you can also choose to edit or delete a query.</p><p>Clicking 'edit' will load the query into " +
				                    			"the view that it was originally entered in, plus any of the more advanced views. For instance, a query entered into <b>extended</b> will be editable in the " +
				                    			"<b>extended</b>, <b>advanced</b>, and <b>expert</b> views.</p>",
				                    	position: "bottom"
				                    },
				                    {
				                    	// 17
				                    	element: "#results",
				                    	intro: "<p>The results are divided into 4 sections: hits, documents, grouped hits, and grouped documents.</p>" +
				                    			"<p>The <b>hits</b> section shows all distinct matches found in the corpus. Initially, they are sorted per document in no particular order. Sort order can be changed using the triangles (▲ and ▼) next to the table headers.</p>",
				                    	position: "top"
				                    },
				                    {
				                    	// 18
				                    	element: "#results",
				                    	intro: "<p>When you click the 'Toggle titles' button, the document ID and title is revealed. Clicking this line opens the document in a separate view.</p>",
				                    	position: "top"
				                    },
				                    {
				                    	// 19
				                    	element: "#results",
				                    	intro: "<p>When you click on a specific hit, a line is displayed containing the context in which the match was found.</p>",
				                    	position: "top"
				                    }
							        ]
						};
					}
				} else if (step == 2) {
					if (lang === "nl") {
						return {
							steps: [
				                    {
				                    	// 20
				                    	element: "#results",
				                    	intro: "<p>De <b>documenten</b> sectie toont alle documenten waarin hits gevonden zijn. De sortering kan gewijzigd worden met de driehoeken (▲ en ▼) naast de kolomnamen. " +
				                    			"Een klik op 'Toon document' opent het document in een apart scherm.</p>",
				                    	position: "top"
				                    }
							        ]
						};
					} else {
						return {
							steps: [
				                    {
				                    	// 20
				                    	element: "#results",
				                    	intro: "<p>In the <b>documents</b> section, all documents that contain matches are displayed. Sort order can be changed using the triangles (▲ and ▼) next to the table headers. " +
				                    			"Clicking the 'Show document' button opens the document in a separate view.</p>",
				                    	position: "top"
				                    }
							        ]
						};
					}
				} else if (step == 3) {
					if (lang === "nl") {
						return {
							steps: [
				                    {
				                    	// 21
				                    	element: "#results",
				                    	intro: "<p>Zowel in de <b>gegroepeerde hits</b>sectie als de <b>gegroepeerde documenten</b> sectie kunt u kiezen uit meerdere kenmerken om de hits of documenten te groeperen. Hier hebben we bijvoorbeeld gekozen om de hits te groeperen per collectie.</p>" +
				                    			"<p>De resultaten zijn aflopend gesorteerd op hun frequentie. De sortering kan gewijzigd worden met de driehoeken (▲ en ▼) naast de kolomnamen.</p>",
				                    	position: "top"
				                    },
				                    {
				                    	// 22
				                    	element: "#results",
				                    	intro: "<p>Wanneer u op een specifieke groep klikt worden de eerste 20 concordanties in die groep geladen. U kunt er meer laden door te klikken op 'Laad meer concordanties'. In de <b>grouped documenten</b> sectie bevat deze lijst de specifieke documenten in de groep.</p>" +
				                    			"<p>Om een aparte zoekopdracht uit te voeren naar alleen de hits of documenten in deze groep klikt u op 'Toon gedetailleerde concordanties in deze groep'.</p>",
				                    	position: "top"
				                    }
							        ]
						};
					} else {
						return {
							steps: [
				                    {
				                    	// 21
				                    	element: "#results",
				                    	intro: "<p>Both in the <b>grouped hits</b> and <b>grouped docs</b> sections you can select multiple attributes to group the matches or documents by. For instance, here we have chosen to group the hits by collection.</p>" +
				                    			"<p>The results are ordered by their frequency (descending). Sort order can be changed using the triangles (▲ and ▼) next to the table headers.</p>",
				                    	position: "top"
				                    },
				                    {
				                    	// 22
				                    	element: "#results",
				                    	intro: "<p>Clicking on a specific line loads the first 20 concordances in that group. More can be loaded by clicking 'Load more concordances'. In the <b>grouped docs</b> section, this lists the specific documents in the group.</p>" +
				                    			"<p>To perform a separate search that only matches the hits in this group, you click 'Show detailed concordances in this group'.</p>",
				                    	position: "top"
				                    }
							        ]
						};
					}
				}
			} else if (page === "document") {
				if (step == 1) {
					if (lang === "nl") {
						return {
							steps: [
				                    {
				                    	// 23
				                    	element: "#document_link",
				                    	intro: "<p>Als u ervoor kiest om een document te laden vanuit een van de resultaatsschermen wordt het <b>document</b> scherm getoond.</p>" +
				                    			"<p>(Wacht u alstublieft totdat het document geladen is voordat u op 'Next' klikt.)</p>",
				                    	position: "right"
				                    },
				                    {
				                    	// 24
				                    	element: "#doc-display",
				                    	intro: "<p>Het overzicht is onderverdeeld in 4 secties: tekst, metadata, statistieken en word cloud.</p>" +
			                			"<p>De sectie <b>tekst</b> toont de daadwerkelijke inhoud van het document. Wanneer u met de muis over een woord beweegt wordt extra, lingu&iuml;stische informatie van dat woord getoond.</p>",
				                    	position: "top"
				                    }
							        ]
						};
					} else {
						return {
							steps: [
				                    {
				                    	// 23
				                    	element: "#document_link",
				                    	intro: "<p>When you choose to load a document from any of the result sections that provide this option, the <b>document</b> view is revealed.</p>" +
				                    			"<p>(Please wait until the document is loaded before clicking 'Next'.)</p>",
				                    	position: "right"
				                    },
				                    {
				                    	// 24
				                    	element: "#doc-display",
				                    	intro: "<p>The <b>document</b> view is divided into 4 sections: text, metadata, statistics, and word cloud. Only one document can be displayed at a time.</p>" +
				                    			"<p>The <b>text</b> section displays the actual contents of the document. Hovering over a word with the mouse reveals additional linguistic information for that word.</p>",
				                    	position: "top"
				                    }
							        ]
						};
					}
				} else if (step == 2) {
					if (lang === "nl") {
						return {
							steps: [
				                    {
				                    	// 25
				                    	element: "#doc-display",
				                    	intro: "<p>De sectie <b>metadata</b> toont de metadata van het huidige document.</p>",
				                    	position: "top"
				                    }
							        ]
						};
					} else {
						return {
							steps: [
				                    {
				                    	// 25
				                    	element: "#doc-display",
				                    	intro: "<p>The <b>metadata</b> tab shows metadata associated with the document.</p>",
				                    	position: "top"
				                    }
							        ]
						};
					}
				} else if (step == 3) {
					if (lang === "nl") {
						return {
							steps: [
				                    {
				                    	// 26
				                    	element: "#doc-display",
				                    	intro: "<p>De <b>statistieken</b> tonen de vocabulaire groei van het document en informatie omtrent de woorsoorten die in het document voorkomen.</p>",
				                    	position: "top"
				                    }
							        ]
						};
					} else {
						return {
							steps: [
				                    {
				                    	// 26
				                    	element: "#doc-display",
				                    	intro: "<p>The <b>statistics</b> tab shows the document's vocabulary growth, and information on the parts of speech used.</p>",
				                    	position: "top"
				                    }
							        ]
						};
					}
				} else if (step == 4) {
					if (lang === "nl") {
						return {
							steps: [
				                    {
				                    	// 27
				                    	element: "#doc-display",
				                    	intro: "<p>Tenslotte wordt een word cloud gegenereerd van de meest voorkomende termen in het document.</p>",
				                    	position: "top"
				                    },
				                    {
				                    	// 28
				                    	element: "#main-explore",
				                    	intro: "<p>U heeft het einde van deze rondleiding bereikt. Wilt u een rondleiding van de 'Verken' interface, klik dan op 'Verken' en vervolgens op het groene vraagteken <img src='../web/img/info_icon.png' style='width: 15px; height: 15px;'/> links boven.</p>",
				                    	position: "bottom"
				                    }
							        ]
						};
					} else {
						return {
							steps: [
				                    {
				                    	// 27
				                    	element: "#doc-display",
				                    	intro: "<p>Finally, a word cloud of the most frequent words in the document is generated.</p>",
				                    	position: "top"
				                    },
				                    {
				                    	// 28
				                    	element: "#main-explore",
				                    	intro: "<p>You have reached the end of this tour. To take a tour of the 'Explore' interface, please click 'Explore' followed by the green question mark <img src='../web/img/info_icon.png' style='width: 15px; height: 15px;'/> at the top left.</p>",
				                    	position: "bottom"
				                    }
							        ]
						};
					}
				}
			}
		}
	},
	
	start : function(ns,page,step,lang,nextPage) {
		Whitelab.intro = introJs();
		Whitelab.intro.setOptions(Whitelab.tour.getOptions(ns,page,step,lang));
		
		var np = 'Continue';
		if (lang === 'nl' && nextPage != null)
			np = 'Ga verder';
		else if (lang === 'nl' && nextPage == null)
			np = 'Einde tour';
		else if (nextPage == null)
			np = 'End of tour';
		
		Whitelab.intro.setOption('doneLabel', np).onchange(function(target) {
			var direction = this._direction;
			var current = this._currentStep;
			$(".introjs-tooltip").css("max-width","450px");
			$(".introjs-tooltip").css("min-width","450px");
			
			if (ns === "explore") {
				$(".introjs-tooltip").css("min-width","200px");
				$(".introjs-tooltip").css("max-width","350px");
				Whitelab.tour.setScrollTop(page,step,current);
				Whitelab.tour.setFilters(ns,page,step,current);
			} else if (ns === "search") {
				if (page === "document" && current == 1 && step == 1) {
					$(".introjs-tooltip").css("min-width","450px");
					$(".introjs-tooltip").css("max-width","700px");
				}
				Whitelab.tour.setFilters(ns,page,step,current);
			}
		}).oncomplete(function() {
			if (nextPage != null)
				window.location.href = nextPage;
			else if (ns === "explore")
				window.location.href = "/whitelab/explore/corpus";
			else
				window.location.href = "/whitelab/search/simple";
		}).onexit(function() {
			if (ns === "explore")
				window.location.href = "/whitelab/explore/corpus";
			else
				window.location.href = "/whitelab/search/simple";
		});
		
		Whitelab.intro.start();
	},
	
	setFilters : function(ns,page,step,current) {
		Whitelab.debug("Setting filters");
		if (ns === "search") {
			if (page === "simple" && current == 1) {
				$("#simple-input > input").val("de gebeten hond");
			} else if (page === "extended" && current == 0) {
				$("#search-meta").addClass("active");
				$(document).find(".metaLabel").first().val("field:CollectionName").change();
				$(document).find(".metaInput").first().val("Newsletters");
				Whitelab.meta.addRule();
				$(document).find(".metaLabel").last().val("field:Country").change();
				$(document).find(".metaInput").last().val("NL");
				$("#group_by-select").val("field:CollectionName").change();
			} else if (page === "extended") {
				$("#search-meta").removeClass("active");
				if (current == 1) {
					$("#word_text").val("");
					$("#lemma_text").val("zijn");
					$("#pos_text").val("");
				} else if (current == 2) {
					$("#word_text").val("ben");
					$("#lemma_text").val("");
					$("#pos_text").val("WW.*");
			        var batch = $("#extended_word").find("div.batchrow");
			        batch.removeClass("active");
			    	batch.find(".batchlist").html("");
			    	var input = $("#extended_word").find("div.inputrow");
			    	input.addClass("active");
			    	$("#extended .splitcheck").prop("checked",false);
				} else if (current == 3 || current == 4) {
					var text = "de kabouter\nhet kabouterbos";
			        var batch = $("#extended_word").find("div.batchrow");
			        batch.addClass("active");
			    	batch.find(".batchlist").html(text);
			    	var input = $("#extended_word").find("div.inputrow");
			    	input.removeClass("active");
			    	$("#extended .splitcheck").prop("checked",true);
					$("#lemma_text").val("");
					$("#pos_text").val("");
				}
			} else if (page === "advanced" && current == 0) {
				Whitelab.search.reset();
				$("#advanced").find("select.token-type").last().val("pos").change();
				$("#advanced").find("select.advanced-pos-select").last().val("LID.*");
				Whitelab.search.advanced.addColumn();
				Whitelab.search.advanced.addColumn();
				$("#advanced").find("select.token-type").last().val("pos").change();
				$("#advanced").find("select.advanced-pos-select").last().val("N.*");
			} else if (page === "advanced" && current == 1) {
				Whitelab.search.reset();
				$("#advanced").find("div.token-input-field > input").last().val("hond");
				$("#advanced").find("a.add-or").last().click();
				$("#advanced").find("div.token-input-field > input").last().val("kat");
				$("#advanced").find("a.add-and").last().click();
				$("#advanced").find("select.token-type").last().val("pos").change();
				$("#advanced").find("select.advanced-pos-select").last().val("N.*");
			} else if (page === "advanced" && current == 2) {
				Whitelab.search.reset();
				$("#advanced").find("div.token-input-field > input").last().val("hond");
				$("#advanced").find("select.token-operator").last().val("starts");
			} else if (page === "advanced" && current == 3) {
				Whitelab.search.reset();
				var text = "de\nhet\neen";
		        var batch = $("#advanced").find("div.batchrow").last();
		        batch.addClass("active");
		    	batch.find(".batchlist").html(text);
		    	var input = $("#advanced").find("div.inputrow").last();
		    	input.removeClass("active");
		    	$("#advanced .splitcheck").prop("checked",true);
			} else if (page === "expert") {
				$("#querybox").val('[word="(hond|kat)"]');
				if (current == 1)
					$("#cql_info").addClass("active");
				else
					$("#cql_info").removeClass("active");
			}
		} else if (ns === "explore") {
			if ((page === "statistics" || page === "ngrams") && step == 1 && current == 0) {
				$(document).find(".metaLabel").first().val("field:CollectionName").change();
				$(document).find(".metaInput").first().val("Discussion lists");
				Whitelab.meta.addRule();
				$(document).find(".metaLabel").last().val("field:Country").change();
				$(document).find(".metaInput").last().val("B");
			} else if (page === "statistics" && step == 1 && current == 1) {
				$("#stats-groupSelect").val("lemma");
			} else if (page === "ngrams" && step == 1 && current == 1) {
				$("#n1 select.type").val("lemma").change();
				$("#n1 .input").val("de");
				$("#n3 select.type").val("lemma").change();
				$("#n3 .input").val("van");
				$("#n4 select.type").val("lemma").change();
				$("#n4 .input").val("de");
			} else if (page === "document" && step == 1 && current == 0) {
				$("#docpid").val("WR-P-P-G-0000347486");
			}
		}
	},
	
	setScrollTop : function(page,step,current) {
		if (page === "statistics" && ((step == 2 && current == 1) || (step > 2 && current == 0))) {
			var pos = $("#result_statistics").offset().top - 268;
			$("body").animate({
		        scrollTop: pos
		    }, 200);
		} else if (page === "ngrams" && step == 2 && current == 1) {
			var pos = $("#result_ngram").offset().top - 168;
			$("body").animate({
		        scrollTop: pos
		    }, 200);
		} else if (page === "document" && step == 5 && current == 0) {
			$("body").animate({
		        scrollTop: 0
		    }, 200);
		}
	}
};