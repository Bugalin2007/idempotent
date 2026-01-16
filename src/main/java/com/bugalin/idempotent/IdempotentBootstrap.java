package com.bugalin.idempotent;

import com.bugalin.idempotent.commands.IdempotentCommand;
import io.papermc.paper.plugin.bootstrap.BootstrapContext;
import io.papermc.paper.plugin.bootstrap.PluginBootstrap;
import io.papermc.paper.plugin.bootstrap.PluginProviderContext;
import io.papermc.paper.plugin.lifecycle.event.types.LifecycleEvents;
import org.bukkit.plugin.java.JavaPlugin;

public class IdempotentBootstrap implements PluginBootstrap {
    @Override
    public JavaPlugin createPlugin(PluginProviderContext context) {
        return PluginBootstrap.super.createPlugin(context);
    }

    @Override
    public void bootstrap(BootstrapContext context) {
        //Register Commands
        context.getLifecycleManager().registerEventHandler(LifecycleEvents.COMMANDS, commands -> {
            commands.registrar().register(IdempotentCommand.createCommand());
        });
    }


}
