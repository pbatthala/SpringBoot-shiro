package cn.wt.bootshiro.config;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import springfox.documentation.builders.ApiInfoBuilder;
import springfox.documentation.builders.PathSelectors;
import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.service.ApiInfo;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spring.web.plugins.Docket;
import springfox.documentation.swagger2.annotations.EnableSwagger2;
//http://localhost/swagger-ui.html  Swagger 访问地址
@Configuration
@EnableSwagger2 
public class Swagger2Config {
	@Bean
	public Docket getDocket() { // 此类主要是整个的Swagger配置项，利用这个类需要来指派扫描包
		return new Docket(DocumentationType.SWAGGER_2)
				.apiInfo(this.getApiInfo()).select()	// 读取描述信息
				.apis(RequestHandlerSelectors
				.basePackage("cn.wt.bootrestful.controller")) // 扫描程序包
				.paths(PathSelectors.any()).build(); // 设置文档的显示类型
	}
	private ApiInfo getApiInfo() {
		return new ApiInfoBuilder().title("SpringBoot中使用Swagger构建项目说明信息")
				.description("更多选择请关注www.mldn.cn")
				.termsOfServiceUrl("http://www.mldn.cn")
				.contact("MLDN小李同学-小帅哥")
				.license("李兴华").version("1.0").build();
	}
}

