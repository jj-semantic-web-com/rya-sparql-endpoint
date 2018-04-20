<%@page import="org.openrdf.model.BNode"%>
<%@page import="org.openrdf.model.URI"%>
<%@taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@page contentType="application/xhtml+xml; charset=UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<fieldset>
    <legend>Results</legend>
    <table border="1">
    <tr>
        <c:forEach var="bindingName" items="${bindingNames}">
            <th>${bindingName}</th>
        </c:forEach>
    </tr>                
    <c:forEach var="bindingSet" items="${bindings}" varStatus="index">
        <tr>
            <c:forEach var="bindingName" items="${bindingNames}">
                <td>${bindingSet.getValue(bindingName).stringValue()}</td>
            </c:forEach>
        </tr>
    </c:forEach>
    </table>
</fieldset>
