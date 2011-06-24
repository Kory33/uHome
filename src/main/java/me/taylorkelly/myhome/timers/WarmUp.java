package me.taylorkelly.myhome.timers;

import java.util.HashMap;

import me.taylorkelly.myhome.Home;
import me.taylorkelly.myhome.HomePermissions;
import me.taylorkelly.myhome.HomeSettings;

import org.bukkit.ChatColor;
import org.bukkit.Server;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;

public class WarmUp {
    private static HashMap<String, Integer> players = new HashMap<String, Integer>();

    public static void addPlayer(Player player, Home home, Plugin plugin) {
        if (HomePermissions.bypassWarming(player)) {
            home.warp(player, plugin.getServer());
            return;
        }
        
        if (HomeSettings.warmUp > 0) {
            if (players.containsKey(player.getName())) {
                plugin.getServer().getScheduler().cancelTask(players.get(player.getName()));
            }
            
            int timer;
        	if (HomeSettings.timerByPerms) {
				timer = HomePermissions.integer(player, "myhome.timer.warmup", HomeSettings.warmUp);
				if(HomeSettings.additionalTime) {
					timer += HomeSettings.warmUp;
				}
			} else {
				timer = HomeSettings.warmUp;
			}
            
            if (HomeSettings.warmUpNotify && timer > 0) {
                player.sendMessage(ChatColor.RED + "You will have to warm up for " + timer + " secs");
            }

            
            int taskIndex = plugin.getServer().getScheduler().scheduleSyncDelayedTask(plugin, new WarmTask(player, home, plugin.getServer()), timer*20);
            players.put(player.getName(), taskIndex);
        } else {
            home.warp(player, plugin.getServer());
        }
    }

    public static boolean playerHasWarmed(Player player) {
        return players.containsKey(player.getName());
    }

    private static void sendPlayer(Player player, Home home, Server server) {
        int timer;
    	if (HomeSettings.timerByPerms) {
			timer = HomePermissions.integer(player, "myhome.timer.warmup", HomeSettings.warmUp);
			if(HomeSettings.additionalTime) {
				timer += HomeSettings.warmUp;
			}
		} else {
			timer = HomeSettings.warmUp;
		}
    	if (HomeSettings.warmUpNotify && timer > 0)
            player.sendMessage(ChatColor.RED + "You have warmed up! Sending you /home");
        home.warp(player, server);
    }

    private static class WarmTask implements Runnable {
        private Player player;
        private Home home;
        private Server server;

        public WarmTask(Player player, Home home, Server server) {
            this.player = player;
            this.home = home;
            this.server = server;
        }

        public void run() {
            sendPlayer(player, home, server);
            players.remove(player.getName());
        }
    }
}
