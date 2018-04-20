package biz.poolparty.rya.templating;

import java.io.OutputStream;
import java.util.Map;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.openrdf.query.resultio.BooleanQueryResultFormat;
import org.openrdf.query.resultio.BooleanQueryResultWriter;
import org.openrdf.query.resultio.BooleanQueryResultWriterFactory;
import org.openrdf.query.resultio.TupleQueryResultFormat;
import org.openrdf.query.resultio.TupleQueryResultWriter;
import org.openrdf.query.resultio.TupleQueryResultWriterFactory;
import org.springframework.web.servlet.View;

/**
 *
 * @author turnguard
 */
public class BooleanQueryMAVWriterFactory implements MAVWriterFactory, BooleanQueryResultWriterFactory {

    @Override
    public BooleanQueryResultFormat getBooleanQueryResultFormat() {
        return BooleanQueryMAVWriter.XHTML;
    }
    
    public BooleanQueryMAVWriter getWriter(View view, Map<String,Object> model, HttpServletRequest req, HttpServletResponse resp){
        return new BooleanQueryMAVWriter(view, model, req, resp);
    }

    @Override
    public BooleanQueryResultWriter getWriter(OutputStream out) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }
            
}
