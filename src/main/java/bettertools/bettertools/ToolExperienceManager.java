package bettertools.bettertools;

import net.md_5.bungee.api.ChatMessageType;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;

import java.util.*;

public class ToolExperienceManager implements Listener {
    private final Map<UUID, Map<Material, Integer>> playerExperience = new HashMap<>();
    private final Map<UUID, Map<Material, Integer>> playerLevels = new HashMap<>();
    private final Better_tools plugin;

    public ToolExperienceManager(Better_tools plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onBlockBreak(BlockBreakEvent event) {
        Player player = event.getPlayer();
        ItemStack tool = player.getInventory().getItemInMainHand();

        if (!isCustomTool(tool)) {
            player.sendMessage("You need a custom tool to gain experience");
            return;
        }

        Material toolType = tool.getType();
        int experience = getPlayerExperience(player.getUniqueId(), toolType) + 1;
        setPlayerExperience(player.getUniqueId(), toolType, experience);

        int level = getPlayerLevel(player.getUniqueId(), toolType);

        player.spigot().sendMessage(ChatMessageType.ACTION_BAR, TextComponent.fromLegacy("Experience: " + experience + "/100"));

        if (experience >= 100) {
            level++;
            setPlayerLevel(player.getUniqueId(), toolType, level);

            upgradeTool(player, tool);
            setPlayerExperience(player.getUniqueId(), toolType, 0);
        }

        updateToolLore(tool, toolType, level, experience);


    }

    private boolean isCustomTool(ItemStack tool) {
        if (tool == null || tool.getItemMeta() == null) {
            return false;
        }

        NamespacedKey key = new NamespacedKey(plugin, "BetterTools");
        PersistentDataContainer container = tool.getItemMeta().getPersistentDataContainer();
        return container.has(key, PersistentDataType.STRING) && Objects.equals(container.get(key, PersistentDataType.STRING), "true");
    }

    private int getPlayerExperience(UUID playerId, Material toolType) {
        return playerExperience.computeIfAbsent(playerId, k -> new HashMap<>()).getOrDefault(toolType, 0);
    }

    private void setPlayerExperience(UUID playerId, Material toolType, int experience) {
        playerExperience.computeIfAbsent(playerId, k -> new HashMap<>()).put(toolType, experience);
    }

    private int getPlayerLevel(UUID playerId, Material toolType) {
        return playerLevels.computeIfAbsent(playerId, k -> new HashMap<>()).getOrDefault(toolType, 0);
    }

    private void setPlayerLevel(UUID playerId, Material toolType, int level) {
        playerLevels.computeIfAbsent(playerId, k -> new HashMap<>()).put(toolType, level);
    }

    private void updateToolLore(ItemStack tool, Material toolType, int level, int experience) {
        ItemMeta meta = tool.getItemMeta();
        List<String> lore = new ArrayList<>();

        lore.add(ChatColor.GRAY + "Level: " + level);
        lore.add(ChatColor.GRAY + "Experience: " + experience + "/100");

        assert meta != null;
        meta.setLore(lore);
        tool.setItemMeta(meta);
    }

    private void upgradeTool(Player player, ItemStack tool) {
        Material toolType = tool.getType();
        Material upgradedToolType = getUpgradedToolType(toolType);

        if (upgradedToolType == null) {
            return;
        }

        ItemStack upgradedTool = new ItemStack(upgradedToolType);
        upgradedTool.setItemMeta(tool.getItemMeta());
        player.getInventory().setItemInMainHand(upgradedTool);
    }

    private Material getUpgradedToolType(Material toolType) {
        switch (toolType) {
            case WOODEN_PICKAXE:
                return Material.STONE_PICKAXE;
            case STONE_PICKAXE:
                return Material.IRON_PICKAXE;
            case IRON_PICKAXE:
                return Material.DIAMOND_PICKAXE;
            case WOODEN_AXE:
                return Material.STONE_AXE;
            case STONE_AXE:
                return Material.IRON_AXE;
            case IRON_AXE:
                return Material.DIAMOND_AXE;
            case WOODEN_SHOVEL:
                return Material.STONE_SHOVEL;
            case STONE_SHOVEL:
                return Material.IRON_SHOVEL;
            case IRON_SHOVEL:
                return Material.DIAMOND_SHOVEL;
            case WOODEN_HOE:
                return Material.STONE_HOE;
            case STONE_HOE:
                return Material.IRON_HOE;
            case IRON_HOE:
                return Material.DIAMOND_HOE;
            default:
                return null;
        }
    }

}
