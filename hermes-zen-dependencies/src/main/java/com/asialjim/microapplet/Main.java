/*
 *    Copyright 2014-2026 <a href="mailto:asialjim@qq.com">Asial Jim</a>
 *
 *    Licensed under the Apache License, Version 2.0 (the "License");
 *    you may not use this file except in compliance with the License.
 *    You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 *    Unless required by applicable law or agreed to in writing, software
 *    distributed under the License is distributed on an "AS IS" BASIS,
 *    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *    See the License for the specific language governing permissions and
 *    limitations under the License.
 */
package com.asialjim.microapplet;

/**
 * Main类示例
 * Main Class Example
 * <p>
 * 该类是一个简单的示例类，
 * 用于演示基本的Java代码结构和输出功能。
 * <p>
 * This class is a simple example class,
 * used to demonstrate basic Java code structure and output functionality.
 *
 * @author <a href="mailto:asialjim@qq.com">Asial Jim</a>
 * @version 1.0.0
 * @since 2026-01-08
 */
public class Main {
    /**
     * 主方法，程序的入口点
     * Main method, entry point of the program
     * <p>
     * 打印欢迎信息并输出1到5的数字
     * <p>
     * Print welcome message and output numbers from 1 to 5
     *
     * @param args 命令行参数
     *             Command line arguments
     * @since 2026-01-08
     */
    public static void main(String[] args) {
        System.out.printf("Hello and welcome!");

        for (int i = 1; i <= 5; i++) {
            System.out.println("i = " + i);
        }
    }
}