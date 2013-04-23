<%@ page language="java" contentType="text/html; charset=utf-8" pageEncoding="utf-8"%>
<%@ include file="includes/head.jsp"%>
<%@ include file="includes/top.jsp"%>

    <div class="container">

      <!-- Main hero unit for a primary marketing message or call to action -->
      <div class="hero-unit">
        <p><h1>Receiving token...</h1></p>
        <p><a class="btn btn-primary btn-large" onclick="setToken()">continue!! &raquo;</a></p>
      </div>
	  
	  <script type="text/javascript">
	    //setTimeout("setToken()", 2000);
	  	function setToken() {
          var token = window.location.href.split("access_token=")[1];
          //alert("/oauth/auth/" + token);
          window.location = "/oauth/auth/" + token;
        }
      </script>

	</div>
<%@ include file="includes/foot.jsp"%>