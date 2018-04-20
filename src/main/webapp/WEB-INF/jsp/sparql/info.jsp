<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<fieldset>
    <legend>Info</legend>
    <a href="https://validator.w3.org/check?uri=referer" target="_blank">
        <img width="88" height="31" src="https://www.w3.org/Icons/valid-xhtml-rdfa-blue.png" alt="Valid XHTML + RDFa"/>
    </a>
    <a href="https://www.w3.org/TR/sparql11-query/" target="_blank">
        <img src="https://www.w3.org/Icons/SW/Buttons/sw-sparql-blue.png" alt="SPARQL"/>
    </a>
    <c:choose>
        <c:when test="${querytype == 'GRAPH' }">
            <a href="http://www.w3.org/2012/pyRdfa/extract?uri=referer" target="_blank">
                <img src="https://www.w3.org/Icons/SW/Buttons/sw-rdfa-blue.png" alt="RDFa Distiller"/>
            </a>
        </c:when>
    </c:choose>    
</fieldset>