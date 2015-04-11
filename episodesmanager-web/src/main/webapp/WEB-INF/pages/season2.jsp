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

        <script>
          angular.module('Season', []).
                      value('episodes', ${episodes});
        </script>

        <script type="text/javascript" src="<c:url value='/js/season.js'/>"></script>

    </head>
    <body id="container">

        <div ng-app="Season">

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

        <table class="table table-bordered table-striped table-hover table-condensed" ng-controller="seasonController">
            <thead>
                <tr>
                    <th>Épisode</th>
                    <th>Possédé - <a class="link all-acquired" topiaId="${season.topiaId}">Tout</a>/<a class="link none-acquired" topiaId="${season.topiaId}">Rien</a></th>
                    <th>Vu - <a class="link all-seen" topiaId="${season.topiaId}">Tout</a>/<a class="link none-seen" topiaId="${season.topiaId}">Rien</a></th>
                </tr>
            </thead>
            <tbody>
                <tr ng-repeat="episode in episodes" class="salesFunnelItem">
                    <td><a ng-href='createUrl("/episode?id=",{{episode.topiaId}})'><i class="icon-film"></i> {{episode.number}} - {{episode.title}}</a></td>
                    <td><input type="checkbox" class="acquired" ng-checked="episode.acquired"/></td>
                    <td><input type="checkbox" class="seen"  ng-checked="episode.viewed"/></td>
                </tr>
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
