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
      (https://www.w3.org/TR/HTTP-in-RDF10) in text/turtle (see below under UPDATE for more infos)
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
        
    * UPDATE (INSERT, DELETE, DROP,..)

      Update queries of whatever form will respond with HTTP 202 Accepted in case there's no error (e.g. MalformedQuery)
      Additionally a response will be given using W3C's HTTP Vocabulary using a mimetype depending on the accept header
      or the overriding mimetype URL parameter, e.g.
      ```bash
      curl -v -H "accept:application/rdf+xml" http://localhost:8080/rya/sparql \
      --data-urlencode "query=INSERT DATA { <urn:a> <urn:b> <urn:c> }"
      ```
      
      Above curl command will respond with "HTTP/1.1 202 Accepted" and an additional response body like the following
      ```bash
      <?xml version="1.0" encoding="UTF-8"?>
      <rdf:RDF
	       xmlns:http="http://www.w3.org/2011/http#"
	       xmlns:cnt="http://www.w3.org/2011/content#"
	       xmlns:rdf="http://www.w3.org/1999/02/22-rdf-syntax-ns#"
	       xmlns:xsd="http://www.w3.org/2001/XMLSchema#">
      <rdf:Description rdf:nodeID="node1cbif3cuax5">
	      <http:statusCodeNumber rdf:datatype="http://www.w3.org/2001/XMLSchema#int">202</http:statusCodeNumber>
      </rdf:Description>
      <rdf:Description rdf:about="http://localhost:8080/rya/sparql">
	      <http:sc rdf:nodeID="node1cbif3cuax5"/>
       <http:body rdf:nodeID="node1cbif3cuax6"/>
      </rdf:Description>
      <rdf:Description rdf:nodeID="node1cbif3cuax6">
	      <cnt:chars>Accepted</cnt:chars>
      </rdf:Description>
      </rdf:RDF>      
      ```
  * Web Browser
  
    The proposed SPARQL Endpoint offers the possibility to easily customize the SPARQL Endpoint and SPARQL Results within a browser. When issuing requests containing application/xhtml+xml or text/html as the accept header (the default in a modern web browser) the SPARQL Endpoint will use a MAVWriter (ModelAndView writer implementation of sesame's infrastructure). The corresponding writer (TupleQueryMAVWriter, GraphQueryMAVWriter and BooleanQueryMAVWriter) will use JSP templates that are easily customizable via styles or re-coding of JSP. By default those writers expose valid XHTML documents, in the case of GraphQueryMAVWriter for graph queries (CONSTRUCT, DESCRIBE) a valid xhtml+rdfa document will be generated.
    
    * Accessing the SPARQL Endpoint
    
      By default the endpoint is available at /sparql (a well known SPARQL Endpoint path). Point your browser to
      http://localhost:8080/rya/sparql to get the default view. Note that when no query is specified the SPARQL Endpoint 
      will expose a SPARQL Service Description about it's capabilities along the lines from the corresponding W3C 
      recommendation (https://www.w3.org/TR/sparql11-service-description/). The default description is available as a 
      turtle document (src/main/resources/sparql-service-description.ttl). When accessing the endpoint without a query
      from within a browser the RDFA (xhtml+rdfa) format will be used since browsers send application/xhtml+xml or 
      text/html accept headers by default. To get the SPARQL Endpoint's service description for example as N-Triples
      call the endpoint using the following URL: http://localhost:8080/rya/sparql?mimetype=text/plain (all available
      mimetypes from above CONSTRUCT section will work). The same principle applies when calling the endpoint via curl, e.g.
      ```bash
      curl -H "accept:application/rdf+xml" http://localhost/rya/sparql
      ```

    * Error reporting
    
    By default SPARQL Query related errors will be reported using the same technique as with UPDATE queries. 
    A HTTP 400 Bad Request will be used as a response status in case of malformed queries. Additionally a HTTP Response
    using W3C's HTTP Vocabulary (https://www.w3.org/TR/HTTP-in-RDF10) will be returned in a format depending on the accept
    header or the overriding mimetype URL parameter, e.g.:
    ```bash
    curl -H "accept:text/turtle" http://localhost:8080/rya/sparql --data-urlencode "query=XX"
    ```
    will report HTTP 400 Bad Request and a turtle message looking about like the following
    ```bash
    @prefix http: <http://www.w3.org/2011/http#> .
    @prefix cnt: <http://www.w3.org/2011/content#> .
    _:node1cbif3cuax11 http:statusCodeNumber "400"^^xsd:int .
    <http://localhost:8080/rya/sparql?null> http:sc _:node1cbif3cuax11 .
    _:node1cbif3cuax12 cnt:chars "Lexical error at line 1, column 3.  Encountered: <EOF> after : \"XX\"" .
    <http://localhost:8080/rya/sparql?null> http:body _:node1cbif3cuax12 .
    ```
