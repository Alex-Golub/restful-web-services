package edu.mrdrprof.app.ws.ui.controller;

import edu.mrdrprof.app.ws.service.AddressService;
import edu.mrdrprof.app.ws.service.UserService;
import edu.mrdrprof.app.ws.shared.dto.UserDto;
import edu.mrdrprof.app.ws.ui.model.request.UserDetailsRequestModel;
import edu.mrdrprof.app.ws.ui.model.request.UserUpdateRequestModel;
import edu.mrdrprof.app.ws.ui.model.response.*;
import lombok.AllArgsConstructor;
import org.modelmapper.ModelMapper;
import org.modelmapper.TypeToken;
import org.springframework.hateoas.CollectionModel;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.Link;
import org.springframework.hateoas.server.mvc.WebMvcLinkBuilder;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.Arrays;
import java.util.List;

import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;
import static org.springframework.http.MediaType.APPLICATION_XML_VALUE;

/**
 * @author Mr.Dr.Professor
 * @since 18/03/2021 21:08
 */
@RestController
@RequestMapping(path = "/users")
@AllArgsConstructor // constructor autowiring
public class UserController {
  private final UserService userService;
  private final AddressService addressService;
  private final ModelMapper modelMapper;

  /**
   * Clients can provide how many entries to display per-page using a query-string
   * http://localhost:{port#}/{context-path}/users/{resource}?page=1&limit=5
   */
  @GetMapping(produces = {APPLICATION_JSON_VALUE, APPLICATION_XML_VALUE})
  public List<UserRest> getListOfUsers(@RequestParam(value = "page", defaultValue = "0") int page,
                                       @RequestParam(value = "limit", defaultValue = "5") int limit) {
    return modelMapper.map(userService.getUsers(page, limit),
                           new TypeToken<List<UserRest>>() {}.getType());
  }

  /** Get user by userId endpoint => http://localhost:{port#}/{context-path}/users/{userId} */
  @GetMapping(path = "/{userId}", produces = {APPLICATION_JSON_VALUE, APPLICATION_XML_VALUE})
  public UserRest getUser(@PathVariable String userId) {
    return modelMapper.map(userService.getUserByUserId(userId),
                           UserRest.class);
  }

  /** Get list of addresses for this userId => http://localhost:{port#}/{context-path}/users/{userId}/addresses */
  @GetMapping(path = "/{userId}/addresses", produces = {APPLICATION_JSON_VALUE, APPLICATION_XML_VALUE})
  public CollectionModel<AddressRest> getListOfAddresses(@PathVariable String userId) {
    List<AddressRest> addressRestList = modelMapper.map(addressService.getAddresses(userId),
                                                        new TypeToken<List<AddressRest>>() {}.getType());
    Link user = WebMvcLinkBuilder
            .linkTo(UserController.class)
            .slash(userId)
            .withRel("user");

    Link selfLink = WebMvcLinkBuilder
            .linkTo(WebMvcLinkBuilder.methodOn(UserController.class)
                            .getListOfAddresses(userId))
            .withSelfRel();

    // Add self link for each addressRest
    addressRestList.forEach(addressRest -> addressRest.add(
            WebMvcLinkBuilder
                    .linkTo((WebMvcLinkBuilder.methodOn(UserController.class)
                            .getUserAddress(userId, addressRest.getAddressId())))
                    .withSelfRel()
    ));

    return CollectionModel.of(addressRestList, user, selfLink);
  }

  /** Get single address details for this userId => http://localhost:{port#}/{context-path}/users/{userId}/addresses/{addressId} */
  @GetMapping(path = "/{userId}/addresses/{addressId}", produces = {APPLICATION_JSON_VALUE, APPLICATION_XML_VALUE})
  public EntityModel<AddressRest> getUserAddress(@PathVariable String userId, @PathVariable String addressId) {
    AddressRest addressRest = modelMapper.map(addressService.getUserAddress(addressId),
                                              AddressRest.class);

    // link to this user => {root-address}/{userId}
    Link user = WebMvcLinkBuilder
            .linkTo(UserController.class)
            .slash(userId)
            .withRel("user");

    // link to all addresses for this user => {root-address}/{userId}/addresses
    UserController userController = WebMvcLinkBuilder.methodOn(UserController.class);
    Link addresses = WebMvcLinkBuilder
            .linkTo(userController.getListOfAddresses(userId))
            .withRel("addresses");

    // link of this endpoint => {root-address}/{userId}/addresses/{addressId}
    Link selfRel = WebMvcLinkBuilder
            .linkTo(userController.getUserAddress(userId, addressId))
            .withSelfRel();

    return EntityModel.of(addressRest, Arrays.asList(user, addresses, selfRel));
  }

  /** Create new user entry => http://localhost:{port#}/{context-path}/users/{resource} */
  @PostMapping(consumes = {APPLICATION_JSON_VALUE, APPLICATION_XML_VALUE},
               produces = {APPLICATION_JSON_VALUE, APPLICATION_XML_VALUE})
  public UserRest createUser(@Valid @RequestBody UserDetailsRequestModel userDetails) {
    return modelMapper.map(userService.createUser(modelMapper.map(userDetails, UserDto.class)),
                           UserRest.class);
  }

  /** Update user entry by providing userId => http://localhost:{port#}/{context-path}/users/{userId} */
  @PutMapping(path = "/{userId}",
              consumes = {APPLICATION_JSON_VALUE, APPLICATION_XML_VALUE},
              produces = {APPLICATION_JSON_VALUE, APPLICATION_XML_VALUE})
  public UserUpdateRest updateUser(@PathVariable String userId,
                             @Valid @RequestBody UserUpdateRequestModel userDetails) {
    UserDto updatedUser = userService.updateUser(userId, modelMapper.map(userDetails, UserDto.class));
    return modelMapper.map(updatedUser, UserUpdateRest.class);
  }

  /** Delete user entry by providing userId => hhtp://localhost:{port#}/{context-path}/users/{userId} */
  @DeleteMapping(path = "/{userId}")
  public OperationRequestModel deleteUser(@PathVariable String userId) {
    OperationRequestModel operationRequestModel = new OperationRequestModel();
    userService.deleteUser(userId);

    operationRequestModel.setOperationName(RequestOperationName.DELETE.toString());
    operationRequestModel.setOperationStatus(RequestOperationStatus.SUCCESS.toString());
    return operationRequestModel;
  }


}
