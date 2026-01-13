<%@ page contentType="text/html; charset=UTF-8" %>
<%@ page import="ro.catalog.model.AuthUser" %>
<%
  AuthUser u = (AuthUser) session.getAttribute("authUser");
%>
<!DOCTYPE html>
<html>
<head>
  <meta charset="UTF-8" />
  <title>Dashboard</title>
  <link rel="stylesheet" href="<%= request.getContextPath() %>/assets/css/app.css">
</head>
<body>

<div class="topbar">
  <div style="display:flex; gap:10px; align-items:center;">
    <b>Catalog Școlar</b>
    <span class="pill"><%= u.getRol() %></span>
  </div>
  <div style="display:flex; gap:10px; align-items:center;">
    <span style="color:#475569;"><%= u.getNume() %> (<%= u.getEmail() %>)</span>
    <a href="<%= request.getContextPath() %>/logout">Logout</a>
  </div>
</div>

<div class="page">
  <div class="grid">
    <div class="tile">
      <b>Studenți</b>
      <div style="color:#64748b; margin-top:6px; font-size:13px;">Listare / adăugare / modificare</div>
      <div style="margin-top:12px;">
        <a class="btn" style="display:inline-block; width:auto;" href="<%= request.getContextPath() %>/studenti">Deschide</a>
      </div>
    </div>

    <div class="tile">
      <b>Materii</b>
      <div style="color:#64748b; margin-top:6px; font-size:13px;">CRUD materii</div>
      <div style="margin-top:12px;">
        <a class="btn" style="display:inline-block; width:auto;" href="<%= request.getContextPath() %>/materii">Deschide</a>
      </div>
    </div>

    <div class="tile">
      <b>Note / Absențe</b>
      <div style="color:#64748b; margin-top:6px; font-size:13px;">Note, absențe, rapoarte</div>
      <div style="margin-top:12px;">
        <a class="btn" style="display:inline-block; width:auto;" href="<%= request.getContextPath() %>/note">Deschide</a>
      </div>
    </div>
  </div>
</div>

</body>
</html>
