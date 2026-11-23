package dev.pewienkot.pluginTemplate.messages;

import net.kyori.adventure.audience.Audience;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.TextDecoration;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.kyori.adventure.text.minimessage.tag.resolver.TagResolver;
import org.bukkit.Bukkit;

//TODO: encomment this class
public class Message {

    private static final MiniMessage MINI = MiniMessage.miniMessage();

    private final String raw;

    private final TagResolver prefix;

    public Message(String raw, TagResolver prefix) {
        this.raw = raw;
        this.prefix = prefix;
    }

    public Parsed message(TagResolver... resolvers) {
        Component component = MINI.deserialize(
                raw, TagResolver.resolver(prefix, TagResolver.resolver(resolvers))
        ).decoration(TextDecoration.ITALIC, false);
        return new Parsed(component);
    }

    public Component component(TagResolver... resolvers) {
        return message(resolvers).component;
    }

    public String raw() {
        return raw;
    }

    public record Parsed(Component component) {

        public void send(Audience audience) {
            audience.sendMessage(component);
        }

        public void actionbar(Audience audience) {
            audience.sendActionBar(component);
        }

        public void console() {
            Bukkit.getConsoleSender().sendMessage(component);
        }
    }
}