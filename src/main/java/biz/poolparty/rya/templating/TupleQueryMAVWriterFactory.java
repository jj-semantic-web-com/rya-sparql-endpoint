package biz.poolparty.rya.templating;

import java.io.OutputStream;
import java.io.Writer;
import java.util.Map;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.openrdf.query.resultio.TupleQueryResultFormat;
import org.openrdf.query.resultio.TupleQueryResultWriter;
import org.openrdf.query.resultio.TupleQueryResultWriterFactory;
import org.openrdf.rio.RDFFormat;
import org.openrdf.rio.RDFWriter;
import org.openrdf.rio.RDFWriterFactory;
import org.springframework.web.servlet.View;

/**
 *
 * @author turnguard
 */
public class TupleQueryMAVWriterFactory implements MAVWriterFactory, TupleQueryResultWriterFactory {

    @Override
    public TupleQueryResultFormat getTupleQueryResultFormat() {
        return TupleQueryMAVWriter.XHTML;
    }

    @Override
    public TupleQueryResultWriter getWriter(OutputStream out) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    public TupleQueryResultWriter getWriter(View view, Map<String,Object> model, HttpServletRequest req, HttpServletResponse resp){
        return new TupleQueryMAVWriter(view, model, req, resp);
    }
            
}
