<%@ page contentType="text/html; charset=UTF-8" %>
<%@ page import="ro.catalog.model.AuthUser" %>
<%
  AuthUser u = (AuthUser) session.getAttribute("authUser");
%>
<!DOCTYPE html>
<html>
<head>
  <meta charset="UTF-8" />
  <title>Dashboard Profesor</title>
  <link rel="stylesheet" href="<%= request.getContextPath() %>/assets/css/app.css">
</head>
<body>

<div class="topbar">
  <div style="display:flex; gap:10px; align-items:center;">
    <b>Catalog Școlar</b>
    <span class="pill">PROFESOR</span>
  </div>
  <div style="display:flex; gap:10px; align-items:center;">
    <span style="color:#475569;"><%= u.getNume() %> (<%= u.getEmail() %>)</span>
    <a href="<%= request.getContextPath() %>/logout">Logout</a>
  </div>
</div>

<div class="page">
  <div class="grid">
    <div class="tile">
      <b>Note</b>
      <div style="color:#64748b; margin-top:6px; font-size:13px;">Adăugare + filtrare + export</div>
      <div style="margin-top:12px;">
        <a class="btn" style="display:inline-block; width:auto;" href="<%= request.getContextPath() %>/app/profesor/note">Deschide</a>
      </div>
    </div>

    <div class="tile">
      <b>Absențe</b>
      <div style="color:#64748b; margin-top:6px; font-size:13px;">Gestionare + export</div>
      <div style="margin-top:12px;">
        <a class="btn" style="display:inline-block; width:auto;" href="<%= request.getContextPath() %>/app/profesor/absente">Deschide</a>
      </div>
    </div>

    <div class="tile">
      <b>Rapoarte & Grafice</b>
      <div style="color:#64748b; margin-top:6px; font-size:13px;">Perioadă + diagramă + export</div>
      <div style="margin-top:12px;">
        <a class="btn" style="display:inline-block; width:auto;" href="<%= request.getContextPath() %>/app/profesor/raport">Deschide</a>
      </div>
    </div>
  </div>
</div>

</body>
</html>
