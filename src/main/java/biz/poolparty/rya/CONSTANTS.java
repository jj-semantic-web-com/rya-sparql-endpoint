package biz.poolparty.rya;

import org.openrdf.query.resultio.BooleanQueryResultFormat;
import org.openrdf.query.resultio.TupleQueryResultFormat;
import org.openrdf.rio.RDFFormat;

/**
 *
 * @author turnguard
 */
public class CONSTANTS {
    public static final TupleQueryResultFormat DEFAULT_TUPLE_QUERY_RESULT_FORMAT = TupleQueryResultFormat.SPARQL;
    public static final RDFFormat DEFAULT_RDF_FORMAT = RDFFormat.TURTLE;
    public static final BooleanQueryResultFormat DEFAULT_BOOLEAN_QUERY_RESULT_FORMAT = BooleanQueryResultFormat.TEXT;
    
    public static final String DEFAULT_SPARQL_ENDPOINT_TEMPLATE = "sparql/endpoint";
    
    public static final String PARAM_QUERYSTRING = "query";
    public static final String PARAM_MIMETYPE = "mimetype";
    
    public static final String ATTRIBUTE_QUERYSTRING = PARAM_QUERYSTRING;
    public static final String ATTRIBUTE_MIMETYPE = PARAM_MIMETYPE;
    public static final String ATTRIBUTE_FORMAT = "format";
    public static final String ATTRIBUTE_QUERYTYPE = "querytype";
    
    public static final String HEADER_ACCEPT = "accept";
    
}
