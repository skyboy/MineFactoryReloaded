package powercrystals.minefactoryreloaded.asm;

import com.google.common.base.Throwables;
import it.unimi.dsi.fastutil.objects.Object2IntOpenHashMap;
import net.minecraft.launchwrapper.IClassTransformer;
import net.minecraft.launchwrapper.Launch;
import net.minecraft.launchwrapper.LogWrapper;
import org.apache.logging.log4j.Level;
import org.objectweb.asm.*;
import org.objectweb.asm.tree.*;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;

import static org.objectweb.asm.Opcodes.*;

public class WorldTransformer implements IClassTransformer {

	static final String worldServerProxy = "powercrystals/minefactoryreloaded/asmhooks/WorldServerProxy";
	static final String worldServer = "net/minecraft/world/WorldServer";
	static final String world = "net/minecraft/world/World";
	static final String serverSig;
	static final String worldSig;

	static {
		final String sigBody = "Lnet/minecraft/world/storage/ISaveHandler;" + "Lnet/minecraft/world/storage/WorldInfo;" +
				"Lnet/minecraft/world/WorldProvider;" + "Lnet/minecraft/profiler/Profiler;" + "Z";
		serverSig = "(L" + worldServer + ";)V";
		worldSig = "(L" + world + ";)V";
	}

	private final static Object2IntOpenHashMap<String> transformerMap = new Object2IntOpenHashMap<>();
	static {
		transformerMap.put(worldServer.replace('/', '.'), 1);
		transformerMap.put(world.replace('/', '.'), 2);
		transformerMap.put(worldServerProxy.replace('/', '.'), 3);
		transformerMap.put("net.minecraft.profiler.Profiler", 22);
	}

	private static void saveTransformedClass(final byte[] data, final String transformedName) {

		File tempFolder = new File(Launch.minecraftHome, "CLASSLOADER_TEMP");
		if (tempFolder == null) {
			return;
		}

		final File outFile = new File(tempFolder, transformedName.replace('.', File.separatorChar) + ".class");
		final File outDir = outFile.getParentFile();

		if (!outDir.exists()) {
			outDir.mkdirs();
		}

		if (outFile.exists()) {
			outFile.delete();
		}

		try {
			LogWrapper.fine("Saving transformed class \"%s\" to \"%s\"", transformedName, outFile.getAbsolutePath().replace('\\', '/'));

			final OutputStream output = new FileOutputStream(outFile);
			output.write(data);
			output.close();
		} catch (IOException ex) {
			LogWrapper.log(Level.WARN, ex, "Could not save transformed class \"%s\"", transformedName);
		}
	}

	private static byte[] alterProfiler(String name, byte[] bytes, ClassReader cr) {

		String[] names;
		names = new String[] { "endSection", "startSection", "endStartSection", "clearProfiling" };

		name = name.replace('.', '/');
		ClassNode cn = new ClassNode(ASM5);
		cr.accept(cn, 0);

		cn.fields.add(new FieldNode(ACC_PRIVATE, "cofh_stack", "Ljava/util/Deque;", null, null));
		cn.fields.add(new FieldNode(ACC_PRIVATE, "cofh_endStart", "Z", null, Boolean.FALSE));
		for (MethodNode m : cn.methods) {
			if ("<init>".equals(m.name)) {
				LabelNode a = new LabelNode(new Label());
				AbstractInsnNode n;
				for (n = m.instructions.getFirst(); n != null; n = n.getNext()) {
					if (n.getOpcode() == INVOKESPECIAL) {
						break;
					}
				}
				m.instructions.insert(n, n = a);
				m.instructions.insert(n, n = new LineNumberNode(-15000, a));
				m.instructions.insert(n, n = new VarInsnNode(ALOAD, 0));
				m.instructions.insert(n, n = new TypeInsnNode(NEW, "java/util/LinkedList"));
				m.instructions.insert(n, n = new InsnNode(DUP));
				m.instructions.insert(n, n = new MethodInsnNode(INVOKESPECIAL, "java/util/LinkedList", "<init>", "()V", false));
				m.instructions.insert(n, n = new FieldInsnNode(PUTFIELD, name, "cofh_stack", "Ljava/util/Deque;"));
			} else if (names[0].equals(m.name)) {
				int c = 0;
				for (AbstractInsnNode n = m.instructions.getFirst(); n != null; n = n.getNext()) {
					if (n.getOpcode() == ALOAD && ++c > 1) {
						LabelNode lCond = new LabelNode(new Label());
						LabelNode lGuard = new LabelNode(new Label());
						m.instructions.insertBefore(n, n = new VarInsnNode(ALOAD, 0));
						m.instructions.insert(n, n = new FieldInsnNode(GETFIELD, name, "cofh_endStart", "Z"));
						m.instructions.insert(n, n = new JumpInsnNode(IFNE, lGuard));
						m.instructions.insert(n, n = new VarInsnNode(ALOAD, 0));
						m.instructions.insert(n, n = new FieldInsnNode(GETFIELD, name, "cofh_stack", "Ljava/util/Deque;"));
						m.instructions.insert(n, n = new MethodInsnNode(INVOKEINTERFACE, "java/util/Deque", "pop", "()Ljava/lang/Object;", true));
						m.instructions.insert(n, n = new TypeInsnNode(CHECKCAST, "java/lang/Throwable"));
						m.instructions.insert(n, n = new InsnNode(DUP));
						m.instructions.insert(n, n = new MethodInsnNode(INVOKEVIRTUAL, "java/lang/Throwable", "getStackTrace", "()[Ljava/lang/StackTraceElement;", false));
						m.instructions.insert(n, n = new InsnNode(ARRAYLENGTH));
						m.instructions.insert(n, n = new TypeInsnNode(NEW, "java/lang/Throwable"));
						m.instructions.insert(n, n = new InsnNode(DUP));
						m.instructions.insert(n, n = new MethodInsnNode(INVOKESPECIAL, "java/lang/Throwable", "<init>", "()V", false));
						m.instructions.insert(n, n = new MethodInsnNode(INVOKEVIRTUAL, "java/lang/Throwable", "getStackTrace", "()[Ljava/lang/StackTraceElement;", false));
						m.instructions.insert(n, n = new InsnNode(ARRAYLENGTH));
						m.instructions.insert(n, n = new JumpInsnNode(IF_ICMPLE, lCond));
						m.instructions.insert(n, n = new TypeInsnNode(NEW, "java/lang/Error"));
						m.instructions.insert(n, n = new InsnNode(DUP));
						m.instructions.insert(n, n = new LdcInsnNode("Detected bad stack depth call to endSection"));
						m.instructions.insert(n, n = new MethodInsnNode(INVOKESPECIAL, "java/lang/Error", "<init>", "(Ljava/lang/String;)V", false));
						m.instructions.insert(n, n = new InsnNode(SWAP));
						m.instructions.insert(n, n = new MethodInsnNode(INVOKEVIRTUAL, "java/lang/Throwable", "initCause", "(Ljava/lang/Throwable;)Ljava/lang/Throwable;", false));
						m.instructions.insert(n, n = new InsnNode(ATHROW));
						m.instructions.insert(n, n = new FrameNode(F_SAME1, 0, null, 0, new Object[] { "java/lang/Throwable" }));
						m.instructions.insert(n, n = lCond);
						m.instructions.insert(n, n = new InsnNode(POP));
						m.instructions.insert(n, n = new FrameNode(F_SAME, 0, null, 0, null));
						m.instructions.insert(n, n = lGuard);
						break;
					}
				}
			} else if (names[1].equals(m.name) || "func_194340_a".equals(m.name)) {
				int c = 0;
				for (AbstractInsnNode n = m.instructions.getFirst(); n != null; n = n.getNext()) {
					if (n.getOpcode() == ALOAD && ++c > 1) {
						LabelNode lGuard = new LabelNode(new Label());
						m.instructions.insertBefore(n, n = new VarInsnNode(ALOAD, 0));
						m.instructions.insert(n, n = new FieldInsnNode(GETFIELD, name, "cofh_endStart", "Z"));
						m.instructions.insert(n, n = new JumpInsnNode(IFNE, lGuard));
						m.instructions.insert(n, n = new VarInsnNode(ALOAD, 0));
						m.instructions.insert(n, n = new FieldInsnNode(GETFIELD, name, "cofh_stack", "Ljava/util/Deque;"));
						m.instructions.insert(n, n = new TypeInsnNode(NEW, "java/lang/Error"));
						m.instructions.insert(n, n = new InsnNode(DUP));
						m.instructions.insert(n, n = new LdcInsnNode("Failed to call endSection after calling startSection"));
						m.instructions.insert(n, n = new MethodInsnNode(INVOKESPECIAL, "java/lang/Error", "<init>", "(Ljava/lang/String;)V", false));
						m.instructions.insert(n, n = new MethodInsnNode(INVOKEVIRTUAL, "java/lang/Throwable", "fillInStackTrace", "()Ljava/lang/Throwable;", false));
						m.instructions.insert(n, n = new MethodInsnNode(INVOKEINTERFACE, "java/util/Deque", "push", "(Ljava/lang/Object;)V", true));
						m.instructions.insert(n, n = new FrameNode(F_SAME, 0, null, 0, null));
						m.instructions.insert(n, n = lGuard);
						break;
					}
				}
			} else if (names[2].equals(m.name) || "func_194339_b".equals(m.name)) {
				AbstractInsnNode n;
				for (n = m.instructions.getLast(); n != null; n = n.getPrevious()) {
					if (n.getOpcode() == RETURN) {
						m.instructions.insertBefore(n, n = new VarInsnNode(ALOAD, 0));
						m.instructions.insert(n, n = new InsnNode(ICONST_0));
						m.instructions.insert(n, n = new FieldInsnNode(PUTFIELD, name, "cofh_endStart", "Z"));
						break;
					}
				}

				m.instructions.insertBefore(m.instructions.getFirst(), n = new VarInsnNode(ALOAD, 0));
				m.instructions.insert(n, n = new InsnNode(ICONST_1));
				m.instructions.insert(n, n = new FieldInsnNode(PUTFIELD, name, "cofh_endStart", "Z"));
			} else if (names[3].equals(m.name)) {
				AbstractInsnNode n;
				m.instructions.insertBefore(m.instructions.getFirst(), n = new VarInsnNode(ALOAD, 0));
				m.instructions.insert(n, n = new FieldInsnNode(GETFIELD, name, "cofh_stack", "Ljava/util/Deque;"));
				m.instructions.insert(n, n = new MethodInsnNode(INVOKEINTERFACE, "java/util/Deque", "clear", "()V", true));
			}
		}

		ClassWriter cw = new ClassWriter(ClassWriter.COMPUTE_FRAMES);
		cn.accept(cw);
		bytes = cw.toByteArray();

		saveTransformedClass(bytes, name);

		return bytes;
	}

	@Override
	public byte[] transform(String name, String transformedName, byte[] basicClass) {

		switch (transformerMap.getInt(transformedName)) {
			case 1:
				return modifyWorldServer(basicClass);
			case 2:
				return modifyWorld(basicClass);
			case 3:
				return modifyWorldServerProxy(basicClass);
			case 22:
				//return alterProfiler(transformedName, basicClass, new ClassReader(basicClass));
			case 0:
			default:
				break;
		}
		return basicClass;
	}

	private static void makeAllPublic(ClassNode cn) {

		for (MethodNode a : cn.methods) {
			a.access = (a.access & ~(ACC_PRIVATE | ACC_PROTECTED | ACC_FINAL)) | ACC_PUBLIC;
			// tweak access, but do not tweak calls: internal private calls remain calls to the method on the given class, and not a subclass
			// this catches reflection calls out, but leaves code calls unmolested
		}
	}

	private static byte[] modifyWorld(byte[] bytes) {

		ClassReader cr = new ClassReader(bytes);
		ClassNode cn = new ClassNode();
		cr.accept(cn, ClassReader.EXPAND_FRAMES);

		makeAllPublic(cn);

		addConstructor:
		{
			for (MethodNode method : cn.methods) {
				if ("<init>".equals(method.name) && worldSig.equals(method.desc))
					break addConstructor; // someone has created it for us
			}

			MethodVisitor mv = cn.visitMethod(ACC_PUBLIC | ACC_SYNTHETIC, "<init>", worldSig, null, null);
			mv.visitCode();
			mv.visitVarInsn(ALOAD, 0);
			// [Object] super();
			mv.visitMethodInsn(INVOKESPECIAL, cn.superName, "<init>", "()V", false);
			for (FieldNode field : cn.fields) {
				if (isFinal(field.access)) {
					mv.visitVarInsn(ALOAD, 0);
					mv.visitVarInsn(ALOAD, 1);
					mv.visitFieldInsn(GETFIELD, world, field.name, field.desc);
					mv.visitFieldInsn(PUTFIELD, world, field.name, field.desc);
				}
			}
			mv.visitInsn(RETURN);
			mv.visitMaxs(4, 4);
			mv.visitEnd();
		}
		addUpdate:
		{
			for (MethodNode method : cn.methods) {
				if ("cofh_updatePropsInternal".equals(method.name) && worldSig.equals(method.desc))
					break addUpdate; // someone has created it for us
			}

			MethodVisitor mv = cn.visitMethod(ACC_PUBLIC | ACC_SYNTHETIC, "cofh_updatePropsInternal", worldSig, null, null);
			mv.visitCode();
			for (FieldNode field : cn.fields) {
				if (isInstance(field.access)) {
					mv.visitVarInsn(ALOAD, 0);
					mv.visitVarInsn(ALOAD, 1);
					mv.visitFieldInsn(GETFIELD, world, field.name, field.desc);
					mv.visitFieldInsn(PUTFIELD, world, field.name, field.desc);
				}
			}
			mv.visitInsn(RETURN);
			mv.visitMaxs(4, 4);
			mv.visitEnd();
		}

		ClassWriter cw = new ClassWriter(ClassWriter.COMPUTE_FRAMES);
		cn.accept(cw);
		bytes = cw.toByteArray();
		return bytes;
	}

	private static byte[] modifyWorldServer(byte[] bytes) {

		ClassReader cr = new ClassReader(bytes);
		ClassNode cn = new ClassNode();
		cr.accept(cn, ClassReader.EXPAND_FRAMES);

		makeAllPublic(cn);

		addConstructor:
		{
			for (MethodNode method : cn.methods) {
				if ("<init>".equals(method.name) && serverSig.equals(method.desc))
					break addConstructor; // someone has created it for us
			}

			MethodVisitor mv = cn.visitMethod(ACC_PUBLIC | ACC_SYNTHETIC, "<init>", serverSig, null, null);
			mv.visitCode();
			mv.visitVarInsn(ALOAD, 0);
			mv.visitVarInsn(ALOAD, 1);
			// [World] super(world);
			mv.visitMethodInsn(INVOKESPECIAL, world, "<init>", worldSig, false);
			for (FieldNode field : cn.fields) {
				if (isFinal(field.access)) {
					mv.visitVarInsn(ALOAD, 0);
					mv.visitVarInsn(ALOAD, 1);
					mv.visitFieldInsn(GETFIELD, worldServer, field.name, field.desc);
					mv.visitFieldInsn(PUTFIELD, worldServer, field.name, field.desc);
				}
			}
			mv.visitInsn(RETURN);
			mv.visitMaxs(4, 4);
			mv.visitEnd();
		}
		addUpdate:
		{
			for (MethodNode method : cn.methods) {
				if ("cofh_updatePropsInternal".equals(method.name) && serverSig.equals(method.desc))
					break addUpdate; // someone has created it for us
			}

			MethodVisitor mv = cn.visitMethod(ACC_PUBLIC | ACC_SYNTHETIC, "cofh_updatePropsInternal", serverSig, null, null);
			mv.visitCode();
			mv.visitVarInsn(ALOAD, 0);
			mv.visitVarInsn(ALOAD, 1);
			mv.visitMethodInsn(INVOKESPECIAL, world, "cofh_updatePropsInternal", worldSig, false); // not virtual
			for (FieldNode field : cn.fields) {
				if (isInstance(field.access)) {
					mv.visitVarInsn(ALOAD, 0);
					mv.visitVarInsn(ALOAD, 1);
					mv.visitFieldInsn(GETFIELD, worldServer, field.name, field.desc);
					mv.visitFieldInsn(PUTFIELD, worldServer, field.name, field.desc);
				}
			}
			mv.visitInsn(RETURN);
			mv.visitMaxs(4, 4);
			mv.visitEnd();
		}

		ClassWriter cw = new ClassWriter(ClassWriter.COMPUTE_FRAMES);
		cn.accept(cw);
		bytes = cw.toByteArray();
		return bytes;
	}

	private static byte[] modifyWorldServerProxy(byte[] bytes) {

		ClassReader cr = new ClassReader(bytes);
		ClassNode cn = new ClassNode();
		cr.accept(cn, ClassReader.EXPAND_FRAMES);

		String oldSuper = cn.superName;
		cn.superName = worldServer;
		for (MethodNode m : cn.methods) {
			InsnList l = m.instructions;
			for (int i = 0, e = l.size(); i < e; i++) {
				AbstractInsnNode n = l.get(i);
				if (n instanceof MethodInsnNode) {
					MethodInsnNode mn = (MethodInsnNode) n;
					if (mn.getOpcode() == INVOKESPECIAL && mn.owner.equals(oldSuper)) {
						mn.owner = cn.superName;
					}
				}
			}
		}

		Method[] worldServerMethods = null;
		try {
			worldServerMethods = net.minecraft.world.WorldServer.class.getDeclaredMethods();
		} catch (Throwable e) {
			Throwables.propagate(e);
		}
		Method[] worldMethods = null;
		try {
			worldMethods = net.minecraft.world.World.class.getDeclaredMethods();
		} catch (Throwable e) {
			Throwables.propagate(e);
		}

		for (Method m : worldServerMethods) {
			if (!Modifier.isStatic(m.getModifiers())) {
				String desc = Type.getMethodDescriptor(m);
				boolean skip = false;
				if (cn.methods.stream().anyMatch(m2 -> m2.name.equals(m.getName()) && m2.desc.equals(desc))) {
					continue;
				}
				MethodVisitor mv = cn.visitMethod(getAccess(m), m.getName(), desc, null, getExceptions(m));
				mv.visitCode();
				mv.visitVarInsn(ALOAD, 0);
				mv.visitFieldInsn(GETFIELD, cn.name, "proxiedWorld", "L" + worldServer + ";");
				Type[] types = Type.getArgumentTypes(m);
				for (int i = 0, w = 1, e = types.length; i < e; i++) {
					mv.visitVarInsn(types[i].getOpcode(ILOAD), w);
					w += types[i].getSize();
				}
				mv.visitMethodInsn(INVOKEVIRTUAL, worldServer, m.getName(), desc, false);
				mv.visitInsn(Type.getReturnType(m).getOpcode(IRETURN));
				mv.visitMaxs(types.length + 1, types.length + 1);
				mv.visitEnd();
			}
		}

		for (Method m : worldMethods) {
			if (!Modifier.isStatic(m.getModifiers())) {
				String desc = Type.getMethodDescriptor(m);
				if (cn.methods.stream().anyMatch(m2 -> m2.name.equals(m.getName()) && m2.desc.equals(desc))) {
					continue;
				}
				MethodVisitor mv = cn.visitMethod(getAccess(m), m.getName(), desc, null, getExceptions(m));
				mv.visitCode();
				mv.visitVarInsn(ALOAD, 0);
				mv.visitFieldInsn(GETFIELD, cn.name, "proxiedWorld", "L" + worldServer + ";");
				Type[] types = Type.getArgumentTypes(m);
				for (int i = 0, w = 1, e = types.length; i < e; i++) {
					mv.visitVarInsn(types[i].getOpcode(ILOAD), w);
					w += types[i].getSize();
				}
				mv.visitMethodInsn(INVOKEVIRTUAL, world, m.getName(), desc, false);
				mv.visitInsn(Type.getReturnType(m).getOpcode(IRETURN));
				mv.visitMaxs(types.length + 1, types.length + 1);
				mv.visitEnd();
			}
		}

		makeAllPublic(cn);

		ClassWriter cw = new ClassWriter(ClassWriter.COMPUTE_FRAMES);
		cn.accept(cw);
		bytes = cw.toByteArray();
		return bytes;
	}

	private static boolean isFinal(int access) {

		return 0 != (access & ACC_FINAL) && 0 == (access & ACC_STATIC);
	}

	private static boolean isInstance(int access) {

		return 0 == (access & ACC_FINAL) && 0 == (access & ACC_STATIC);
	}

	private static int getAccess(Method m) {

		int r = m.getModifiers();
		r &= ~(ACC_PUBLIC | ACC_PRIVATE | ACC_PROTECTED | ACC_FINAL | ACC_BRIDGE | ACC_ABSTRACT);
		r |= ACC_PUBLIC | ACC_SYNTHETIC;
		return r;
	}

	private static String[] getExceptions(Method m) {

		Class<?>[] d = m.getExceptionTypes();
		if (d == null) {
			return null;
		}
		String[] r = new String[d.length];
		for (int i = 0; i < d.length; ++i) {
			r[i] = Type.getInternalName(d[i]);
		}
		return r;
	}

}
