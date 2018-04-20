package biz.poolparty.rya.templating;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.openrdf.model.Statement;
import org.openrdf.rio.RDFFormat;
import org.openrdf.rio.RDFHandlerException;
import org.openrdf.rio.helpers.RDFWriterBase;
import org.springframework.web.servlet.View;

/**
 *
 * @author turnguard
 */
public class GraphQueryMAVWriter extends RDFWriterBase {

    private View view;
    private List<Statement> statements = new ArrayList<>();
    private HttpServletRequest req;
    private HttpServletResponse resp;
    private Map<String,Object> model;
    public GraphQueryMAVWriter(View view, Map<String,Object> model, HttpServletRequest req, HttpServletResponse resp){
        this.view = view;        
        this.model = model;
        this.req = req;
        this.resp = resp;
    }
    
    
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
        this.model.put("statements", this.statements);
        try {
            this.view.render(this.model, req, resp);
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
    
}
