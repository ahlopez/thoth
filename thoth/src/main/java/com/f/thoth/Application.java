package com.f.thoth;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.boot.autoconfigure.web.servlet.error.ErrorMvcAutoConfiguration;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.web.servlet.support.SpringBootServletInitializer;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

import com.f.thoth.app.security.SecurityConfiguration;
import com.f.thoth.backend.data.entity.Order;
import com.f.thoth.backend.data.gdoc.classification.Classification;
import com.f.thoth.backend.data.gdoc.classification.Level;
import com.f.thoth.backend.data.gdoc.classification.Retention;
import com.f.thoth.backend.data.gdoc.expediente.BaseExpediente;
import com.f.thoth.backend.data.gdoc.expediente.ExpedienteGroup;
import com.f.thoth.backend.data.gdoc.expediente.Expediente;
import com.f.thoth.backend.data.gdoc.expediente.ExpedienteIndex;
import com.f.thoth.backend.data.gdoc.expediente.IndexEntry;
import com.f.thoth.backend.data.gdoc.expediente.LeafExpediente;
import com.f.thoth.backend.data.gdoc.expediente.Volume;
import com.f.thoth.backend.data.gdoc.expediente.VolumeInstance;
import com.f.thoth.backend.data.gdoc.metadata.DocumentType;
import com.f.thoth.backend.data.gdoc.metadata.Metadata;
import com.f.thoth.backend.data.gdoc.metadata.Schema;
import com.f.thoth.backend.data.gdoc.metadata.SchemaValues;
import com.f.thoth.backend.data.gdoc.numerator.Numerator;
import com.f.thoth.backend.data.gdoc.numerator.Sequence;
import com.f.thoth.backend.data.security.ObjectToProtect;
import com.f.thoth.backend.data.security.Permission;
import com.f.thoth.backend.data.security.Role;
import com.f.thoth.backend.data.security.Tenant;
import com.f.thoth.backend.data.security.User;
import com.f.thoth.backend.repositories.BaseExpedienteRepository;
import com.f.thoth.backend.repositories.ExpedienteGroupRepository;
import com.f.thoth.backend.repositories.ClassificationRepository;
import com.f.thoth.backend.repositories.ExpedienteLeafRepository;
import com.f.thoth.backend.repositories.LeafExpedienteRepository;
import com.f.thoth.backend.repositories.LevelRepository;
import com.f.thoth.backend.repositories.OperationRepository;
import com.f.thoth.backend.repositories.PermissionRepository;
import com.f.thoth.backend.repositories.RetentionRepository;
import com.f.thoth.backend.repositories.RoleRepository;
import com.f.thoth.backend.repositories.SchemaRepository;
import com.f.thoth.backend.repositories.SequenceRepository;
import com.f.thoth.backend.repositories.TenantRepository;
import com.f.thoth.backend.repositories.UserRepository;
import com.f.thoth.backend.repositories.VolumeRepository;
import com.f.thoth.backend.service.BaseExpedienteService;
import com.f.thoth.backend.service.ExpedienteGroupService;
import com.f.thoth.backend.service.ClassificationService;
import com.f.thoth.backend.service.ExpedienteLeafService;
import com.f.thoth.backend.service.LeafExpedienteService;
import com.f.thoth.backend.service.LevelService;
import com.f.thoth.backend.service.OperationService;
import com.f.thoth.backend.service.RetentionService;
import com.f.thoth.backend.service.SchemaService;
import com.f.thoth.backend.service.SequenceService;
import com.f.thoth.backend.service.TenantService;
import com.f.thoth.backend.service.UserService;
import com.f.thoth.backend.service.VolumeService;
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
            SchemaService.class,
            ClassificationService.class,
            BaseExpedienteService.class,
            LeafExpedienteService.class,
            ExpedienteLeafService.class,
            ExpedienteGroupService.class,
            VolumeService.class,
            LevelService.class,
            TenantService.class,
            RetentionService.class,
            ObjectToProtect.class,
            SequenceService.class,
            Role.class,
            Level.class,
            Permission.class,
            Sequence.class,
            BaseExpediente.class,
            LeafExpediente.class,
            ExpedienteGroup.class,
            Expediente.class,
            Volume.class,
            VolumeInstance.class,
            ExpedienteIndex.class,
            IndexEntry.class,
            Schema.class,
            SchemaValues.class,
            Metadata.class,
            DocumentType.class,
            Retention.class,
            Order.class
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
            SchemaRepository.class,
            ClassificationRepository.class,
            BaseExpedienteRepository.class,
            ExpedienteGroupRepository.class,
            LeafExpedienteRepository.class,
            ExpedienteLeafRepository.class,
            VolumeRepository.class,
            SequenceRepository.class,
            RetentionRepository.class,
            LevelRepository.class
         }
      )
@EntityScan(
         basePackageClasses =
         { User.class,
           Tenant.class,
           Role.class,
           ObjectToProtect.class,
           Classification.class,
           Numerator.class,
           Sequence.class,
           Level.class,
           Permission.class,
           BaseExpediente.class,
           ExpedienteGroup.class,
           LeafExpediente.class,
           Expediente.class,
           Volume.class,
           VolumeInstance.class,
           ExpedienteIndex.class,
           IndexEntry.class,
           Schema.class,
           Metadata.class,
           SchemaValues.class,
           DocumentType.class,
           Retention.class,
           Order.class
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
