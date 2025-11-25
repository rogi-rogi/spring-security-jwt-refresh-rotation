# Spring Security JWT & REST API 예제 프로젝트

이 프로젝트는 Spring Boot와 Spring Security를 사용하여 RESTful API에서 JWT(Access Token / Refresh Token) 기반 인증 및 인가 시스템을 구현하는 방법을 보여주는 예제입니다.

## 주요 학습 목표
- JWT 발급 및 검증
- Access Token과 Refresh Token을 사용한 인증 흐름
- Spring Security 필터 체인 커스터마이징
- CORS 및 CSRF 설정에 대한 이해
- 인증/인가 예외 처리

## 기술 스택 및 버전
- **Java**: 17
- **Spring Boot**: 3.x (build.gradle 플러그인 버전: 4.0.0)
- **Spring Security**: 6.x
- **JWT (jjwt)**: 0.12.5
- **Build Tool**: Gradle

---

## 주요 개념 설명

### 1. CORS / CSRF 설정

#### CORS (Cross-Origin Resource Sharing)
웹 브라우저는 보안상의 이유로 '동일 출처 정책(Same-Origin Policy)'을 따릅니다. 이 때문에 프론트엔드 애플리케이션(예: `http://localhost:3000`)이 백엔드 API(`http://localhost:8080`)를 호출할 때 기본적으로 차단됩니다.

`CorsConfig`와 `SecurityConfig`의 `cors()` 설정을 통해 특정 출처(Origin), 메서드(GET, POST 등), 헤더를 허용하도록 명시해야 합니다. 이를 통해 다른 도메인에서도 우리 API 리소스를 안전하게 요청할 수 있습니다.

#### CSRF (Cross-Site Request Forgery)
CSRF는 사용자가 자신의 의지와 무관하게 공격자가 의도한 행위(수정, 삭제 등)를 특정 웹사이트에 요청하게 만드는 공격입니다.

이 프로젝트에서는 `csrf(CsrfConfigurer::spa)` 설정을 사용합니다. 이는 Spring Security가 제공하는 SPA(Single Page Application) 환경을 위한 CSRF 보호 전략입니다.

전통적인 CSRF 보호는 서버가 생성한 CSRF 토큰을 세션에 저장하고, 모든 상태 변경 요청(POST, PUT, DELETE 등)에 이 토큰을 함께 보내도록 요구하는 방식입니다. 하지만 이 프로젝트처럼 세션을 사용하지 않는 `STATELESS` 환경에서는 다른 방식이 필요합니다.

`spa()` 설정은 다음과 같이 동작합니다.
1.  최초 GET 요청 시, 서버는 `X-CSRF-TOKEN`이라는 이름의 쿠키에 CSRF 토큰을 담아 클라이언트에게 전달합니다.
2.  클라이언트(예: React, Vue)는 이 쿠키 값을 읽어서, 이후의 모든 상태 변경 요청(POST, PUT 등)의 HTTP 헤더(기본적으로 `X-XSRF-TOKEN`)에 이 토큰을 담아 보냅니다.
3.  서버는 요청 헤더에 담긴 토큰과 쿠키에 담긴 토큰을 비교하여 요청이 유효한지 검증합니다.

이 방식은 자바스크립트 코드는 쿠키를 읽을 수 있지만, 다른 도메인에서는 쿠키를 읽을 수 없다는 점(Same-Origin Policy)을 활용하여 CSRF 공격을 방지합니다.

### 2. JWT 필터의 인증 오류 처리

**"왜 토큰이 없거나 유효하지 않을 때 그냥 요청을 넘기지 않고, 인증 오류를 발생시키는가?"**

Spring Security는 **"기본적으로 거부(Deny by Default)"** 원칙을 따릅니다. 즉, 명시적으로 허용되지 않은 모든 요청은 차단됩니다.

`JwtFilter`의 핵심 책임은 요청에 포함된 토큰을 검증하여 유효한 사용자인지 확인하고, `SecurityContextHolder`에 인증 정보(`Authentication` 객체)를 설정하는 것입니다.

만약 토큰이 없거나, 서명이 위조되었거나, 만료되는 등 유효하지 않다면, 해당 요청은 **'인증되지 않은(unauthenticated)'** 상태입니다. 이 상태에서 요청을 그냥 통과시키면, 결국 뒤따르는 인가(Authorization) 필터에서 `AccessDeniedException`을 발생시키게 됩니다. 이는 "인증은 되었지만 권한이 없는" 상황을 의미하므로, "아직 인증을 통과하지 못한" 상황과는 다릅니다.

따라서 `JwtFilter`에서 명시적으로 `AuthenticationException`을 발생시켜 **'인증 실패'** 상황임을 명확히 알리는 것이 더 올바른 설계입니다. 이는 다음과 같은 장점을 가집니다.
- **Fail-Fast**: 문제가 발생한 즉시 요청 처리를 중단하여 시스템 부하를 줄입니다.
- **명확한 오류 피드백**: "접근 거부(403 Forbidden)"가 아닌 "인증 필요(401 Unauthorized)"라는 정확한 상태를 클라이언트에게 전달할 수 있습니다.

### 3. 인증 오류 처리 담당 필터

**"JWT 필터가 던진 인증 오류는 어떤 필터가 처리하는가?"**

`JwtFilter` 등에서 `AuthenticationException`이 발생하면, 이 예외는 `ExceptionTranslationFilter`에 의해 처리됩니다. 이 필터는 Spring Security 필터 체인에서 예외 처리를 전담하는 핵심 컴포넌트입니다.

`ExceptionTranslationFilter`의 동작 방식은 다음과 같습니다.

1.  필터 체인 내에서 발생하는 `AuthenticationException` 또는 `AccessDeniedException`을 감지(catch)합니다.
2.  `AuthenticationException`이 발생한 경우 (즉, 인증 실패):
    - `SecurityContextHolder`를 비웁니다.
    - `SecurityConfig`에 등록된 `RestAuthenticationEntryPoint`를 호출하여 클라이언트에게 인증이 필요하다는 응답을 보내는 역할을 시작합니다.
3.  `AccessDeniedException`이 발생한 경우 (즉, 인가 실패):
    - `SecurityConfig`에 등록된 `RestAccessDeniedHandler`를 호출하여 클라이언트에게 접근이 거부되었다는 응답을 보냅니다.

이 프로젝트에서는 커스텀 `AuthenticationEntryPoint`와 `AccessDeniedHandler`를 구현하여, 클라이언트에게 일관된 형식의 JSON 에러 메시지(각각 HTTP 401 Unauthorized, 403 Forbidden 상태 코드와 함께)를 반환하도록 설정합니다. 따라서 클라이언트는 이 응답을 보고 문제의 원인을 명확하게 파악할 수 있습니다.
