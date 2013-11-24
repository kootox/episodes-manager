<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>
<%@page contentType="text/html" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>
    <head>
        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
        <c:choose>
            <c:when test="${season.number==0}"><title>EpisodesManager - ${season.show.title} - Season ${season.number}</title></c:when>
            <c:otherwise><title>EpisodesManager - ${season.show.title} - Specials</title></c:otherwise>
        </c:choose>

    </head>
    <body id="container">

        <div>

            <h1>
                <c:choose>
                    <c:when test="${season.number==0}">${season.show.title} - Hors-séries</c:when>
                    <c:otherwise>${season.show.title} - Saison ${season.number}</c:otherwise>
                </c:choose>
            </h1>

            <p/>

            <!--BREADCRUMB FOR NAVIGATION-->
            <c:url value="show" var="showUrl"><c:param name="id" value="${season.show.topiaId}"/></c:url>
            <ul class="breadcrumb">
                <li><a href="index">Index</a> <span class="divider">/</span></li>
                <li><a href="${showUrl}">${season.show.title}</a> <span class="divider">/</span></li>

                <c:choose>
                   <c:when test="${season.number==0}"><li class="active">Hors-séries</li></c:when>
                   <c:otherwise><li class="active">Saison ${season.number}</li></c:otherwise>
                </c:choose>
            </ul>

        <table class="table table-bordered table-striped table-hover table-condensed">
            <thead>
                <tr>
                    <th>Épisode</th>
                    <th>Possédé - <a class="link all-acquired" topiaId="${season.topiaId}">Tout</a>/<a class="link none-acquired" topiaId="${season.topiaId}">Rien</a></th>
                    <th>Vu - <a class="link all-seen" topiaId="${season.topiaId}">Tout</a>/<a class="link none-seen" topiaId="${season.topiaId}">Rien</a></th>
                </tr>
            </thead>
            <tbody>
                <c:forEach items="${episodes}" var="episode">
                    <c:url value="episode" var="episodeUrl"><c:param name="id" value="${episode.topiaId}"/></c:url>
                    <tr>
                        <td><a href="${episodeUrl}"><i class="icon-film"></i> ${episode.number} - ${episode.title}</a></td>
                        <td><input type="checkbox" class="acquired" <c:choose><c:when test="${episode.acquired}">checked</c:when></c:choose>/></td>
                        <td><input type="checkbox" class="seen" <c:choose><c:when test="${episode.viewed}">checked</c:when></c:choose>/></td>
                    </tr>
                </c:forEach>
                <tr>
                  <td></td>
                  <td><a class="link all-acquired" topiaId="${season.topiaId}">Tout</a>/<a class="link none-acquired" topiaId="${season.topiaId}">Rien</a></td>
                  <td><a class="link all-seen" topiaId="${season.topiaId}">Tout</a>/<a class="link none-seen" topiaId="${season.topiaId}">Rien</a></td>
                </tr>
            </tbody>
        </table>

        </div>

    </body>
</html>