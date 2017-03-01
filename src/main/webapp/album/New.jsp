<%@page contentType="text/html"%>
<%@page pageEncoding="UTF-8"%>
<%@taglib uri="http://java.sun.com/jsf/core" prefix="f" %>
<%@taglib uri="http://java.sun.com/jsf/html" prefix="h" %>
<f:view>
    <html>
        <head>
            <meta http-equiv="Content-Type" content="text/html; charset=UTF-8" />
            <title>New Album</title>
            <link rel="stylesheet" type="text/css" href="/pandamedia/jsfcrud.xhtml" />
        </head>
        <body>
            <h:panelGroup id="messagePanel" layout="block">
                <h:messages errorStyle="color: red" infoStyle="color: green" layout="table"/>
            </h:panelGroup>
            <h1>New Album</h1>
            <h:form>
                <h:inputHidden id="validateCreateField" validator="#{album.validateCreate}" value="value"/>
                <h:panelGrid columns="2">
                    <h:outputText value="Title:"/>
                    <h:inputText id="title" value="#{album.album.title}" title="Title" required="true" requiredMessage="The title field is required." />
                    <h:outputText value="ReleaseDate (MM/dd/yyyy):"/>
                    <h:inputText id="releaseDate" value="#{album.album.releaseDate}" title="ReleaseDate" required="true" requiredMessage="The releaseDate field is required." >
                        <f:convertDateTime pattern="MM/dd/yyyy" />
                    </h:inputText>
                    <h:outputText value="ArtistId:"/>
                    <h:inputText id="artistId" value="#{album.album.artistId}" title="ArtistId" required="true" requiredMessage="The artistId field is required." />
                    <h:outputText value="GenreId:"/>
                    <h:inputText id="genreId" value="#{album.album.genreId}" title="GenreId" required="true" requiredMessage="The genreId field is required." />
                    <h:outputText value="RecordingLabelId:"/>
                    <h:inputText id="recordingLabelId" value="#{album.album.recordingLabelId}" title="RecordingLabelId" required="true" requiredMessage="The recordingLabelId field is required." />
                    <h:outputText value="NumTracks:"/>
                    <h:inputText id="numTracks" value="#{album.album.numTracks}" title="NumTracks" required="true" requiredMessage="The numTracks field is required." />
                    <h:outputText value="DateEntered (MM/dd/yyyy):"/>
                    <h:inputText id="dateEntered" value="#{album.album.dateEntered}" title="DateEntered" required="true" requiredMessage="The dateEntered field is required." >
                        <f:convertDateTime pattern="MM/dd/yyyy" />
                    </h:inputText>
                    <h:outputText value="CostPrice:"/>
                    <h:inputText id="costPrice" value="#{album.album.costPrice}" title="CostPrice" required="true" requiredMessage="The costPrice field is required." />
                    <h:outputText value="ListPrice:"/>
                    <h:inputText id="listPrice" value="#{album.album.listPrice}" title="ListPrice" required="true" requiredMessage="The listPrice field is required." />
                    <h:outputText value="SalePrice:"/>
                    <h:inputText id="salePrice" value="#{album.album.salePrice}" title="SalePrice" required="true" requiredMessage="The salePrice field is required." />
                    <h:outputText value="RemovalStatus:"/>
                    <h:inputText id="removalStatus" value="#{album.album.removalStatus}" title="RemovalStatus" required="true" requiredMessage="The removalStatus field is required." />
                    <h:outputText value="RemovalDate (MM/dd/yyyy):"/>
                    <h:inputText id="removalDate" value="#{album.album.removalDate}" title="RemovalDate" >
                        <f:convertDateTime pattern="MM/dd/yyyy" />
                    </h:inputText>

                </h:panelGrid>
                <br />
                <h:commandLink action="#{album.create}" value="Create"/>
                <br />
                <br />
                <h:commandLink action="#{album.listSetup}" value="Show All Album Items" immediate="true"/>
                <br />
                <br />
                <h:commandLink value="Index" action="welcome" immediate="true" />

            </h:form>
        </body>
    </html>
</f:view>
