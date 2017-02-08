<%@page contentType="text/html"%>
<%@page pageEncoding="UTF-8"%>
<%@taglib uri="http://java.sun.com/jsf/core" prefix="f" %>
<%@taglib uri="http://java.sun.com/jsf/html" prefix="h" %>
<f:view>
    <html>
        <head>
            <meta http-equiv="Content-Type" content="text/html; charset=UTF-8" />
            <title>Listing Album Items</title>
            <link rel="stylesheet" type="text/css" href="/pandamedia/jsfcrud.xhtml" />
        </head>
        <body>
            <h:panelGroup id="messagePanel" layout="block">
                <h:messages errorStyle="color: red" infoStyle="color: green" layout="table"/>
            </h:panelGroup>
            <h1>Listing Album Items</h1>
            <h:form styleClass="jsfcrud_list_form">
                <h:outputText escape="false" value="(No Album Items Found)<br />" rendered="#{album.pagingInfo.itemCount == 0}" />
                <h:panelGroup rendered="#{album.pagingInfo.itemCount > 0}">
                    <h:outputText value="Item #{album.pagingInfo.firstItem + 1}..#{album.pagingInfo.lastItem} of #{album.pagingInfo.itemCount}"/>&nbsp;
                    <h:commandLink action="#{album.prev}" value="Previous #{album.pagingInfo.batchSize}" rendered="#{album.pagingInfo.firstItem >= album.pagingInfo.batchSize}"/>&nbsp;
                    <h:commandLink action="#{album.next}" value="Next #{album.pagingInfo.batchSize}" rendered="#{album.pagingInfo.lastItem + album.pagingInfo.batchSize <= album.pagingInfo.itemCount}"/>&nbsp;
                    <h:commandLink action="#{album.next}" value="Remaining #{album.pagingInfo.itemCount - album.pagingInfo.lastItem}"
                                   rendered="#{album.pagingInfo.lastItem < album.pagingInfo.itemCount && album.pagingInfo.lastItem + album.pagingInfo.batchSize > album.pagingInfo.itemCount}"/>
                    <h:dataTable value="#{album.albumItems}" var="item" border="0" cellpadding="2" cellspacing="0" rowClasses="jsfcrud_odd_row,jsfcrud_even_row" rules="all" style="border:solid 1px">
                        <h:column>
                            <f:facet name="header">
                                <h:outputText value="Id"/>
                            </f:facet>
                            <h:outputText value="#{item.id}"/>
                        </h:column>
                        <h:column>
                            <f:facet name="header">
                                <h:outputText value="Title"/>
                            </f:facet>
                            <h:outputText value="#{item.title}"/>
                        </h:column>
                        <h:column>
                            <f:facet name="header">
                                <h:outputText value="ReleaseDate"/>
                            </f:facet>
                            <h:outputText value="#{item.releaseDate}">
                                <f:convertDateTime pattern="MM/dd/yyyy" />
                            </h:outputText>
                        </h:column>
                        <h:column>
                            <f:facet name="header">
                                <h:outputText value="ArtistId"/>
                            </f:facet>
                            <h:outputText value="#{item.artistId}"/>
                        </h:column>
                        <h:column>
                            <f:facet name="header">
                                <h:outputText value="GenreId"/>
                            </f:facet>
                            <h:outputText value="#{item.genreId}"/>
                        </h:column>
                        <h:column>
                            <f:facet name="header">
                                <h:outputText value="RecordingLabelId"/>
                            </f:facet>
                            <h:outputText value="#{item.recordingLabelId}"/>
                        </h:column>
                        <h:column>
                            <f:facet name="header">
                                <h:outputText value="NumTracks"/>
                            </f:facet>
                            <h:outputText value="#{item.numTracks}"/>
                        </h:column>
                        <h:column>
                            <f:facet name="header">
                                <h:outputText value="DateEntered"/>
                            </f:facet>
                            <h:outputText value="#{item.dateEntered}">
                                <f:convertDateTime pattern="MM/dd/yyyy" />
                            </h:outputText>
                        </h:column>
                        <h:column>
                            <f:facet name="header">
                                <h:outputText value="CostPrice"/>
                            </f:facet>
                            <h:outputText value="#{item.costPrice}"/>
                        </h:column>
                        <h:column>
                            <f:facet name="header">
                                <h:outputText value="ListPrice"/>
                            </f:facet>
                            <h:outputText value="#{item.listPrice}"/>
                        </h:column>
                        <h:column>
                            <f:facet name="header">
                                <h:outputText value="SalePrice"/>
                            </f:facet>
                            <h:outputText value="#{item.salePrice}"/>
                        </h:column>
                        <h:column>
                            <f:facet name="header">
                                <h:outputText value="RemovalStatus"/>
                            </f:facet>
                            <h:outputText value="#{item.removalStatus}"/>
                        </h:column>
                        <h:column>
                            <f:facet name="header">
                                <h:outputText value="RemovalDate"/>
                            </f:facet>
                            <h:outputText value="#{item.removalDate}">
                                <f:convertDateTime pattern="MM/dd/yyyy" />
                            </h:outputText>
                        </h:column>
                        <h:column>
                            <f:facet name="header">
                                <h:outputText escape="false" value="&nbsp;"/>
                            </f:facet>
                            <h:commandLink value="Show" action="#{album.detailSetup}">
                                <f:param name="jsfcrud.currentAlbum" value="#{jsfcrud_class['com.mycompany.pandamedia.views.util.JsfUtil'].jsfcrud_method['getAsConvertedString'][item][album.converter].jsfcrud_invoke}"/>
                            </h:commandLink>
                            <h:outputText value=" "/>
                            <h:commandLink value="Edit" action="#{album.editSetup}">
                                <f:param name="jsfcrud.currentAlbum" value="#{jsfcrud_class['com.mycompany.pandamedia.views.util.JsfUtil'].jsfcrud_method['getAsConvertedString'][item][album.converter].jsfcrud_invoke}"/>
                            </h:commandLink>
                            <h:outputText value=" "/>
                            <h:commandLink value="Destroy" action="#{album.remove}">
                                <f:param name="jsfcrud.currentAlbum" value="#{jsfcrud_class['com.mycompany.pandamedia.views.util.JsfUtil'].jsfcrud_method['getAsConvertedString'][item][album.converter].jsfcrud_invoke}"/>
                            </h:commandLink>
                        </h:column>

                    </h:dataTable>
                </h:panelGroup>
                <br />
                <h:commandLink action="#{album.createSetup}" value="New Album"/>
                <br />
                <br />
                <h:commandLink value="Index" action="welcome" immediate="true" />


            </h:form>
        </body>
    </html>
</f:view>
