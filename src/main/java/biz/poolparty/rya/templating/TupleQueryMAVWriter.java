package biz.poolparty.rya.templating;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.openrdf.query.BindingSet;
import org.openrdf.query.QueryResultHandlerException;
import org.openrdf.query.TupleQueryResultHandlerException;
import org.openrdf.query.resultio.QueryResultFormat;
import org.openrdf.query.resultio.TupleQueryResultFormat;
import org.openrdf.query.resultio.TupleQueryResultWriter;
import org.openrdf.rio.RioSetting;
import org.openrdf.rio.WriterConfig;
import org.springframework.web.servlet.View;

/**
 *
 * @author turnguard
 */
public class TupleQueryMAVWriter implements TupleQueryResultWriter {

    public static final TupleQueryResultFormat XHTML = new TupleQueryResultFormat(
            "SPARQL/XHTML", 
            "application/xhtml+xml", 
            "xhtml"
    );
    
    private View view;
    private List<BindingSet> bindings = new ArrayList<>();
    private HttpServletRequest req;
    private HttpServletResponse resp;
    private Map<String,Object> model;
    public TupleQueryMAVWriter(View view, Map<String,Object> model, HttpServletRequest req, HttpServletResponse resp){
        this.view = view;
        this.model = model;
        this.req = req;
        this.resp = resp;
    }

    @Override
    public TupleQueryResultFormat getTupleQueryResultFormat() {
        return XHTML;
    }
    
    @Override
    public QueryResultFormat getQueryResultFormat() {
        return getTupleQueryResultFormat();
    }

    
    @Override
    public void handleBoolean(boolean bln) throws QueryResultHandlerException {}

    @Override
    public void handleLinks(List<String> list) throws QueryResultHandlerException {}

    @Override
    public void startQueryResult(List<String> list) throws TupleQueryResultHandlerException {
        this.model.put("bindingNames", list);
    }

    @Override
    public void endQueryResult() throws TupleQueryResultHandlerException {
        this.model.put("bindings", this.bindings);
        try {
            this.view.render(model, req, resp);
        } catch (Exception ex) {
            throw new TupleQueryResultHandlerException(ex);
        }        
    }

    @Override
    public void handleSolution(BindingSet bs) throws TupleQueryResultHandlerException {           
        this.bindings.add(bs);
    }

    @Override
    public void handleNamespace(String string, String string1) throws QueryResultHandlerException {}

    @Override
    public void startDocument() throws QueryResultHandlerException {}

    @Override
    public void handleStylesheet(String string) throws QueryResultHandlerException {}

    @Override
    public void startHeader() throws QueryResultHandlerException {}

    @Override
    public void endHeader() throws QueryResultHandlerException {}

    @Override
    public void setWriterConfig(WriterConfig wc) {}

    @Override
    public WriterConfig getWriterConfig() { return null; }

    @Override
    public Collection<RioSetting<?>> getSupportedSettings() { return null; }
    
    /**
    @Override
    public RDFFormat getRDFFormat() {
        return RDFFormat.RDFA;
    }

    @Override
    public void handleNamespace(String prefix, String uri) throws RDFHandlerException {
        
    }
    @Override
    public void startRDF() throws RDFHandlerException {
    }

    @Override
    public void endRDF() throws RDFHandlerException {
        
        Map<String,Object> model = new HashMap<>();
        model.put("statements", this.statements);
        try {
            this.view.render(model, req, resp);
        } catch (Exception ex) {
            throw new RDFHandlerException(ex);
        }
    }

    @Override
    public void handleStatement(Statement stmnt) throws RDFHandlerException {
        this.statements.add(stmnt);
    }

    @Override
    public void handleComment(String string) throws RDFHandlerException {
    }
    **/
}
