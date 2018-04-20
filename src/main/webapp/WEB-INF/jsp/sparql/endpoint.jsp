<%@page contentType="application/xhtml+xml; charset=UTF-8" %><%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %><jsp:include page="/WEB-INF/jsp/sparql/header.jsp" flush="true"/>
<jsp:include page="/WEB-INF/jsp/sparql/sparql_form.jsp" flush="true"/>
<c:choose>
    <c:when test="${querytype == 'TUPLE' }">
        <jsp:include page="/WEB-INF/jsp/sparql/tuplequery/index.jsp" flush="true"/>        
    </c:when>
    <c:when test="${querytype == 'GRAPH' }">
        <jsp:include page="/WEB-INF/jsp/sparql/graphquery/rdfa/index.jsp" flush="true"/>        
    </c:when>
    <c:when test="${querytype == 'BOOLEAN' }">
        <jsp:include page="/WEB-INF/jsp/sparql/booleanquery/index.jsp" flush="true"/>        
    </c:when>    
</c:choose>
<jsp:include page="/WEB-INF/jsp/sparql/info.jsp" flush="true"/>
<jsp:include page="/WEB-INF/jsp/sparql/footer.jsp" flush="true"/>