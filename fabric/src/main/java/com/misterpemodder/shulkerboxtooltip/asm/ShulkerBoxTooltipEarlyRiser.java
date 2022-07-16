package com.misterpemodder.shulkerboxtooltip.asm;

import com.chocohead.mm.api.ClassTinkerers;
import net.fabricmc.api.EnvType;
import net.fabricmc.loader.api.FabricLoader;
import net.fabricmc.loader.api.MappingResolver;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.tree.*;

import java.io.Serial;

public class ShulkerBoxTooltipEarlyRiser implements Runnable {
  private static final Logger LOGGER = LogManager.getFormatterLogger("ShulkerBoxTooltip Early Riser");

  @Override
  public void run() {
    if (FabricLoader.getInstance().getEnvironmentType() != EnvType.CLIENT)
      return;

    MappingResolver remapper = FabricLoader.getInstance().getMappingResolver();
    String tooltipComponent = remapper.mapClassName("intermediary", "net.minecraft.class_5684");

    ClassTinkerers.addTransformation(tooltipComponent, classNode -> {
      String tooltipData = remapper.mapClassName("intermediary", "net.minecraft.class_5632");
      String ofMethodName =
          remapper.getCurrentRuntimeNamespace().equals("intermediary") ? "method_32663" : "of";
      String ofMethodDesc =
          "(L" + tooltipData.replace('.', '/') + ";)L" + tooltipComponent.replace('.', '/') + ";";

      for (MethodNode methodNode : classNode.methods) {
        if (methodNode.name.equals(ofMethodName) && methodNode.desc.equals(ofMethodDesc)) {
          injectIntoOfMethod(methodNode);
          return;
        }
      }
      throw new InjectionException(
          "could not find method " + ofMethodName + ofMethodDesc + " in " + tooltipComponent);
    });
  }

  /**
   * Injects code at the head of {@link net.minecraft.client.gui.tooltip.TooltipComponent#of(net.minecraft.client.item.TooltipData)}
   * to obtain the following result:
   * <blockquote><pre>
   * static TooltipComponent of(TooltipData data) {
   *     if (data instanceof PreviewTooltipData) {
   *         return new PreviewTooltipComponent((PreviewTooltipData)data);
   *     }
   *     // original method body
   * }
   * </pre></blockquote>
   */
  private static void injectIntoOfMethod(MethodNode methodNode) {
    LabelNode label = null;

    for (AbstractInsnNode insn : methodNode.instructions) {
      if (insn instanceof LabelNode l) {
        label = l;
        break;
      }
    }
    if (label == null)
      throw new InjectionException("Could not find a label node target");

    InsnList toAdd = new InsnList();
    String prefix = "com/misterpemodder/shulkerboxtooltip/impl/tooltip/";
    String previewTooltipData = prefix + "PreviewTooltipData";
    String previewTooltipComponent = prefix + "PreviewTooltipComponent";

    toAdd.add(new VarInsnNode(Opcodes.ALOAD, 0));
    toAdd.add(new TypeInsnNode(Opcodes.INSTANCEOF, previewTooltipData));
    toAdd.add(new JumpInsnNode(Opcodes.IFEQ, label));
    toAdd.add(new TypeInsnNode(Opcodes.NEW, previewTooltipComponent));
    toAdd.add(new InsnNode(Opcodes.DUP));
    toAdd.add(new VarInsnNode(Opcodes.ALOAD, 0));
    toAdd.add(new TypeInsnNode(Opcodes.CHECKCAST, previewTooltipData));
    toAdd.add(new MethodInsnNode(Opcodes.INVOKESPECIAL, previewTooltipComponent, "<init>",
        "(L" + previewTooltipData + ";)V"));
    toAdd.add(new InsnNode(Opcodes.ARETURN));
    methodNode.instructions.insertBefore(label, toAdd);
  }

  private static class InjectionException extends RuntimeException {
    @Serial
    private static final long serialVersionUID = 5697454468791313004L;
    private final String message;

    public InjectionException(String message) {
      LOGGER.error(message);
      this.message = message;
    }

    @Override
    public String getMessage() {
      return message;
    }
  }
}
