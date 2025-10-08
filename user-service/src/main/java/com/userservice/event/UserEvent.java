package com.userservice.event;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserEvent {

    @JsonProperty("operation")
    private String operation;

    @JsonProperty("email")
    private String email;
}
