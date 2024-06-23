package bettertools.bettertools;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabExecutor;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.ArrayList;
import java.util.List;

import java.util.Objects;

public final class Better_tools extends JavaPlugin implements TabExecutor {

    @Override
    public void onEnable() {
        if (!getDataFolder().exists()) {
            getDataFolder().mkdirs();
        }

        Objects.requireNonNull(this.getCommand("bt")).setTabCompleter(new BetterToolsTabCompleter());
        Objects.requireNonNull(this.getCommand("bt")).setExecutor(this);
        getServer().getPluginManager().registerEvents(new ToolExperienceManager(this), this);

    }

    @Override
    public void onDisable() {

    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (sender instanceof Player) {
            Player player = (Player) sender;
            if (args.length == 0) {
                player.sendMessage(ChatColor.RED + "Usage: /bt <item> (all, hoe, pickaxe, ...)");
                return false;
            }

            String itemType = args[0];
            switch (itemType.toLowerCase()) {
                case "all":
                    giveTool(player, Material.WOODEN_SWORD, true);
                    giveTool(player, Material.WOODEN_PICKAXE, true);
                    giveTool(player, Material.WOODEN_AXE, true);
                    giveTool(player, Material.WOODEN_SHOVEL, true);
                    giveTool(player, Material.WOODEN_HOE, true);
                    break;
                case "sword":
                    giveTool(player, Material.WOODEN_SWORD, false);
                    break;
                case "pickaxe":
                    giveTool(player, Material.WOODEN_PICKAXE, false);
                    break;
                case "axe":
                    giveTool(player, Material.WOODEN_AXE, false);
                    break;
                case "shovel":
                    giveTool(player, Material.WOODEN_SHOVEL, false);
                    break;
                case "hoe":
                    giveTool(player, Material.WOODEN_HOE, false);
                    break;
                default:
                    player.sendMessage(ChatColor.RED + "Invalid item type");
                    break;
            }
            return true;
        }
        return false;
    }

    private void giveTool(Player player, Material material, Boolean getAll) {
        String displayName = getDisplayName(material);

        for (ItemStack item : player.getInventory().getContents()) {
            if (item != null && item.getType() == material && item.getItemMeta() != null && item.getItemMeta().hasDisplayName() &&
                    item.getItemMeta().getDisplayName().equals(displayName)) {
                if (!getAll) {
                    player.sendMessage(ChatColor.YELLOW + "You already have this tool.");
                }
                return;
            }
        }

        ItemStack tool = new ItemStack(material);
        ItemMeta meta = tool.getItemMeta();

        assert meta != null;
        meta.setDisplayName(displayName);
        meta.setUnbreakable(true);

        NamespacedKey key = new NamespacedKey(this, "BetterTools");
        PersistentDataContainer container = meta.getPersistentDataContainer();
        container.set(key, PersistentDataType.STRING, "true");

        container.set(new NamespacedKey(this, "owner"), PersistentDataType.STRING, player.getUniqueId().toString());

        List<String> lore = new ArrayList<>();
        lore.add(ChatColor.GRAY + "Level: 1");
        lore.add(ChatColor.GRAY + "Experience: 0/100");
        meta.setLore(lore);

        tool.setItemMeta(meta);

        player.getInventory().addItem(tool);

        player.sendMessage(ChatColor.GREEN + "You have received a " + tool.getItemMeta().getDisplayName());
    }

    public String getDisplayName(Material material) {
        String[] words = material.name().toLowerCase().split("_");
        String lastWord = words[words.length - 1];

        String capitalizedLastWord = Character.toUpperCase(lastWord.charAt(0)) + lastWord.substring(1);

        return ChatColor.GOLD + "Better " + capitalizedLastWord;
    }

}
