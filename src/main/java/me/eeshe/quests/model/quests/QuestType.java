package me.eeshe.quests.model.quests;

public enum QuestType {
  MINING,
  KILLING,
  RUNNING,
  EXPLORING;

  public static QuestType fromName(String name) {
    try {
      return QuestType.valueOf(name.toUpperCase());
    } catch (Exception e) {
      return null;
    }
  }
}
