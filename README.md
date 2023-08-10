﻿# I'm here! Server
 
# 1. 프로젝트 소개
출결 관리 서비스 I'm here! 입니다 <Br>
수업을 개설하고, 학생의 출결을 관리해요! <br>

지금 바로 출석하러 가기 -> [imhere.im](imhere.im)

 
## GDSC Hongik 웹 기초 스터디의 출석 문제를 해결했습니다!
동아리 GDSC Hongik에서 웹 기초 스터디 강의를 맡게 된 후, 진행한 참여자 사전 조사에서 140명의 신청자가 있었습니다. <br>
처음 수강 인원을 40여명 정도로 예상했으나, 훨씬 많은 학우분이 신청해 주셨습니다. <br>
동아리 내 개설된 다른 스터디 또한 같은 상황에 놓였습니다.  <br>
이런 상황에서, 동아리에서는 대면 수업 시 140명의 출결 방식에 대해 고민하게 되었고, <br>
출결 관리 문제를 직접 해결하고 싶어 동아리의 다양한 스터디에서 쓰일 수 있는 출결 관리 프로그램을 제작했습니다. <br>
- [<b>기초 웹 스터디 강의 소개 페이지</b>](https://www.gdschongik.com/web-study/introduce) 
- [<b>수업과 실습 영상 유튜브</b>](https://www.youtube.com/watch?v=KpxVNBJ9UDw)

### 시간과 에너지의 절약했습니다
개발 일정이 늦어져서, 처음에는 직접 이름을 호명하며 출석을 체크했습니다. <br>
당시 대면 수업 참석 인원은 70명이었습니다. <br>
70명의 학생의 이름을 하나하나 호명하고 얼굴을 확인하면서 수업을 시작하기 전부터 지치게 되었습니다. <br>
10분의 시간과 많은 에너지가 소모되었습니다.  <br> 

그러나 서비스 도입 이후에는 출석 번호를 칠판에 적은 후, 즉시 수업을 진행할 수 있게 되었습니다. <br>
출석 체크 과정은 겨우 1분 정도가 소요됐고, 수업에만 집중할 수 있게 되었습니다. <br>

그리고 서비스에 출석 위치와 강의실의 거리를 계산하는 기능을 구현하여, 출석 위치가 너무 멀게 측정된 학생들의 실제 참석 여부를 파악할 수 있었습니다. 이를 통해 부정 출석 인원을 미출석으로 처리할 수 있었습니다.

<br>

# 2. 서비스 기능 소개

<details>
<summary> <h2> 강사 계정 기능 </h2> </summary>

## 강사 계정 기능

### 1. 강의 관리
강의를 개설하고 폐쇄할 수 있습니다.

### 2. 출결 관리
수업을 Open하면 출석 번호가 발급되고, 학생은 10분간 출석 번호를 입력해 출석할 수 있습니다.

출석시 출석 위치와 강의실 사이의 거리가 측정되어 부정 출석을 확인할 수 있습니다.

특정 날짜의 출석 정보를 엑셀 파일로 다운받을 수 있습니다.

### 3. 수강생 관리
수강 신청 학생을 승인하거나 거절할 수 있어요!

</details>

<details>
<summary> <h2> 학생 계정 기능 </h2> </summary>
  
## 학생 계정 기능

### 1. 회원가입
구글 이메일 인증을 통해 회원가입 할 수 있습니다.

### 2. 수강 신청
원하는 수업에 수강신청 할 수 있습니다.

### 3. 출석 신청
수강 승인 받은 수업이, Open된 경우 출석 번호와 함께 출석 신청할 수 있습니다. 

</details>

# 3. 인프라 구조와 설명

![image](https://github.com/binary-ho/imhere-server/assets/71186266/f781a563-45a1-4e10-81ab-9e0ad5079e86)


프론트엔드 프로젝트와 백엔드 프로젝트를 직접 구현하고, 배포했습니다.
백엔드와 인프라 작업에 집중하기 위해, v1 배포 이후의 클라이언트 프로젝트는 @eugene028 님께서 맡아주시고 있습니다. 


## 3.1 클라이언트와 배포
프론트엔드는 javascript 라이브러리 react를 이용해 만들었습니다. 
- 보안을 위해 정적 리소스를 저장하는 버킷 S3의 웹 호스팅을 사용하지 않고, CloudFront를 통해 호스팅 했습니다. <br> CloudFront를 이용함으로써 안전하게 원본을 지키고, 한국 리전에 캐싱할 수 있었습니다.
- 백엔드와 인프라 작업에 집중하기 위해, v1 배포 이후의 클라이언트 프로젝트는 @eugene028 님께서 맡아주시고 있습니다. <br> 타입스크립트로의 마이그레이션과 디자인 개선 및 기능 고도화를 진행해주셨습니다. <br> 클라이언트 배포를 위해 AWS IAM S3, CloudFront FullAccess 권한을 부여했습니다.
- 클라이언트 작업자의 편의와 배포 전 테스트를 위해 dev 서버와 prod 서버를 구분해 사용중입니다. <br>  클라이언트 개발자는 dev 서버로 요청을 보내며 개발중입니다
- [imhere-client 바로가기](https://github.com/eugene028/imhere-client)

## 3.2 백엔드
백엔드 애플리케이션은 유연한 프로그램을 위해, java 기반의 Spring Boot 프레임워크를 이용해 만들었습니다. <br>

- 출석 시간 동안 매우 짧은 시간에 수백건의 요청이 몰릴 것을 우려하여, 강의 출석 시 출석 번호 확인 과정에서 Redis를 사용하였습니다. 강의 ID와 출석 번호가 key-value 형태로 저장되며, 출석 요청 시 이 값을 확인합니다. 값이 일치하는 경우 DB에 해당 학생의 출석 정보를 저장합니다. 이로써 학생의 각 출석 요청마다 발생하는 DB Read를 줄일 수 있었습니다.
- 유연한 설계와, 회귀 방지를 위한 테스트 작성을 위해 의존성 역전을 적극적으로 활용했습니다. RedisTemplate과 JavaMailSender를 사용하거나, 토큰 생성을 위해 SECRET을 사용하는 객체들을 인터페이스로 추상화하였습니다. 구현체는 빈으로 관리하고, 호출하는 쪽에서는 추상화된 인터페이스를 의존하도록 구현하였습니다. 테스트 시엔 인터페이스를 구현한 Fake 객체를 활용했습니다.
- 학생과 강사의 사용 가능한 기능을 분리하기 위해 스프링 시큐리티를 통해 인증 기능을 구현했습니다. 서버를 stateless 하게 유지하면서도 회원이 편리하게 이용할 수 있도록 토큰을 활용해 인가 기능을 구현했습니다.
- 문제 상황 추적을 위해 로깅을 진행하였습니다. 처음엔 일반 출력문을 사용하다가, 파일로 보관하기 위해 로깅 프레임워크를 도입했습니다. 스프링 부트가 기본으로 제공하는 logback 대신 log4j2를 사용하였습니다. 로그는 날짜를 기준으로 파일에 저장되며, 지정 용량을 초과하는 경우 동일한 날짜라도 별도의 파일에 보관됩니다.
로그로 인해 회원가입 메일이 오지 않는다는 회원의 컴플레인이 서버 문제가 아님을 확신할 수 있었습니다.

<br>

## 3.3 앱과 DB 서버 배포

- 애플리케이션은 도커 이미지로 제작하여, Redis, Nginx 이미지와 함께 AWS EC2에 배포하였습니다. Nginx 서버는 HTTPS 접속을 위한 리버스 프록시를 위해 사용했고, 도커 컴포즈를 이용해 편하게 배포하기 위해 SSL 인증서 경로를 포함한 이미지를 만들어 사용중입니다.
- 사용자의 안전한 접속을 보장하기 위해 Certbot을 이용하여, Let's Encrypt의 SSL 인증서를 발급받아 HTTPS 프로토콜을 구축하였습니다.
- 클라이언트는 javascript 기반 React 라이브러리를 활용해 구현했고, 정적 리소스는 S3에 저장하였습니다. 보안을 위해 S3의 자체 호스팅 기능을 사용하지 않고, CloudFront를 통해 호스팅했습니다. CloudFront를 통해 원본을 안전하게 보호하고, 한국 리전에 캐싱하여 더욱 빠르게 리소스를 전달할 수 있었습니다.
관련 글 : [S3와 CloudFront 정책 설정 방법](https://dwaejinho.tistory.com/entry/%EC%B6%A9%EA%B2%A9-S3-Hosting-%EC%9D%B4%EA%B2%83-%EC%84%A4%EC%A0%95-%EC%95%88-%ED%95%98%EC%9E%90-%EA%B0%9C%EC%9D%B8%EC%A0%95%EB%B3%B4-%EC%88%A0%EC%88%A0)
- 첫 배포 이후, 더 나은 프로젝트를 위해 클라이언트 담당 팀원을 구했습니다. AWS IAM 권한을 부여하고, 작업자의 편의를 위해 서버를 배포용 서버와 개발용 서버로 구분하였습니다. 클라이언트 개발자는 개발 과정에서 개발용 서버에 요청을 보낼 수 있습니다.
- MySQL는 원활한 배포와 관리 및 백업을 위해 RDS 서비스를 이용했습니다.

# 4. 프로젝트 관련 블로그 글
1. [@WithSecurityContext를 이용해 커스텀 UserDetails SecurityContext Test 코드 작성하기](https://dwaejinho.tistory.com/entry/%EC%BB%A4%EC%8A%A4%ED%85%80-UserDetails-SecurityContext-Test-%EC%BD%94%EB%93%9C-%EC%9E%91%EC%84%B1%ED%95%98%EA%B8%B0)
2. S3와 CloudFront를 활용한 배포와 보안 설정 -> [S3 배포시 '이것' 설정 안 하자... 개인정보 '술술'](https://dwaejinho.tistory.com/entry/%EC%B6%A9%EA%B2%A9-S3-Hosting-%EC%9D%B4%EA%B2%83-%EC%84%A4%EC%A0%95-%EC%95%88-%ED%95%98%EC%9E%90-%EA%B0%9C%EC%9D%B8%EC%A0%95%EB%B3%B4-%EC%88%A0%EC%88%A0)
3. Nginx 다중 소스 허용과 if 문법 -> [Don't try this at home - "IF" is Evil](https://dwaejinho.tistory.com/entry/Nginx-%EC%95%85%EB%A7%88%EC%9D%98-IF-Dont-try-if-at-home)
4. CORS 처리와 HTTPS 인증 처리 -> 작성 예정
2. @Slf4j와 logback, log4j, log4j2 -> 작성 예정
3. 도커 컴포즈를 활용한 백엔드 애플리케이션 배포하기 -> 작성 예정
4. 의존성 역전으로 테스트하기 어려운 객체 테스트하기 -> 작성 예정


# 5. 향후 개발 계획
GDSC Hongik 외에도 다른 커뮤니티에서도 사용할 수 있도록
TPS 1000건을 목표로 고도화 할 예정입니다.

학생들끼리 서로의 과제물을 확인할 수 있는 커뮤니티 기능에 대한 학생들의 익명 요청이 많아 추가할 예정입니다.   

1. 스터디 과제물 커뮤티니
2. 깃허브 회원가입과 로그인
3. 쓰로틀링
4. 스터디별 강의실 위치 지정
5. 더 자세한 로깅
6. 메서드별 접근 권한 확인을 AOP로 처리
7. 회원 비밀번호 찾기
8. 회원 탈퇴
