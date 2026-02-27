package me.eeshe.quests.cache;

import java.util.UUID;
import me.eeshe.quests.model.questplayer.IQuestPlayer;

public class QuestPlayerCache extends ConcurrentHashMapCache<UUID, IQuestPlayer> {}
