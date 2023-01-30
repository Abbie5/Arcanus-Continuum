package dev.cammiescorner.arcanuscontinuum.client.gui.widgets;

import com.mojang.blaze3d.systems.RenderSystem;
import dev.cammiescorner.arcanuscontinuum.api.spells.SpellComponent;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawableHelper;
import net.minecraft.client.gui.screen.narration.NarrationMessageBuilder;
import net.minecraft.client.gui.screen.narration.NarrationPart;
import net.minecraft.client.gui.widget.ClickableWidget;
import net.minecraft.client.render.GameRenderer;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.text.MutableText;
import net.minecraft.text.Text;

import java.util.function.Consumer;

public class SpellComponentWidget extends ClickableWidget {
	protected final TooltipSupplier tooltipSupplier;
	private final MinecraftClient client = MinecraftClient.getInstance();
	private final SpellComponent component;

	public SpellComponentWidget(int x, SpellComponent component) {
		super(x, 0, 24, 24, Text.empty());
		this.component = component;
		this.tooltipSupplier = new TooltipSupplier() {
			final MutableText text = Text.translatable(component.getTranslationKey(client.player));

			@Override
			public void onTooltip(SpellComponentWidget spellComponentWidget, MatrixStack matrices, int mouseX, int mouseY) {
				if(client.currentScreen != null)
					client.currentScreen.renderTooltip(matrices, text, mouseX, mouseY);
			}

			@Override
			public void supply(Consumer<Text> consumer) {
				consumer.accept(text);
			}
		};
	}

	@Override
	public void renderButton(MatrixStack matrices, int mouseX, int mouseY, float delta) {
		RenderSystem.setShader(GameRenderer::getPositionTexShader);
		RenderSystem.setShaderColor(0.25F, 0.25F, 0.3F, 1F);
		RenderSystem.setShaderTexture(0, component.getTexture());
		DrawableHelper.drawTexture(matrices, getX(), getY(), 0, 0, 24, 24, 24, 24);
	}

	public void renderTooltip(MatrixStack matrices, int mouseX, int mouseY) {
		tooltipSupplier.onTooltip(this, matrices, mouseX, mouseY);
	}

	@Override
	protected void updateNarration(NarrationMessageBuilder builder) {
		appendDefaultNarrations(builder);
		tooltipSupplier.supply(text -> builder.put(NarrationPart.HINT, text));
	}

	public SpellComponent getSpellComponent() {
		return component;
	}

	public interface TooltipSupplier {
		void onTooltip(SpellComponentWidget spellComponentWidget, MatrixStack matrixStack, int i, int j);

		default void supply(Consumer<Text> consumer) {
		}
	}
}