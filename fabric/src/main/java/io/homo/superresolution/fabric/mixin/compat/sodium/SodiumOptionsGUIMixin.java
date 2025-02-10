package io.homo.superresolution.fabric.mixin.compat.sodium;

import com.google.common.collect.ImmutableList;
import io.homo.superresolution.common.gui.ConfigScreenBuilder;
import me.jellysquid.mods.sodium.client.gui.SodiumOptionsGUI;
import me.jellysquid.mods.sodium.client.gui.options.Option;
import me.jellysquid.mods.sodium.client.gui.options.OptionGroup;
import me.jellysquid.mods.sodium.client.gui.options.OptionImpl;
import me.jellysquid.mods.sodium.client.gui.options.OptionPage;
import me.jellysquid.mods.sodium.client.gui.options.control.Control;
import me.jellysquid.mods.sodium.client.gui.options.control.ControlValueFormatter;
import me.jellysquid.mods.sodium.client.gui.options.control.SliderControl;
import me.jellysquid.mods.sodium.client.gui.options.storage.MinecraftOptionsStorage;
import net.minecraft.client.Minecraft;
import net.minecraft.client.Options;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.List;

@Mixin(SodiumOptionsGUI.class)
public class SodiumOptionsGUIMixin extends Screen {
    @Shadow(remap = false)
    @Final
    private List<OptionPage> pages;
    @Unique
    private OptionPage page;

    protected SodiumOptionsGUIMixin(Component title) {
        super(title);
    }

    @Inject(
            method = "<init>",
            at = {@At("TAIL")}
    )
    private void onInit(Screen prevScreen, CallbackInfo ci) {
        Component shaderPacksTranslated = Component.translatable("superresolution.screen.config.name");
        //byd rso groups是空的根本不加进去
        this.page = new OptionPage(shaderPacksTranslated, ImmutableList.of(
                OptionGroup.createBuilder()
                        .add(OptionImpl.createBuilder(Integer.class, new MinecraftOptionsStorage())
                                .setBinding((Options o,Integer b)->{},(Options o)-> 1)
                                .setControl((option)-> new SliderControl(option, 0, Minecraft.getInstance().getWindow().calculateScale(0, Minecraft.getInstance().isEnforceUnicode()), 1, (a)-> Component.literal("")))
                                .setName(Component.literal(""))
                                .setTooltip(Component.literal(""))
                                .build()
                        )
                        .build()
                )
        );
        this.pages.add(this.page);
    }

    @Inject(
            method = "setPage",
            at = {@At("HEAD")},
            remap = false,
            cancellable = true
    )
    private void onSetPage(OptionPage page, CallbackInfo ci) {
        if (page == this.page) {
            this.minecraft.setScreen(ConfigScreenBuilder.create().build(this));
            ci.cancel();
        }
    }
}
