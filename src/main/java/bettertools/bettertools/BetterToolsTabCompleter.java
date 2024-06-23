package bettertools.bettertools;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;

import java.util.ArrayList;
import java.util.List;

public class BetterToolsTabCompleter implements TabCompleter {
    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
        List<String> suggestions = new ArrayList<>();

        if (args.length == 1) {
            suggestions.add("all");
            suggestions.add("hoe");
            suggestions.add("pickaxe");
            suggestions.add("axe");
            suggestions.add("shovel");
            suggestions.add("sword");
        }

        return suggestions;
    }
}
