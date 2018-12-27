package com.codetaylor.mc.pyrotech.modules.pyrotech.item;

import net.minecraft.item.ItemTool;

import java.util.Collections;

public class ItemCrudeHammer
    extends ItemTool {

  public static final String NAME = "crude_hammer";

  public ItemCrudeHammer() {

    super(ToolMaterial.STONE, Collections.emptySet());
    this.setMaxDamage(ToolMaterial.STONE.getMaxUses() / 4);
  }
}
