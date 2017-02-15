<%@page contentType="text/html"%>
<%@page pageEncoding="UTF-8"%>
<%@taglib uri="http://java.sun.com/jsf/core" prefix="f" %>
<%@taglib uri="http://java.sun.com/jsf/html" prefix="h" %>
<f:view>
    <html>
        <head>
            <meta http-equiv="Content-Type" content="text/html; charset=UTF-8" />
            <title>Album Detail</title>
            <link rel="stylesheet" type="text/css" href="/pandamedia/jsfcrud.xhtml" />
        </head>
        <body>
            <h:panelGroup id="messagePanel" layout="block">
                <h:messages errorStyle="color: red" infoStyle="color: green" layout="table"/>
            </h:panelGroup>
            <h1>Album Detail</h1>
            <h:form>
                <h:panelGrid columns="2">
                    <h:outputText value="Id:"/>
                    <h:outputText value="#{album.album.id}" title="Id" />
                    <h:outputText value="Title:"/>
                    <h:outputText value="#{album.album.title}" title="Title" />
                    <h:outputText value="ReleaseDate:"/>
                    <h:outputText value="#{album.album.releaseDate}" title="ReleaseDate" >
                        <f:convertDateTime pattern="MM/dd/yyyy" />
                    </h:outputText>
                    <h:outputText value="ArtistId:"/>
                    <h:outputText value="#{album.album.artistId}" title="ArtistId" />
                    <h:outputText value="GenreId:"/>
                    <h:outputText value="#{album.album.genreId}" title="GenreId" />
                    <h:outputText value="RecordingLabelId:"/>
                    <h:outputText value="#{album.album.recordingLabelId}" title="RecordingLabelId" />
                    <h:outputText value="NumTracks:"/>
                    <h:outputText value="#{album.album.numTracks}" title="NumTracks" />
                    <h:outputText value="DateEntered:"/>
                    <h:outputText value="#{album.album.dateEntered}" title="DateEntered" >
                        <f:convertDateTime pattern="MM/dd/yyyy" />
                    </h:outputText>
                    <h:outputText value="CostPrice:"/>
                    <h:outputText value="#{album.album.costPrice}" title="CostPrice" />
                    <h:outputText value="ListPrice:"/>
                    <h:outputText value="#{album.album.listPrice}" title="ListPrice" />
                    <h:outputText value="SalePrice:"/>
                    <h:outputText value="#{album.album.salePrice}" title="SalePrice" />
                    <h:outputText value="RemovalStatus:"/>
                    <h:outputText value="#{album.album.removalStatus}" title="RemovalStatus" />
                    <h:outputText value="RemovalDate:"/>
                    <h:outputText value="#{album.album.removalDate}" title="RemovalDate" >
                        <f:convertDateTime pattern="MM/dd/yyyy" />
                    </h:outputText>


                </h:panelGrid>
                <br />
                <h:commandLink action="#{album.remove}" value="Destroy">
                    <f:param name="jsfcrud.currentAlbum" value="#{jsfcrud_class['com.mycompany.pandamedia.views.util.JsfUtil'].jsfcrud_method['getAsConvertedString'][album.album][album.converter].jsfcrud_invoke}" />
                </h:commandLink>
                <br />
                <br />
                <h:commandLink action="#{album.editSetup}" value="Edit">
                    <f:param name="jsfcrud.currentAlbum" value="#{jsfcrud_class['com.mycompany.pandamedia.views.util.JsfUtil'].jsfcrud_method['getAsConvertedString'][album.album][album.converter].jsfcrud_invoke}" />
                </h:commandLink>
                <br />
                <h:commandLink action="#{album.createSetup}" value="New Album" />
                <br />
                <h:commandLink action="#{album.listSetup}" value="Show All Album Items"/>
                <br />
                <br />
                <h:commandLink value="Index" action="welcome" immediate="true" />

            </h:form>
        </body>
    </html>
</f:view>
