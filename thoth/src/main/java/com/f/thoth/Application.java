package com.f.thoth;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.boot.autoconfigure.web.servlet.error.ErrorMvcAutoConfiguration;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.web.servlet.support.SpringBootServletInitializer;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

import com.f.thoth.app.security.SecurityConfiguration;
import com.f.thoth.backend.data.entity.User;
import com.f.thoth.backend.data.gdoc.metadata.DocType;
import com.f.thoth.backend.data.security.Tenant;
import com.f.thoth.backend.repositories.TenantRepository;
import com.f.thoth.backend.repositories.UserRepository;
import com.f.thoth.backend.service.UserService;
import com.f.thoth.ui.MainView;

/**
 * Spring boot web application initializer.
 */
@SpringBootApplication(
		scanBasePackageClasses = { SecurityConfiguration.class, MainView.class, Application.class, UserService.class }, 
        exclude = ErrorMvcAutoConfiguration.class)
@EnableJpaRepositories(basePackageClasses = { UserRepository.class, TenantRepository.class })
@EntityScan(basePackageClasses = { User.class, Tenant.class, DocType.class })
public class Application extends SpringBootServletInitializer {

   public static void main(String[] args) {
      SpringApplication.run(Application.class, args);
   }

   @Override
   protected SpringApplicationBuilder configure(SpringApplicationBuilder application) {
      return application.sources(Application.class);
   }
}
