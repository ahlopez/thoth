package com.f.thoth.app.security;

import static com.f.thoth.Parm.CLASS_ROOT;
import static com.f.thoth.Parm.CURRENT_USER;
import static com.f.thoth.Parm.EXPEDIENTE_ROOT;
import static com.f.thoth.Parm.TENANT;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Scope;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.authentication.SavedRequestAwareAuthenticationSuccessHandler;

import com.f.thoth.Parm;
import com.f.thoth.backend.data.Role;
import com.f.thoth.backend.data.gdoc.document.jackrabbit.NodeType;
import com.f.thoth.backend.data.security.Tenant;
import com.f.thoth.backend.data.security.User;
import com.f.thoth.backend.repositories.SingleUserRepository;
import com.f.thoth.ui.utils.Constant;
import com.vaadin.flow.server.VaadinSession;

/**
 * Configures spring security, doing the following:
 * <li>Bypass security checks for static resources,</li>
 * <li>Restrict access to the application, allowing only logged in users,</li>
 * <li>Set up the login form,</li>
 * <li>Configures the {@link UserDetailsServiceImpl}.</li>

 */
@EnableWebSecurity
@Configuration
public class SecurityConfiguration extends WebSecurityConfigurerAdapter
{

   private static final String LOGIN_PROCESSING_URL = "/login";
   private static final String LOGIN_FAILURE_URL    = "/login?error";
   private static final String LOGIN_URL            = "/login";
   private static final String LOGOUT_SUCCESS_URL   = Parm.PATH_SEPARATOR + Constant.PAGE_EVIDENTIAFRONT;

   private final UserDetailsService userDetailsService;


   @Autowired
   private PasswordEncoder passwordEncoder;

   @Autowired
   public SecurityConfiguration(UserDetailsService userDetailsService)
   {
      this.userDetailsService = userDetailsService;
   }

   /**
    * The password encoder to use when encrypting passwords.
    */
   @Bean
   public PasswordEncoder passwordEncoder()
   {
      return new BCryptPasswordEncoder();
   }

   @Bean
   @Scope(ConfigurableBeanFactory.SCOPE_PROTOTYPE)
   public CurrentUser currentUser(SingleUserRepository userRepository)
   {
      final String username = SecurityUtils.getUsername();
      com.f.thoth.backend.data.security.User user = (username != null) ? userRepository.findByEmailIgnoreCase(username) :  null;
      if (user != null)
      {   saveUserContext(user);
      }
      return () -> user;
   }//currentUser


   private void  saveUserContext(User currentUser)
   {
      VaadinSession session = VaadinSession.getCurrent();
      if (session != null && session.getAttribute("CURRENT_USER") == null)
      {
         session.setAttribute(CURRENT_USER, currentUser);
         Tenant tenant = currentUser.getTenant();
         session.setAttribute(TENANT, tenant);
         session.setAttribute(CLASS_ROOT,      tenant.getWorkspace()+ Parm.PATH_SEPARATOR+ NodeType.CLASSIFICATION.getCode());
         session.setAttribute(EXPEDIENTE_ROOT, tenant.getWorkspace()+ Parm.PATH_SEPARATOR+ NodeType.EXPEDIENTE.getCode());
         // TODO: Ojo, si la definición de tipos es jerárquica, EXPEDIENTE_ROOT debe ser una clase, no un path cualquiera
      }
   }//saveUserContext


   /**
    * Registers our UserDetailsService and the password encoder to be used on login attempts.
    */
   @Override
   protected void configure(AuthenticationManagerBuilder auth) throws Exception
   {
      super.configure(auth);
      auth.userDetailsService(userDetailsService).passwordEncoder(passwordEncoder);
   }//configure


   /**
    * Require login to access internal pages and configure login form.
    */
   @Override
   protected void configure(HttpSecurity http) throws Exception
   {
      // Not using Spring CSRF here to be able to use plain HTML for the login page
      http.csrf().disable()

            // Register our CustomRequestCache, that saves unauthorized access attempts, so
            // the user is redirected after login.
            .requestCache().requestCache(new CustomRequestCache())

            // Restrict access to our application.
            .and().authorizeRequests()

            // Allow all flow internal requests.
            .requestMatchers(SecurityUtils::isFrameworkInternalRequest).permitAll()

            // Allow all requests by logged in users.
            .anyRequest().hasAnyAuthority(Role.getAllRoles())

            // Configure the login page.
            .and().formLogin().loginPage(LOGIN_URL).permitAll().loginProcessingUrl(LOGIN_PROCESSING_URL)
            .failureUrl(LOGIN_FAILURE_URL)

            // Register the success handler that redirects users to the page they last tried
            // to access
            .successHandler(new SavedRequestAwareAuthenticationSuccessHandler())

            // Configure logout
            .and().logout().logoutSuccessUrl(LOGOUT_SUCCESS_URL);

   }//configure

   /**
    * Allows access to static resources, bypassing Spring security.
    */
   @Override
   public void configure(WebSecurity web)
   {
      web.ignoring().antMatchers(
            // client-side JS code
            "/VAADIN/**",

            // the standard favicon URI
            "/favicon.ico",

            // the robots exclusion standard
            "/robots.txt",

            // web application manifest
            "/manifest.webmanifest",
            "/sw.js",
            "/offline-page.html",

            // icons and images
            "/icons/**",
            "/images/**",

            // (development mode) H2 debugging console
            "/h2-console/**"
      );

   }//configure

}//SecurityConfiguration
