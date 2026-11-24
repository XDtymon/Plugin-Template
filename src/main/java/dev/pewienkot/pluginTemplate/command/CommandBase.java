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
 * Provides a simple base for all command Trees and Branches
 * {@link viewPermission}, is a permission to view the command
 *
 */
public abstract class CommandBase {

    // ========================================= FIELDS

    protected final PluginTemplate plugin;

    protected CommandBase(PluginTemplate plugin) {
        this.plugin = plugin;
    }

    // ========================================= REGISTRY

    public abstract LiteralArgumentBuilder<CommandSourceStack> build();

    // ========================================= PERMISSIONS

    public abstract String viewPermission();

    // ========================================= METHODS

    /** @return Plugin's messages **/
    protected Messages messages() {
        return plugin.messages();
    }

    /** @return Executor of the command **/
    protected CommandSender executor(CommandSourceStack source) {
        if (source.getExecutor() instanceof Player p) return p;
        if (source.getExecutor() instanceof ConsoleCommandSender c) return c;
        if (source.getExecutor() instanceof RemoteConsoleCommandSender r) return r;
        return source.getSender();
    }

    /** @return <code>true</code>, if entity has permission, else <code>false</code> **/
    protected boolean perm(CommandSourceStack ctx, String permission) {
        return ctx.getSender() instanceof Player player && player.hasPermission(permission);
    }

    /** @return <code>true</code>, if entity has permission, else <code>false</code> **/
    protected boolean perm(Entity entity, String permission) {
        if (entity == null) return false;
        return entity instanceof Player player && player.hasPermission(permission);
    }

    /** @return <code>true</code>, if entity is a Player, else <code>false</code> **/
    protected boolean isPlayer(CommandSourceStack commandSourceStack) {
        return commandSourceStack instanceof Player;
    }

    /** @return Player from a command argument **/
    protected Player player(CommandContext<CommandSourceStack> ctx, String name) throws CommandSyntaxException {
        return ctx.getArgument(name, PlayerSelectorArgumentResolver.class)
                .resolve(ctx.getSource())
                .getFirst();
    }

}