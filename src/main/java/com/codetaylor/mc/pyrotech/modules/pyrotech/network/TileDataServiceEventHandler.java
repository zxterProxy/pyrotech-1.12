package com.codetaylor.mc.pyrotech.modules.pyrotech.network;

import com.codetaylor.mc.pyrotech.modules.pyrotech.ModulePyrotech;
import it.unimi.dsi.fastutil.ints.IntArrayList;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraftforge.client.event.RenderGameOverlayEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;
import net.minecraftforge.fml.relauncher.Side;
import org.lwjgl.opengl.GL11;

@Mod.EventBusSubscriber(value = Side.CLIENT, modid = ModulePyrotech.MOD_ID)
public class TileDataServiceEventHandler {

  private static final IntArrayList BYTES_RECEIVED_PER_SECOND = new IntArrayList(11);
  private static int BYTES_RECEIVED;
  private static float TICK_COUNTER;

  @SubscribeEvent
  public static void onEvent(TickEvent.ClientTickEvent event) {

    if (event.phase == TickEvent.Phase.END) {
      return;
    }

    TICK_COUNTER += 1;//Minecraft.getMinecraft().getRenderPartialTicks();

    if (TICK_COUNTER >= 20) {
      TICK_COUNTER = 0;
      BYTES_RECEIVED_PER_SECOND.add(0, BYTES_RECEIVED);
      BYTES_RECEIVED = 0;

      if (BYTES_RECEIVED_PER_SECOND.size() > 120) {
        BYTES_RECEIVED_PER_SECOND.removeInt(BYTES_RECEIVED_PER_SECOND.size() - 1);
      }
    }
  }

  @SubscribeEvent
  public static void onEvent(TileDataServicePacketReceivedEvent event) {

    BYTES_RECEIVED += event.getSize();
  }

  @SubscribeEvent
  public static void onRenderGameOverlayPostEvent(RenderGameOverlayEvent.Post event) {

    RenderGameOverlayEvent.ElementType type = event.getType();

    if (type == RenderGameOverlayEvent.ElementType.ALL) {
      // TODO
      Minecraft minecraft = Minecraft.getMinecraft();

      Tessellator tessellator = Tessellator.getInstance();
      BufferBuilder renderer = tessellator.getBuffer();

      GlStateManager.disableTexture2D();
      GlStateManager.enableAlpha();
      GlStateManager.enableBlend();
      renderer.begin(GL11.GL_QUADS, DefaultVertexFormats.POSITION_COLOR);

      int x = 1;
      int y = 10;
      int height = 1;

      float max = 0;
      float min = Integer.MAX_VALUE;
      int total = 0;
      int size = BYTES_RECEIVED_PER_SECOND.size();

      for (int i = 0; i < size; i++) {
        int count = BYTES_RECEIVED_PER_SECOND.getInt(i);
        total += count;

        if (count > max) {
          max = count;
        }

        if (count > 0 && count < min) {
          min = count;
        }
      }

      if (min > max) {
        min = 0;
      }

      int avg = (int) (total / (float) size);
      int totalWidth = 64;

      bufferColoredQuad(renderer, 1, 11, totalWidth, size, 0, 0, 0, 0.75f);

      for (int i = 0; i < size; i++) {

        float width = BYTES_RECEIVED_PER_SECOND.getInt(i) / max;
        width *= totalWidth;

        y += height;

        if (i == 10) {
          bufferColoredQuad(renderer, x, y, width, height, 0, 0, 1, 0.5f);

        } else {
          bufferColoredQuad(renderer, x, y, width, height, 1, 1, 1, 0.5f);
        }
      }

      bufferColoredQuad(renderer, (int) ((avg / max) * totalWidth), 11, 1, size, 1, 1, 0, 0.5f);
      bufferColoredQuad(renderer, (int) ((min / max) * totalWidth), 11, 1, size, 0, 1, 0, 0.5f);
      bufferColoredQuad(renderer, totalWidth, 11, 1, size, 1, 0, 0, 0.5f);

      tessellator.draw();
      GlStateManager.enableTexture2D();
    }
  }

  private static void bufferColoredQuad(BufferBuilder renderer, int x, int y, float width, int height, float red, float green, float blue, float alpha) {

    renderer.pos((double) (x + 0), (double) (y + 0), 1.0D).color(red, green, blue, alpha).endVertex();
    renderer.pos((double) (x + 0), (double) (y + height), 1.0D).color(red, green, blue, alpha).endVertex();
    renderer.pos((double) (x + width), (double) (y + height), 1.0D).color(red, green, blue, alpha).endVertex();
    renderer.pos((double) (x + width), (double) (y + 0), 1.0D).color(red, green, blue, alpha).endVertex();
  }

  @SubscribeEvent
  public static void onEvent(RenderGameOverlayEvent.Text event) {

    int max = 0;
    int min = Integer.MAX_VALUE;
    int total = 0;
    int size = BYTES_RECEIVED_PER_SECOND.size();
    int tracked = 0;

    for (int i = 0; i < size; i++) {
      int count = BYTES_RECEIVED_PER_SECOND.getInt(i);
      total += count;

      if (i == 10) {
        tracked = count;
      }

      if (count > max) {
        max = count;
      }

      if (count > 0 && count < min) {
        min = count;
      }
    }

    event.getLeft().add("§a" + min + " §e" + (int) (total / (float) size) + " §c" + max + " §9" + tracked);


    /*for (int i = 0; i < BYTES_RECEIVED_PER_SECOND.size(); i++) {
      event.getLeft().add(String.valueOf(BYTES_RECEIVED_PER_SECOND.getInt(i)));
    }*/
  }

}