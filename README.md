# The proposed upgrade for the Apache RYA SPARQL Endpoint
* Building and Running 
  * The following commands create a sample webapp application (with an empty sesame SailRepository),
  download a fresh tomcat, deploy above webapp to tomcat and run the complete demo service.
  ```bash
  git clone https://github.com/jj-semantic-web-com/rya-sparql-endpoint.git
  mvn clean package -Pdistribution
  ./src/main/scripts/run-packaged-server.sh
  ```
* Testing 
  * curl
    * src/main/scripts directory contains a file queries.txt containing a couple of curl scripts to test
      content-negotiation and basic interaction with the SPARQL Endpoint
    * INSERT DATA

      note that besides HTTP 202 Accepted header this interaction will also provide an answer using w3c's HTTP vocabulary
      (https://www.w3.org/TR/HTTP-in-RDF10) in text/turtle
      ```bash
      curl -H "accept:text/turtle" http://localhost:8080/rya/sparql \
      --data-urlencode "query=INSERT DATA { <urn:a> <urn:b> <urn:c> }"
      ```
    * CONSTRUCT

      here's a sample of a CONSTRUCT query accepting text/turtle.
      ```bash
      curl -H "accept:text/turtle" http://localhost:8080/rya/sparql \
      --data-urlencode "query=CONSTRUCT { ?s ?p ?o } WHERE { ?s ?p ?o }"
      ```
      note that it's possible to override (mainly for use in a browser) the accept header by using the "mimetype" URL parameter.
      ```bash
      curl -H "accept:text/turtle" http://localhost:8080/rya/sparql \
      --data-urlencode "query=CONSTRUCT { ?s ?p ?o } WHERE { ?s ?p ?o }"
      ````
      * Available mimetypes
        * text/plain => N-Triples (https://www.w3.org/TR/n-triples/)
        * text/turtle => Turtle (https://www.w3.org/TR/turtle/)
        * application/rdf+xml => RDF/XML (https://www.w3.org/TR/rdf-syntax-grammar/)
        * text/n3 => N3 (https://www.w3.org/TeamSubmission/n3/)
        * text/x-nquads = NQuads (https://www.w3.org/TR/n-quads/)
        * application/xhtml+xml => RDFA (https://www.w3.org/TR/rdfa-primer/) (see below: "Web Browser")
        
    * SELECT
    
      here's an example of issuing a SELECT query
      ```bash
      curl -H "accept:application/sparql-results+json" http://localhost:8080/rya/sparql \
      --data-urlencode "query=SELECT * WHERE { ?s ?p ?o }"
      ```
      
      the same query accepting CSV (Comma Separated Values) as specified by the overriding mimetype URL parameter
      ```bash
      curl -H "accept:application/sparql-results+json" http://localhost:8080/rya/sparql \
      --data-urlencode "query=SELECT * WHERE { ?s ?p ?o }" --data-urlencode "mimetype=text/csv"
      ```
      * Availabel mimetypes
        * application/sparql-results+xml => SPARQL/XML (https://www.w3.org/TR/rdf-sparql-XMLres/)
        * application/sparql-results+json => SPARQL/JSON (https://www.w3.org/TR/sparql11-results-json/)
        * text/tab-separated-values => SPARQL/TSV 
        * text/csv => SPARQL/CSV
        * application/xhtml+xml => SPARQL/XHTML (see below: "Web Browser")

    * ASK
    
      here's an example of issuing an ASK query
      ```bash
      curl -H "accept:application/sparql-results+json" http://localhost:8080/rya/sparql \
      --data-urlencode "query=ASK { ?s ?p ?o }"
      ```
      * Availabel mimetypes
        * application/sparql-results+xml => SPARQL/XML (https://www.w3.org/TR/rdf-sparql-XMLres/)
        * application/sparql-results+json => SPARQL/JSON (https://www.w3.org/TR/sparql11-results-json/)
        * text/boolean => TEXT/PLAIN
        * application/xhtml+xml => SPARQL/XHTML (see below: "Web Browser")
        
* Content-Negotiation
