package com.f.thoth.app.security;

import java.util.Collections;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Primary;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import com.f.thoth.backend.data.security.User;
import com.f.thoth.backend.repositories.SingleUserRepository;

/**
 * Implements the {@link UserDetailsService}.
 *
 * This implementation searches for {@link User} entities by the e-mail address
 * supplied in the login screen.
 */
@Service
@Primary
public class UserDetailsServiceImpl implements UserDetailsService 
{
   private final SingleUserRepository userRepository;

   @Autowired
   public UserDetailsServiceImpl(SingleUserRepository userRepository) 
   {   this.userRepository = userRepository;
   }
   

   /**
    *
    * Recovers the {@link User} from the database using the e-mail address supplied
    * in the login screen. If the user is found, returns a
    * {@link org.springframework.security.core.userdetails.User}.
    *
    * @param username User's e-mail address
    *
    */
   @Override
   public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException 
   {
      User user = userRepository.findByEmailIgnoreCase(username);
      if (null == user) 
      { throw new UsernameNotFoundException("No hay un usuario con nombre[" + username+ "]");
      } else 
      { return new org.springframework.security.core.userdetails.User(user.getEmail(), user.getPasswordHash(),
               Collections.singletonList(new SimpleGrantedAuthority(user.getRole())));
      }
   }//loadUserByUsername
   
}//UserDetailsServiceImpl