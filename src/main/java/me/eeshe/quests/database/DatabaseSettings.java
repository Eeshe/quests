package me.eeshe.quests.database;

public record DatabaseSettings(
    String host, int port, String user, String password, String database) {}
