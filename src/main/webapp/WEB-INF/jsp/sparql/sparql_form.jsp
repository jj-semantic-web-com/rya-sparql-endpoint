<%@page import="org.openrdf.query.resultio.BooleanQueryResultWriterRegistry"%>
<%@page import="org.openrdf.query.resultio.BooleanQueryResultWriterFactory"%>
<%@page import="org.openrdf.rio.RDFWriterRegistry"%>
<%@page import="org.openrdf.query.resultio.TupleQueryResultWriterRegistry"%>
<%@taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>

<div class="sparql_container">
    <form action="/rya/sparql">
        <fieldset>
            <legend>Query</legend>
            <textarea name="query" rows="10" cols="10">${fn:escapeXml(query)}</textarea>
            <br/>                        
            <select name="mimetype">
                <optgroup label="SELECT">
                    <c:forEach var="tuplequerywriterfactory" items="<%=TupleQueryResultWriterRegistry.getInstance().getAll()%>">
                        <option 
                            ${tuplequerywriterfactory.getTupleQueryResultFormat().getName() eq format ? "selected='selected'":""}
                            value="${tuplequerywriterfactory.getTupleQueryResultFormat().getDefaultMIMEType()}">
                            ${tuplequerywriterfactory.getTupleQueryResultFormat().getName()}
                        </option>
                    </c:forEach>
                </optgroup>                
                <optgroup label="CONSTRUCT">
                    <c:forEach var="rdfwriterfactory" items="<%=RDFWriterRegistry.getInstance().getAll()%>">
                        <option
                            ${rdfwriterfactory.getRDFFormat().getName() eq format ? "selected='selected'":""}
                            value="${rdfwriterfactory.getRDFFormat().getDefaultMIMEType()}">
                            ${rdfwriterfactory.getRDFFormat().getName()}
                        </option>
                    </c:forEach>
                </optgroup>
                <optgroup label="ASK">
                    <c:forEach var="booleanquerywriterfactory" items="<%=BooleanQueryResultWriterRegistry.getInstance().getAll()%>">
                        <option
                            ${booleanquerywriterfactory.getBooleanQueryResultFormat().getName() eq format ? "selected='selected'":""}
                            value="${booleanquerywriterfactory.getBooleanQueryResultFormat().getDefaultMIMEType()}">
                            ${booleanquerywriterfactory.getBooleanQueryResultFormat().getName()}
                        </option>
                    </c:forEach>
                </optgroup>                
            </select>
            <button type="submit">Query</button>            
        </fieldset>
    </form>
</div>