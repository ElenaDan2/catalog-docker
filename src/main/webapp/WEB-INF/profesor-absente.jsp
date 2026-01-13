<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@ page import="java.time.LocalDate" %>

<!DOCTYPE html>
<html>
<head>
  <meta charset="UTF-8"/>
  <title>Profesor - Absențe</title>
  <link rel="stylesheet" href="${pageContext.request.contextPath}/assets/css/app.css">

  <style>
    .wrap{max-width:1150px;margin:0 auto;padding:18px}
    .head{display:flex;align-items:flex-start;justify-content:space-between;gap:12px;flex-wrap:wrap}
    .title{font-size:28px;font-weight:900;letter-spacing:-.02em}
    .subtitle{color:var(--muted);font-size:13px;margin-top:6px;line-height:1.4}

    .cardX{background:rgba(255,255,255,.86);border:1px solid rgba(229,231,235,.95);border-radius:var(--radius);box-shadow: var(--shadow);padding:16px;}

    .filters{display:grid;grid-template-columns: 1fr 1fr 1.4fr 1fr 1fr auto;gap:12px;align-items:end;}
    @media (max-width: 1100px){
      .filters{grid-template-columns: repeat(2,minmax(0,1fr));}
      .filters .actions{grid-column:1/-1; display:flex; justify-content:flex-end; gap:12px}
    }

    .f{display:flex;flex-direction:column;gap:6px}
    .f label{font-size:12px;color:var(--muted);font-weight:800}
    .f input,.f select{background:#fff;border:1px solid rgba(229,231,235,.95);padding:10px 10px;border-radius:12px;outline:none}
    .f input:focus,.f select:focus{border-color:rgba(79,70,229,.6);box-shadow:0 0 0 4px rgba(79,70,229,.12)}

    .btnS{cursor:pointer;border:0;padding:12px 16px;border-radius:12px;font-weight:900;background: linear-gradient(135deg, var(--primary), var(--primary2));color:#fff;box-shadow: 0 12px 26px rgba(79,70,229,.25);text-decoration:none;display:inline-flex;align-items:center;justify-content:center;gap:8px;white-space:nowrap;}
    .btnS.secondary{background:rgba(255,255,255,.85);border:1px solid rgba(229,231,235,.95);color:var(--primary);box-shadow: 0 14px 34px rgba(15,23,42,.08);}

    table{width:100%;border-collapse:separate;border-spacing:0 10px}
    th{color:var(--muted);font-size:12px;text-align:left;padding:0 10px}
    td{padding:12px 10px;background:rgba(255,255,255,.85);border:1px solid rgba(229,231,235,.95);vertical-align:middle}
    tr td:first-child{border-top-left-radius:var(--radius);border-bottom-left-radius:var(--radius)}
    tr td:last-child{border-top-right-radius:var(--radius);border-bottom-right-radius:var(--radius)}

    .pill2{display:inline-flex;padding:6px 10px;border-radius:999px;background:rgba(79,70,229,.10);color:rgba(79,70,229,1);font-weight:900;font-size:12px;border:1px solid rgba(79,70,229,.18)}
    .pill2.bad{background:rgba(239,68,68,.10);border-color:rgba(239,68,68,.20);color:#991b1b}
    .pill2.good{background:rgba(34,197,94,.12);border-color:rgba(34,197,94,.22);color:#166534}

    .miniTop{display:flex;gap:10px;align-items:center;flex-wrap:wrap}
    .miniTop a{font-weight:900}
    .danger{background:rgba(239,68,68,.10);border:1px solid rgba(239,68,68,.20);color:#991b1b}
    .danger:hover{opacity:.9}
  </style>
</head>

<body>

<div class="topbar">
  <div class="miniTop">
    <b>Catalog Școlar</b>
    <span class="pill">PROFESOR</span>
    <a href="${pageContext.request.contextPath}/app/dashboard">↩ Dashboard</a>
  </div>
  <div style="display:flex; gap:10px; align-items:center;">
    <span style="color:#475569;">${user.nume} (${user.email})</span>
    <a href="${pageContext.request.contextPath}/logout">Logout</a>
  </div>
</div>

<div class="wrap">

  <div class="head">
    <div>
      <div class="title">🚫 Absențe</div>
      <div class="subtitle">Filtrare pe clasă / nume + adăugare absențe doar la materia ta.</div>
    </div>
  </div>

  <div style="height:14px"></div>

  <c:if test="${not empty error}">
    <div class="error">${error}</div>
  </c:if>
  <c:if test="${not empty success}">
    <div class="success">${success}</div>
  </c:if>

  <!-- FILTRE (GET) -->
  <div class="cardX">
    <form method="get" action="${pageContext.request.contextPath}/app/profesor/absente">
      <div class="filters">
        <div class="f">
          <label>Materie</label>
          <select name="materieId">
            <c:forEach var="m" items="${materii}">
              <option value="${m.id}" <c:if test="${m.id == materieIdSel}">selected</c:if>>${m.nume}</option>
            </c:forEach>
          </select>
        </div>

        <div class="f">
          <label>Clasă</label>
          <select name="clasaId">
            <option value="">Toate</option>
            <c:forEach var="c" items="${clase}">
              <option value="${c.id}" <c:if test="${clasaIdSel == ('' + c.id)}">selected</c:if>>${c.an}${c.litera}</option>
            </c:forEach>
          </select>
        </div>

        <div class="f">
          <label>Caută după nume</label>
          <input name="q" value="${q}" placeholder="ex: Popescu"/>
        </div>

        <div class="f">
          <label>De la</label>
          <input type="date" name="from" value="${from}"/>
        </div>

        <div class="f">
          <label>Până la</label>
          <input type="date" name="to" value="${to}"/>
        </div>

        <div class="actions" style="display:flex; gap:12px; justify-content:flex-end; flex-wrap:wrap;">
          <button class="btnS" type="submit">Aplică</button>
          <a class="btnS secondary" href="${pageContext.request.contextPath}/app/profesor/absente">Reset</a>

          <a class="btnS secondary"
             href="${pageContext.request.contextPath}/app/profesor/export?entity=absente&fmt=pdf&materieId=${materieIdSel}&clasaId=${clasaIdSel}&q=${q}&from=${from}&to=${to}">
            Export PDF
          </a>

          <a class="btnS secondary"
             href="${pageContext.request.contextPath}/app/profesor/export?entity=absente&fmt=xls&materieId=${materieIdSel}&clasaId=${clasaIdSel}&q=${q}&from=${from}&to=${to}">
            Export Excel
          </a>
        </div>
      </div>
    </form>
  </div>

  <div style="height:12px"></div>

  <!-- ADAUGA ABSENTA (POST) -->
  <div class="cardX">
    <div style="font-weight:900; margin-bottom:10px;">➕ Adaugă absență</div>
    <form method="post" action="${pageContext.request.contextPath}/app/profesor/absente">
      <input type="hidden" name="action" value="add"/>
      <input type="hidden" name="materieId" value="${materieIdSel}"/>
      <input type="hidden" name="clasaId" value="${clasaIdSel}"/>
      <input type="hidden" name="q" value="${q}"/>
      <input type="hidden" name="from" value="${from}"/>
      <input type="hidden" name="to" value="${to}"/>

      <div class="filters" style="grid-template-columns: 2fr 1fr 1fr auto;">
        <div class="f">
          <label>Student</label>
          <select name="studentId" required>
            <option value="">Selectează...</option>
            <c:forEach var="s" items="${studenti}">
              <option value="${s.id}">${s.nume} <c:if test="${not empty s.clasaNume}">(${s.clasaNume})</c:if></option>
            </c:forEach>
          </select>
        </div>

        <div class="f">
          <label>Data</label>
          <input type="date" name="data" value="<%= LocalDate.now().toString() %>" required/>
        </div>

        <div class="f">
          <label>Motivată?</label>
          <select name="motivata">
            <option value="">Nemotivată</option>
            <option value="on">Motivată</option>
          </select>
        </div>

        <div class="actions" style="display:flex; gap:12px; justify-content:flex-end;">
          <button class="btnS" type="submit">Salvează</button>
        </div>
      </div>

      <div style="margin-top:8px; color:var(--muted); font-size:12px;">
        Materia selectată:
        <span class="pill2">
          <c:forEach var="m" items="${materii}">
            <c:if test="${m.id == materieIdSel}">${m.nume}</c:if>
          </c:forEach>
        </span>
        (doar materiile tale apar în listă).
      </div>
    </form>
  </div>

  <div style="height:12px"></div>

  <!-- LISTA ABSENTE -->
  <div class="cardX">
    <div style="display:flex; align-items:center; justify-content:space-between; gap:12px; flex-wrap:wrap;">
      <div style="font-weight:900;">📋 Absențe găsite</div>
      <div class="pill2">${fn:length(absente)} înregistrări</div>
    </div>

    <div style="height:10px"></div>

    <c:if test="${empty absente}">
      <div style="color:var(--muted); font-size:13px;">Nu există absențe pentru filtrele curente.</div>
    </c:if>

    <c:if test="${not empty absente}">
      <table>
        <thead>
        <tr>
          <th>Data</th>
          <th>Student</th>
          <th>Clasă</th>
          <th>Materie</th>
          <th>Status</th>
          <th>Acțiuni</th>
        </tr>
        </thead>
        <tbody>
        <c:forEach var="a" items="${absente}">
          <tr>
            <td><c:out value="${a.data}"/></td>
            <td><b><c:out value="${a.studentNume}"/></b></td>
            <td><c:out value="${a.clasaNume}"/></td>
            <td><c:out value="${a.materie}"/></td>
            <td>
              <span class="pill2 <c:if test='${a.motivata}'>good</c:if><c:if test='${!a.motivata}'>bad</c:if>">
                <c:if test="${a.motivata}">Motivată</c:if>
                <c:if test="${!a.motivata}">Nemotivată</c:if>
              </span>
            </td>
            <td style="display:flex; gap:8px; flex-wrap:wrap;">
              <form method="post" action="${pageContext.request.contextPath}/app/profesor/absente" style="margin:0">
                <input type="hidden" name="action" value="toggle"/>
                <input type="hidden" name="absentaId" value="${a.id}"/>
                <input type="hidden" name="motivata" value="${a.motivata ? 0 : 1}"/>
                <input type="hidden" name="materieId" value="${materieIdSel}"/>
                <input type="hidden" name="clasaId" value="${clasaIdSel}"/>
                <input type="hidden" name="q" value="${q}"/>
                <input type="hidden" name="from" value="${from}"/>
                <input type="hidden" name="to" value="${to}"/>
                <button type="submit" class="btnS secondary">
                  <c:if test="${a.motivata}">Marchează nemotivată</c:if>
                  <c:if test="${!a.motivata}">Marchează motivată</c:if>
                </button>
              </form>

              <form method="post" action="${pageContext.request.contextPath}/app/profesor/absente" style="margin:0">
                <input type="hidden" name="action" value="delete"/>
                <input type="hidden" name="absentaId" value="${a.id}"/>
                <input type="hidden" name="materieId" value="${materieIdSel}"/>
                <input type="hidden" name="clasaId" value="${clasaIdSel}"/>
                <input type="hidden" name="q" value="${q}"/>
                <input type="hidden" name="from" value="${from}"/>
                <input type="hidden" name="to" value="${to}"/>
                <button type="submit" class="btnS secondary danger" onclick="return confirm('Ștergi absența?');">Șterge</button>
              </form>
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
