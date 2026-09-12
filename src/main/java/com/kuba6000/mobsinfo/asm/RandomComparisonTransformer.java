package com.kuba6000.mobsinfo.asm;

import net.minecraft.launchwrapper.IClassTransformer;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.objectweb.asm.ClassReader;
import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.tree.AbstractInsnNode;
import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.InsnList;
import org.objectweb.asm.tree.IntInsnNode;
import org.objectweb.asm.tree.JumpInsnNode;
import org.objectweb.asm.tree.LdcInsnNode;
import org.objectweb.asm.tree.LineNumberNode;
import org.objectweb.asm.tree.MethodInsnNode;
import org.objectweb.asm.tree.MethodNode;

import com.kuba6000.mobsinfo.api.RandomSequencer;

/**
 * Adds comparison metadata without interpreting mod code or resolving its class hierarchy.
 * Only a directly consumed Random result is eligible; labels are barriers because another
 * predecessor could supply a different value to the comparison.
 */
public final class RandomComparisonTransformer implements IClassTransformer {

    private static final Logger LOG = LogManager.getLogger("MobsInfo[Random comparisons]");
    private static final String BRIDGE = "com/kuba6000/mobsinfo/api/RandomComparison";
    private static final char[] RANDOM_OWNER = "java/util/Random".toCharArray();
    private final boolean disabled = Boolean.getBoolean("mobsinfo.disableRandomComparisonTransformer");

    @Override
    public byte[] transform(String name, String transformedName, byte[] basicClass) {
        if (disabled || basicClass == null || excluded(transformedName == null ? name : transformedName))
            return basicClass;
        if (!referencesRandom(basicClass)) return basicClass;
        try {
            ClassReader reader = new ClassReader(basicClass);
            ClassNode node = new ClassNode();
            reader.accept(node, 0);
            int changed = 0;
            for (MethodNode method : node.methods) {
                for (AbstractInsnNode instruction : method.instructions.toArray()) {
                    if (!(instruction instanceof MethodInsnNode)) continue;
                    MethodInsnNode call = (MethodInsnNode) instruction;
                    if (call.getOpcode() != Opcodes.INVOKEVIRTUAL || !call.owner.equals("java/util/Random")) continue;
                    if (call.name.equals("nextInt") && call.desc.equals("(I)I")) {
                        if (transformInt(method, call)) changed++;
                    } else if (call.name.equals("nextFloat") && call.desc.equals("()F")) {
                        if (transformFloat(method, call)) changed++;
                    }
                }
            }
            if (changed == 0) return basicClass;
            // Stack types at existing frame boundaries are unchanged. COMPUTE_FRAMES would load
            // mod classes recursively through LaunchWrapper while they are still transforming.
            ClassWriter writer = new ClassWriter(reader, ClassWriter.COMPUTE_MAXS);
            node.accept(writer);
            LOG.debug("Optimized {} random comparisons in {}", changed, transformedName);
            return writer.toByteArray();
        } catch (RuntimeException exception) {
            LOG.warn("Could not optimize random comparisons in {}; keeping original class", transformedName, exception);
            return basicClass;
        }
    }

    private static boolean transformInt(MethodNode method, MethodInsnNode call) {
        AbstractInsnNode next = nextInstruction(call);
        Integer threshold = integerConstant(next);
        AbstractInsnNode branch = threshold == null ? next : nextInstruction(next);
        if (!(branch instanceof JumpInsnNode)) return false;
        int opcode = branch.getOpcode();
        int comparison = comparison(opcode, threshold == null ? Opcodes.IFEQ : Opcodes.IF_ICMPEQ);
        if (comparison == -1) return false;
        InsnList arguments = new InsnList();
        arguments.add(new LdcInsnNode(threshold == null ? 0 : threshold));
        arguments.add(new LdcInsnNode(comparison));
        method.instructions.insertBefore(call, arguments);
        method.instructions
            .set(call, new MethodInsnNode(Opcodes.INVOKESTATIC, BRIDGE, "nextInt", "(Ljava/util/Random;III)I", false));
        return true;
    }

    private static boolean transformFloat(MethodNode method, MethodInsnNode call) {
        AbstractInsnNode constant = nextInstruction(call);
        Float threshold = floatConstant(constant);
        if (threshold == null) return false;
        AbstractInsnNode compare = nextInstruction(constant);
        if (compare == null || (compare.getOpcode() != Opcodes.FCMPL && compare.getOpcode() != Opcodes.FCMPG))
            return false;
        AbstractInsnNode branch = nextInstruction(compare);
        if (!(branch instanceof JumpInsnNode)) return false;
        int comparison = comparison(branch.getOpcode(), Opcodes.IFEQ);
        if (comparison == -1) return false;
        InsnList arguments = new InsnList();
        arguments.add(new LdcInsnNode(threshold));
        arguments.add(new LdcInsnNode(comparison));
        method.instructions.insertBefore(call, arguments);
        method.instructions
            .set(call, new MethodInsnNode(Opcodes.INVOKESTATIC, BRIDGE, "nextFloat", "(Ljava/util/Random;FI)F", false));
        return true;
    }

    private static int comparison(int opcode, int base) {
        switch (opcode - base) {
            case 0:
            case 1:
                return RandomSequencer.EQUALITY;
            case 2:
            case 3:
                return RandomSequencer.LESS_THAN;
            case 4:
            case 5:
                return RandomSequencer.LESS_OR_EQUAL;
            default:
                return -1;
        }
    }

    private static AbstractInsnNode nextInstruction(AbstractInsnNode instruction) {
        AbstractInsnNode next = instruction.getNext();
        while (next instanceof LineNumberNode) next = next.getNext();
        return next;
    }

    private static Integer integerConstant(AbstractInsnNode instruction) {
        if (instruction == null) return null;
        int opcode = instruction.getOpcode();
        if (opcode >= Opcodes.ICONST_M1 && opcode <= Opcodes.ICONST_5) return opcode - Opcodes.ICONST_0;
        if (opcode == Opcodes.BIPUSH || opcode == Opcodes.SIPUSH) return ((IntInsnNode) instruction).operand;
        if (instruction instanceof LdcInsnNode && ((LdcInsnNode) instruction).cst instanceof Integer)
            return (Integer) ((LdcInsnNode) instruction).cst;
        return null;
    }

    private static Float floatConstant(AbstractInsnNode instruction) {
        if (instruction == null) return null;
        int opcode = instruction.getOpcode();
        if (opcode >= Opcodes.FCONST_0 && opcode <= Opcodes.FCONST_2) return (float) (opcode - Opcodes.FCONST_0);
        if (instruction instanceof LdcInsnNode && ((LdcInsnNode) instruction).cst instanceof Float)
            return (Float) ((LdcInsnNode) instruction).cst;
        return null;
    }

    private static boolean excluded(String name) {
        if (name == null) return true;
        return name.startsWith("com.kuba6000.mobsinfo.") || name.startsWith("java.")
            || name.startsWith("javax.")
            || name.startsWith("sun.")
            || name.startsWith("jdk.")
            || name.startsWith("org.objectweb.asm.")
            || name.startsWith("org.spongepowered.")
            || name.startsWith("org.apache.logging.")
            || name.startsWith("net.minecraft.launchwrapper.")
            || name.startsWith("cpw.mods.fml.");
    }

    // Most loaded classes cannot contain a matching call. Avoid allocating an ASM tree for them.
    private static boolean referencesRandom(byte[] bytes) {
        for (int start = 0; start <= bytes.length - RANDOM_OWNER.length; start++) {
            int offset = 0;
            while (offset < RANDOM_OWNER.length && bytes[start + offset] == RANDOM_OWNER[offset]) offset++;
            if (offset == RANDOM_OWNER.length) return true;
        }
        return false;
    }
}
