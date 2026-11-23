package dev.pewienkot.pluginTemplate.command;

import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import dev.pewienkot.pluginTemplate.PluginTemplate;
import dev.pewienkot.pluginTemplate.messages.Messages;
import io.papermc.paper.command.brigadier.CommandSourceStack;
import io.papermc.paper.command.brigadier.argument.resolvers.selector.PlayerSelectorArgumentResolver;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.command.RemoteConsoleCommandSender;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;

/**
 * TODO: comment this shi
 */
public abstract class CommandBase {

    protected final PluginTemplate plugin;

    protected CommandBase(PluginTemplate plugin) {
        this.plugin = plugin;
    }

    public abstract LiteralArgumentBuilder<CommandSourceStack> build();

    public abstract String viewPermission();

    protected Messages messages() {
        return plugin.messages();
    }

    protected CommandSender executor(CommandSourceStack source) {
        if (source.getExecutor() instanceof Player p) return p;
        if (source.getExecutor() instanceof ConsoleCommandSender c) return c;
        if (source.getExecutor() instanceof RemoteConsoleCommandSender r) return r;
        return source.getSender();
    }

    protected boolean perm(CommandSourceStack ctx, String permission) {
        return ctx.getSender() instanceof Player player && player.hasPermission(permission);
    }

    protected boolean perm(Entity entity, String permission) {
        if (entity == null) return false;

        return entity instanceof Player player && player.hasPermission(permission);
    }

    protected boolean isPlayer(CommandSourceStack commandSourceStack) {
        return commandSourceStack instanceof Player;
    }

    protected Player player(CommandContext<CommandSourceStack> ctx, String name) throws CommandSyntaxException {
        return ctx.getArgument(name, PlayerSelectorArgumentResolver.class)
                .resolve(ctx.getSource())
                .getFirst();
    }
}