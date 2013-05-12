<%@page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>

<?xml version="1.0" encoding="UTF-8" ?>
<!DOCTYPE html>

<html>
<head>
    <title>Episodes Manager - <sitemesh:write property='title'/></title>
    <link rel=stylesheet href="<c:url value='/css/episodesManager.css'/>">

    <script type="text/javascript">
        var webContext = "<c:url value='/'/>";
    </script>

    <%-- import via nuiton-js to add js or css show WEB-INF/wro.xml --%>
    <link href="<c:url value='/nuiton-js/episodesmanager.css'/>" rel="stylesheet" type="text/css"/>
    <script type="text/javascript" src="<c:url value='/nuiton-js/episodesmanager.js?minimize=false'/>"></script>

    <script type="text/javascript" src="<c:url value='/js/episodes-manager.js'/>"></script>

    <sitemesh:write property='head'/>
</head>
<body>

<!-- Header
================================================== -->

<div class="navbar navbar-fixed-top">
    <div class="navbar-inner">
        <div class="container">
            <a class="btn btn-navbar" data-toggle="collapse" data-target=".nav-collapse">
                <span class="icon-bar"></span>
                <span class="icon-bar"></span>
                <span class="icon-bar"></span>
            </a>
            <a class="brand" href="index">Episodes Manager</a>
            <div class="nav-collapse">
                <ul class="nav">
                    <li class="active"><a href="<c:url value='/index'/>">Accueil</a></li>
                    <li><a href="<c:url value='/add/search'/>">Ajouter</a></li>
                    <li><a href="<c:url value='/timeSpent'/>">Temps passé</a></li>
                    <li><a href="#about">About</a></li>
                    <li><a href="#contact">Contact</a></li>
                </ul>
            </div><!--/.nav-collapse -->
        </div>
    </div>
</div>

<!-- Container
================================================== -->

<div id="main" class="container">
    <sitemesh:write property='body'/>

    <!-- Footer
   ================================================== -->
    <div id="footer">
            <p>Episodes Manager - &copy; Jean Couteau - License AGPL</p>
    </div>
</div>


</body>
</html>