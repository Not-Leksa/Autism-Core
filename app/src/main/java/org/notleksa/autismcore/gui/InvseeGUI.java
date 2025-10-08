// this shit is lowkey stolen from qwertzcore LMAO

package org.notleksa.autismcore.gui;

import org.notleksa.autismcore.AutismCore;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.inventory.InventoryDragEvent;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerAttemptPickupItemEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitTask;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextDecoration;

public class InvseeGUI implements Listener {
    private final AutismCore plugin;
    private final Player viewer;
    private final Player target;
    private final Inventory gui;
    private BukkitTask updateTask;

    public InvseeGUI(AutismCore plugin, Player viewer, Player target) {
        this.plugin = plugin;
        this.viewer = viewer;
        this.target = target;
        this.gui = Bukkit.createInventory(
            null,
            54,   // size
            Component.text(AutismCore.CORE_ICON + " ")
                .append(Component.text(target.getName() + "'s Inventory", NamedTextColor.LIGHT_PURPLE))
        );

        plugin.getServer().getPluginManager().registerEvents(this, plugin);
    }

    public void open() {
        updateGUI();
        viewer.openInventory(gui);
        startUpdateTask();
    }

    private void updateGUI() {
        // Set black glass panes
        ItemStack blackPane = new ItemStack(Material.BLACK_STAINED_GLASS_PANE);
        for (int i = 9; i < 18; i++) {
            gui.setItem(i, blackPane);
        }
        for (int i = 1; i < 5; i++) {
            gui.setItem(i, blackPane);
        }
        // Set inventory contents
        for (int i = 0; i < 36; i++) {
            gui.setItem(i + 18, target.getInventory().getItem(i+9));
        }

        // Set hotbar
        for (int i = 0; i < 9; i++) {
            gui.setItem(i + 45, target.getInventory().getItem(i));
        }

        // Set offhand
        gui.setItem(0, target.getInventory().getItemInOffHand());

        // Set armor
        ItemStack[] armor = target.getInventory().getArmorContents();
        for (int i = 0; i < 4; i++) {
            gui.setItem(i + 5, armor[i]);
        }
    }

    private void startUpdateTask() {
        updateTask = Bukkit.getScheduler().runTaskTimer(plugin, this::updateGUI, 1L, 1L);
    }

    @EventHandler
    public void onInventoryClick(InventoryClickEvent event) {
        if (event.getInventory() != gui) return;

        if (event.getRawSlot() >= 0 && event.getRawSlot() < 54) {
            if ((event.getRawSlot() >= 9 && event.getRawSlot() < 18) || (event.getRawSlot() >= 1 && event.getRawSlot() < 5)) {
                event.setCancelled(true);
                return;
            }

            Bukkit.getScheduler().runTask(plugin, () -> {
                if (event.getRawSlot() >= 18 && event.getRawSlot() < 54) {
                    int targetSlot = event.getRawSlot() - 18;
                    int column = targetSlot % 9;
                    if (targetSlot < 9) {
                        target.getInventory().setItem(9 + column, event.getCurrentItem());
                    }
                    else if (targetSlot >= 9 && targetSlot < 18) {
                        target.getInventory().setItem(18 + column, event.getCurrentItem());
                        }
                    else if (targetSlot >= 18 && targetSlot < 27) {
                        target.getInventory().setItem(27 + column, event.getCurrentItem());
                    }
                    else if (targetSlot >= 27 && targetSlot < 36) {
                        target.getInventory().setItem(column, event.getCurrentItem());
                    }
                } else if (event.getRawSlot() >= 5 && event.getRawSlot() < 9) {
                    int armorSlot = event.getRawSlot() - 5;
                    target.getInventory().setArmorContents(new ItemStack[]{
                            gui.getItem(5), gui.getItem(6), gui.getItem(7), gui.getItem(8)
                    });
                } else if (event.getRawSlot() == 0) {
                    target.getInventory().setItemInOffHand(event.getCurrentItem());
                }
                target.updateInventory();
            });
        }
    }

    @EventHandler
    public void onInventoryDrag(InventoryDragEvent event) {
        if (event.getInventory() != gui) return;

        for (int slot : event.getRawSlots()) {
            if ((slot >= 9 && slot < 18) || (slot >= 1 && slot < 5)) {
                event.setCancelled(true);
                return;
            }
        }

        Bukkit.getScheduler().runTask(plugin, () -> {
            for (int slot : event.getRawSlots()) {
                if (slot >= 18 && slot < 54) {
                    int targetSlot = slot - 18;
                    int column = targetSlot % 9;
                    if (targetSlot < 9) {
                        target.getInventory().setItem(9 + column, event.getNewItems().get(slot));
                    }
                    else if (targetSlot >= 9 && targetSlot < 18) {
                        target.getInventory().setItem(18 + column, event.getNewItems().get(slot));
                    }
                    else if (targetSlot >= 18 && targetSlot < 27) {
                        target.getInventory().setItem(27 + column, event.getNewItems().get(slot));
                    }
                    else if (targetSlot >= 27 && targetSlot < 36) {
                        target.getInventory().setItem(column, event.getNewItems().get(slot));
                    }
                } else if (slot >= 5 && slot < 9) {
                    target.getInventory().setArmorContents(new ItemStack[]{
                            gui.getItem(5), gui.getItem(6), gui.getItem(7), gui.getItem(8)
                    });
                } else if (slot == 0) {
                    target.getInventory().setItemInOffHand(event.getNewItems().get(slot));
                }
            }
            target.updateInventory();
        });
    }

    @EventHandler
    public void onInventoryClose(InventoryCloseEvent event) {
        if (event.getInventory() != gui) return;

        updateTask.cancel();
        InventoryCloseEvent.getHandlerList().unregister(this);
        InventoryClickEvent.getHandlerList().unregister(this);
        InventoryDragEvent.getHandlerList().unregister(this);
        PlayerDropItemEvent.getHandlerList().unregister(this);
        PlayerAttemptPickupItemEvent.getHandlerList().unregister(this);
    }

    @EventHandler
    public void onPlayerDropItem(PlayerDropItemEvent event) {
        if (event.getPlayer() == target) {
            Bukkit.getScheduler().runTask(plugin, this::updateGUI);
        }
    }

    @EventHandler
    public void onPlayerPickupItem(PlayerAttemptPickupItemEvent event) {
        if (event.getPlayer() == target) {
            Bukkit.getScheduler().runTask(plugin, this::updateGUI);
        }
    }
}