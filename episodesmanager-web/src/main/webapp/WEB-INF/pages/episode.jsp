<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@page contentType="text/html" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>
    <head>
        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
        <title>EpisodesManager - ${episode.title}</title>
    </head>
    <body>
        <div class="hero-unit">
            <h1>${episode.title}</h1>

            <p/>

            <!-- BREADCRUMB FOR NAVIGATION -->

            <c:url value="show" var="showUrl"><c:param name="id" value="${episode.season.show.topiaId}"/></c:url>
            <c:url value="season" var="seasonUrl"><c:param name="id" value="${episode.season.topiaId}"/></c:url>

            <ul class="breadcrumb">
                <li><a href="index">Index</a> <span class="divider">/</span></li>
                <li><a href="${showUrl}">${episode.season.show.title}</a> <span class="divider">/</span></li>

                <c:choose>
                   <c:when test="${episode.season.number==0}"><li><a href="${seasonUrl}">Hors-séries</a> <span class="divider">/</span></li></c:when>
                   <c:otherwise><li><a href="${seasonUrl}">Saison ${episode.season.number}</a> <span class="divider">/</span></li></c:otherwise>
                </c:choose>

                <li class="active">${episode.title}</li>
            </ul>
        
            <dl class="dl-horizontal">

                <dt>Date de diffusion</dt>
                <dd><fmt:formatDate pattern="dd/MM/yyyy" value="${episode.airingDate}"/></dd>

                <dt>Résumé</dt>
                <dd><div align="justify">${episode.summary}</div></dd>

                <dt>Réalisateur<dt>
                <dd>${episode.director}</dd>

                <dt>Scénariste</dt>
                <dd>${episode.writer}</dd>

                <dt>Guests</dt>
                <dd>
                    <ul>
                         <c:forEach items="${episode.guestStars}" var="guest">
                            <li>${guest}</li>
                        </c:forEach>
                    </ul>
                </dd>
            </dl>
        </div>
        
        
    </body>
</html>
