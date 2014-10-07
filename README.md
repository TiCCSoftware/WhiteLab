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

* The home page within WhiteLab is currently included in the interface as an iframe, so it may be hosted on a different server than WhiteLab itself. This choice was made to enable less technically inclined project managers to maintain the information displayed on the home page without interference of system administrators.

Questions
=========

For technical questions about WhiteLab, please contact: Matje van de Camp (matje@taalmonsters.nl)
For questions about the OpenSoNaR project, please contact: Dr. Martin Reynaert (reynaert@uvt.nl)

