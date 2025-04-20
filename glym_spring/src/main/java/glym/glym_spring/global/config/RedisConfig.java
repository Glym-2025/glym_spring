package glym.glym_spring.global.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.Jackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.StringRedisSerializer;

@Configuration
public class RedisConfig {


    //RedisTemplate<K,V> Key 는 고유 식별자, Value 는 저장할 실제 데이터... Object 이면 직렬화 가능
    /*
     * Redis에서 Key는 테이블/컬럼 없이 데이터를 구분하는 고유 식별자 역할을 합니다.
     * Value는 실제 저장되는 데이터이며, 문자열 또는 객체(직렬화)를 사용할 수 있습니다.
     *
     * Key 설계 전략 예시:
     * - "authCode:email@example.com"   → 이메일 인증 코드
     * - "refreshToken:123"            → 유저 ID 기반 리프레시 토큰
     * - "resetPwd:user@example.com"   → 비밀번호 초기화 요청
     * - "post:456:likes"              → 게시글 좋아요 수
     * - "loginFail:123"               → 로그인 실패 횟수 저장
     *
     * Key 네이밍 규칙 권장:
     *   prefix:분류:식별자
     *   예: "authCode:user@email.com", "refreshToken:42"
     *
     * TTL(Time To Live) 설정:
     * - 인증코드: 5분
     * - 리프레시 토큰: 7일
     *
     * 예시:
     * redisTemplate.opsForValue().set("authCode:user@email.com", "A1B2C3", 5, TimeUnit.MINUTES);
     * redisTemplate.opsForValue().set("refreshToken:123", "JWT_REFRESH_TOKEN", 7, TimeUnit.DAYS);
     */
    @Bean
    public RedisTemplate<String, String> authCodeRedisTemplate(RedisConnectionFactory connectionFactory) {
        RedisTemplate<String, String> template = new RedisTemplate<>();
        template.setConnectionFactory(connectionFactory);
        //key 를 string 으로 저장
        template.setKeySerializer(new StringRedisSerializer());

        //value 를 Json 으로 변환해 저장
        template.setValueSerializer(new StringRedisSerializer());
        return template;
    }
}
