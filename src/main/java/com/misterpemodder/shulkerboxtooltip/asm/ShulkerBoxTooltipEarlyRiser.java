package com.misterpemodder.shulkerboxtooltip.asm;

import com.chocohead.mm.api.ClassTinkerers;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.tree.AbstractInsnNode;
import org.objectweb.asm.tree.InsnList;
import org.objectweb.asm.tree.InsnNode;
import org.objectweb.asm.tree.JumpInsnNode;
import org.objectweb.asm.tree.LabelNode;
import org.objectweb.asm.tree.MethodInsnNode;
import org.objectweb.asm.tree.MethodNode;
import org.objectweb.asm.tree.TypeInsnNode;
import org.objectweb.asm.tree.VarInsnNode;

import net.fabricmc.loader.api.FabricLoader;
import net.fabricmc.loader.api.MappingResolver;

public class ShulkerBoxTooltipEarlyRiser implements Runnable {
    private static Logger LOGGER = LogManager.getFormatterLogger("ShulkerBoxTooltip Early Riser");

    @Override
    public void run() {
        MappingResolver remapper = FabricLoader.getInstance().getMappingResolver();
        String tooltipComponent = remapper.mapClassName("intermediary", "net.minecraft.class_5684");

        ClassTinkerers.addTransformation(tooltipComponent, classNode -> {
            String tooltipData = remapper.mapClassName("intermediary", "net.minecraft.class_5632");
            String ofMethodName = remapper.getCurrentRuntimeNamespace().equals("intermediary") ? "method_32663" : "of";
            String ofMethodDesc = "(L" + tooltipData.replace('.', '/') + ";)L" + tooltipComponent.replace('.', '/')
                    + ";";

            for (MethodNode methodNode : classNode.methods) {
                if (methodNode.name.equals(ofMethodName) && methodNode.desc.equals(ofMethodDesc)) {
                    injectIntoOfMethod(methodNode);
                    return;
                }
            }
            throw new InjectionException(
                    "cound not find method " + ofMethodName + ofMethodDesc + " in " + tooltipComponent);
        });
    }

    /**
     * Injects code at the head of {@link net.minecraft.client.gui.tooltip.TooltipComponent#of(net.minecraft.client.item.TooltipData)}
     * to obtain to following result:
     * <blockquote><pre>
     * static TooltipComponent of(TooltipData data) {
     *     if (data instanceof PreviewTooltipData) {
     *         return new PreviewTooltipComponent((PreviewTooltipData)data);
     *     }
     *     // original method body
     * }
     * </pre></blockquote>
     * @param methodNode
     */
    private static void injectIntoOfMethod(MethodNode methodNode) {
        LabelNode label = null;

        for (AbstractInsnNode insn : methodNode.instructions) {
            if (insn instanceof LabelNode) {
                label = (LabelNode) insn;
                break;
            }
        }
        if (label == null)
            throw new InjectionException("Could not find a label node target");

        InsnList toAdd = new InsnList();

        toAdd.add(new VarInsnNode(Opcodes.ALOAD, 0));
        toAdd.add(new TypeInsnNode(Opcodes.INSTANCEOF, "com/misterpemodder/shulkerboxtooltip/impl/PreviewTooltipData"));
        toAdd.add(new JumpInsnNode(Opcodes.IFEQ, label));
        toAdd.add(new TypeInsnNode(Opcodes.NEW, "com/misterpemodder/shulkerboxtooltip/impl/PreviewTooltipComponent"));
        toAdd.add(new InsnNode(Opcodes.DUP));
        toAdd.add(new VarInsnNode(Opcodes.ALOAD, 0));
        toAdd.add(new TypeInsnNode(Opcodes.CHECKCAST, "com/misterpemodder/shulkerboxtooltip/impl/PreviewTooltipData"));
        toAdd.add(new MethodInsnNode(Opcodes.INVOKESPECIAL,
                "com/misterpemodder/shulkerboxtooltip/impl/PreviewTooltipComponent", "<init>",
                "(Lcom/misterpemodder/shulkerboxtooltip/impl/PreviewTooltipData;)V"));
        toAdd.add(new InsnNode(Opcodes.ARETURN));
        methodNode.instructions.insertBefore(label, toAdd);
    }

    private static class InjectionException extends RuntimeException {
        private static final long serialVersionUID = 5697454468791313004L;
        private String message;

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
