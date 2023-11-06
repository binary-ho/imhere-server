﻿# I'm here! Server
 
# 1. 프로젝트 소개
출결 관리 서비스 I'm here! 입니다 <Br>
수업을 개설하고, 학생의 출결을 관리해요! <br>

## 지금 바로 출석하러 가기 -> [https://imhere.im](https://imhere.im)

<br>
 
## 1.1 GDSC Hongik 웹 기초 스터디의 출석 문제를 해결했습니다!
동아리 GDSC Hongik에서 웹 기초 스터디 강의를 맡게 되었습니다. <br>
**처음 동아리에선 약 20여명의 수강생을 예상했으나, 실제로는 140명의 신청자가 있었습니다.** <br> <br>

동아리 내 개설된 다른 스터디 또한 같은 상황에 놓였습니다. <br>
이런 상황에서, 동아리에서는 대면 수업 시 140명의 출결과 과제물 관리에 대한 방식에 대해 고민하게 되었습니다. <br>
강의를 무료로 진행하는 만큼, 출석과 과제 수행에 성실히 참여한 학생에게 다음 스터디 참여 기회를 주고 <br>
성실히 참여하지 않은 학생에게는 참여 기회를 주지 않고 있습니다. <Br> <br>

출결 관리 문제를 직접 해결하고 싶어 동아리의 다양한 스터디에서 쓰일 수 있는 출결 관리 프로그램을 제작했습니다. <br>
- [<b>기초 웹 스터디 강의 소개 페이지</b>](https://www.gdschongik.com/web-study/introduce)
- [<b>수업과 실습 영상 유튜브</b>](https://www.youtube.com/watch?v=KpxVNBJ9UDw)

<br>

### 1.2 시간과 에너지의 절약했습니다
개발 일정이 늦어져서, 처음에는 직접 이름을 호명하며 출석을 체크했습니다. <br>
첫 수업에 참석한 학생의 이름을 하나하나 호명하고 얼굴을 확인하면서 수업을 시작하기 전부터 지치게 되었습니다. <br>
10분이 넘는 시간과 많은 에너지가 소모되었습니다. <br> <br>

그러나 서비스 도입 이후에는 출석 번호를 칠판에 적은 후, 즉시 수업을 진행할 수 있게 되었습니다. <br>
출석 체크 과정은 겨우 1분 정도가 소요됐고, 수업에만 집중할 수 있게 되었습니다. <br>

그리고 서비스에 출석 위치와 강의실의 거리를 계산하는 기능을 구현하여, 출석 위치가 너무 멀게 측정된 학생들의 실제 참석 여부를 파악할 수 있었습니다. 이를 통해 부정 출석 인원을 미출석으로 처리할 수 있었습니다.

<br>

### 1.3 유저와 소통하기 위해 노력합니다.

<br>

사용자들이 직접 사용하는 서비스인 만큼, 여러 경로를 통해 피드백을 받고, 반영하기 위해 노력합니다.


![피드백](https://github.com/binary-ho/imhere-server/assets/71186266/9340ffde-0bae-4b8a-b107-4a81e9ca7200)


# 2. 서비스 기능 소개
### [Wiki 바로 가기](https://github.com/binary-ho/imhere-server/wiki)

<br>

# 3. 인프라 구조와 설명

<br>

![imhere2 drawio](https://github.com/binary-ho/imhere-server/assets/71186266/63912ddf-6d75-4339-8ec6-4d7730706f08)


프론트엔드 프로젝트와 백엔드 프로젝트를 직접 구현하고, 배포했습니다.
백엔드와 인프라 작업에 집중하기 위해, v1 배포 이후의 클라이언트 프로젝트는 @eugene028 님께서 맡아주시고 있습니다. 


## 3.1 백엔드
백엔드 애플리케이션은 유연한 프로그램을 위해, java 11과 Spring Boot 프레임워크를 이용해 만들었습니다. <br>

- **트래픽이 몰리는 출석 과정 전반을 인덱싱과 캐싱을 통해 개선했습니다.** <br> **10만 건 데이터 1,000건 동시 조회 : 30초 타임아웃 → 6.4872초 → 0.1434초** <br> <br> ▪️ 문제 상황 : 수강 신청 데이터 10만건, 1000명의 학생이 동시에 자신이 출석 가능한 수업 조회시,  650건 이상의 요청이 30초 타임아웃으로 실패 <br>
▪️ 인덱싱 적용 : 6.487초로 응답 시간 개선. <br>
 카디널리티가 높은 컬럼에 인덱싱을 적용하여 6.4872초 만에 모든 요청이 성공. 그러나 6.4872초도 사용자 입장에선 느린 응답일 것으로 생각되어, 다른 인덱싱 방법을 고려. 커버링 인덱스나 복합 인덱스를 고려했으나, 인덱싱이 차지하는 용량에 비해 큰 응답 시간 개선이 이루어지진 않았음. 결국 DB I/O를 줄이기 위해 캐싱을 도입하게 됨. <br> <br>
▪️ 캐싱 적용 : 0.1434초로 응답 시간 개선. <br>
 수강생들의 조회 요청이 몰리는 상황 직전에 수강생들의 정보를 캐싱하여 0.1434초로 응답 시간을 개선. 강의 데이터는 Hash 자료형으로 저장하고, 수강생 정보는 하나의 Redis Collection에 수강생 정보를 모두 저장하는 경우, 삭제시 Redis 스레드가 오랜 시간 점거될 수도 있으므로, 개별 String 형태로 저장. <br> 수강생 캐싱은 오직 수업을 여는 것이 성공했을 때만 캐싱되어야 하며, 강사는 캐싱 때문에 응답을 늦게 받아선 안 된다. 그리고 캐싱 실패로 인해 강의를 여는 행위가 실패할 필요는 없다. 따라서, 캐싱은 이벤트를 발행해 개별 트랜잭션에서 비동기적으로 수행하게 만들었다. 
- 학생들의 출석 체크 과정에서 수강 신청 정보를 가져오는 과정의 소요 시간을 인덱싱을 통해 개선했습니다. 수강 신청 정보 테이블의 외래키인 학생 id에 인덱스를 적용했습니다. 이후, 자바 동시성 컬렉션을 이용해 10만 건의 데이터 중 1000건을 동시에 조회하는 테스트를 진행했더니, 기존엔 600건 이상 실패하던 작업을 인덱스 적용 이후 6.4872초 만에 처리할 수 있게 되었습니다. <br> <br>

- 유연한 설계와, 회귀 방지를 위한 테스트 작성을 위해 의존성 역전을 적극적으로 활용했습니다. RedisTemplate과 JavaMailSender를 사용하거나, 토큰 생성을 위해 SECRET을 사용하는 객체들을 인터페이스로 추상화하였습니다. 구현체는 빈으로 관리하고, 호출하는 쪽에서는 추상화된 인터페이스를 의존하도록 구현하였습니다. 테스트 시엔 인터페이스를 구현한 Fake 객체를 활용했습니다.
- 서버가 접속 불가 상태가 되는 문제를 해결하고, 알림을 받기 위해 모니터링과 알림을 도입했습니다. 다운시 디스코드로 알람이 옵니다.
- 학생과 강사의 사용 가능한 기능을 분리하기 위해 스프링 시큐리티를 통해 인증 기능을 구현했습니다. 서버를 stateless 하게 유지하면서도 회원이 편리하게 이용할 수 있도록 토큰을 활용해 인가 기능을 구현했습니다.
- 문제 상황 추적을 위해 로깅을 진행하였습니다. 처음엔 일반 출력문을 사용하다가, 파일로 보관하기 위해 로깅 프레임워크를 도입했습니다. 스프링 부트가 기본으로 제공하는 logback 대신 log4j2를 사용하였습니다. 로그는 날짜를 기준으로 파일에 저장되며, 지정 용량을 초과하는 경우 동일한 날짜라도 별도의 파일에 보관됩니다.
로그로 인해 회원가입 메일이 오지 않는다는 회원의 컴플레인이 서버 문제가 아님을 확신할 수 있었습니다.

<br>

## 3.2 앱과 DB 서버 배포

- 애플리케이션은 도커 이미지로 제작하여, Redis, Nginx 이미지와 함께 AWS EC2에 배포하였습니다. Nginx 서버는 HTTPS 접속을 위한 리버스 프록시를 위해 사용했고, 도커 컴포즈를 이용해 편하게 배포하기 위해 SSL 인증서 경로를 포함한 이미지를 만들어 사용중입니다.
- 사용자의 안전한 접속을 보장하기 위해 Certbot을 이용하여, Let's Encrypt의 SSL 인증서를 발급받아 HTTPS 프로토콜을 구축하였습니다.
- 첫 배포 이후, 더 나은 프로젝트를 위해 클라이언트 담당 팀원을 구했습니다. AWS IAM 권한을 부여하고, 작업자의 편의를 위해 서버를 배포용 서버와 개발용 서버로 구분하였습니다. 클라이언트 개발자는 개발 과정에서 개발용 서버에 요청을 보낼 수 있습니다.
- PostgreSQL의 원활한 배포와 관리 및 백업을 위해 RDS 서비스를 이용했습니다.


## 3.3 클라이언트와 배포
프론트엔드는 javascript 라이브러리 react를 이용해 만들었습니다. 
- 보안을 위해 정적 리소스를 저장하는 버킷 S3의 웹 호스팅을 사용하지 않고, CloudFront를 통해 호스팅 했습니다. <br> CloudFront를 이용함으로써 안전하게 원본을 지키고, 한국 리전에 캐싱할 수 있었습니다.
- 백엔드와 인프라 작업에 집중하기 위해, v1 배포 이후의 클라이언트 프로젝트는 @eugene028 님께서 맡아주시고 있습니다. <br> 타입스크립트로의 마이그레이션과 디자인 개선 및 기능 고도화를 진행해주셨습니다. <br> 클라이언트 배포를 위해 AWS IAM S3, CloudFront FullAccess 권한을 부여했습니다.
- 클라이언트 작업자의 편의와 배포 전 테스트를 위해 dev 서버와 prod 서버를 구분해 사용중입니다. <br>  클라이언트 개발자는 dev 서버로 요청을 보내며 개발중입니다
- [imhere-client 바로가기](https://github.com/eugene028/imhere-client)
- 
# 4. 프로젝트 관련 블로그 글
1. [@WithSecurityContext를 이용해 커스텀 UserDetails SecurityContext Test 코드 작성하기](https://dwaejinho.tistory.com/entry/%EC%BB%A4%EC%8A%A4%ED%85%80-UserDetails-SecurityContext-Test-%EC%BD%94%EB%93%9C-%EC%9E%91%EC%84%B1%ED%95%98%EA%B8%B0)
2. S3와 CloudFront를 활용한 배포와 보안 설정 -> [S3 배포시 '이것' 설정 안 하자... 개인정보 '술술'](https://dwaejinho.tistory.com/entry/%EC%B6%A9%EA%B2%A9-S3-Hosting-%EC%9D%B4%EA%B2%83-%EC%84%A4%EC%A0%95-%EC%95%88-%ED%95%98%EC%9E%90-%EA%B0%9C%EC%9D%B8%EC%A0%95%EB%B3%B4-%EC%88%A0%EC%88%A0)
3. Nginx 다중 소스 허용과 if 문법 -> [Don't try this at home - "IF" is Evil](https://dwaejinho.tistory.com/entry/Nginx-%EC%95%85%EB%A7%88%EC%9D%98-IF-Dont-try-if-at-home)
2. @Slf4j와 logback, log4j, log4j2 
3. 도커 컴포즈를 활용한 백엔드 애플리케이션 배포하기 
4. 의존성 역전으로 테스트하기 어려운 객체 테스트하기
5. 인덱싱과 캐싱을 통한 응답 시간 개선

## 5. Team Member 🏠
<table>
    <tr align="center">
        <td><B>이진호<B></td>
        <td><B>김유진<B></td>
    </tr>
    <tr align="center">
        <td>기획, 백엔드, 프론트 (v1), 배포 </td>
        <td>프론트, v2 리팩토링, 디자인시스템 생성</td>
    </tr>
    <tr align="center">
        <td>
            <img src="https://github.com/binary-ho.png?size=100">
            <br>
            <a href="https://github.com/binary-ho"><I>binary-ho</I></a>
        </td>
        <td>
            <img src="https://github.com/eugene028.png?size=100" width="100">
            <br>
            <a href="https://github.com/eugene028"><I>eugene028</I></a>
        </td>
    </tr>
</table>
         
## 6. 향후 개발 계획
1. GDSC Hongik 외에도 다른 커뮤니티에서도 사용할 수 있도록 많은 트래픽을 받아낼 수 있는 서버로 고도화할 예정입니다. <br> -> 다양한 부하 및 응답시간 개선 피처 개발중
2. 학생들끼리 서로의 과제물을 확인할 수 있는 커뮤니티 기능에 대한 학생들의 요청이 많아 추가할 예정입니다.   
