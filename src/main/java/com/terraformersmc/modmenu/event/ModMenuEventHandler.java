package com.terraformersmc.modmenu.event;

import com.terraformersmc.modmenu.ModMenu;
import com.terraformersmc.modmenu.api.ModMenuApi;
import com.terraformersmc.modmenu.config.ModMenuConfig;
import com.terraformersmc.modmenu.gui.ModsScreen;
import com.terraformersmc.modmenu.gui.widget.ModMenuButtonWidget;
import com.terraformersmc.modmenu.gui.widget.ModMenuTexturedButtonWidget;
import com.terraformersmc.modmenu.mixin.ScreenAccessor;
import net.fabricmc.fabric.api.client.screen.v1.ScreenEvents;
import net.fabricmc.fabric.api.client.screen.v1.Screens;
import net.fabricmc.fabric.impl.client.screen.ScreenExtensions;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.Drawable;
import net.minecraft.client.gui.screen.GameMenuScreen;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.TitleScreen;
import net.minecraft.client.gui.widget.ClickableWidget;
import net.minecraft.text.Text;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.Identifier;

import java.util.List;

public class ModMenuEventHandler {
	private static final Identifier FABRIC_ICON_BUTTON_LOCATION = new Identifier(ModMenu.MOD_ID, "textures/gui/mods_button.png");

	public static void register() {
		ScreenEvents.AFTER_INIT.register(ModMenuEventHandler::afterScreenInit);
	}

	public static void afterScreenInit(MinecraftClient client, Screen screen, int scaledWidth, int scaledHeight) {
		if (screen instanceof TitleScreen) {
			afterTitleScreenInit(screen);
		} else if (screen instanceof GameMenuScreen) {
			afterGameMenuScreenInit(screen);
		}
	}

	private static void afterTitleScreenInit(Screen screen) {
		if (ModMenuConfig.MODIFY_TITLE_SCREEN.getValue()) {
			final List<ClickableWidget> buttons = Screens.getButtons(screen);
			final List<Drawable> drawables = ((ScreenAccessor)screen).getDrawables();
			int modsButtonIndex = -1;
			final int spacing = 24;
			final int buttonsY = screen.height / 4 + 48;
			for (int i = 0; i < buttons.size(); i++) {
				ClickableWidget button = buttons.get(i);
				if (ModMenuConfig.MODS_BUTTON_STYLE.getValue() == ModMenuConfig.ModsButtonStyle.CLASSIC) {
					shiftButtons(button, modsButtonIndex == -1, spacing);
				}
				if (buttonHasText(button, "menu.online")) {
					if (ModMenuConfig.MODS_BUTTON_STYLE.getValue() == ModMenuConfig.ModsButtonStyle.REPLACE_REALMS) {
						setButton(i, new ModMenuButtonWidget(button.x, button.y, button.getWidth(), button.getHeight(), ModMenuApi.createModsButtonText(), screen),buttons,drawables);
					} else {
						if (ModMenuConfig.MODS_BUTTON_STYLE.getValue() == ModMenuConfig.ModsButtonStyle.SHRINK) {
							button.setWidth(98);
						}
						modsButtonIndex = i + 1;
					}
				}

			}
			if (modsButtonIndex != -1) {
				if (ModMenuConfig.MODS_BUTTON_STYLE.getValue() == ModMenuConfig.ModsButtonStyle.CLASSIC) {
					addButton(modsButtonIndex, new ModMenuButtonWidget(screen.width / 2 - 100, buttonsY + spacing * 3 - (spacing / 2), 200, 20, ModMenuApi.createModsButtonText(), screen),buttons,drawables);
				} else if (ModMenuConfig.MODS_BUTTON_STYLE.getValue() == ModMenuConfig.ModsButtonStyle.SHRINK) {
					addButton(modsButtonIndex, new ModMenuButtonWidget(screen.width / 2 + 2, buttonsY + spacing * 2, 98, 20, ModMenuApi.createModsButtonText(), screen),buttons,drawables);
				} else if (ModMenuConfig.MODS_BUTTON_STYLE.getValue() == ModMenuConfig.ModsButtonStyle.ICON) {
					addButton(modsButtonIndex, new ModMenuTexturedButtonWidget(screen.width / 2 + 104, buttonsY + spacing * 2, 20, 20, 0, 0, FABRIC_ICON_BUTTON_LOCATION, 32, 64, button -> MinecraftClient.getInstance().openScreen(new ModsScreen(screen)), ModMenuApi.createModsButtonText()),buttons,drawables);
				}
			}
		}
	}


	private static void afterGameMenuScreenInit(Screen screen) {
		if (ModMenuConfig.MODIFY_GAME_MENU.getValue()) {
			final List<ClickableWidget> buttons = Screens.getButtons(screen);
			final List<Drawable> drawables = ((ScreenAccessor)screen).getDrawables();
			int modsButtonIndex = -1;
			final int spacing = 24;
			final int buttonsY = screen.height / 4 + 8;
			ModMenuConfig.ModsButtonStyle style = ModMenuConfig.MODS_BUTTON_STYLE.getValue().forGameMenu();
			for (int i = 0; i < buttons.size(); i++) {
				ClickableWidget button = buttons.get(i);
				if (style == ModMenuConfig.ModsButtonStyle.CLASSIC) {
					shiftButtons(button, modsButtonIndex == -1, spacing);
				}
				if (buttonHasText(button, "menu.reportBugs")) {
					modsButtonIndex = i + 1;
					if (style == ModMenuConfig.ModsButtonStyle.SHRINK) {
						setButton(i, new ModMenuButtonWidget(button.x, button.y, button.getWidth(), button.getHeight(), ModMenuApi.createModsButtonText(), screen), buttons, drawables);
					} else {
						modsButtonIndex = i + 1;
					}
				}
			}
			if (modsButtonIndex != -1) {
				if (style == ModMenuConfig.ModsButtonStyle.CLASSIC) {
					addButton(modsButtonIndex, new ModMenuButtonWidget(screen.width / 2 - 102, buttonsY + spacing * 3 - (spacing / 2), 204, 20, ModMenuApi.createModsButtonText(), screen), buttons, drawables);
				} else if (style == ModMenuConfig.ModsButtonStyle.ICON) {
					addButton(modsButtonIndex, new ModMenuTexturedButtonWidget(screen.width / 2 + 4 + 100 + 2, screen.height / 4 + 72 + -16, 20, 20, 0, 0, FABRIC_ICON_BUTTON_LOCATION, 32, 64, button -> MinecraftClient.getInstance().openScreen(new ModsScreen(screen)), ModMenuApi.createModsButtonText()), buttons, drawables);
				}
			}
		}
	}

	private static boolean buttonHasText(ClickableWidget button, String translationKey) {
		Text text = button.getMessage();
		return text instanceof TranslatableText && ((TranslatableText) text).getKey().equals(translationKey);
	}

	private static void addButton(int index, ClickableWidget button,  List<ClickableWidget> buttons, List<Drawable> drawables) {
		buttons.add(index, button);
		drawables.add(button);
	}
	private static void setButton(int index, ClickableWidget button,  List<ClickableWidget> buttons, List<Drawable> drawables) {
		buttons.set(index, button);
		drawables.set(index, button);
	}

	private static void shiftButtons(ClickableWidget button, boolean shiftUp, int spacing) {
		if (shiftUp) {
			button.y -= spacing / 2;
		} else {
			button.y += spacing - (spacing / 2);
		}
	}
}
