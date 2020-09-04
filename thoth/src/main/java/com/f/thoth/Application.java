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
import com.f.thoth.backend.data.gdoc.classification.ClassificationClass;
import com.f.thoth.backend.data.gdoc.classification.Retencion;
import com.f.thoth.backend.data.gdoc.classification.RetentionSchedule;
import com.f.thoth.backend.data.gdoc.expediente.Expediente;
import com.f.thoth.backend.data.gdoc.expediente.FileIndex;
import com.f.thoth.backend.data.gdoc.expediente.IndexEntry;
import com.f.thoth.backend.data.gdoc.metadata.DocType;
import com.f.thoth.backend.data.gdoc.metadata.Metadata;
import com.f.thoth.backend.data.gdoc.metadata.Schema;
import com.f.thoth.backend.data.gdoc.metadata.PropertyValues;
import com.f.thoth.backend.data.security.ObjectToProtect;
import com.f.thoth.backend.data.security.Permission;
import com.f.thoth.backend.data.security.Role;
import com.f.thoth.backend.data.security.Tenant;
import com.f.thoth.backend.repositories.ClassificationClassRepository;
import com.f.thoth.backend.repositories.OperationRepository;
import com.f.thoth.backend.repositories.PermissionRepository;
import com.f.thoth.backend.repositories.RoleRepository;
import com.f.thoth.backend.repositories.TenantRepository;
import com.f.thoth.backend.repositories.UserRepository;
import com.f.thoth.backend.service.ClassificationClassService;
import com.f.thoth.backend.service.OperationService;
import com.f.thoth.backend.service.TenantService;
import com.f.thoth.backend.service.UserService;
import com.f.thoth.ui.MainView;

/**
 * Spring boot web application initializer.
      scanBasePackageClasses = { SecurityConfiguration.class, MainView.class, Application.class, UserService.class, ObjectToProtectService.class },
     @EntityScan(basePackageClasses = { User.class, Tenant.class, ObjectToProtect.class, DocType.class })
 */
@SpringBootApplication(
         scanBasePackageClasses =
         {
            SecurityConfiguration.class,
            MainView.class,
            Application.class,
            UserService.class,
            OperationService.class,
            ClassificationClassService.class,
            TenantService.class,
            ObjectToProtect.class,
            Role.class,
            Permission.class,
            Expediente.class,
            FileIndex.class,
            IndexEntry.class,
            Schema.class,
            PropertyValues.class,
            Metadata.class,
            DocType.class,
            Retencion.class,
            RetentionSchedule.class
         },
           exclude = ErrorMvcAutoConfiguration.class
      )
@EnableJpaRepositories(
         basePackageClasses =
         {
            UserRepository.class,
            OperationRepository.class,
            TenantRepository.class,
            RoleRepository.class,
            PermissionRepository.class,
            ClassificationClassRepository.class
         }
      )
@EntityScan(
         basePackageClasses =
         { User.class,
           Tenant.class,
           Role.class,
           ObjectToProtect.class,
           ClassificationClass.class,
           Permission.class,
           Expediente.class,
           FileIndex.class,
           IndexEntry.class,
           Schema.class,
           PropertyValues.class,
           Metadata.class,
           DocType.class,
           Retencion.class,
           RetentionSchedule.class,
         }
      )
public class Application extends SpringBootServletInitializer
{

   public static void main(String[] args) {
      SpringApplication.run(Application.class, args);
   }

   @Override
   protected SpringApplicationBuilder configure(SpringApplicationBuilder application) {
      return application.sources(Application.class);
   }
}//Application
