package me.eeshe.quests.config.registry;

import me.eeshe.quests.config.Message;

public class MessageRegistry extends ConfigRegistry<Message> {
  private static final MessageRegistry INSTANCE = new MessageRegistry();

  public static final Message TEST = new Message("test", true, "This is a test message");

  public static MessageRegistry getInstance() {
    return INSTANCE;
  }
}
