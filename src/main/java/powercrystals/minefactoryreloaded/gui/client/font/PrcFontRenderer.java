package powercrystals.minefactoryreloaded.gui.client.font;

import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import java.io.IOException;
import java.io.InputStream;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.renderer.texture.TextureManager;
import net.minecraft.client.settings.GameSettings;
import net.minecraft.util.ResourceLocation;

import powercrystals.minefactoryreloaded.MFRProps;

@SideOnly(Side.CLIENT)
public class PrcFontRenderer extends FontRenderer {

	protected static final ResourceLocation[] unicodePageLocations = new ResourceLocation[256];

	private static final String fontTexture = MFRProps.TEXTURE_FOLDER + "font/";

	public PrcFontRenderer(GameSettings settings, TextureManager resourceManager, boolean unicode) {

		super(settings, new ResourceLocation(fontTexture + "ascii.png"), resourceManager, unicode);
	}

	@Override
	protected void readGlyphSizes() {

		try {
			@SuppressWarnings("resource")
			InputStream inputstream = Minecraft.getMinecraft().getResourceManager().
					getResource(new ResourceLocation(fontTexture + "glyph_sizes.bin")).getInputStream();
			inputstream.read(this.glyphWidth);
		} catch (IOException ioexception) {
			throw new RuntimeException(ioexception);
		}
	}

	@Override
	protected ResourceLocation getUnicodePageLocation(int i) {

		if (unicodePageLocations[i] == null)
			unicodePageLocations[i] = new ResourceLocation(String.format(fontTexture + "unicode_page_%02x.png", i));

		return unicodePageLocations[i];
	}

}
