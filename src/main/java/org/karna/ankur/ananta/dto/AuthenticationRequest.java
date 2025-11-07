// AuthenticationRequest.java
package org.karna.ankur.ananta.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class AuthenticationRequest {
    private String username;
    private String password;
    private String email;
}
