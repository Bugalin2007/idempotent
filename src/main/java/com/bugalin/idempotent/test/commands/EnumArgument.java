package com.bugalin.idempotent.test.commands;

import com.mojang.brigadier.arguments.ArgumentType;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.DynamicCommandExceptionType;
import com.mojang.brigadier.suggestion.Suggestions;
import com.mojang.brigadier.suggestion.SuggestionsBuilder;
import io.papermc.paper.command.brigadier.MessageComponentSerializer;
import io.papermc.paper.command.brigadier.argument.CustomArgumentType;
import net.kyori.adventure.text.Component;
import org.jspecify.annotations.NullMarked;

import java.util.Locale;
import java.util.concurrent.CompletableFuture;

@NullMarked
public class EnumArgument<T extends Enum<T>> implements CustomArgumentType.Converted<T, String> {
    private final Class<T> enumClass;

    public static <T extends Enum<T>> EnumArgument<T> of(Class<T> enumClass) {
        return new EnumArgument<>(enumClass);
    }

    private EnumArgument(Class<T> enumClass) {
        this.enumClass = enumClass;
    }

    private static final DynamicCommandExceptionType ERROR_INVALID_FLAVOR = new DynamicCommandExceptionType(enumEntry -> {
        return MessageComponentSerializer.message().serialize(Component.text(enumEntry + " is not a valid value!"));
    });

    @Override
    public T convert(String nativeType) throws CommandSyntaxException {
        try {
            return Enum.valueOf(enumClass,nativeType.toUpperCase(Locale.ROOT));
        } catch (IllegalArgumentException ignored) {
            throw ERROR_INVALID_FLAVOR.create(nativeType);
        }
    }

    @Override
    public <S> CompletableFuture<Suggestions> listSuggestions(CommandContext<S> context, SuggestionsBuilder builder) {
        for (T enumEntry : enumClass.getEnumConstants()) {
            String name = enumEntry.toString();

            // Only suggest if the enumEntry name matches the user input
            if (name.startsWith(builder.getRemainingLowerCase())) {
                builder.suggest(enumEntry.toString());
            }
        }

        return builder.buildFuture();
    }

    @Override
    public ArgumentType<String> getNativeType() {
        return StringArgumentType.word();
    }
}