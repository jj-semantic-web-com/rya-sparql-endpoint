package biz.poolparty.rya;

import biz.poolparty.rya.templating.BooleanQueryMAVWriterFactory;
import biz.poolparty.rya.templating.GraphQueryMAVWriterFactory;
import biz.poolparty.rya.templating.MAVWriterFactory;
import biz.poolparty.rya.templating.TupleQueryMAVWriterFactory;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.openrdf.model.impl.URIImpl;
import org.openrdf.query.MalformedQueryException;
import org.openrdf.query.QueryEvaluationException;
import org.openrdf.query.QueryLanguage;
import org.openrdf.query.UpdateExecutionException;
import org.openrdf.query.parser.ParsedBooleanQuery;
import org.openrdf.query.parser.ParsedGraphQuery;
import org.openrdf.query.parser.ParsedOperation;
import org.openrdf.query.parser.ParsedTupleQuery;
import org.openrdf.query.parser.ParsedUpdate;
import org.openrdf.query.parser.QueryParserUtil;
import org.openrdf.query.resultio.BooleanQueryResultWriterFactory;
import org.openrdf.query.resultio.BooleanQueryResultWriterRegistry;
import org.openrdf.query.resultio.TupleQueryResultWriterFactory;
import org.openrdf.query.resultio.TupleQueryResultWriterRegistry;
import org.openrdf.repository.RepositoryConnection;
import org.openrdf.repository.RepositoryException;
import org.openrdf.repository.sail.SailRepository;
import org.openrdf.rio.RDFFormat;
import org.openrdf.rio.RDFHandlerException;
import org.openrdf.rio.RDFParseException;
import org.openrdf.rio.RDFWriterFactory;
import org.openrdf.rio.RDFWriterRegistry;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.View;
import org.springframework.web.servlet.ViewResolver;

/**
 *
 * @author http://www.turnguard.com/turnguard
 */
@Controller()
@RequestMapping("")
public class SPARQLController {
   
    public static enum QUERY_TYPE {
        TUPLE,
        GRAPH,
        BOOLEAN,
        UPDATE
    }
    
    @Autowired
    @Qualifier("viewResolver")
    private ViewResolver viewResolver;
    
    @Resource(name="repository")
    SailRepository repository = null;
    
    @Resource(name="sdBaseURL")
    URL sdBaseURL;
    
    /**
     * 
     * @throws MalformedURLException
     * @throws IOException 
     */
    public SPARQLController() throws MalformedURLException, IOException {
        RDFWriterRegistry.getInstance().add(new GraphQueryMAVWriterFactory());  
        TupleQueryResultWriterRegistry.getInstance().add(new TupleQueryMAVWriterFactory());        
        BooleanQueryResultWriterRegistry.getInstance().add(new BooleanQueryMAVWriterFactory());
    }
    
    /**
     * 
     * @throws RepositoryException
     * @throws IOException
     * @throws RDFParseException 
     */
    @PostConstruct
    public void postConstruct() throws RepositoryException, IOException, RDFParseException{
        RepositoryConnection repCon = null;
        try {
            repCon = repository.getConnection();
            repCon.begin();
            repCon.add(
                SPARQLController.class.getResourceAsStream("/sparql-service-description.ttl"), 
                sdBaseURL.toString(),
                RDFFormat.TURTLE,
                new URIImpl("urn:sd")
            );
            repCon.commit();
        } finally {
            if(repCon!=null){
                repCon.close();
            }
        }
    }

    /**
     * 
     * @param request
     * @param response
     * @param query
     * @param mimetype 
     */
    @RequestMapping(
            value = "/sparql", 
            method = {RequestMethod.GET, RequestMethod.POST},
            params = {
                CONSTANTS.PARAM_QUERYSTRING
            } 
    )
    public void endpoint(
        final HttpServletRequest request,
        final HttpServletResponse response,
        @RequestParam(value = CONSTANTS.PARAM_QUERYSTRING) final String query, 
        @RequestParam(value = CONSTANTS.PARAM_MIMETYPE, required = false) String mimetype
    ){
        
        String accept = (mimetype!=null && !mimetype.isEmpty()) ? mimetype:request.getHeader(CONSTANTS.HEADER_ACCEPT);
        ParsedOperation parsedOperation;
        RepositoryConnection repCon = null;
        
        try {
            repCon = this.repository.getConnection();
            parsedOperation = QueryParserUtil.parseOperation(QueryLanguage.SPARQL, query, "<urn:base:>");
            
            if(parsedOperation instanceof ParsedGraphQuery){
                this.handleGraphQuery(
                    this.getRDFWriterFactory(accept), 
                    (ParsedGraphQuery)parsedOperation, 
                    repCon, 
                    request,response
                );
            } else
            if(parsedOperation instanceof ParsedTupleQuery){
                this.handleTupleQuery(
                    this.getTupleQueryResultWriterFactory(accept), 
                    (ParsedTupleQuery)parsedOperation, 
                    repCon, 
                    request, response
                );
            } else
            if(parsedOperation instanceof ParsedBooleanQuery){
                this.handleBooleanQuery(
                    this.getBooleanQueryResultWriterFactory(accept), 
                    (ParsedBooleanQuery)parsedOperation, 
                    repCon, 
                    request, response
                );
            } else
            if(parsedOperation instanceof ParsedUpdate){
                repCon.begin();
                repCon.prepareUpdate(QueryLanguage.SPARQL, query).execute();                
                repCon.commit();
                response.setStatus(HttpServletResponse.SC_ACCEPTED);
                String messageQuery = ""
                        + "PREFIX http:<http://www.w3.org/2011/http#> "
                        + "PREFIX cnt:<http://www.w3.org/2011/content#> "
                        + "CONSTRUCT { "
                        + " <"+request.getRequestURL().append("?").append(request.getQueryString())+"> http:sc ["
                        + "     http:statusCodeNumber '"+HttpServletResponse.SC_ACCEPTED+"'^^xsd:int"
                        + " ];"
                        + " http:body ["
                        + "     cnt:chars 'Accepted'"
                        + " ] "
                        + "} WHERE { "
                        + " VALUES ?x { 'x' } "
                        + "}";   
                this.sendRDFHTTPMessage(request, response, repCon, query, accept, messageQuery);                
            }
        } catch (MalformedQueryException ex) {
            try {
                
                String messageQuery = ""
                        + "PREFIX http:<http://www.w3.org/2011/http#> "
                        + "PREFIX cnt:<http://www.w3.org/2011/content#> "
                        + "CONSTRUCT { "
                        + " <"+request.getRequestURL().append("?").append(request.getQueryString())+"> http:sc ["
                        + "     http:statusCodeNumber '"+HttpServletResponse.SC_BAD_REQUEST+"'^^xsd:int"
                        + " ];"
                        + " http:body ["
                        + "     cnt:chars '"+ex.getMessage()+"'"
                        + " ] "
                        + "} WHERE { "
                        + " VALUES ?x { 'x' } "
                        + "}";   
                response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                this.sendRDFHTTPMessage(request, response, repCon, query, accept, messageQuery);
            } catch (Exception ex1) {
                Logger.getLogger(SPARQLController.class.getName()).log(Level.SEVERE, null, ex1);
            }
        } catch (RepositoryException ex) {
            
        } catch (Exception ex) {
            
        } finally {
            if(repCon!=null){
                try {
                    repCon.close();
                } catch (RepositoryException ex) {
                    
                }
            }
        }

    }
    
    /**
     * 
     * @param rdfWriterFactory
     * @param parsedQuery
     * @param repCon
     * @param request
     * @param response
     * @throws Exception 
     */
    private void handleGraphQuery(
            RDFWriterFactory rdfWriterFactory, 
            ParsedGraphQuery parsedQuery,
            RepositoryConnection repCon,
            HttpServletRequest request,
            HttpServletResponse response
    ) throws Exception{
        
        response.setContentType(rdfWriterFactory.getRDFFormat().getDefaultMIMEType());
        
        if(rdfWriterFactory instanceof MAVWriterFactory){
            this.handleGraphQueryMAV((GraphQueryMAVWriterFactory)rdfWriterFactory, parsedQuery, repCon, request, response);
        } else {
            repCon
                .prepareGraphQuery(
                    QueryLanguage.SPARQL, 
                    parsedQuery.getSourceString()
                ).evaluate(
                    rdfWriterFactory.getWriter(response.getWriter())
                );
        }
    }
    
    /**
     * 
     * @param tupleQueryWriterFactory
     * @param parsedQuery
     * @param repCon
     * @param request
     * @param response
     * @throws Exception 
     */
    private void handleTupleQuery(
            TupleQueryResultWriterFactory tupleQueryWriterFactory, 
            ParsedTupleQuery parsedQuery,
            RepositoryConnection repCon,
            HttpServletRequest request,
            HttpServletResponse response
    ) throws Exception{
        
        response.setContentType(tupleQueryWriterFactory.getTupleQueryResultFormat().getDefaultMIMEType());
        
        if(tupleQueryWriterFactory instanceof MAVWriterFactory){
            this.handleTupleQueryMAV((TupleQueryMAVWriterFactory)tupleQueryWriterFactory, parsedQuery, repCon, request, response);
        } else {
            repCon
                .prepareTupleQuery(
                    QueryLanguage.SPARQL, 
                    parsedQuery.getSourceString()
                ).evaluate(                        
                    tupleQueryWriterFactory.getWriter(response.getOutputStream())
                );            
        }
    }
    
    /**
     * 
     * @param booleanQueryWriterFactory
     * @param parsedQuery
     * @param repCon
     * @param request
     * @param response
     * @throws Exception 
     */
    private void handleBooleanQuery(
            BooleanQueryResultWriterFactory booleanQueryWriterFactory, 
            ParsedBooleanQuery parsedQuery,
            RepositoryConnection repCon,
            HttpServletRequest request,
            HttpServletResponse response
    ) throws Exception{
        
        response.setContentType(booleanQueryWriterFactory.getBooleanQueryResultFormat().getDefaultMIMEType());

        if(booleanQueryWriterFactory instanceof MAVWriterFactory){
            this.handleBooleanQueryMAV((BooleanQueryMAVWriterFactory)booleanQueryWriterFactory, parsedQuery, repCon, request, response);
        } else {
            booleanQueryWriterFactory
                .getWriter(response.getOutputStream())
                .write(
                    repCon
                        .prepareBooleanQuery(
                                QueryLanguage.SPARQL, 
                                parsedQuery.getSourceString()
                        ).evaluate()
                );
        }
    }
    
    /**
     * 
     * @param rdfWriterFactory
     * @param parsedQuery
     * @param repCon
     * @param request
     * @param response
     * @throws Exception 
     */
    private void handleGraphQueryMAV(
            GraphQueryMAVWriterFactory rdfWriterFactory, 
            ParsedGraphQuery parsedQuery,
            RepositoryConnection repCon,
            HttpServletRequest request,
            HttpServletResponse response
    ) throws Exception{
        View view = this.getView(request);
        Map<String,Object> model = new HashMap<>();
                           model.put(CONSTANTS.ATTRIBUTE_QUERYSTRING, parsedQuery.getSourceString());
                           model.put(CONSTANTS.ATTRIBUTE_MIMETYPE, rdfWriterFactory.getRDFFormat().getDefaultMIMEType());
                           model.put(CONSTANTS.ATTRIBUTE_FORMAT, rdfWriterFactory.getRDFFormat().getName());
                           model.put(CONSTANTS.ATTRIBUTE_QUERYTYPE, QUERY_TYPE.GRAPH);        
        repCon
            .prepareGraphQuery(
                QueryLanguage.SPARQL, 
                parsedQuery.getSourceString()
            ).evaluate(
                rdfWriterFactory
                    .getWriter(
                        view, 
                        model, 
                        request, 
                        response
                    )
            );                           
    }
    
    /**
     * 
     * @param tupleQueryWriterFactory
     * @param parsedQuery
     * @param repCon
     * @param request
     * @param response
     * @throws Exception 
     */
    private void handleTupleQueryMAV(
            TupleQueryMAVWriterFactory tupleQueryWriterFactory, 
            ParsedTupleQuery parsedQuery,
            RepositoryConnection repCon,
            HttpServletRequest request,
            HttpServletResponse response
    ) throws Exception{
        View view = this.getView(request);
        Map<String,Object> model = new HashMap<>();
                           model.put(CONSTANTS.ATTRIBUTE_QUERYSTRING, parsedQuery.getSourceString());
                           model.put(CONSTANTS.ATTRIBUTE_MIMETYPE, tupleQueryWriterFactory.getTupleQueryResultFormat().getDefaultMIMEType());
                           model.put(CONSTANTS.ATTRIBUTE_FORMAT, tupleQueryWriterFactory.getTupleQueryResultFormat().getName());
                           model.put(CONSTANTS.ATTRIBUTE_QUERYTYPE, QUERY_TYPE.TUPLE);        
        repCon
            .prepareTupleQuery(
                QueryLanguage.SPARQL, 
                parsedQuery.getSourceString()
            ).evaluate(
                tupleQueryWriterFactory
                    .getWriter(
                        view, 
                        model, 
                        request, 
                        response
                    )
            );                           
    }
    
    /**
     * 
     * @param booleanQueryWriterFactory
     * @param parsedQuery
     * @param repCon
     * @param request
     * @param response
     * @throws Exception 
     */
    private void handleBooleanQueryMAV(
            BooleanQueryMAVWriterFactory booleanQueryWriterFactory, 
            ParsedBooleanQuery parsedQuery,
            RepositoryConnection repCon,
            HttpServletRequest request,
            HttpServletResponse response
    ) throws Exception{
        View view = this.getView(request);
        Map<String,Object> model = new HashMap<>();
                           model.put(CONSTANTS.ATTRIBUTE_QUERYSTRING, parsedQuery.getSourceString());
                           model.put(CONSTANTS.ATTRIBUTE_MIMETYPE, booleanQueryWriterFactory.getBooleanQueryResultFormat().getDefaultMIMEType());
                           model.put(CONSTANTS.ATTRIBUTE_FORMAT, booleanQueryWriterFactory.getBooleanQueryResultFormat().getName());
                           model.put(CONSTANTS.ATTRIBUTE_QUERYTYPE, QUERY_TYPE.BOOLEAN);   
        booleanQueryWriterFactory
            .getWriter(
                view, 
                model, 
                request, 
                response
            ).write(
                repCon
                    .prepareBooleanQuery(
                            QueryLanguage.SPARQL, 
                            parsedQuery.getSourceString()
                    ).evaluate()                                
            );
    }    
    
    /**
     * 
     * @param request
     * @return
     * @throws Exception 
     */
    private View getView(HttpServletRequest request) throws Exception{
        return viewResolver.resolveViewName(CONSTANTS.DEFAULT_SPARQL_ENDPOINT_TEMPLATE, request.getLocale());
    }
    
    /**
     * Sends messages to the client as RDF Triples.
     * @param request
     * @param response
     * @param repCon
     * @param query
     * @param accept
     * @param messageQuery
     * @throws Exception 
     */
    private void sendRDFHTTPMessage(
            HttpServletRequest request, 
            HttpServletResponse response, 
            RepositoryConnection repCon, 
            String query, 
            String accept, 
            String messageQuery
    ) throws Exception {
        RDFWriterFactory rdfWriterFactory = this.getRDFWriterFactory(accept);        
        View view = this.getView(request);
        Map<String,Object> model = new HashMap<>();
                           model.put(CONSTANTS.ATTRIBUTE_QUERYSTRING, query);
                           model.put(CONSTANTS.ATTRIBUTE_MIMETYPE, rdfWriterFactory.getRDFFormat().getDefaultMIMEType());
                           model.put(CONSTANTS.ATTRIBUTE_FORMAT, rdfWriterFactory.getRDFFormat().getName());
                           model.put(CONSTANTS.ATTRIBUTE_QUERYTYPE, QUERY_TYPE.GRAPH);
        repCon.prepareGraphQuery(QueryLanguage.SPARQL,messageQuery).evaluate(
                                    (rdfWriterFactory instanceof MAVWriterFactory)?
                                            ((GraphQueryMAVWriterFactory)rdfWriterFactory).getWriter(view, model, request, response):
                                            rdfWriterFactory.getWriter(response.getWriter())
                            );
    }
    
    /**
     * Default SPARQL Endpoint without any parameters, displaying the endpoint's capabilities
     * using SPARQL Service Description Vocabulary 
     * (see: https://www.w3.org/TR/sparql11-service-description/)
     * @param request
     * @param response
     * @throws IOException
     * @throws RepositoryException
     * @throws QueryEvaluationException
     * @throws RDFHandlerException
     * @throws MalformedQueryException
     * @throws UpdateExecutionException
     * @throws Exception 
     */
    @RequestMapping(value="/sparql", method=RequestMethod.GET)
    public void index(            
        final HttpServletRequest request,
        final HttpServletResponse response
    ) throws IOException, RepositoryException, QueryEvaluationException, RDFHandlerException, MalformedQueryException, UpdateExecutionException, Exception {
        this.endpoint(request, response, "CONSTRUCT { ?s ?p ?o } WHERE { { SELECT * WHERE { GRAPH <urn:sd> { ?s ?p ?o } } } } ", request.getParameter("mimetype"));
    }
    
    
    /**
     * Get an appropriate TupleQueryResultWriterFactory for the given acceptHeader
     * @param acceptHeader
     * @return TupleQueryResultWriterFactory
     */
    private TupleQueryResultWriterFactory getTupleQueryResultWriterFactory(String acceptHeader){
        return TupleQueryResultWriterRegistry.getInstance().getAll().stream().filter(tqrw->{            
            return tqrw.getTupleQueryResultFormat().getMIMETypes().stream().anyMatch(mimeType->{
                /**
                 * hack to prevent prefer application/sparql-results+xml over rdfa writer
                 */
                if(mimeType.equals("application/xml") && acceptHeader.contains("application/xhtml+xml")){
                    return false;
                }                
                return acceptHeader.contains(mimeType);
            });
        })
        .findFirst()
        .orElseGet(()->{ return TupleQueryResultWriterRegistry.getInstance().get(CONSTANTS.DEFAULT_TUPLE_QUERY_RESULT_FORMAT); });
    }
    
    /**
     * Get an appropriate RDFWriterFactory for the given acceptHeader
     * @param acceptHeader
     * @return RDFWriterFactory
     */
    private RDFWriterFactory getRDFWriterFactory(String acceptHeader){        
        return RDFWriterRegistry.getInstance().getAll().stream().filter(rdfwf->{                 
            return rdfwf.getRDFFormat().getMIMETypes().stream().anyMatch(mimeType->{
                /**
                 * hack to prevent prefer application/rdf+xml over rdfa writer
                 */
                if(mimeType.equals("application/xml") && acceptHeader.contains("application/xhtml+xml")){
                    return false;
                }
                return acceptHeader.contains(mimeType);
            });
        })
        .findFirst()
        .orElseGet(()->{ return RDFWriterRegistry.getInstance().get(CONSTANTS.DEFAULT_RDF_FORMAT); });    
    }
    
    /**
     * Get an appropriate BooleanQueryResultWriterFactory for the given acceptHeader
     * @param acceptHeader
     * @return BooleanQueryResultWriterFactory
     */
    private BooleanQueryResultWriterFactory getBooleanQueryResultWriterFactory(String acceptHeader){
        return BooleanQueryResultWriterRegistry.getInstance().getAll().stream().filter(rdfwf->{                 
            return rdfwf.getBooleanQueryResultFormat().getMIMETypes().stream().anyMatch(mimeType->{
                return acceptHeader.contains(mimeType);
            });
        })
        .findFirst()
        .orElseGet(()->{ return BooleanQueryResultWriterRegistry.getInstance().get(CONSTANTS.DEFAULT_BOOLEAN_QUERY_RESULT_FORMAT); });        
    }    
}
