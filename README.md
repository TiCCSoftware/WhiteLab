WhiteLab
========

WhiteLab is a tomcat web application for the exploration and search of large corpora. It was developed within the CLARIN OpenSoNaR project (http://www.clarin.nl/node/1404) to enable the online disclosure of the SoNaR reference corpus, which consists of 500 million words of contemporary, written Dutch. WhiteLab provides a range of interfaces, each targeted to a different type of user or a different type of search question. At the back end, access to the corpus is provided by the corpus search engine BlackLab (https://github.com/INL/BlackLab), and its accompanying web service BlackLab Server (https://github.com/INL/BlackLab-server).

Configuration
=============

In order to disclose your own corpus through WhiteLab, some configuration needs to be done. Settings for WhiteLab are stored together with the translations of the interface as a Java ResourceBundle and can be found in the following files:
- src/WhitelabBundle.properties (default, currently Dutch)
- src/WhitelabBundle_nl.properties (Dutch)
- src/WhitelabBundle_en.properties (English)

The settings that need to be set in order for WhiteLab to function are:
- corpus: name of the corpus index created with BlackLab
- title: title of the corpus
- description: description of the corpus (optional)
- documents.total: the total number of documents in the corpus, required to create the corpus tree map
- homeUrl: full URL to the home page*

*The home page within WhiteLab is currently included in the interface as an iframe, so it may be hosted on a different server than WhiteLab itself. This choice was made to enable less technically inclined project managers to maintain the information displayed on the home page without interference of system administrators.

Usage
=====

**2015, July**: A major update has been done to facilitate persistent URLs throughout the application. To this end, the *explore* and *search* functionalities are placed in separate namespaces. The main application pages are now accessible through the following paths:

- Home page: /whitelab/page/home
- Explore page: /whitelab/explore/corpus
- Search page: /whitelab/search/simple

In order to bypass the visual input and directly input a query to view its results, the following path is used:

- /whitelab/search/results,

in combination with the following parameters:

- **query**: Your query formatted in Corpus Query Language.
- **within**: Optional. String value indicating to limit the query to matches within a 'document' (default), 'paragraph' or 'sentence'.
- **from**: Required to enable query editing. Integer value representing the original input screen (1=simple, 2=extended, 3=advanced, 4=expert). Queries can be edited in their original input screen or any screen with a higher identifier. Currently, if this parameter is not supplied, your query will not be added to the query list at the top of the result screen.
- **number**: Optional. Integer value representing the number of results to show per page (default: 50).
- **first**: Optional. Integer value representing the index of the first result to include in the list (default: 0).
- **view**: Optional. Integer value representing the type of results to display (1=hits, 2=documents, 8=grouped hits, 16=grouped documents).
- **group**: Optional. Can only be used in combination with 'view=8' or 'view=16'. The following values can be used as input:
  - hit:word *
  - wordleft:word *
  - wordright:word *
  - hit:lemma *
  - wordleft:lemma *
  - wordright:lemma *
  - hit:pos *
  - wordleft:pos *
  - wordright:pos *
  - field:Age
  - field:AuthorNameOrPseudonym
  - field:CollectionName
  - field:Country
  - field:LicenseCode
  - field:OriginalLanguage
  - field:PublicationDate
  - field:PublicationName
  - field:PublicationPlace
  - field:Published
  - field:Publisher
  - field:Sex
  - field:TextDescription
  - field:TextKeyword
  - field:TextType
  - field:Town
  - field:Translated
  - field:TranslatorName
  - * (only available when view=8)
- metadata: Metadata filters are optional. Each field may be used multiple times and accepts literal values only. Prefix a value with '-' to exclude it. The following fields are defined:
  - **Age**
  - **AuthorNameOrPseudonym**
  - **CollectionName**
  - **Country**
  - **LicenseCode**
  - **OriginalLanguage**
  - **PublicationDate**
  - **PublicationName**
  - **PublicationPlace**
  - **Published**
  - **Publisher**
  - **Sex**
  - **TextDescription**
  - **TextKeyword**
  - **TextType**
  - **Town**
  - **Translated**
  - **TranslatorName**

Questions
=========

For technical questions about WhiteLab, please contact: Matje van de Camp (matje@taalmonsters.nl)
For questions about the OpenSoNaR project, please contact: Dr. Martin Reynaert (reynaert@uvt.nl)

