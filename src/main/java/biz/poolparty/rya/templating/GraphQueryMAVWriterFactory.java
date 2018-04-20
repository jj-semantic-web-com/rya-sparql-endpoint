package biz.poolparty.rya.templating;

import java.io.OutputStream;
import java.io.Writer;
import java.util.Map;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.openrdf.rio.RDFFormat;
import org.openrdf.rio.RDFWriter;
import org.openrdf.rio.RDFWriterFactory;
import org.springframework.web.servlet.View;

/**
 *
 * @author turnguard
 */
public class GraphQueryMAVWriterFactory implements MAVWriterFactory, RDFWriterFactory {

    @Override
    public RDFFormat getRDFFormat() {        
        return RDFFormat.RDFA;
    }

    @Override
    public RDFWriter getWriter(OutputStream out) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public RDFWriter getWriter(Writer writer) {
        throw new UnsupportedOperationException("Not supported yet.");
    }
    
    public RDFWriter getWriter(View view, Map<String,Object> model, HttpServletRequest req, HttpServletResponse resp){
        return new GraphQueryMAVWriter(view, model, req, resp);
    }
}
