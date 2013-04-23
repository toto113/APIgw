<%@ page language="java" contentType="text/html; charset=utf-8" pageEncoding="utf-8"%>
<%@ include file="includes/head.jsp"%>
<%@ include file="includes/top.jsp"%>
<script>
function call() {
	$( "#result" ).empty();
	$.ajax({
		type: 'POST',
		url: '/radix',
		data: { authorization: $("#authorization").val(), uri: $("#uri").val(), method: $("#method").val() },
		dataType: "text",
		timeout: 10000,
		contentType: "application/x-www-form-urlencoded;charset=utf-8",
		success: function(data) {
			$( "#result" ).empty().append( data );
		},
		error: function(data) {
			alert($(data).val());
		}
	});
}

function access(){
	document.accessform.submit();
}
</script>
    <div class="container">

      <!-- Main hero unit for a primary marketing message or call to action -->
      <div class="hero-unit">
        <h1>Welcome, Bros!!</h1>
        <p><br/></p>
        <p>This is a sample site for showing how to authorize client requests to remote resoures using OAuth 2.0</p>
        <p>	
        <form class="well" action="/access" method="get" name="accessform">
        APIKey : <input id="apikey" name="apikey" type="text" value="0fe145b9-c753-11e1-ade2-12313f062e84"  /> <br>
        Secret : <input id="secret" name="secret" type="text" value="490e2b9bcd80cabf88316212c820be546ce86eca"  /> <br>
        Scope : <input id="scope" name="scope" type="text" value="photos,users"  /> <br>
        	<% if(request.getSession().getAttribute("accessToken") != null) { %>
        		your access token is <%=request.getSession().getAttribute("accessToken") %>
        		<a class="btn btn-primary btn-large"  onClick="access()">refresh Access Token >></a>
        	<% } else { %>
        		you have no acces token
        		<a class="btn btn-primary btn-large"  onClick="access()">get new Access Token >></a>
        	<% } %>
        </form>
        </p>
      </div>
Basic MGZlMTQ1YjktYzc1My0xMWUxLWFkZTItMTIzMTNmMDYyZTg0OjQ5MGUyYjliY2Q4MGNhYmY4ODMx
NjIxMmM4MjBiZTU0NmNlODZlY2E=
      <!-- Example row of columns -->
      <div class="hero-unit">
      	<form class="well" action="/radix" method="post" id="form">
      		<label>HTTP Header Authorization</label>
      		<input id="authorization" name="authorization" type="text" class="span3"  value="" />
      		<label>uri</label>
      		<input id="uri" name="uri" type="text" class="span3" placeholder="https://api.pudding.to/v1/users/1234?access_token=<%=request.getSession().getAttribute("accessToken") %>" value="https://api.pudding.to/v1/users/1234?access_token=<%=request.getSession().getAttribute("accessToken") %>" />
      		<label>method</label>
      		<select id="method" name="method">
      			<option value="GET">GET</option>
      			<option value="POST">POST</option>
      			<option value="PUT">PUT</option>
      			<option value="DELETE">DELETE</option>
      		</select>
      		
      		<button type="button" class="btn" onclick="call()">Submit</button>
      		
      	</form>
        <div class="hero-unit">
        	<h2>result</h2>
			<div class="well" id="result">
			</div>
        </div>
      </div>

<%@ include file="includes/foot.jsp"%>