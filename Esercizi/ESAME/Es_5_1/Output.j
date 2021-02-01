.class public Output 
.super java/lang/Object

.method public <init>()V
 aload_0
 invokenonvirtual java/lang/Object/<init>()V
 return
.end method

.method public static print(I)V
 .limit stack 2
 getstatic java/lang/System/out Ljava/io/PrintStream;
 iload_0 
 invokestatic java/lang/Integer/toString(I)Ljava/lang/String;
 invokevirtual java/io/PrintStream/println(Ljava/lang/String;)V
 return
.end method

.method public static read()I
 .limit stack 3
 new java/util/Scanner
 dup
 getstatic java/lang/System/in Ljava/io/InputStream;
 invokespecial java/util/Scanner/<init>(Ljava/io/InputStream;)V
 invokevirtual java/util/Scanner/next()Ljava/lang/String;
 invokestatic java/lang/Integer.parseInt(Ljava/lang/String;)I
 ireturn
.end method

.method public static run()V
 .limit stack 1024
 .limit locals 256
 invokestatic Output/read()I
 istore 0
 invokestatic Output/read()I
 istore 1
 invokestatic Output/read()I
 istore 2
 iload 0
 istore 3
 iload 0
 iload 1
 if_icmpgt L2
 goto L1
L2:
 iload 0
 istore 3
 iload 2
 iload 0
 if_icmpgt L3
 goto L1
L3:
 iload 2
 istore 3
 goto L4
L1:
 iload 1
 istore 3
L4:
 iload 1
 iload 2
 if_icmpgt L6
 goto L5
L6:
 iload 1
 istore 3
 goto L7
L5:
 iload 2
 istore 3
L7:
L8:
 iload 0
 iload 3
 if_icmplt L9
 goto L10
L9:
 iload 0
 ldc 1
 iadd 
 istore 0
 goto L8
L10:
 iload 0
 invokestatic Output/print(I)V
L11:
 iload 1
 iload 3
 if_icmplt L12
 goto L13
L12:
 iload 1
 ldc 1
 iadd 
 istore 1
 goto L11
L13:
 iload 1
 invokestatic Output/print(I)V
L14:
 iload 1
 iload 3
 if_icmplt L15
 goto L16
L15:
 iload 2
 ldc 1
 iadd 
 istore 2
 goto L14
L16:
 iload 2
 invokestatic Output/print(I)V
L0:
 return
.end method

.method public static main([Ljava/lang/String;)V
 invokestatic Output/run()V
 return
.end method

