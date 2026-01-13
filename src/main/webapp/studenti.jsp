<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>

<!DOCTYPE html>
<html>
<head>
  <meta charset="UTF-8">
  <title>Studenți</title>

  <link rel="stylesheet" href="${pageContext.request.contextPath}/assets/css/app.css">

  <style>
    .wrap{max-width:1150px;margin:0 auto;padding:18px}
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

    .filters{
      display:grid;
      grid-template-columns: 1.4fr .9fr .9fr 1fr auto;
      gap:12px;
      align-items:end;
    }
    @media (max-width: 1100px){
      .filters{grid-template-columns: repeat(2,minmax(0,1fr));}
      .filters .actions{grid-column:1/-1; display:flex; justify-content:flex-end; gap:12px; flex-wrap:wrap}
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
    .btnS.danger{
      background:rgba(239,68,68,.10);
      border:1px solid rgba(239,68,68,.20);
      color:#991b1b;
      box-shadow:none;
    }

    table{width:100%;border-collapse:separate;border-spacing:0 10px}
    th{color:var(--muted);font-size:12px;text-align:left;padding:0 10px}
    td{padding:12px 10px;background:rgba(255,255,255,.85);border:1px solid rgba(229,231,235,.95);vertical-align:middle}
    tr td:first-child{border-top-left-radius:var(--radius);border-bottom-left-radius:var(--radius)}
    tr td:last-child{border-top-right-radius:var(--radius);border-bottom-right-radius:var(--radius)}

    .pill2{
      display:inline-flex;padding:6px 10px;border-radius:999px;
      background:rgba(79,70,229,.10);color:rgba(79,70,229,1);
      font-weight:900;font-size:12px;border:1px solid rgba(79,70,229,.18)
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
      <div class="title">🎓 Studenți</div>
      <div class="subtitle">Căutare / filtrare + administrare studenți.</div>
    </div>

    <a class="btnS" href="${pageContext.request.contextPath}/studenti?action=new">+ Adaugă student</a>
  </div>

  <div style="height:14px"></div>

  <!-- FILTRE -->
  <div class="cardX">
    <form method="get" action="${pageContext.request.contextPath}/studenti">
      <input type="hidden" name="action" value="list"/>

      <div class="filters">
        <div class="f">
          <label>Caută nume/email</label>
          <input name="q" value="${q}" placeholder="ex: Popescu / ana@student.ro"/>
        </div>

        <div class="f">
          <label>Vârsta min</label>
          <input name="minVarsta" value="${minVarsta}" type="number" min="1" max="120" placeholder="ex: 16"/>
        </div>

        <div class="f">
          <label>Vârsta max</label>
          <input name="maxVarsta" value="${maxVarsta}" type="number" min="1" max="120" placeholder="ex: 20"/>
        </div>

        <div class="f">
          <label>Clasă</label>
          <select name="clasaId">
            <option value="">Toate</option>
            <c:forEach var="c" items="${clase}">
              <option value="${c.id}" <c:if test="${clasaId == ('' + c.id)}">selected</c:if>>
                ${c.nume}
              </option>
            </c:forEach>
          </select>
        </div>

        <div class="actions" style="display:flex; gap:12px; justify-content:flex-end; flex-wrap:wrap;">
          <button class="btnS" type="submit">Caută / Filtrează</button>
          <a class="btnS secondary" href="${pageContext.request.contextPath}/studenti">Reset</a>
        </div>
      </div>
    </form>
  </div>

  <div style="height:12px"></div>

  <!-- LISTA -->
  <div class="cardX">
    <div style="display:flex; align-items:center; justify-content:space-between; gap:12px; flex-wrap:wrap;">
      <div style="font-weight:900;">📋 Lista studenților</div>
      <div class="pill2">
        <c:choose>
          <c:when test="${not empty studenti}">${fn:length(studenti)} înregistrări</c:when>
          <c:otherwise>0 înregistrări</c:otherwise>
        </c:choose>
      </div>
    </div>

    <div style="height:10px"></div>

    <c:if test="${empty studenti}">
      <div style="color:var(--muted); font-size:13px;">Nu există studenți pentru filtrele curente.</div>
    </c:if>

    <c:if test="${not empty studenti}">
      <table>
        <thead>
        <tr>
          <th>ID</th>
          <th>Nume</th>
          <th>Email</th>
          <th>Clasă</th>
          <th>Vârstă</th>
          <th>Acțiuni</th>
        </tr>
        </thead>
        <tbody>
        <c:forEach var="s" items="${studenti}">
          <tr>
            <td>${s.id}</td>
            <td><b>${s.nume}</b></td>
            <td>${s.email}</td>
            <td>
              <c:choose>
                <c:when test="${not empty s.clasaNume}">${s.clasaNume}</c:when>
                <c:otherwise>-</c:otherwise>
              </c:choose>
            </td>
            <td>${s.varsta}</td>
            <td style="display:flex; gap:8px; flex-wrap:wrap;">
              <a class="btnS secondary" href="${pageContext.request.contextPath}/studenti?action=edit&id=${s.id}">Edit</a>
              <a class="btnS danger" href="${pageContext.request.contextPath}/studenti?action=delete&id=${s.id}"
                 onclick="return confirm('Sigur ștergi studentul?');">Delete</a>
            </td>
          </tr>
        </c:forEach>
        </tbody>
      </table>
    </c:if>
  </div>

</div>

</body>
</html>
