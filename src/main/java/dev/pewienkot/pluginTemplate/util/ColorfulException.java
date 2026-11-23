package dev.pewienkot.pluginTemplate.util;

import dev.pewienkot.pluginTemplate.PluginTemplate;
import dev.pewienkot.pluginTemplate.messages.Message;
import dev.pewienkot.pluginTemplate.messages.Messages;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.tag.resolver.TagResolver;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;

import java.util.function.Function;

/**
 * TODO: comment this shi
 */
public abstract class ColorfulException extends RuntimeException {

  private final transient Message message;
  private final transient TagResolver placeholders;

  public ColorfulException(Message message, TagResolver... placeholders) {
    super(null, null, false, false);
    this.message = message;
    this.placeholders = TagResolver.resolver(placeholders);
  }

  public ColorfulException(PluginTemplate plugin, Function<Messages, Message> factory, TagResolver... placeholders) {
    this(factory.apply(plugin.messages()), placeholders);
  }

  public Component component() {
    return message.message(placeholders).component();
  }

  @Override
  public String getMessage() {
    return LegacyComponentSerializer.legacySection().serialize(component());
  }
}
