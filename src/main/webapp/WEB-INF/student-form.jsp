<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ page import="ro.catalog.model.Student" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>

<%
  String mode = (String) request.getAttribute("mode");
  if (mode == null) mode = "create";
  boolean edit = "edit".equals(mode);

  Student st = (Student) request.getAttribute("student");
  if (st == null) st = new Student();

  request.setAttribute("edit", edit);
  request.setAttribute("st", st);
%>

<!DOCTYPE html>
<html>
<head>
  <meta charset="UTF-8">
  <title>${edit ? "Edit student" : "Adaugă student"}</title>

  <link rel="stylesheet" href="${pageContext.request.contextPath}/assets/css/app.css">

  <style>
    .wrap{max-width:900px;margin:0 auto;padding:18px}
    .head{display:flex;align-items:flex-start;justify-content:space-between;gap:12px;flex-wrap:wrap}
    .title{font-size:28px;font-weight:900;letter-spacing:-.02em}
    .subtitle{color:var(--muted);font-size:13px;margin-top:6px;line-height:1.4}

    .cardX{
      background:rgba(255,255,255,.86);
      border:1px solid rgba(229,231,235,.95);
      border-radius:var(--radius);
      box-shadow: var(--shadow);
      padding:16px;
    }

    .gridF{
      display:grid;
      grid-template-columns: 1.4fr 1.4fr 1fr .8fr;
      gap:12px;
      align-items:end;
    }
    @media (max-width: 980px){
      .gridF{grid-template-columns: 1fr; }
    }

    .f{display:flex;flex-direction:column;gap:6px}
    .f label{font-size:12px;color:var(--muted);font-weight:800}
    .f input,.f select{
      background:#fff;border:1px solid rgba(229,231,235,.95);
      padding:10px 10px;border-radius:12px;outline:none
    }
    .f input:focus,.f select:focus{border-color:rgba(79,70,229,.6);box-shadow:0 0 0 4px rgba(79,70,229,.12)}

    .btnS{
      cursor:pointer;border:0;
      padding:12px 16px;border-radius:12px;
      font-weight:900;
      background: linear-gradient(135deg, var(--primary), var(--primary2));
      color:#fff;
      box-shadow: 0 12px 26px rgba(79,70,229,.25);
      text-decoration:none;
      display:inline-flex;align-items:center;justify-content:center;gap:8px;
      white-space:nowrap;
    }
    .btnS.secondary{
      background:rgba(255,255,255,.85);
      border:1px solid rgba(229,231,235,.95);
      color:var(--primary);
      box-shadow: 0 14px 34px rgba(15,23,42,.08);
    }

    .miniTop{display:flex;gap:10px;align-items:center;flex-wrap:wrap}
    .miniTop a{font-weight:900}
  </style>
</head>

<body>

<div class="topbar">
  <div class="miniTop">
    <b>Catalog Școlar</b>
    <span class="pill">
      <c:choose>
        <c:when test="${not empty sessionScope.authUser}">${sessionScope.authUser.rol}</c:when>
        <c:otherwise>USER</c:otherwise>
      </c:choose>
    </span>
    <a href="${pageContext.request.contextPath}/app/dashboard">↩ Dashboard</a>
  </div>
  <div style="display:flex; gap:10px; align-items:center;">
    <c:if test="${not empty sessionScope.authUser}">
      <span style="color:#475569;">${sessionScope.authUser.nume} (${sessionScope.authUser.email})</span>
      <a href="${pageContext.request.contextPath}/logout">Logout</a>
    </c:if>
    <c:if test="${empty sessionScope.authUser}">
      <a href="${pageContext.request.contextPath}/login">Login</a>
    </c:if>
  </div>
</div>

<div class="wrap">

  <div class="head">
    <div>
      <div class="title">${edit ? "✏️ Edit student" : "➕ Adaugă student"}</div>
      <div class="subtitle">Completează datele și salvează.</div>
    </div>
    <a class="btnS secondary" href="${pageContext.request.contextPath}/studenti">Înapoi la listă</a>
  </div>

  <div style="height:14px"></div>

  <div class="cardX">
    <form method="post" action="${pageContext.request.contextPath}/studenti">
      <input type="hidden" name="action" value="${edit ? "update" : "create"}"/>
      <c:if test="${edit}">
        <input type="hidden" name="id" value="${st.id}"/>
      </c:if>

      <div class="gridF">
        <div class="f">
          <label>Nume</label>
          <input name="nume" required value="${st.nume}"/>
        </div>

        <div class="f">
          <label>Email</label>
          <input name="email" type="email" required value="${st.email}"/>
        </div>

        <div class="f">
          <label>Clasă</label>
          <select name="clasaId">
            <option value="">(Nesetat)</option>
            <c:forEach var="c" items="${clase}">
              <option value="${c.id}" <c:if test="${st.clasaId != null && st.clasaId == c.id}">selected</c:if>>
                ${c.nume}
              </option>
            </c:forEach>
          </select>
        </div>

        <div class="f">
          <label>Vârstă</label>
          <input name="varsta" type="number" min="1" max="120" required value="${st.varsta}"/>
        </div>
      </div>

      <div style="display:flex; gap:12px; justify-content:flex-end; flex-wrap:wrap; margin-top:14px;">
        <button class="btnS" type="submit">Salvează</button>
        <a class="btnS secondary" href="${pageContext.request.contextPath}/studenti">Renunță</a>
      </div>
    </form>
  </div>

</div>

</body>
</html>
