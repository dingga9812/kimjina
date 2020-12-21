<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
    
    <!-- jsp: JavaServerPage -->
    <%-- jsp에서 자바프로그램을 사용할 때 쓰는 태그'<% %>' 감싸주게 됨 --%>
    <%-- 자바 주석 시작
    @ page:골뱅이=애노테이션-페이지 지시자 랭귀지:자바, 
    콘텐츠 타입:text/html, 문자셋:유니코드, 페이지인코딩:유니코드 
    자바 주석 끝--%>

<!-- HomeController.java 에서 index.jsp로 매칭 시킨 결과가 나오는 페이지 입니다.<br>
- HTML에서는 /resoureces/sample/index.html 이렇게 접근,<br>
- jsp프로그램에서는 위 html처럼 바로접근 못하고, 홈 컨트롤러자바를 거쳐서 index.jsp를 접근하는 이유는 보안때문 입니다.<br>
- 그래서, 은행, 관공서, 대학에서 대부분 사용합니다.(스프링을 배우는 목적)<br>
- 다른 예로, php프로그램은 위에 있는 html처럼 파일에 바로 접근이 가능합니다.(그래서, 중소기업홈페이지에서 사용)<br>
- 출발은 자바(pc용애플리케이션,웹프로그램 만드는데 모두 사용)<br>
- 자바에서 출발한 스프링프레임워크: 자바에서 웹프로그램 만드는데 특화된 라이브러리(프로그램묶음)를 구조화시키기<br>
- 개발자가 편하게 만들어 놓은 것이 스프링프레임워크.(현재는 버전5.x, 우리는4.3.22버전 사용 예정)<br>
- 그럼, jsp는 뭐냐: 지금 보이는 프로젝트는 스프링MVC프로젝트임<br>
- 여기서MVC View단에 해당하는 기술 부분이 jsp.<br>
- MVC = model(데이터베이스) - View(jsp페이지) <-> controller(컨트롤러java클래스)<br>
- org.edu.controller 패키지명: 자바클래스가 있는 폴더 경로<br>
- 패키지 사용 이유: 자바클래스를 기능별로 묶어 놓기 위해 명시적으로 만든 묶음.<br>  -->

<%@ include file="./include/header.jsp" %>
<link rel="stylesheet" href="https://cdn.jsdelivr.net/npm/bootstrap@4.5.3/dist/css/bootstrap.min.css">
<script src="https://cdn.jsdelivr.net/npm/bootstrap@4.5.3/dist/js/bootstrap.bundle.min.js"></script>
<!-- 위 부트스트랩 코어 입니다. -->

<section><!-- division디비전이라는 div 영역표시 태그 대신에 사용 -->
<!-- 본문 슬라이드 영역 시작 -->
<!-- 이미지를 불러오는 태그 img -->
<!-- 경로:상대(./img/...),절대경로(/resources/sample/img/...) -->
<!-- 태그안쪽에 지정한 스타일을 인라인스타일 -->
<div id="carouselExampleIndicators" class="carousel slide" data-ride="carousel">
  <ol class="carousel-indicators">
    <li data-target="#carouselExampleIndicators" data-slide-to="0" class="active"></li>
    <li data-target="#carouselExampleIndicators" data-slide-to="1"></li>
    <li data-target="#carouselExampleIndicators" data-slide-to="2"></li>
  </ol>
  <div class="carousel-inner">
    <div class="carousel-item active">
      <img src="/resources/sample/img/ippari1.png" class="d-block w-100" alt="...">
    </div>
    <div class="carousel-item">
      <img src="/resources/sample/img/ippari2.png" class="d-block w-100" alt="...">
    </div>
    <div class="carousel-item">
      <img src="/resources/sample/img/ippari3.png" class="d-block w-100" alt="...">
    </div>
  </div>
  <a class="carousel-control-prev" href="#carouselExampleIndicators" role="button" data-slide="prev">
    <span class="carousel-control-prev-icon" aria-hidden="true"></span>
    <span class="sr-only">Previous</span>
  </a>
  <a class="carousel-control-next" href="#carouselExampleIndicators" role="button" data-slide="next">
    <span class="carousel-control-next-icon" aria-hidden="true"></span>
    <span class="sr-only">Next</span>
  </a>
</div>
<!-- 본문 슬라이드 영역 끝 -->
</section>

<%@ include file="./include/footer.jsp" %>
