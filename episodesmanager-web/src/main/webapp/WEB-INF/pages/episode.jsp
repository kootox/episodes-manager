<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>
<%@page contentType="text/html" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>
    <head>
        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
        <title>EpisodesManager - ${episode.title}</title>
    </head>
    <body>
        <div>
            <h1>${episode.season.show.title} - ${episode.season.number}x${episode.number} - ${episode.title}</h1>

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

            <!-- TODO kootox 13-05-20 put episode image here-->

            <table class="table">
                <tr>
                    <td><b>Statut</b></td>
                    <td>
                        <c:choose>
                            <c:when test="${episode.viewed}">Vu</c:when>
                            <c:otherwise>
                                <c:choose>
                                    <c:when test="${episode.acquired}">Possédé</c:when>
                                    <c:otherwise>Non possédé</c:otherwise>
                                </c:choose>
                            </c:otherwise>
                        </c:choose>
                    </td>
                </tr>
                <tr>
                    <td><b>Date de diffusion</b></td>
                    <td><fmt:formatDate pattern="dd/MM/yyyy" value="${episode.airingDate}"/></td>
                </tr>
                <tr>
                    <td><b>Résumé</b></td>
                    <td><div align="justify">${episode.summary}</div></td>
                </tr>
                <tr>
                    <td><b>Réalisateur</b></td>
                    <td>${episode.director}</td>
                </tr>
                <tr>
                    <td><b>Scénariste</b></td>
                    <td>${episode.writer}</td>
                </tr>
                <tr>
                    <td><b>Guests</b></td>
                    <td>
                        <ul>
                             <c:forEach items="${episode.guestStars}" var="guest">
                                <li>${guest}</li>
                            </c:forEach>
                        </ul>
                    </td>
                </tr>
            </table>


        </div>
        
        
    </body>
</html>
