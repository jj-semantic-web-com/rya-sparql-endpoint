package biz.poolparty.rya.templating;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.openrdf.model.Statement;
import org.openrdf.query.BindingSet;
import org.openrdf.query.QueryResultHandlerException;
import org.openrdf.query.TupleQueryResultHandlerException;
import org.openrdf.query.resultio.BooleanQueryResultFormat;
import org.openrdf.query.resultio.BooleanQueryResultWriter;
import org.openrdf.query.resultio.QueryResultFormat;
import org.openrdf.query.resultio.TupleQueryResultFormat;
import org.openrdf.query.resultio.TupleQueryResultWriter;
import org.openrdf.rio.RDFFormat;
import org.openrdf.rio.RDFHandlerException;
import org.openrdf.rio.RioSetting;
import org.openrdf.rio.WriterConfig;
import org.openrdf.rio.helpers.RDFWriterBase;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.View;

/**
 *
 * @author turnguard
 */
public class BooleanQueryMAVWriter implements BooleanQueryResultWriter {

    public static final BooleanQueryResultFormat XHTML = new BooleanQueryResultFormat(
            "SPARQL/XHTML", 
            "application/xhtml+xml", 
            "xhtml"
    );
    
    private View view;
    private boolean result;
    private HttpServletRequest req;
    private HttpServletResponse resp;
    private Map<String,Object> model;
    
    public BooleanQueryMAVWriter(View view, Map<String,Object> model, HttpServletRequest req, HttpServletResponse resp){
        this.view = view;
        this.model = model;
        this.req = req;
        this.resp = resp;
    }

    @Override
    public BooleanQueryResultFormat getBooleanQueryResultFormat() {
        return XHTML;
    }
    
    @Override
    public QueryResultFormat getQueryResultFormat() {
        return getBooleanQueryResultFormat();
    }
    
    @Override
    public void write(boolean bln) throws IOException {
        this.result = bln;
        try {
            this.endQueryResult();
        } catch (TupleQueryResultHandlerException ex) {
            throw new IOException(ex);
        }
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

    @Override
    public void handleBoolean(boolean bln) throws QueryResultHandlerException {}

    @Override
    public void handleLinks(List<String> list) throws QueryResultHandlerException {}

    @Override
    public void startQueryResult(List<String> list) throws TupleQueryResultHandlerException {}

    @Override
    public void endQueryResult() throws TupleQueryResultHandlerException {
        this.model.put("result", this.result);
        try {
            this.view.render(model, req, resp);
        } catch (Exception ex) {
            throw new TupleQueryResultHandlerException(ex);
        }
    }

    @Override
    public void handleSolution(BindingSet bs) throws TupleQueryResultHandlerException {}
    
}
