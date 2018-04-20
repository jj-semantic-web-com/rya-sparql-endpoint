<%@page import="org.openrdf.model.BNode"%><%@page import="org.openrdf.model.URI"%><%@taglib prefix="spring" uri="http://www.springframework.org/tags" %><%@page contentType="application/xhtml+xml; charset=UTF-8" %><%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %><?xml version="1.0" encoding="UTF-8"?>
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML+RDFa 1.1//EN" "http://www.w3.org/MarkUp/DTD/xhtml-rdfa-2.dtd">
<html 
    xmlns="http://www.w3.org/1999/xhtml" 
    xmlns:foaf="http://xmlns.com/foaf/0.1/" 
    xmlns:wdrs="http://www.w3.org/2007/05/power-s#" 
    xmlns:contact="http://www.w3.org/2000/10/swap/pim/contact#"     
    xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance" 
    xsi:schemaLocation="http://www.w3.org/1999/xhtml  
    http://www.w3.org/MarkUp/SCHEMA/xhtml-rdfa-2.xsd" 
    version="XHTML+RDFa 1.1" 
    lang="en" 
    xml:lang="en">
    <head>
        <meta http-equiv="Content-Type" content="application/xhtml+xml; charset=UTF-8"/>
        <title><spring:message code="sparql.endpoint.title"/></title>
        <link rel="stylesheet" type="text/css" href="resources/styles/style.css" />
    </head>
    <body>