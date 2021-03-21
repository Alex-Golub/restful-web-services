package edu.mrdrprof.app.ws.ui.model.request;

import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.Email;
import javax.validation.constraints.Size;

/**
 * @author Mr.Dr.Professor
 * @since 3/21/2021 11:17 AM
 */
@Getter
@Setter
public class UserLoginRequestModel {
  @Email(message = "Invalid email provided")
  private String email;

  @Size(min = 4, message = "Password should be at least {min} characters long")
  private String password;
}
