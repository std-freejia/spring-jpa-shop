package jpabook.jpashop;

import com.fasterxml.jackson.datatype.hibernate5.Hibernate5Module;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

@SpringBootApplication
public class JpashopApplication {

	public static void main(String[] args) {
		SpringApplication.run(JpashopApplication.class, args);
	}

	@Bean
	Hibernate5Module hibernate5Module(){ // OrderSimpleApiController.ordersV1
		Hibernate5Module hibernate5Module = new Hibernate5Module();
		// FORCE_LAZY_LOADING : 강제로 '즉시로딩' 설정. 비추하는 방식이긴 함. 기본적으로 엔티티를 전부 노출하지 말아야 하기 때문.
		// hibernate5Module.configure(Hibernate5Module.Feature.FORCE_LAZY_LOADING, true);
		return new Hibernate5Module();
	}
}
