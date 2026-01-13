<%@ page contentType="text/html; charset=UTF-8" %>
<!DOCTYPE html>
<html>
<head>
    <title>Catalog Online</title>
</head>
<body>

<h1>Catalog Online</h1>

<p>${mesaj}</p>
<%
  response.sendRedirect(request.getContextPath() + "/login");
%>

</body>
</html>
