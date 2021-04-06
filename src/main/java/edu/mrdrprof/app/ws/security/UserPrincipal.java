package edu.mrdrprof.app.ws.security;

import edu.mrdrprof.app.ws.io.entity.AuthorityEntity;
import edu.mrdrprof.app.ws.io.entity.RoleEntity;
import edu.mrdrprof.app.ws.io.entity.UserEntity;
import lombok.AllArgsConstructor;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * @author Alex Golub
 * @since 06-Apr-21 6:56 PM
 */
@AllArgsConstructor
public class UserPrincipal implements UserDetails {
  private static final long serialVersionUID = -2672354501180630076L;
  private final UserEntity userEntity;

  /**
   * Return a collection of all roles and authorities for this
   * authenticated user
   */
  @Override
  public Collection<? extends GrantedAuthority> getAuthorities() {
    List<GrantedAuthority> authorities = new ArrayList<>();
    List<AuthorityEntity> authorityEntities = new ArrayList<>();

    Collection<RoleEntity> userEntityRoles = userEntity.getRoles();
    if (userEntityRoles == null) {
      return authorities; // authenticated user has no roles
    }

    // extract all roles and authorities
    userEntityRoles.forEach(roleEntity -> {
      authorities.add((new SimpleGrantedAuthority(roleEntity.getName())));
      authorityEntities.addAll(roleEntity.getAuthorities());
    });

    authorityEntities.forEach(authorityEntity ->
                                      authorities.add(new SimpleGrantedAuthority(authorityEntity.getName()))
    );

    return authorities;
  }

  @Override
  public String getPassword() {
    return userEntity.getEncryptedPassword();
  }

  @Override
  public String getUsername() {
    return userEntity.getEmail(); // email is used to load user
  }

  @Override
  public boolean isAccountNonExpired() {
    return true;
  }

  @Override
  public boolean isAccountNonLocked() {
    return true;
  }

  @Override
  public boolean isCredentialsNonExpired() {
    return true;
  }

  @Override
  public boolean isEnabled() {
    // NB: email verification functionality is not implemented (Amazon $$$)
//    return userEntity.isEmailVerificationStatus();
    return true;
  }
}
