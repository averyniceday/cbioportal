<%--
 - Copyright (c) 2015 Memorial Sloan-Kettering Cancer Center.
 -
 - This library is distributed in the hope that it will be useful, but WITHOUT
 - ANY WARRANTY, WITHOUT EVEN THE IMPLIED WARRANTY OF MERCHANTABILITY OR FITNESS
 - FOR A PARTICULAR PURPOSE. The software and documentation provided hereunder
 - is on an "as is" basis, and Memorial Sloan-Kettering Cancer Center has no
 - obligations to provide maintenance, support, updates, enhancements or
 - modifications. In no event shall Memorial Sloan-Kettering Cancer Center be
 - liable to any party for direct, indirect, special, incidental or
 - consequential damages, including lost profits, arising out of the use of this
 - software and its documentation, even if Memorial Sloan-Kettering Cancer
 - Center has been advised of the possibility of such damage.
 --%>

<%--
 - This file is part of cBioPortal.
 -
 - cBioPortal is free software: you can redistribute it and/or modify
 - it under the terms of the GNU Affero General Public License as
 - published by the Free Software Foundation, either version 3 of the
 - License.
 -
 - This program is distributed in the hope that it will be useful,
 - but WITHOUT ANY WARRANTY; without even the implied warranty of
 - MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 - GNU Affero General Public License for more details.
 -
 - You should have received a copy of the GNU Affero General Public License
 - along with this program.  If not, see <http://www.gnu.org/licenses/>.
--%>

<%@ page import="org.mskcc.cbio.portal.util.GlobalProperties" %>
<%@ taglib prefix='c' uri='http://java.sun.com/jsp/jstl/core' %>
<%@ taglib prefix="s" uri="http://www.springframework.org/tags" %>
<%@ taglib prefix="sec" uri="http://www.springframework.org/security/tags" %>
<jsp:include page="js_include_standard.jsp" flush="true" />
<%
    String principal = "";
    String authenticationMethod = GlobalProperties.authenticationMethod();
    pageContext.setAttribute("authenticationMethod", authenticationMethod);
    if (authenticationMethod.equals("openid") || authenticationMethod.equals("ldap")) {
        principal = "principal.name";
    }
    else if (authenticationMethod.equals("googleplus") ||
	    		authenticationMethod.equals("saml") ||
	    		authenticationMethod.equals("ad") ||
	    		authenticationMethod.equals("social_auth")) {
        principal = "principal.username";
    }
    pageContext.setAttribute("principal", principal);
%>
<%-- Calling static methods is not supported in all versions of EL without
     explicitly defining a function in an external taglib XML file. Using
     Spring's SpEL instead to keep it short for this one function call --%>
<s:eval var="rightLogo" expression="T(org.mskcc.cbio.portal.util.GlobalProperties).getRightLogo()"/>
<s:eval var="samlLogoutLocal" expression="T(org.mskcc.cbio.portal.util.GlobalProperties).getSamlIsLogoutLocal()"/>

<c:url var="samlLogoutUrl" value="/saml/logout">
    <c:param name="local" value="${samlLogoutLocal}" />
</c:url>

<style type="text/css">
.identity > a {
    color: #3786C2;
}

.identity .login {
    color: #3786C2;
    cursor: pointer;
}

.identity .login:hover{
    text-decoration: underline !important;
}
</style>

<script type="text/javascript">
function openSoicalAuthWindow() {
    var _window = open('login.jsp', '', 'width=1000, height=800');

    var interval = setInterval(function() {
        try {
            if (_window.closed) {
                clearInterval(interval);
            } else if (_window.document.URL.includes(location.origin) &&
                        !_window.document.URL.includes(location.origin + '/auth') &&
                        !_window.document.URL.includes('login.jsp')) {
                _window.close();

                setTimeout(function() {
                    clearInterval(interval);
                    if(window.location.pathname.includes('/study')) {
                        $('#rightHeaderContent').load(' #rightHeaderContent');
                        iViz.vue.manage.getInstance().showSaveButton= true
                    } else {
                        location.reload();
                    }
                }, 500);
            }
        } catch (err) {
            console.log('Error while monitoring the Login window: ', err);
        }
    }, 500);
};

function constructDownloadableTokenFile(tokenResponse) {
    returnString = "Your token is contained below. All previous tokens are now invalid.\n" + 
                   "Created: " + tokenResponse["creation"] + "\n" + 
                   "Expires: " + tokenResponse["expiration"] + "\n" +
                   "===============================================\n" +
                   "Token: " + tokenResponse["token"];
    return returnString;
}

function initiateTokenFileDownload(tokenFileContent) {
    var opts = {
        filename: "cbioportal_data_access_token.txt", // download file name
        contentType: "text/plain", // download data type,
        dataType: null,      // servlet data type
        servletName: null,   // name of the data/conversion servlet (optional)
        servletParams: null, // servlet parameters (optional)
        preProcess: null,   // pre-process function for the provided data
        postProcess: null // post-process function for the data returned by the server (optional)
    };
    console.log("DOWNLOADING");
    cbio.download.initDownload(tokenFileContent, opts);    
}
 
window.onReactAppReady(function() {
    $('#requestDataAccessToken').click(function() {
        // Make call to api endpoint for token
        // Check response for status code or error
        // Construct output string from JSON response (with token info)
        // Transfer output string to user 
            
        //window.cbioURL is defined in header.jsp but not set when we reach this point
        window.cbioURL =  window.location.origin + window.location.pathname.substring(0, window.location.pathname.indexOf("/",2))+'/';
        console.log("TEST MESSAGE FOR WINDOW.CBIOURL" + window.cbioURL);
        $.ajax({
            type: 'POST',
            url: window.cbioURL + 'api-legacy/dataAccessToken?allowRevocationOfOtherTokens=true',
            dataType: 'json',
            contentType: "application/json; charset=utf-8",
        }).then(function(data) {
            var downloadContent = constructDownloadableTokenFile(data);
            initiateTokenFileDownload(downloadContent);    
        }).fail(function() {
            initiateTokenFileDownload("Error: Data access tokens feature is disabled for this account.");    
        });
    });
});
</script>

<header>
        <div id="leftHeaderContent">
        <a id="cbioportal-logo" href="index.do"><img src="<c:url value="/images/cbioportal_logo.png"/>" alt="cBioPortal Logo" /></a>    
    
        <nav id="main-nav">
            <ul>
                <% if (GlobalProperties.showDataTab()) { %>
                <li class="internal">
                    <a href="data_sets.jsp">Data Sets</a>
                </li>
                <% } %>
                <%
                    //  Hide the Web API and R/MAT Tabs if the Portal Requires Authentication
                    if (!GlobalProperties.usersMustAuthenticate()) {
                %>
                <!-- Added call GlobalProperties to check whether to show the Web API tab -->
                <% if (GlobalProperties.showWebApiTab()) { %>
                <li class="internal">
                    <a href="web_api.jsp">Web API</a>
                </li>
                <% } %>
                <!-- Added call GlobalProperties to check whether to show the R Matlab tab -->
                <% if (GlobalProperties.showRMatlabTab()) { %>
                <li class="internal">
                    <a href="cgds_r.jsp">R/MATLAB</a>
                </li>
                <% } %>
                <% } %>
                <!-- Added call GlobalProperties to check whether to show the Tutorials tab -->
                <% if (GlobalProperties.showTutorialsTab()) { %>
                <li class="internal">
                    <a href="tutorial.jsp">Tutorials</a>
                </li>
                <% } %>
                <!-- Added call GlobalProperties to check whether to show the Faqs tab -->
                <% if (GlobalProperties.showFaqsTab()) { %>
                <li class="internal">
                    <a href="faq.jsp">FAQ</a>
                </li>
                <% } %>
                <% if (GlobalProperties.showNewsTab()) { %>
                <li class="internal">
                    <a href="news.jsp">News</a>
                </li>
                <% } %>
                <!-- Added call GlobalProperties to check whether to show the Tools tab -->
                <% if (GlobalProperties.showToolsTab()) { %>
                <li class="internal">
                    <a href="tools.jsp">Visualize Your Data</a>
                </li>
                <% } %>
                <!-- Added call GlobalProperties to check whether to show the About tab -->
                <% if (GlobalProperties.showAboutTab()) { %>
                <li class="internal">
                    <a href="about_us.jsp">About</a>
                </li>
                <% } %>
                <!-- Added for adding custom header tabs. If the customPageArray is not
                null, creates list items for the elements in the array. -->
                <%
                String [] customPagesArray = GlobalProperties.getCustomHeaderTabs();
                if(customPagesArray!=null){
                    // as the customPagesArray should have an even length, there's a problem
                    // if the length is uneven. In that case, don't add the last page.
                    // This way, the user will still get feedback for the other customPages
                    int until=customPagesArray.length - customPagesArray.length%2;
                    for(int i=0; i<until; i=i+2){ %>
                        <li class="internal">
                            <a href="<%=customPagesArray[i].trim()%>"><%=customPagesArray[i+1].trim()%></a>
                        </li>
                    <%}
                }%>
            </ul>
        </nav>
        </div>

        <div id="rightHeaderContent">
        <%-- Display Sign Out Button for Real (Non-Anonymous) User --%>
	        <sec:authorize access="!hasRole('ROLE_ANONYMOUS')">
	            <div class="identity">Logged in as <sec:authentication property="${principal}" />&nbsp;|&nbsp;
                        <a id="requestDataAccessToken">Get Token</a> 
	            <c:choose>
	                <c:when test="${authenticationMethod == 'saml'}">
	                    <a href="${samlLogoutUrl}">Sign out</a>
	                </c:when>
	                <c:otherwise>
	                    <a href="j_spring_security_logout">Sign out</a>
	                </c:otherwise>
	            </c:choose>
	            </div>
	        </sec:authorize>
        
	        <% if (authenticationMethod.equals("social_auth")) { %>
	        
		        <sec:authorize access="hasRole('ROLE_ANONYMOUS')">
		            <div class="identity">
		                &nbsp;
		                <span
		                    class="login"
		                    title="Optional login via Google allows you to save cohorts"
		                    onclick="openSoicalAuthWindow();">
		                    Login
		                </span>
		                &nbsp;&nbsp;
		            </div>
	            </sec:authorize>
	            
	        <% } %>
	        
	        

        <c:if test="${rightLogo != ''}">
            <img id="institute-logo" src="<c:url value="${rightLogo}"/>" alt="Institute Logo" />
        </c:if>
        </div>

    </header>
