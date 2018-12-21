# Nand2Teris
-------------

本项目来自 [nand2teris.org](http://nand2teris.org)，包含一门课程[《构建现代计算机：从与非门到俄罗斯方块》](https://www.coursera.org/learn/build-a-computer)和一本书[《计算机系统要素：从零开始构建现代计算机》](https://book.douban.com/subject/1998341/)，通过12个章节和项目引领学生和自学者从头开始逐步地构建一个基本的硬件平台和现代软件阶层体系。在这个过程中，能够获得关于**硬件体系结构**、**操作系统**、**编程语言**、**编译器**、**数据结构**、**算法**以及**软件工程**的详实知识。全书基于“先抽象再实现”的阐述模式，每一章都介绍一个关键的硬件或软件抽象，一种实现方式以及一个实际的项目。完成这些项目所必要的计算机科学知识在本书中都有涵盖，只要求读者具备程序设计经验。


### 项目描述

- [x] **Project 1: Boolean Logic**

> **布尔逻辑**：使用原始的 Nand 门以及由此所构建的一些复合门实现以下逻辑门：Not、And、Or/Xor、Multiplexor/Demultiplexor、多位 Not/And/Or 门、多位 Multiplexor、多位门（Multi-Way Gates）。


- [x] **Project 2: Boolean Arithmetic**

> **布尔算术**：门逻辑设计可以用来表达数字概念并对数字进行算术操作，ALU 是核心的单元，执行所有的算术和逻辑操作。 使用上一个项目里构建的一组逻辑门电路构建具有完整功能的算术逻辑单元，包括：半加器、全加器、加法器、增量器、ALU。


- [x] **Project 3: Sequential Logic**

> **时序逻辑**：保存数据的**记忆单元**由**时序芯片**组成，涉及同步、时钟、反馈回路，其中大部分封装到称为**触发器**的底层**时序门**中。将这些触发器作为基本模块使用，构建出典型的现代计算机所采用的记忆设备，包括：1-位寄存器（Bit）、寄存器（Register）、8-位寄存器（RAM8）、n-寄存器、计数器。


- [x] **Project 4: Machine Language**

> **机器语言**：是硬件和软件的接口，以一种约定的形式，用来对底层程序进行编码，利用**处理器**和**寄存器**操控**内存**。程序员可以用一系列机器指令命令处理器执行算术和逻辑操作，在内存中进行存取操作，让数据在寄存器之间传递，验证布尔表达式的值，等等。使用 Hack 机器语言编写以下两个程序：乘法程序、I/O 处理程序。


- [x] **Project 5: Computer Architecture**

> **计算机体系结构**：将 Project 1 - Project3 构建的所有芯片整合起来，集成为一台通用计算机，使之能够运行 Hack 机器语言编写的程序。步骤如下：内存（RAM16K + Screen + Keyboard）、CPU（ALU + 寄存器）、指令内存（内置 ROM32K 芯片）、计算机。


- [x] **Project 6: Assembler**

> **汇编编译器**：编译器能够将汇编语言编写的程序翻译成二进制模式，使用 Java 语言（或其他编程语言）开发汇编编译器，将用 Hack 汇编语言编写的程序翻译成 Hack 硬件平台能够理解的二进制代码，分为 4 个模块：语法分析器（Parser，对输入文件进行语法分析）、编码模块（Code，提供所有汇编命令所对应的二进制代码）、符号表（Symbol Table，处理符号）、主程序（驱动整个编译过程）。


- [x] **Project 7: VM I: Stack Arithmetic**

- [x] **Project 8: VM II: Program Control**

> **虚拟机**：基于对象的高级语言被翻译成中间代码，中间代码被转化为机器语言，中间代码运行在**虚拟机**（Virtual Machine）上。虚拟机模型一般配有一种语言，可用于编写 **VM 程序**，包括 4 种类型的命令：**算术命令，内存访问命令，程序流程控制命令和函数调用命令**。VM 操作中的操作数和结果保存在堆栈中，并且：任何算术表达式和布尔表达式都能被系统化地转化为一系列在堆栈上的简单操作，并被系统地计算出来。VM 翻译器主要包括：Parser模块（读取 VM 命令并解析）、CodeWriter模块（将 VM 命令翻译成 Hack 汇编代码）和主程序。


- [x] **Project 9: High-Level Language**

> **高级语言**：Jack 语言是简单的基于对象的语言，具有现代语言（如 Java）的基本特性和风格，但语法相对简单，且不支持继承。编写了一个基于 Jack 语言的 [PigDiceGame](https://en.wikipedia.org/wiki/Pig_(dice_game))。


- [x] **Project 10: Compiler I: Syntax Analysis**

- [ ] **Project 11: Compiler II: Code Generation**

> **编译器**：典型的编译器由两个主要模块组成：语法分析模块和代码生成模块。前者可细分为：字元化（tokenizing）模块将输入的字符分组成语言原子元素，然后由语法分析（parsing）模块将所得到的语言原子元素集合同语法规则相匹配，输出 XML 文件。后者先构建符号表模块，再用 VM 代码输出取代之前的 XML 输出。


- [ ] **Project 12: Operating System**


### 使用方式

在官方网站[nand2teris.org](http://nand2teris.org)下载运行项目所需要的软件等并按说明运行。


## 关于作者

* **[WangYixu](wangyixu.github.io)**