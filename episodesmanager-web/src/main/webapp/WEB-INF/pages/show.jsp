<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>
<%@page contentType="text/html" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>
    <head>
        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
        <title>EpisodesManager - ${show.title}</title>
    </head>
    <body id="container">

    <div>

        <h1>${show.title}</h1>

        <p/>

        <!--BREADCRUMB FOR NAVIGATION-->
        <ul class="breadcrumb">
            <li><a href="index">Index</a> <span class="divider">/</span></li>
            <li class="active">${show.title}</li>
        </ul>

        <ul class="unstyled">
            <li>
                <div class="enum-left">Statut</div>
                <div class="enum-right">
                    <c:choose>
                       <c:when test="${show.over}">Terminé</c:when>
                       <c:otherwise>En cours</c:otherwise>
                    </c:choose>
                </div>
                <div style="clear:both;"></div>
            </li>

            <li>
                <div class="enum-left">Pays d'origine</div>
                <div class="enum-right">${show.originCountry}</div>
                <div style="clear:both;"></div>
            </li>

            <li>
                <div class="enum-left">Chaîne</div>
                <div class="enum-right">${show.network}</div>
                <div style="clear:both;"></div>
            </li>

            <li>
                <div class="enum-left">Première diffusion</div>
                <div class="enum-right"><fmt:formatDate pattern="dd/MM/yyyy" value="${show.firstAired}"/></div>
                <div style="clear:both;"></div>
            </li>

            <li>
                <div class="enum-left">Résumé</div>
                <div class="enum-right">${show.summary}</div>
                <div style="clear:both;"></div>
            </li>

            <li>
                <div class="enum-left">Acteurs</div>
                <div class="enum-right">
                    <ul class="inline">
                        <c:forEach items="${show.actors}" var="actor">
                            <li>${actor}</li>
                        </c:forEach>
                    </ul>
                </div>
                <div style="clear:both;"></div>
            </li>

            <li>
                <div class="enum-left">Genres</div>
                <div class="enum-right">
                    <ul class="inline">
                        <c:forEach items="${show.genres}" var="genre">
                            <li>${genre}</li>
                        </c:forEach>
                    </ul>
                </div>
                <div style="clear:both;"></div>
            </li>

            <li>
                <div class="enum-left">Saisons</div>
                <div class="enum-right">
                    <ul class="inline">
                        <c:forEach items="${seasons}" var="season">
                            <c:url value="season" var="seasonUrl"><c:param name="id" value="${season.topiaId}"/></c:url>

                            <c:choose>
                               <c:when test="${season.number==0}"><li><a href="${seasonUrl}">Hors-séries</a></li></c:when>
                               <c:otherwise><li><a href="${seasonUrl}">Saison ${season.number}</a></li></c:otherwise>
                            </c:choose>

                        </c:forEach>
                    </ul>
                </div>
                <div style="clear:both;"></div>
            </li>

        </ul>
        
        <!--img src="http://images.zap2it.com/favicon.ico"/-->
        Plus d'informations :
        <a href="http://www.imdb.com/title/${show.imdbId}" title="IMDb"><img src="http://i.media-imdb.com/images/SFff39adb4d259f3c3fd166853a6714a32/favicon.ico"/></a>
        <a href="http://thetvdb.com/?tab=series&id=${show.thetvdbId}" title="TheTVDB"><img src="http://www.thetvdb.com/favicon.ico"/></a>
        
    </div>
    </body>
</html>
