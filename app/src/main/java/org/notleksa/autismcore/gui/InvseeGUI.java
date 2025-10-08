package org.notleksa.autismcore.gui;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.inventory.InventoryDragEvent;
import org.bukkit.event.player.PlayerAttemptPickupItemEvent;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitTask;
import org.notleksa.autismcore.AutismCore;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;

public class InvseeGUI implements Listener {

    private final AutismCore plugin;
    private final Player viewer;
    private final Player target;
    private final Inventory inventoryView;
    private BukkitTask refreshTask;

    public InvseeGUI(AutismCore plugin, Player viewer, Player target) {
        this.plugin = plugin;
        this.viewer = viewer;
        this.target = target;

        this.inventoryView = Bukkit.createInventory(
                null,
                54,
                Component.text(AutismCore.CORE_ICON + " ")
                        .append(Component.text(target.getName() + "'s Inventory", NamedTextColor.LIGHT_PURPLE))
        );

        Bukkit.getPluginManager().registerEvents(this, plugin);
    }

    // opens inventory view
    public void open() {
        refreshContents();
        viewer.openInventory(inventoryView);
        beginAutoRefresh();
    }

    // updates it every tick
    private void beginAutoRefresh() {
        refreshTask = Bukkit.getScheduler().runTaskTimer(plugin, this::refreshContents, 1L, 1L);
    }

    // updates its items every tick
    private void refreshContents() {
        ItemStack filler = new ItemStack(Material.BLACK_STAINED_GLASS_PANE);

        // border panes
        for (int i = 9; i < 18; i++) inventoryView.setItem(i, filler);
        for (int i = 1; i < 5; i++) inventoryView.setItem(i, filler);

        // main inventory (slots 9–44)
        for (int i = 0; i < 36; i++) {
            inventoryView.setItem(i + 18, target.getInventory().getItem(i + 9));
        }

        // hotbar (slots 0–8)
        for (int i = 0; i < 9; i++) {
            inventoryView.setItem(i + 45, target.getInventory().getItem(i));
        }

        // offhand
        inventoryView.setItem(0, target.getInventory().getItemInOffHand());

        // armor pieces
        ItemStack[] armor = target.getInventory().getArmorContents();
        for (int i = 0; i < armor.length; i++) {
            inventoryView.setItem(i + 5, armor[i]);
        }
    }

    @EventHandler
    public void handleClick(InventoryClickEvent event) {
        if (event.getInventory() != inventoryView) return;

        int slot = event.getRawSlot();

        // prevent editing decorative zones
        if ((slot >= 9 && slot < 18) || (slot >= 1 && slot < 5)) {
            event.setCancelled(true);
            return;
        }

        Bukkit.getScheduler().runTask(plugin, () -> {
            if (slot >= 18 && slot < 54) {
                int index = slot - 18;
                int col = index % 9;
                int targetSlot = switch (index / 9) {
                    case 0 -> 9 + col;
                    case 1 -> 18 + col;
                    case 2 -> 27 + col;
                    case 3 -> col;
                    default -> -1;
                };
                if (targetSlot >= 0) target.getInventory().setItem(targetSlot, event.getCurrentItem());
            } else if (slot >= 5 && slot < 9) {
                target.getInventory().setArmorContents(new ItemStack[]{
                        inventoryView.getItem(5),
                        inventoryView.getItem(6),
                        inventoryView.getItem(7),
                        inventoryView.getItem(8)
                });
            } else if (slot == 0) {
                target.getInventory().setItemInOffHand(event.getCurrentItem());
            }
            target.updateInventory();
        });
    }

    @EventHandler
    public void handleDrag(InventoryDragEvent event) {
        if (event.getInventory() != inventoryView) return;

        for (int slot : event.getRawSlots()) {
            if ((slot >= 9 && slot < 18) || (slot >= 1 && slot < 5)) {
                event.setCancelled(true);
                return;
            }
        }

        Bukkit.getScheduler().runTask(plugin, () -> {
            for (int slot : event.getRawSlots()) {
                if (slot >= 18 && slot < 54) {
                    int index = slot - 18;
                    int col = index % 9;
                    int targetSlot = switch (index / 9) {
                        case 0 -> 9 + col;
                        case 1 -> 18 + col;
                        case 2 -> 27 + col;
                        case 3 -> col;
                        default -> -1;
                    };
                    if (targetSlot >= 0) {
                        target.getInventory().setItem(targetSlot, event.getNewItems().get(slot));
                    }
                } else if (slot >= 5 && slot < 9) {
                    target.getInventory().setArmorContents(new ItemStack[]{
                            inventoryView.getItem(5),
                            inventoryView.getItem(6),
                            inventoryView.getItem(7),
                            inventoryView.getItem(8)
                    });
                } else if (slot == 0) {
                    target.getInventory().setItemInOffHand(event.getNewItems().get(slot));
                }
            }
            target.updateInventory();
        });
    }

    @EventHandler
    public void handleClose(InventoryCloseEvent event) {
        if (event.getInventory() != inventoryView) return;

        if (refreshTask != null) refreshTask.cancel();
        InventoryClickEvent.getHandlerList().unregister(this);
        InventoryDragEvent.getHandlerList().unregister(this);
        InventoryCloseEvent.getHandlerList().unregister(this);
        PlayerDropItemEvent.getHandlerList().unregister(this);
        PlayerAttemptPickupItemEvent.getHandlerList().unregister(this);
    }

    @EventHandler
    public void handleDrop(PlayerDropItemEvent event) {
        if (event.getPlayer() == target) {
            Bukkit.getScheduler().runTask(plugin, this::refreshContents);
        }
    }

    @EventHandler
    public void handlePickup(PlayerAttemptPickupItemEvent event) {
        if (event.getPlayer() == target) {
            Bukkit.getScheduler().runTask(plugin, this::refreshContents);
        }
    }
}
