package com.social.backend.test.model.factory;

import com.social.backend.model.chat.Message;
import com.social.backend.test.model.type.MessageType;
import com.social.backend.test.model.type.ModelType;
import com.social.backend.test.model.wrapper.ModelWrapper;
import com.social.backend.test.model.wrapper.message.Meeting;
import com.social.backend.test.model.wrapper.message.Ok;
import com.social.backend.test.model.wrapper.message.WhatsUp;

class MessageFactory extends AbstractFactory<Message> {

  @Override
  ModelWrapper<Message> createWrapper(ModelType<Message> type) {
    switch (Enum.valueOf(MessageType.class, type.name())) {
      case MEETING:
        return new Meeting();
      case OK:
        return new Ok();
      case WHATS_UP:
        return new WhatsUp();
      default:
        return null;
    }
  }

}
