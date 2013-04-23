<%@ page language="java" contentType="text/html; charset=utf-8" pageEncoding="utf-8"%>
<%@ page import="java.util.List"%>
<%@ include file="includes/head.jsp"%>
<%@ include file="includes/top.jsp"%>

    <div class="container">

      <!-- Main hero unit for a primary marketing message or call to action -->
      <div class="hero-unit">
        <p><h1>radix want to access your resource...</h1></p>
        <p></p>
      </div>
	  
		<form name="confirmationForm"  class="well" method="post" action="/oauth/login">
		<input type="hidden" name="redirect_url" value="<%=request.getAttribute("redirect_url")%>" />
    <div class="row">
        <input id="user_oauth_approval" name="user_oauth_approval" value="true" type="hidden"/>
        
    </div>
    <div class="hero-unit">

		<input name="userid" type="text" placeholder="your id" value="" />
		<input name="userpw" type="password" placeholder="your password" value="pudding" readonly />
		<input name="authorize" value="Authorize" type="submit"
               onclick="$('#user_oauth_approval').attr('value',true)" class="btn" />

        &nbsp;
        <input name="deny" value="Deny" type="submit" onclick="$('#user_oauth_approval').attr('value',false)"
               class="btn" />
        <br/>
        scopes:
    <%	List<String> scopeList = (List<String>) request.getAttribute("scopeList"); 
    	for(String scope : scopeList) {
    %>
        <input type="checkbox" name="scope" checked="true" value="<%=scope%>" /> <%=scope%>
    <%	} %>
    </div>
</form>

	</div>
<%@ include file="includes/foot.jsp"%>