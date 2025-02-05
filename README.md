<div align="center">
<h1> :envelope:온라인을 통해 만나는 아날로그한 편지:mailbox_with_mail: </h1>

<a href="https://www.toletter.co.kr" target="_blank" style="text-decoration: none; display:none"><img src="https://github.com/user-attachments/assets/14ffa9e7-3718-4af8-8935-d8ea6a63dcd0" alt="To.Letter Logo" width="600"></a>

  <h3>Welcome To TO.LETTER</h3>
  <div>가상의 방에서 소중한 사람에게 편지를 부쳐보세요!</div>
  <br/>
  
[![Visit](https://img.shields.io/badge/VISIT-TO.LETTER-FF4154?style=for-the-badge&logoColor=white)](https://www.toletter.co.kr)
</div>


## To Letter 소개
### 프로젝트 개요
+ 요즘은 이메일, 카톡 등 실시간으로 채팅을 주고 받는게 일상인 생활에서 주고 받는데 시간이 걸리는 편지를 인터넷 상으로 주고 받으면 좋을 것 같다는 생각으로 웹 사이트를 제작함.
+ 편지를 쓸 때의 주소와 편지함 주소의 거리 상으로 최소 1일에서 최대 5일 정도 편지를 받는데 시간이 걸림.

### 기술 스택
+ Spring Boot/JPA 
+ AWS EC2(Ubuntu) - 서버
+ AWS RDS - 클라우드 데이터베이스
+ MySQL - 데이터베이스
+ Redis - 인메모리 기반 데이터베이스
+ SSE - 서버에서 클라이언트로 실시간 이벤트를 전송하는 단방향 기술
+ Docker - 배포를 위한 컨테이너 가상화
+ github actions - 자동화 배포(CI/CD)
+ Swagger

### 서비스 역할 
* Alarm Service
> 1. SSE 방식을 이용한 실시간 알림 서비스 구현
> 2. 편지를 보내고 거리에 따른 1-5일 후 유저에게 도착 시, 실시간으로 알람 도착

* Email Service
> 1. 구글 이메일을 사용하여 유저에게 메일을 보내는 서비스 구현
> 2. 로컬 로그인 진행 시 2차 인증으로 이메일 보냄
> 3. 보낸 6자리 코드를 입력해야만 회원가입 완료됨

* GPS Service
> 1. 위도 경도를 구하고 그에 따른 거리를 구하는 서비스 구현
> 2. 회원가입 시 주소(우체통 위치)를 가지고 위도 경도로 변함
> 3. 위도 경도를 가지고 편지를 쓴 유저의 위치로 거리를 구하여 편지를 도착할 시간을 구함

* Kakao Service
> 1. 로컬 로그인을 제외한 카카오 로그인 시 필요한 서비스 구현
> 2. 회원가입, 로그인, 카카오 토큰 발급, 탈퇴 기능을 구현

* Letter Service
> 1. 편지쓰기 <br>
>  가. 편지를 쓰면 도착할 시간을 구해 데이터베이스에 저장을 하고 실시간 알람(1-5일 후)을 보냄 <br>
>  나. 유저가 보낸 편지를 저장할 지 선택하게 한 후 이에 따라 데이터베이스에 저장함 <br>
> 2. 편지 보기 <br>
>  가. 받은 모든 편지들 보여주기 <br>
>  나. 안 읽은 모든 편지들 보여주기 <br>
>  다. 읽은 모든 편지들 보여주기 <br>
>  라. 보낸 모든 편지들 보여주기 <br>
> 3. 편지 읽음 처리 및 삭제 

* User Service
> 1. 로컬 로그인과 카카오 로그인 지원함
> 2. SecurityConfig, JWTFilter, Cookie 등 보안 기능 구현
> 3. 내 정보 보여주기, 수정하기, 탈퇴, 로그아웃 등의 서비스 지원함

## 팀원 구성
<table>
  <tr>
    <td colspan="2" align="center"><strong>Front-end</strong></td>
    <td align="center"><strong>Back-end</strong></td>
  </tr>
  <tr>
    <td align="center">
      <a href="https://github.com/kimjeyoun">
        <img src="https://avatars.githubusercontent.com/u/63177849?v=4" width="160px;" alt=""/><br />
        <sub>
          <b>김재윤</b>
        </sub>
      </a><br />
    </td>
    <td align="center">
      <a href="https://github.com/JeongYunMi">
        <img src="https://avatars.githubusercontent.com/u/50102538?s=96&v=4" width="160px;" alt=""/><br />
        <sub>
          <b>정윤미</b>
        </sub>
      </a><br />
    </td>
    <td align="center">
      <a href="https://github.com/ovo1234">
        <img src="https://avatars.githubusercontent.com/u/79007447?v=4" width="160px;" alt=""/>
        <br />
        <sub>
          <b>이유정</b>
        </sub>
      </a><br />
    </td>
  </tr>
  <tr>
    <td rowspan="1" align="center">
      <a href="https://github.com/To-Letter/To-Letter-front/issues?q=is%3Aissue%20assignee%3Akimjeyoun" title="Code">issues</a>
    </td>
    <td rowspan="1" align="center">
      <a href="https://github.com/To-Letter/To-Letter-front/issues?q=is%3Aissue%20assignee%3AJeongYunMi%20" title="Code">issues</a>
    </td>
    <td rowspan="1" align="center">
      <a href="https://github.com/To-Letter/To-Letter-back" title="Code">To. Letter backend</a>
    </td>
  </tr>
</table>

<br/>
