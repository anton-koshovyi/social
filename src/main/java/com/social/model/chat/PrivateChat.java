package com.social.model.chat;

import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;

@Entity
@DiscriminatorValue("private")
public class PrivateChat extends Chat {
}
