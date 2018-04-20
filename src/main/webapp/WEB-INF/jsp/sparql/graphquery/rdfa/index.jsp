<%@page import="org.openrdf.model.Literal"%>
<%@page import="org.openrdf.model.BNode"%>
<%@page import="org.openrdf.model.URI"%>
<%@taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@taglib prefix="utils" uri="/WEB-INF/tld/Utils" %>
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>

<fieldset>
    <legend>Results</legend>
    <table border="1">
        <tr>
            <td><spring:message code="sparql.endpoint.subject"/></td>
            <td><spring:message code="sparql.endpoint.predicate"/></td>
            <td><spring:message code="sparql.endpoint.object"/></td>
            <td><spring:message code="sparql.endpoint.datatype"/></td>
        </tr>
        <c:set var="literal" value="<%=Literal.class%>"/>
        <c:set var="uri" value="<%=URI.class%>"/>
        <c:set var="bnode" value="<%=BNode.class%>"/>
        <c:forEach var="statement" items="${statements}">
            <tr>
                <td>${fn:escapeXml(statement.subject.toString())}</td>
                <td>${statement.predicate.stringValue()}</td>
                <c:choose>
                    <c:when test="${utils:isInstanceof(statement.object,literal)}">
                        <td
                            about="${statement.subject.stringValue()}"
                            property="${statement.predicate.stringValue()}"
                            datatype="${statement.object.getDatatype()}"
                            content="${fn:escapeXml(statement.object.stringValue())}"
                            >${statement.object.stringValue()}                                    
                        </td>
                        <td>
                            ${statement.object.getDatatype()}
                        </td>
                    </c:when>
                    <c:when test="${utils:isInstanceof(statement.object,uri) || utils:isInstanceof(statement.object,bnode)}">
                        <td
                            about="${fn:escapeXml(statement.subject.toString())}"
                            rel="${statement.predicate.stringValue()}"
                            resource="${statement.object.toString()}"
                            >${statement.object.toString()}                                 
                        </td>
                        <td>&#160;</td>
                    </c:when>
                </c:choose>
            </tr>
        </c:forEach>
    </table>
</fieldset>