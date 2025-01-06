package com.crazy.service;

import com.crazy.dto.UserLoginDTO;
import com.crazy.entity.User;

public interface UserService {

    User wxLogin(UserLoginDTO userLoginDTO);
}
