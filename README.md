# Quests

## Commands
### Main Commands
/quests
- Permission: quests.player
- Usage: /quests
- Description: Opens the main quest list menu for players.
/questsadmin
- Permission: quests.admin
- Usage: /questsadmin
- Description: Admin command for managing quests and player progress.
- Subcommands:
  - /questsadmin reload
    - Permission: quests.admin
    - Usage: `/questsadmin reload`
    - Description: Reloads all configuration files.
  - /questsadmin setprogress
    - Permission: quests.admin
    - Usage: `/questsadmin setprogress <player> <quest_id> <progress>`
    - Description: Sets a player's progress for a specific quest.
  - /questsadmin reset
    - Permission: quests.admin
    - Usage: `/questsadmin reset <player> <quest_id>`
    - Description: Resets a player's progress for a specific quest or all quests.
## Configuration Options
### config.yml
Database Settings:
- database-settings.host - Database host (default: localhost)
- database-settings.port - Database port (default: 27017)
- database-settings.user - Database username (default: user)
- database-settings.password - Database password (default: password)
- database-settings.database - Database name (default: quests)
  
Quests:
- quests.`<quest_id>`.type - Type of quest (RUNNING, MINING, KILLING, EXPLORING)
- quests.`<quest_id>`.icon - Item icon for the quest
- quests.`<quest_id>`.goal - Goal amount for the quest
- quests.`<quest_id>`.target - Target for the quest (for KILLING and EXPLORING quests)
- quests.`<quest_id>`.rewards.commands.`<index>`.commands - List of commands to execute as rewards
- quests.`<quest_id>`.rewards.items.`<index>`.item - Item to give as reward
  
messages.yml

- message_key.enabled - Whether the message is enabled
- message_key.value - The actual message text
  
menus.yml

- menu_key.size - Size of the menu (must be multiple of 9 between 9 and 54)
- menu_key.title - Title of the menu
- menu_key.frame-item - Item used for menu frame
- menu_key.filler-item - Item used to fill empty slots
- menu_key.previous-page-item - Item for previous page button
- menu_key.next-page-item - Item for next page button
- menu_key.additional - Additional menu configuration properties
  
sounds.yml
- sound_key.enabled - Whether the sound is enabled
- sound_key.sound - Sound effect to play
- sound_key.volume - Sound volume (0.0-1.0)
- sound_key.pitch - Sound pitch (0.0-2.0)

## Installation Instructions

1. Place the plugin JAR file in your server's plugins folder
2. Start the server for the first time to generate default configuration files
3. Configure the MongoDB database settings in config.yml:
   - Set database-settings.host to your MongoDB host
   - Set database-settings.port to your MongoDB port
   - Set database-settings.user to your MongoDB username
   - Set database-settings.password to your MongoDB password
   - Set database-settings.database to your MongoDB database name
4. **Important**: Once the plugin is in the plugins folder, you need to turn on the server, configure the MongoDB database in the config.yml file, then restart the server in order for the plugin to successfully connect to the MongoDB databas
