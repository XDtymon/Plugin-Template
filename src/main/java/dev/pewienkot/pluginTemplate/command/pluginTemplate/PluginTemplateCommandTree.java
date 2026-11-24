package dev.pewienkot.pluginTemplate.command.pluginTemplate;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.tree.LiteralCommandNode;
import dev.pewienkot.pluginTemplate.PluginTemplate;
import dev.pewienkot.pluginTemplate.command.CommandBase;
import io.papermc.paper.command.brigadier.CommandSourceStack;
import io.papermc.paper.command.brigadier.Commands;

import java.util.List;

/**
 * Simple CommandTree
 * This one is used for <code>reload</code>s
 */
public class PluginTemplateCommandTree extends CommandBase {

    /** List of Tree's branches **/
    private final List<CommandBase> branches;

    // ========================================= REGISTRY

    public PluginTemplateCommandTree(PluginTemplate plugin) {
        super(plugin);

        this.branches = List.of(
            // COMMAND BRANCHES
        );
    }

    // ========================================= REGISTER

    /** @return CommandTree's root  **/
    public LiteralCommandNode<CommandSourceStack> create() {

        String mainCommand = "pluginTemplate";

        // Main command
        var root = Commands.literal(mainCommand)
                .requires(ctx -> perm(ctx, viewPermission()))
                .then(Commands.literal("reload")
                        .executes(ctx -> {
                            return reload(ctx.getSource());
                        })

                );

        branches.forEach(branch -> {
            root.then(branch.build());
        });

        return root.build();
    }

    // =================================== UTILITY

    @Override
    public com.mojang.brigadier.builder.LiteralArgumentBuilder<CommandSourceStack> build() {
        throw new UnsupportedOperationException("use create()");
    }

    /** @return View permission value of the command **/
    @Override
    public String viewPermission() {
        return "pluginTemplate.admin";
    }

    // ==================================== EXECUTE

    public int reload(CommandSourceStack source) {
        plugin.reload();
        messages().PLUGIN_RELOAD.message().send(source.getSender());
        return Command.SINGLE_SUCCESS;
    }

}