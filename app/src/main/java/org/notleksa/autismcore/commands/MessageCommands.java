package org.notleksa.autismcore.commands;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;

public class MessageCommands implements CommandExecutor, TabCompleter {

    // spagettikoodia >:3

    private final MiniMessage mm = MiniMessage.miniMessage();

    private final Map<UUID, UUID> lastMessaged = new HashMap<>();
    private final Set<UUID> toggledOff = new HashSet<>();
    private final Map<UUID, Set<UUID>> blocked = new HashMap<>();

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command,
                             @NotNull String label, @NotNull String[] args) {

        if (!(sender instanceof Player)) {
            sender.sendMessage("§cOnly players can use this command.");
            return true;
        }

        Player player = (Player) sender;
        String cmd = command.getName().toLowerCase();

        switch (cmd) {
            case "msg":
                return handleMsg(player, args);
            case "r":
                return handleReply(player, args);
            case "msgtoggle":
                return handleToggle(player);
            case "msgblock":
                return handleBlock(player, args);
            default:
                return false;
        }
    }

    // /msg
    private boolean handleMsg(Player sender, String[] args) {
        if (args.length < 2) {
            sender.sendMessage(mm.deserialize("<red>Usage: /msg <player> <message>"));
            return true;
        }

        Player target = Bukkit.getPlayerExact(args[0]);
        if (target == null || !target.isOnline()) {
            sender.sendMessage(mm.deserialize("<red>That player is not online."));
            return true;
        }

        if (target.equals(sender)) {
            sender.sendMessage(mm.deserialize("<red>You cannot message yourself."));
            return true;
        }

        if (toggledOff.contains(target.getUniqueId())) {
            sender.sendMessage(mm.deserialize("<red>That player has messages disabled."));
            return true;
        }

        if (isBlocked(target, sender)) {
            sender.sendMessage(mm.deserialize("<red>You are blocked by that player."));
            return true;
        }

        String message = String.join(" ", Arrays.copyOfRange(args, 1, args.length));

        sendMessage(sender, target, message);

        lastMessaged.put(sender.getUniqueId(), target.getUniqueId());
        lastMessaged.put(target.getUniqueId(), sender.getUniqueId());

        return true;
    }

    // /r
    private boolean handleReply(Player sender, String[] args) {
        if (args.length < 1) {
            sender.sendMessage(mm.deserialize("<red>Usage: /r <message>"));
            return true;
        }

        UUID targetUUID = lastMessaged.get(sender.getUniqueId());
        if (targetUUID == null) {
            sender.sendMessage(mm.deserialize("<red>You have no one to reply to."));
            return true;
        }

        Player target = Bukkit.getPlayer(targetUUID);
        if (target == null || !target.isOnline()) {
            sender.sendMessage(mm.deserialize("<red>That player is no longer online."));
            return true;
        }

        if (toggledOff.contains(target.getUniqueId())) {
            sender.sendMessage(mm.deserialize("<red>That player has messages disabled."));
            return true;
        }

        if (isBlocked(target, sender)) {
            sender.sendMessage(mm.deserialize("<red>You are blocked by that player."));
            return true;
        }

        String message = String.join(" ", args);
        sendMessage(sender, target, message);

        lastMessaged.put(sender.getUniqueId(), target.getUniqueId());
        lastMessaged.put(target.getUniqueId(), sender.getUniqueId());

        return true;
    }

    // /msgtoggle
    private boolean handleToggle(Player player) {
        UUID uuid = player.getUniqueId();
        if (toggledOff.contains(uuid)) {
            toggledOff.remove(uuid);
            player.sendMessage(mm.deserialize("<green>Private messages enabled."));
        } else {
            toggledOff.add(uuid);
            player.sendMessage(mm.deserialize("<red>Private messages disabled."));
        }
        return true;
    }

    // /msgblock
    private boolean handleBlock(Player player, String[] args) {
        if (args.length != 1) {
            player.sendMessage(mm.deserialize("<red>Usage: /msgblock <player>"));
            return true;
        }

        Player target = Bukkit.getPlayerExact(args[0]);
        if (target == null) {
            player.sendMessage(mm.deserialize("<red>That player is not online."));
            return true;
        }

        UUID uuid = player.getUniqueId();
        blocked.putIfAbsent(uuid, new HashSet<>());
        Set<UUID> blockedSet = blocked.get(uuid);

        if (blockedSet.contains(target.getUniqueId())) {
            blockedSet.remove(target.getUniqueId());
            player.sendMessage(mm.deserialize("<yellow>You have <green>unblocked</green> <light_purple>" + target.getName() + "</light_purple>."));
        } else {
            blockedSet.add(target.getUniqueId());
            player.sendMessage(mm.deserialize("<yellow>You have <red>blocked</red> <light_purple>" + target.getName() + "</light_purple>."));
        }

        return true;
    }

    private void sendMessage(Player sender, Player target, String message) {
        Component senderMsg = mm.deserialize("<gray>[<aqua>You</aqua>] -> [<light_purple>" + target.getName() + "</light_purple>] <white>" + message);
        Component receiverMsg = mm.deserialize("<gray>[<light_purple>" + sender.getName() + "</light_purple>] -> [<aqua>You</aqua>] <white>" + message);

        sender.sendMessage(senderMsg);
        target.sendMessage(receiverMsg);
    }

    private boolean isBlocked(Player blocker, Player blockedPlayer) {
        Set<UUID> blockedSet = blocked.getOrDefault(blocker.getUniqueId(), Collections.emptySet());
        return blockedSet.contains(blockedPlayer.getUniqueId());
    }

    @Override
    public @Nullable List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command command,
                                                @NotNull String alias, @NotNull String[] args) {
        if (command.getName().equalsIgnoreCase("msg") && args.length == 1) {
            return Bukkit.getOnlinePlayers().stream().map(Player::getName).toList();
        }
        if (command.getName().equalsIgnoreCase("msgblock") && args.length == 1) {
            return Bukkit.getOnlinePlayers().stream().map(Player::getName).toList();
        }
        return List.of();
    }
}
