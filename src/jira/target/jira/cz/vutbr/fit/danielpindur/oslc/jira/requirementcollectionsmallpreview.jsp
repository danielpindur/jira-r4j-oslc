<%--To avoid the overriding of any manual code changes upon subsequent code generations, disable "Generate JSP Files" option in the Adaptor model.--%>
<!DOCTYPE html>
<%--
 Copyright (c) 2022 Contributors to the Eclipse Foundation
 
 See the NOTICE file(s) distributed with this work for additional
 information regarding copyright ownership.
 
 This program and the accompanying materials are made available under the
 terms of the Eclipse Distribution License 1.0 which is available at
 http://www.eclipse.org/org/documents/edl-v10.php.
 
 SPDX-License-Identifier: BSD-3-Simple

 This file is generated by Lyo Designer (https://www.eclipse.org/lyo/)
--%>

<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn" %>

<%@page import="org.eclipse.lyo.oslc4j.core.model.Link" %>
<%@page import="org.eclipse.lyo.oslc4j.core.model.ServiceProvider"%>
<%@page import="org.eclipse.lyo.oslc4j.core.model.OslcConstants"%>
<%@page import="org.eclipse.lyo.oslc4j.core.OSLC4JUtils"%>
<%@page import="org.eclipse.lyo.oslc4j.core.annotation.OslcPropertyDefinition"%>
<%@page import="org.eclipse.lyo.oslc4j.core.annotation.OslcName"%>
<%@page import="java.lang.reflect.Method"%>
<%@page import="java.net.URI"%>
<%@page import="java.util.Collection"%>
<%@page import="java.util.Date"%>
<%@page import="java.util.HashSet"%>
<%@page import="java.util.List"%>
<%@page import="java.util.Iterator"%>
<%@page import="java.util.Map"%>
<%@page import="javax.xml.namespace.QName"%>
<%@page import="javax.ws.rs.core.UriBuilder"%>

<%@page import="cz.vutbr.fit.danielpindur.oslc.jira.resources.RequirementCollection"%>
<%@page import="cz.vutbr.fit.danielpindur.oslc.jira.resources.Oslc_rmDomainConstants"%>

<%@ page contentType="text/html" language="java" pageEncoding="UTF-8" %>

<%
  RequirementCollection aRequirementCollection = (RequirementCollection) request.getAttribute("aRequirementCollection");
%>

<html lang="en">
<head>
    <meta charset="utf-8">
    <meta name="viewport" content="width=device-width, initial-scale=1, shrink-to-fit=no">
    <title><%= aRequirementCollection.toString() %></title>

    <link rel="stylesheet" href="https://stackpath.bootstrapcdn.com/bootstrap/4.2.1/css/bootstrap.min.css" integrity="sha384-GJzZqFGwb1QTTN6wy59ffF1BuGJpLSa9DkKMp0DgiMDm4iYMj70gZWKYbI706tWS" crossorigin="anonymous">
    <script src="https://code.jquery.com/jquery-3.3.1.slim.min.js" integrity="sha384-q8i/X+965DzO0rT7abK41JStQIAqVgRVzpbzo5smXKp4YfRvH+8abtTE1Pi6jizo" crossorigin="anonymous"></script>
    <script src="https://cdnjs.cloudflare.com/ajax/libs/popper.js/1.14.6/umd/popper.min.js" integrity="sha384-wHAiFfRlMFy6i5SRaxvfOCifBUQy1xHdJ/yoi7FRNXMRBu5WHdZYu1hA6ZOblgut" crossorigin="anonymous"></script>
    <script src="https://stackpath.bootstrapcdn.com/bootstrap/4.2.1/js/bootstrap.min.js" integrity="sha384-B0UglyR+jN6CkvvICOB2joaf5I4l3gm9GU6Hc1og6Ls7i6U/mkkaduKaBhlAXv9k" crossorigin="anonymous"></script>
    <link href="<c:url value="/static/css/adaptor.css"/>" rel="stylesheet">
</head>

<body>

<!-- Begin page content -->
<div>
    <% Method method = null; %>
    <dl class="dl-horizontal">
        <% method = RequirementCollection.class.getMethod("getTitle"); %>
        <dt><a href="<%=method.getAnnotation(OslcPropertyDefinition.class).value() %>"><%=method.getAnnotation(OslcName.class).value()%></a></dt>
        <dd>
        <%
        if (aRequirementCollection.getTitle() == null) {
            out.write("<em>null</em>");
        }
        else {
            out.write(aRequirementCollection.getTitle().toString());
        }
        %>
        
        </dd>
    </dl>
    <dl class="dl-horizontal">
        <% method = RequirementCollection.class.getMethod("getDescription"); %>
        <dt><a href="<%=method.getAnnotation(OslcPropertyDefinition.class).value() %>"><%=method.getAnnotation(OslcName.class).value()%></a></dt>
        <dd>
        <%
        if (aRequirementCollection.getDescription() == null) {
            out.write("<em>null</em>");
        }
        else {
            out.write(aRequirementCollection.getDescription().toString());
        }
        %>
        
        </dd>
    </dl>
    <dl class="dl-horizontal">
        <% method = RequirementCollection.class.getMethod("getIdentifier"); %>
        <dt><a href="<%=method.getAnnotation(OslcPropertyDefinition.class).value() %>"><%=method.getAnnotation(OslcName.class).value()%></a></dt>
        <dd>
        <%
        if (aRequirementCollection.getIdentifier() == null) {
            out.write("<em>null</em>");
        }
        else {
            out.write(aRequirementCollection.getIdentifier().toString());
        }
        %>
        
        </dd>
    </dl>
    <dl class="dl-horizontal">
        <% method = RequirementCollection.class.getMethod("getDecomposedBy"); %>
        <dt><a href="<%=method.getAnnotation(OslcPropertyDefinition.class).value() %>"><%=method.getAnnotation(OslcName.class).value()%></a></dt>
        <dd>
        <ul>
        <%
        for(Link next : aRequirementCollection.getDecomposedBy()) {
            if (next.getValue() == null) {
                out.write("<li>" + "<em>null</em>" + "</li>");
            }
            else {
                out.write("<li>" + "<a href=\"" + next.getValue().toString() + "\" class=\"oslc-resource-link\">" + next.getValue().toString() + "</a>" + "</li>");
            }
        }
        %>
        </ul>
        
        </dd>
    </dl>
    <dl class="dl-horizontal">
        <% method = RequirementCollection.class.getMethod("getDecomposes"); %>
        <dt><a href="<%=method.getAnnotation(OslcPropertyDefinition.class).value() %>"><%=method.getAnnotation(OslcName.class).value()%></a></dt>
        <dd>
        <ul>
        <%
        for(Link next : aRequirementCollection.getDecomposes()) {
            if (next.getValue() == null) {
                out.write("<li>" + "<em>null</em>" + "</li>");
            }
            else {
                out.write("<li>" + "<a href=\"" + next.getValue().toString() + "\" class=\"oslc-resource-link\">" + next.getValue().toString() + "</a>" + "</li>");
            }
        }
        %>
        </ul>
        
        </dd>
    </dl>
    <dl class="dl-horizontal">
        <% method = RequirementCollection.class.getMethod("getProject"); %>
        <dt><a href="<%=method.getAnnotation(OslcPropertyDefinition.class).value() %>"><%=method.getAnnotation(OslcName.class).value()%></a></dt>
        <dd>
        <%
        if ((aRequirementCollection.getProject() == null) || (aRequirementCollection.getProject().getValue() == null)) {
            out.write("<em>null</em>");
        }
        else {
            %>
            <jsp:include page="/cz/vutbr/fit/danielpindur/oslc/jira/projecttohtml.jsp">
                <jsp:param name="resourceUri" value="<%=aRequirementCollection.getProject().getValue()%>"/> 
                </jsp:include>
            <%
        }
        %>
        
        </dd>
    </dl>
    <dl class="dl-horizontal">
        <% method = RequirementCollection.class.getMethod("getShortTitle"); %>
        <dt><a href="<%=method.getAnnotation(OslcPropertyDefinition.class).value() %>"><%=method.getAnnotation(OslcName.class).value()%></a></dt>
        <dd>
        <%
        if (aRequirementCollection.getShortTitle() == null) {
            out.write("<em>null</em>");
        }
        else {
            out.write(aRequirementCollection.getShortTitle().toString());
        }
        %>
        
        </dd>
    </dl>
    <dl class="dl-horizontal">
        <% method = RequirementCollection.class.getMethod("getModified"); %>
        <dt><a href="<%=method.getAnnotation(OslcPropertyDefinition.class).value() %>"><%=method.getAnnotation(OslcName.class).value()%></a></dt>
        <dd>
        <%
        if (aRequirementCollection.getModified() == null) {
            out.write("<em>null</em>");
        }
        else {
            out.write(aRequirementCollection.getModified().toString());
        }
        %>
        
        </dd>
    </dl>
    <dl class="dl-horizontal">
        <% method = RequirementCollection.class.getMethod("getCreated"); %>
        <dt><a href="<%=method.getAnnotation(OslcPropertyDefinition.class).value() %>"><%=method.getAnnotation(OslcName.class).value()%></a></dt>
        <dd>
        <%
        if (aRequirementCollection.getCreated() == null) {
            out.write("<em>null</em>");
        }
        else {
            out.write(aRequirementCollection.getCreated().toString());
        }
        %>
        
        </dd>
    </dl>
    <dl class="dl-horizontal">
        <% method = RequirementCollection.class.getMethod("getContributor"); %>
        <dt><a href="<%=method.getAnnotation(OslcPropertyDefinition.class).value() %>"><%=method.getAnnotation(OslcName.class).value()%></a></dt>
        <dd>
        <ul>
        <%
        for(Link next : aRequirementCollection.getContributor()) {
            if (next.getValue() == null) {
                out.write("<li>" + "<em>null</em>" + "</li>");
            }
            else {
                %>
                <li>
                <jsp:include page="/cz/vutbr/fit/danielpindur/oslc/jira/persontohtml.jsp">
                    <jsp:param name="resourceUri" value="<%=next.getValue()%>"/> 
                    </jsp:include>
                </li>
                <%
            }
        }
        %>
        </ul>
        
        </dd>
    </dl>
    <dl class="dl-horizontal">
        <% method = RequirementCollection.class.getMethod("getCreator"); %>
        <dt><a href="<%=method.getAnnotation(OslcPropertyDefinition.class).value() %>"><%=method.getAnnotation(OslcName.class).value()%></a></dt>
        <dd>
        <ul>
        <%
        for(Link next : aRequirementCollection.getCreator()) {
            if (next.getValue() == null) {
                out.write("<li>" + "<em>null</em>" + "</li>");
            }
            else {
                %>
                <li>
                <jsp:include page="/cz/vutbr/fit/danielpindur/oslc/jira/persontohtml.jsp">
                    <jsp:param name="resourceUri" value="<%=next.getValue()%>"/> 
                    </jsp:include>
                </li>
                <%
            }
        }
        %>
        </ul>
        
        </dd>
    </dl>
    <dl class="dl-horizontal">
        <% method = RequirementCollection.class.getMethod("getSubject"); %>
        <dt><a href="<%=method.getAnnotation(OslcPropertyDefinition.class).value() %>"><%=method.getAnnotation(OslcName.class).value()%></a></dt>
        <dd>
        <ul>
        <%
        Iterator<String> subjectItr = aRequirementCollection.getSubject().iterator();
        while(subjectItr.hasNext()) {
            out.write("<li>" + subjectItr.next().toString() + "</li>");
        }
        %>
        </ul>
        
        </dd>
    </dl>
    <dl class="dl-horizontal">
        <% method = RequirementCollection.class.getMethod("getJiraId"); %>
        <dt><a href="<%=method.getAnnotation(OslcPropertyDefinition.class).value() %>"><%=method.getAnnotation(OslcName.class).value()%></a></dt>
        <dd>
        <%
        if (aRequirementCollection.getJiraId() == null) {
            out.write("<em>null</em>");
        }
        else {
            out.write(aRequirementCollection.getJiraId().toString());
        }
        %>
        
        </dd>
    </dl>
</div>
<%
Map<QName, Object> extendedProperties = aRequirementCollection.getExtendedProperties();
if (!extendedProperties.isEmpty()) {
%>
    <div>
    <%
    for (Map.Entry<QName, Object> entry : extendedProperties.entrySet()) 
    {
        QName key = entry.getKey();
        Object value = entry.getValue();
    %>
    <dl class="row">
        <dt  class="col-sm-2 text-right"><a href="<%=key.getNamespaceURI() + key.getLocalPart() %>"><%=key.getLocalPart()%></a></dt>
        <dd class="col-sm-9"><%= value.toString()%></dd>
    </dl>
    <%
    }
    %>
    </div>
<%
}
%>
</body>
</html>
